<?php
$host = '';
$sqluser = '';
$sqlpasswd = '';     // egw to ebala to pass
$dbname = '';

$error = false;
$error_message = "";

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
    $uuid = $_POST['uuid'];
    if ( isset ($_FILES['js']) ) {
        $temp_data = stripslashes(file_get_contents($_FILES['js']['tmp_name']));
        $json_dataAll = json_decode($temp_data, true);
        if ( $json_dataAll != NULL ) {
            foreach ($json_dataAll as $json_data){
                $datetime = $json_data['datetime'];
                $action = $json_data['action'];
                $protocol = $json_data['protocol'];
                $srcip = $json_data['srcip'];
                $dstip = $json_data['dstip'];
                $srcport = $json_data['srcport'];
                $dstport = $json_data['dstport'];
                $size = $json_data['size'];
                $tcpflags = $json_data['tcpflags'];
                $tcpsyn = $json_data['tcpsyn'];
                $tcpack = $json_data['tcpack'];
                $tcpwin = $json_data['tcpwin'];          
                $icmptype = $json_data['icmptype'];         
                $icmpcode = $json_data['icmpcode'];           
                $info = $json_data['info'];
                $path = $json_data['path'];
                
                $query = sprintf("insert into packets
                                                     (    
                                                          pc_client_id,
                                                          datetime,
                                                          action,
                                                          protocol, 
                                                          srcip,
                                                          dstip,
                                                          srcport,
                                                          dstport,
                                                          size,
                                                          tcpflags,
                                                          tcpsyn,
                                                          tcpack,
                                                          tcpwin,
                                                          icmptype,
                                                          icmpcode,
                                                          info,
                                                          path,
                                                          public_ip) 
                             values('%s', '%s', '%s', '%s',  '%s', '%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s', '%s')",
                                            $uuid,
                                            $datetime,  
                                            $action,  
                                            $protocol, 
                                            $srcip,  
                                            $dstip, 
                                            $srcport, 
                                            $dstport,  
                                            $size, 
                                            $tcpflags, 
                                            $tcpsyn,  
                                            $tcpack,  
                                            $tcpwin, 
                                            $icmptype,  
                                            $icmpcode,  
                                            $info, 
                                            $path,
                                            $ip);
                //echo $query;
                if (!mysql_query($query, $link)) {
                    $error = true;
                    $error_message = "insert statement failed";
                }
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
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
