package swag.web;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.StringUtils;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AggregationOperationInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToBaseMeasureCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToResultFilters;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasure;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceMultiplePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMsr;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerivedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASSimple;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionTyped;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionTypedWritten;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionWritten;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditoinGeneric;
import swag.analysis_graphs.execution_engine.analysis_situations.SlicePositionInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceSet;
import swag.analysis_graphs.execution_engine.analysis_situations.Variable;
import swag.md_elements.MultipleMDElement;
import swag.predicates.LiteralCondition;

public class GenerateVariableStringVisitor implements VariableStringVisitor {

    private Set<Entry<Integer, Variable>> setOfVariables;
    private AnalysisGraph graph;

    public Set<Entry<Integer, Variable>> getSetOfVariables() {
	return setOfVariables;
    }

    public void setSetOfVariables(Set<Entry<Integer, Variable>> setOfVariables) {
	this.setOfVariables = setOfVariables;
    }

    private GenerateVariableStringVisitor(Set<Entry<Integer, Variable>> setOfVariables, AnalysisGraph graph) {
	this.setOfVariables = setOfVariables;
	this.graph = graph;
    }

    public static GenerateVariableStringVisitor createGenerateVariableStringVisitor(
	    Set<Entry<Integer, Variable>> setOfVariables, AnalysisGraph graph) {
	return new GenerateVariableStringVisitor(setOfVariables, graph);
    }

    @Override
    public String visit(LevelInAnalysisSituation level, int variableIndex) {

	String str = "";

	String correspondingDiceNodeButtonName = "";
	String correspondingDiceNodeButtonValue = "";

	if (level.getSignature().getItemType().equals(ItemInAnalysisSituationType.DiceLevel)) {
	    for (Map.Entry<Integer, Variable> entry1 : getSetOfVariables()) {
		if (entry1.getValue() instanceof DiceNodeInAnalysisSituation) {
		    DiceNodeInAnalysisSituation diceNode = (DiceNodeInAnalysisSituation) entry1.getValue();

		    // getting the generated name for the corresponding dice
		    // node
		    if (diceNode.getSignature().isVariable() && diceNode.getSignature().getContainingObject().getD()
			    .equals(level.getSignature().getContainingObject().getD())) {
			correspondingDiceNodeButtonName = "diceNode_suggestion"
				+ diceNode.getSignature().getVriableName() + "_variable" + entry1.getKey() + "";
			correspondingDiceNodeButtonValue = "{ &quot;type&quot;:&quot;diceLevel&quot;, &quot;levelName&quot;:&quot;"
				+ level.getName() + "&quot; }";
		    }
		}
	    }
	}

	str = "<input class='variable'"
		+ (level.getSignature().isBoundVariable() ? " value='" + level.getIdentifyingName() + "' " : " ")
		+ " placeholder="
		+ (level.getSignature().getItemType().equals(ItemInAnalysisSituationType.GranularityLevel)
			? "'granularity level'" : "'dice level'")
		+ "type='text' id='1' name='level_" + level.getSignature().getVriableName() + "_variable"
		+ variableIndex + "' "
		+ (level.getSignature().getItemType().equals(ItemInAnalysisSituationType.DiceLevel)
			? " onpaste='doLevelOnChange(this," + "&#39;" + correspondingDiceNodeButtonName + "&#39;" + ", "
				+ "&#39;" + correspondingDiceNodeButtonValue + "&#39;"
				+ ");' oncut='doLevelOnChange(this," + "&#39;" + correspondingDiceNodeButtonName
				+ "&#39;" + ", " + "&#39;" + correspondingDiceNodeButtonValue + "&#39;"
				+ "); ' onkeydown=' doLevelOnChange(this," + "&#39;" + correspondingDiceNodeButtonName
				+ "&#39;" + ", " + "&#39;" + correspondingDiceNodeButtonValue + "&#39;"
				+ "); ' oninput='doLevelOnChange(this," + "&#39;" + correspondingDiceNodeButtonName
				+ "&#39;" + ", " + "&#39;" + correspondingDiceNodeButtonValue + "&#39;"
				+ "); ' onkeyup=' doLevelOnChange(this," + "&#39;" + correspondingDiceNodeButtonName
				+ "&#39;" + ", " + "&#39;" + correspondingDiceNodeButtonValue + "&#39;"
				+ "); ' onblur=' doLevelOnChange(this," + "&#39;" + correspondingDiceNodeButtonName
				+ "&#39;" + ", " + "&#39;" + correspondingDiceNodeButtonValue + "&#39;" + ");'"
			: " ")
		+ " id='2' name='" + "level_suggestion" + level.getSignature().getVriableName() + "_variable"
		+ variableIndex + "'" + " onclick='clickNext(this);' /> "
		+ "<button type='button' class='btn btn-info btn-sm' style='display: none' onclick='doSuggButtonOnClick(this)' name='"
		+ "level_suggestion" + level.getSignature().getVriableName() + "_variable" + variableIndex + "' "
		+ "value='{ &quot;type&quot;:&quot;"
		+ (level.getSignature().getItemType().equals(ItemInAnalysisSituationType.GranularityLevel)
			? "granularityLevel" : "diceLevel")
		+ "&quot;, &quot;dimensionURI&quot;:&quot;"
		+ level.getSignature().getContainingObject().getD().getIdentifyingName() + "&quot;} '/>"
		+ " Suggestions </button> </p> </td>  </p> ";

	return str;
    }

