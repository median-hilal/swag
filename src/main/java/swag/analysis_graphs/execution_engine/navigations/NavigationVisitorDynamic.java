package swag.analysis_graphs.execution_engine.navigations;

import org.apache.log4j.Logger;

import swag.analysis_graphs.dao.IAnalysisGraphDAO;
import swag.analysis_graphs.dao.IDataDAO;
import swag.analysis_graphs.dao.IMDSchemaDAO;
import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.CloneUtils;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureToAnalysisSituaitonImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.analysis_graphs.execution_engine.operators.AddBaseMeasureSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.AddDimTypedSliceConditoinOperator;
import swag.analysis_graphs.execution_engine.operators.AddDimensionSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.AddMeasureOperator;
import swag.analysis_graphs.execution_engine.operators.AddResultSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.ChangeGranularityOperator;
import swag.analysis_graphs.execution_engine.operators.DrillDownOperator;
import swag.analysis_graphs.execution_engine.operators.DrillDownToOperator;
import swag.analysis_graphs.execution_engine.operators.IOperatorVisitor;
import swag.analysis_graphs.execution_engine.operators.MoveDownToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToNextDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToPreviousDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveUpToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.NullOperator;
import swag.analysis_graphs.execution_engine.operators.RollUpOperator;
import swag.analysis_graphs.execution_engine.operators.RollUpToOperator;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;

/**
 * 
 * Performs the navigation given an operator, i.e., generates the target
 * analysis situation from the operator and the source analysis situatoin.
 * 
 * @author swag
 *
 */
public class NavigationVisitorDynamic implements IOperatorVisitor {

    private static final Logger logger = Logger.getLogger(NavigationVisitorDynamic.class);

    private AnalysisSituation src;
    private AnalysisSituation des;
    private MDSchema schema;
    private IMDSchemaDAO mdDao;
    private IAnalysisGraphDAO agDao;
    private IDataDAO dataDao;

    public AnalysisSituation getReturn() {
	return src;
    }

    public NavigationVisitorDynamic(AnalysisSituation src, AnalysisSituation des, MDSchema schema, IMDSchemaDAO mdDao,
	    IAnalysisGraphDAO agDao, IDataDAO dataDao) {
	super();
	this.src = src;
	this.des = des;
	this.schema = schema;
	this.mdDao = mdDao;
	this.agDao = agDao;
	this.dataDao = dataDao;
    }

    @Override
    public void visit(RollUpOperator up) throws NavigateException {
	try {

	    // getting the granularity level on the required dimension on the
	    // source analysis situation
	    Level prevLevel = schema.getPreviousLevel(src
		    .getDimToASByDimAndHierName(up.getOnDimension().getIdentifyingName(),
			    up.getOnHierarchy().getIdentifyingName())
		    .getGranularities().get(0).getPosition().getIdentifyingName());

	    // getting the level to roll up to
	    Level nextRollUpLevel = schema.getNextLevelInHierarchy(prevLevel.getIdentifyingName(),
		    up.getOnHierarchy().getIdentifyingName(), up.getOnDimension().getIdentifyingName());

	    LevelInAnalysisSituation sourceGranularityLevel = src
		    .getDimToASByDimAndHierName(up.getOnDimension().getIdentifyingName(),
			    up.getOnHierarchy().getIdentifyingName())
		    .getGranularities().get(0).getPosition();

	    // if the source granularity level is a variable; then no
	    // modification is required in the destination
	    if (!sourceGranularityLevel.getSignature().isVariable()) {

		// getting the initial (unbound) value of the granularity level
		LevelInAnalysisSituation sourceInitialVarOfSourceGranularityLevel = (LevelInAnalysisSituation) src
			.getInitialVarOfBoundVar(sourceGranularityLevel);

		// the source granularity level was a variable (but removed via
		// instantiation)
		if (sourceInitialVarOfSourceGranularityLevel != null) {
		    LevelInAnalysisSituation desGranularityLevel = des
			    .getDimToASByDimAndHierName(up.getOnDimension().getIdentifyingName(),
				    up.getOnHierarchy().getIdentifyingName())
			    .getGranularities().get(0).getPosition();

		    if (desGranularityLevel.getSignature().isVariable()) {
			LevelInAnalysisSituation initialDesGranularityLevelVariable = desGranularityLevel.shallowCopy();
			Integer indexOfVarToRemoveFromDes = des.getKeyOfVariableInVariables(desGranularityLevel);

			if (indexOfVarToRemoveFromDes >= 0) {

			    LevelInAnalysisSituation granLevelToAssignToDes = new LevelInAnalysisSituation(
				    nextRollUpLevel.getIdentifyingName(), nextRollUpLevel.getName(),
				    nextRollUpLevel.getMapping(),
				    new Signature<IDimensionQualification>(
					    des.getDimToASByDimAndHierName(up.getOnDimension().getIdentifyingName(),
						    up.getOnHierarchy().getIdentifyingName()),
					    ItemInAnalysisSituationType.GranularityLevel, VariableState.NON_VARIABLE,
					    "", null),
				    nextRollUpLevel.getLabel());
			    des.getVariables().get(indexOfVarToRemoveFromDes)
				    .assignFromSourceVar(granLevelToAssignToDes);
			    des.getInitialVariables().put(initialDesGranularityLevelVariable,
				    des.getVariables().get(indexOfVarToRemoveFromDes));
			    des.getVariables().remove(indexOfVarToRemoveFromDes);

			}
		    }
		}
	    }
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    logger.error("Error performing roll Up operation. ", ex);
	    throw new NavigateException("Error performing roll Up operation. ", ex);
	}
    }

