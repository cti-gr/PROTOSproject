<?php

class ProtosDataCollector
{
  const DaySeconds = 86400;
  const MonthSeconds = 2592000;
  const WeekSeconds = 604800;
  const thirtySeconds = 30;
  const hourSeconds = 3600;
  const yearSeconds = 31536000;
 

  private $link = False;
  private $interval = self::DaySeconds;
  
  function __construct() {
    include 'config.php';
    $this->link = mysqli_connect($host, $sqluser, $sqlpasswd);
    if (!$this->link)
      throw new Exception("Error connecting to database: ". mysqli_error());
    $ret = mysqli_select_db($this->link,$dbname);
    if (!$ret)
      throw new Exception("Error selecting database: ". mysqli_error());
  } 
 
  function __destruct(){
    if ($this->link)
      mysqli_close($this->link);
  }

 function collect_live_sensors($granularity){
	date_default_timezone_set('EET');

    $start_time = new DateTime();
    $end_time = new DateTime();
    $period_back = 0;
    switch($granularity) {
        case 'Day':
            $period_back = self::DaySeconds;
            break;
        case 'Week':
            $period_back = self::WeekSeconds;
            break;
        case 'Month':
            $period_back = self::MonthSeconds;
            break;
        case 'Live':
            $period_back = self::thirtySeconds;
                        break;
                case 'Init':
                        $period_back = self::hourSeconds;
                        break;
        default:
            /* No valid timeperiod */
            throw new Exception("No valid granularity param");
    }
    $end_time->modify("-".$period_back." seconds");

    if (!$this->link)
      throw new Exception("Not connected to database");
    $link = $this->link;

	$query = sprintf('SELECT distinct(rt_public_ip) as sensor_ip
                                   FROM ratio
                                   WHERE rt_entry_time>="%s" AND rt_entry_time<="%s" ',$end_time->format("Y-m-d H:i:s"),$start_time->format("Y-m-d H:i:s"));
	$rs = mysqli_query($link, $query, MYSQLI_USE_RESULT);
    if(!$rs) {
        throw new Exception("Unable to execute query");
    }

    while ($row = $rs->fetch_assoc() ) {
        $sensorIPs[] = $row['sensor_ip'];
    }

  //  $r1array = substr_replace($r1array ,"]",-1);
   // $r2array = substr_replace($r2array ,"]",-1);
    $arrayTest = Array("sensors"=>$sensorIPs);
    //echo json_encode($arrayTest);
    return json_encode($arrayTest);
 } 


  function collect_data($granularity) {
    date_default_timezone_set('EET');

    $start_time = new DateTime();
    $end_time = new DateTime();
    $period_back = 0;
    switch($granularity) {
        case 'Day':
            $period_back = self::DaySeconds;
            break;
        case 'Week':
            $period_back = self::WeekSeconds;
            break;
        case 'Month':
            $period_back = self::MonthSeconds;
            break;
        case 'Live':
            $period_back = self::thirtySeconds;
			break;
		case 'Init':
			$period_back = self::hourSeconds;
			break;
        default:
            /* No valid timeperiod */
            throw new Exception("No valid granularity param");
    }
    $end_time->modify("-".$period_back." seconds");

    if (!$this->link)
      throw new Exception("Not connected to database");
    $link = $this->link;


    //$stmt = $link->prepare('SELECT avg_ratio1 as rate_one, avg_ratio2 as rate_two, avg_time as entry_time
    //                               FROM averages
    //                               WHERE avg_time>= ? AND avg_time<= ? ');
    $query = sprintf('SELECT avg_ratio1 as rate_one, avg_ratio2 as rate_two, avg_time as entry_time, sum_total_count as sum_total, clients as clients
                                   FROM averages
                                   WHERE avg_time>="%s" AND avg_time<="%s" ',$end_time->format("Y-m-d H:i:s"),$start_time->format("Y-m-d H:i:s"));
  // echo $query;
//error_log($query, 0);
   $rs = mysqli_query($link, $query, MYSQLI_USE_RESULT);
    if(!$rs) {
        throw new Exception("Unable to execute query");
    }

    while ($row = $rs->fetch_assoc() ) {
        $timeseries[] = $row['entry_time'];
        $r1array[]= array(strtotime($row['entry_time'])*1000,$row['rate_one']);
        $r2array[]= array(strtotime($row['entry_time'])*1000,$row['rate_two']);
	$sumTotal[] =  array(strtotime($row['entry_time'])*1000,$row['sum_total']);
	$clients[] = array(strtotime($row['entry_time'])*1000,$row['clients']);
    }

  //  $r1array = substr_replace($r1array ,"]",-1);
   // $r2array = substr_replace($r2array ,"]",-1);
    $arrayTest = Array("rate1"=>$r1array,"rate2"=>$r2array,"timeseries"=>$timeseries,"sumTotal"=>$sumTotal,"clients"=>$clients);
    //echo json_encode($arrayTest);
    return json_encode($arrayTest);
  // return array($r1array, $r2array, $timeseries);   
  }

  function collect_top_data_by_time($granularity, $top){
	  date_default_timezone_set('EET');
 include 'protocolDictionary.php';	  
	$start_time = new DateTime();
    $end_time = new DateTime();
    $period_back = 0;
    switch($granularity) {
        case 'Day':
            $period_back = self::DaySeconds;
            break;
        case 'Week':
            $period_back = self::WeekSeconds;
            break;
        case 'Month':
            $period_back = self::MonthSeconds;
            break;
        case 'Year':
            $period_back = self::yearSeconds;
            break;
        case 'Live':
            $period_back = self::thirtySeconds;
			break;
		case 'Init':
			$period_back = self::hourSeconds;
			break;
        default:
            /* No valid timeperiod */
            throw new Exception("No valid granularity param");
    }
    $start_time->modify("-".$period_back." seconds");

    if (!$this->link)
      throw new Exception("Not connected to database");
    $link = $this->link;
	//select count(protocol),protocol from packets where datetime >= '2015-05-02 00:00:00' and datetime<= now() group by protocol;
	//select count(dstport) as cou,dstport from packets where datetime >= '2015-05-02 00:00:00' and datetime<= now() group by dstport order by cou desc limit 10;
	//select count(srcip) as cou,srcip from packets where datetime >= '2015-05-02 00:00:00' and datetime<= now() group by srcip order by cou desc limit 10;
	$query = sprintf('SELECT protocol,count(protocol) AS cou FROM packets WHERE datetime >= "%s" AND datetime<= "%s" AND protocol != "-" GROUP BY protocol;',$start_time->format("Y-m-d H:i:s"),$end_time->format("Y-m-d H:i:s"));
	 
	$rs = mysqli_query($link, $query, MYSQLI_USE_RESULT);
    if(!$rs) {
        throw new Exception("Unable to execute query");
    }

    while ($row = $rs->fetch_assoc() ) {
        $label = '-';
	try{
		if(isset($dictionary[$row['protocol']])){
			$label = $dictionary[$row['protocol']];
		}
		else{
                	$label = $row['protocol'];
		}
        }
        catch(Exception $e){
		echo "33333333333333333333333333333333333333333333";
        }
        $protocolArray[]= array("label"=>$label,"data"=>$row['cou']);
     }
	
	$query = sprintf('SELECT count(dstport) AS cou,dstport FROM packets WHERE datetime >= "%s" AND datetime<= "%s"  AND dstport != "null" GROUP BY dstport ORDER BY cou DESC LIMIT %s;',$start_time->format("Y-m-d H:i:s"),$end_time->format("Y-m-d H:i:s"),$top);
	 
	$rs = mysqli_query($link, $query, MYSQLI_USE_RESULT);
    if(!$rs) {
        throw new Exception("Unable to execute query");
    }

    while ($row = $rs->fetch_assoc() ) {
        $portArray[]= array("label"=>$row['dstport'],"data"=>$row['cou']);
    }
	
	$query = sprintf('SELECT count(srcip) AS cou,srcip FROM packets WHERE datetime >= "%s" AND datetime<= "%s" AND srcip not in (SELECT lclip_ip FROM localIPs) GROUP BY srcip ORDER BY cou DESC LIMIT %s;',$start_time->format("Y-m-d H:i:s"),$end_time->format("Y-m-d H:i:s"),$top);
	 
	$rs = mysqli_query($link, $query, MYSQLI_USE_RESULT);
    if(!$rs) {
        throw new Exception("Unable to execute query");
    }

    while ($row = $rs->fetch_assoc() ) {
        $ipArray[]= array("label"=>$row['srcip'],"data"=>$row['cou']);
    }

  //  $r1array = substr_replace($r1array ,"]",-1);
   // $r2array = substr_replace($r2array ,"]",-1);
    $arrayTest = Array("protocols"=> $protocolArray, "ports"=> $portArray, "ips"=> $ipArray); //,"rate2"=>$r2array,"timeseries"=>$timeseries,"sumTotal"=>$sumTotal,"clients"=>$clients
    //echo json_encode($arrayTest);
    return json_encode($arrayTest);
  }

      function collect_sensors($granularity){
        date_default_timezone_set('EET');

        $start_time = new DateTime();
        $end_time = new Datetime($start_time->format("Y-m-d H:i:s"));
        $period_back = 0;
        $interval_split = 0;
        switch($granularity) {
                case 'Day':
                $period_back = self::DaySeconds;
                $interval_split = self::hourSeconds;
                break;
               case 'Week':
                   $period_back = self::WeekSeconds;
                   break;
               case 'Month':
                   $period_back = self::MonthSeconds;
				   $interval_split = self::DaySeconds;
                   break;
               case 'Year':
                   $period_back = self::yearSeconds;
                   $interval_split = self::MonthSeconds;
                   break;
              case 'Live':
                  $period_back = self::thirtySeconds;
                  break;
              default:
                /* No valid timeperiod */
            throw new Exception("No valid granularity param");
        }
        $start_time->modify("-".$period_back." seconds");

        $interval = new Datetime($start_time->format("Y-m-d H:i:s"));
        $interval->modify("+".$interval_split." seconds");
    //  echo "START::".$start_time->format("Y-m-d H:i:s")."\n";
    //  echo "End::".$end_time->format("Y-m-d H:i:s")."\n";
        $counter = 1;
    //  echo "Interval ".$counter." ".$interval->format("Y-m-d H:i:s")."\n";

        if (!$this->link)
                throw new Exception("Not connected to database");
                $link = $this->link;
        while($interval < $end_time){
                $query = sprintf('SELECT count(distinct(rt_client_id)) as clients,"%s" as time from ratio 
                                   WHERE rt_entry_time>="%s" AND rt_entry_time<"%s" ',$interval->format("Y-m-d H:i:s"),$start_time->format("Y-m-d H:i:s"),$interval->format("Y-m-d H:i:s"));
//      echo $query."\n";
                $rs = mysqli_query($link, $query, MYSQLI_USE_RESULT);
                if(!$rs) {
                        throw new Exception("Unable to execute query");
                }

                        while ($row = $rs->fetch_assoc()) {
                        $timeseries[] = array(strtotime($row['time'])*1000,$row['clients']);
                }
                $counter++;
		$interval->modify("+".$interval_split." seconds");
                $start_time->modify("+".$interval_split." seconds");
        }
                $arrayToSend = Array($granularity=>$timeseries);
                return json_encode($arrayToSend);
               // print_r($clients);
    }

}
