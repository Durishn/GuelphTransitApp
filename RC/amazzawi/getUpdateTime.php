<?php

$response = array();
$response["success"] = 0;
$response["message"] = 0;

require_once __DIR__ . '/db_connect.php';

//connect to the database
$db = new DB_CONNECT();

// Time the db was updated
$timeResult = mysql_query("SELECT * FROM information_schema.tables WHERE table_name='Routes'") or die(mysql_error());

//check for empty result
if(mysql_num_rows($timeResult) > 0)
{
    $response["Time"] = array();

    while($row = mysql_fetch_array($timeResult))
    {
        $time = array();
	$time["update"] = $row["CREATE_TIME"];
	array_push($response["Time"], $time);
    }

    //sucess
    $response["success"] = 1;
    $response["message"] = "Success";
}

echo json_encode($response);
die()

?>
