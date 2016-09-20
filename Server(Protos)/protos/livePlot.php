<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
PROTOS Live Plotting
</title>
    <LINK REL=StyleSheet HREF="css/layout.css" TYPE="text/css">
    <!--[if IE]><script language="javascript" type="text/javascript" src="js/excanvas.min.js"></script><![endif]-->
    <script type="text/javascript" src="jsNew/jquery.min.js"></script>
    <script type="text/javascript" src="jsNew/jquery.flot.min.js"></script>
	 <script type="text/javascript" src="jsNew/jquery.flot.time.min.js"></script>

   <script type="text/javascript">

function plotDataLive(dataToPlot) {

   //  minDate=splitDate(dataToPlot.timeseries[0]);
   //     maxDate=splitDate(dataToPlot.timeseries[dataToPlot.timeseries.length-1]);
   //alert(dataToPlot.rate2[55]);
        $("#clients").html(dataToPlot.currentClients[dataToPlot.currentClients.length-1][1]);
	$.plot($("#graphdiv"), [
                              {
                                label: "Malicious Activity",
                                        data: dataToPlot.rate1,
                                        points: { show: false },
                                        lines: { show: true, lineWidth: 3.5 },
                                        color:"rgb(255,0,0)"
                                  }
                              ], {xaxis:
                                        {
                                        mode: "time"
                                                }
                                }
                );
          $.plot($("#graphdiv3"), [
                              {
                                label: "Epidemic Rate",
                                        data: dataToPlot.rate2,
                                        points: { show: false },
                                        lines: { show: true, lineWidth: 3.5},
                                        color:"rgba(100, 50, 61,1)"
                                  }
                              ], {xaxis:
                                        {
                                        mode: "time"
                                                }
                                }
                );

         $.plot($("#graphdiv2"), [
                              {
                                label: "Number of Incidents",
                                        data: dataToPlot.sumTotal,
                                        points: { show: false },
                                        lines: { show: true, lineWidth: 3.5 },
                                        color:"rgba(255, 265, 0,1)"
                                  }
                              ], {xaxis:
                                        {
                                                mode: "time"
                                                }
                                }
                );
				$.plot($("#graphdiv4"), [
                              {
                                label: "Number of Sensors",
                                        data: dataToPlot.currentClients,
                                        points: { show: false },
                                        lines: { show: true, lineWidth: 3.5 },
                                        color:"rgba(0, 0, 0, 1)"
										
                                  }
								  
                              ], {xaxis:
                                        {
                                                mode: "time"
                                                }
                                }
                );		
    }
	function plotSensorStats(dataToPlot, granularity){
		if(granularity=="Day"){
		 $.plot($("#graphdiv5"), [
                              {
                                label: "Sensors per hour",
                                        data: dataToPlot,
                                        points: { show: true },
                                        lines: { show: true, lineWidth: 3.5 },
                                        color:"rgba(0, 128, 0, 1)"
                                  }
                              ], {xaxis:
                                        {
                                        mode: "time"
                                                }
                                }
                );
		} else if(granularity=="Month"){
                 $.plot($("#graphdiv6"), [
                              {
                                label: "Sensors per day",
                                        data: dataToPlot,
                                        points: { show: true },
                                        lines: { show: true, lineWidth: 3.5 },
                                         color:"rgb(0,0,255)"
                                  }
                              ], {xaxis:
                                        {
                                        mode: "time"
                                                }
                                }
                );
                }
		else if(granularity=="Year"){
                 $.plot($("#graphdiv7"), [
                              {
                                label: "Sensors per month",
                                        data: dataToPlot,
                                        points: { show: true },
                                        lines: { show: true, lineWidth: 3.5 },
                                        color:"rgba(233, 136, 75, 1)"
                                  }
                              ], {xaxis:
                                        {
                                        mode: "time"
                                                }
                                }
                );
                }  
	}	

    function splitDate(dateToSplit){
                var firstSplit=dateToSplit.split(" ");
                var dateSplit=firstSplit[0].split("-");
                var timeSplit=firstSplit[1].split(":");
                dateSplit[1]=(parseInt(dateSplit[1])-1).toString();
                var returnDateTime=dateSplit.concat(timeSplit);
                return returnDateTime;
        }


