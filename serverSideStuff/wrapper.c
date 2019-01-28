/*wrapper.c
* William Aidan Maher
* Binary wrapper to call shell script
* which calls python nextBusScrape.py
* the wraper has 1 argument, the stopID
**************************************
* chown root php_root
* chmod u=rwx,go=xr,+s php_root
*/

#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
int main (int argc, char * argv[]){
    setuid(0);
    //Size will never be more than 128 characters (97 + 4 chars at most).
    char* command = malloc(sizeof(char)*128);
    if(command!=NULL){
    	sprintf(command, "/bin/sh /home/sysadmin/3760/3760transit/serverSideStuff/php_shell.sh %s",argv[1]);
    	system (command);
    	free(command);
    }else{
    	printf("Malloc,fail");
    }
    return 0;
}
