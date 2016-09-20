# -*- coding: UTF-8 -*-

# Multiplatform Firelog client program for Windows and Linux.
# Developed for the PROTOS Project
# URL: http://protos.cti.gr
# e-mail: protosproject@gmail.com
# Developed using Python 2.7
# Last updated: 2016-01-09

import platform, os, sys, subprocess, shutil, pdb
import time
from datetime import datetime, timedelta
import math
import httplib, urllib, urllib2, urlparse, requests
import socket
import json, sqlite3
import uuid
import collections
import locale
import ConfigParser
import re

# Import OS-specific libraries
if sys.platform.startswith('win32'):
    import win32event, win32service, WindowsBalloonTip
elif sys.platform.startswith('linux'):
    import gi.repository.Notify


# Determine 64-bit architecture:
# platform.machine().endswith('64')

# Get windows system variables: os.environ['PROGRAMFILES']
# ProgramFiles = C:\Program Files
# ProgramFiles(x86) = C:\Program Files (x86)
# SystemRoot = C:\Windows

# Determine Linux distro:
# platform.linux_distribution()
# ('Ubuntu', '14.10', 'utopic')

class Firelog:

    # Some class variables and constants
    VERSION = '2.3'

    # Used for triggering a version update check
    THRESHOLD_PACKETS = 720
    THRESHOLD_ITERATIONS = 2500

    THRESHOLD_BLOCKED_IP_UPDATE = 120 # Min time to pass before updating the list of blocked IPs again (in seconds).

    HTTP = 'http://'
    HTTPS = 'https://'
    PROTOS_HOST = 'protos.cti.gr'
    PROTOS_ADDRATIO_PATH = '/promis/addratio.php'
    PROTOS_ADDRATIO_URL = urlparse.urljoin(HTTP + PROTOS_HOST, PROTOS_ADDRATIO_PATH)
    # Form the remaining URLs by using PROTOS_ADDRATIO_URL as base.
    PROTOS_ADDPACKET_URL = urlparse.urljoin(PROTOS_ADDRATIO_URL, 'addpacket.php')
    PROTOS_VERSION_URL = urlparse.urljoin(PROTOS_ADDRATIO_URL, 'version.html')
    PROTOS_ADDVERSION_URL = urlparse.urljoin(PROTOS_ADDRATIO_URL, 'addversion.php')
    PROTOS_TOPDATA_URL = PROTOS_ADDVERSION_URL = urlparse.urljoin(PROTOS_ADDRATIO_URL, 'returnTopData.php?granularity=Day')

    SLEEP_TIME = 30 # Client transmits every 30 seconds
    NULL = 'null' # Aux constant to be used with sqlite
    DB_FILE = 'firelog.db'
    CONFIG_FILE = 'firelog.conf'
    DB_FWL_TABLE = 'firewallLog'
    DB_ERR_TABLE = 'errors'
    LOG_CONTAINS_YEAR = True # Whether the firewall log contains the year or not.
    UUID_FILE = 'uuid.dat'
    PREV_FW_LOG_DATETIME = datetime.now() # Mark a "resume processing point" in the log file, to account for async logging and thus miss no entries.
    #VALID_PROTO_NAMES = ['TCP', 'UDP', 'ICMP'] # Not currently used
   
    # Some OS-dependent constants            
    if sys.platform.startswith('win32'):
        # For Python 2.7.9 (python.org) must run: pip install pypiwin32
        # Windows firewall log file example entry:
        # 2014-04-13 22:51:26 DROP UDP 10.1.2.3 224.0.0.252 51632 5355 54 - - - - - - - RECEIVE

        # Set correct path for Windows pfirewall.log
        win_release = platform.release()
        if (win_release == 'XP'):
            FW_LOG_PATH = os.environ['SystemRoot'] # Usually C:\Windows
        elif (win_release == 'Vista' or win_release == '7' or win_release == '8'):
            # Win Vista, 7, 8, 8.1
            # Note: Both Win 8 and 8.1 are reported as '8'
            FW_LOG_PATH = os.path.join(os.environ['SystemRoot'], 'System32/LogFiles/Firewall/')
        else:
            print 'Unsupported Windows platform'
        # Common settings for all Windows platforms
        FW_LOG_FILE = 'pfirewall.log'
        DATE_TIME_FORMAT_STR = '%Y-%m-%d %H:%M:%S'
        NUM_DATETIME_ITEMS = 2
        INSTALL_PATH = os.path.join(os.environ['PROGRAMFILES'], 'Firelog/')
        FW_BLOCK_KEYWORD = 'DROP'

    elif sys.platform.startswith('linux'):
        # Ubuntu Linux 12.04 UFW log file example entry:
        # Mar  8 08:06:54 ubuntu kernel: [5055367.033091] [UFW BLOCK] IN=eth0 OUT= MAC=01:00:5e:00:00:fb:00:21:b7:a2:be:ec:08:00 SRC=147.102.23.178 DST=224.0.0.251 LEN=32 TOS=0x00 PREC=0xC0 TTL=1 ID=0 DF PROTO=2
        # Mar  8 08:07:07 ubuntu kernel: [5055380.002176] [UFW BLOCK] IN=eth0 OUT= MAC=01:00:5e:00:00:01:00:1a:2a:7e:09:ac:08:00 SRC=192.168.2.1 DST=224.0.0.1 LEN=28 TOS=0x00 PREC=0x00 TTL=1 ID=6501 PROTO=2
        # WARNING: Several other variants exist! Perform string/regex search.

        FW_LOG_PATH = '/var/log/'
        FW_LOG_FILE = 'ufw.log'
        DATE_TIME_FORMAT_STR = '%b %d %H:%M:%S'
        NUM_DATETIME_ITEMS = 3
        LOG_CONTAINS_YEAR = False  # UFW log file omits year
        INSTALL_PATH = '/opt/firelog/'
        FW_BLOCK_KEYWORD = 'BLOCK'

    else:
        # AP: Need to make these steps robust (e.g. exception)
        print 'Unsupported OS'

    # Merge OS-dependent information, so as to form full paths
    FW_LOG_WITH_PATH = os.path.join(FW_LOG_PATH, FW_LOG_FILE)
    DB_WITH_PATH = os.path.join(INSTALL_PATH, DB_FILE)
    UUID_FILE_WITH_PATH = os.path.join(INSTALL_PATH, UUID_FILE)
    CONFIG_FILE_WITH_PATH = os.path.join(INSTALL_PATH, CONFIG_FILE)
    CLIENT_UUID = None  # Will be set at init stage...
    
    # Process preferences file and set constants accordingly...
    # Set default values for robustness
    config = ConfigParser.SafeConfigParser({
        'ManageFirewallRules': 'False', 
        'MonthsDataToKeepInDB': '3'
        })
    config.read(CONFIG_FILE_WITH_PATH)
    PREF_MANAGE_FW_RULES = config.getboolean('DEFAULT', 'ManageFirewallRules')
    PREF_MONTHS_DATA_TO_KEEP_IN_DB = config.get('DEFAULT', 'MonthsDataToKeepInDB') 
    
    prevNumIncidents = 0 # The previous number of incidents extracted from log file
    prevRate1 = 0 # The previous malicious activity (rate of change of incidents)
    iteration = 0
    packetsSent = 0    
    oldListOfBlockedIPs = [] # Keep track of blocked IPs
    lastUpdateOfBlockedIPs = datetime.now() # When the list of blocked IPs was last updated

