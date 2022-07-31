<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/results.css">
  <script src="https://code.jquery.com/jquery-1.11.3.min.js"></script>
  <!-- script src="https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"> </script-->
  <!-- link rel="stylesheet" href="https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css" -->
  <!-- script type="text/javascript" src="lib/jquery-3.2.1.js"></script -->
  <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.js"></script> 
  <!-- script src="http://code.jquery.com/jquery-latest.js"> </script -->
  
  
  <meta name="viewport" content="width=device-width, initial-scale=1">  
   
 <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
 
  <!-- script type="text/javascript" src="http://code.jquery.com/jquery-1.10.2.js"> </script -->
  <script type="text/javascript" src="http://code.jquery.com/ui/1.12.1/jquery-ui.js"> </script>
  <script src="//cdnjs.cloudflare.com/ajax/libs/json3/3.3.2/json3.min.js"></script>
  <link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css" />
  <!--link rel="stylesheet" href="css/styl1.css"-->
  <!-- meta name="viewport" content="width=device-width, initial-scale=1" -->
  <!-- meta name="viewport" content="width=device-width, initial-scale=1" -->
  
  
  <!-- Those two lines must go here; otherwise, some collapse things don't work -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">  
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="lib/jquery-3.2.1.js"></script>

  <style>
        .ui-autocomplete { 
            cursor:pointer; 
            height:200px; 
            overflow-y:scroll;
            font-size: small;
            color: black;
        }    
    </style>
    
 <script src="https://www.gstatic.com/charts/loader.js"></script>

<script type="text/javascript">

/*
Cancel escape single quotes
*/
function cancelEscapeSingleQuotes(str){
	  //return str.replace('\\\'', /'/g);	 
	  return str.replace(/\\'/g,"'");
}

    
google.charts.load('current', {packages: ['corechart']});
google.charts.setOnLoadCallback(drawChart);


var chart = new google.visualization.ChartWrapper({
	  chartType: 'BarChart',  // <-- chart type property
	  containerId: 'chart',
	  dataTable: data,
	  options: {		  
		  annotations: { textStyle: {
			  fontName: 'Times-Roman',
			  fontSize: 6,
			  bold: false,
			  italic: false,
			  color: '#871b47',     // The color of the text.
			  auraColor: '#d799ae', // The color of the text outline.
			  opacity: 0.8          // The transparency of the text.
			}, style: 'line', hAxis: {direction:-1, slantedText:true, slantedTextAngle:45 }},
		hAxis: {chartArea: {left:100, width: 400} , direction:-1, slantedText:true, slantedTextAngle:45 }//, 
		//height: 1000,
	    //theme: 'maximized'
	  }
	});

google.charts.load('current', {
	  callback: drawChart,
	  packages: ['controls', 'corechart']
	});

 drawChart();
 
 var jsonForVisualization;
 

	function drawChart() {
		
	
	  var arr = JSON.parse(cancelEscapeSingleQuotes(document.getElementById("chartData").innerHTML));
	  
	  var data = new google.visualization.DataTable(arr);
			  /*[
	    ['x', 'y0', 'r'],
	    ['a', 898, 122],
	    ['b', 37319, 324],
	    ['c', 8980, 32],
	    ['d', 35400, 233]
	  ]*/
			 
	  	  	  
	  var chartType = document.getElementById('chart-type');
	  var chartWrapper = new google.visualization.ChartWrapper({
	    chartType: chartType.value,
	    containerId: 'chart',
	    dataTable: data,
	    width: '100%',
	    options: {	 
	    	 chartArea: {
	    		 width: '100%',
	    		 top: 10,
	    	     height: '60%' 
	    	    },
	    	annotations: { textStyle: {
				  fontName: 'Times-Roman',
				  fontSize: 6,
				  bold: false,
				  italic: false,
				  color: '#871b47',     // The color of the text.
				  //auraColor: '#d799ae', // The color of the text outline.
				  opacity: 0.8          // The transparency of the text.
				}, style: 'line', hAxis: {direction:-1, slantedText:true, slantedTextAngle:30 }},
	      hAxis: {chartArea: {left:200, width: 400}, direction:-1, slantedText:true, slantedTextAngle:40 }//,
	      //height: '1000px'
	    }
	  });
	  chartType.addEventListener('change', drawChartWrapper, false);
	  drawChartWrapper();

	  function drawChartWrapper() {
	    chartWrapper.setChartType(chartType.value);
	    chartWrapper.draw();
	  }
	}
   
  	

</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title> Query Results</title>


</head>
<body>

 <div id="resultsChart" class="tabcontentForGraphOrResult">
       
       <div align="center">
       	
    	<form action="ManipulateAnalysisGraphs" method="post">
    	    
		   
					
				   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b> Chart type  </b>
				  <select id="chart-type">		   
				    <option value="BarChart">Bar</option>
				    <option value="ColumnChart">Column</option>
				    <option value="LineChart">Line</option>
				    <option value="Table" selected>Table</option>
				    <option value="AnnotatedTimeLine">AnnotatedTimeLine</option>
				    <option value="TreeMap">TreeMap</option>
				    <option value="LineChart">LineChart</option>
				    <option value="AreaChart">AreaChart</option>
				    <option value="ComboChart">ComboChart</option>
				    <option value="ImageChart">ImageChart</option>
				    <option value="ImageBarChart">ImageBarChart</option>
				    <option value="ImageAreaChart">ImageAreaChart</option>
				    <option value="ImageSparkLine">ImageSparkLine</option>		    
				    <option value="PieChart">Pie</option>
				    <option value="ScatterChart">Scatter</option>
				    <option value="GeoChart">Geo</option>
				    <option value="Histogram">Histogram</option>
				    <option value="BubbleChart">Bubble</option>
				    <option value="Gauge">Gauge</option>
				    <option value="Timeline">Timeline</option>
				    <option value="Filters">Filters</option>
				    <option value="MotionChart">Motion</option>
				    <option value="TermCloud">TermCloud </option>		   
				  </select>
		
		</form>

		<div id="chart" align="center" 
		style="width: 100%; border-style: solid; border-color: #499dab; height: 800px; display: inline-block; margin: 0 auto !important;; ">
		</div>
		<div id="chartData" style="display:none"></div>
		</div>
       </div>

<script> 
function doSomething(){
	alert(document.getElementById("chart").innerHTML);
}
</script>
</body>
</html>