package swag.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraphsManager;
import swag.analysis_graphs.execution_engine.DefinedAGConditions;
import swag.analysis_graphs.execution_engine.DefinedAGConditionsTypes;
import swag.analysis_graphs.execution_engine.DefinedAGPredicates;
import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.analysis_situations.AggregationOperationInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasure;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceMultiplePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMsr;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePositionNoType;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePositionTyped;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerivedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASSimple;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionFactory;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionStatus;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionTyped;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditoinGeneric;
import swag.analysis_graphs.execution_engine.analysis_situations.SlicePositionInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceSet;
import swag.analysis_graphs.execution_engine.analysis_situations.Variable;
import swag.helpers.AutoCompleteData;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.md_elements.Mapping;
import swag.md_elements.MappingFunctions;
import swag.predicates.LiteralCondition;
import swag.predicates.LiteralConditionType;

/**
 * 
 * Setting paramName is necessary prior to each visit call;
 * 
 * @author swag
 *
 */
public class VariableVisitorProcessParameter implements IVariableProcessVisitor {

    private static final Logger logger = Logger.getLogger(VariableVisitorProcessParameter.class);

    String paramName;
    String addedNSForSMDInstance;
    String paramValue;

    Map<String, String[]> addedLabelBindings = new HashMap<>();
    Map<String, String[]> addedValueBindings = new HashMap<>();
    List<AutoCompleteData> autocompleteData = new ArrayList<>();

    Map<Variable, Variable> initialVariables;
    Map<Integer, Variable> variables;
    AnalysisGraphsManager buildEng;
    IVariablesList as;
    String factURI;

    @Override
    public void init(String paramName, String paramValue) {
	setParamName(paramName);
	setParamValue(paramValue);
    }

    public VariableVisitorProcessParameter(String addedNSForSMDInstance, Map<Variable, Variable> initialVariables,
	    Map<Integer, Variable> variables, AnalysisGraphsManager buildEng, IVariablesList as, String factURI) {
	super();
	this.addedNSForSMDInstance = addedNSForSMDInstance;
	this.initialVariables = initialVariables;
	this.variables = variables;
	this.buildEng = buildEng;
	this.as = as;
	this.factURI = factURI;
    }

    @Override
    public void visit(LevelInAnalysisSituation level) {

	if (!paramValue.equals("")) {
	    String[] strs = paramName.split("_variable");
	    LevelInAnalysisSituation lvl = (LevelInAnalysisSituation) variables.get(Integer.parseInt(strs[1]));

	    LevelInAnalysisSituation lvlCopy = lvl.shallowCopy();
	    String uri = paramValue;
	    Level l = (Level) buildEng.getGraph().getNode(uri);
	    LevelInAnalysisSituation tempLevel = new LevelInAnalysisSituation(l);
	    lvl.bind(tempLevel);
	    initialVariables.put(lvlCopy, lvl);
	}
    }

    @Override
    public void visit(DiceNodeInAnalysisSituation node) throws OperationNotSupportedException {
	String[] strs = paramName.split("_variable");

	DiceNodeInAnalysisSituation dn = (DiceNodeInAnalysisSituation) variables.get(Integer.parseInt(strs[1]));

	if (!paramValue.equals("")) {

	    DiceNodeInAnalysisSituation dnInitial = null;
	    if (dn.getSignature().isBoundVariable()) {
		dnInitial = (DiceNodeInAnalysisSituation) as.getInitialVarOfBoundVar(dn);
	    } else {
		dnInitial = dn.shallowCopy();
	    }

	    String value = paramValue;
	    DiceNodeInAnalysisSituation dnTemp = new DiceNodeInAnalysisSituation(value);
	    dn.bind(dnTemp);

	    initialVariables.put(dnInitial, dn);

	    // The case of clearing the variable value
	} else {
	    if (dn.getSignature().isBoundVariable()) {

		DiceNodeInAnalysisSituation dnInitial = null;
		dnInitial = (DiceNodeInAnalysisSituation) as.getInitialVarOfBoundVar(dn);
		dn.unBind();
		initialVariables.put(dnInitial, null);
	    }
	}
    }

    @Override
    public void visit(SlicePositionInAnalysisSituation position) {
    }

