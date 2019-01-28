<?php

$response = array();
$response["success"] = 0;
$response["message"] = 0;

require_once __DIR__ . '/db_connect.php';

//connect to the database
$db = new DB_CONNECT();

$result = mysql_query("SELECT * FROM Routes WHERE day != 'Express'") or die(mysql_error());
$result2 = mysql_query("SELECT * FROM Routes WHERE day = 'Express'") or die(mysql_error());

//check for empty result
if(mysql_num_rows($result) > 0 && mysql_num_rows($result2))
{
    $response["Routes"] = array();
    $response["Express"] = array();

    while($row = mysql_fetch_array($result))
    {
        $routes = array();
        $routes["route"] = $row["route"];
        $routes["stopID"] = $row["stopID"];
        $routes["stopName"] = $row["stopName"];
        $routes["Latitude"] = $row["Latitude"];
        $routes["Longitude"] = $row["Longitude"];
        $routes["timeList"] = $row["timeList"];
        $routes["day"] = $row["day"];
        array_push($response["Routes"], $routes);
    }

    while($row = mysql_fetch_array($result2))
    {
        $express = array();
        $express["route"] = $row["route"];
        $express["stopID"] = $row["stopID"];
        $express["stopName"] = $row["stopName"];
        $express["Latitude"] = $row["Latitude"];
        $express["Longitude"] = $row["Longitude"];
        $express["timeList"] = $row["timeList"];
        $express["day"] = $row["day"];
        array_push($response["Express"], $express);
    }

    //success
    $response["success"] = 1;
    $response["message"] = "Success";
}

echo json_encode($response);
die();

?>





