/**
 * Based on:
 * 
 * Modal Logic Playground -- application code
 * 
 * Dependencies: D3, MathJax, MPL
 * 
 * Copyright (c) 2013 Ross Kirsling Released under the MIT License.
 */

var clickEvent = new MouseEvent("click", {
	"view" : window,
	"bubbles" : true,
	"cancelable" : false
});

jQuery.fn.d3Click = function() {
	this.each(function(i, e) {
		var evt = new MouseEvent("click");
		e.dispatchEvent(evt);
	});
};

// app mode constants

// JAVASCRIPT (jQuery)

var MODE = {
	EDIT : 0,
	EVAL : 1
}, appMode = MODE.EDIT;

// set up initial MPL model (loads saved model if available, default otherwise)

var graphByClass = $('div[class=graph]');

var rect = $(graphByClass).position();

var graphX = rect.top;
var graphY = rect.left;

graphByClass.contextmenu(function(event) {
	event.preventDefault();
	event.stopPropagation();
});

// set up initial nodes and links (edges) of graph, based on MPL model
var lastNodeId = -1, nodes = [], links = [];

firstLoaded = true;

// Used to ensure that positioning nodes according to saved positions has ended
var done = 0;

// --> nodes setup
var nodes = $.parseJSON('${asjson}');
// alert('${asjson}');
// alert (nodes);
var links = JSON.parse('${nvjson}');

$('#nodesPositions').val('${nodesPositions}');
$('#transform').val('${transform}');
if ('${nodesPositions}' !== '') {
	// alert('${nodesPositions}');
	if (typeof ($('#nodesPositions').val()) !== 'undefined') {
		// alert('${nodesPositions}');
		var nodesPositions = $.parseJSON('${nodesPositions}');
		nodes.forEach(function(obj) {
			obj.x = nodesPositions[obj.id].x;
		});
		nodes.forEach(function(obj) {
			obj.y = nodesPositions[obj.id].y;
		});
		nodes.forEach(function(obj) {
			obj.fixed = true;
		});
		// force.stop();
	}
}

if ('${transform}' !== '') {
	// alert('${transform}');
	if (typeof ($('#transform').val()) !== 'undefined') {

		svg.attr('transform', '${transform}')
		$('.plotting-area').attr('transform', '${transform}');

	}
}

// set up SVG for D3

var width = 800, height = 700;// ,
colors = d3.scale.category20(), lightBlue = '#DEF7FF', green = '#00CF34',
		medBlue = '#4FD6FF';
darkBlue = '#111111';
darkGreen = '#660000';
azBlue = '#ff6666';
orange = '#076299';

var min_zoom = 0.3;
var max_zoom = 3;
var enlargedCircleOpa = 1.0;
var circleOpa = 1.0;

var zoom;

if ('${transform}' !== '') {

	firstLoaded = false;

	var str = '${transform}';

	// Slice with two parameters: return a new substring of the original [start,
	// end]
	var scTrans = str.slice(str.indexOf('translate(') + 'translate('.length,
			str.indexOf(') scale('));

	var splittedTrans = scTrans.split(',');
	var transX = parseFloat(splittedTrans[0]);
	var transY = parseFloat(splittedTrans[1]);

	var floatArrayForTrans = [ transX, transY ];

	var scScale = str.slice(str.indexOf('scale(') + 'scale('.length);
	// Removing the bracket at the end ')'
	scScale = scScale.substr(0, scScale.length - 1);
	var floatScale = parseFloat(scScale);

	console.log('Coming from session: translate and scale values: ' + transX
			+ ', ' + transY + ' ..... ' + floatArrayForTrans[0] + ', '
			+ floatArrayForTrans[1] + '  ----   ' + floatScale);

	zoom = d3.behavior.zoom().scaleExtent([ min_zoom, max_zoom ]);

	zoom.translate(floatArrayForTrans).scale(floatScale);
} else {
	zoom = d3.behavior.zoom().scaleExtent([ min_zoom, max_zoom ]);
}

var graph = d3.select('#app-body .graph').append('svg').attr('width', '100%')
		.attr('height', '100%');

var svg = graph.append("svg:g").attr("class", "plotting-area").attr("style",
		"width=600px; height=600px");

var defs = svg.append("defs");

// Pattern injection
defs.append("pattern").attr({
	id : "pinkTriangle",
	width : "10",
	height : "10",
	patternUnits : "userSpaceOnUse",
	patternTransform : "rotate(45)"
}).append("path").attr({
	d : "M5,0 10,10 0,10 Z",
	fill : azBlue
})

defs.append("pattern").attr({
	id : "blueTriangle",
	width : "10",
	height : "10",
	patternUnits : "userSpaceOnUse",
	patternTransform : "rotate(45)"
}).append("path").attr({
	d : "M5,0 10,10 0,10 Z",
	fill : orange
})

defs.append("pattern").attr({
	id : "BrownTriangle",
	width : "10",
	height : "10",
	patternUnits : "userSpaceOnUse",
	patternTransform : "rotate(45)"
}).append("path").attr({
	d : "M5,0 10,10 0,10 Z",
	fill : darkGreen
})

defs.append("pattern").attr({
	id : "blueRectangle",
	width : "8",
	height : "8",
	patternUnits : "userSpaceOnUse",
	patternTransform : "rotate(45)"
}).append("rect").attr({
	width : "4",
	height : "8",
	transform : "translate(0,0)",
	fill : orange
})

var pattern = defs.append("pattern").attr({
	id : "brownRectangle",
	width : "16",
	height : "16",
	background : orange,
	fill : orange,
	color : orange,
	patternUnits : "userSpaceOnUse",
	patternTransform : "rotate(0)"
});

var pat1 = pattern.append("rect").attr({
	width : "8",
	height : "8",
	transform : "translate(0,0)",
	fill : darkGreen
});

var pat2 = pattern.append("rect").attr({
	width : "8",
	height : "8",
	transform : "translate(8,0)",
	fill : '#FFFFFF'
});

var pat3 = pattern.append("rect").attr({
	width : "8",
	height : "8",
	transform : "translate(8,8)",
	fill : darkGreen
});

var pat4 = pattern.append("rect").attr({
	width : "8",
	height : "8",
	transform : "translate(0,8)",
	fill : '#FFFFFF'
});

var pattern1 = defs.append("pattern").attr({
	id : "brownYelloRectangle",
	width : "4",
	height : "4",
	background : orange,
	fill : orange,
	color : orange,
	patternUnits : "userSpaceOnUse",
	patternTransform : "rotate(0)"
});

var pat11 = pattern1.append("rect").attr({
	width : "3",
	height : "3",
	transform : "translate(0,0)",
	fill : darkGreen
});

var pat12 = pattern1.append("rect").attr({
	width : "1",
	height : "1",
	transform : "translate(3,0)",
	fill : '#FFFF00'
});

