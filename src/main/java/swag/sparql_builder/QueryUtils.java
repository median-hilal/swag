package swag.sparql_builder;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.TripleCollectorBGP;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.md_elements.*;
import swag.predicates.LiteralCondition;
import swag.predicates.PredicateSyntacticTypes;

public class QueryUtils {

	public QueryUtils(MDSchema schema) {
		super();
		this.schema = schema;
	}

	MDSchema schema;

	public Var getVarOfLevel(Dimension d, Level l) {
		return getVarOfLevel(d.getName(), l.getName());
	}

	public Var getVarOfLevel(String d, String l) {
		String name = getLocalName(d).substring(0,2) + "_" + getLocalName(l);
		return Var.alloc(name);
	}

	private String getLocalName(String str){
		String newStr = str;

		int index = newStr.lastIndexOf("#");

		if (index == -1){
			return str;
		}

		newStr = newStr.substring(index + 1, newStr.length());

		return newStr;
	}

	public Var getVarOfLevelAttribute(String d, String l, String  a) {
		String name = getLocalName(d).substring(0,2) +
				"_" + getLocalName(l).substring(0,2) +
				"_" + getLocalName(a);
		return Var.alloc(name);
	}

	public Var getVarOfMeasure(String m) {
		return Var.alloc(getLocalName(m));
	}

	public Var getVarOfFact(String f) {
		return Var.alloc(getLocalName(f));
	}

	public TripleCollectorBGP getTriplesOfLevel(String d, String l) {

		TripleCollectorBGP bgp = new TripleCollectorBGP();
		Triple triple = new Triple(getVarOfLevel(d, l), NodeFactory.createURI("http://purl.org/qb4olap/cubes#memberOf"),
				NodeFactory.createURI(l));
		bgp.addTriple(triple);
		return bgp;
	}

	public TripleCollectorBGP getTriplesOfRollUp(String d, String l, String d2, String l2) {

		TripleCollectorBGP bgp = new TripleCollectorBGP();

		MDRelation rel = schema.getRollUpOrHasAttributeProperty(l, d, l2, d2);

		if (rel instanceof QB4OHierarchyStep) {
			Triple triple1 = new Triple(getVarOfLevel(d, l),
					NodeFactory.createURI("http://purl.org/qb4olap/cubes#memberOf"), NodeFactory.createURI(l));
			SetBasedBGP.addTripleToBgp(bgp, triple1);
			Triple triple2 = new Triple(getVarOfLevel(d, l), NodeFactory.createURI(rel.getURI()),
					getVarOfLevel(d, l2));
			SetBasedBGP.addTripleToBgp(bgp, triple2);
		}else{
			Triple triple1 = new Triple(getVarOfLevel(d, l), NodeFactory.createURI(l2),
					getVarOfLevelAttribute(d, l, l2));
			SetBasedBGP.addTripleToBgp(bgp, triple1);
		}
		return bgp;
	}

	public TripleCollectorBGP getTriplesOfPathMeasuure(String msr) {
		TripleCollectorBGP bgp = new TripleCollectorBGP();

		Triple triple1 = new Triple(getVarOfFact(schema.getFactOfSchema().getURI()),
				 NodeFactory.createURI(msr), getVarOfMeasure(msr));
		SetBasedBGP.addTripleToBgp(bgp, triple1);

		return bgp;
	}


	public TripleCollectorBGP getTriplesOfPath(String d, String l, String l2) {

		TripleCollectorBGP bgp = new TripleCollectorBGP();

		Triple tt =  new Triple(getVarOfFact(schema.getFactOfSchema().getURI()),
				NodeFactory.createURI(schema.getFinestLevelOnDimension1(d).getURI()),
				getVarOfLevel(d,schema.getFinestLevelOnDimension1(d).getURI()));

		SetBasedBGP.addTripleToBgp(bgp, tt);

		List<MDElement> path = new LinkedList<>();

		path = schema.getPath(path, l2, l, d);
		Collections.reverse(path);

		for (int i = 0; i < path.size(); i++) {

			if(path.get(i) instanceof Level){
				for (Triple t : getTriplesOfLevel(d, path.get(i).getURI()).getBGP().getList()) {
					SetBasedBGP.addTripleToBgp(bgp, t);
				}
			}

			if (i < path.size() - 1) {
				for (Triple t : getTriplesOfRollUp(d, path.get(i).getURI(), d, path.get(i + 1).getURI()).getBGP()
						.getList()) {
					SetBasedBGP.addTripleToBgp(bgp, t);
				}
			}
		}
		return bgp;
	}