    @Override
    public void visit(ChangeGranularityOperator op) throws NavigateException {

	/*
	 * try{ // getting the level to roll up to Level nextRollUpLevel =
	 * op.getToLevel(); nextRollUpLevel.setMapping(handler.
	 * getMappingQueryBetweenTwoElementsByURI(up.getToLevel().getURI(),
	 * des.getFact().getURI()));
	 * 
	 * // getting the granularity level on the required dimension on the
	 * source analysis situation LevelInAnalysisSituation
	 * sourceGranularityLevel =
	 * src.getDimToASByDimName(up.getOnDimension().getURI()).
	 * getGranularityLevel(); // if the source granularity level is a
	 * variable; then no modification is required in the destination if (!
	 * sourceGranularityLevel.getSignature().isVariable()){ // getting the
	 * initial (unbound) value of the granularity level
	 * LevelInAnalysisSituation sourceInitialVarOfSourceGranularityLevel =
	 * (LevelInAnalysisSituation)
	 * src.getInitialVarOfBoundVar(sourceGranularityLevel); // the source
	 * granularity level was a variable (but removed via instantiation) if
	 * (sourceInitialVarOfSourceGranularityLevel!= null){
	 * des.getDimToASByDimName(up.getOnDimension().getURI())
	 * .getGranularityLevel() .assignFromSourceVar( new
	 * LevelInAnalysisSituation( up.getToLevel().getURI(),
	 * up.getToLevel().getName(), up.getToLevel().getMapping(), new
	 * DimensionToAnalysisSituationItemSignature(
	 * des.getDimToASByDimName(up.getOnDimension().getURI()),
	 * ItemInAnalysisSituationType.GranularityLevel, false, ""))); } }
	 * }catch(Exception ex){ throw new NavigateException(); }
	 * 
	 * 
	 * try{ up.getToLevel().setMapping(handler.
	 * getMappingQueryBetweenTwoElementsByURI(up.getToLevel().getURI(),
	 * des.getFact().getURI()));
	 * des.getDimToASByDimName(up.getOnDimension().getURI()).
	 * setGranularityLevel(new
	 * LevelInAnalysisSituation(up.getToLevel().getURI(),
	 * up.getToLevel().getName(), up.getToLevel().getMapping(), new
	 * DimensionToAnalysisSituationItemSignature(src.getDimToASByDimName(up.
	 * getOnDimension().getURI()),
	 * ItemInAnalysisSituationType.GranularityLevel, false, ""))); return
	 * src; }catch(Exception ex){ throw new NavigateException(); }
	 */
    }