var pat13 = pattern1.append("rect").attr({
	width : "3",
	height : "3",
	transform : "translate(3,3)",
	fill : darkGreen
});

var pat14 = pattern1.append("rect").attr({
	width : "1",
	height : "1",
	transform : "translate(0,3)",
	fill : '#FFFF00'
});

var nodeWidth = 20;
var enlargedNodeWidth = 30;
var marg = 5;
var linkDistance = 250;

var fisheye = d3.fisheye.circular().radius(linkDistance);

var outerLink = svg.append('svg:g').selectAll(".link").data(links).enter()
		.append("g").attr("class", "link-group component");

var outVar = svg.selectAll(".link-group").data(links);

var linkText1 = outVar.append("text")

var linkText = outVar.append("text")

var link = outerLink.append("line");

var linkForText = outVar.append('svg:g').attr('class', 'tooltip').attr('name',
		function(d) {
			return d.id + "namesForLinkIDs";
		});

// define arrow markers for graph links
svg.append('svg:defs').append('svg:marker').attr('id', 'end-arrow').attr(
		'viewBox', '0 -5 10 10').attr('refX', 10).attr('markerWidth', 3).attr(
		'markerHeight', 3).attr('orient', 'auto').append('svg:path').attr('d',
		'M0,-5L10,0L0,5').attr('fill', darkBlue).style('opacity', 0.3);

// init D3 force layout
var force = d3.layout.force().nodes(nodes).links(links).size([ width, height ])
		.linkDistance(linkDistance).charge(-1500).gravity(0.01)
		// .alpha(0.013)
		.on('tick', tick)
		// .linkStrength(1.0)
		.start().on('end', function() {
			firstLoaded = false;
			console.log('ended!');
		});

var node_drag = d3.behavior.drag().on("dragstart", dragstart).on("drag",
		dragmove).on("dragend", dragend);

function dragstart(d, i) {
	onTooltipsOut();
	force.stop(); // stops the force auto positioning before you start
					// dragging
}

var cntr = 0;

function dragmove(d, i) {

	firstLoaded = false;
	console.log(d3.event);

	var coordinates = [ 0, 0 ];
	coordinates = d3.mouse(this.parentNode.parentNode);
	var x = coordinates[0];
	var y = coordinates[1];

	d.x = x;
	d.y = y;

	d.px = x;// d3.event.dx;
	d.py = y; // d3.event.dy;

	console.log(d);

	circle.selectAll('circle').each(function(d1) {
		// console.log(d1);
	});

	circle.attr(
			"cx",
			function(d) {

				if (d.x > 0) {
					// console.log('inner ticking');
					return d.x = Math.max(nodeWidth, Math.min(scaledWidth
							- nodeWidth, d.x));
				} else {
					// console.log('inner ticking');
					return d.x = Math.min(nodeWidth, Math.max(-scaledWidth
							+ nodeWidth, d.x));
				}
			}).attr(
			"cy",
			function(d) {

				if (d.y > 0) {
					return d.y = Math.max(nodeWidth, Math.min(scaledHeight
							- nodeWidth, d.y));
				} else {
					return d.y = Math.min(nodeWidth, Math.max(-scaledHeight
							+ nodeWidth, d.y));
				}

			});

	circle.attr('transform', function(d) {
		// console.log('transformation coordinates: ' + d.x + ',' + d.y);
		return 'translate(' + d.x + ',' + d.y + ')';

	});

	path
			.attr(
					'd',
					function(d) {
						var deltaX = d.target.x - d.source.x, deltaY = d.target.y
								- d.source.y, dist = Math.sqrt(deltaX * deltaX
								+ deltaY * deltaY), normX = deltaX / dist, normY = deltaY
								/ dist, sourcePadding = d.left ? 17 : 12, targetPadding = d.right ? 17
								: 12, sourceX = d.source.x
								+ (sourcePadding * normX), sourceY = d.source.y
								+ (sourcePadding * normY), targetX = d.target.x
								- (targetPadding * normX), targetY = d.target.y
								- (targetPadding * normY);
						return 'M' + sourceX + ',' + sourceY + 'L' + targetX
								+ ',' + targetY;
					});

	linkForText.attr('transform', function(d) {
		return 'translate(' + (d.source.x + (d.target.x - d.source.x) * 0.5)
				+ ',' + (d.source.y + (d.target.y - d.source.y) * 0.5) + ')';
	});

	linkText.attr("x", function(d) {
		return (d.source.x + (d.target.x - d.source.x) * 0.5);
	}).attr("y", function(d) {
		return (d.source.y + (d.target.y - d.source.y) * 0.5);
	});

	linkText1.attr("x", function(d) {
		return (d.source.x + (d.target.x - d.source.x) * 0.5);
	}).attr("y", function(d) {
		return (d.source.y + (d.target.y - d.source.y) * 0.5);
	});

	// dragTick(d);

}

function dragend(d, i) {

	circle.selectAll('circle').each(function(d1) {
		d1.fixed = true;
	});
	d.fixed = true; // of course set the node to fixed so the force doesn't
					// include the node in its auto positioning stuff
	force.resume();
}

function releasenode(d) {

	d.fixed = false; // of course set the node to fixed so the force doesn't
						// include the node in its auto positioning stuff
	// force.resume();
}

var selectedNodeLabel = d3.select('#edit-pane .selected-node-id');

// handles to link and node element groups

var path = svg.append('svg:g').selectAll('path'), circle = svg.append('svg:g')
		.selectAll('g');

var scaledWidth = width * 2.0;
var scaledHeight = height * 2.0;
var totalZoom = 1;
var oldZoom = 1;

var firstZoom = false;

var prevZoomFactor = 1;
var prevD3EventScale = 1

zoom.on("zoom", function() {
	console.log('translate and scale values: ' + d3.event.translate
			+ '  ----   ' + d3.event.scale);

	svg.attr("transform", "translate(" + d3.event.translate + ")" + " scale("
			+ d3.event.scale + ")");

	$('.plotting-area').attr(
			'transform',
			"translate(" + d3.event.translate + ")" + " scale("
					+ d3.event.scale + ")");

	svg.on("mousemove", function() {
	});
});

graph.call(zoom);

// mouse event vars
var selected_node = null, selected_link = null, mousedown_link = null, mousedown_node = null, mouseup_node = null;
dummy_selected_node = null;

function resetMouseVars() {
	mousedown_node = null;
	mouseup_node = null;
	mousedown_link = null;
}