#-------------------------------------------------------------------------------

    def checkDatabase(self, dbConn):
        """Checks if the DB file exists and has the required schema. If any of the two is missing, it is created.
        """
        c = dbConn.cursor()
        
        # Check if the required table exists
        c.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='" + Firelog.DB_FWL_TABLE + "'")
        # The above will return empty list or: [(u'firewallLog',)]
        r = str(c.fetchall()).split("'")
        
        if Firelog.DB_FWL_TABLE not in r :
            # Table does not exist, so create it. SQL commands:
            #CREATE TABLE firewallLog (date TEXT, time TEXT, action TEXT, protocol TEXT, srcip TEXT, dstip TEXT, srcport TEXT, dstport TEXT, size TEXT, tcpflags TEXT, tcpsyn TEXT, tcpack TEXT, tcpwin TEXT, icmptype TEXT, icmpcode TEXT, info TEXT, path TEXT, sent INTEGER DEFAULT 0);
            #CREATE INDEX datetime_index ON firewallLog(date, time);

            try:
                c.execute('CREATE TABLE ' + Firelog.DB_FWL_TABLE + ' (date TEXT, time TEXT, action TEXT, protocol TEXT, srcip TEXT, dstip TEXT, srcport TEXT, dstport TEXT, size TEXT, tcpflags TEXT, tcpsyn TEXT, tcpack TEXT, tcpwin TEXT, icmptype TEXT, icmpcode TEXT, info TEXT, path TEXT, sent INTEGER DEFAULT 0)')
                c.execute('CREATE INDEX datetime_index ON ' + Firelog.DB_FWL_TABLE + ' (date, time)')
                dbConn.commit()
            except:
                pass
            