    @Override
    public void visit(DrillDownOperator down) throws NavigateException {

	try {

	    // getting the level to drill down to
	    Level previousRollUpLevel = schema.getPreviousLevel(src
		    .getDimToASByDimAndHierName(down.getOnDimension().getIdentifyingName(),
			    down.getOnHierarchy().getIdentifyingName())
		    .getGranularities().get(0).getPosition().getIdentifyingName());

	    // getting the granularity level on the required dimension on the
	    // source analysis situation
	    LevelInAnalysisSituation sourceGranularityLevel = src
		    .getDimToASByDimAndHierName(down.getOnDimension().getIdentifyingName(),
			    down.getOnHierarchy().getIdentifyingName())
		    .getGranularities().get(0).getPosition();

	    // if the source granularity level is a variable; then no
	    // modification is required in the destination
	    if (!sourceGranularityLevel.getSignature().isVariable()) {

		// getting the initial (unbound) value of the granularity level
		LevelInAnalysisSituation sourceInitialVarOfSourceGranularityLevel = (LevelInAnalysisSituation) src
			.getInitialVarOfBoundVar(sourceGranularityLevel);

		// the source granularity level was a variable (but removed via
		// instantiation)
		if (sourceInitialVarOfSourceGranularityLevel != null) {
		    LevelInAnalysisSituation desGranularityLevel = des
			    .getDimToASByDimAndHierName(down.getOnDimension().getIdentifyingName(),
				    down.getOnHierarchy().getIdentifyingName())
			    .getGranularities().get(0).getPosition();

		    if (desGranularityLevel.getSignature().isVariable()) {
			LevelInAnalysisSituation initialDesGranularityLevelVariable = desGranularityLevel.shallowCopy();
			Integer indexOfVarToRemoveFromDes = des.getKeyOfVariableInVariables(desGranularityLevel);

			if (indexOfVarToRemoveFromDes >= 0) {

			    LevelInAnalysisSituation granLevelToAssignToDes = new LevelInAnalysisSituation(
				    previousRollUpLevel.getIdentifyingName(), previousRollUpLevel.getName(),
				    previousRollUpLevel.getMapping(),
				    new Signature<IDimensionQualification>(
					    des.getDimToASByDimAndHierName(down.getOnDimension().getIdentifyingName(),
						    down.getOnHierarchy().getIdentifyingName()),
					    ItemInAnalysisSituationType.GranularityLevel, VariableState.NON_VARIABLE,
					    "", null),
				    previousRollUpLevel.getLabel());

			    des.getVariables().get(indexOfVarToRemoveFromDes)
				    .assignFromSourceVar(granLevelToAssignToDes);
			    des.getInitialVariables().put(initialDesGranularityLevelVariable,
				    des.getVariables().get(indexOfVarToRemoveFromDes));
			    des.getVariables().remove(indexOfVarToRemoveFromDes);

			}
		    }
		}
	    }
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    logger.error("Error performing drill down operation. ", ex);
	    throw new NavigateException("Error performing drill down operation. ", ex);
	}
    }

    @Override
    public void visit(DrillDownToOperator down) throws NavigateException {

	try {
	    des.getDimToASByDimAndHierName(down.getOnDimension().getURI(), down.getOnHierarchy().getURI())
		    .setGranularity(down.getGranSpec());
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    throw new NavigateException("DrillDownToOperator", ex);
	}

    }

    // Variables are not supported yet
    @Override
    public void visit(MoveToDiceNodeOperator mvDice) throws NavigateException {
	try {
	    des.getDimToASByDimAndHierName(mvDice.getOnDimension().getURI(), mvDice.getOnHierarchy().getURI())
		    .setSingleDice(mvDice.getDiceSpecification());
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    throw new NavigateException("MoveToDiceNodeOperator", ex);
	}
    }

    @Override
    public void visit(MoveDownToDiceNodeOperator mvDice) throws NavigateException {

	/*
	 * try{ mvDice.getDiceOnLevel().setMapping(handler.
	 * getMappingQueryBetweenTwoElementsByURI(mvDice.getDiceOnLevel().getURI
	 * (), des.getFact().getURI()));
	 * des.getDimToASByDimName(mvDice.getOnDimension().getURI()).
	 * setDiceLevel(new
	 * LevelInAnalysisSituation(mvDice.getDiceOnLevel().getURI(),
	 * mvDice.getDiceOnLevel().getName(),
	 * mvDice.getDiceOnLevel().getMapping(), new
	 * DimensionToAnalysisSituationItemSignature(src.getDimToASByDimName(
	 * mvDice.getOnDimension().getURI()),
	 * ItemInAnalysisSituationType.DiceLevel, false, ""))); // the dimension
	 * to analysis situation must be filled in here
	 * mvDice.getDiceNode().getSignature().setDimToAS(des.
	 * getDimToASByDimName(mvDice.getOnDimension().getURI()));
	 * des.getDimToASByDimName(mvDice.getOnDimension().getURI()).setDiceNode
	 * (mvDice.getDiceNode()); return src; }catch(Exception ex){ throw new
	 * NavigateException(); }
	 */
    }