// mouse click
// set selected node and notify panel of changes
function setSelectedNode(node) {

	if (node !== null) {
		var elemName = node.id + "namesForNodeIDs";
		console.log('elem name:' + elemName);
		$('[name=\'' + elemName + '\']').css('visibility', 'hidden');
		onTooltipsOut();
	}

	selected_node = node;

	// update selected node label
	selectedNodeLabel
			.html(selected_node ? '<strong>Analysis Situation '
					+ selected_node.id + '</strong>'
					: 'No Analysis Situation selected');
	if (node != null) {
		selected_link = null;

		var currASAbbName = $('input[name=currASAbbName]').val();

		console.log('in out');

		if (checkValueIsOk(currASAbbName)) {

			console.log('in in 1');

			$('input[name=currASAbbName]').trigger('click');

			asArray[currASAbbName][0] = $('#as_section1').html();
			asArray[currASAbbName][1] = $('[name=as_section2]').html();

			ajaxSubmitAs();

		}

		var currNVAbbName = $('input[name=currNVAbbName]').val();

		if (checkValueIsOk(currNVAbbName)) {

			console.log('in in 2');

			$('input[name=currNVAbbName]').trigger('click');

			nvArray[currNVAbbName][0] = $('#as_section1').html();
			nvArray[currNVAbbName][1] = $('[name=as_section2]').html();

			ajaxSubmitNv();

		}

		fillASDiv(node.name);
	}

	setAppMode(MODE.EDIT, 0);

	path.style('stroke', function(l) {
		if (node === l.source || node === l.target) {
			return '';
		} else
			return '';
	});

	path.classed('pointed', function(l) {
		if (node === l.source || node === l.target) {
			return true;
		} else
			return false;
	});

	// update variable table
	if (selected_node) {

	}
}

function makePathsText(path) {
	return path.left;
}

// get truth assignment for node as a displayable string
function makeAssignmentString(node) {
	return node.label;
}

function doPathsRender(path, tempPath) {

	path.classed('selected', function(d) {

		return d === selected_link;
	}).style('marker-start', function(d) {
		return d.left ? 'url(#start-arrow)' : '';
	}).style('marker-end', function(d) {
		return d.right ? 'url(#end-arrow)' : '';
	});

	tempPath.attr('class', 'link').attr('id', function(d) {
		return 'path' + d.id;
	}).classed('selected', function(d) {
		return d === selected_link;
	}).style('stroke', function(d) {
		if (d === selected_link)
			return 'url(#brownYelloRectangle)'
		else
			return '';
	}).style('marker-start', function(d) {
		return d.left ? 'url(#start-arrow)' : '';
	}).style('marker-end', function(d) {
		return d.right ? 'url(#end-arrow)' : '';
	}).on('mouseover', function(d) {
		if (/* appMode !== MODE.EDIT || *//* d === selected_link */false)
			return;
		// enlarge target node
		d3.select(this).classed('enlarged', true);
		d3.select(this).style('opacity', enlargedCircleOpa);
		var elemName = d.id + "namesForLinkIDs";
		console.log('elem name:' + elemName);
		$('[name=\"' + elemName + '\"]').css('visibility', 'visible');

		// d3.select(this.parent).moveToFront();

		// document.getElementsByName(d.id +
		// 'namesForLinkIDs')[0].style.visibility = "visible" ;
		console.log('hover 1');
		onTooltipsHover();
	}).on('mouseout', function(d) {
		if (/* appMode !== MODE.EDIT || *//* d === selected_link */false)
			return;
		// smallen target node
		onTooltipsOut();
		d3.select(this).classed('enlarged', false);
		d3.select(this).style('opacity', circleOpa);
		// $('name=' + d.id + 'namesForLinkIDs').hide();
		var elemName = d.id + "namesForLinkIDs";
		console.log('elem name:' + elemName);
		$('[name=\'' + elemName + '\']').css('visibility', 'hidden');
		// document.getElementsByName(d.id +
		// 'namesForLinkIDs')[0].style.visibility = "hidden" ;

	}).text(function(d) {
		return d.label;
	})

	.append("svg:title")

	.text(function(d) {

	});

	if (selected_link !== null) {
		fillNVDivWrapper(selected_link.name);
	}

	// remove old links
	path.exit().remove();

}

var globalMode = 0

