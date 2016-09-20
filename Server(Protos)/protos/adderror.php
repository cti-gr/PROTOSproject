<?php
$host = '';
$sqluser = '';
$sqlpasswd = '';     // egw to ebala to pass
$dbname = '';

$error = false;
$error_message = "";
$link = mysql_connect($host, $sqluser, $sqlpasswd);

if ($link != FALSE) {
    // proceed, seems that we are connected
    mysql_select_db($dbname);
    if ( isset ($_POST['jsonData']) ) {
        $json_dataAll = json_decode(stripslashes($_POST['jsonData']), true);
        if($json_dataAll != NULL){        
	   $client_id = $json_dataAll['uuid'];
	   $error_msg = $json_dataAll['error'];
	   $query = sprintf("insert into error(er_client_id, er_entry_time, er_error_msg) values('%s', now(), '%s')",
                             $client_id,
                             $error_msg);
            //echo $query;
            if (!mysql_query($query, $link)) {
                $error = true;
                $error_message = "insert statement failed";
            }
        } 
        else {
             $error = true;
             $error_message = "Malformed JSON packet data ";
        }      
    } 
    else {
        $error = true;
        $error_message = "Could not find the JSON packet data";
    }

    // close the database
    mysql_close($link);
} else { 
    $error = true;
    $error_message = "Could not connect to database";
}
if ($error) {
    //header("HTTP/1.0 404 Not Found");
}
?>
 <html>
    <body>
        <?php if ($error) { ?>
        <h2>Could not insert data in packets</h2>
        Reason: <?php echo $error_message ?>
        <?php } else { ?>
        <h2>Success packets added!!</h2>
        <?php } ?>
    </body>
</html>   
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