    @Override
    public String visit(DiceNodeInAnalysisSituation node, int variableIndex) {

	String str = "";

	DiceNodeInAnalysisSituation diceNode = node;
	str = " <input class='variable'"
		+ (diceNode.getSignature().isBoundVariable() ? " value='" + diceNode.getNodeValue() + "' " : " ")
		+ "placeholder='level member' type='text' id='2' name='diceNode_"
		+ diceNode.getSignature().getVriableName() + "_variable" + variableIndex
		+ "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm' style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		+ "diceNode_suggestion" + diceNode.getSignature().getVriableName() + "_variable" + variableIndex + "'"
		+ "value='{ &quot;type&quot;:&quot;diceLevel&quot;, &quot;levelName&quot;:&quot;"
		+ diceNode.getSignature().getContainingObject().getDiceLevel().getIdentifyingName() + "&quot; } '/>"
		+ " Suggestions </button> <button type='button' class='btn' onclick = '"
		+ "$( &#39;[name=&quot;diceNode_" + diceNode.getSignature().getVriableName() + "_variable"
		+ variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;" + "diceNode_"
		+ diceNode.getSignature().getVriableName() + "_variable" + variableIndex + "&quot;);"
		+ "'><i class='fa fa-close'></i></button>  </p> ";

	return str;
    }

    @Override
    public String visit(IMeasureInAS meas, int variableIndex) throws OperationNotSupportedException {
	throw new OperationNotSupportedException();
    }

    @Override
    public String visit(SlicePositionInAnalysisSituation position, int variableIndex) {
	return "";
    }

    @Override
    public String visit(PredicateInASSimple predicate, int variableIndex) {

	String str = "";

	str = " <input class='variable'"
		+ (predicate.getSignature().isBoundVariable() ? " value='" + predicate.getURI() + "' " : " ")
		+ "placeholder='predicate' type='text' id='6' name='diceNode_slice_"
		+ predicate.getSignature().getVriableName() + "_variable" + variableIndex
		+ "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm' style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		+ "diceNode_slice_suggestion" + predicate.getSignature().getVriableName() + "_variable" + variableIndex
		+ "'" + "value='{ &quot;type&quot;:&quot;diceLevel&quot;, &quot;levelName&quot;:&quot;"
		+ predicate.getPositionOfCondition().getIdentifyingName() + "&quot; } '/>"
		+ " Suggestions </button> <button type='button' class='btn' onclick = '"
		+ "$( &#39;[name=&quot;diceNode_slice_" + predicate.getSignature().getVriableName() + "_variable"
		+ variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;" + "diceNode_slice_"
		+ predicate.getSignature().getVriableName() + "_variable" + variableIndex + "&quot;);"
		+ "'><i class='fa fa-close'></i></button>  </p> ";

	return str;

    }

