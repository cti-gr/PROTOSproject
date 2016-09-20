<?php
$host = '';
$sqluser = '';
$sqlpasswd = '';     
$dbname = '';

$error = false;
$error_message = "";

// insert into ratio(rt_client_id, rt_entry_time, rt_rate_one, rt_rate_two, rt_total_count, rt_local_ip) values('skdskdjskdjksdjs', now(), 0.23, 1000.24, 5000, '127.0.0.1')
if (!empty($_SERVER['HTTP_CLIENT_IP'])) {
    $ip = $_SERVER['HTTP_CLIENT_IP'];
} elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR'])) {
    $ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
} else {
    $ip = $_SERVER['REMOTE_ADDR'];
}

$link = mysql_connect($host, $sqluser, $sqlpasswd);

if ($link != FALSE) {
    // proceed, seems that we are connected
    mysql_select_db($dbname);

    if ( isset ($_POST['data']) ) {
        $json_data = json_decode(stripslashes($_POST['data']), true);
        if ( $json_data != NULL ) {
            $rt_client_id = $json_data['clientid'];
            $rt_rate_one = $json_data['rate1'];
            if ($rt_rate_one == NULL) { $rt_rate_one = "NULL"; }
            $rt_rate_two = $json_data['rate2'];
            if ($rt_rate_two == NULL) { $rt_rate_two = "NULL"; }
            $rt_total_count = $json_data['tcount'];
            $rt_local_ip = $json_data['localip'];
			$rt_public_ip = $ip;
            $query = sprintf("insert into ratio(rt_client_id, rt_entry_time, rt_rate_one, rt_rate_two, rt_total_count, rt_local_ip, rt_public_ip) values('%s', now(), %s, %s,  %d, '%s', '%s')",
                             $rt_client_id,
                             $rt_rate_one,
                             $rt_rate_two,
                             $rt_total_count,
                             $rt_local_ip,
                             $rt_public_ip);
           // echo $query;
            if (!mysql_query($query, $link)) {
                $error = true;
                $error_message = "insert statement failed";
            }
        } else {
            $error = true;
            $error_message = "Malformed JSON data ";
		
        }
    } else {
        $error = true;
        $error_message = "Could not find the JSON data";
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
        <h2>Could not insert data</h2>
        Reason: <?php echo $error_message ?>
        <?php } else { ?>
        <h2>Success!!</h2>
        <?php } ?>
    </body>
</html>