    /**
     * @param conditoin
     * @throws Exception
     */
    private void visitSliceCondition(SliceCondition<?> conditoin) throws Exception {
	String[] strs = paramName.split("_variable");

	SliceCondition<?> dn = (SliceCondition<?>) variables.get(Integer.parseInt(strs[1]));

	if (!paramValue.equals("")) {

	    SliceCondition<?> dnInitial = null;
	    if (dn.getSignature().isBoundVariable()) {
		dnInitial = (SliceCondition<?>) as.getInitialVarOfBoundVar(dn);
	    } else {
		dnInitial = (SliceCondition<?>) dn.shallowCopy();
	    }

	    dnInitial.setStatus(SliceConditionStatus.NON_WRITTEN);
	    String value = paramValue;

	    DefinedAGConditions preds = buildEng.getAg().getDefinedAGConditions();

	    // A defined condition
	    if (preds.getConditoinByIdentifyingName(value) != null) {
		dn.bind(SliceConditionFactory.createSliceConditionWrittenOfSignature(dn,
			preds.getConditoinByIdentifyingName(value).getMdElems().stream().findAny().orElse(null),
			preds.getConditoinByIdentifyingName(value).getName(),
			preds.getConditoinByIdentifyingName(value).getURI()));
		initialVariables.put(dnInitial, dn);
	    }
	    // The case of clearing the variable value
	} else {
	    if (dn.getSignature().isBoundVariable()) {

		SliceCondition<?> dnInitial = null;
		dnInitial = (SliceCondition<?>) as.getInitialVarOfBoundVar(dn);
		dn.unBind();
		initialVariables.put(dnInitial, null);
	    }
	}
    }

    /**
     * @param conditoin
     * @throws Exception
     */
    private void visitSliceConditionTyped(SliceConditionTyped<?> conditoin) throws Exception {
	String[] strs = paramName.split("_variable");

	SliceConditionTyped<?> dn = (SliceConditionTyped<?>) variables.get(Integer.parseInt(strs[1]));

	if (!paramValue.equals("")) {

	    SliceConditionTyped<?> dnInitial = null;
	    if (dn.getSignature().isBoundVariable()) {
		dnInitial = (SliceConditionTyped<?>) as.getInitialVarOfBoundVar(dn);
	    } else {
		dnInitial = (SliceConditionTyped<?>) dn.shallowCopy();
	    }

	    dnInitial.setStatus(SliceConditionStatus.WRITTEN);
	    String value = paramValue;
	    DefinedAGConditions preds = buildEng.getAg().getDefinedAGConditions();

	    DefinedAGConditionsTypes types = buildEng.getAg().getDefinedAGConditionTypes();
	    // Binding a value
	    if (types.getConditoinTypeByIdentifyingName(dnInitial.getType()) != null) {

		LiteralCondition cond = preds.getConditoinByIdentifyingName(value);
		dn.bind(SliceConditionFactory.createSliceConditionTypedOfSignature(dn,
			cond.getMdElems().stream().findAny().orElse(null), cond.getExpression(), cond.getURI(),
			dnInitial.getType()));
		initialVariables.put(dnInitial, dn);
	    }else {
			if (preds.getConditoinByIdentifyingName(dnInitial.getType()) != null) {
				LiteralCondition cond = preds.getConditoinByIdentifyingName(value);
				dn.bind(SliceConditionFactory.createSliceConditionTypedOfSignature(dn,
						cond.getMdElems().stream().findAny().orElse(null), cond.getExpression(), cond.getURI(),
						dnInitial.getType()));
				initialVariables.put(dnInitial, dn);
			}
		}

	    // The case of clearing the variable value
	} else {
	    if (dn.getSignature().isBoundVariable()) {

		SliceCondition<?> dnInitial = null;
		dnInitial = (SliceCondition<?>) as.getInitialVarOfBoundVar(dn);
		dn.unBind();
		initialVariables.put(dnInitial, null);
	    }
	}
    }