    @Override
    public String visit(SliceSet predicate, int variableIndex) {
	String str = "";

	str = " <input class='variable'"
		+ (predicate.getSignature().isBoundVariable() ? " value='" + predicate.getUri() + "' " : " ")
		+ "placeholder='predicate' type='text' id='7' name='diceNode_slice_multiple_"
		+ predicate.getSignature().getVriableName() + "_variable" + variableIndex
		+ "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm'"
		+ " style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		+ "diceNode_slice_multiple_suggestion" + predicate.getSignature().getVriableName() + "_variable"
		+ variableIndex + "'" + "value='{ &quot;type&quot;:&quot;diceLevel&quot;, &quot;levelName&quot;:&quot;"
		+ predicate.getPositoinOfSliceSet().getIdentifyingName() + "&quot; } '/>"
		+ " Suggestions </button> <button type='button' class='btn' onclick = '"
		+ "$( &#39;[name=&quot;diceNode_slice_multiple_" + predicate.getSignature().getVriableName()
		+ "_variable" + variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;"
		+ "diceNode_slice_multiple_" + predicate.getSignature().getVriableName() + "_variable" + variableIndex
		+ "&quot;);" + "'><i class='fa fa-close'></i></button>  </p> ";

	return str;
    }

    @Override
    public String visit(PredicateInASMultiple predicate, int variableIndex) {
	String str = "";

	str = " <input class='variable'"
		+ (predicate.getSignature().isBoundVariable() ? " value='" + predicate.getURI() + "' " : " ")
		+ "placeholder='predicate' type='text' id='8' name='diceNode_slice_multiple_"
		+ predicate.getSignature().getVriableName() + "_variable" + variableIndex
		+ "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm'"
		+ " style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		+ "diceNode_slice_multiple_suggestion" + predicate.getSignature().getVriableName() + "_variable"
		+ variableIndex + "'" + "value='{ &quot;type&quot;:&quot;diceLevel&quot;, &quot;levelName&quot;:&quot;"
		+ predicate.getSignature().getContainingObject().getName() + "&quot; } '/>"
		+ " Suggestions </button> <button type='button' class='btn' onclick = '"
		+ "$( &#39;[name=&quot;diceNode_slice_multiple_" + predicate.getSignature().getVriableName()
		+ "_variable" + variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;"
		+ "diceNode_slice_multiple_" + predicate.getSignature().getVriableName() + "_variable" + variableIndex
		+ "&quot;);" + "'><i class='fa fa-close'></i></button>  </p> ";

	return str;
    }

    private String visit(ISliceSinglePosition<?> conditoin, int variableIndex) {
	String str = "";

	if (conditoin.getContainingObject() instanceof IDimensionQualification) {

	    IDimensionQualification q = (IDimensionQualification) conditoin.getContainingObject();

	    str = " <input class='variable'"
		    + (conditoin.getSignature().isBoundVariable() ? " value='" + conditoin.getConditoin() + "' " : " ")
		    + "placeholder='predicate' type='text' id='9' name='diceNode_slice_noPosition_"
		    + conditoin.getSignature().getVriableName() + "_variable" + variableIndex
		    + "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm'"
		    + " style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		    + "diceNode_slice_noPosition_suggestion" + conditoin.getSignature().getVriableName() + "_variable"
		    + variableIndex + "'"
		    + "value='{ &quot;type&quot;:&quot;hierInDim&quot;, &quot;levelName&quot;:&quot;"
		    + q.getHierarchy().getIdentifyingName() + q.getD().getIdentifyingName() + "&quot; "
		    + "&quot;predType&quot;:&quot;" + "" + "&quot;  } '/>" + " Suggestions </button>"
		    + " <button type='button' class='btn' onclick = '"
		    + "$( &#39;[name=&quot;diceNode_slice_noPosition_" + conditoin.getSignature().getVriableName()
		    + "_variable" + variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;"
		    + "diceNode_slice_noPosition_" + conditoin.getSignature().getVriableName() + "_variable"
		    + variableIndex + "&quot;);" + "'><i class='fa fa-close'></i></button>" + " suggestions "
		    + "<input type='checkbox' name='checkBox_diceNode_slice_noPosition_"
		    + conditoin.getSignature().getVriableName() + "_variable" + variableIndex + "' >  </p> ";
	}

	return str;

    }

