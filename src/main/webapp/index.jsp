<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head> 
  
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<script src="https://code.jquery.com/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.js"></script> 
<meta name="viewport" content="width=device-width, initial-scale=1">    
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="http://code.jquery.com/ui/1.12.1/jquery-ui.js"> </script>
<script src="//cdnjs.cloudflare.com/ajax/libs/json3/3.3.2/json3.min.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css" /> 
<!-- Those two lines must go here; otherwise, some collapse things don't work -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">  
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link href="css/jquery-menu.css" rel="stylesheet">
<script src="js/jquery-menu.js"></script>
<script src="js/jquery.hoverIntent.js"></script>
<link rel="stylesheet" href="css/context.standalone.css">  
<script src="ContextMenu/contextMenu.js"></script>
<link rel="stylesheet" href="ContextMenu/contextMenu.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.form/4.2.2/jquery.form.min.js" integrity="sha384-FzT3vTVGXqf7wRfy8k4BiyzvbNfeYjK+frTVqZeNDFl8woCbF0CYG6g2fMEFFo/i" crossorigin="anonymous"></script>

 <style>
        .ui-autocomplete { 
            cursor:pointer; 
            height:200px; 
            overflow-y:scroll;
            font-size: small;
            color: black;
        }    
    </style>
 <script>      
 
 
 </script>
    
 <script src="https://www.gstatic.com/charts/loader.js"></script>
	
	
  <script>
  
  $(function(){
      $("#help").load("help.htm"); 
    });
  
  function onResultsButtonClick (event){
	  
	  var currAsName = $('input[name=currASName]').val();
	  
	  // Ensuring a real click was triggered
	  if(event.clientX != 0 && event.clientY != 0){
		  if (checkValueIsOk(currAsName)){
			  //alert('submitting');
			  submitForm('analysisSituation');
		  }else{
			  alert('Cannot display results; please select an analysis situation first');
		  }
	  }else{
		  setActiveGraphTab(event, 'resultsChart');
	  }
  }
    
  function onSPARQLTabClick(event){
	  
	var currAsName = $('input[name=currASName]').val();
	  
	  // Ensuring a real click was triggered
	  if(event.clientX != 0 && event.clientY != 0){
		  if (checkValueIsOk(currAsName)){			  
			  $("#analysisSituation").append('<input type="hidden" name="typeOfSubmitForSPARQL" value="sparqlSubmit">');
			  submitForm('analysisSituation');
		  }else{
			  alert('Cannot display SPARQL; please select an analysis situation first');
		  }
	  }else{
		  setActiveGraphTab(event, 'sparql');
	  }
  }
  
  function onHelpButtonClick (event){
	  openWindowWithPost('help.htm', '');
  }
  
  function onMDSchemaButtonClick (event){
	  //openWindowWithPost('Main2.jsp', '');
  }
  
  $(document).ready(function() 
		  {	
		      $('a').on('click', function() {
		    	  		    	  
		          $("input,select,textarea").each(function() {
		        	  
		             if($(this).is("[type='checkbox']") || $(this).is("[type='checkbox']")) {
		            	 		            	 
		               $(this).attr("checked", $(this).attr("checked"));
		             }
		             else {
		            	 		            	 
		                $(this).attr("value", $(this).val()); 
		             }
		          });
		      });
		  });
	  
  // document listner to register click events...
  
  document.addEventListener('click', function (event, from) {
	  	 
      $("input").each(function() {    	
            $(this).attr("value", $(this).val()); 
         });      
      $("input:checkbox").each(function() {    	  
         if($(this).is("[type='checkbox']") || $(this).is("[type='checkbox']")) {        	         
           $(this).attr("checked", $(this).prop("checked"));
         }         
      });
      
	$("select").each(function() {
    	      	
        	 var val = $(this).find(":selected").val();
        	 var theName = $(this).attr('name');        	 
        	 $(this).find("option[value='" + val + "']").attr('selected', true);
        	 $(this).find("option[value!='" + val + "']").removeAttr('selected');        	 
      });
      
		 if(event.clientX != 0 && event.clientY != 0){
			 // Probably real click 
			 $.ajax({
				  type: 'POST',
				  url: 'StoreEventsServlet',//url of receiver file on server
				  data: '---------------------- real ----------------------\n ' + event.target.outerHTML.replace(event.target.innerHTML, ""),
				  dataType: 'text'
				});
		 }
	 	// Probably fake click
	    else{
	    	$.ajax({
				  type: 'POST',
				  url: 'StoreEventsServlet',//url of receiver file on server
				  data: '---------------------- fake ----------------------\n' + event.target.outerHTML.replace(event.target.innerHTML, ""),
				  dataType: 'text'
	    		});
	    }
		
	}, false);
  
  
  var asArray = {};
  var nvArray = {};
  var i1 = 0;
  
  // loading the analysis situations array from the session values
  <c:forEach items="${allASsResponseStr}" var="entry"> 
  
	  i1 = 0;
	  if (!asArray["${entry.key}"]){
			asArray["${entry.key}"] = {};
  	  }
	  <c:forEach items="${entry.value}" var="entry1">
			if (i1 == 0){				
				asArray["${entry.key}"][0] = "${entry1}";
				++i1;
			}else{
				asArray["${entry.key}"][1] = "${entry1}";
			}
		</c:forEach>

  		console.log('AS Array: ' + 'value: ' + asArray["${entry.key}"]);
  </c:forEach>	
  
	//loading the navigation steps array from the session values
	var i2 = 0;
  <c:forEach items="${allNVsResponseStr}" var="entry"> 
  
  	  i2 = 0;
	  if (!nvArray["${entry.key}"]){
		  nvArray["${entry.key}"] = {};
	  }
	  <c:forEach items="${entry.value}" var="entry1">
			if (i2 == 0){				
				nvArray["${entry.key}"][0] = "${entry1}";
				++i2;
			}else{
				nvArray["${entry.key}"][1] = "${entry1}";
			}
		</c:forEach>
		console.log('NV Array: ' + 'value: ' + nvArray["${entry.key}"][0]);
		
  </c:forEach>	
  
  function reload(){
	  document.getElementById('reloadForm').submit();
  }
  
  function selectGraph(){
	  if (confirm('Are you sure you want to leave to graph selection? You will lose your progress!')) {
		  document.getElementById('selectGraphForm').submit();
		}   
  }
  
  function setActiveGraphTab(evt, cityName) {
	    // Declare all variables
	    var i, tabcontent, tablinks;

	    // Get all elements with class="tabcontent" and hide them
	    tabcontent = document.getElementsByClassName("tabcontentForGraphOrResult");
	    for (i = 0; i < tabcontent.length; i++) {
	        tabcontent[i].style.display = "none";
	    }

	    // Get all elements with class="tablinks" and remove the class "active"
	    tablinks = document.getElementsByClassName("graphOrResults");
	    for (i = 0; i < tablinks.length; i++) {
	        tablinks[i].className = tablinks[i].className.replace(" active", "");
	    }

	    // Show the current tab, and add an "active" class to the button that opened the tab
	    document.getElementById(cityName).style.display = "block";
	    evt.currentTarget.className += " active";
	}

  function openCity(evt, cityName) {
	    // Declare all variables
	    var i, tabcontent, tablinks;

	    // Get all elements with class="tabcontent" and hide them
	    tabcontent = document.getElementsByClassName("tabcontent");
	    for (i = 0; i < tabcontent.length; i++) {
	        tabcontent[i].style.display = "none";
	    }

	    // Get all elements with class="tablinks" and remove the class "active"
	    tablinks = document.getElementsByClassName("mainTab");
	    for (i = 0; i < tablinks.length; i++) {
	        tablinks[i].className = tablinks[i].className.replace(" active", "");
	    }

	    // Show the current tab, and add an "active" class to the button that opened the tab
	    document.getElementById(cityName).style.display = "block";
	    evt.currentTarget.className += " active";
	}
  
  function addslashes(string) {
	    return string.replace(/\\/g, '\\\\').
	        replace(/\u0008/g, '\\b').
	        replace(/\t/g, '\\t').
	        replace(/\n/g, '\\n').
	        replace(/\f/g, '\\f').
	        replace(/\r/g, '\\r').
	        replace(/'/g, '\\\'').
	        replace(/"/g, '\\"');
	}
  
  /*
  Escape single quotes
  */
  function escapeSingleQuotes(str){
	  return str.replace(/'/g, '\\\'');	  
  }
  
  /*
  Escape single quotes
  */
  function cancelEscapeSingleQuotes(str){
	  //return str.replace('\\\'', /'/g);	 
	  return str.replace(/\\'/g,"'");
  }
  
  
  /*
  * Checking against all possible terms of an empty/non defined/ non exisitent... value
  */
  function checkValueIsOk (val){
	  if (val !== '' && val !== 'undefined' && typeof val !== 'undefined' && val !== null)
		  return true;
	  else
		  return false;
  }
  
	////console.log('in the beginning');

	var labelValuePairs = {};
	var labelValuePairsLabels = {};
	
	if ('${labelValuePairs}' !== '') {

		labelValuePairs = JSON.parse('${labelValuePairs}');
		labelValuePairsLabels = JSON.parse('${labelValuePairsLabels}');
	}
	
	var str = '';
	for (var fieldLabelIn in labelValuePairsLabels){	
		for (var index in labelValuePairsLabels[fieldLabelIn]){
		  	console.log('labelValuePairsLabels: key: ' + index + ' ---- value: ' + labelValuePairsLabels[fieldLabelIn][index]);
		  	str += 'labelValuePairsLabels: key: ' + index + ' ---- value: ' + labelValuePairsLabels[fieldLabelIn][index] + '\n';
		}
	}
	
	for (var fieldLabelIn in labelValuePairs){	
		for (var index in labelValuePairs[fieldLabelIn]){
		  	console.log('labelValuePairs: key: ' + index + ' ---- value: ' + labelValuePairs[fieldLabelIn][index]);
		  	str +='labelValuePairs: key: ' + index + ' ---- value: ' + labelValuePairs[fieldLabelIn][index] + '\n';
		}
	}

	
	var autocompleteDataByInputField = {};
	
	var filledAfterNavigation = false;
	
	function fillInLabelsAfterNavigation() {

		
		filledAfterNavigation = true;
		var currAsName = $('input[name=currASName]').val();
		
		console.log('in filling in labels -- navigation');

		/*
		labelValuePairs = JSON.parse('${labelValuePairs}');
		labelValuePairsLabels = JSON.parse('${labelValuePairsLabels}');
		*/

		// Looping a two-dimensional array of analysis situations as objects;
		// each is an obect (array) of field names and their values

		var currAsName = $('input[name=currASName]').val();
		var prevASName = '${prevASName}';		
				
		 //for (var asObj in labelValuePairsLabels) {
			// console.log("inn Level 1 " + currAsName + '   ' + asObj);
			//if (currAsName === asObj) {	
				console.log("inn Level 2");
			//	var trimmedName = index.substring(0, index.lastIndexOf('_'));
				$('[name^=diceNode_]').each(function() {
					
						if ($(this).val() !== ''){
						
							if ("${sessionScope['loadedSuggestions']}" != ''){
								<c:forEach items="${sessionScope['loadedSuggestions']}" var="entry">	

									console.log("debigging values " + $(this).val() + "    " + "${entry.getValue()}"); 
								  if($(this).val() === "${entry.getValue()}"){

									$('input[name=\'' + $(this).attr('name') + '\']').val("${entry.getLabel()}");																			
									
									if (!labelValuePairs[currAsName]){
					            		labelValuePairs[currAsName] = {};
					            	}
					            	
					            	if (!labelValuePairsLabels[currAsName]){
					            		labelValuePairsLabels[currAsName] = {};
					            	}
					            	
									labelValuePairsLabels[currAsName][$(this).attr('name')] = "${entry.getLabel()}";
									labelValuePairs[currAsName][$(this).attr('name')] = "${entry.getValue()}";
																		
								  }
								
								</c:forEach>
							}
					}
				});	
				
				$('[name^=diceLevel_]').each(function() {
					
						if ($(this).val() !== ''){
						
							if ("${sessionScope['loadedSuggestions']}" != ''){
								<c:forEach items="${sessionScope['loadedSuggestions']}" var="entry">	

								  if($(this).val() === "${entry.getValue()}"){

									$('input[name=\'' + $(this).attr('name') + '\']').val("${entry.getLabel()}");																			
									
									if (!labelValuePairs[currAsName]){
					            		labelValuePairs[currAsName] = {};
					            	}
					            	
					            	if (!labelValuePairsLabels[currAsName]){
					            		labelValuePairsLabels[currAsName] = {};
					            	}
					            	
									labelValuePairsLabels[currAsName][$(this).attr('name')] = "${entry.getLabel()}";
									labelValuePairs[currAsName][$(this).attr('name')] = "${entry.getValue()}";
									
								  }
								
								</c:forEach>
							}
					}
				});	
				
				$('[name^=granularityLevel_]').each(function() {

				
						if ($(this).val() !== ''){
						
							if ("${sessionScope['loadedSuggestions']}" != ''){
								<c:forEach items="${sessionScope['loadedSuggestions']}" var="entry">	

								  if($(this).val() === "${entry.getValue()}"){

									$('input[name=\'' + $(this).attr('name') + '\']').val("${entry.getLabel()}");																													
									
									if (!labelValuePairs[currAsName]){
					            		labelValuePairs[currAsName] = {};
					            	}
					            	
					            	if (!labelValuePairsLabels[currAsName]){
					            		labelValuePairsLabels[currAsName] = {};
					            	}
					            	
									labelValuePairsLabels[currAsName][$(this).attr('name')] = "${entry.getLabel()}";
									labelValuePairs[currAsName][$(this).attr('name')] = "${entry.getValue()}";
									
								  }
								
								</c:forEach>
							}
					}
				});	
				
				fillInLabels();
	}
			 
			 

	function fillInLabels() {

		console.log('before filling in labels');		
		for (var fieldLabelIn in labelValuePairsLabels){	
			for (var index in labelValuePairsLabels[fieldLabelIn]){
			  	console.log('labelValuePairsLabels: AS: ' + fieldLabelIn + ' ---- key: ' + index + ' ---- value: ' + labelValuePairsLabels[fieldLabelIn][index]);
			}
		}		
		for (var fieldLabelIn in labelValuePairs){	
			for (var index in labelValuePairs[fieldLabelIn]){
			  	console.log('labelValuePairs: AS: ' + fieldLabelIn + ' ---- key: ' + index + ' ---- value: ' + labelValuePairs[fieldLabelIn][index]);
			}
		}		
		
		var currAsName = $('input[name=currASName]').val();			
		// Analysis situation case
		if (checkValueIsOk(currAsName)){			
			// Looping a two-dimensional array of analysis situations as objects;
			// each is an obect (array) of field names and their values
			for ( var asObj in labelValuePairsLabels) {
				if (asObj === currAsName) {					
					
					for ( var index in labelValuePairsLabels[asObj]) {	
						console.log('index: ' + index.substring(0, index.lastIndexOf('_')));
						var currObj = labelValuePairsLabels[asObj][index];						
						// getting the part of the name before the last underscoree
						var trimmedName = index.substring(0, index.lastIndexOf('_'));						
						console.log('trimmedName: ' + trimmedName);
						$('[name^=\'' + trimmedName + '_' + '\']').each(function() {																											
								if ($('input[name=\'' + $(this).attr('name') + '\']').val() !== '') {
									$('input[name=\''+ $(this).attr('name')+ '\']').val(labelValuePairsLabels[asObj][index]);									
									console.log('filling in as: ' + ' with value: ' + labelValuePairsLabels[asObj][index]);																				
								}
						});
					}
				}
			}			
		}else {			
			var currNvName = $('input[name=currNVName]').val();
			
			// Navigation step case
			if (checkValueIsOk(currNvName)){
				// Looping a two-dimensional array of analysis situations as objects;
				// each is an obect (array) of field names and their values
				for ( var nvObj in labelValuePairsLabels) {
					if (nvObj === currNvName) {					
						for ( var index in labelValuePairsLabels[nvObj]) {										
							var currObj = labelValuePairsLabels[nvObj][index];						
							// getting the part of the name before the last underscoree
							var trimmedName = index.substring(0, index.lastIndexOf('_'));													
							$('[name^=\'' + trimmedName + '_' + '\']').each(function() {																											
									if ($('input[name=\'' + $(this).attr('name') + '\']').val() !== '') {
										$('input[name=\''+ $(this).attr('name')+ '\']').val(labelValuePairsLabels[nvObj][index]);									
										console.log('filling in nv: ' + ' with value: ' + labelValuePairsLabels[nvObj][index]);																				
									}
							});
						}
					}
				}
			}
		}
		
		for (var fieldLabelIn in labelValuePairsLabels){	
			for (var index in labelValuePairsLabels[fieldLabelIn]){
			  	console.log('labelValuePairsLabels: AS: ' + fieldLabelIn + ' ---- key: ' + index + ' ---- value: ' + labelValuePairsLabels[fieldLabelIn][index]);
			}
		}		
		for (var fieldLabelIn in labelValuePairs){	
			for (var index in labelValuePairs[fieldLabelIn]){
			  	console.log('labelValuePairs: AS: ' + fieldLabelIn + ' ---- key: ' + index + ' ---- value: ' + labelValuePairs[fieldLabelIn][index]);
			}
		}
	}

	// clear the values from labelValuePairs and labelValuePairsLabels
	// when the delete button is clicked on a specific field
	function clearFromValuesAndLabels(nameOfField) {
		delete labelValuePairs[nameOfField];
		delete labelValuePairsLabels[nameOfField];
	}  
 
  var currPageNum = 1; 
  
  var alreadyLoadedSuggestion = [];
  
  
  
  function showOrHideNonVariables(d){
	  if($('#showHideNonVarsChck').prop('checked')) {
		  $('.nonVariableListItem').each(function(){
			 $(this).hide();			 
		  });
      }else{
    	  $('.nonVariableListItem').each(function(){
    		 $(this).show();			 
 		  });
      }  	    	
  }
  
  function clickNext(d){
	  $(d).next().trigger('click');
  }   
  
  function submitForm(formID){

	generateSliceConditionString();	  
	var currNvName = $('input[name=currNVName]').val();
	var hasErrors = false;
	
	if (checkValueIsOk(currNvName)){
		$('input[name*=variable]').each(function(){
			if($(this).val() == ''){				
				$(this).addClass('error');
				hasErrors = true;							
			}
			console.log('variable: ' + $(this).val());
		});				
	}
	
	if (hasErrors){
		alert('Please fill in the variables of the navigation highlighted in bold red!');
		return;
	}
  
	//alert(getGPlottingAreaTransform());
	
	$("#nodesPositions").val(getPosition(force));
	$("#transform").val(getGPlottingAreaTransform());
	
	var currAsName = $('input[name=currASName]').val();

	if (checkValueIsOk(currAsName)){
		// Looping a two-dimensional array of analysis situations as objects;
		// each is an obect (array) of field names and their values
		for ( var asObj in labelValuePairs) {		
			if (asObj === currAsName) {
				for ( var index in labelValuePairs[asObj]) {
					var currObj = labelValuePairs[asObj][index];			
					// getting the part of the name before the last underscoree
					var trimmedName = index.substring(0, index.lastIndexOf('_'));
					$('[name^=\'' + trimmedName + '_' + '\']').each(function() {
								if ($('input[name=\'' + $(this).attr('name') + '\']').val() !== '' /* No substitution for written slice conditions */ && labelValuePairsLabels[asObj][index] === $('input[name=\'' + $(this).attr('name') + '\']').val()) {
									$('input[name=\'' + $(this).attr('name') + '\']').val(labelValuePairs[asObj][index]);
									console.log("altering in: " + trimmedName + "--" + $(this).attr('name') + " -- " + labelValuePairs[asObj][index]);
								}
					});
				}
			}
		}	
	}else {

		if (checkValueIsOk(currNvName)){
			// Looping a two-dimensional array of analysis situations as objects;
			// each is an obect (array) of field names and their values
			for (var nvObj in labelValuePairs) {		
				if (nvObj === currNvName) {
					for ( var index in labelValuePairs[nvObj]) {
						var currObj = labelValuePairs[nvObj][index];				
						// getting the part of the name before the last underscoree
						var trimmedName = index.substring(0, index.lastIndexOf('_'));
						$('[name^=\'' + trimmedName + '_' + '\']').each(function() {
									if ($('input[name=\'' + $(this).attr('name') + '\']').val() !== '' /* No substitution for written slice conditions */&& labelValuePairsLabels[nvObj][index] === $('input[name=\'' + $(this).attr('name') + '\']').val()) {								
										$('input[name=\'' + $(this).attr('name') + '\']').val(labelValuePairs[nvObj][index]);
									}
						});
					}
				}
			}
		}
	}
	
	
	var str = '';
	for (var fieldLabelIn in labelValuePairsLabels){	
		for (var index in labelValuePairsLabels[fieldLabelIn]){
		  	console.log('labelValuePairsLabels: key: ' + index + ' ---- value: ' + labelValuePairsLabels[fieldLabelIn][index]);
		  	str += 'labelValuePairsLabels: key: ' + index + ' ---- value: ' + labelValuePairsLabels[fieldLabelIn][index] + '\n';
		}
	}
	
	for (var fieldLabelIn in labelValuePairs){	
		for (var index in labelValuePairs[fieldLabelIn]){
		  	console.log('labelValuePairs: key: ' + index + ' ---- value: ' + labelValuePairs[fieldLabelIn][index]);
		  	str +='labelValuePairs: key: ' + index + ' ---- value: ' + labelValuePairs[fieldLabelIn][index] + '\n';
		}
	}
	
	  				
	$("#labelValuePairs").val(JSON.stringify(labelValuePairs));	
	$("#labelValuePairsLabels").val(JSON.stringify(labelValuePairsLabels));

	$("#" + formID).submit();
  }
  
  function generateSliceConditionString(){
  
	  $('[name^=sliceCond_]').each(function() {

		  var str1 = $('input[name=\'' + 'splited_' + $(this).attr('name') + '_part1' + '\']').val();
		  var str2 = $('select[name=\'' + 'splited_' + $(this).attr('name') + '_part2' + '\']').find(":selected").val();
		  var str3 = $('input[name=\'' + 'splited_' + $(this).attr('name') + '_part3' + '\']').val();
	
		  if (checkValueIsOk(str1) && checkValueIsOk(str2) && checkValueIsOk(str3)){
			  $('input[name=\'' + $(this).attr('name') + '\']').val(/*str1 + ' ' + */ str2 + ' ' + str3);  
		  }else{
			  $('input[name=\'' + $(this).attr('name') + '\']').val('');
		  }
		  
		  console.log('Slice condition -- str4:' + $(this).attr('name') + ' -- ' + $('input[name=\'' + $(this).attr('name') + '\']').val());		  
	  });	  
  }
  
  function doErrorClassRemoval(d){
	  $(d).removeClass('error');
  }
  
  function submitFormByName(formName, targetAsName){
	  
	  generateSliceConditionString();
	    
	$("#nodesPositions").val(getPosition(force));
	$("#transform").val(getGPlottingAreaTransform());

	
	var currAsName = $('input[name=currASName]').val();

	// Looping a two-dimensional array of analysis situations as objects;
	// each is an obect (array) of field names and their values targetAsName
	for ( var asObj in labelValuePairs) {
		if (asObj === currAsName) {

			for ( var index in labelValuePairs[asObj]) {

				var currObj = labelValuePairs[asObj][index];				
				var trimmedName = index
						.substring(0, index.lastIndexOf('_'));

				$('[name^=\'' + trimmedName + '_' + '\']').each(
						function() {
							if ($(
									'input[name=\'' + $(this).attr('name')
											+ '\']').val() !== '') {
								$(
										'input[name=\''
												+ $(this).attr('name')
												+ '\']').val(
										labelValuePairs[asObj][index]);
							}
						});
			}
		}
	}
	
	$("#labelValuePairs").val(JSON.stringify(labelValuePairs));
	$("#labelValuePairsLabels").val(JSON.stringify(labelValuePairsLabels));

	return;
	$('form[name=' + formName + ']').submit();
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
		
	  if (!jsonForVisualization) 
	  	var arr = JSON.parse('${jsonForVisualization}');
	  else{
		  var arr = JSON.parse(cancelEscapeSingleQuotes(jsonForVisualization));  
	  }
		
	
	  var data = new google.visualization.DataTable(arr);
	  
	  var chartType = document.getElementById('chart-type');
	  var chartWrapper = new google.visualization.ChartWrapper({
	    chartType: chartType.value,
	    containerId: 'chart',
	    dataTable: data,
	    width: '100%',
	    options: {	 
	    	/*explorer: { 
				  //actions: ['dragToZoom', 'rightClickToReset'],
		            keepInBounds: true,
		            maxZoomIn: 4.0},*/
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
  
  $.ui.autocomplete.prototype._renderItem = function (ul, item) {
	    var re = new RegExp($.trim(this.term.toLowerCase()));
	    var t = item.label.replace(re, "<span style='font-weight:600;color:#5C5C5C;'>" + $.trim(this.term.toLowerCase()) +
	        "</span>");
	    return $("<li></li>")
	        .data("item.autocomplete", item)
	        .append("<a>" + t + "</a>")
	        .appendTo(ul);
	};
	
	function doLevelOnChange(d, nam, val){	 
	  
	  val = "[" + val + "]";
	  var obj = JSON.parse(val);		  
	  var currAsName = $('input[name=currASName]').val();  	
  
	  var val1 =  $(d).val();
	  
	  for (var fieldLabelIn in labelValuePairs){	
			for (var index in labelValuePairs[fieldLabelIn]){
			  	console.log('labelValuePairs: key: ' + index + ' ---- value: ' + labelValuePairs[fieldLabelIn][index]);
			  	//str +='labelValuePairs: key: ' + index + ' ---- value: ' + labelValuePairs[fieldLabelIn][index] + '\n';
			}
		}
  	
  	if (checkValueIsOk(currAsName)){  		
  		console.log("levelonchange  0.5 " + $(d).attr("name"));
  		 console.log("levelonchange  1 " + labelValuePairs[currAsName][$(d).attr("name")]);
  	
  		if (checkValueIsOk(labelValuePairs[currAsName][$(d).attr("name")])){
  			console.log("levelonchange  2 " + labelValuePairs[currAsName][$(d).attr("name")]);
  			
  			val1 = labelValuePairs[currAsName][$(d).attr("name")];
  		}
  	}

	  obj[0].levelName = val1;
	  
	  var jsonBack = JSON.stringify(obj[0]);
	  $('button[name=\'' + nam + '\']').attr('value', jsonBack);
  }
	
	
	
	function doAjaxSubmitForSPARQL (d){	 
		
		  $("#sparqlForm").append('<input type="hidden" name="submitType" value="ajaxSubmit">');
		  $("#sparqlForm").append('<input type="hidden" name="name" value="sparql">');
		  
		    var url = 'ManipulateAnalysisGraphs'; // the script where you handle the form input.

		    $.ajax({
		           type: "POST",
		           url: url,
		           data: $("#sparqlForm").serialize(), // serializes the form's elements.
		           success: function(data1)
		           {		        	   
		        	   openWindowWithPost('Results.jsp', data1);
		        	   		               
		           },
		           error:function(){
		        	  alert('failure'); 
		           }
		           
		         });
		    $("#sparqlForm input[name=submitType]").remove();
		    $("#sparqlForm input[name=name]").remove();		   
		    e.preventDefault(); // avoid to execute the actual submit of the form.	  
	}	
	
	
	  
	
  function doAjaxSubmit (d){	 
	
	var firstRow = parseInt($('input[name=firstRow]').attr('value'), 10);	  
	  $("#analysisSituation").append('<input type="hidden" name="submitType" value="ajaxSubmit">');	  
	    var url = 'ManipulateAnalysisGraphs'; // the script where you handle the form input.

	    $.ajax({
	           type: "POST",
	           url: url,
	           data: $("#analysisSituation").serialize(), // serializes the form's elements.
	           success: function(data1)
	           {
	        	   $("input[name=submitType]").remove();
	               //alert(data1); // show response from the php script.
	               
	               var retData = JSON.parse(data1);
	               jsonForVisualization = retData.results;
		           drawChart();
		           document.getElementById('resultsButton').click();
		           $('#results > tbody').html(jsonForVisualization);
		           
		           var totalRowCount = retData.totalRowCount;
		           var pagesCount = retData.pagesCount;
		           		          
		           var inHTML = "<h4 class='specification' style='width: 30%'>" + 
		           totalRowCount +
		           " total results found </h4> <b> <font color='red' id='pageNumber'> 1 </font> of " +
		           pagesCount +
		           " pages  </b>";
		           $("#resultsNumbers").html(inHTML);
	           },
	           error:function(){
	        	   $("input[name=submitType]").remove();
	        	  alert('failure'); 
	           }
	           
	         });

	    e.preventDefault(); // avoid to execute the actual submit of the form.	  
	  $("#analysisSituation").submit();	

  }	
  
 
  function doSuggButtonOnClick(d){	 	
	  if (false /*$.inArray($(d).attr('name'), alreadyLoadedSuggestion) !== -1*/){
		////console.log('loadedzz');
		  var replacedInputName = $(d).attr('name').replace('suggestion', '');	  
	  }else{
		  var replacedInputName = $(d).attr('name').replace('suggestion', '');	  
		  
		  if (!$('input[name=checkBox_' + replacedInputName + ']').is(":checked")){
		////console.log('not loadedzz');
		  alreadyLoadedSuggestion.push($(d).attr('name'));		  
		  var dataString1 = $(d).attr('name');
		  var dataString2 = $(d).attr('value');
		 
		  var factName = $('#factName').text();	  
		  
	      $.ajax({
	         url:'ManipulateAnalysisGraphs',
	         //async: false,
	         timeout: 50000,
	         data:{submitType:'ajaxSubmit', name:dataString1, value:dataString2, factName:factName},
	         type:'get',
	         cache:true,
	         success:function(data){
	        	        	 
	            $('input[name=\'' + replacedInputName + '\']').autocomplete({
	      			source:data,
	      		  classes: {
	      		    "ui-autocomplete": "highlight"
	      		  },
	      		
	      		minLength: 0,
	            scroll: true,
	            select: function(event, ui) {
	            	
	            	$('input[name=\'' + replacedInputName + '\']').val(ui.item.label);
	            	
	            	// Case the current selection is an analysis situation
	            	var currAsName = $('input[name=currASName]').val();
	            	
	            	console.log('currAsName' + currAsName);
	            	
	            	if (checkValueIsOk(currAsName)){
	            		
		            	if (!labelValuePairs[currAsName]){labelValuePairs[currAsName] = {};}	            	
		            	if (!labelValuePairsLabels[currAsName]){labelValuePairsLabels[currAsName] = {};}	            	
		            	labelValuePairs[currAsName][replacedInputName] = ui.item.value;	            	
		            	labelValuePairsLabels[currAsName][replacedInputName] = ui.item.label;
		            	
		            	console.log('inserting: '  + replacedInputName + ' '  + labelValuePairs[currAsName][replacedInputName] 
					            	+ ' ---- ' 
					            	+ labelValuePairsLabels[currAsName][replacedInputName]);
		            	
	            	}else{
	            		// Case the current selection is a navigation
						var currNvName = $('input[name=currNVName]').val();
	            		
						console.log('currNvName' + currNvName);
	            		
	            		if (checkValueIsOk(currNvName)){
	            			
	            			if (!labelValuePairs[currNvName]){labelValuePairs[currNvName] = {};}	            	
			            	if (!labelValuePairsLabels[currNvName]){labelValuePairsLabels[currNvName] = {};}	            	
			            	labelValuePairs[currNvName][replacedInputName] = ui.item.value;	            	
			            	labelValuePairsLabels[currNvName][replacedInputName] = ui.item.label;
			            	
			            	console.log('inserting: ' + replacedInputName + ' ' + labelValuePairs[currNvName][replacedInputName] 
						            	+ ' ---- ' 
						            	+ labelValuePairsLabels[currNvName][replacedInputName]);
	            		}
	            	}
	            		            		            
	         		for (var fieldLabelIn in labelValuePairsLabels){	
	         			for (var index in labelValuePairsLabels[fieldLabelIn]){
	         			  	console.log('labelValuePairsLabels: AS: ' + fieldLabelIn + ' ---- key: ' + index + ' ---- value: ' + labelValuePairsLabels[fieldLabelIn][index]);
	         			}
	         		}	         		
	         		for (var fieldLabelIn in labelValuePairs){	
	         			for (var index in labelValuePairs[fieldLabelIn]){
	         				console.log('labelValuePairs: AS: ' + fieldLabelIn + ' ---- key: ' + index + ' ---- value: ' + labelValuePairs[fieldLabelIn][index]);
	         			}
	         		}
	            	
					if (ui.item == null && $('input[name=\'' + replacedInputName + '\']').val() != '') {
	      				
	    		    	$('input[name=\'' + replacedInputName + '\']').val("");
	    		    	$('input[name=\'' + replacedInputName + '\']').focus();
	    		    }else{
	    		    	$('input[name=\'' + replacedInputName + '\']').removeClass('error');
	    		    }
	         		
	                return false; // Prevent the widget from inserting the value.
	            },	      		  
	      		change: function(event, ui) {
	      			 if (ui.item == null && $('input[name=\'' + replacedInputName + '\']').val() != '') {
	      				
	    		    	$('input[name=\'' + replacedInputName + '\']').val("");
	    		    	$('input[name=\'' + replacedInputName + '\']').focus();
		    		    }else{
		    		    	$('input[name=\'' + replacedInputName + '\']').removeClass('error');
		    		    }
	      		}
	      		  // position: { my : "right top", at: "right bottom" }            
	    		}).focus(function() {
	                $(this).autocomplete("search", "");
	        
	    		});

	            $('#somediv').text(data);
	    		$('#otherdiv').text(data);
	            //alert('Suggestions loaded');
	         },
	         error:function(){
	        	 	        
	           alert('Suggestions could not be loaded');
	         }
	  	});	      	    
		 	
		 // document.getElementById('refreshResults').click();
	  }	else{
		  $('input[name=\'' + replacedInputName + '\']').autocomplete({source: [],
			  change: function(event, ui) {}
   			 }).focus(function() {
             
  		});
	  }
	  }
  }
  

  
function doNextOnClick(d, typ){	 	  	
	  var firstRow = $('input[name=firstRow]').attr('value');	  
	  var firstRow = parseInt($('input[name=firstRow]').attr('value'), 10);
	  
	  if (typ === 'next'){
		  		 
		  if (currPageNum === parseInt("${pagesCount}")){		
			  return;
		  }
	      $.ajax({
	         url:'ManipulateAnalysisGraphs',
	         async: false,
	         data:{submitType:'ajaxSubmit', name:'next', firstRow:firstRow},
	         type:'get',
	         cache:false,
	         success:function(data){
	        	 jsonForVisualization = data;
	        	 drawChart();
	            $('#results > tbody').html(data);
	            $('input[name=firstRow]').attr('value', firstRow + ${rowsCount});
	            currPageNum = parseInt(parseInt(firstRow  + ${rowsCount}) / parseInt(${rowsCount}) + 1);
	            $('#pageNumber').html(currPageNum);
	         },
	         error:function(){
	           alert('error paging the results');
	         }
	  	});
	  }else{
		  
		  if (typ === 'previous'){
			  if (currPageNum === 1){
				  return;
		  	  }
			  $.ajax({
			         url:'ManipulateAnalysisGraphs',
			         async: false,
			         data:{submitType:'ajaxSubmit', name:'previous', firstRow:firstRow},
			         type:'get',
			         cache:false,
			         success:function(data){
			        	jsonForVisualization = data;
			        	drawChart();			        	
			            $('#results > tbody').html(data);
			            $('input[name=firstRow]').attr('value', firstRow - ${rowsCount});
			            currPageNum = parseInt(parseInt(firstRow  - ${rowsCount}) / parseInt(${rowsCount}) + 1);
			            $('#pageNumber').html(currPageNum);
			         },
			         error:function(){
			           alert('error paging the results');
			         }
			  	});
		  }
	  }
  }
  
  function fillData (){
	  
	  <c:forEach items="${allASsResponseStr}" var="entry">
	    if ("${entry.key}" == asName){
	    	var i = 0;
	    	<c:forEach items="${entry.value}" var="entry1">
	    	if (i == 0){
	    		document.getElementById("as_section1").innerHTML = "${entry1}";
	    		++i;
	    	}else{
	    		document.getElementsByName("as_section2")[0].innerHTML = "${entry1}";
	    	}	    	
	    	</c:forEach>	    
	    }
 	 </c:forEach>	
  }
  </script>  
  <script>
  
  $( "#target" ).contextmenu(function() {
	  alert( "Handler for .contextmenu() called." );
	});
  
  function changeNav(sel){
	  document.getElementById('navigateOrNot').value = sel.value;
  }
  
  function ajaxSubmitAs(){
	  
	  console.log('ajaxA')
	  
	  generateSliceConditionString();
	  
	  var dataString1 = 'refreshContents';
	  
	  	//$("#nodesPositions").val(getPosition(force));
		//$("#transform").val(getGPlottingAreaTransform());
		$("#labelValuePairs").val(JSON.stringify(labelValuePairs));	
		$("#labelValuePairsLabels").val(JSON.stringify(labelValuePairsLabels));
	    
	    $("#analysisSituation").append('<input type="hidden" name="submitType" value="ajaxSubmit">');
	    $("#analysisSituation").append('<input type="hidden" name="name" value="refreshContents">');
		  
	    var currAsName = $('input[name=currASName]').val();

		// Looping a two-dimensional array of analysis situations as objects;
		// each is an obect (array) of field names and their values
		for ( var asObj in labelValuePairs) {
			if (asObj === currAsName) {

				for ( var index in labelValuePairs[asObj]) {

					var currObj = labelValuePairs[asObj][index];

					console.log('labelValuePairs for AS: ' + asObj + 'key: '
							+ index + ' ---- value: '
							+ labelValuePairs[asObj][index]);
					console.log('labelValuePairsLabels for AS: ' + asObj + 'key: '
							+ index + ' ---- value: '
							+ labelValuePairsLabels[asObj][index]);

					// getting the part of the name before the last underscoree
					var trimmedName = index
							.substring(0, index.lastIndexOf('_'));

					$('[name^=\'' + trimmedName + '_' +  '\']').each(
							function() {
								if ($(
										'input[name=\'' + $(this).attr('name')
												+ '\']').val() !== '') {

									$(
											'input[name=\''
													+ $(this).attr('name')
													+ '\']').val(
											labelValuePairs[asObj][index]);
								}
							});
				}
			}
		}				
	    
	    $.ajax({
	       url:'ManipulateAnalysisGraphs',
	       //async: false,
	       timeout: 50000,
	       data:$("#analysisSituation").serialize(),
	       type:'get',
	       cache:true,
	       success:function(data){
	       },
	       error:function(){
	         //alert('Error saving!');
	       }
		});
	    $("input[name=submitType]").remove();
  	    $("input[name=name]").remove();
  }
  
function ajaxSubmitNv(){
	console.log('ajaxN');
	
	generateSliceConditionString();

	  var dataString1 = 'refreshContents'; 
	
	$("#labelValuePairs").val(JSON.stringify(labelValuePairs));	
	$("#labelValuePairsLabels").val(JSON.stringify(labelValuePairsLabels));
	    
	    $("#navigationStep").append('<input type="hidden" name="submitType" value="ajaxSubmit">');
	    $("#navigationStep").append('<input type="hidden" name="name" value="refreshContents">');
		  
	    var currNvName = $('input[name=currNVName]').val();

		// Looping a two-dimensional array of analysis situations as objects;
		// each is an obect (array) of field names and their values
		for ( var nvObj in labelValuePairs) {
			if (nvObj === currNvName) {

				for ( var index in labelValuePairs[nvObj]) {
					var currObj = labelValuePairs[nvObj][index];					

					// getting the part of the name before the last underscoree
					var trimmedName = index
							.substring(0, index.lastIndexOf('_'));

					$('[name^=\'' + trimmedName + '_' +  '\']').each(
							function() {
								if ($(
										'input[name=\'' + $(this).attr('name')
												+ '\']').val() !== '') {

									$(
											'input[name=\''
													+ $(this).attr('name')
													+ '\']').val(
											labelValuePairs[nvObj][index]);
								}
							});
				}
			}
		}				
	    
	    $.ajax({
	       url:'ManipulateAnalysisGraphs',
	       //async: false,
	       timeout: 50000,
	       data:$("#navigationStep").serialize(),
	       type:'get',
	       cache:true,
	       success:function(data){

		    },
	       error:function(){ 
	   	 
	         //alert('Error saving!');
	       }
		});
	    $("input[name=submitType]").remove();
	    $("input[name=name]").remove();

  }
  
  function fillASDiv(asName){  
	  
    for (var as in asArray) {
    	if (as === asName){
    		document.getElementById("as_section1").innerHTML = asArray[as][0];
    		document.getElementsByName("as_section2")[0].innerHTML = asArray[as][1];
    	}    	
    }
    
    if ('${prevASName}' === '' || filledAfterNavigation)
    	fillInLabels();       
  }
  
  function fillNVDiv(nvName){

	  for (var nv in nvArray) {
	    	if (nv === nvName){
	    		document.getElementById("as_section1").innerHTML = nvArray[nv][0];
	    		document.getElementsByName("as_section2")[0].innerHTML = nvArray[nv][1];
	    	}    	
	    }	
	    
	    if ('${prevASName}' === '' || filledAfterNavigation)
	    	fillInLabels();
  }
    
  function openWindowWithPost(url,str)
  {	
      var newWindow = window.open(url);
      if (!newWindow) return false;
    
      newWindow.onload = function(){   
    	  newWindow.document.getElementById("chartData").innerHTML = str;
    	  newWindow.drawChart();    	  
     }           
      return newWindow;
  }
 
  
  </script>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title> Analysis Graphs </title>
    <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Open+Sans:300,400,700">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/2.2.2/css/bootstrap.min.css">      
    <!--link rel="stylesheet" href="css/styl2.css"-->
    <link rel="stylesheet" href="css/app.css">  
     <link rel="stylesheet" href="css/results.css">    
    <script src="lib/d3/d3.min.js"></script>
	<script src="lib/d3/d3.js"></script>
	<script type="text/javascript" src="lib/colors.js"></script>
	<!-- script type="text/javascript" src="lib/jqColorPicker.js"></script -->
  </head>

  <body>        
 
  
	<form id="reloadForm" action="ManipulateAnalysisGraphs" method="post" style="display: none;">      
		    <input type="hidden" class="button" name="formTypeForReset" value="reset">				    
	  </form>
	  
	  <form id="selectGraphForm" action="ManipulateAnalysisGraphs" method="post" style="display: none;">      
		    <input type="hidden" class="button" name="formTypeForSelectGraph" value="selectGraph">				    
	  </form>
	  
    
    <div class="tab">   
    
    	<button class="tablinks main mainTab" onclick="selectGraph()"> New </button>
	  <button class="tablinks main mainTab" id="manipulateButton" onclick="openCity(event, 'manipulate')"> Analysis </button>
	  <!--button class="tablinks main" onclick="openCity(event, 'resultsTabular')">Tabular Results</button-->	
	   <button class="tablinks main mainTab" id="mdSchemaButton" onclick="onMDSchemaButtonClick();openCity(event, 'mdSchema')"> Schema </button>
	   <button class="tablinks main mainTab" id="helpButton" onclick="onHelpButtonClick();"> Help </button>		 
	  <!-- button class="tablinks main mainTab" onclick="reload()"> Reload</button-->    
	    	  
	 
	</div>
	
    <div id="page-container" style="margin:0 auto;">   
    <div id="manipulate" class="tabcontent">        
      <section id="app">
      <!-- h2 id="t"> <span id="title">  Analysis Graph </span> </h2 -->
        
        <div id="app-body" >                              
          <div class="panel tab-content">
    
          
          <c:choose>
			    <c:when test="${empty finalResponse}">
			        <script>
			        	
			        </script>
			    </c:when>
			    <c:otherwise>
			    	<script>
			       	 //openWindowWithPost('Results.jsp', "${finalResponse}");
			        </script>
			    </c:otherwise>
			</c:choose>

            <div id="edit-pane" class="tab-pane">   
            
            <input style="display:none;" class="button" id="refreshResults" type="button" 
            onclick="hide" value="REFRESH RESULTS">	                    	
            	            
            	
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
            
            <br/>
                	                  
                <div class="eval-input oneHundredPercentHeight" align="center">                  		       
                <div id="as_section1" class="oneHundredPercentHeight" name="as_section1">
                
               </div>    
 
                <input type="hidden" id="selectedASName" name="selectedASName" value=""/>                       
                             
				</div>
                                              
              </div>              
          
            <div id="eval-pane" class="tab-pane">
            <div name="as_section2" id="as_section2">
             
         </div>    
                              
              <div class="eval-input">                                                      
              </div>
              
              <div class="eval-output inactive">
              </div>
              <div class="instructions">               
              </div>
            </div>                         
            
          </div>                
         
         <div class="graph1">

         
         <div id="graphOrResultsSelect" class="minitab" align="right">
         
             <button style="width:22%" data-toggle="tooltip" class="tooltipForItems tablinks main graphOrResults" id="graphButton" onclick="setActiveGraphTab(event, 'graphArea')">
              
              
			 Graph   
	         </button>
	         <button style="width:22%" class="tablinks main graphOrResults" id="resultsButton" onclick="onResultsButtonClick(event);"> 
	         Results  	         
	         </button>   
	         <button style="width:22%" class="tablinks main graphOrResults" id="sparqlButton" onclick="onSPARQLTabClick(event)"> 
	         SPARQL  	         
	         </button>    
         </div>
                  
         <div id="resultsChart" class="tabcontentForGraphOrResult">
          <!-- h6 class="specificationWithoutAlign" align="center"> 
         	<input class="button" type="button" onclick="doAjaxSubmit(this)" value="REFRESH RESULTS"> 
         </h6 -->
          
       <!-- section id="app" -->
       
       <div align="center">
       	
    	<form action="ManipulateAnalysisGraphs" method="post">
    	    
    	    <div id="resultsNumbers">
    		</div>
      	 	
      	 	<h4 class="specification" style="width: 100%">  
    			<c:choose>
				    <c:when test="${empty totalRowCount}">
				       0
				    </c:when>    
				    <c:otherwise>
				       ${totalRowCount}
				    </c:otherwise>
				</c:choose>
				total results found 
				<c:choose>
					<c:when test="${not empty as}">
						for
				    </c:when>    
				</c:choose>
				
				<a class="toVisitLink" href="#" onclick="fillASDiv('${as.label}');">
				<c:choose>
					<c:when test="${not empty as}">
						${as.label}
				    </c:when>    
				</c:choose>  
				</a>
				</h4>
							
    			<b> 
    			<font color="red" id="pageNumber"> 
    			<c:choose>
				    <c:when test="${empty pagesCount || pagesCount == 0}">
				       0
				    </c:when>    
				    <c:otherwise>
				       1
				    </c:otherwise>
				</c:choose>
    			</font> 
    			of ${pagesCount} pages  </b>    		 	
    		 
		    <input type="hidden" class="button" name="firstRow" value="0">
		    <input type="hidden" class="button" name="rowcount" value="">
		    
		    <c:choose>
				<c:when test="${pagesCount > 1}">				   
				    <input type="button" class="btn btn-info btn-sm" style="font-weight:bold; font-size:12px;" name="page" onclick="doNextOnClick(this, 'previous')" value="previous">
				    <input type="button" class="btn btn-info btn-sm" style="font-weight:bold; font-size:12px;" name="page" onclick="doNextOnClick(this, 'next')" value="next">
				</c:when>    				    
			</c:choose>
			
			
			<c:choose>
				<c:when test="${pagesCount > 0}">				   
					
				   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b> Chart type  </b>
				  <select id="chart-type" style="display:block;">		
				    <option value="Table" selected>Table</option>
				    <option value="PieChart">Pie</option> 
				    <option value="BarChart">Bar</option>				    
				    <option value="LineChart">Line</option -->	
				    			   
				    <option value="AnnotatedTimeLine">AnnotatedTimeLine</option>
				    <option value="ColumnChart">Column</option>
				    <option value="TreeMap">TreeMap</option>
				    <option value="LineChart">LineChart</option>
				    <option value="AreaChart">AreaChart</option>
				    <option value="ComboChart">ComboChart</option>
				    <option value="ImageChart">ImageChart</option>
				    <option value="ImageBarChart">ImageBarChart</option>
				    <option value="ImageAreaChart">ImageAreaChart</option>
				    <option value="ImageSparkLine">ImageSparkLine</option>		    
				   
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
				  
		  		</c:when>    				    
			</c:choose>
			
			<h4 class="specification" style="width: 100%">  
				<c:choose>
					<c:when test="${not empty as}">
						${as.summarizabilityString}
				    </c:when>    
				</c:choose>  
				</a>
			</h4>
			
		</div>
		
		</form>
		
        <div class="autoOverflowGraph">	
		<div id="chart" align="center" 
		style="width: 100%; border-style: solid; border-color: #499dab; height: 533px; display: inline-block; margin: 0 auto !important;; ">
		</div>
		</div>
		
       <!-- /section -->
       </div>
       
       <div class="tabcontentForGraphOrResult" id="graphArea">
       <div class=" " style="padding-top: 4px;" align="center">
       
       
       		<!-- h6 class="specificationWithoutAlign" align="center" --> 
						
			<!-- input class="btn btn-info btn-sm" style="font-weight:bold; font-size:12px;" type="button" onclick="doTicking()" value="Rearrange graph" -->
			&ensp;&ensp;&ensp;&ensp;		
		   <input style="text-align:center;" alignplaceholder="analysis situation or navigation" class="variable" style="width:400px;" id="search">
		   
		    <input class="btn btn-info btn-sm" style="font-weight:bold; font-size:12px;" type="button" id="theSearchButton" onclick="searchNode(this)" value="SEARCH">	
		    &ensp;<i class="qtip tip-right" data-tip="Look for a specific analysis situation or navigation step by name."> 
		    <font style="font-weight:bold; font-size:12px;">  <img src='img/info.png'  width='5' height='5' /> </font> </i>	    
		    <!-- /h6  --> 
			</div>
			<br/>
		  <div class="autoOverflowGraph">	
          <div class="graph" id="graph"></div>
          
          <div class="current-formula inactive"></div>
          </div>
          </div>
          
           <div id="sparql" class="tabcontentForGraphOrResult">
         
       
       <div align="center">

      	 	
      	 	<br/>
      	 	    
      	 	
		</div>

		
        <div class="autoOverflowGraph" align="center">	
        
        		<form id="sparqlForm" name="sparqlForm">
		 <input class="btn btn-info btn-sm" style="align:center;font-weight:bold; font-size:12px;" type="button" onclick="doAjaxSubmitForSPARQL(this)" value="EXECUTE">
		 
		       	 	<br/>
		       	 	<br/>
		       	 	
		<div align="center" 
		style="width: 100%; border-style: solid; border-color: #499dab; height: 567px; display: inline-block; margin: 0 auto !important;; ">

		 <textarea name="sparqlContent" id="sparqlContent" rows="10" cols="200"><c:out value="${sparql}" escapeXml="false"></c:out></textarea>
		
		 
		</div>
		</form>
		</div>

       </div>

     
      </section>
      <div class="myFooter">
        
      </div>
      </div>
      
     
       
       <div id="mdSchema" class="tabcontent">
       <div class = "app-body-cls">       
       <center>
        <!-- img src="img/mds.jpg" / -->
       <embed src="MDSchema.jsp" style="width:800px; height: 700px;">
        </center>
      
	</div>
	<div class="myFooter">

      </div> 
        
      
       </div>
       
       
       
        <div id="help" class="tabcontent">
       <br/>
       </div> 
             
    </div>

  
  </body>

  <script> document.getElementById('${selectedTab}').click() ; </script>
  <script src="lib/d3/d3.min.js"></script>
  <script type='text/javascript' src="http://bost.ocks.org/mike/fisheye/fisheye.js?0.0.3"> </script>
  <script type="text/x-mathjax-config">
    MathJax.Hub.Config({
      tex2jax: {inlineMath: [['$','$'], ['\\(','\\)']]}
    });
  </script>
  <script src="//cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS_HTML"></script>
  <script src="js/graph/formula-parser.min.js"></script>
  <script src="js/graph/MPL.js"></script>
  <!-- script src="lib/contextmenu.r2.js"></script -->
  <script> <%@ include file="js/graph/app.js" %>  </script>
  <script> 

  // shown.bs.collapse Occurs when the collapsible element is fully shown (after CSS transitions have completed)
  $('#as_section1').on('shown.bs.collapse', '.collapse', function(e){

	e.stopPropagation();
	 $(this).prev().find(".glyphicon-plus").each(function(){		 	
		 $(this).removeClass("glyphicon-plus").addClass("glyphicon-minus");
	 });
		 
  }).on('hidden.bs.collapse', '.collapse', function(e){		 
	 
	  e.stopPropagation();
	  $(this).prev().find(".glyphicon-minus").each(function(){
		 $(this).removeClass("glyphicon-minus").addClass("glyphicon-plus");
	  });	 
  });
  
  // shown.bs.collapse Occurs when the collapsible element is fully shown (after CSS transitions have completed)
  $('#as_section2').on('shown.bs.collapse', '.collapse', function(e){ 
	  
  	  e.stopPropagation();
	  $(this).prev().find(".glyphicon-plus").first().removeClass("glyphicon-plus").addClass("glyphicon-minus");
  }).on('hidden.bs.collapse', '.collapse', function(e){
		 
	 e.stopPropagation();
	 $(this).prev().find(".glyphicon-minus").first().removeClass("glyphicon-minus").addClass("glyphicon-plus");
  });
  
  </script>  
  
  
  
  <script>

  /*
  if ('${fromNavigate}'=== 'fromNavigate')
	  document.getElementById('refreshResults').click();
  */
  
  if ('${graphOrResultTabButton}'!= '')
	  document.getElementById('${graphOrResultTabButton}').click();
  else
	  document.getElementById('graphButton').click();
  
  
  // Should be called here, so that currAsName takes a value
  if (!filledAfterNavigation && '${prevASName}' !== '')
		fillInLabelsAfterNavigation();
  else
	  fillInLabels();
  
  
  // Search on clicking enter  in the search field
  // Get the input field
  var input = document.getElementById("search");

  // Execute a function when the user releases a key on the keyboard
  input.addEventListener("keyup", function(event) {
    // Cancel the default action, if needed
    event.preventDefault();
    // Number 13 is the "Enter" key on the keyboard
    if (event.keyCode === 13 && $('#theSearchButton').is(':enabled')) {
      // Trigger the button element with a click
    	clickNext(input);
    }
  });
  
  // prevent form submission on enter click
  $(document).ready(function() {
	  $(window).keydown(function(event){
	    if(event.keyCode == 13) {
	      event.preventDefault();
	      return false;
	    } 
	  });
	});
 
  
  </script>
  
  <script>
	 
  </script>
</html>

<!-- fix for dice values to show  + trimmedName + '_' -->