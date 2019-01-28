<?php

/**
 * This file will fetch information from the sql table and convert it
 * into JSON which the phone will utilize
 *
 * @author     Anthony Mazzawi <amazzawi@mail.uoguelph.ca>
 */

$response = array();

require_once __DIR__ . '/db_connect.php';

//connect to the database
$db = new DB_CONNECT();

$result = mysql_query("SELECT * FROM Routes") or die(mysql_error());

//check for empty result
if(mysql_num_rows($result) > 0)
{
    $response["Routes"] = array();

    while($row = mysql_fetch_array($result))
    {
        $routes = array();
        $routes["route"] = $row["route"];
        $routes["stopID"] = $row["stopID"];
        $routes["stopName"] = $row["stopName"];
        $routes["Latitude"] = $row["Latitude"];
        $routes["Longitude"] = $row["Longitude"];
        $routes["Latitude"] = $row["timeList"];
        $routes["day"] = $row["day"];

        array_push($response["Routes"], $routes);
    }
    //success
    $response["success"] = 1;

    //echo JSON response
    echo json_enconde($response);
}
else
{
    $response["success"] = 0;
    $response["message"] = "Nothing found";

    echo json_encode($response);
}

?>