    @Override
    public void visit(MoveUpToDiceNodeOperator mvDice) throws NavigateException {

	try {

	    // the dimension to analysis situation must be filled in here
	    IDimensionQualification dimToASSrc = src.getDimToASByDimAndHierName(
		    mvDice.getDiceSpecification().getPosition().getSignature().getContainingObject().getD()
			    .getIdentifyingName(),
		    mvDice.getDiceSpecification().getPosition().getSignature().getContainingObject().getHierarchy()
			    .getIdentifyingName());
	    IDimensionQualification dimToASDes = des.getDimToASByDimAndHierName(
		    mvDice.getDiceSpecification().getPosition().getSignature().getContainingObject().getD()
			    .getIdentifyingName(),
		    mvDice.getDiceSpecification().getPosition().getSignature().getContainingObject().getHierarchy()
			    .getIdentifyingName());

	    LevelInAnalysisSituation lvl = mvDice.getDiceSpecification().getPosition();
	    DiceNodeInAnalysisSituation diceNode = mvDice.getDiceSpecification().getDiceNodeInAnalysisSituation();

	    IDiceSpecification spec = dimToASSrc.getDiceSpecByLevel(lvl);
	    IDiceSpecification specTarget = dimToASDes.getDiceSpecByLevel(lvl);

	    if (spec != null && specTarget != null) {
		// getting the granularity level on the required dimension on
		// the
		// source analysis situation
		DiceNodeInAnalysisSituation sourceDiceNode = dimToASSrc.getDiceSpecByLevel(lvl)
			.getDiceNodeInAnalysisSituation();

		// if the source granularity level is a variable; then no
		// modification is required in the destination
		if (!sourceDiceNode.getSignature().isVariable()) {
		    // getting the initial (unbound) value of the granularity
		    // level
		    DiceNodeInAnalysisSituation sourceInitialVarOfSourceDiceNode = sourceDiceNode.shallowCopy();

		    // the source granularity level was a variable (but removed
		    // via
		    // instantiation)
		    if (sourceInitialVarOfSourceDiceNode != null) {
			diceNode.setNodeValue(
				dataDao.getNextUpDiceValue(schema, spec.getDiceNodeInAnalysisSituation().getNodeValue(),
					spec.getPosition().getIdentifyingName()));

			DiceNodeInAnalysisSituation desDiceNode = specTarget.getDiceNodeInAnalysisSituation();

			if (desDiceNode.getSignature().isVariable()) {
			    DiceNodeInAnalysisSituation initialDesDiceVariable = desDiceNode.shallowCopy();
			    Integer indexOfVarToRemoveFromDes = des.getKeyOfVariableInVariables(desDiceNode);

			    if (indexOfVarToRemoveFromDes >= 0) {
				des.getVariables().get(indexOfVarToRemoveFromDes).assignFromSourceVar(diceNode);
				des.getInitialVariables().put(initialDesDiceVariable,
					des.getVariables().get(indexOfVarToRemoveFromDes));
				des.getVariables().remove(indexOfVarToRemoveFromDes);
			    }
			}
		    }
		}
	    }
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    logger.error("Error performing move up to dice node operation. ", ex);
	    throw new NavigateException("Error performing move up to dice node operation. ", ex);
	}
    }

    @Override
    public void visit(AddMeasureOperator addMeas) throws NavigateException {
	try {
	    MeasureToAnalysisSituaitonImpl measImp = new MeasureToAnalysisSituaitonImpl(des, addMeas.getMeasToAS());
	    des.getMeasuresToAnalysisSituation().add(measImp);
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    throw new NavigateException();
	}
    }

