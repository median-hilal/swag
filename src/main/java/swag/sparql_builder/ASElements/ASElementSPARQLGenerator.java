package swag.sparql_builder.ASElements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.naming.OperationNotSupportedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AggregationOperationInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasure;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.IMultipleSliceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceMultiplePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMsr;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
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
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditoinGeneric;
import swag.analysis_graphs.execution_engine.analysis_situations.SlicePositionInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceSet;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.md_elements.MappingFunctions;
import swag.md_elements.Measure;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;

/**
 * 
 * Generates SPARQL fragments that express specific elements of the bound analysis situation.
 * 
 * @author swag
 *
 */
public class ASElementSPARQLGenerator implements IASElementGenerateSPARQLVisitor {

  private Query asQuery;
  private List<String> returned;
  private AnalysisGraph ag;
  Map<MDElement, String> mdElemToVarMap;
  private static final Logger logger = Logger.getLogger(ASElementSPARQLGenerator.class);


  public ASElementSPARQLGenerator(AnalysisGraph ag, Query asQuery,
      Map<MDElement, String> mdElemToVarMap) {
    super();
    this.ag = ag;
    this.asQuery = asQuery;
    this.mdElemToVarMap = mdElemToVarMap;

  }

  public ASElementSPARQLGenerator(AnalysisGraph ag, Map<MDElement, String> mdElemToVarMap) {
    super();
    this.ag = ag;
    this.mdElemToVarMap = mdElemToVarMap;
  }

  public List<String> getReturn() {
    if (returned == null) {
      returned = new ArrayList<>();
    }
    return returned;
  }

  public void visit(IGranularitySpecification gsi) {
    returned = new ArrayList<>();
    logger.error("this operation is not supported for granularities");
  }

  public void visit(IDiceSpecification dsi) {

    returned = new ArrayList<>();

    if (!dsi.getDiceNodeInAnalysisSituation().getSignature().isVariable()
        && !dsi.getPosition().getSignature().isVariable()) {

      LevelInAnalysisSituation lvl = dsi.getPosition();
      // the diceLevel query is added as-is in case it is neither null nor a variable. It needs to
      // be joined in body, as it adds no header variables


      DiceNodeInAnalysisSituation node = dsi.getDiceNodeInAnalysisSituation();
      // treating dice value
      if (node != null && // null dice values do not cause anything
          !node.getNodeValue().equals("") && // empty dice values do not cause anything
          !node.getSignature().isVariable()) {
        // equality expression. Left is the diceLevel, right is a temporal variable to be replaced
        // by the value read from the file
        Expr e = new E_Equals(new ExprVar(lvl.getMapping().getQuery().getUnaryProjectionVar()),
            new ExprVar("temporaryExpression"));
        ElementFilter filter = new ElementFilter(e); // Make a filter matching the expression

        // here the replacement takes place
        ParameterizedSparqlString queryString = new ParameterizedSparqlString(filter.toString());
        Model m = ModelFactory.createDefaultModel();
        org.apache.jena.datatypes.TypeMapper tm = new org.apache.jena.datatypes.TypeMapper();
        String str = node.getNodeValue();
        if (str.contains("^^")) { // the dice value is a data typed value (literal)
          String[] tmp = str.split("\\^\\^");
          String part1 = tmp[0].substring(0, tmp[0].length());
          String part2 = tmp[1].substring(0, tmp[1].length());
          Literal lit = m.createTypedLiteral(part1, part2);
          queryString.setLiteral("temporaryExpression", lit);
        } else { // the dice value is a URI
          if (str.startsWith("http://")) {
            queryString.setIri("temporaryExpression", str);
            // queryString.setLiteral("temporaryExpression", str);
          } else { // dice node has no type, considered string by default
            Literal lit = m.createTypedLiteral(str, "http://www.w3.org/2001/XMLSchema#string");
            queryString.setLiteral("temporaryExpression", lit);
          }
        }
        returned.add(queryString.toString());
      }
    }
  }

