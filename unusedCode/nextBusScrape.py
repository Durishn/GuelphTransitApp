import re
from selenium import webdriver
import time
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities

def getEtas()
	print "Opening Broswer..."
	broswer = webdriver.Firefox()
	broswer.get("http://www.nextbus.com/#!/guelph/1a/01a_loop/univcent_d/gordston_l")

#Route Selection#
	broswer.find_element_by_xpath("/html/body/div[1]/div/div/main/form/div[2]/div[2]/div").click()
	browser.find_element_by_xpath(routeNumber).click()

#Direction Selection#
	broswer.find_element_by_xpath("/html/body/div[1]/div/div/main/form/div[2]/div[3]/div").click()
	browser.find_element_by_xpath(directionSelection).click()

#Stop Selection#
	broswer.find_element_by_xpath("/html/body/div[1]/div/div/main/form/div[2]/div[4]/div").click()
	browser.find_element_by_xpath(stopSelection).click()

	etaOne = broswer.find_element_by_xpath(" /div/table/tbody/tr[1]/td[1]").text()
	etaTwo = broswer.find_element_by_xpath("/div/table/tbody/tr[2]/td[1]").text()
	etaThree = broswer.find_element_by_xpath("/html/body/div[1]/div/div/main/article/div[1]/div/table/tbody/tr[3]/td[1]").text()