    /**
     * @param conditoin
     * @throws Exception
     */
    private void visitSliceConditionTypedWritten(SliceConditionTyped<?> conditoin) throws Exception {
	String[] strs = paramName.split("_variable");

	SliceConditionTyped<?> dn = (SliceConditionTyped) variables.get(Integer.parseInt(strs[1]));

	if (!paramValue.equals("")) {

	    SliceConditionTyped<?> dnInitial = null;
	    if (dn.getSignature().isBoundVariable()) {
		dnInitial = (SliceConditionTyped<?>) as.getInitialVarOfBoundVar(dn);
	    } else {
		dnInitial = (SliceConditionTyped<?>) dn.shallowCopy();
	    }
	    dnInitial.setStatus(SliceConditionStatus.NON_WRITTEN);

	    DefinedAGConditionsTypes types = buildEng.getAg().getDefinedAGConditionTypes();

	    // Binding a value
	    if (types.getConditoinTypeByIdentifyingName(dnInitial.getType()) != null) {

		LiteralConditionType type = types.getConditoinTypeByIdentifyingName(dnInitial.getType());
		String expr = type.getExpression();
		expr = expr.replace(":1", paramValue);

		String var = ":1";
		String val = paramValue;

		Map<String, String> bindings = new HashMap<String, String>();
		bindings.put(type.getInputVariableByID(":1"), val);
		Set<String> s = new HashSet<>();
		s.add(type.getURI());

		LiteralCondition cond = new LiteralCondition(type, paramValue, expr);

		String[] arrLabel = new String[2];
		arrLabel[0] = paramName;
		arrLabel[1] = cond.getLabel();
		addedLabelBindings.put(as.getUri(), arrLabel);

		String[] arrVal = new String[2];
		arrVal[0] = paramName;
		arrVal[1] = cond.getURI();
		addedValueBindings.put(as.getUri(), arrVal);

		autocompleteData.add(new AutoCompleteData(arrLabel[1], arrVal[1]));

		buildEng.getAg().getDefinedAGConditions().addCondition(cond);

		dn.bind(SliceConditionFactory.createSliceConditionTypedOfSignature(dn,
			type.getMdElems().stream().findAny().orElse(null), expr, type.getURI() + paramValue,
			type.getURI()));

		initialVariables.put(dnInitial, dn);
	    }

	    // The case of clearing the variable value
	} else {
	    if (dn.getSignature().isBoundVariable()) {

		SliceConditionTyped<?> dnInitial = null;
		dnInitial = (SliceConditionTyped<?>) as.getInitialVarOfBoundVar(dn);
		dn.unBind();
		initialVariables.put(dnInitial, null);
	    }
	}
    }

    @Override
    public void visit(SliceSet set) {

    }

    @Override
    public void visit(PredicateInASMultiple predicate) throws OperationNotSupportedException {
	String[] strs = paramName.split("_variable");

	PredicateInASMultiple dn = (PredicateInASMultiple) variables.get(Integer.parseInt(strs[1]));

	if (!paramValue.equals("")) {

	    PredicateInASMultiple dnInitial = null;
	    if (dn.getSignature().isBoundVariable()) {
		dnInitial = (PredicateInASMultiple) as.getInitialVarOfBoundVar(dn);
	    } else {
		dnInitial = dn.shallowCopy();
	    }

	    String value = paramValue;

	    DefinedAGPredicates preds = buildEng.getAg().getDefinedAGPredicates();
	    PredicateInASMultiple dnTemp = new PredicateInASMultiple(preds.getPredicateByIdentifyingName(value));
	    dn.bind(dnTemp);
	    initialVariables.put(dnInitial, dn);

	    // The case of clearing the variable value
	} else {
	    if (dn.getSignature().isBoundVariable()) {

		PredicateInASMultiple dnInitial = null;
		dnInitial = (PredicateInASMultiple) as.getInitialVarOfBoundVar(dn);
		dn.unBind();
		initialVariables.put(dnInitial, null);
	    }
	}
    }

    @Override
    public void visit(ISliceSinglePositionNoType<?> pos) throws Exception {

	DefinedAGConditions conds = buildEng.getAg().getDefinedAGConditions();
	DefinedAGPredicates pred = buildEng.getAg().getDefinedAGPredicates();

	if (conds.getConditoinByIdentifyingName(paramValue) != null) {
	    this.visitSliceCondition(SliceConditionFactory.convertSliceCondition(pos));
	    return;
	}

	if (pred.getPredicateByIdentifyingName(paramValue) != null) {
	    this.visit(SliceConditionFactory.convertToPredicateInASSimple(pos));
	    return;
	}

	this.visitSliceConditionWritten(SliceConditionFactory.convertSliceCondition(pos));
    }

    @Override
    public void visit(ISliceSinglePositionTyped<?> pos) throws Exception {

	DefinedAGConditions conds = buildEng.getAg().getDefinedAGConditions();
	DefinedAGPredicates pred = buildEng.getAg().getDefinedAGPredicates();

	if (conds.getConditoinByIdentifyingName(paramValue) != null) {
	    this.visitSliceConditionTyped(SliceConditionFactory.convertToSliceConditionTyped(pos));
	    return;
	}

	if (pred.getPredicateByIdentifyingName(paramValue) != null) {
	    throw new OperationNotSupportedException();
	}

	this.visitSliceConditionTypedWritten(SliceConditionFactory.convertToSliceConditionTyped(pos));
    }

