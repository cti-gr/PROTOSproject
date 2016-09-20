<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
PROTOS Top 5 Plotting (Weekly)
</title>
    <LINK REL=StyleSheet HREF="css/layout.css" TYPE="text/css">
    <!--[if IE]><script language="javascript" type="text/javascript" src="js/excanvas.min.js"></script><![endif]-->
    <script type="text/javascript" src="jsNew/jquery.min.js"></script>
    <script type="text/javascript" src="jsNew/jquery.flot.min.js"></script>
	<script type="text/javascript" src="jsNew/jquery.flot.pie.min.js"></script>

   <script type="text/javascript">
   
   function labelFormatter(label, series) {
		return "<div style='font-size:8pt; text-align:center; padding:2px; color:white;'>" + Math.round(series.percent) + "%</div>";
	}

	function plotTopStats(dataToPlot){
		$.plot('#graphdivW1', dataToPlot.protocols, {
			series: {
				pie: {
					show: true,
					radius: 1,
					label: {
						show: true,
						radius: 2/3,
						formatter: labelFormatter,
						threshold: 0.1
					}			
				}
			},
			legend: {
				show: true
			}
		});
		$.plot('#graphdivW3', dataToPlot.ports, {
			series: {
				pie: {
					innerRadius: 0.5,
					show: true,
					radius: 1,
					label: {
						show: true,
						radius: 2/3,
						formatter: labelFormatter,
						threshold: 0.1
					}			
				}
			},
			legend: {
				show: true
			}
		});
		$.plot('#graphdivW2', dataToPlot.ips, {
			series: {
				pie: {
					show: true,
					radius: 1,
					label: {
						show: true,
						radius: 2/3,
						formatter: labelFormatter,
						threshold: 0.1
					}			
				}
			},
			legend: {
				show: true
			}
		});
	}	


$(document).ready(
        function () {
			$(document).ajaxStart(function () {
				$("body").addClass("loading");
			}).ajaxStop(function () {
				$("body").removeClass("loading");
    });
	
	function fetchTops(){
		function onTopDataReceived(tops){
			plotTopStats(tops);
		}
		$.getJSON("/promis/returnTopData.php?granularity=Week&top=5",onTopDataReceived);
	}	
	fetchTops();
    }
);

</script>
</head>
<body>
<table>
<tr><td>
<h1>PROactive Threat Observatory System - Top 5 Plotting (Weekly)</h1>
<h2>Top 5 Protocols</h2>
<div id="graphdivW1" class="graph"></div>
<h2>Top 5 Ports</h2>
<div id="graphdivW3" class="graph"></div>
<h2>Top 5 IPs</h2>
<div id="graphdivW2" class="graph"></div>
</td>
</tr>
</table>
 <div class="modal"><!-- Place at bottom of page --></div>
</body>
</html>
