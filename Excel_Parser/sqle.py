#!/usr/bin/python

""" mySQL functions to implement Guelph Transit Bus Schedule

This module connects to the mySQL Guelph Transit Database and creates
tables for each of the bus routes.  Information is received such as 
stop name, stop id and added to the tables.  Coordinates from a location
are calculated in this module using google Places API

"""

import MySQLdb as sql
import sys
import getpass
import ctypes
from googleplaces import GooglePlaces, types, lang

def connect(db):  

  # Connect to the database
  try:

    # Get mySQL information
    username = raw_input("Enter mySQL username: ")
    password = getpass.getpass("Enter mySQL password: ")

    # Database connection
    db = sql.connect(host = 'localhost', user = username, passwd = password, db = 'GuelphTransit')

  # Handle any errors that could occur
  except sql.Error, e:

    print "Error %d: %s" % (e.args[0], e.args[1])
    sys.exit(1)

  else:

    print "Connected to the Database"
    return db
 
def createRoutesTable(db):

  cur = db.cursor()
  print "Creating Routes Table..."

  cur.execute("DROP TABLE IF EXISTS Routes")
  print "Old Routes Table dropped."
   
  cur.execute("CREATE TABLE Routes (route varchar(100) NOT NULL, stopID varchar(5) NOT NULL, stopName VARCHAR(100) NOT NULL, Latitude FLOAT(10, 6), Longitude FLOAT(10, 6), timeList VARCHAR(1000)  NOT NULL, day VARCHAR(5) NOT NULL, PRIMARY KEY (route, stopID, day))")

def close(db):

  # Close the MySQL connection
  print "Closed the connection"
  db.close()

def coordinates(address):
  # The major problem now is if an intersection has two bus stops
  # the API will spit out two coordinates and we need to determine
  # which bus stop is which

  # To determine the coordinates, the google Places API must be used
  apiKey = 'AIzaSyDysbMzE596SYG3kRdGGhJOGI6piDSXtgw'
  google_places = GooglePlaces(apiKey)

  search = str(address + " ,Guelph, ON")
  # To find specific bus stop coordinates, the address from the
  # spreadsheet is used and tagging key words like bus and bus_station
  result = google_places.nearby_search(location= address, keyword = 'bus', radius=15, types=['bus_station'])

  x = 0
  print address
  for place in result.places:
    x = x + 1
    a = place.geo_location
    print a
  #if x > 1:
  #  return None
  #else:
  #  return a
  #print result.places.geo_location

def insert(db, info):

  route = info[0]

  print "Inserting " + route + " into database."
  for x in range(1, len(info)):
    stopName = str(info[x][0])
    stopID = str(info[x][1])
    day = str(info[x][2])
    timeList = ''
    for y in range(3, len(info[x])):
      timeList = timeList + str(info[x][y])+" "
    
    timeList = str(timeList)

    insertStop(db, route, stopName, stopID, day, timeList)
    #coordinates(stopName)
    
 
 
<<<<<<< HEAD
def insertStop(route, stopName, stopID, day, timeList):
  if day == "Sun":
    print "new stop"
    print stopName
    print stopID
    print day
    print timeList
#   Insert everything except coordinates first
#  insertStatement = "INSERT INTO " + info + " ('stopID', 'stopName', 'timeList', 'day') VALUES ("
=======
def insertStop(db, route, stopName, stopID, day, timeList):
  if route == "1A_CollegeEdinburgh":
    routeNum = str("1A")
  elif route == "1B_CollegeEdinburgh":
    routeNum = str("1B")
  elif route == "2A_WestLoop-Oct62014" or route == "2A_WestLoop":
    routeNum = str("2A")
  elif route == "2B_WestLoop":
    routeNum = str("2B")
  elif route == "3A_EastLoop":
    routeNum = str("3A")
  elif route == "3B_EastLoop":
    routeNum = str("3B")
  elif route == "4_York":
    routeNum = str("4")
  elif route == "5_Gordon1" or route == "5_Gordon":
    routeNum = str("5")
  elif route == "6_HarvardIronwood":
    routeNum = str("6")
  elif route == "7_KortrightDowney":
    routeNum = str("7")
  elif route == "8_StoneRoadMall":
    routeNum = str("8")
  elif route == "9_Waterloo":
    routeNum = str("9")
  elif route == "10_Imperial":
    routeNum = str("10")
  elif route == "11_WillowWest":
    routeNum = str("11")
  elif route == "12_GeneralHospital":
    routeNum = str("12")
  elif route == "13_VRRC":
    routeNum = str("13")
  elif route == "14_Grange":
    routeNum = str("14")
  elif route == "15_UniversityCollege":
    routeNum = str("15")
  elif route == "16_Southgate-Oct62014" or route == "16_Southgate":
    routeNum = str("16")
  elif route == "20_NorthwestIndustrial":
    routeNum = str("20")
  elif route == "50_StoneRoadExpress":
    routeNum = str("50")
  elif route == "56_VictoriaExpress-30MinServiceSept2014" or route == "56_VictoriaExpress":
    routeNum = str("56")
  elif route == "57_HarvardExpress":
    routeNum = str("57")
  else:
    routeNum = str("")
  Lat = None
  Long = None
  
  insert = ("INSERT IGNORE INTO Routes (route, stopName, stopID, Latitude, Longitude, timeList, day) VALUES (%s, %s, %s, %s, %s, %s, %s)")
 
  stopInfo = (routeNum, stopName, stopID, None, None, timeList, day)

  #Insert everything except coordinates first

  query = db.cursor()
  query.execute(insert, stopInfo)
  db.commit()
  query.close()

#  print insertStatement

  # Latitude is the first element of the dictionary and longitude is second
 # if stopName == "University Centre":
  #  location = coordinates(stopName + " ,Guelph")
 # else:
  #  location  = coordinates("Kortright Rd. W. at Rickson Ave.")

#  if location == None:
#    print "Coordinates for .(route# stopid# stopname#...) must be entered manually."
#  else:
#    print location.values()[0]
#    print location.values()[1]

if __name__ == "__main__":
  coordinates("Kortright Ave. W. at Rickson Ave.")

