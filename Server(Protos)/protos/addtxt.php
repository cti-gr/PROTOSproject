<?php
$msg = 'hi';
echo $msg;
print_r($_FILES);
print_r($_POST);
//if ($_FILES["file"]["error"] > 0)
 // {
 // echo "Error: " . $_FILES["file"]["error"] . "<br>";
  //}
//else
  //{
 // echo readfile($path);

//print_r  $_FILES;/
//print_r $_POST;
//var_dump($_FILES["file"]);
 // echo "Upload: " . $_FILES["file"]["name"] . "<br>";
 // echo "Type: " . $_FILES["file"]["type"] . "<br>";
 // echo "Size: " . ($_FILES["file"]["size"] / 1024) . " kB<br>";
  //echo "Stored in: " . $_FILES["file"]["tmp_name"];
 $path =  $_FILES["js"]["tmp_name"];
 $ffile = file_get_contents($path);
echo $ffile;
// echo " \n skata \n \n " ;
// echo $path;

//readfile($path);
// $ffile = fopen($path, "r") or die("can't open file");
// $members = array();

 //while (!feof($file)) {
 // $members[] = fgets($file);
//}
///
//fclose($ffile);

//var_dump($members);
      
  
  
 //}
?> 