function simpleRestart() {

	// console.log("simple restart");
	// path (link) group
	path = path.data(links);

	// add new links
	var tempPath = path.enter().append('svg:path');
	var tempV = false;
	tempPath.on('mousedown', function(d) {
		linkMouseDown(d, 0);
	})

	function linkMouseDown(d, typeOfCall) {

		if (typeOfCall === 0) {
			if (/* appMode !== MODE.EDIT || */d3.event.ctrlKey)
				return;
		}

		// hiding tooltips
		var elemName = d.id + "namesForLinkIDs";
		console.log('elem name:' + elemName);
		$('[name=\'' + elemName + '\']').css('visibility', 'hidden');
		onTooltipsOut();

		// select link
		mousedown_link = d;
		// if(mousedown_link === selected_link) selected_link = null;
		// else {

		selected_link = mousedown_link;
		// update existing links
		// }
		setSelectedNode(null);

		circle = circle.data(nodes, function(d) {
			return d.id;
		});

		// update existing nodes (reflexive & selected visual states)
		circle.selectAll('circle')
		// .style('fill', azBlue)
		.attr('r', nodeWidth).append("svg:title")
		// .style('fill', azBlue)
		// .attr("class", "tip")
		.text(function(d) {
			// return 'asdasd adsa das das das';
		}).classed('reflexive', function(d) {
			return d.reflexive;
		})
		// .on("dblclick", dblclick)
		.style('fill', function(d) {
			if (selected_link != null) {
				if (selected_link.source === d || selected_link.target === d) {
					console.log("oranging A");
					return orange;
				} else {
					console.log("azBluing 1 A");
					return azBlue;
				}
			} else {
				console.log("azBluing 2 A");
				return azBlue;
			}
		}).style('opacity', function(d) {
			return circleOpa
		}).call(node_drag);

		doPathsRender(path, tempPath);
		tempV = true;
		// update existing links
	}

	// if (!tempV)
	doPathsRender(path, tempPath);

	// circle (node) group
	// NB: the function arg is crucial here! nodes are known by id, not by
	// index!
	circle = circle.data(nodes, function(d) {
		return d.id;
	});

	// update existing nodes (reflexive & selected visual states)
	circle.selectAll('circle').style('fill', function(d) {
		if (selected_link != null) {
			if (selected_link.source === d || selected_link.target === d) {
				console.log("oranging B");
				return orange
			} else {
				console.log("azBlue 1 B");
				return azBlue;
			}
		} else {
			if (d === selected_node) {
				console.log("dark greening D");
				return 'url(#brownRectangle)';
			}
			// return d3.rgb(colors(d.id)).brighter().toString()
			else {
				console.log("azBlue 2 D");
				return azBlue;
			}
		}
	}).style('opacity', function(d) {
		if (d === selected_node)
			return enlargedCircleOpa;
		// return d3.rgb(colors(d.id)).brighter().toString()
		else
			return circleOpa;
	}).attr('r', function(d) {
		if (d === selected_node)
			return enlargedNodeWidth;
		else
			return nodeWidth;
	}).append("svg:title")
	// .style('fill', azBlue)
	// .attr("class", "tip")
	.text(function(d) {

		// return 'dasasd asdasd asd asd asdas ';
	}).classed('reflexive', function(d) {
		return d.reflexive;
	})

	// add new nodes
	var g = circle.enter().append('svg:g');

	g.attr('class', 'component');

	// g.append('svg:rect')
	g
			.append('svg:circle')
			.attr('class', 'node')
			// for the tooltips
			.attr('id', function(d) {
				return d.id;
			})
			// .attr("width", nodeWidth)
			// .attr("height", nodeWidth)
			.call(node_drag)
			.attr('r', nodeWidth)
			.attr("x", function(d) {
				return d.x;
			})
			.attr("y", function(d) {
				return d.y;
			})
			.style(
					'fill',
					function(d) {
						if (selected_link != null) {
							if (selected_link.source === d
									|| selected_link.target === d) {
								console.log("orange");
								return orange;
							} else {
								console.log("azBlue 1");
								return azBlue;
							}
						} else {
							if (d === selected_node) {
								console.log("dark greening XXX");
								return 'url(#brownRectangle)';
							}
							// return d3.rgb(colors(d.id)).brighter().toString()
							else {
								console.log("az bluing XXX");
								return azBlue;
							}
						}
						// return (d === selected_node) ?
						// /*d3.rgb(colors(d.id)).brighter().toString()*/
						// darkGreen : azBlue;
					}

			)
			.style('opacity', function(d) {
				if (d === selected_node)
					return enlargedCircleOpa;
				// return d3.rgb(colors(d.id)).brighter().toString()
				else
					return circleOpa;
			})
			.attr('r', function(d) {
				if (d === selected_node)
					return enlargedNodeWidth;
				else
					return nodeWidth;
			})
			.style('stroke', function(d) {
				return d3.rgb(colors(d.id)).darker().toString();
			})
			.classed('reflexive', function(d) {
				return d.reflexive;
			})

			// when moving mouse over the target node that the is the target of
			// the drag
			.on(
					'mouseover',
					function(d) {
						if (/* appMode !== MODE.EDIT || *//* !mousedown_node || */false /*
																						 * d
																						 * ===
																						 * mousedown_node
																						 */)
							return;
						// enlarge target node
						// not modifying the size of the selected node
						if (d !== mousedown_node) {
							d3.select(this).attr('r', enlargedNodeWidth);
							d3.select(this).style('opacity', enlargedCircleOpa);
						}
						console.log('hover 2');
						var elemName = d.id + "namesForNodeIDs";
						console.log('elem name:' + elemName);
						$('[name=\'' + elemName + '\']').css('visibility',
								'visible');
						onTooltipsHover();
					})
			.on(
					'mouseout',
					function(d) {

						if (/* appMode !== MODE.EDIT || *//* !mousedown_node || */false /*
																						 * d
																						 * ===
																						 * mousedown_node
																						 */)
							return;
						onTooltipsOut();
						// smallen target node
						// not modifying the size of the selected node
						if (d !== mousedown_node) {
							d3.select(this).attr('r', nodeWidth);
							d3.select(this).style('opacity', circleOpa);
						}
						var elemName = d.id + "namesForNodeIDs";
						console.log('elem name:' + elemName);
						$('[name=\'' + elemName + '\']').css('visibility',
								'hidden');

					})
			/*
			 * .on('mouseout', function(d) { if(appMode !== MODE.EDIT ||
			 * !mousedown_node || d === mousedown_node) return; // unenlarge
			 * target node d3.select(this).attr('transform', ''); })
			 */
			.on('mousedown', function(d) {
				console.log("Downing");
				if (/* appMode !== MODE.EDIT || */d3.event.ctrlKey)
					return;
				d3.event.stopPropagation();
				// select node
				mousedown_node = d;
				// if(mousedown_node === selected_node) setSelectedNode(null);
				// else
				setSelectedNode(mousedown_node);
				console.log("Nulling selected Link");

				// restart();
			})

	// show node IDs
	g.append('svg:text').attr('x', 0).attr('y', 4).attr('class', 'id')
	// .text(function(d) { return d.id; })
	;

	// text shadow
	g.append('svg:text').attr('x', nodeWidth + marg).attr('y', 0).attr('class',
			'shadow').text(makeAssignmentString);

	// text foreground
	g.append('svg:text').attr('x', nodeWidth + marg).attr('y', 0).attr('class',
			'front').text(makeAssignmentString);

	// remove old nodes
	circle.exit().remove();

	var gTitle = g.append('svg:g').attr('class', 'tooltip').attr('name',
			function(d) {
				return d.id + "namesForNodeIDs";
			});

	gTitle.append('svg:rect').attr('rx', 20).attr('x', 25) // '1.2em'); )
	.attr('y', 25).attr('width', 300).attr('height', 100);

	var titleText = gTitle.append('text').attr('x', 35).attr('y', 35);

	// findRemainingDistanceToGraphEdgesXAndY(g.attr('cx'), g.attr('cy'));

	titleText.each(function(d) {

		// console.log('parent: ' + this.parentNode.parentNode.attr('cx'));
		// console.log('parent: ' + d.parentNode.parentNode.attr('cx'));

		var a = d.summary.split("</br>"), i;
		for (i = 0; i < a.length; i++) {
			var tSpan = document.createElement('tspan');
			$(tSpan).attr('x', '35');
			$(tSpan).attr('dy', '1.2em');
			tSpan.innerHTML = a[i];
			// var tempElm = document.createElement('br');
			// v.append(tempElm);
			this.innerHTML += tSpan.outerHTML;
		}
	});

	linkForText.append('svg:rect').attr('rx', 20)
	// .attr('x', 25) //'1.2em'); )
	// .attr('y', 25)
	.attr('width', 300).attr('height', 100);

	linkForText.append('text').attr('x', 35).attr('y', 35).each(function(d) {

		var a = d.summary.split("</br>"), i;
		for (i = 0; i < a.length; i++) {
			var tSpan = document.createElement('tspan');
			if (i == 0) {
				$(tSpan).attr('x', '5');
				$(tSpan).attr('dy', '-1.2em');
			} else {
				$(tSpan).attr('x', '5');
				$(tSpan).attr('dy', '1.2em');
			}
			tSpan.innerHTML = a[i];
			// var tempElm = document.createElement('br');
			// v.append(tempElm);
			this.innerHTML += tSpan.outerHTML;
		}
	});

	linkText1.attr('class', 'shadow').text(function(d) {
		return d.label;
	}).attr("text-anchor", "middle")
	// .text(function(d) { return d.name; })
	;

	linkText.attr('class', 'navigation').text(function(d) {
		return d.label;
	}).attr("text-anchor", "middle")
	// .text(function(d) { return d.name; })
	;

	// set the graph in motion
	// force.start();
	// force.stop();

}