#-------------------------------------------------------------------------------

    def str2date(self, logLineList):
        """Extract the date/time part from a log line (first n chars), process it and create a datetime object.
        """
        try:
            # Prepare the slice that contains the date/time and parse it
            dt = ' '.join(logLineList[:Firelog.NUM_DATETIME_ITEMS])
            dObj = datetime.strptime(dt, Firelog.DATE_TIME_FORMAT_STR)

            current_year = datetime.now().year
            # Replace default year (1900) with current, if it is not included in the log line
            if not Firelog.LOG_CONTAINS_YEAR:
                dObj = dObj.replace(year = current_year)
                # Ensure entries before and after a year change in year-less logs (e.g. Dec 31 ... Jan 1) are assigned the correct year number
                if dObj > datetime.now():
                    dObj = dObj.replace(year = current_year - 1)
        except ValueError:
            dObj = None
        return(dObj)

#-------------------------------------------------------------------------------

    def checkServiceStatus(self):
        if sys.platform.startswith('win32'):
            if getattr(sys, 'stopservice', True) == "true":
                sys.exit()
        else:
		pass #sys.exit()
            #print 'Unsupported platform' # ToDo: ...or add exception?

#-------------------------------------------------------------------------------

    def getValueAfter(self, substr, theList):
        """Used for extracting e.g. "TCP" if theList contains "PROTO=TCP". Note that it returns the first occurrence (it is not expected to have more occurrences anyway). If substr does not exist (there are such cases), it returns 'null', so as to be used directly as input to sqlite.
        """
        # The solution with the regex is ~3x slower.
        #r = [string for string in theList if re.match('^' + substr, string)]
        r = [elem for elem in theList if elem.startswith(substr)]
        if r:
            return r[0].split(substr)[1]
        else:
            # Returns 'null' in order to be used as input to sqlite.
            return Firelog.NULL

#-------------------------------------------------------------------------------
    
    def getIPAddress(self):
        """Alternative way for getting the IP address, as the command:
        socket.gethostbyname(socket.gethostname())
        does not work well in Linux. It returns e.g. 127.0.0.1, which is the entry of /etc/hosts
        """
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect(('google.com', 0))
            return s.getsockname()[0]
        except:
            pass
        
#-------------------------------------------------------------------------------

    def readOrGenerateUUID(self):
        """Reads the UUID from a specific file. If either the file does not exist or the retrieved string is not a valid UUID, a new UUID is generated and stored to the file. 
        """
        clientUUID = None
        isValidUUID = False
        
        if os.path.isfile(Firelog.UUID_FILE_WITH_PATH):
            # UUID file exists, so read the value from it
            with open(Firelog.UUID_FILE_WITH_PATH, 'r') as f :
                try:
                    clientUUID = f.readline().strip()
                    # Check UUID validity...
                    try:
                        val = uuid.UUID(clientUUID, version=4)
                        isValidUUID = val.hex == clientUUID.replace('-', '')
                    except ValueError:
                        print 'Invalid format for UUID v4'
                except:
                    pass

        # UUID either does not exist or is invalid, so generate a new one
        if not isValidUUID:
            # Generate a random UUID and store it in a file for future reference.
            clientUUID = uuid.uuid4()
            try:
                with open(Firelog.UUID_FILE_WITH_PATH, 'w') as f :
                    f.write(str(clientUUID) + '\n')
            except:
                pass
        
        return clientUUID        
        
