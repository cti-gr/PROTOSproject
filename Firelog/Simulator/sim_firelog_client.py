
# Firelog client simulator program.
# Developed for the PROTOS Project
# URL: http://protos.cti.gr
# e-mail: protosproject@gmail.com
# Developed using Python 2.7.9

import multiprocessing
import sys
import time, datetime
import random
import uuid
import socket
import httplib, urllib, urllib2, urlparse, requests


class SimFirelogClient(multiprocessing.Process):
    
    # The delay range before a client starts transmitting
    RAND_DELAY_MIN = 0
    RAND_DELAY_MAX = 20
    
    # The number of times a client will send information
    # Use: Ensure controlled and normal termination of the script
    CLIENT_LOOPS = 3
    
    # The number of incidents a client supposedly counted
    RAND_INCIDENTS_MIN = 0
    RAND_INCIDENTS_MAX = 15
    
    # How much the ranges of incidents will be incremented by, at the end of each iteration (0 to disable)
    # Use: A positive value will simulate a continually-increasing epidemic
    INCIDENTS_INCREMENT = 0

    # The time interval a client is idle before transmitting again
    CLIENT_SLEEP_TIME = 30
    
    # Constants and globals from Firelog class -- Do not change
    prevNumIncidents = 0
    prevRate1 = 0
    NULL = 'null'
    
    HTTP = 'http://'
    HTTPS = 'https://'
    PROTOS_HOST = 'protos.cti.gr'
    PROTOS_ADDRATIO_PATH = '/promis/addratio.php'
    PROTOS_ADDRATIO_URL = urlparse.urljoin(HTTP + PROTOS_HOST, PROTOS_ADDRATIO_PATH)
    
#------------------------------------------------------------------------------- 

    def getIPAddress(self):
        # The command:
        # socket.gethostbyname(socket.gethostname())
        # does not work well in Linux. It returns e.g. 127.0.0.1, which is the entry of /etc/hosts
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect(('google.com', 0))
            return s.getsockname()[0]
        except:
            pass

#-------------------------------------------------------------------------------

    def computeRates(self, numIncidents):
        # rate1: Malicious activity (rate of change of numIncidents)
        # rate2: Epidemic rate (rate of change of malicious activity)
        
        if SimFirelogClient.prevNumIncidents == 0 :
            rate1 = SimFirelogClient.NULL
        else:
            rate1 = (float(numIncidents) - float(SimFirelogClient.prevNumIncidents)) / float(SimFirelogClient.prevNumIncidents)

        if SimFirelogClient.prevRate1 == 0 or SimFirelogClient.prevRate1 == SimFirelogClient.NULL or rate1 == SimFirelogClient.NULL:
            rate2 = SimFirelogClient.NULL
        else:
            rate2 = (float(rate1) - float(SimFirelogClient.prevRate1)) / float(SimFirelogClient.prevRate1)

        # Prepare information to be sent in JSON format
        #data = '{"clientid": "' + str(uuid.uuid5(uuid.NAMESPACE_DNS, str(uuid.getnode()))) + '","rate1":' + str(rate1) + ',"rate2":' + str(rate2) + ',"tcount":' + str(numIncidents) + ',"localip":"' + str(self.getIPAddress()) + '"}'

        data = '{"clientid":"Dummy_' + str(self._identity[0]) + '","rate1":' + str(rate1) + ',"rate2":' + str(rate2) + ',"tcount":' + str(numIncidents) + ',"localip":"' + str(self.getIPAddress()) + '"}'

        # Store values for next call
        SimFirelogClient.prevNumIncidents = numIncidents
        SimFirelogClient.prevRate1 = rate1

        return data

#-------------------------------------------------------------------------------

    def sendThem(self, data, theURL):
        try:
            params = urllib.urlencode({"data":data})
            headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
            conn = httplib.HTTPConnection(SimFirelogClient.PROTOS_HOST)
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

    def run(self):
        # Introduce a random delay before starting each thread
        startDelay = random.randint(SimFirelogClient.RAND_DELAY_MIN, SimFirelogClient.RAND_DELAY_MAX)
        time.sleep(startDelay)
        
        incidentsMin = SimFirelogClient.RAND_INCIDENTS_MIN
        incidentsMax = SimFirelogClient.RAND_INCIDENTS_MAX

        for i in range(SimFirelogClient.CLIENT_LOOPS):
            # Compute rates randomly
            incidents = random.randint(incidentsMin, incidentsMax)
            data = self.computeRates(incidents)
            # Output data to send
            #print '[' + str(incidentsMin) + ', ' + str(incidentsMax) + '] ' + str(incidents)
            print 'Client {0:4d} ({1}/{2}): '.format(int(self._identity[0]), i + 1, SimFirelogClient.CLIENT_LOOPS) + str(data) + '\n' #self.name
            
            self.sendThem(data, SimFirelogClient.PROTOS_ADDRATIO_URL)
            
            # Increment the range of reported incidents, if non-zero
            incidentsMin += SimFirelogClient.INCIDENTS_INCREMENT
            incidentsMax += SimFirelogClient.INCIDENTS_INCREMENT

            time.sleep(SimFirelogClient.CLIENT_SLEEP_TIME)

        return
        
#------------------------------------------------------------------------------- 

if __name__ == '__main__':
    MAX = 150
    num = raw_input('Enter number of simulated clients: ')
    if int(num) > max:
        print 'Number too large! Acceptable range: [1, ' + str(MAX) + ']'
        sys.exit()
    
    jobs = []
    for i in range(int(num)):
        p = SimFirelogClient()
        jobs.append(p)
        p.start()
        
    for j in jobs:
        j.join()
