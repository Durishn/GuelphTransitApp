#!/usr/bin/python

import xlrd,datetime
from datetime import time
import math
import ctypes
import subprocess
import os.path

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
subprocess.call("python data/grabFiles.py", shell=True)

#get proper filenames from links file for reading
for link in filePointer:
	#remove newlines for urlOpener
	link = link.rstrip('\r|\n')
	t = link.partition(guelphLink)
	t2 = 'data/'+t[2]
	t3=t[2].partition('.xlsx')
	nameList.insert(0,t3[0])
	fileNameList.insert(0,t2)

#Instantiate routeList tuple
routeList=()

#read xlsx files with xlrd
currFile=0
for fileN in fileNameList:
	print 'READING FILE ', fileN
	workbook = xlrd.open_workbook(fileN)
	route=() #ROUTE tuple we will store all data
	name=str(nameList[currFile])
	currFile=currFile+1
	route+=(name,)

	#read by sheet
	for sheetCounter in range(0,workbook.nsheets):
		print 'READING SHEET ',sheetCounter, fileN
		worksheet = workbook.sheet_by_index(sheetCounter)
		num_rows = worksheet.nrows - 1
		num_cells = worksheet.ncols - 1
		curr_row = -1

		timeList=""
		#stopName, stopID, times.
		stopList=()
		stopTimeList=()
		#I want to have (3 sets of) times inside of stops inside of routes

		while curr_row < num_rows:
			stopTimeList=()
			curr_row += 1
			row = worksheet.row(curr_row)
			busStop=()
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
					if(hour>23):
						hour-=1
					minute = ((x%3600)/60.) #period after number to divide by signifies float division
					if minute < 59:
						minute = int(math.ceil(minute))
					else:
						minute= int(minute)

					stopTime=str(str(hour)+":"+str(minute))
					stopTime=(stopTime,)
					stopTimeList += stopTime
					numTimes=numTimes+1
					#end of file case
					if(curr_cell==worksheet.ncols-1):
						busStop+= stopTimeList
						route +=(busStop,)
						#print busStop
						#print 'numTimes',numTimes

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
					if(sheetCounter==0):
						busStop+= ('Week',)

					elif(sheetCounter==1):
						busStop+=('Sat',)

					elif(sheetCounter==2):
						busStop+=('Sun',)

					#print 'stopID : ',cell_value

					#end of row/bus stop
				elif (cell_type ==1 and '-' in cell_value and curr_cell==worksheet.ncols-1):
					busStop+= stopTimeList
					route += (busStop,)
					#print busStop

					#print 'numTimes',numTimes
				#elif (cell_type==1 and '-' in cell_value) :
				#	print 'blank stop'

			#end row loop
		#end sheet loop
	#end file loop

		#print 'TOP\n\n'
		routeList+=(route,)
		#print route
		#for x in range(len(route)):
		#	print route[x]
		#	print 'x',x
 

	filesRead=filesRead+1
				#print '	', cell_type, ':', cell_value
print filesRead,' files read:'
for t in fileNameList:
	print t
#routeList
#is the final output tuple.
# ('1A_CollegeEdinburgh', ('University Centre', 100, 'Week', '6:0', ... ,timeN)),

#  (busNameStr, (stopNameStr,stopIDstr,schedTypeStr,time1,time2, ... ,timeN))

###########################################
#print routeList
##########################################