    @Override
    public void visit(AddDimensionSelectionOperator modifySliceCondition) throws NavigateException {
	try {
	    des.getDimToASByDimAndHierName(modifySliceCondition.getOnDimension().getURI(),
		    modifySliceCondition.getOnHierarchy().getURI()).getSliceSet()
		    .addCondition(modifySliceCondition.getCondition());
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    throw new NavigateException();
	}
    }

    @Override
    public void visit(MoveToNextDiceNodeOperator nextDiceSpec) throws NavigateException {
	try {

	    // the dimension to analysis situation must be filled in here

	    // getting the granularity level on the required dimension on the
	    // source analysis situation
	    IDimensionQualification dimToASSrc = src.getDimToASByDimAndHierName(
		    nextDiceSpec.getOnDimension().getIdentifyingName(),
		    nextDiceSpec.getOnHierarchy().getIdentifyingName());

	    IDimensionQualification dimToASDes = des.getDimToASByDimAndHierName(
		    nextDiceSpec.getOnDimension().getIdentifyingName(),
		    nextDiceSpec.getOnHierarchy().getIdentifyingName());

	    LevelInAnalysisSituation lvl = dimToASDes.getDices().get(0).getPosition();

	    DiceNodeInAnalysisSituation diceNode = dimToASDes.getDices().get(0).getDiceNodeInAnalysisSituation();

	    DiceNodeInAnalysisSituation sourceDiceNode = dimToASSrc.getDiceSpecByLevel(lvl)
		    .getDiceNodeInAnalysisSituation();

	    // if the source granularity level is a variable; then no
	    // modification is required in the destination
	    if (!sourceDiceNode.getSignature().isVariable()) {

		// getting the initial (unbound) value of the granularity level
		DiceNodeInAnalysisSituation sourceInitialVarOfSourceDiceNode = sourceDiceNode.shallowCopy();

		// the source granularity level was a variable (but removed via
		// instantiation)
		if (sourceInitialVarOfSourceDiceNode != null) {
		    diceNode.setNodeValue(dataDao.getNextDiceValue(schema,
			    dimToASSrc.getDiceSpecByLevel(lvl).getDiceNodeInAnalysisSituation().getNodeValue(),
			    dimToASSrc.getDiceSpecByLevel(lvl).getPosition().getIdentifyingName()));

		    DiceNodeInAnalysisSituation desDiceNode = dimToASDes.getDiceSpecByLevel(lvl)
			    .getDiceNodeInAnalysisSituation();

		    if (desDiceNode.getSignature().isVariable()) {
			DiceNodeInAnalysisSituation initialDesDiceVariable = desDiceNode.shallowCopy();
			Integer indexOfVarToRemoveFromDes = des.getKeyOfVariableInVariables(desDiceNode);

			if (indexOfVarToRemoveFromDes >= 0) {
			    des.getVariables().get(indexOfVarToRemoveFromDes).assignFromSourceVar(diceNode);
			    des.getInitialVariables().put(initialDesDiceVariable,
				    des.getVariables().get(indexOfVarToRemoveFromDes));
			    des.getVariables().remove(indexOfVarToRemoveFromDes);
			}
		    }
		}
	    }
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    logger.error("Error performing move to next dice node operation. ", ex);
	    throw new NavigateException("Error performing move to next dice node operation. ", ex);
	}
    }

