package swag.data_handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Constants {

	
	public static final String PAIR_CLASS = "Pair";
	public static final String PARAMETER_CLASS = "Parameter";
	public static final String PARAMETER_PROP = "parameter";
	
	public final static String level = "level";

	public final static String qb_link = "https://raw.githubusercontent.com/UKGovLD/publishing-statistical-data/master/specs/src/main/vocab/cube.ttl";
	public final static String qb4o_link = "https://raw.githubusercontent.com/lorenae/qb4olap/master/rdf/qb4olap.ttl";

	public final static String QB_DATASET = "http://purl.org/linked-data/cube#dataSet";

	public final static String granularityE = "granularity";
	public final static String GRANULARITY_LEVEL_E = "granularityLevelInAG";
	public final static String diceSpecificationE = "dice";
	public final static String ON_DIMENSION_E = "dimension";
	public final static String DIMENSION_CONF = "DimensionSummarizability";
	public final static String ON_HIERARCHY_E = "hierarchy";
	public final static String ON_LEVEL_E = level;
	public final static String ON_DICE_LEVEL_E = "diceLevelInAG";
	public final static String ON_NODE_E = "diceNodeInAG";
	public final static String HAS_SLICE_E = "dimensionSelection";
	public final static String HAS_DIM_PREDICATE_E = "dimensionPredicateInAG";
	public final static String OP_ON_DIMENSION_E = "opDimension";
	public final static String OP_ON_HIERARCHY_E = "opHierarchy";
	public final static String OP_TO_LEVEL_E = "opGranularityLevel";
	public final static String MEASURE_CONDITION_E = "baseMeasurePredicateInAG";//mm
	public final static String RESULT_FILTER_E = "resultPredicateInAG";//mmS
	public final static String OP_TO_DICE_NOCE_E = "opDiceNodeInAG";
	public final static String OP_TO_DICE_LEVEL_E = "opDiceLevelInAG";
	public final static String onMeasure_E = "usesMeasure";
	public final static String aggregationOperation_E = "usesAggregationFunction";
	public final static String MEASURE_E = "resultMeasureInAG";
	public final static String OP_RESULT_FILTER_PROP_E = "opNewResultPredicateInAG";
	public final static String OP_MEASURE_SELECTION_PROP_E = "opBaseMeasurePredicateInAG";
	public final static String OP_SLICE_COND_PROP_E = "opNewDimPredicateInAG";
	public final static String FACT_CLASS_E = "cube";
	public final static String DATA_SET = "dataset";
	public final static String HAS_ANALYSIS_SITUATIO_E = "analysisSituation";
	public final static String HAS_NAVIGATION_STEP_E = "navigationStep";

	public static final String BINDING = "binding";
	public static final String PARAMETER = "parameter";

	public static final String PAIR = "Pair";
	public static final String LEVEL_PAIR = "LevelPropertyPair";
	public static final String DICE_NODE_PAIR = "LevelMemberPair";
	public static final String DIM_PREDICATE_PAIR = "GroundDimensionPredicatePair";
	public static final String RES_PREDICATE_PAIR = "GroundResultPredicatePair";
	public static final String RES_MEASURE_PAIR = "ResultMeasurePair";
	public final static String HAS_OPERATION_E = "operation";
	public final static String PREDICATE_MD_ELEMENT = "predicateMDElement";
	public final static String ON_ELEMENT = "mdElement";
	public final static String PREDICATE_EXPRESSION = "expression";
	public final static String VARIABLE_DOMAIN = "domain";
	public final static String INSTANCE_OF = "derivedFrom";

	public final static String ELEM_ON_LEVEL_E = "ofLevel";
	public final static String ELEM_ON_ATTRIBUTE_E = "ofAttribute";
	public final static String ELEM_IN_LEVEL_E = "inLevel";
	public final static String ELEM_IN_HIER_E = "inHierarchy";
	public final static String ELEM_IN_DIM_E = "inDimension";
	public final static String QUALIFIED_LEVEL_E = "QualifiedLevel";
	public final static String QUALIFIED_ATTRIBUTE_E = "QualifiedAttribute";

	public final static String MSR_SUMMARIZABILITY_E = "measureSummarizability";
	public final static String DIM_SUMMARIZABILITY_E = "dimensionSummarizability";

	public final static String INTERN_AGG = "internAgg";
	public final static String DEFAULT_VAL = "defaultValue";

	public final static String VAL = "val";
	public final static String MODE = "mode";
	public final static String NONE = "None";
	public final static String NON_STRICT = "nonStrict";
	public final static String INCOMPLETE = "incomplete";

	public static final String SMDFile = "SMD.ttl";
	public static final String AGFile = "AG.ttl";
	public static final String qbFile = "QB.ttl";
	public static final String qb4oFile = "QB4OLAP.ttl";
	public static final String SMDInstanceFile = "films.ttl";
	public static final String PredicatesFile = "predicates_1.ttl";

	public static final String internalPathToMDSAndAGONtologiesFolder = "resources/newer";
	public static final String internalPathToAGsFolder = "resources";

	public final static String hasLevel = "hasLevel";
	public final static String hasMeasure = "hasMeasure";
	public final static String rollsUpTo = "rollsUpTo";
	public final static String hasDescriptor = "hasDescriptor";

	public final static Set<String> P_MAPPED = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList(hasLevel, hasMeasure, rollsUpTo, hasDescriptor)));

	public final static String HAS_LEVEL = "HasLevel";
	public final static String HAS_MEASURE = "HasMeasure";
	public final static String HIEREARCHY_STEP = "HierarchyStep";
	public final static String HAS_ATTRIBUTE = "HasAttribute";
	public final static String IN_DIMENSION = "inDimension";
	public final static String IN_HIERARCHY = "inHierarchy";
	public final static String HAS_SCHEMA_FACT = "hasSchemaFactClass";
	public final static String HIERARCHY_IN_DIMENSION = "hierInDimension";

	// NEW MEASURES
	public final static String DERIVED_MEASURE = "DerivedMeasure";
	public final static String AGG_MEASURE = "ResultMeasure";
	public final static String AGG_MEASURE_TO_DERIVED = "aggregatedMeasure";
	public final static String DERIVED_MEASURE_EXPRESSION = "derivationExpression";
	public final static String DERIVED_MEASURE_MEASURE = "derivedFrom";

	public final static String MEASURE_CONDITION = "msrCondition";
	public final static String RESULT_FILTER = "resultFilter";

	public final static Set<String> R_MAPPED = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList(HAS_LEVEL, HAS_MEASURE, HIEREARCHY_STEP, HAS_ATTRIBUTE)));

	public final static String FROM_LEVEL = "fromLevel";
	public final static String TO_ATTRIBUTE = "toAttribute";
	public final static String FACT_OF_LEVEL = "factClassOfLevel";
	public final static String TO_LEVEL = "toLevel";
	public final static String FACT_OF_MEASURE = "factClassOfMeasure";
	public final static String TO_MEASURE = "toMeasure";
	public final static String CHILD_LEVEL = "childLevel";
	public final static String PARENT_LEVEL = "parentLevel";
	public final static String SMD_MEASURE = "Measure";

	public final static String ANALYSIS_GRAPH = "AnalysisGraph";
	public final static String HAS_ANALYSIS_SITUATION = "hasAnalysisSituation";
	public final static String HAS_NAVIGATION_STEP = "hasNavigationStep";

	public final static String onSchema = "onSchema";
	public final static String analysisSituation = "AnalysisSituation";
	public final static String hasSPARQLService = "hasSPARQLService";
	public final static String analysisSituationFact = "hasFactClass";
	public final static String analysisSituationDimensionToAnalysisSituation = "hasDimQualification";
	public final static String analysisSituationMeasureToAnalysisSituation = "hasMsrSpecification";
	public final static String ON_DIMENSION = "onDimension";
	public final static String ON_HIERARCHY = "onHierarchy";
	public final static String granularity = "hasGranularitySpecification";
	public final static String variable = "Variable";
	public final static String diceSpecification = "hasDiceSpecification";
	public final static String HAS_SLICE_SET = "slice";
	public final static String opDiceSpecification = "opHasDiceSpecification";
	public final static String diceLevel = "hasDiceLevel";

	public final static String OP_TO_DICE_LEVEL = "opToDiceLevel";

	public final static String SIMPLE_CONDITION = "SimpleCondition";

	public final static String SLICE_SET_POS = "hasSlicePos";
	public final static String SLICE_SET = "hasSliceSet";

	public final static String CONTAINS_CONDITOIN = "hasCondition";

	public final static String diceNode = "hasDiceNode";

	public final static String OP_TO_DICE_NOCE = "opToDiceNode";

	public final static String value = "Value";
	public final static String hasValue = "hasValue";

	public final static String ON_LEVEL = "onLevel";
	public final static String ON_ATTRIBUTE = "onAttribute";

	public final static String PRED_TYPE = "http://www.dke.jku.ac.at/swag/pr#predType";

	public final static String SLICE_SPECIFICATION_PROP = "hasDimSliceSpecification";
	public final static String OP_SLICE_COND_PROP = "opDimCondition";
	public final static String OP_SLICE_COND_TYPED_PROP = "opDimConditionType";

	public final static String OP_RESULT_FILTER_PROP = "opCondition";
	public final static String OP_RESULT_FILTER_TYPE_PROP = "opConditionType";

	public final static String MULTIPLE_SLICE_SPECIFICATION_PROP = "hasMultipleSliceSpecification";
	public final static String OP_SLICE_SPECIFICATION_PROP = "opHasDimSliceSpecification";
	public final static String COMPARISON_OPERATOR = "hasComparisonOperator";
	public final static String COMPARISON_OPERAND = "hasOperand";

	public final static String slicePosition = "hasDimSlicePosition";
	public final static String sliceCondition = "hasDimSliceCondition";
	public final static String onMeasure = "hasMeasure";
	public final static String aggregationOperation = "hasAggFunction";
	public final static String hasMapping = "hasMapping";
	public final static String hasMappingBody = "hasMappingBody";
	public final static String hasString = "hasString";
	public final static String reifiedMapping = "ReifiedMapping";
	public final static String fromStr = "from";
	public final static String toStr = "to";
	public final static String propMapping = "propMapping";

	public final static String ELEM_ON_HIER = "hierOfHierInDim";
	public final static String ELEM_ON_DIM = "dimOfHierInDim";

	public final static String ELEM_ON_LEVEL = "onLevelProperty";
	public final static String ELEM_ON_ATTRIBUTE = "onLevelAttribute";
	public final static String ELEM_ON_LEVEL_IN_HIER_AND_DIM = "onLevelInHierAndDim";

	public final static String ELEM_ON_HIER_IN_DIM = "onHierInDim";

	public final static String COND_ON_ELEM = "condOnElem";
	public final static String COND_HAS_VALUE = "hasValue";

	public final static String COMP_FACT_CLASS = "hasCompFactClass";

	public final static String COMP_DIM_QUAL = "hasCompDimQualification";

	public final static String COMP_MSR_SPEC = "hasCompMsrSpecification";

	public final static String COMP_GRANULARITY = "hasCompGranularitySpecification";

	public final static String COMP_DICE_SPECIFICATION = "hasCompDiceSpecification";
	public final static String COMP_SLICE_SPECIFICATION = "hasCompDimSliceSpecification";
	public final static String COMP_MULTIPLE_SLICE_SPECIFICATION_PROP = "hasCompMultipleSliceSpecification";

	public final static String PredicateInstanceClass = "PredicateInstanceInAG";
	public final static String VAR_TO_ELEMENT_MAPPING = "varToElementMapping";
	public final static String HAS_VAR_TO_ELEMENT_MAPPING = "hasVarToElementMapping";
	public final static String HAS_MD_ELEM = "hasMappingElement";

	public final static String MULTIPLE_HAS_VAR_TO_ELEMENT_MAPPING = "hasMultipleVarToElemMapping";
	public final static String MULTIPLE_HAS_MD_ELEM = "predOnElem";

	public final static String MEASURE_HAS_VAR_TO_ELEMENT_MAPPING = "hasMsrVarToElemMapping";
	public final static String MEASURE_HAS_MD_ELEM = "predOnMeasure";

	public final static String DIM_HAS_VAR_TO_ELEMENT_MAPPING = "hasDimVarToElemMapping";
	public final static String DIM_HAS_MD_ELEM = "predOnDimElem";

	public final static String POS_HIERARCHY = "posOnHier";
	public final static String POS_DIMENSION = "posOnDim";
	public final static String POS_POS = "posOnElem";
	public final static String HAS_COMPLEX_POS = "hasComplexPos";

	public final static String HAS_VAR = "hasMappingVar";
	public final static String HAS_CONNECT_OVER = "connectOver";
	public final static String ON_PREDICATE = "onPredicate";
	public final static String HAS_MAPPING_ELEMENT = "hasMappingElement";

	public final static String HAS_SET = "hasSet";
	public final static String COMPARATIVE_AS = "ComparativeAnalysisSituation";
	public final static String ON_SET = "onSet";
	public final static String NONE_SET = "Shared";

	public final static String POS_ON_HIERARCHY = "posOnHier";
	public final static String POS_ON_DIMENSION = "posOnDim";
	public final static String POS_ON_ELEMENT = "posOnElem";

	public final static String navigationStep = "NavigationStep";
	public final static String summary = "summary";
	public final static String source = "source";
	public final static String target = "target";

	public final static String rollsUpDirectlyTo = "rollsUpDirectlyTo";

	public final static String RollUpOpClass = "RollUp";
	public final static String RollUpToOpClass = "RollUpTo";
	public final static String DrillDownOpClass = "DrillDown";
	public final static String DrillDownToOpClass = "DrillDownTo";
	public final static String OP_TO_GRANULARITY = "opToGranularity";
	public final static String MoveToDiceNodeOpClass = "MoveToLevelAndNode";
	public final static String navToDiceValue = "opHasDiceNode";
	public final static String AddDimensionSelectionOperator = "AddDimPredicate";
	public final static String AddDimTypedConditoinOperator = "AddTypedDimensionSelectoin";
	public final static String AddResultSelectionOperator = "AddResultPredicate";
	public final static String AddBaseMeasureSeletionOperator = "AddBaseMeasurePredicate";
	public final static String MoveDownToDiceNodeOpClass = "MoveDownToNode";
	public final static String MoveUpToDiceNodeOpClass = "MoveUpToNode";
	public final static String MoveToNextDiceNodeOpClass = "MoveToNextNode";
	public final static String MoveToPrevDiceNodeOpClass = "MoveToPreviousNode";
	public final static String AddMeasureOpClass = "AddResultMeasure";
	public final static String navMeasureToAS = "navMeasureToAS";
	public final static String ModifySliceConditionOpClass = "ReplaceDimPredicate";
	public final static String ChangeSliceOpClass = "ReplaceDimPredicate";
	public final static String navToSliceCond = "opHasDimSliceCondition";
	public final static String opHasSliceSpecification = "opHasSliceSpecification";

	public final static String OP_ON_DIMENSION = "opOnDimension";
	public final static String OP_ON_HIERARCHY = "opOnHierarchy";
	public final static String hasOperator = "hasOperation";

	public final static String descriptor = "descriptor";
	public final static String measure = "measure";
	public final static String fact = "FactClass";
	public final static String levelClass = "Level";
	public final static String dimension = "dimension";
	public final static String belongsToDimension = "inDimension";

	public final static String mdSchema = "MDSchema";

	public final static String HAS_DIM_CONFIGURATION = "hasDimConfiguration";
	public final static String HAS_MSR_CONFIGURATION = "hasMsrConfiguration";

	public final static String HAS_NON_STRICT_MODE = "hasNonStrictMode";
	public final static String HAS_INCOMPLETE_MODE = "hasIncompleteMode";
	public final static String HAS_MANDATORY_MODE_MODE = "hasMandatoryMode";

	public final static String HAS_MISSING_VAL_MODE = "hasMissingValMode";
	public final static String HAS_INTERNAL_AGG_MODE = "hasInternalAggMode";

	public final static String NON_STRICT_MODE = "NonStrictMode";
	public final static String INCOMPLETE_MODE = "IncompleteMode";
	public final static String MANDATORY_MODE_MODE = "NonStrictMode";

	public final static String MISSING_VAL_MODE = "MissingValMode";

	public static final String QB = "QB";
	public static final String QB_NS = "http://purl.org/linked-data/cube#";

	public static final String QB4O = "QB4O";
	public static final String QB4O_NS = "http://purl.org/qb4olap/cubes#";

	public static final String SMD = "SMD";
	public static final String SMD_NS = "http://www.dke.jku.ac.at/swag/smd#";

	public static final String SMD_INS = "SMD_INS";

	public static final String AG = "AG";
	public static final String AG_NS = "http://www.dke.jku.ac.at/swag/ag#";

	public static final String AG_INS = "AG_INS";

	public static final String QB4O_DATA_STRUCTURE_DEF = "http://purl.org/linked-data/cube#DataStructureDefinition";

	public static final String QB4O_COMPONENT = "http://purl.org/linked-data/cube#component";
	public static final String QB_MEASURE = "http://purl.org/linked-data/cube#measure";
	public static final String QB4O_LEVEL = "http://purl.org/qb4olap/cubes#level";
	public static final String QB4O_CARDINALITY = "http://purl.org/qb4olap/cubes#cardinality";
	public static final String QB_MEASURE_PROPERTY = "http://purl.org/linked-data/cube#MeasureProperty";
	public static final String QB4O_LEVEL_PROPERTY = "http://purl.org/qb4olap/cubes#LevelProperty";
	public static final String QB4O_LEVEL_ATTRIBUTE = "http://purl.org/qb4olap/cubes#LevelAttribute";
	public static final String QB_DIMENSION_PROPERTY = "http://purl.org/linked-data/cube#DimensionProperty";

	public static final String QB4O_HIERARCHY = "http://purl.org/qb4olap/cubes#Hierarchy";
	public static final String QB4O_HAS_ATTRIBUTE = "http://purl.org/qb4olap/cubes#hasAttribute";
	public static final String QB4O_HAS_HIERARCHY = "http://purl.org/qb4olap/cubes#hasHierarchy";
	public static final String QB4O_IN_DIMENSION = "http://purl.org/qb4olap/cubes#inDimension";
	public static final String QB4O_HAS_LEVEL = "http://purl.org/qb4olap/cubes#hasLevel";
	public static final String QB4O_ROLLUP_PROPERTY = "http://purl.org/qb4olap/cubes#RollupProperty";
	public static final String QB4O_HIERARCHY_STEP = "http://purl.org/qb4olap/cubes#HierarchyStep";
	public static final String QB4O_IN_HIERARCHY = "http://purl.org/qb4olap/cubes#inHierarchy";
	public static final String QB4O_PARENT_LEVEL = "http://purl.org/qb4olap/cubes#parentLevel";
	public static final String QB4O_CHILD_LEVEL = "http://purl.org/qb4olap/cubes#childLevel";
	public static final String QB4O_PC_CARDINALITY = "http://purl.org/qb4olap/cubes#pcCardinality";
	public static final String QB4O_ROLLUP = "http://purl.org/qb4olap/cubes#rollup";
	public static final String QB4O_AGGREGATION_FUNCTION = "http://purl.org/qb4olap/cubes#aggregateFunction";

}
