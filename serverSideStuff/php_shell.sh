#!bin/sh
# This script calls the python script which scrapes
# the nextBus time identified by the stopID which is
# in the argument sent from binary wrapper php_root
# with the argument of the stopID in arg #1, hence $1
# arg sent to python script
# chown root php_shell.sh
# chmod u=rwx,go=xr php_shell.sh
python2 /home/sysadmin/3760/3760transit/ServerScripts/nextBusScrape.py $1