    public String visit(ISliceMultiplePosition<?> conditoin, int variableIndex) {
	String str = "";

	if (conditoin.getContainingObject() instanceof AnalysisSituation) {

	    AnalysisSituation q = (AnalysisSituation) conditoin.getContainingObject();

	    str = " <input class='variable'"
		    + (conditoin.getSignature().isBoundVariable() ? " value='" + conditoin.getConditoin() + "' " : " ")
		    + "placeholder='predicate' type='text' id='9' name='diceNode_slice_multiple_"
		    + conditoin.getSignature().getVriableName() + "_variable" + variableIndex
		    + "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm'"
		    + " style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		    + "diceNode_slice_noPosition_suggestion" + conditoin.getSignature().getVriableName() + "_variable"
		    + variableIndex + "'"
		    + "value='{ &quot;type&quot;:&quot;hierInDim&quot;, &quot;levelName&quot;:&quot;"
		    + MultipleMDElement.getInstance().getIdentifyingName() + "&quot; } '/>" + " Suggestions </button>"
		    + " <button type='button' class='btn' onclick = '"
		    + "$( &#39;[name=&quot;diceNode_slice_noPosition_" + conditoin.getSignature().getVriableName()
		    + "_variable" + variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;"
		    + "diceNode_slice_noPosition_" + conditoin.getSignature().getVriableName() + "_variable"
		    + variableIndex + "&quot;);" + "'><i class='fa fa-close'></i></button>" + " suggestions "
		    + "<input type='checkbox' name='checkBox_diceNode_slice_noPosition_"
		    + conditoin.getSignature().getVriableName() + "_variable" + variableIndex + "' >  </p> ";
	}

	return str;

    }

    @Override
    public String visit(SliceCondition conditoin, int variableIndex) {

	String str = "";
	String val = getValStrNonTyped(conditoin);
	String containingObjectStr = getContainingObjectStr(conditoin);

	str = " <input class='variable'" + (conditoin.getSignature().isBoundVariable() ? " value='" + val + "' " : " ")
		+ "placeholder='predicate' type='text' id='9' name='diceNode_slice_noPosition_"
		+ conditoin.getSignature().getVriableName() + "_variable" + variableIndex
		+ "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm'"
		+ " style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		+ "diceNode_slice_noPosition_suggestion" + conditoin.getSignature().getVriableName() + "_variable"
		+ variableIndex + "'" + "value='{ &quot;type&quot;:&quot;hierInDim&quot;, &quot;levelName&quot;:&quot;"
		+ containingObjectStr + "&quot; " + ", &quot;conditionType&quot;:&quot;" + conditoin.getType()
		+ "&quot; } '/>" + " Suggestions </button>" + " <button type='button' class='btn' onclick = '"
		+ "$( &#39;[name=&quot;diceNode_slice_noPosition_" + conditoin.getSignature().getVriableName()
		+ "_variable" + variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;"
		+ "diceNode_slice_noPosition_" + conditoin.getSignature().getVriableName() + "_variable" + variableIndex
		+ "&quot;);" + "'><i class='fa fa-close'></i></button>"
	/*
	 * +" </p> <br>" + " Suggestions Off" +
	 * "<input type='checkbox' name='checkBox_diceNode_slice_noPosition_" +
	 * conditoin.getSignature().getVriableName() + "_variable" +
	 * variableIndex + "' > "
	 */
	;

	return str;
    }