    @Override
    public void visit(ISliceMultiplePosition<?> pos) throws Exception {

	DefinedAGConditions conds = buildEng.getAg().getDefinedAGConditions();
	DefinedAGPredicates pred = buildEng.getAg().getDefinedAGPredicates();

	if (conds.getConditoinByIdentifyingName(paramValue) != null) {
	    this.visit((SliceCondition<?>) pos);
	    return;
	}

	if (pred.getPredicateByIdentifyingName(paramValue) != null) {
	    this.visit((PredicateInASMultiple) pos);
	    return;
	}

    }

    private void visitSliceConditionWritten(SliceCondition<?> conditoin)
	    throws OperationNotSupportedException, Exception {

	String[] strs = paramName.split("_variable");

	SliceCondition<?> dn = (SliceCondition<?>) variables.get(Integer.parseInt(strs[1]));

	if (!paramValue.equals("")) {

	    SliceCondition<?> dnInitial = null;
	    if (dn.getSignature().isBoundVariable()) {
		dnInitial = (SliceCondition<?>) as.getInitialVarOfBoundVar(dn);
	    } else {
		dnInitial = (SliceCondition<?>) dn.shallowCopy();
	    }
	    dnInitial.setStatus(SliceConditionStatus.WRITTEN);
	    String value = paramValue;

	    MDElement elem = getMDElementOfExpression(paramValue, buildEng.getGraph());

	    if (elem != null) {
		dn.bind(SliceConditionFactory.createSliceConditionWrittenOfSignature(dn, elem, value, ""));
		initialVariables.put(dnInitial, dn);
	    } else {
		throw new Exception("Cannot bind variable with value " + value);
	    }

	    // The case of clearing the variable value
	} else {
	    if (dn.getSignature().isBoundVariable()) {

		SliceCondition<?> dnInitial = null;
		dnInitial = (SliceCondition<?>) as.getInitialVarOfBoundVar(dn);
		dn.unBind();
		initialVariables.put(dnInitial, null);
	    }
	}

    }

    @Override
    public void visit(PredicateInASSimple predicate) throws OperationNotSupportedException {
	String[] strs = paramName.split("_variable");

	PredicateInASSimple dn = (PredicateInASSimple) variables.get(Integer.parseInt(strs[1]));

	if (!paramValue.equals("")) {

	    PredicateInASSimple dnInitial = null;
	    if (dn.getSignature().isBoundVariable()) {
		dnInitial = (PredicateInASSimple) as.getInitialVarOfBoundVar(dn);
	    } else {
		dnInitial = dn.shallowCopy();
	    }

	    String value = paramValue;

	    DefinedAGPredicates preds = buildEng.getAg().getDefinedAGPredicates();

	    String[] values = value.split(WebConstants.PREDICATE_SPLITTER);

	    PredicateInASSimple dnTemp = new PredicateInASSimple((preds.getPredicateByIdentifyingName(values[0])),
		    buildEng.getGraph().getNode(values[1]));
	    dn.bind(dnTemp);
	    initialVariables.put(dnInitial, dn);

	    // The case of clearing the variable value
	} else {
	    if (dn.getSignature().isBoundVariable()) {

		PredicateInASSimple dnInitial = null;
		dnInitial = (PredicateInASSimple) as.getInitialVarOfBoundVar(dn);
		dn.unBind();
		initialVariables.put(dnInitial, null);
	    }
	}
    }

    @Override
    public void visit(MeasureInAnalysisSituation meas) {
	String[] strs = paramName.split("_variable");

	if (!paramValue.equals("")) {

	    MeasureInAnalysisSituation ms = (MeasureInAnalysisSituation) variables.get(Integer.parseInt(strs[1]));
	    MeasureInAnalysisSituation msCopy = ms.shallowCopy();

	    Mapping mapping;
	    try {
		mapping = MappingFunctions.getPathQuery(buildEng.getGraph(), factURI, ms.getIdentifyingName());
	    } catch (Exception ex) {
		mapping = new Mapping();
	    }
	    String uri = paramValue;
	    String name = paramValue.replace(addedNSForSMDInstance, "");
	    MeasureInAnalysisSituation msTemp = new MeasureInAnalysisSituation(uri, name, mapping, null);
	    ms.bind(msTemp);

	    initialVariables.put(msCopy, ms);
	}
    }

