<html>
<head>
 <title> Hi man! </title>
</head>
<body>

<?php 
//echo '<p>himan</p>';
$host = '';
$sqluser = '';
$sqlpasswd = '';     // egw to ebala to pass
$dbname = '';

$error = false;
$error_message = "";
$link = mysql_connect($host, $sqluser, $sqlpasswd) or die(mysql_error());
$datefrom= $_POST['FROM_DATE']; 
$dateto= $_POST['TO_DATE'];
echo $datefrom;

$arrderivatives = array();
mysql_select_db($dbname) or die(mysql_error());
$selectquery = "select avg_ratio1,avg_ratio2,sum_total_count,avg_time from averages where avg_time >='$datefrom' and avg_time < '$dateto'";
echo $selectquery;
			$data = mysql_query($selectquery) or die(myql_error());
echo $data;
Print "<table border cellpadding=3>"; 
while($info = mysql_fetch_array( $data )) 
{ 
$arrderivatives[] = $info;
Print "<tr>"; 
Print "<th>RATIO 1:</th> <td>".$info['avg_ratio1'] . "</td> ";
Print "<th>RATIO 1:</th> <td>".$info['avg_ratio2'] . "</td> "; 
Print "<th>INCIDENTS:</th> <td>".$info['sum_total_count'] . "</td> ";  
Print "<th>DATETIME:</th> <td>".$info['avg_time'] . " </td></tr>"; 
} 
echo '{"derivatives":'.json_encode($arrderivatives).'}';
//echo '{"dstport":'.json_encode($arrdstport).'}';
Print "</table>"; 
//select avg_ratio1,avg_ratio2,sum_total_count,avg_time from averages limit 1;
//select  dstport, count(dstport) as top from packets where datetime >='$datefrom' and datetime < '$dateto' group by dstport order by top desc limit 5
 ?> 
 
 
 </body>
</html> 

