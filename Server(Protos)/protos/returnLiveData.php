<?php

include 'ProtosDataCollector.php';

try {
  $obj = new ProtosDataCollector();
  // Get the data of the last day
  $data_live = $obj -> collect_data('Live');
  //Get the data of the last week
 
} catch (Exception $e) {
  die("<h3>An error occured: ". $e -> getMessage() . "</h3>");  
}

echo $data_live;
?>
