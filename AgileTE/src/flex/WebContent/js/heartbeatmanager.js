function GetRequest() {
   var url = location.search; 
   var theRequest = new Object();
   if (url.indexOf("?") != -1) {
      var str = url.substr(1);
      strs = str.split("&");
      for(var i = 0; i < strs.length; i ++) {
         theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
      }
   }
   return theRequest;
}
	
var myRequest = new Object();
myRequest = GetRequest();
var fatherurl = myRequest['fatherurl'];

var jumpurl = fatherurl;
var localpagehost = ((window.location.protocol == "https:") ? "https://" : "http://"); 
var localhost = window.location.host
var hearturl = localpagehost + localhost + '/AgileSysM/agile/agilesysm/socketStatus';
var serStatus = localpagehost + localhost +'/AgileSysM/agile/agilesysm/serverStatus2';


  
var heartAdd = "";
var interval= null;

function ajaxRun(obj){
	heartAdd = obj;
	interval = setInterval(heart,"5000"); 
}

function heart(){
					$.ajax({
				     type: "GET",
				     async:false, 
				     contentType: "application/xml",
				     url: serStatus+'?value='+new Date().toString(),
				     timeout: 10000,
				     dataType: "jsonp",
					 jsonpCallback: "call",					     
				     success: function(result) {
						var res = result.content;
						if(res=="spare"){
							alert("The server you vist is not allowable!");
							window.location.href = jumpurl;
						}
				      },
				     error:function(){
					
				     }
				  });
	
	$.ajax({
	     type: "GET",
	     async:false, 
	     contentType: "application/xml",
	     url: heartAdd +'?value='+new Date().toString(),
	     timeout: 10000,
		 dataType: "jsonp",
	     jsonpCallback: "call",
	     success: function(result) {
	         
	    
	      },
	     error:function(){
	    	 clearInterval(interval);
	    	 alert('The url request lost . jump to main url. ');
	    	 window.location.href = jumpurl;
	     }
	  });
}
        
$(function() {

	if ("undefined" != typeof fatherurl && fatherurl != '')
	{
	      setTimeout(function() {
	          ajaxRun(hearturl);
	      }, 0)
	}

});