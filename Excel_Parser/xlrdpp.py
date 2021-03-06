#!/usr/bin/python

import xlrd,datetime
from datetime import time
import math
import ctypes
import subprocess
import os.path
from sqlEntry import insert
from sqlEntry import connect, close, createTables, insert

#global sheetCounter Var, workaround 
#instead of passing workbook to sub-functions
#that the parser calls
sheetCounter=0

def readFile(workbook,busName):
	route=() #ROUTE tuple we will store all data
	route+=(busName,)
	for count in range(0,workbook.nsheets):
		global sheetCounter
		sheetCounter = count
		print 'READING SHEET ',sheetCounter, fileN

		#read by sheet
		sheetCounter=count
		worksheet = workbook.sheet_by_index(count)	
		stopList=readSheet(worksheet)
		#print stopList
		route+=stopList
	return route

def readSheet(worksheet):
	num_rows = worksheet.nrows - 1
	num_cells = worksheet.ncols - 1
	curr_row = -1

	timeList=""
	#stopName, stopID, times.
	stopList=()

	while curr_row < num_rows:
		stopTimeList=()
		curr_row += 1
		row = worksheet.row(curr_row)
		busStop=readRow(row,num_cells,worksheet,curr_row)
		if(busStop is not None):
			#print busStop
			stopList+=(busStop,)
	return stopList

def readRow(row,num_cells,worksheet,curr_row):
	busStop=()
	stopTimeList=()

	curr_cell= -1

	#print 'Row:', curr_row
	numTimes=0 # number of times at a stop
	#print worksheet.ncols
	while curr_cell < num_cells:
		#iterate cell, A1 -> A2 -> A3 ----> B1 -> B2 -> B3
		#print curr_cell
		stopTime=()
		stopID=()
		curr_cell += 1
		# Cell Types: 0=Empty, 1=Text, 2=Number, 3=Date, 4=Boolean, 5=Error, 6=Blank
		#Get type and value
		cell_type = worksheet.cell_type(curr_row, curr_cell)
		cell_value = worksheet.cell_value(curr_row, curr_cell)
		#times
		if cell_type == 3:
			x = cell_value# a float
			x = int(x * 24 * 3600) # convert to number of seconds
			hour = x//3600
			#if(hour>23):
			#	hour-=1
			minute = ((x%3600)/60.) #period after number to divide by signifies float division
			if minute < 59:
				minute = int(math.ceil(minute))
			else:
				minute= int(minute)

			if(minute !=0):
				stopTime=str(str(hour)+":"+str(minute))
			elif(minute==0):
				stopTime=str(str(hour)+":"+str(minute)+'0')

			stopTime=(stopTime,)
			stopTimeList += stopTime
			numTimes=numTimes+1
			#end of file case
			if(curr_cell==worksheet.ncols-1):
				busStop+= stopTimeList
				#print busStop
				return busStop
				#print 'numTimes',numTimes
			

			#stopName
		elif (cell_type == 1 and '-' not in(str(cell_value)) and len(str(cell_value))>4 
			and str(cell_value)!='Stop Name' and str(cell_value)!='Stop ID' 
			and str(cell_value)!='Stop ID' and str(cell_value) and str(cell_value)!='Schedule'):
			#print 'bus stop:', cell_value

			stopName=str(cell_value) #cast to str
			#print 'bus stop '+busStop
			busStop+=(stopName,) #cast to str-tuple


			#stopID
		elif cell_type == 1 and (len(str(cell_value))==4) and '-' not in (str(cell_value)):
			#code for stopID
			stopID=int(cell_value)
			#print 'stopID: ',stopID
			stopID=(stopID,)
			busStop += stopID
			global sheetCounter 
			sheetcounter=sheetCounter
			if(sheetcounter==0):
				busStop+= ('Week',)

			elif(sheetcounter==1):
				busStop+=('Sat',)

			elif(sheetcounter==2):
				busStop+=('Sun',)

			#print 'stopID : ',cell_value

			#end of row/bus stop
		elif (cell_type ==1 and '-' in cell_value and curr_cell==worksheet.ncols-1):
			busStop+= stopTimeList
			return busStop
			#print busStop

			#print 'numTimes',numTimes
		#elif (cell_type==1 and '-' in cell_value) :
		#	print 'blank stop'

filePointer=open('data/links.txt','r')
guelphLink='http://guelph.ca/wp-content/uploads/Route'
fileNameList=list()
filesRead=0
nameList=list()


#All busses
#routeName - str
#stopName - str
#stopID - str
#numStops

#os.system("data/grabFiles.py")
def getFiles(a):
	filePointer=open('data/links.txt','r')
	for link  in filePointer:
		link = link.rstrip('\r|\n')
		t = link.partition(guelphLink)
		t2 = ('data/'+t[2])
		#if missing a file, re download them all in the list
		if(not(os.path.isfile(t2))):
			subprocess.call("python data/grabFiles.py", shell=True)
			break
	filePointer.close()

db = None
#db = connect(db)
#createTables(db)
#get proper filenames from links file for reading
for link in filePointer:
	#remove newlines for urlOpener
	link = link.rstrip('\r|\n')
	t = link.partition(guelphLink)
	t2 = 'data/'+t[2]
	t3=t[2].partition('.xlsx')
	nameList.insert(0,t3[0])
	fileNameList.insert(0,t2)

filePointer.close()
#Instantiate routeList tuple
routeList=()

#read xlsx files with xlrd
currFile=0
print 'Files to read'
for t in fileNameList:
	print t
for fileN in fileNameList:
	getFiles(1)
	print 'READING FILE ', fileN
	workbook = xlrd.open_workbook(fileN)
	busName=str(nameList[currFile])	
	route=readFile(workbook,busName)
	insert(route)
	currFile=currFile+1
	routeList+=(route,)
	filesRead=filesRead+1
print filesRead,' files read:'
#close(db)
#routeList
#is the final output tuple.
# ('1A_CollegeEdinburgh', ('University Centre', 100, 'Week', '6:0', ... ,timeN)),

#  (busNameStr, (stopNameStr,stopIDstr,schedTypeStr,time1,time2, ... ,timeN))

###########################################
#print routeList
##########################################
#print route
#for x in range(len(routeList)):
#	print routeList[x]
	#for x in range(len(route)):
	#	print route[x]
	#	print 'x',x