// =========================================================================
// =========================================================================
/*
 * Here updating the whole graph takes place All the nodes/edges information are
 * reloaded It is only called when required simpleRestart is called instead when
 * clicks on nodes or edges are done
 */

function restart() {

	// path (link) group
	path = path.data(links);

	// add new links
	var tempPath = path.enter().append('svg:path');
	var tempV = false;
	tempPath.on('mousedown', function(d) {
		linkMouseDown(d, 0);
	})

	function linkMouseDown(d, typeOfCall) {
		if (typeOfCall === 0) {
			if (/* appMode !== MODE.EDIT || */d3.event.ctrlKey)
				return;
		}

		// hiding tooltips
		var elemName = d.id + "namesForLinkIDs";
		console.log('elem name:' + elemName);
		$('[name=\'' + elemName + '\']').css('visibility', 'hidden');
		onTooltipsOut();

		// select link
		mousedown_link = d;
		// if(mousedown_link === selected_link) selected_link = null;
		// else {

		selected_link = mousedown_link;
		// update existing links
		// }
		setSelectedNode(null);

		circle = circle.data(nodes, function(d) {
			return d.id;
		});

		// update existing nodes (reflexive & selected visual states)
		circle.selectAll('circle')
		// .style('fill', azBlue)
		.attr('r', nodeWidth).append("svg:title")
		// .style('fill', azBlue)
		// .attr("class", "tip")
		.text(function(d) {
			// return 'ttttttttttttttt sdlnklasm;las, d;las,d /.as,d/.as,
			// d/.asd,/.asd,/. asd,/.as';
		}).classed('reflexive', function(d) {
			return d.reflexive;
		})
		// .on("dblclick", dblclick)
		.style('fill', function(d) {
			if (selected_link != null) {
				if (selected_link.source === d || selected_link.target === d) {
					console.log("oranging C");
					return azBlue;
				} else {
					console.log("az bluing 1 C");
					return orange;
				}
			} else {
				return azBlue;
			}
		}).style('opacity', function(d) {
			return circleOpa
		}).call(node_drag);

		doPathsRender(path, tempPath);
		tempV = true;
		// update existing links
	}

	// if (!tempV)
	doPathsRender(path, tempPath);

	// circle (node) group
	// NB: the function arg is crucial here! nodes are known by id, not by
	// index!
	circle = circle.data(nodes, function(d) {
		return d.id;
	});

	// update existing nodes (reflexive & selected visual states)
	circle.selectAll('circle').style('fill', function(d) {
		if (selected_link != null) {
			if (selected_link.source === d || selected_link.target === d) {
				console.log("oranging D");
				return orange;
			} else {
				return azBlue;
				console.log("az bluing 1 D");
			}
		} else {
			if (d === selected_node) {
				console.log("dark greening D");
				return 'url(#brownRectangle)';
			}
			// return d3.rgb(colors(d.id)).brighter().toString()
			else {
				console.log("azBlue 2 D");
				return azBlue;
			}
		}
	}).style('opacity', function(d) {
		if (d === selected_node)
			return enlargedCircleOpa;
		// return d3.rgb(colors(d.id)).brighter().toString()
		else
			return circleOpa;
	}).attr('r', function(d) {
		if (d === selected_node)
			return enlargedNodeWidth;
		else
			return nodeWidth;
	}).append("svg:title")

	// .style('fill', azBlue)
	// .attr("class", "tip")
	.text(function(d) {
		// return 'wfew ;dk;lewdk ;lewkd ;lewkd;lew kd;lewk ;le';

	}).classed('reflexive', function(d) {
		return d.reflexive;
	})

	// add new nodes
	var g = circle.enter().append('svg:g');

	g.attr('class', 'component');

	// g.append('svg:rect')
	g
			.append('svg:circle')
			.attr('class', 'node')
			// for the tooltips
			.attr('id', function(d) {
				return d.id;
			})
			// .attr("width", nodeWidth)
			// .attr("height", nodeWidth)
			.call(node_drag)
			.attr('r', nodeWidth)
			.attr("x", function(d) {
				return d.x;
			})
			.attr("y", function(d) {
				return d.y;
			})
			.style(
					'fill',
					function(d) {
						if (selected_link != null) {
							if (selected_link.source === d
									|| selected_link.target === d) {
								console.log("oranging E");
								return orange;
							} else {
								console.log("az bluing E");
								return azBlue;
							}
						} else {
							if (d === selected_node) {
								console.log("dark greening D");
								return 'url(#brownRectangle)';
							}
							// return d3.rgb(colors(d.id)).brighter().toString()
							else {
								console.log("azBlue 2 D");
								return azBlue;
							}
						}
						// return (d === selected_node) ?
						// /*d3.rgb(colors(d.id)).brighter().toString()*/
						// darkGreen : azBlue;
					}

			)
			.style('opacity', function(d) {
				if (d === selected_node)
					return enlargedCircleOpa;
				// return d3.rgb(colors(d.id)).brighter().toString()
				else
					return circleOpa;
			})
			.attr('r', function(d) {
				if (d === selected_node)
					return enlargedNodeWidth;
				else
					return nodeWidth;
			})
			.style('stroke', function(d) {
				return d3.rgb(colors(d.id)).darker().toString();
			})
			.classed('reflexive', function(d) {
				return d.reflexive;
			})

			// when moving mouse over the target node that the is the target of
			// the drag
			.on(
					'mouseover',
					function(d) {
						if (/* appMode !== MODE.EDIT || *//* !mousedown_node || */false /*
																						 * d
																						 * ===
																						 * mousedown_node
																						 */)
							return;
						// enlarge target node

						// not modifying the size of the selected node
						if (d !== mousedown_node) {
							d3.select(this).attr('r', enlargedNodeWidth);
							d3.select(this).style('opacity', enlargedCircleOpa);
						}

						// d3.select(this).moveToFront();

						var elemName = d.id + "namesForNodeIDs";
						console.log('elem name:' + elemName);
						$('[name=\'' + elemName + '\']').css('visibility',
								'visible');

						onTooltipsHover();
					})
			.on(
					'mouseout',
					function(d) {
						if (/* appMode !== MODE.EDIT || *//* !mousedown_node || */false /*
																						 * d
																						 * ===
																						 * mousedown_node
																						 */)
							return;
						// smallen target node
						onTooltipsOut();
						// not modifying the size of the selected node
						if (d !== mousedown_node) {
							d3.select(this).attr('r', nodeWidth);
							d3.select(this).style('opacity', circleOpa);
						}
						var elemName = d.id + "namesForNodeIDs";
						console.log('elem name:' + elemName);
						$('[name=\'' + elemName + '\']').css('visibility',
								'hidden');

					})
			/*
			 * .on('mouseout', function(d) { if(appMode !== MODE.EDIT ||
			 * !mousedown_node || d === mousedown_node) return; // unenlarge
			 * target node d3.select(this).attr('transform', ''); })
			 */
			.on('mousedown', function(d) {
				if (/* appMode !== MODE.EDIT || */d3.event.ctrlKey)
					return;
				d3.event.stopPropagation();
				// select node
				mousedown_node = d;
				// if(mousedown_node === selected_node) setSelectedNode(null);
				// else
				setSelectedNode(mousedown_node);
				selected_link = null;
				// restart();
			})

	// show node IDs
	g.append('svg:text').attr('x', 0).attr('y', 4).attr('class', 'id')
	// .text(function(d) { return d.id; })
	;

	// text shadow
	g.append('svg:text').attr('x', nodeWidth + marg).attr('y', 0).attr('class',
			'shadow').text(makeAssignmentString);

	// text foreground
	g.append('svg:text').attr('x', nodeWidth + marg).attr('y', 0).attr('class',
			'front').text(makeAssignmentString);

	// remove old nodes
	circle.exit().remove();

	var gTitle = g.append('svg:g').attr('class', 'tooltip').attr('name',
			function(d) {
				return d.id + "namesForNodeIDs";
			});

	gTitle.append('svg:rect').attr('rx', 20).attr('x', 25) // '1.2em'); )
	.attr('y', 25).attr('width', 300).attr('height', 100);

	var titleText = gTitle.append('text').attr('x', 35).attr('y', 35);

	titleText.each(function(d) {

		var a = d.summary.split("</br>"), i;
		for (i = 0; i < a.length; i++) {
			var tSpan = document.createElement('tspan');
			$(tSpan).attr('x', '35');
			$(tSpan).attr('dy', '1.2em');
			tSpan.innerHTML = a[i];
			// var tempElm = document.createElement('br');
			// v.append(tempElm);
			this.innerHTML += tSpan.outerHTML;
		}
	});

	// Links context menu
	path.each(function(d) {

		var itmId = d.id;
		var itmClass = "link"; // d.className;
		var itmIdAndClass = "#path" + itmId + "." + itmClass;

		var cntxtArr = [];

		var objToPush = {
			name : 'ACTIONS',
			disable : true
		};
		cntxtArr.push(objToPush)

		var obj = {
			name : "Navigate to target analysis situation",
			img : 'img/index.png',
			fun : function(key, options) {

				// var lnkId = value.id;
				submitForm("navigationStep");
				// submitFormByName('navigationStep', value.target.uri);
			}
		};

		cntxtArr.push(obj);

		$(itmIdAndClass).contextmenu(function(event) {
			event.preventDefault();
			event.stopPropagation();
		});

		$(itmIdAndClass).contextMenu(cntxtArr, {
			triggerOn : 'click',
			mouseClick : 'right'
		});

		$(itmIdAndClass).contextmenu(function(event) {
			event.preventDefault();
			event.stopPropagation();
		});

	});

	linkForText.append('svg:rect').attr('rx', 20)
	// .attr('x', 25) //'1.2em'); )
	// .attr('y', 25)
	.attr('width', 300).attr('height', 100);

	linkForText.append('text').attr('x', 35).attr('y', 35).each(function(d) {

		var a = d.summary.split("</br>"), i;
		for (i = 0; i < a.length; i++) {
			var tSpan = document.createElement('tspan');
			if (i == 0) {
				$(tSpan).attr('x', '5');
				$(tSpan).attr('dy', '-1.2em');
			} else {
				$(tSpan).attr('x', '5');
				$(tSpan).attr('dy', '1.2em');
			}
			tSpan.innerHTML = a[i];
			// var tempElm = document.createElement('br');
			// v.append(tempElm);
			this.innerHTML += tSpan.outerHTML;
		}
	});

	linkText1.attr('class', 'shadow').text(function(d) {
		return d.label;
	}).attr("text-anchor", "middle")
	// .text(function(d) { return d.name; })
	;

	linkText.attr('class', 'navigation').text(function(d) {
		return d.label;
	}).attr("text-anchor", "middle")
	// .text(function(d) { return d.name; })
	;
}

