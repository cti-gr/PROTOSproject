<?php

include 'ProtosDataCollector.php';

try {
  $granularity = $_GET["granularity"];
  $top = $_GET["top"];
  $obj = new ProtosDataCollector();
  // Get the data of the last day
  $data_live = $obj -> collect_top_data_by_time($granularity, $top);
  //Get the data of the last week
 
} catch (Exception $e) {
  die("<h3>An error occured: ". $e -> getMessage() . "</h3>");  
}

echo $data_live;
?>