    @Override
    public String visit(SliceConditionTyped conditoin, int variableIndex) {

	String str = "";
	String val = getValStr(conditoin);
	String typeName = "";
	try {
		typeName = graph.getDefinedAGConditionTypes().getConditoinTypeByIdentifyingName(conditoin.getType())
				.getName();
	}catch(Exception ex){

	}

	if (typeName.equals("")){
		typeName = graph.getDefinedAGConditions().getConditoinByIdentifyingName(conditoin.getType()).getName();
	}

	String containingObjectStr = getContainingObjectStr(conditoin);

	str = " " + typeName + "</br> " + "<input class='variable'"
		+ (conditoin.getSignature().isBoundVariable() ? " value='" + val + "' " : " ")
		+ "placeholder='predicate' type='text' id='9' name='diceNode_slice_noPosition_typed_"
		+ conditoin.getSignature().getVriableName() + "_variable" + variableIndex
		+ "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm'"
		+ " style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		+ "diceNode_slice_noPosition_typed_suggestion" + conditoin.getSignature().getVriableName() + "_variable"
		+ variableIndex + "'" + "value='{ &quot;type&quot;:&quot;hierInDim&quot;, &quot;levelName&quot;:&quot;"
		+ containingObjectStr + "&quot; " + ", &quot;conditionType&quot;:&quot;" + conditoin.getType()
		+ "&quot; } '/>" + " Suggestions </button>" + " <button type='button' class='btn' onclick = '"
		+ "$( &#39;[name=&quot;diceNode_slice_noPosition_typed_" + conditoin.getSignature().getVriableName()
		+ "_variable" + variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;"
		+ "diceNode_slice_noPosition_typed_" + conditoin.getSignature().getVriableName() + "_variable"
		+ variableIndex + "&quot;);" + "'><i class='fa fa-close'></i></button>  "
	/*
	 * + "</p>" + "<br>" + " Suggestions Off" +
	 * "<input type='checkbox' name='checkBox_diceNode_slice_noPosition_typed_"
	 * + conditoin.getSignature().getVriableName() + "_variable" +
	 * variableIndex + "' >  "
	 */
	;

	return str;
    }

    @Override
    public String visit(SliceConditionTypedWritten conditoin, int variableIndex) {

	String str = "";
	String val = "";

	Map<String, String> bindings = graph.getDefinedAGConditions().getConditoinByIdentifyingName(conditoin.getURI())
		.getBindings();

	for (Map.Entry<String, String> entry : bindings.entrySet()) {
	    val = entry.getValue();
	}

	if (conditoin.getContainingObject() instanceof IDimensionQualification) {

	    IDimensionQualification q = (IDimensionQualification) conditoin.getContainingObject();

	    str = " " + conditoin.getType() + "</p>" + "<input class='variable'"
		    + (conditoin.getSignature().isBoundVariable() ? " value='" + val + "' " : " ")
		    + "placeholder='predicate' type='text' id='9' name='diceNode_slice_noPosition_typed_"
		    + conditoin.getSignature().getVriableName() + "_variable" + variableIndex
		    + "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm'"
		    + " style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		    + "diceNode_slice_noPosition_typed_suggestion" + conditoin.getSignature().getVriableName()
		    + "_variable" + variableIndex + "'"
		    + "value='{ &quot;type&quot;:&quot;hierInDim&quot;, &quot;levelName&quot;:&quot;"
		    + q.getHierarchy().getIdentifyingName() + q.getD().getIdentifyingName() + "&quot; "
		    + ", &quot;conditionType&quot;:&quot;" + conditoin.getType() + "&quot; } '/>"
		    + " Suggestions </button>" + " <button type='button' class='btn' onclick = '"
		    + "$( &#39;[name=&quot;diceNode_slice_noPosition_typed_" + conditoin.getSignature().getVriableName()
		    + "_variable" + variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;"
		    + "diceNode_slice_noPosition_typed_" + conditoin.getSignature().getVriableName() + "_variable"
		    + variableIndex + "&quot;);" + "'><i class='fa fa-close'></i></button>" + " suggestions "
		    + "<input type='checkbox' name='checkBox_diceNode_slice_noPosition_typed_"
		    + conditoin.getSignature().getVriableName() + "_variable" + variableIndex + "' >  </p> ";
	}

	return str;
    }

