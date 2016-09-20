import os
import sys
import time
import win32event, win32service, win32serviceutil, servicemanager


sys.stopservice = "false"
startparams = 30 # in seconds

def main(servObj):
    sys.path.insert(0, os.getcwd())
    import Firelog
    #servicemanager.LogInfoMsg("I am in main!!!!")    
    #time.sleep(20);
    Firelog.Firelog(startparams)
    # a == True means we are asked to update a == False it finished normally
    #servicemanager.LogInfoMsg("I am about to return %s" % sys.stopservice)
    return sys.stopservice


class ServiceLauncher(win32serviceutil.ServiceFramework):

    _svc_name_ = 'FirelogSvc'
    _svc_display_name_ ='Firelog Service'
    _svc_description_ = 'Firelog Service'

    
    def __init__(self, args):
        win32serviceutil.ServiceFramework.__init__(self, args)
        servicemanager.LogInfoMsg('Firelog service initialised')
        self.hWaitStop = win32event.CreateEvent(None, 0, 0, None)

        
    def SvcStop(self):
        servicemanager.LogInfoMsg('Stopping Firelog...')
        self.ReportServiceStatus(win32service.SERVICE_STOP_PENDING)
        sys.stopservice = "true"
        win32event.SetEvent(self.hWaitStop)
        #self.ReportServiceStatus(win32service.SERVICE_STOPPED)
        #self.log('Stopped Firelog')
        servicemanager.LogInfoMsg('Firelog service has stopped')

            
    def SvcDoRun(self):
        servicemanager.LogInfoMsg('Starting Firelog...')
        self.ReportServiceStatus(win32service.SERVICE_START_PENDING)
        try:
            self.ReportServiceStatus(win32service.SERVICE_RUNNING)
            self.start()
            #self.log('Now we are asked to stop')
            #self.SvcStop()
            #win32event.WaitForSingleObject(self.stop_event, win32event.INFINITE)
        except Exception, x:
            servicemanager.LogInfoMsg('Firelog Exception : %s' % x)
            self.SvcStop()

            
    def start(self):
        servicemanager.LogInfoMsg('Firelog service has started')
        while (sys.stopservice == "false"):
            if (main(self) == "true"):
                # value 10 means that we are required to exit out of the while
                #servicemanager.LogInfoMsg("I am about to break free!!")
                continue
            #time.sleep(startparams)

            
    # To be used in the future
    def stop(self):
        pass
