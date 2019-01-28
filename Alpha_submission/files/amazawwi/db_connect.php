<?php

/**
 * Functions to connect to the MYSQL database.
 *
 * @author     Anthony Mazzawi <amazzawi@mail.uoguelph.ca>
 */

class DB_CONNECT {

    function __construct() {
        $this->connect();
    }

    function __destruct()
    {
        $this->close();
    }

    function connect() 
    {
        // import database variables
        require_once __DIR__ . '/db_config.php';

        // Connect to the database
        $con = mysql_connect(DB_SERVER, DB_USER, DB_PASSWORD) or die(mysqlerror());

        // Select database
        $db = mysql_select_db(DB_DATABASE) or die(mysqlerror());

        // return cursor
        return $con;
    }

    function close()
    {
        mysql_close();
    }
}

?>