#-------------------------------------------------------------------------------

    def computeRates(self, numIncidents):
        """Computes the following rates of change:
        rate1: Malicious activity (rate of change of numIncidents)
        rate2: Epidemic rate (rate of change of malicious activity)
        """
        
        if Firelog.prevNumIncidents == 0 :
            rate1 = Firelog.NULL
        else:
            rate1 = (float(numIncidents) - float(Firelog.prevNumIncidents)) / float(Firelog.prevNumIncidents)

        if Firelog.prevRate1 == 0 or Firelog.prevRate1 == Firelog.NULL or rate1 == Firelog.NULL:
            rate2 = Firelog.NULL
        else:
            rate2 = (float(rate1) - float(Firelog.prevRate1)) / float(Firelog.prevRate1)

        # Prepare information to be sent in JSON format
        data = '{"clientid": "' + str(Firelog.CLIENT_UUID) + '","rate1":' + str(rate1) + ',"rate2":' + str(rate2) + ',"tcount":' + str(numIncidents) + ',"localip":"' + str(self.getIPAddress()) + '"}'

        # Store values for next call
        Firelog.prevNumIncidents = numIncidents
        Firelog.prevRate1 = rate1

        return data
            
#-------------------------------------------------------------------------------

    def sendThem(self, data, theURL):
        try:
            params = urllib.urlencode({"data":data})
            headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
            conn = httplib.HTTPConnection(Firelog.PROTOS_HOST)
            conn.request("POST", theURL, params, headers)
            response = conn.getresponse()
            #print response.status, response.reason
            #fp.write("Resp status is %s and reason is %s\n" % (str(response.status),response.reason))
            data = response.read()
            #fp.write("The server replied %s \n" % data)
            #print data
            conn.close()
        except:
            pass

#-------------------------------------------------------------------------------
            
    def collectPackets(self):
        CHUNKS = 5000

        try:
            conn = sqlite3.connect(Firelog.DB_WITH_PATH)
            self.checkDatabase(conn)
            c = conn.cursor()
            c.execute('SELECT  date, time, action, protocol, srcip, dstip, srcport, dstport, size, tcpflags, tcpsyn, tcpack, tcpwin, icmptype, icmpcode, info, path FROM ' + Firelog.DB_FWL_TABLE + ' WHERE sent = 0;')
            queryResult = c.fetchall()

            objects_list = []
            pattern = "%Y-%m-%d %H:%M:%S"
            for ln in queryResult:
                d = collections.OrderedDict()
                tempDate = ln[0] + " " + ln[1]
                date = time.strptime(tempDate, pattern)
                localtime = time.mktime(date)
                utc = localtime + time.altzone
                utcDate = time.localtime(utc)
                d['datetime'] = time.strftime(pattern, utcDate)
                d['action'] = ln[2]
                d['srcip'] = ln[4]
                d['protocol'] = ln[3]
                d['dstip'] = ln[5]
                d['srcport'] = ln[6]
                d['dstport'] = ln[7]
                d['size'] = ln[8]
                d['tcpflags'] = ln[9]
                d['tcpsyn'] = ln[10]
                d['tcpack'] = ln[11]
                d['tcpwin'] = ln[12]
                d['icmptype'] = ln[13]
                d['icmpcode'] = ln[14]
                d['info'] = ln[15]
                d['path'] = ln[16]
                objects_list.append(d)
                
            params = {"uuid":str(Firelog.CLIENT_UUID)}
            counter = 0 
            error = 0
            
            if len(objects_list) == 0:
                error = 2
            while counter < len(objects_list):
                to = counter + CHUNKS
                if counter + CHUNKS > len(objects_list):
                    to = len(objects_list)
                js = json.dumps(objects_list[counter:to])
                counter = counter + CHUNKS
                files = {"js":js}
                try:
                    # Send as files to account for large data size. Otherwise, it may fail if sent via a "plain" POST request.
                    r = requests.post(Firelog.PROTOS_ADDPACKET_URL, data = params, files = files)
                except Exception as exc:
                    print exc
                    error = 1
                    break

            print len(objects_list)
            if error == 1:
                print "Error occurred"
            elif error == 2:
                print "No new packets to send"
            elif error == 0:
                print "Packets sent. Updating local DB..."
                query = "UPDATE " + Firelog.DB_FWL_TABLE + " SET sent = 1 WHERE sent = 0;"
                c.execute(query)
                conn.commit()
        
            conn.close() 
        except sqlite3.OperationalError: 
        # Gets raised if path to DB file does not exist or is not accessible. If only the DB file does not exist, it gets created.
            print '1SQLite operational error'
            pass

