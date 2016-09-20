<?php

include 'ProtosDataCollector.php';

try {
$granularity = $_GET["granularity"];
  $obj = new ProtosDataCollector();
  // Get the data of the last day
  $data_day = $obj -> collect_sensors($granularity);
 
} catch (Exception $e) {
  die("<h3>An error occured: ". $e -> getMessage() . "</h3>");  
}

echo $data_day;
?>