    @Override
    public String visit(ISliceSetMultiple predicate, int variableIndex) {
	String str = "";

	str = " <input class='variable'"
		+ (predicate.getSignature().isBoundVariable() ? " value='" + predicate.getUri() + "' " : " ")
		+ "placeholder='predicate' type='text' id='7' name='diceNode_slice_multiple_"
		+ predicate.getSignature().getVriableName() + "_variable" + variableIndex
		+ "' onclick='clickNext(this);' /> <button type='button' class='btn btn-info btn-sm'"
		+ " style='display: none' onclick='doSuggButtonOnClick(this)' id='2' name='"
		+ "diceNode_slice_multiple_suggestion" + predicate.getSignature().getVriableName() + "_variable"
		+ variableIndex + "'" + "value='{ &quot;type&quot;:&quot;diceLevel&quot;, &quot;levelName&quot;:&quot;"
		+ MultipleMDElement.getInstance().getIdentifyingName() + "&quot; } '/>"
		+ " Suggestions </button> <button type='button' class='btn' onclick = '"
		+ "$( &#39;[name=&quot;diceNode_slice_multiple_" + predicate.getSignature().getVriableName()
		+ "_variable" + variableIndex + "&quot;]&#39;).val(&quot;&quot;); clearFromValuesAndLabels(&quot;"
		+ "diceNode_slice_multiple_" + predicate.getSignature().getVriableName() + "_variable" + variableIndex
		+ "&quot;);" + "'><i class='fa fa-close'></i></button>  </p> ";

	return str;
    }

    @Override
    public String visit(ISliceSetMsr sliceSetMsr, int variableIndex) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String visit(IMeasure meas, int variableIndex) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String visit(MeasureDerivedInAS meas, int variableIndex) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String visit(MeasureAggregated meas, int variableIndex) {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * @param conditoin
     * @return
     */
    private String getValStr(SliceConditionTyped conditoin) {

	String val = "";

	/*
	 * if (conditoin.isWritten()) { Map<String, String> bindings =
	 * graph.getDefinedAGConditions()
	 * .getConditoinByIdentifyingName(conditoin.getURI()).getBindings(); for
	 * (Map.Entry<String, String> entry : bindings.entrySet()) { val =
	 * entry.getValue(); break; } } else
	 */
	{
	    if (!StringUtils.isEmpty(conditoin.getURI())) {
		val = graph.getDefinedAGConditions().getConditoinByIdentifyingName(conditoin.getURI()).getURI();
	    }
	}

	return val;
    }

    /**
     * @param conditoin
     * @return
     */
    private String getValStrNonTyped(SliceCondition conditoin) {

	String val = "";
	/*
	 * if (conditoin.isWritten()) { val = conditoin.getCondition(); } else
	 */ {

	    if (!StringUtils.isEmpty(conditoin.getURI())) {
		LiteralCondition cond = graph.getDefinedAGConditions()
			.getConditoinByIdentifyingName(conditoin.getURI());
		if (cond != null) {
		    val = cond.getURI();
		}
	    }
	}
	return val;
    }

    /**
     * @param conditoin
     * @return
     */
    private String getContainingObjectStr(ISliceSinglePosition conditoin) {

	String containingObjectStr = "";

	if (conditoin.getContainingObject() instanceof IDimensionQualification) {
	    IDimensionQualification q = (IDimensionQualification) conditoin.getContainingObject();
	    containingObjectStr = q.getHierarchy().getIdentifyingName() + q.getD().getIdentifyingName();
	} else {
	    if (conditoin.getContainingObject() instanceof AnalysisSituation) {
		containingObjectStr = "AnalysisSituation";
	    } else {
		if (conditoin.getContainingObject() instanceof AnalysisSituationToBaseMeasureCondition) {
		    containingObjectStr = "AnalysisSituationToBaseMeasureCondition";
		} else {
		    if (conditoin.getContainingObject() instanceof AnalysisSituationToResultFilters) {
			containingObjectStr = "AnalysisSituationToResultFilters";
		    }
		}
	    }
	}

	return containingObjectStr;
    }

    @Override
    public String visit(AggregationOperationInAnalysisSituation aggregationOperationInAnalysisSituation,
	    int variableIndex) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String visit(SliceConditionWritten<?> sliceConditionWritten, int variableIndex) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String visit(SliceConditoinGeneric<?> sliceConditoinGeneric, int variableIndex) {
	// TODO Auto-generated method stub
	return null;
    }

}
