currLine=1
limit=1498
line=""
splitstr=""
currID=""
stopID=""
currStop=""
stopName=""
lines=[]
toRemove=[]
with open("out.txt","r") as f:
	for x in range(0,limit):
		line=f.readline()
		lines.append(line)

f.close()

for x in range(0,limit):
	line = lines[x]

	splitstr = line.split('|')
	currID = str(splitstr[1])
	currID = currID.rstrip(' ')
	currID = currID.lstrip(' ')

	currStop = str(splitstr[2])
	currStop = currStop.rstrip(' ')
	currStop = currStop.lstrip(' ')
	for y in range(x+1,limit):

		splitstr = lines[y].split()
		stopID = str(splitstr[1])
		stopID = stopID.rstrip(' ')
		stopID = stopID.lstrip(' ')

		stopName = str(splitstr[2])
		stopName = stopName.rstrip(' ')
		stopName = stopName.lstrip(' ')

		if(str(stopID) == str(currID)):
			#print 'Removing'
			#if stops match, add line index to removeList
			toRemove.append(y)
		#this if statement doesn't work.
		#if(stopName!=currStop):
			#print 'was at'+ stopName +' now at '+currStop
			#print 'break'
		#	break
f.close()
currLine=1
blank="\n"
#print toRemove
#open the file for writing
with open("out.txt","w") as f:
	for x in range(1,limit):
		#don't write line indices with repeated stopID's
		if(currLine not in toRemove):
			f.write(lines[x])
		currLine+=1
f.close()

