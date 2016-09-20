<?php
$host = '';
$sqluser = '';
$sqlpasswd = '';     // egw to ebala to pass
$dbname = '';

$error = false;
$error_message = "";
$link = mysqli_connect($host, $sqluser, $sqlpasswd);

if ($link != FALSE) {
    // proceed, seems that we are connected
    mysqli_select_db($link, $dbname);
    if ( isset ($_POST['data']) ) {
        $json_dataAll = json_decode(stripslashes($_POST['data']), true);
        if($json_dataAll != NULL){        
	   $client_id = $json_dataAll['uuid'];
	   $version = $json_dataAll['version'];
	   $find_query = sprintf("select ver_id from client_version where ver_client_id='%s'",$client_id);
           $rs = mysqli_query($link, $find_query);
	   //echo "pos genen ".mysqli_num_rows($rs);
           if($rs && $rs->num_rows > 0 ) {
              $query = sprintf("update client_version set ver_version = '%s', ver_entry_time = now() where ver_client_id = '%s'",
                             $version,
			     $client_id);
	   }
	   else{
	      $query = sprintf("insert into client_version(ver_client_id, ver_entry_time, ver_version) values('%s', now(), '%s')",
                             $client_id,
                             $version);
	   }
	//echo $query;
           if (!mysqli_query($link, $query)) {
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
    mysqli_close($link);
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
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
