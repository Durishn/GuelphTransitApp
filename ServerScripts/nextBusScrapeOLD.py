import re
from selenium import webdriver
import time
import sys
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities


def getEtas(id):
	
	#stopUrlTags['xxxx'] = "xxxxxxxxxxxxx"
	#Stopped at line 220

	##ID to URL Mapping##
	stopUrlTags = {}
	##Route: 1A##
	stopUrlTags['0100'] = "univcent_d"
	stopUrlTags['0107'] = "edincarr_l"
	stopUrlTags['0108'] = "edinrick_l"
	stopUrlTags['0120'] = "collmacd_l"
	stopUrlTags['0121'] = "eastdund_l"
	stopUrlTags['0122'] = "eastarbo_l"

	##Route: 1B##
	stopUrlTags['0151'] = "crbostop_l"
	stopUrlTags['0152'] = "studresi_l"
	stopUrlTags['0153'] = "collmacd_l"
	stopUrlTags['0166'] = "edinrick_l"
	stopUrlTags['0167'] = "edincarr_l"
	

	##Route: 2A##
	stopUrlTags['0211'] = "fifegate_l"
	stopUrlTags['0214'] = "254elmi_l"
	stopUrlTags['0217'] = "elmicomm_l"
	stopUrlTags['0219'] = "impewill_l"
	stopUrlTags['0222'] = "impespee_l"
	stopUrlTags['0226'] = "impewood_l"
	stopUrlTags['0227'] = "woodroya_l"
	stopUrlTags['0228'] = "woodrega_l"
	stopUrlTags['0229'] = "woodsilv_l"
	stopUrlTags['0230'] = "wooddaws_l"
	stopUrlTags['0231'] = "woodedin_l"
	stopUrlTags['0232'] = "woodnick_l"
	stopUrlTags['0233'] = "smarcent_l"
	stopUrlTags['0234'] = "woolmari_l"
	stopUrlTags['0238'] = "woollond_l"

	##Route: 2B##
	stopUrlTags['0252'] = "wollcard_l"
	stopUrlTags['0254'] = "woollond_l"
	stopUrlTags['0260'] = "woolmari_l"
	stopUrlTags['0261'] = "smarcent_l"
	stopUrlTags['0262'] = "woodnick_l"
	stopUrlTags['0263'] = "woodedin_l"
	stopUrlTags['0264'] = "wooddaws_l"
	stopUrlTags['0266'] = "woodsilv_l"
	stopUrlTags['0267'] = "woodrega_l"
	stopUrlTags['0268'] = "woodroya_l"
	stopUrlTags['0269'] = "impewood_l"
	stopUrlTags['0273'] = "impespee_l"
	stopUrlTags['0276'] = "impewill_l"
	stopUrlTags['0278'] = "elmicomm_l"
	stopUrlTags['0280'] = "elmifres_l"
	stopUrlTags['0283'] = "fifegate_l"
	stopUrlTags['0296'] = "wellplaz_l"

	##Route: 3A##
	stopUrlTags['0302'] = "wollcard_l"
	stopUrlTags['0307'] = "westlour_l"
	stopUrlTags['0308'] = "westdivi_l"
	stopUrlTags['0309'] = "stjoseph_l"
	stopUrlTags['0314'] = "smarcent_l"

	stopUrlTags['0318'] = "woodgolf_l"
	stopUrlTags['0322'] = "victwood_l"
	stopUrlTags['0325'] = "victeram_l"
	stopUrlTags['0330'] = "eastsum_l"
	stopUrlTags['0331'] = "wastgran_l"
	stopUrlTags['0334'] = "yorkwats_l"
	stopUrlTags['0335'] = "yorkyork_l"
	stopUrlTags['0337'] = "yorkbeau_l"
	stopUrlTags['0340'] = "stonarbo_l"
	stopUrlTags['0347'] = "wellplaz_l"

	##Route: 3B##
	stopUrlTags['0358'] = "stonarbo_l"
	stopUrlTags['0360'] = "535york_l"
	stopUrlTags['0361'] = "yorkbeau_l"
	stopUrlTags['0362'] = "728york_l"
	stopUrlTags['0364'] = "yorkwats_l"
	stopUrlTags['0367'] = "wastgran_l"
	stopUrlTags['0369'] = "eastsum_l"
	stopUrlTags['0374'] = "victeram_l"
	stopUrlTags['0377'] = "victwood_l"
	stopUrlTags['0381'] = "woodgolf_l"
	stopUrlTags['0385'] = "walmart_l"
	stopUrlTags['0390'] = "stjoseph_l"
	stopUrlTags['0391'] = "westdivi_l"
	stopUrlTags['0392'] = "westlour_l"


	##Route: 4###
	stopUrlTags['0406'] = "yorkbroc1_l"
	stopUrlTags['0408'] = "535york_l"
	stopUrlTags['0409'] = "yorkbeau1_l"
	stopUrlTags['0410'] = "728york1_l"
	stopUrlTags['0412'] = "yorkwell1_l"
	stopUrlTags['0420'] = "yorkwell2_l"
	stopUrlTags['0421'] = "780york_l"
	stopUrlTags['0423'] = "yorkbeau2_l"
	stopUrlTags['0425'] = "yorkbroc2_l"

	##Route: 5###

	##Route: 6###

	##Route: 7###

	##Route: 8###

	##Route: 9###

	##Route: 10##

	##Route: 11##

	##Route: 12##

	##Route: 13##

	##Route: 14##

	##Route: 15##

	##Route: 16##

	##Route: 20##

	##Route: 57##
	stopUrlTags['5705'] = "harvyoun_l"
	stopUrlTags['5706'] = "ironreid_l"
	stopUrlTags['5708'] = "632scot_l"
	##End of Mapping##



	baseUrl = "http://www.nextbus.com/#!/guelph/"
	if(route is '1a' or route is '1A' or route is '2a' or route is '2A' or route is '3a' or route is '3A'):
		baseRoute = route + "/0" + route + "_loop/"
	else:
		baseRoute = route + "/" + route + "_loop/"
	baseStop = stopUrlTags[id]
	finalUrl = baseUrl + baseRoute + baseStop
	print finalUrl

	browser = webdriver.PhantomJS('C:/Users/Owner/Downloads/phantomjs-2.0.0-windows/phantomjs-2.0.0-windows/bin/phantomjs')
	browser.get(finalUrl)
	time.sleep(3)

	firstTwoTimes = browser.title
	splitfirstTwoTimes = firstTwoTimes.split(":")
	splitTimes = splitfirstTwoTimes[1].split("&")

	etaOne = splitTimes[0]
	etaOne = etaOne[1:]
	etaTwo = splitTimes[1]
	etaTwo = etaTwo[1:-4]


	#print "1st Next Bus time: " + etaOne
	#print "2nd Next Bus time: " + etaTwo
	browser.close()
	#Parse into JSON string
	JSONdata = simplejson.dumps({"ETA1":etaOne,"ETA2":etaTwo})
	#print to stdout, PHP will grab it
	print JSONdata


#getEtas("57", "5705")
id = sys.argv[1]
getEtas(id)