  public void visit(IMultipleSliceSpecification ssi) throws Exception {

    returned = new ArrayList<>();

    IMultipleSliceSpecification pred = (IMultipleSliceSpecification) ssi;
    if (!pred.getPredicate().getSignature().isVariable()) {


      PredicateInASMultiple cond = pred.getPredicate();
      // treating slice condition
      if (cond != null) {
        StringBuilder builder = new StringBuilder(200);
        builder.append(
            SliceConditionSPARQLGenerator.generatePredicateInASQuery(pred.getPredicate(), ag));
        this.returned.add(builder.toString());

      }
    }
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
  public void visit(MeasureInAnalysisSituation msr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(LevelInAnalysisSituation level) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(DiceNodeInAnalysisSituation node) throws OperationNotSupportedException {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(SlicePositionInAnalysisSituation position) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(SliceSet conditoin) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ISliceSetMultiple conditoin) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(PredicateInASMultiple predicate) throws OperationNotSupportedException {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(PredicateInASSimple predicate) throws OperationNotSupportedException {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(AggregationOperationInAnalysisSituation aggOp) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ISliceMultiplePosition<?> pos) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ISliceSinglePosition cond) throws Exception {
    // if (cond.getSignature().isBoundVariable()) {
    returned = new ArrayList<>();
    String filter =
        SliceConditionSPARQLGenerator.generateConditionnASQuery(cond, ag, mdElemToVarMap);
    returned.add(filter);
    // }
  }

  @Override
  public void visit(ISliceSinglePositionNoType<?> cond) throws Exception {
    visit((ISliceSinglePosition) cond);
  }

  @Override
  public void visit(ISliceSinglePositionTyped<?> cond) throws Exception {
    visit((ISliceSinglePosition) cond);
  }

  @Override
  public void visit(MeasureDerivedInAS msr) throws Exception {

    returned = new ArrayList<>();

    MDSchema schema = ag.getSchema();

    CustomSPARQLQuery query = new CustomSPARQLQuery();

    for (Measure baseMsr : msr.getMeasures()) {
      query = query.joinWithOnlyPatternsAsGroups(MappingFunctions
          .getPathQueryAndKeepIndividualMappingBlocksExclusive(schema,
              schema.getFactOfSchema().getIdentifyingName(), baseMsr.getIdentifyingName())
          .getQuery());
    }

    String str =
        SPARQLUtilities.encapsulateQuyerInSelect(query.getSparqlQuery(), msr.getExpression());

    if (!StringUtils.isEmpty(str)) {
      this.returned.add(str);
    }
  }

  @Override
  public void visit(MeasureAggregatedInAS msr) {
    // TODO Auto-generated method stub
  }

  @Override
  public void visit(IMeasureInAS msr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(MeasureDerived msr) throws Exception {
    returned = new ArrayList<>();

    MDSchema schema = ag.getSchema();
    CustomSPARQLQuery query = new CustomSPARQLQuery();
    String str = "";

    if (msr.isDerived()) {

      for (Measure baseMsr : msr.getMeasures()) {
        query = query.joinWithOnlyPatternsAsGroups(MappingFunctions
            .getPathQueryAndKeepIndividualMappingBlocksExclusive(schema,
                schema.getFactOfSchema().getIdentifyingName(), baseMsr.getIdentifyingName())
            .getQuery());
      }
      str = SPARQLUtilities.encapsulateQuyerInSelect(query.getSparqlQuery(), msr.getExpression());

    } else {

      query =
          query.joinWithOnlyPatternsAsGroups(MappingFunctions
              .getPathQueryAndKeepIndividualMappingBlocksExclusive(schema,
                  schema.getFactOfSchema().getIdentifyingName(), msr.getIdentifyingName())
              .getQuery());
      query.getSparqlQuery().getProjectVars().add(msr.getHeadVar());
      str = query.toString();
    }

    if (!StringUtils.isEmpty(str)) {
      this.returned.add(str);
    }

  }

  @Override
  public void visit(MeasureAggregated m) throws Exception {
    visit(m.getMeasure());
  }

  @Override
  public void visit(SliceConditoinGeneric<?> sliceConditoinGeneric) {
    // TODO Auto-generated method stub

  }

}