    @Override
    public void visit(AggregationOperationInAnalysisSituation aggOp) {
	String[] strs = paramName.split("_variable");

	if (!paramValue.equals("")) {

	    AggregationOperationInAnalysisSituation ag = (AggregationOperationInAnalysisSituation) variables
		    .get(Integer.parseInt(strs[1]));
	    AggregationOperationInAnalysisSituation agCopy = ag.shallowCopy();

	    AggregationOperationInAnalysisSituation aggTemp = new AggregationOperationInAnalysisSituation(paramValue,
		    paramValue, "");
	    ag.bind(aggTemp);

	    initialVariables.put(agCopy, ag);
	}
    }

    public String getParamName() {
	return paramName;
    }

    public void setParamName(String paramName) {
	this.paramName = paramName;
    }

    public String getAddedNSForSMDInstance() {
	return addedNSForSMDInstance;
    }

    public void setAddedNSForSMDInstance(String addedNSForSMDInstance) {
	this.addedNSForSMDInstance = addedNSForSMDInstance;
    }

    public Map<Variable, Variable> getInitialVariables() {
	return initialVariables;
    }

    public void setInitialVariables(Map<Variable, Variable> initialVariables) {
	this.initialVariables = initialVariables;
    }

    public Map<Integer, Variable> getVariables() {
	return variables;
    }

    public void setVariables(Map<Integer, Variable> variables) {
	this.variables = variables;
    }

    public AnalysisGraphsManager getBuildEng() {
	return buildEng;
    }

    public void setBuildEng(AnalysisGraphsManager buildEng) {
	this.buildEng = buildEng;
    }

    public IVariablesList getAs() {
	return as;
    }

    public void setAs(IVariablesList as) {
	this.as = as;
    }

    public String getFactURI() {
	return factURI;
    }

    public void setFactURI(String factURI) {
	this.factURI = factURI;
    }

    public String getParamValue() {
	return paramValue;
    }

    public void setParamValue(String paramValue) {
	this.paramValue = paramValue;
    }

    /**
     * 
     * Tries to find an MD element that can be derived from some variable name
     * in the passed expression, otherwise returns null.
     * 
     * @param expr
     * @param schema
     * @return
     */
    public static MDElement getMDElementOfExpression(String expr, MDSchema schema) {

	MDElement elem = null;

	Pattern pattern = Pattern.compile("\\?" + "[\\w]*");
	List<String> list = new ArrayList<String>();
	Matcher m = pattern.matcher(expr);
	while (m.find()) {
	    list.add(m.group());
	}

	for (String varName : list) {

	    String name = varName.replace("?", "");
	    Set<MDElement> es = schema.getNodesByIdentifyingNameSimilarity(name);
	    if (es != null && es.size() == 1) {
		return es.stream().findFirst().orElse(null);
	    }
	    Set<MDElement> es1 = schema.getNodesByName(name);
	    if (es1 != null && es1.size() == 1) {
		return es1.stream().findFirst().orElse(null);
	    }
	}
	return elem;
    }

    @Override
    public void visit(ISliceSetMultiple conditoin) {

    }

    @Override
    public void visit(ISliceSetMsr sliceSetMsr) {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(IMeasure msr) {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(MeasureDerivedInAS msr) {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(MeasureAggregated msr) {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(IMeasureInAS msr) {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(MeasureAggregatedInAS msr) {
	// TODO Auto-generated method stub

    }

    public Map<String, String[]> getAddedLabelBindings() {
	return addedLabelBindings;
    }

    public void setAddedLabelBindings(Map<String, String[]> addedLabelBindings) {
	this.addedLabelBindings = addedLabelBindings;
    }

    public Map<String, String[]> getAddedValueBindings() {
	return addedValueBindings;
    }

    public void setAddedValueBindings(Map<String, String[]> addedValueBindings) {
	this.addedValueBindings = addedValueBindings;
    }

    public List<AutoCompleteData> getAutocompleteData() {
	return autocompleteData;
    }

    public void setAutocompleteData(List<AutoCompleteData> autocompleteData) {
	this.autocompleteData = autocompleteData;
    }

    @Override
    public void visit(MeasureDerived msr) throws Exception {
	// TODO Auto-generated method stub

    }

    @Override
    public void visit(SliceConditoinGeneric<?> sliceConditoinGeneric) {
	// TODO Auto-generated method stub

    }

}