	public String generateConditionnASQuery(ISliceSinglePosition predicate, String dimension, AnalysisGraph ag) throws Exception {

		String condStr = StringUtils.EMPTY;
		LiteralCondition cond =
				ag.getDefinedAGConditions().getConditoinByIdentifyingName(predicate.getURI());

		if (cond != null) {
			Set<PredicateVariableToMDElementMapping> varofElemNams = cond.getMappings();
			Set<String> varNames =
					varofElemNams.stream().map(x -> x.getVar().getVariable()).collect(Collectors.toSet());
			condStr = cond.getExpression();

			for (PredicateVariableToMDElementMapping v : varofElemNams) {

				Var var = null;

				if (v.getElem() instanceof Level){
					var = getVarOfLevel(dimension, v.getElem().getURI());
				}else{
					if (v.getElem() instanceof Descriptor){
						Level level = schema.getLevelOfDescriptor1(v.getElem().getURI());
						var = getVarOfLevelAttribute(dimension, level.getURI(), v.getElem().getURI());
					}
				}

					// Otherwise fallback to default name from the MD schema
					condStr =
							condStr.replace(v.getVar().getVariable(), "?" + (var.getVarName()));

			}

			for (Map.Entry<String, String> binding : cond.getBindings().entrySet()) {
				condStr = condStr.replaceFirst(cond.getDirectInputVarByUri(binding.getKey()),
						SPARQLUtilities.literalzeConditoinOperand(binding.getValue()));
			}

			PredicateSyntacticTypes t = cond.getSyntacticType();

			switch (t) {
				case FILTER:
					condStr = "FILTER( " + condStr + " )";
					break;

				case DEFAULT:
					break;

				case HAVING:
					/*
					 * Having clause is not added as no multiple HAVING clauses are allowed. The calling
					 * routine should add HAVING clause.
					 */
					break;

				default:
					break;
			}
		}
		return condStr;
	}

	public String generateConditionnASQueryMsr(ISliceSinglePosition predicate, AnalysisGraph ag) throws Exception {

		String condStr = StringUtils.EMPTY;
		LiteralCondition cond =
				ag.getDefinedAGConditions().getConditoinByIdentifyingName(predicate.getURI());

		if (cond != null) {
			Set<PredicateVariableToMDElementMapping> varofElemNams = cond.getMappings();
			Set<String> varNames =
					varofElemNams.stream().map(x -> x.getVar().getVariable()).collect(Collectors.toSet());
			condStr = cond.getExpression();

			for (PredicateVariableToMDElementMapping v : varofElemNams) {

				Var var = null;

				var = getVarOfMeasure(v.getElem().getURI());


				// Otherwise fallback to default name from the MD schema
				condStr =
						condStr.replace(v.getVar().getVariable(),
								((MeasureAggregated) predicate.getPositionOfCondition()).getAgg()
										+ "(" + getVarOfMeasure(((MeasureAggregated) predicate.getPositionOfCondition()).getMeasure().getURI()) + ")");

			}

			for (Map.Entry<String, String> binding : cond.getBindings().entrySet()) {
				condStr = condStr.replaceFirst(cond.getDirectInputVarByUri(binding.getKey()),
						SPARQLUtilities.literalzeConditoinOperand(binding.getValue()));
			}

			PredicateSyntacticTypes t = cond.getSyntacticType();

			switch (t) {
				case FILTER:
					condStr = "FILTER( " + condStr + " )";
					break;

				case DEFAULT:
					break;

				case HAVING:
					/*
					 * Having clause is not added as no multiple HAVING clauses are allowed. The calling
					 * routine should add HAVING clause.
					 */
					break;

				default:
					break;
			}
		}
		return condStr;
	}
}