#-------------------------------------------------------------------------------

    def checkForNewVersion(self):
        PROG_LABEL = 'Firelog'

        try:
            req = urllib2.urlopen(Firelog.PROTOS_VERSION_URL)
            latestVersion = req.read()
            latestVersion = latestVersion.strip()

            if float(latestVersion) > float(Firelog.VERSION):
                clientLocale = locale.getdefaultlocale()
                if clientLocale[0] == 'el_GR':
                    msg = u'Έκδοση ' + latestVersion + u' διαθέσιμη'
                else:  # Assuming en_US for default case
                    msg = 'Version ' + latestVersion + ' available'
            
                if sys.platform.startswith('win32'):
                    WindowsBalloonTip.WindowsBalloonTip(PROG_LABEL, msg)
                    # This is the trick for stopping the service!!!
                    #sys.stopservice = "true"
                    return True
                    
                elif sys.platform.startswith('linux'):
                    gi.repository.Notify.init(PROG_LABEL)
                    notif = gi.repository.Notify.Notification.new('Firelog', msg)
                    notif.show()
                    return True
                else:
                    pass
        except:
            return False
            
#-------------------------------------------------------------------------------

    def processFirewallLogFile(self, interval):
        # The column number containing the protocol, in the line to be sent to the server (not in the line read from the log file).
        PROTO_POS = 3

        startPeriod = Firelog.PREV_FW_LOG_DATETIME
        
        try:
            with open(Firelog.FW_LOG_WITH_PATH) as f:
                try:
                    conn = sqlite3.connect(Firelog.DB_WITH_PATH)
                    self.checkDatabase(conn)
                    c = conn.cursor()
                    numLinesOfInterest = 0  # The number of lines to be included in the calculations
                    
                    for ln in f:
                        # If ufw.log is being processed, split() will produce '[UFW' and 'BLOCK]'. The square brackets need to be removed.
                        # For pfirewall.log, translate() will not have any effect, but may be less expensive than calling sys.platform to determine OS.
                        ln = ln.translate(None, '[]')
                        v = ln.split()

                        # Some minor checks to ensure the line contains data.
                        # Windows pfirewall.log uses '#' for comments.
                        try:
                            if(v[0][0] == '#' or v[0][0] == '\n' or v[0][0] == '\x00'):
                                continue
                        except IndexError:
                            continue
                        
                        dtObj = self.str2date(v)
                        #print 'dtObj:       ' + str(dtObj)
                        #print 'startPeriod: ' + str(startPeriod)
                        #print dtObj > startPeriod
                        if (dtObj != None and dtObj > startPeriod and Firelog.FW_BLOCK_KEYWORD in v):
                            # Line has an earlier-than-now timestamp and concerns dropped packet

                            # The column order of the Windows pfirewall.log has been chosen as "template":
                            # date time action protocol src-ip dst-ip src-port dst-port size tcpflags tcpsyn tcpack tcpwin icmptype icmpcode info path
                            # The first 9 fields are actually required (the rest can be 'null'):
                            # date, time, action, proto, srcip, dstip, srcpt, dstpt, size

                            if sys.platform.startswith('win32'): 
                                # pfirewall.log has '-' for columns without a value, so replace them with 'null' for consistency
                                dataList = [elem if elem != '-' else Firelog.NULL for elem in v]
                                
                            elif sys.platform.startswith('linux'):
                                # Process line, so as to form desired structure
                                date = str(dtObj.date())
                                time = str(dtObj.time())
                                action = Firelog.FW_BLOCK_KEYWORD
                                proto = self.getValueAfter('PROTO=', v)
                                srcip = self.getValueAfter('SRC=', v)
                                dstip = self.getValueAfter('DST=', v)
                                # SPT may not exist
                                srcpt = self.getValueAfter('SPT=', v)
                                # DPT may not exist
                                dstpt = self.getValueAfter('DPT=', v)
                                size = self.getValueAfter('LEN=', v)

                                # Prepare the list for SQL insertion
                                dataList = [date, time, action, proto, srcip, dstip, srcpt, dstpt, size, Firelog.NULL, Firelog.NULL, Firelog.NULL, Firelog.NULL, Firelog.NULL, Firelog.NULL, Firelog.NULL, Firelog.NULL]
                            
                            # Keep 'PROTO=2' etc., as useful conclusions may be derived from their analysis. Otherwise, they will be rejected in future versions.
                            ##########################
                            # Eliminate 'PROTO=2' etc.
                            # if dataList[PROTO_POS] not in Firelog.VALID_PROTO_NAMES:
                                # continue
                            # else:
                                # Line is OK and will be included in calculations
                            numLinesOfInterest += 1
                                # print dataList
                            ##########################
                            
                            # Insert data to DB
                            try:
                                sqlStr = 'INSERT INTO ' + Firelog.DB_FWL_TABLE + ' VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)'
                                c.execute(sqlStr, dataList)
                                conn.commit()
                            except:
                                pass
                                
                        # Update latest firewall log timestamp
                        if dtObj != None:
                            Firelog.PREV_FW_LOG_DATETIME = dtObj
                        
                    # Calculate rates and send data to server
                    data = self.computeRates(numLinesOfInterest)
                    self.sendThem(data, Firelog.PROTOS_ADDRATIO_URL)
                    Firelog.iteration += 1  
                    Firelog.packetsSent += 1

                    conn.close()
                except sqlite3.OperationalError:
                    # Gets raised if path to DB file does not exist or is not accessible. If only the DB file does not exist, it gets created.
                    print '2SQLite operational error'
                    pass

        except IOError: # Log file may not exist (deleted, rotated etc.)
            print 'Could not open file: ' + Firelog.FW_LOG_WITH_PATH
            pass
            