// =========================================================================
// =========================================================================

function getPosition() {
	return JSON.stringify(force.nodes().map(function(node) {
		return {
			x : node.x,
			y : node.y
		}
	}));
}

function getGPlottingAreaTransform() {
	return $('.plotting-area').attr('transform');
}

force.on('end', function() {

});

console.log('nodesPositions: ' + '${nodesPositions}');

function doTicking() {

	force.resume();
	for (var i = 0; i < 500; i++) {
		force.tick();
	}
	force.stop();

}

var newCounter = 0;
var tickCounter = 0;

function tick() {

	// ensuring end function is only applied when loading the page for the first
	// time
	if (checkValueIsOk('${nodesPositions}') // there are sessions values
			/* && typeof($('#nodesPositions').val()) !== 'undefined' */
			// $('#nodesPositions') and $("#transform") may be undefined because
			// there is no selected as or nv yet!
			&& tickCounter < 5) // I think tickCounter can be limited to 1
								// instead of 5...
	{
		tickCounter++;

		// console.log("Section 1 -- tickCounter: " + tickCounter)

		force.resume();

		// done = 2;
		// alert("in end stuff");
		// console.log("doing end");

		$('#nodesPositions').val('${nodesPositions}')
		$("#transform").val('${transform}');

		if ('${nodesPositions}' !== '') {
			console.log("Section 1 -- tickCounter: AAAAAAA" + tickCounter);
			// if (typeof($('#nodesPositions').val()) !== 'undefined'){
			// console.log("Section 1 -- tickCounter: BBBBBBBB" + tickCounter);
			var nodesPositions = $.parseJSON('${nodesPositions}');
			nodes.forEach(function(obj) {
				obj.x = nodesPositions[obj.id].x;
			});
			nodes.forEach(function(obj) {
				obj.y = nodesPositions[obj.id].y;
			});
			// console.log("doing inside");
			// force.stop();
			// }

		}

		if ('${transform}' !== '') {
			// console.log("Section 1 -- tickCounter: DDDDDDDDD" + tickCounter);
			// alert('${transform}');
			// if (typeof($('#transform').val()) !== 'undefined'){
			// console.log("Section 1 -- tickCounter: EEEEEE" + tickCounter);
			svg.attr('transform', '${transform}');
			$('.plotting-area').attr('transform', '${transform}');

			// }
		}

		// Circles must be positioned before links and their texts, otherwise
		// some problems might happen
		// This couple of lines enable to show the ticking while it happens
		// console.log("Section 1 -- tickCounter: FFFFFFF" + tickCounter);

		circle.attr('transform', function(d) {
			return 'translate(' + d.x + ',' + d.y + ')';
		});

		linkForText.attr('transform', function(d) {
			return 'translate('
					+ (d.source.x + (d.target.x - d.source.x) * 0.5) + ','
					+ (d.source.y + (d.target.y - d.source.y) * 0.5) + ')';
		});

		linkText
		// .attr("x", function(d) { return (Math.min(d.source.x, d.target.x) +
		// Math.abs(d.target.x - d.source.x) * 0.1); })
		.attr("x", function(d) {
			return (d.source.x + (d.target.x - d.source.x) * 0.5);
		}).attr("y", function(d) {
			return (d.source.y + (d.target.y - d.source.y) * 0.5);
		});

		linkText1
		// .attr("x", function(d) { return (Math.min(d.source.x, d.target.x) +
		// Math.abs(d.target.x - d.source.x) * 0.1); })
		.attr("x", function(d) {
			return (d.source.x + (d.target.x - d.source.x) * 0.5);
		}).attr("y", function(d) {
			return (d.source.y + (d.target.y - d.source.y) * 0.5);
		});

		// draw directed edges with proper padding from node centers
		path
				.attr(
						'd',
						function(d) {
							var deltaX = d.target.x - d.source.x, deltaY = d.target.y
									- d.source.y, dist = Math.sqrt(deltaX
									* deltaX + deltaY * deltaY), normX = deltaX
									/ dist, normY = deltaY / dist, sourcePadding = d.left ? 17
									: 12, targetPadding = d.right ? 17 : 12, sourceX = d.source.x
									+ (sourcePadding * normX), sourceY = d.source.y
									+ (sourcePadding * normY), targetX = d.target.x
									- (targetPadding * normX), targetY = d.target.y
									- (targetPadding * normY);
							return 'M' + sourceX + ',' + sourceY + 'L'
									+ targetX + ',' + targetY;
						});

	} else {
		if (tickCounter == 5) {
			force.stop();
			tickCounter++;
			// console.log("Section 2 -- tickCounter: " + tickCounter)
			return;
		}
		if (!checkValueIsOk('${nodesPositions}') // No values saved in
													// session
				|| tickCounter > 5 // I do not know why is this
		/*
		 * || ('${nodesPositions}' != '' && typeof($('#nodesPositions').val())
		 * === 'undefined')
		 */
		) {
			newCounter++;

			// Circles must be positioned before links and their texts,
			// otherwise some problems might happen
			// This couple of lines enable to show the ticking while it happens
			circle.attr(
					"cx",
					function(d) {

						if (firstLoaded) {
							// console.log('outer ticking' + d.x);
							return d.x = Math.max(nodeWidth, Math.min(width
									- nodeWidth, d.x));
						} else {
							if (d.x > 0) {
								// console.log('inner ticking1111' + d.x);
								return d.x = Math.max(nodeWidth, Math.min(
										scaledWidth - nodeWidth, d.x));
							} else {
								// console.log('inner ticking2222' + d.x);
								return d.x = Math.min(nodeWidth, Math.max(
										-scaledWidth + nodeWidth, d.x));
							}
						}
					}).attr(
					"cy",
					function(d) {
						if (firstLoaded)
							return d.y = Math.max(nodeWidth, Math.min(height
									- nodeWidth, d.y));
						else {
							if (d.y > 0) {
								return d.y = Math.max(nodeWidth, Math.min(
										scaledHeight - nodeWidth, d.y));
							} else {
								return d.y = Math.min(nodeWidth, Math.max(
										-scaledHeight + nodeWidth, d.y));
							}
						}
					});

			circle.attr('transform', function(d) {
				return 'translate(' + d.x + ',' + d.y + ')';

			});

			// draw directed edges with proper padding from node centers
			path
					.attr(
							'd',
							function(d) {
								var deltaX = d.target.x - d.source.x, deltaY = d.target.y
										- d.source.y, dist = Math.sqrt(deltaX
										* deltaX + deltaY * deltaY), normX = deltaX
										/ dist, normY = deltaY / dist, sourcePadding = d.left ? 17
										: 12, targetPadding = d.right ? 17 : 12, sourceX = d.source.x
										+ (sourcePadding * normX), sourceY = d.source.y
										+ (sourcePadding * normY), targetX = d.target.x
										- (targetPadding * normX), targetY = d.target.y
										- (targetPadding * normY);
								return 'M' + sourceX + ',' + sourceY + 'L'
										+ targetX + ',' + targetY;
							});

			linkForText.attr('transform', function(d) {
				return 'translate('
						+ (d.source.x + (d.target.x - d.source.x) * 0.5) + ','
						+ (d.source.y + (d.target.y - d.source.y) * 0.5) + ')';
			});

			linkText.attr("x", function(d) {
				return (d.source.x + (d.target.x - d.source.x) * 0.5);
			}).attr("y", function(d) {
				return (d.source.y + (d.target.y - d.source.y) * 0.5);
			});

			linkText1.attr("x", function(d) {
				return (d.source.x + (d.target.x - d.source.x) * 0.5);
			}).attr("y", function(d) {
				return (d.source.y + (d.target.y - d.source.y) * 0.5);
			});

		}
	}
	if (newCounter > 1000) {
		firstLoaded = false;

	}

}

