<?php
//getNextBus.php
//William Aidan Maher
//March 22 / 2015

//dummy data
//$userIDstr = "uniquename";
//$stopIDstr = "5705";

//Make dummy array 
//$_POST=array('UID' => $userIDstr,'stopID' => $stopIDstr);

//echo $_POST,PHP_EOL;
$userID=$_POST['UID'];
$stopID=$_POST['stopID'];
$response = array();

if(strlen($stopID)==4){
	//Execute Command
	//$command = escapeshellcmd("python /home/bull3t3/Desktop/3760transit/3760transit/ServerScripts/nextBusScrape.py $stopID");
	$command = escapeshellcmd("./home/sysadmin/3760/3760transit/serverSideStuff/php_root $stopID");
	$output = shell_exec($command);

	$etaOne=(int)(strtok($output,","));
	$etaTwo=(int)(strtok(" "));
	$response["success"]=1;

}else{
	$response["fail"]=1;
	$etaOne="php Err";
	$etaTwo="php Err";
}
$response["ETA1"] = $etaOne;
$response["ETA2"] = $etaTwo;

echo json_encode($response);

?>