#-------------------------------------------------------------------------------

    def maintainDB(self):
        """Deletes old records so as to prevent the DB file from getting too big.
        """
        try:
            conn = sqlite3.connect(Firelog.DB_WITH_PATH)
            self.checkDatabase(conn)
            c = conn.cursor()
            
            # Delete old data from DB (currently set to 3 months ago)
            try:
                sqlStr = 'DELETE FROM ' + Firelog.DB_FWL_TABLE + ' WHERE date < date("now", "-' + Firelog.PREF_MONTHS_DATA_TO_KEEP_IN_DB + ' month");'
                c.execute(sqlStr)
                
                sqlStr = 'VACUUM;'
                c.execute(sqlStr)
                
                conn.commit()
            except:
                pass
                
            conn.close()
            print 'DB maintenance finished'
        except sqlite3.OperationalError:
            # Gets raised if path to DB file does not exist or is not accessible. If only the DB file does not exist, it gets created.
            print '3SQLite operational error'
            pass

#-------------------------------------------------------------------------------

    def addFirewallRule(self, addRule, srcIP):
        """Adds a rule to block or allow traffic from a given IP address.
        If addRule = True, then block the IP.
        """
        
        if sys.platform.startswith('win32'):
            win_release = platform.release()
            if (win_release == 'XP'):
               if addRule:
                    # netsh firewall add portopening protocol=all port=? mode=enable scope=custom addresses=50.50.50.50 profile=all
                    # Note: 'scope' must be 'CUSTOM' to specify 'addresses'.
                    cmdStr = 'netsh firewall add portopening protocol=all port=? mode=enable scope=custom addresses=' + srcIP + ' profile=all'
               else:
                    # netsh firewall delete portopening protocol=all port=? profile=all 
                    cmdStr = 'netsh firewall delete portopening protocol=all port=? profile=all'
            elif (win_release == 'Vista' or win_release == '7' or win_release == '8'):
                # Win Vista, 7, 8, 8.1
                # Note: Both Win 8 and 8.1 are reported as '8'
               if addRule:
                    # netsh advfirewall firewall add rule name="Firelog - Block 50.50.50.50" dir=in action=block remoteip=50.50.50.50
                    cmdStr = 'netsh advfirewall firewall add rule name="Firelog - Block ' + srcIP + '" dir=in action=block remoteip=' + srcIP 
               else:
                    # netsh advfirewall firewall delete rule name="Firelog - Block 50.50.50.50" dir=in remoteip=50.50.50.50
                    cmdStr = 'netsh advfirewall firewall delete rule name="Firelog - Block ' + srcIP + '" dir=in remoteip=' + srcIP
            else:
                print 'Unsupported Windows platform'

            #print cmdStr        
            subprocess.call(cmdStr)
        elif sys.platform.startswith('linux'):
            if addRule:
                cmdStr = 'ufw deny from ' + srcIP
            else:
                cmdStr = 'ufw delete deny from ' + srcIP

            try:
                #print cmdStr
                subprocess.call(cmdStr)
            except OSError:
                print 'Super-user rights required'

        else:
            print 'Unsupported OS'

