system = require('system')     
address = system.args[1];

//console.log('Loading a web page');     
var page = require('webpage').create();     
var url = address;
//console.log(url);     
page.open(url, function (status) {     
    //Page is loaded!  
	
		if (status !== 'success') {     
			console.log('Unable to post!');     
		}   
    
	 
});  
page.onLoadFinished = function(status) {
  console.log('Status: ' + status);
  // Do other things here...
  var t=setTimeout(function(){
  		phantom.exit();  
  },20000)
};