    @Override
    public void visit(MoveToPreviousDiceNodeOperator prevDice) throws NavigateException {

	try {
	    // the dimension to analysis situation must be filled in here

	    // getting the granularity level on the required dimension on the
	    // source analysis situation
	    IDimensionQualification dimToASSrc = src.getDimToASByDimAndHierName(
		    prevDice.getOnDimension().getIdentifyingName(), prevDice.getOnHierarchy().getIdentifyingName());

	    IDimensionQualification dimToASDes = des.getDimToASByDimAndHierName(
		    prevDice.getOnDimension().getIdentifyingName(), prevDice.getOnHierarchy().getIdentifyingName());

	    LevelInAnalysisSituation lvl = dimToASDes.getDices().get(0).getPosition();

	    DiceNodeInAnalysisSituation diceNode = dimToASDes.getDices().get(0).getDiceNodeInAnalysisSituation();

	    DiceNodeInAnalysisSituation sourceDiceNode = dimToASSrc.getDiceSpecByLevel(lvl)
		    .getDiceNodeInAnalysisSituation();

	    // if the source granularity level is a variable; then no
	    // modification is required in the destination
	    if (!sourceDiceNode.getSignature().isVariable()) {

		// getting the initial (unbound) value of the granularity level
		DiceNodeInAnalysisSituation sourceInitialVarOfSourceDiceNode = sourceDiceNode.shallowCopy();

		// the source granularity level was a variable (but removed via
		// instantiation)
		if (sourceInitialVarOfSourceDiceNode != null) {
		    diceNode.setNodeValue(dataDao.getPreviousDiceValue(schema,
			    dimToASSrc.getDiceSpecByLevel(lvl).getDiceNodeInAnalysisSituation().getNodeValue(),
			    dimToASSrc.getDiceSpecByLevel(lvl).getPosition().getIdentifyingName()));

		    DiceNodeInAnalysisSituation desDiceNode = dimToASDes.getDiceSpecByLevel(lvl)
			    .getDiceNodeInAnalysisSituation();

		    if (desDiceNode.getSignature().isVariable()) {
			DiceNodeInAnalysisSituation initialDesDiceVariable = desDiceNode.shallowCopy();
			Integer indexOfVarToRemoveFromDes = des.getKeyOfVariableInVariables(desDiceNode);

			if (indexOfVarToRemoveFromDes >= 0) {
			    des.getVariables().get(indexOfVarToRemoveFromDes).assignFromSourceVar(diceNode);
			    des.getInitialVariables().put(initialDesDiceVariable,
				    des.getVariables().get(indexOfVarToRemoveFromDes));
			    des.getVariables().remove(indexOfVarToRemoveFromDes);
			}
		    }
		}
	    }
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    logger.error("Error performing move to previous dice node operation. ", ex);
	    throw new NavigateException("Error performing move to previous dice node operation. ", ex);
	}
    }

    @Override
    public void visit(AddBaseMeasureSelectionOperator addBaseMeasureSelectionOperator) throws NavigateException {
	try {
	    des.AddResultBaseFilter(addBaseMeasureSelectionOperator.getCondition());
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    throw new NavigateException("AddBaseMeasureSelectionOperator.", ex);
	}
    }

    @Override
    public void visit(AddResultSelectionOperator addResultSelectionOperator) throws NavigateException {
	try {
	    des.AddResultFilter(addResultSelectionOperator.getCondition());
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    throw new NavigateException("AddBaseMeasureSelectionOperator.", ex);
	}
    }

    @Override
    public void visit(AddDimTypedSliceConditoinOperator addDimTypedSliceConditoinOperator) {
	throw new RuntimeException("Unsupported Operation");

    }

    @Override
    public void visit(RollUpToOperator op) throws NavigateException {
	try {
	    des.getDimToASByDimAndHierName(op.getOnDimension().getURI(), op.getOnHierarchy().getURI())
		    .setGranularity(op.getGranSpec());
	    CloneUtils.createVariables(des);
	} catch (Exception ex) {
	    throw new NavigateException("AddBaseMeasureSelectionOperator.", ex);
	}
    }

    @Override
    public void visit(NullOperator nullOperator) {
	// TODO Auto-generated method stub

    }

    public AnalysisSituation getSrc() {
	return src;
    }

    public void setSrc(AnalysisSituation src) {
	this.src = src;
    }

    public AnalysisSituation getDes() {
	return des;
    }

    public void setDes(AnalysisSituation des) {
	this.des = des;
    }

    public MDSchema getSchema() {
	return schema;
    }

    public void setSchema(MDSchema schema) {
	this.schema = schema;
    }

    public IMDSchemaDAO getMdDao() {
	return mdDao;
    }

    public void setMdDao(IMDSchemaDAO mdDao) {
	this.mdDao = mdDao;
    }

    public IAnalysisGraphDAO getAgDao() {
	return agDao;
    }

    public void setAgDao(IAnalysisGraphDAO agDao) {
	this.agDao = agDao;
    }

    public IDataDAO getDataDao() {
	return dataDao;
    }

    public void setDataDao(IDataDAO dataDao) {
	this.dataDao = dataDao;
    }

    public static Logger getLogger() {
	return logger;
    }

}
