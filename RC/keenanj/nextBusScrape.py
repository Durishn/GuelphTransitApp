#!/usr/bin/python
import os
import re
from selenium import webdriver
from time import sleep
import sys
import json
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
browserExists=-1
def processLine(line):
	global browserExists
	if(line != ''):
		if(id in line):
			temp = line.split(" = ")
			baseStop=temp[1]
			browserExists=1
			return baseStop
		return ' '
	else:
		return ''

	#Stopped at line 220
def getEtas(id):
	global browserExists
	finalUrl=""
	try:
		route=id[:2]
		baseUrl = "http://www.nextbus.com/#!/guelph/"
		dataPath = "/home/sysadmin/3760/3760transit/ServerScripts/data/"
		if(route=="01" or route == "02" or route == "03"):
			#filePointer=open(route);
			filePointer=open(dataPath+route)
			for line in filePointer:
				temp=processLine(line)
				if(temp==''):
					browserExists=0
					raise KeyError
				elif(temp!=' '):
					baseStop=temp
					finalUrl=baseUrl+baseStop
			filePointer.close()
		else:
			#filePointer=open(route);
			filePointer=open(dataPath+route)
			temp=filePointer.readline().split(" = ")
			baseRoute=temp[1].rstrip()
			for line in filePointer:
				temp=processLine(line)
				if(temp==''):
					browserExists=0
					raise KeyError
				elif(temp!=' '):
					baseStop=temp
					finalUrl=baseUrl+baseRoute+baseStop
	except KeyError:
		browserExists=0
		filePointer.close()
		pass
	if(browserExists==1):
		browserPath = '/home/bull3t3/Desktop/sel/phantomjs/bin/phantomjs'
        #browserPath = '/home/sysadmin/3760/phantomjs/bin/phantomjs'
		browser = webdriver.PhantomJS(browserPath,service_log_path=os.path.devnull)
		browser.get(finalUrl)
		#sleep necessary for phantomJS
		sleep(1)
		firstTwoTimes = browser.title

		
	if (browserExists) != 0 and finalUrl!="":
		try:
			splitfirstTwoTimes = firstTwoTimes.split(":")
			splitTimes = splitfirstTwoTimes[1].split("&")
			etaOne = splitTimes[0]
			etaOne = etaOne[1:]
			etaTwo = splitTimes[1]
			etaTwo = etaTwo[1:-4]
			etaOne=etaOne.rstrip()
			etaTwo=etaTwo.rstrip()
		except IndexError:
			etaOne = "PythErr!"
			etaTwo = "PytherErr!"
			pass
	else:
		etaOne = "PythErr"
		etaTwo = "PythErr"

	if(browserExists==1):
		browser.close()
	#PHP will read this
	print etaOne +","+etaTwo


#getEtas("57", "5705")
try:
	id = sys.argv[1]
	getEtas(id)
except IndexError:
	print 'noarg,specified'
