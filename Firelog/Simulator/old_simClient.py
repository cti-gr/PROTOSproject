# Old version of sim client. Contains some logical error.


import uuid, random, socket, time, random, urllib, httplib, datetime
class simClient:
	clients=[]
	sleepDiff=0
	sleepClients=0
	dateToStop=datetime.datetime(2010, 9, 27, 5, 0, 0, 0)
	def __init__(self,nClients=30,sleepDiff=1,sleepClients=30):
		for i in range (0,nClients):
			previousIncidents = random.randint(1,1000)
			currentIncidents = random.randint(1,1000)
			currentRate1 = (float(currentIncidents)-float(previousIncidents))/float(previousIncidents)
			previousIncidents = currentIncidents
			previousRate1 = currentRate1
			
			currentIncidents = random.randint(1,1000)
			currentRate1 = (float(currentIncidents)-float(previousIncidents))/float(previousIncidents)
			if previousRate1==0:
				currentRate2='null'
			else:
				currentRate2 = (float(currentRate1)-float(previousRate1))/float(previousRate1)
			previousIncidents = currentIncidents
			previousRate1 = currentRate1
			simClient.clients.append([i,previousIncidents,previousRate1])
		simClient.sleepDiff=sleepDiff
		simClient.sleepClients=sleepClients
		self.simulateData()
	
	def simulateData(self):
		iterationsDiff=0
		iterationsClient=0
		while datetime.datetime.now() < simClient.dateToStop:
			for client in simClient.clients:
				currentIncidents = random.randint(1,1000)
				currentRate1 = (float(currentIncidents)-float(client[1]))/float(client[1])
				if client[2]==0:
					currentRate2='null'
				else:
					currentRate2 = (float(currentRate1)-float(client[2]))/float(client[2])
				client[1]=currentIncidents
				client[2]=currentRate1
				
				data='{"clientid": "Dummy'+str(client[0])+'","rate1":'+str(currentRate1)+',"rate2":'+str(currentRate2)+',"tcount":' +str(currentIncidents)+',"localip":"Dummy'+str(socket.gethostbyname(socket.gethostname()))+'"}'
				#print data
				self.sendThem(data)
				time.sleep(simClient.sleepDiff)
			iterationsDiff=iterationsDiff+1
			iterationsClient=iterationsClient+1
			
			if iterationsDiff==100:
				if simClient.sleepDiff>0:
					simClient.sleepDiff=simClient.sleepDiff-0.1
					if simClient.sleepDiff<0:
						simClient.sleepDiff=0
					data='{"clientid": "Pace between clients changed","rate1":0,"rate2":0,"tcount":0,"localip":"PaceNow'+str(simClient.sleepDiff)+'"}'
					self.sendThem(data)
					#print data
					iterationsDiff=0
			
			if iterationsClient==300:
				if simClient.sleepClients>0:
					simClient.sleepClients=simClient.sleepClients-0.1
					if simClient.sleepClients<0:
						simClient.sleepClients=0
					data='{"clientid": "Pace between chunks changed","rate1":0,"rate2":0,"tcount":0,"localip":"PaceNow'+str(simClient.sleepClients)+'"}'
					self.sendThem(data)
					#print data
					iterationsClient=0
			time.sleep(simClient.sleepClients)
			
	def sendThem(self,data):
		try:
			params=urllib.urlencode({'data':data})
			headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
			conn = httplib.HTTPConnection("prescott.cti.gr:80")
			conn.request("POST", "/promis/addratio.php", params, headers)
			response = conn.getresponse()
			data = response.read()
			conn.close()
		except:
			pass
			
if __name__ == "__main__":
	simObj=simClient(30,1,30)