$(document).ready(
        function () {
          var data={"rate1":[],"rate2":[],"timeseries":[],"sumTotal":[],"currentClients":[]};

          var maxPace1=0;
          var minPace1=0;
          var maxPace2=0;
          var minPace2=0;

        //alert("eee");
		function onDataReceived(series) {
			//$('clients').append(series.rate1);
			//alert(series.rate1);
			//alert(maxPace1+" "+minPace1+" "+maxPace2+" "+minPace2);
			if (Number(maxPace1) <=Number(series.rate1[0][1])){
					maxPace1=series.rate1[0][1];
			}
			if (Number(minPace1) > Number(series.rate1[0][1])){
					minPace1=series.rate1[0][1];
			}
			if (Number(maxPace2) <= Number(series.rate2[0][1])){
					maxPace2=series.rate2[0][1];
			}
			if (Number(minPace2) > Number(series.rate2[0][1])){
			//      x=minPace2+1;
					//alert("eeeee"+minPace2+"ffff"+series.rate2[0][1]);
					minPace2=series.rate2[0][1];
			}
			//alert(maxPace1+" "+minPace1+" "+maxPace2+" "+minPace2+"\n"+series.rate1[0][1]+" "+series.rate2[0][1]);

			$("#max1").html(maxPace1);
			$("#min1").html(minPace1);
			$("#max2").html(maxPace2);
			$("#min2").html(minPace2);

			var localTimeZone=(((new Date()).getTimezoneOffset())*60000)*-1;
			  // alert(series.rate1[0][0]+"   "+localTimeZone);
			for(rate1_key in series.rate1){
					series.rate1[rate1_key][0]=series.rate1[rate1_key][0]+localTimeZone;
			}
			for(rate2_key in series.rate2){
					series.rate2[rate2_key][0]=series.rate2[rate2_key][0]+localTimeZone;
			}
			for(sum_key in series.sumTotal){
					series.sumTotal[sum_key][0]=series.sumTotal[sum_key][0]+localTimeZone;
			}
			for(client_key in series.clients){
					series.clients[client_key][0]=series.clients[client_key][0]+localTimeZone;
			}
			data.rate1=data.rate1.concat(series.rate1);
			data.rate2=data.rate2.concat(series.rate2);
			data.timeseries=data.timeseries.concat(series.timeseries);
			data.sumTotal=data.sumTotal.concat(series.sumTotal);
			data.currentClients=data.currentClients.concat(series.clients);
			plotDataLive(data);
        }
		function fillFirstData() {
			$.getJSON("/promis/returnFirstData.php",onDataReceived);
		}
        function fetchData() {
            $.getJSON("/promis/returnLiveData.php",onDataReceived);
			setTimeout(fetchData, 32000);
        }
	
	function fetchSensorStats(){
		function onSensorDataReceived(series){
			var data={"clients":[]};
			var localTimeZone=(((new Date()).getTimezoneOffset())*60000)*-1;
			if(Object.keys(series)[0]=='Day'){
				for(client_key in series.Day){
                                	series.Day[client_key][0]=series.Day[client_key][0]+localTimeZone;
                        	}
				plotSensorStats(series.Day,"Day");
			}
			else if(Object.keys(series)[0]=='Month'){
                                for(client_key in series.Month){
                                        series.Month[client_key][0]=series.Month[client_key][0]+localTimeZone;
                                }
                                plotSensorStats(series.Month,"Month");                        
			}
			else if(Object.keys(series)[0]=='Year'){
                                for(client_key in series.year){
                                        series.Year[client_key][0]=series.Year[client_key][0]+localTimeZone;
                                }
                                plotSensorStats(series.Year,"Year");        
                        }
		}
		$.getJSON("/promis/returnSensorStats.php?granularity=Day",onSensorDataReceived);
		$.getJSON("/promis/returnSensorStats.php?granularity=Month",onSensorDataReceived);
		$.getJSON("/promis/returnSensorStats.php?granularity=Year",onSensorDataReceived);
	}	
    fillFirstData();
	fetchSensorStats();
	setTimeout(fetchData,32000);
    }
);

</script>
</head>
<body>
<table>
<tr><td>
<h1>PROactive Threat Observatory System</h1>
<h2>Time trends of attack incidents</h2>
<div id="graphdiv" class="graph"></div>
<div id="graphdiv3" class="graph"></div>
<div id="graphdiv2" class="graph2"></div>
<div id="graphdiv4" class="graph2"></div>
<div id="graphdiv5" class="graph2"></div>
<div id="graphdiv6" class="graph2"></div>
<div id="graphdiv7" class="graph2"></div>
</td></tr>
<tr>
<td valign="top">
        <table>
        <tr><td>
        <h3>Active Client(s):</h3><td><h3><div id="clients" class="clients"></div></h3>
        </tr>
        <tr><td>
        <h3>Max Malware Activity:</h3><td><h3><div id="max1" class="clients"></div></h3>
        <td>
        <h3>Min Malware Activity:</h3><td><h3><div id="min1" class="clients"></div></h3>
        </tr>
        <tr><td>
        <h3>Max Epidemic Rate:</h3><td><h3><div id="max2" class="clients"></div></h3>
        <td>
        <h3>Min Epidemic Rate:</h3><td><h3><div id="min2" class="clients"></div></h3>
        </tr>
        </table>
</td>
</tr>
</table>
</body>
</html>