function dragTick(d) {

	circle.attr('transform', function(d) {
		console.log('transformation coordinates: ' + d.x + ',' + d.y);
		return 'translate(' + d.x + ',' + d.y + ')';

	});

	// draw directed edges with proper padding from node centers
	path
			.attr(
					'd',
					function(d) {
						var deltaX = d.target.x - d.source.x, deltaY = d.target.y
								- d.source.y, dist = Math.sqrt(deltaX * deltaX
								+ deltaY * deltaY), normX = deltaX / dist, normY = deltaY
								/ dist, sourcePadding = d.left ? 17 : 12, targetPadding = d.right ? 17
								: 12, sourceX = d.source.x
								+ (sourcePadding * normX), sourceY = d.source.y
								+ (sourcePadding * normY), targetX = d.target.x
								- (targetPadding * normX), targetY = d.target.y
								- (targetPadding * normY);
						return 'M' + sourceX + ',' + sourceY + 'L' + targetX
								+ ',' + targetY;
					});

	linkForText.attr('transform', function(d) {
		return 'translate(' + (d.source.x + (d.target.x - d.source.x) * 0.5)
				+ ',' + (d.source.y + (d.target.y - d.source.y) * 0.5) + ')';
	});

	linkText.attr("x", function(d) {
		return (d.source.x + (d.target.x - d.source.x) * 0.5);
	}).attr("y", function(d) {
		return (d.source.y + (d.target.y - d.source.y) * 0.5);
	});

	linkText1.attr("x", function(d) {
		return (d.source.x + (d.target.x - d.source.x) * 0.5);
	}).attr("y", function(d) {
		return (d.source.y + (d.target.y - d.source.y) * 0.5);
	});

}

