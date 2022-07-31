<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title> Analysis Graph Selection </title>
 <script src="https://code.jquery.com/jquery-1.11.3.min.js"></script>
<link href="css/styl1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="css/app.css"> 
<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css" />
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">  


<script>

function openTab(evt, cityName) {
    // Declare all variables
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("div");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    
    tabcontent = document.getElementsByClassName("tab");
    for (i = 0; i < tabcontent.length; i++) {
       // tabcontent[i].style.display = "none";
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("icon");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the link that opened the tab   
    document.getElementById(cityName).style.display = "block";  
    document.getElementById(cityName).style.visibility="visible";
    document.getElementById("tab" + cityName).style.display = "block";    
    evt.currentTarget.className += " active";
}

function activateTab(evt, cityName) {
    // Declare all variables
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("div");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    
    tabcontent = document.getElementsByClassName("tab");
    for (i = 0; i < tabcontent.length; i++) {
       //tabcontent[i].style.display = "none";
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("icon");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the link that opened the tab
    document.getElementById(cityName).style.display = "block";    
    document.getElementById("tab" + cityName).style.display = "block";
    //document.getElementById(cityName).style.display = "block";
    evt.currentTarget.className += " active";
    
    document.getElementByClassName("tab").style.display = "block";
}

function changeSelected() {
	var e = document.getElementById('selectAS');		
	var strUser = e.options[e.selectedIndex].value;
	
	toSet = document.getElementById('analysisGraphFileName');
	toSet.value = strUser;
}

function validURL(str) {
	  var pattern = new RegExp('^(https?:\\/\\/)?'+ // protocol
	    '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|'+ // domain name
	    '((\\d{1,3}\\.){3}\\d{1,3}))'+ // OR ip (v4) address
	    '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*'+ // port and path
	    '(\\?[;&a-z\\d%_.~+=-]*)?'+ // query string
	    '(\\#[-a-z\\d_]*)?$','i'); // fragment locator
	  return !!pattern.test(str);
	}

function doSubmit(typ) {
	
	if (typ == 'select'){
		if ($('#analysisGraphFileName').val() != 'none' && $('#analysisGraphFileName').val() != ''){		
			$('#formType').val('selectAnalysisGraphFile');
			$('#FormOfSelectAnalysisGraphFile').submit();
		}else{
			alert('Please select an analysis graph from the list first.');
			return;
		}
	}
	
	if (typ == 'paste'){
		if ($('#pasteAG').val() != '' && validURL($('#pasteAG').val())){			
			$('#formType').val('selectAnalysisGraphPaste');
			$('#FormOfSelectAnalysisGraphFile').submit();
		}else{
			alert('Please paste a valid link.');
			return;
		}
	}
	
	if (typ == 'upload'){	
		
		if ($('#myFileSMD').get(0).files.length === 0 && $('#myFileAG').get(0).files.length === 0) {
			alert('Please choose at least one file.');
			return;
		}else{
			$('#formType').val('selectAnalysisGraphUpload');
			$('#FormOfSelectAnalysisGraphFile').submit();	
		}			
	}	
}

</script>

</head>

<body>

<div class="tab">    
	<button class="tablinks main mainTab active" onclick=""> New </button>    	 
</div>
	

<div id="bg">
	
        
  <div class="module">
  		<div class="wrapper fixedPercentageWidth" align="center">            
	  <c:forEach items="${requestScope['errors']}" var="entry">
	  <strong> <a class="error" href="#"> Errors occured! Click here!</a> </strong> 	  	  			 							  					  
	  	<div id="alertDiv" class="smallFontSize small alert alert-danger">					  
		  ${entry}
		</div>		  							  							
	  </c:forEach> 			  
      </div>  
 		<script>            
           $('.wrapper').find('a[href="#"]').on('click', function (e) {
               e.preventDefault();
               this.expand = !this.expand;
               $(this).text(this.expand?"Click to collapse errors!":"Errors! Click here!");	                
               $(this).closest('.wrapper').find('.small, .big').toggleClass('small big');
               $(this).closest('.wrapper').find('#alertDiv').addClass('alert alert-danger smallFontSize');
           });                        
       </script>
       
		<form id="FormOfSelectAnalysisGraphFile" class="from" method="POST" action="ManipulateAnalysisGraphs">
		
		<!-- h4 class="specification" style="height:25px; padding-top:5px; padding-bottom:2px;">  Select Analysis Graph </h4-->    	
   	
    	<div style="border-style: solid;border: darkorchid;border-color: coral;border-width: 1px;border: 1px;">
	    	<h4 class="mainPage"> Choose an existing analysis graph file </h4>
	    	<br/>
			<select onchange="changeSelected()" id="selectAS" class="textbox" name="availableASs">
			  <option value="none"> none </option>
	          <c:forEach var="item" items="${requestScope.availableAGs}">
	            <option value="${item}">${item}</option>
	          </c:forEach>
	        </select> 
	        &ensp;<i class="qtip tip-right" data-tip="Select an analysis graph from the already existing ones."> 
		    <font style="font-weight:bold; font-size:12px;">  <img src='img/info.png'  width='5' height='5' /> </font> </i>	    
	        <br/>
	        <input type="button" onclick="doSubmit('select');" value="SELECT" class="minimizedButton btn btn-info btn-sm" />
	        <br/>
	        <br/>
        </div>
        <div style="border-style: solid;border: darkorchid;border-color: coral;border-width: 1px;border: 1px;" >
			<h4 class="mainPage"> Or paste a link to an analysis graph file </h4>
			<br/>
			<input type="text" id="pasteAG" class="textbox" name="pasteAG"/> 
			&ensp;<i class="qtip tip-right" data-tip="Provide a link to an analysis graph file over the web."> 
		    <font style="font-weight:bold; font-size:12px;">  <img src='img/info.png'  width='5' height='5' /> </font> </i>	    
			<br/>	
			<input type="button" onclick="doSubmit('paste');" value="SELECT" class="minimizedButton btn btn-info btn-sm" />
			<br/>	
			<br/>
		</div>
		<div style="border-style: solid;border: darkorchid;border-color: coral;border-width: 1px;border: 1px;" >
			<h4 class="mainPage"> Or upload local files </h4>
			<br/>
			
			Analysis Graph 
			&ensp;<i class="qtip tip-right" data-tip="Upload an analysis graph file from your PC."> 
		    <font style="font-weight:bold; font-size:12px;">  <img src='img/info.png'  width='5' height='5' /> </font> </i>	 
		       
		    <input style="text-align: left; margin: auto;" type="file" id="myFileAG" name="myFileAG" class="textbox" accept=".ttl,.rdf,.nt,.jsonld,.owl,.trig,.nq,.trix,.trdf"> 
		     	
			<br/>	
			Multidimensional Schema 
			&ensp;<i class="qtip tip-right" data-tip="Upload a multidimensional schema RDF file from your PC, 
			on which the provided analysis graph file depends."> 
		    <font style="font-weight:bold; font-size:12px;">  <img src='img/info.png'  width='5' height='5' /> </font> </i>	 
		       
		    	<input style="text-align: left; margin: auto;" type="file" name="myFileSMD" id="myFileSMD" class="textbox" accept=".ttl,.rdf,.nt,.jsonld,.owl,.trig,.nq,.trix,.trdf">
		    	
			<br/>
			<input type="button" onclick="doSubmit('upload');" value="SELECT" class="minimizedButton btn btn-info btn-sm" />
			
			<br/>
		</div>
		
		<input type="hidden" id="analysisGraphFileName" name="analysisGraphFileName" placeholder="analysis situation name"/>
		<input type='hidden' id='formType' name='formType' />		
		</form>
								
		
	</div>
	
	
</div>

</body>
</html>

</body>
</html>