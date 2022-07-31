package swag.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebConstants {

    public static final String PREDICATE_SPLITTER = "__";

    public static final String INITIAL_POSITIONS = "[{\"x\":141.21875,\"y\":386},{\"x\":470.21875,\"y\":284},{\"x\":402.21875,\"y\":447},"
	    + "{\"x\":133.21875,\"y\":206},{\"x\":464.21875,\"y\":52},{\"x\":360.7115568059036,\"y\":165.41013116249897},{\"x\":63.21875,\"y\":51}]";
    public static final String FURTHER_ACTIONS = "Possible further steps for your analysis. A click on one of the links selects the navigation step of the same name.";

    public static final String DICE_LEVEL_STRING = "The level that we want to restrict to a specific value.";
    public static final String DICE_NODE_STRING = "The level to restrict to a specific value. Enter the name of a level member at the specified level.";

    public static final String SLICE_POS_STRING = "Select a comparison operator and enter a value to specify a filter condition over the specified level.";

    public static final String SLICE_PRED_POS_STRING = "The level or attribute to restrict using a defined predicate.";

    public static final String SLICE_COND_STRING = "The conditoin that is used to restrict the values. The button labelled with x can be clicked to clear the value.";

    public static final String MEAURES_STRING = "The measures to aggregate.";

    public static final String BASE_MEAURES_COND_STRING = "The conditions to be applied to base measures.";

    public static final String RESULT_FILTERS_STRING = "The conditions to be applied to the result measures.";

    public static final String SLICE_PRED_STRING = "The conditoin that is used to restrict a level/attribute values. The button labelled with x can be clicked to clear the value.";

    public static final String SLICE_MULTIPLE_PRED_STRING = "Conditions to restrict the selected values.";

    public static final String SET_STRING = "The set of comparison to which specification belongs.";

    public static final String GRAN_LEVEL_STRING = "The level of granularity of the result in the specified dimension.";
    public static final String NEW_GRANULARITY_LEVEL = "The new level that is used as a granualrity, i.e., for grouping by.";

    public static final String SOURCE = "The source analysis situation of the navigation step.";
    public static final String TARGET = "The target analysis situation of the navigation step.";
    public static final String OPERATIONS = "A set of OLAP operations that change the source analysis situation into the target analysis situation.";

    public static final String OPERATOR_MOVE_TO_DICE_NODE = "Restrict a given level to a specified level member.";
    public static final String OPERATOR_MODIFY_SLICE_CONDITION = "Changes the condition over the specified level in the specified dimension for filtering the facts used for the aggregation.";
    public static final String OPERATOR_MODIFY_BASE_MSR_CONDITION = "Changes the base measure conditions.";
    public static final String OPERATOR_MODIFY_RESULT_FILTER_CONDITION = "Changes the result filter.";
    public static final String OPERATOR_DRILL_DOWN_TO = "Change the granularity level of the query in a specified dimension.";
    public static final String OPERATOR_ROLL_UP_TO = "Changes the granularity on a dimension by rolling up to a specific level.";

    public static final String NAVIGATE = "Open the target analysis situation with the specified bindings, execute the query, and show the results.";
    public static final String RESULTS = "Execute the query and show the results.";

    public static final String SEARCH = "Look for a specific analysis situation or navigation step by name.";
    public static final String SEARCH_BOX = "Enter the name of a specific analysis situation or navigation step and press Enter or the SEARCH button to look for a specific "
	    + "analysis situation or navigation step by name in the graph.";

    public static final String SELECT_AG = "Select an analysis graph from the already existing ones.";
    public static final String LINK_AG = "Provide a link to an analysis graph file over the web.";
    public static final String UPLOAD_AG = "Upload an analysis graph RDF file from your PC. The file could be of rdf extension.";
    public static final String UPLOAD_SMD = "Upload a multidimensional schema RDF file from your PC, on which the provided analysis graph file depends. The file should be of rdf extension.";

    public static final String I_OPEN_DOWN = "&nbsp; <i class='qtip tip-left' data-tip='";
    public static final String I_OPEN_LEFT = "&nbsp; <i class='qtip tip-left' data-tip='";
    public static final String I_OPEN_RIGHT = "&nbsp; <i class='qtip tip-right' data-tip='";
    public static final String I_CLOSE = "'> <font style='font-weight:bold; font-size:12px;'>  <img src='img/info.png'  width='5' height='5' /> </font> </i>";

    public final static Set<String> ALLOWED_FILE_TYPES = Collections.unmodifiableSet(
	    new HashSet<String>(Arrays.asList("ttl", "rdf", "nt", "jsonld", "owl", "trig", "nq", "trix", "trdf")));

    public final static Map<String, String> TYPES_MAP = Stream
	    .of(new String[][] { { ".ttl", "Turtle" }, { ".nt", "N-Triples" }, { ".nq", "N-Quads" },
		    { ".trig", "TriG" }, { ".rdf", "RDF/XML" }, { ".owl", "RDF/XML" }, { ".jsonld", "JSON-LD" },
		    { ".trdf", "RDF Thrift" }, { ".rt", "RDF Thrift" }, { ".rj", "RDF/JSON" }, { ".trix", "TriX" } })
	    .collect(Collectors.toMap(data -> data[0], data -> data[1]));

}
