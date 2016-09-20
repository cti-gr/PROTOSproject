
var IPMapper = {
	map: null,
	mapTypeId: google.maps.MapTypeId.HYBRID,
	latlngbound: null,
	infowindow: null,
	baseUrl: "http://freegeoip.net/json/",
	initializeMap: function(mapId){
		IPMapper.latlngbound = new google.maps.LatLngBounds();
		var latlng = new google.maps.LatLng(0, 0);
		//set Map options
		var mapOptions = {
			zoom: 1,
			center: latlng,
			mapTypeId: IPMapper.mapTypeId
		}
		//init Map
		IPMapper.map = new google.maps.Map(document.getElementById(mapId), mapOptions);
		//init info window
		IPMapper.infowindow = new google.maps.InfoWindow();
		//info window close event
		google.maps.event.addListener(IPMapper.infowindow, 'closeclick', function() {
			IPMapper.map.fitBounds(IPMapper.latlngbound);
			IPMapper.map.panToBounds(IPMapper.latlngbound);
		});
	},
    addBlueIPArray: function(ipArray){
		ipArray = IPMapper.uniqueArray(ipArray); 
		
		for (var i = 0; i < ipArray.length; i++){
			
            IPMapper.addIPMarker(ipArray[i], blue);
            
		}
	},

	addIPArray: function(ipArray){
		ipArray = IPMapper.uniqueArray(ipArray); 
		
		for (var i = 0; i < ipArray.length; i++){
			IPMapper.addIPMarker(ipArray[i]);
		}
	},
	addIPMarker: function(ip){
		ipRegex = /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/;
		if($.trim(ip) != '' && ipRegex.test(ip)){ 
			var url = encodeURI(IPMapper.baseUrl + ip + "?callback=?");
			$.getJSON(url, function(data) { 
				if($.trim(data.latitude) != '' && data.latitude != '0' && !isNaN(data.latitude)){ 
					var latitude = data.latitude;
					var longitude = data.longitude;
					var contentString = "";
					$.each(data, function(key, val) {
						contentString += '<b>' + key.toUpperCase().replace("_", " ") + ':</b> ' + val + '<br />';
					});
					var latlng = new google.maps.LatLng(latitude, longitude);
                   
					
					var bluemarker = new google.maps.Marker({ 
						map: IPMapper.map,
						draggable: true,
						position: latlng ,
						icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
						
					});
                                       
                                    
                  
                    var marker = new google.maps.Marker({ 
						map: IPMapper.map,
						draggable: false,
						position: latlng
						
					});
                    
					IPMapper.placeIPMarker(marker, latlng, contentString); 
				} else {
					IPMapper.logError('PROTOS SpotMaps failed!');
					$.error('PROTOS SpotMaps failed!');
				}
			});
		} else {
			IPMapper.logError('Invalid IP Address!');
			$.error('Invalid IP Address!');
		}
	},
	placeIPMarker: function(marker, latlng, contentString){ 
		marker.setPosition(latlng);
		google.maps.event.addListener(marker, 'click', function() {
			IPMapper.getIPInfoWindowEvent(marker, contentString);
		});
		IPMapper.latlngbound.extend(latlng);
		IPMapper.map.setCenter(IPMapper.latlngbound.getCenter());
		IPMapper.map.fitBounds(IPMapper.latlngbound);
	},
	getIPInfoWindowEvent: function(marker, contentString){ 
		IPMapper.infowindow.close()
		IPMapper.infowindow.setContent(contentString);
		IPMapper.infowindow.open(IPMapper.map, marker);
	},
	uniqueArray: function(inputArray){ 
		var a = [];
		for(var i=0; i<inputArray.length; i++) {
			for(var j=i+1; j<inputArray.length; j++) {
				if (inputArray[i] === inputArray[j]) j = ++i;
			}
			a.push(inputArray[i]);
		}
		return a;
	},
	logError: function(error){
		if (typeof console == 'object') { console.error(error); }
	}
}