// handles to mode select buttons and left-hand panel
var modeButtons = d3.selectAll('#mode-select button'), panes = d3
		.selectAll('.panel .tab-pane'),
// non-used
graphOrResultsSelect = d3.selectAll('#graphOrResultsSelect button');

function setAppMode(newMode, modeOfRestart) {

	// switch button and panel states and set new mode
	modeButtons.each(function(d, i) {
		if (i !== newMode)
			d3.select(this).classed('active', false);
		else
			d3.select(this).classed('active', true);
	});

	panes.each(function(d, i) {
		if (i !== newMode)
			d3.select(this).classed('active', false);
		else
			d3.select(this).classed('active', true);
	});
	appMode = newMode;

	if (modeOfRestart === 1)
		restart();
	else
		simpleRestart();
}

// setting the selected node depending on request information
if ("${currASName}" != null && "${currASName}" != '') {

	var res1 = $.grep(nodes, function(e) {
		return e.name == "${currASName}";
	});
	dummy_selected_node = res1[0];
	setSelectedNode(dummy_selected_node);
}

// app starts here
setAppMode(MODE.EDIT, 1);

function triggerEvent(el, type) {
	if ('createEvent' in document) {
		// modern browsers, IE9+
		var e = document.createEvent('HTMLEvents');
		e.initEvent(type, false, true);
		el.dispatchEvent(e);
	} else {
		// IE 8
		var e = document.createEventObject();
		e.eventType = type;
		el.fireEvent('on' + e.eventType, e);
	}
}

var optArray = [];
for (var i = 0; i < nodes.length; i++) {
	optArray.push(nodes[i].name);
}
for (var i = 0; i < links.length; i++) {
	optArray.push(links[i].name);
}
optArray = optArray.sort();
$(function() {
	$("#search").autocomplete({
		source : optArray
	});
});
function searchNode(disButton) {

	// find the node
	var selectedVal = document.getElementById('search').value;
	var node = svg.selectAll(".front, .shadow, .navigation");

	if (selectedVal != "" && optArray.includes(selectedVal)) {
		if (selectedVal == "none") {
			node.style("stroke", "white").style("stroke-width", "1");
		} else {
			var selected = node.filter(function(d, i) {
				return d.name != selectedVal;
			});

			selected.style("opacity", "0");
			var link = svg.selectAll(".link")
			link.style("opacity", "0");
			// var txt = svg.selectAll(".front")
			// txt.style("opacity", "0");
			var nd = svg.selectAll(".node")
			nd.style("opacity", "0");
			d3.selectAll(".node, .link, .navigation, .shadow, .front")
					.transition().duration(3000).style("opacity", 1);
			disButton.disabled = true;
			setTimeout(function() {
				disButton.disabled = false;
			}, 4000);
		}
	}
}

$(document).ready(function() {

});

function onTooltipsHover() {

	console.log('hover');
	svg.selectAll(".node, .link, .navigation, .shadow, .front").style(
			"opacity", "0.2");

}

// https://github.com/wbkd/d3-extended
d3.selection.prototype.moveToFront = function() {
	return this.each(function() {
		this.parentNode.appendChild(this);
		console.log('parenting: ' + this.parentNode);
	});
};
d3.selection.prototype.moveToBack = function() {
	return this.each(function() {
		var firstChild = this.parentNode.firstChild;
		if (firstChild) {
			this.parentNode.insertBefore(this, firstChild);
		}
	});
};

function onTooltipsOut() {

	svg.selectAll(".node, .link, .navigation, .shadow, .front").style(
			"opacity", "1");

}

function fillNVDivWrapper(nvName) {

	var currASAbbName = $('input[name=currASAbbName]').val();

	console.log('in out');

	if (checkValueIsOk(currASAbbName)) {

		console.log('in in 1');
		// triggering click enables saving the current values
		$('input[name=currASAbbName]').trigger('click');

		asArray[currASAbbName][0] = $('#as_section1').html();
		asArray[currASAbbName][1] = $('[name=as_section2]').html();

		ajaxSubmitAs();

		// console.log('AsArray' + asArray[currASAbbName][0] +
		// asArray[currASAbbName][1]);
	}

	var currNVAbbName = $('input[name=currNVAbbName]').val();

	if (checkValueIsOk(currNVAbbName)) {

		console.log('in in 2');
		// triggering click enables saving the current values
		$('input[name=currNVAbbName]').trigger('click');

		nvArray[currNVAbbName][0] = $('#as_section1').html();
		nvArray[currNVAbbName][1] = $('[name=as_section2]').html();

		ajaxSubmitNv();

	}

	fillNVDiv(nvName);
}

function findRemainingDistanceToGraphEdgesXAndY(nodeX, nodeY) {

	var str = $('.plotting-area').attr('transform');

	// Slice with two parameters: return a new substring of the original [start,
	// end]
	var scTrans = str.slice(str.indexOf('translate(') + 'translate('.length,
			str.indexOf(') scale('));

	var splittedTrans = scTrans.split(',');
	var transX = parseFloat(splittedTrans[0]);
	var transY = parseFloat(splittedTrans[1]);
	var floatArrayForTrans = [ transX, transY ];

	var scScale = str.slice(str.indexOf('scale(') + 'scale('.length);
	// Removing the bracket at the end ')'
	scScale = scScale.substr(0, scScale.length - 1);
	var floatScale = parseFloat(scScale);

	console.log('width: ' + width);
	console.log('transX: ' + transX);
	console.log('nodeX: ' + nodeX);
	console.log('floatScale: ' + floatScale);

	var remainingX = width - transX - nodeX * floatScale;
	var remainingY = height - transY - nodeY * floatScale;

	console.log('remainingX: ' + remainingX);
	console.log('remainingY: ' + remainingY);

	var arr = [];
	arr[0] = remainingX;
	arr[1] = remainingY;

	return arr;
}

function triggerNVSelectionFromAS(nvName) {

	if (checkValueIsOk(nvName)) {

		var res1 = $.grep(links, function(e) {
			return e.name == nvName;
		});

		dummy_selected_link = res1[0];

		console.log('res2 ' + res1[0].id);

		var res2 = $.grep(path, function(e) {
			return e.id == res1[0].id;
		});

		path
				.each(function(d1) {
					if (d1.id === res1[0].id) {

						var itmId = d1.id;
						var itmClass = "link"; // d.className;
						var itmIdAndClass = "#" + itmId + "." + itmClass;
						var itmIdStr = "#" + itmId;

						console.log('dispatching...'
								+ d3.select('#' + 'path' + d1.id));

						// d3.select('#' + 'path' +
						// d1.id).dispatch("mousedown");

						var evt = new MouseEvent("mousedown");

						// The way to dispatch the event using d3
						d3.select('#' + 'path' + d1.id).node().dispatchEvent(
								evt);

						document.getElementById('graphButton').click();

						return;
					}
				});
	}

}

globalMode = 1;