#-------------------------------------------------------------------------------

    def isPrivateIP(self, ipStr):
        """Checks whether an IP address belongs to a private or public network range. This is an alternative way if the ipaddress package is unavailable.
        """
        
        # 4 patterns for private IPs and 1 for reserved
        privPatterns = [
                    '^127.\d{1,3}.\d{1,3}.\d{1,3}$', 
                    '^10.\d{1,3}.\d{1,3}.\d{1,3}$', 
                    '^192.168.\d{1,3}.\d{1,3}$', 
                    '^172.(1[6-9]|2[0-9]|3[0-1]).[0-9]{1,3}.[0-9]{1,3}$', 
                    '0\.0\.0\.0' 
                    ]
        
        for p in privPatterns:
            if re.match(p, ipStr) != None:
                return True

        return False

#------------------------------------------------------------------------------- 

    def updateListOfBlockedIPs(self, oldList, url):
        """Gets the list of malicious IPs from server, determines which are to be blocked or unblocked and performs the necessary actions (according to the set preferences). Returns the updated list of malicious IPs.
        """
        newList = [] # New IP list fetched from server
        updatedList = [] # Updated list to be returned at the end
        
        if Firelog.PREF_MANAGE_FW_RULES:
            print 'Firewall rule management option enabled'

            # Get list of malicious IPs from server
            resp = requests.get(url=url)
            jsonResponse = json.loads(resp.text)
            jsonData = jsonResponse['ips']
            for item in jsonData:
                label = item.get('label')
                data = item.get('data')
                if not self.isPrivateIP(label):
                    newList.append(label)
                   
            # Process the two lists to determine IPs to block or unblock
            unchanged = list(set(oldList).intersection(newList))
            unchanged.sort()
            updatedList = unchanged[:]
            #print 'unchanged: ' + str(unchanged)

            toAdd = list(set(newList).difference(unchanged))
            toAdd.sort()
            #print 'add: ' + str(toAdd)
            for ip in toAdd:
                print 'Block ' + ip
                # Add rule
                self.addFirewallRule(True, ip)
                updatedList.append(ip)

            toRemove = list(set(oldList).difference(newList))
            toRemove.sort()
            for ip in toRemove:
                print 'Unblock ' + ip
                self.addFirewallRule(False, ip)

            updatedList.sort()
            return updatedList
        else:
            print 'Firewall rule management option disabled'

#-------------------------------------------------------------------------------

    def performPeriodicTasks(self):
    
        if Firelog.iteration == 0 or Firelog.iteration >= Firelog.THRESHOLD_ITERATIONS or Firelog.packetsSent >= Firelog.THRESHOLD_PACKETS:

            # Send current version to server
            currVersionData = '{"uuid": "' + str(Firelog.CLIENT_UUID) + '","version": "' + str(Firelog.VERSION) + '"}'
            self.sendThem(currVersionData, Firelog.PROTOS_ADDVERSION_URL)
            
            self.checkForNewVersion()
            self.collectPackets()
            self.maintainDB()

            Firelog.iteration = 1
            Firelog.packetsSent = 0

        # The list of blocked IPs should be updated more frequently, so deal with it separately from the other periodic tasks.
        currTime = datetime.now()
        if (currTime - Firelog.lastUpdateOfBlockedIPs).seconds >= Firelog.THRESHOLD_BLOCKED_IP_UPDATE:
            Firelog.oldListOfBlockedIPs = self.updateListOfBlockedIPs(Firelog.oldListOfBlockedIPs, Firelog.PROTOS_TOPDATA_URL)
            Firelog.lastUpdateOfBlockedIPs = currTime
            
#-------------------------------------------------------------------------------

    def __init__(self, startParams = SLEEP_TIME):
        Firelog.CLIENT_UUID = self.readOrGenerateUUID()
        # if self.checkForUpdate(): 
            # return
        self.performPeriodicTasks()
        self.checkServiceStatus()  # Will exit program if true
        self.processFirewallLogFile(startParams)

#-------------------------------------------------------------------------------

#===============================================================================

if __name__ == "__main__":
    while(True):
        mainObj = Firelog()
        time.sleep(Firelog.SLEEP_TIME)
