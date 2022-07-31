package swag.analysis_graphs.execution_engine;

import java.util.List;
import org.apache.log4j.Logger;

import swag.analysis_graphs.dao.IAnalysisGraphDAO;
import swag.analysis_graphs.dao.IDataDAO;
import swag.analysis_graphs.dao.IMDSchemaDAO;
import swag.analysis_graphs.execution_engine.analysis_situations.Variable;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.analysis_graphs.execution_engine.navigations.NavigationVisitorDynamic;
import swag.analysis_graphs.execution_engine.operators.Operation;
import swag.md_elements.MDSchema;

/**
 * 
 * Ignores source variables if they are nulls. Meaning that the nulls should not be forwarded to the
 * target analysis situation.
 * 
 * @author swag
 *
 */
public class IgnoreEmptyVariablesStrategy implements NavigationStrategy {

  private static final Logger logger = Logger.getLogger(IgnoreEmptyVariablesStrategy.class);

  @Override
  public void doNavigate(NavigationStep nv, MDSchema schema, IMDSchemaDAO mdDao,
      IAnalysisGraphDAO agDao, IDataDAO dataDao) {


    logger.info("Navigating from " + nv.getSource().getName() + " to " + nv.getTarget().getName());

    NavigationVisitorDynamic mv =
        new NavigationVisitorDynamic(nv.getSource(), nv.getTarget(), schema, mdDao, agDao, dataDao);
    // getting all the bound variables in the source analysis situation
    List<Variable> sourceInitialVariablesThatAreBound =
        nv.getSource().getInitialVariablesThatAreBound();
    // getting destination analysis situation initial variables
    List<Variable> destinationInitialVariables = nv.getTarget().getInitialVariablesAllAsList();

    // checking which bound variables exist in the destination analysis situation
    for (Variable v : sourceInitialVariablesThatAreBound) {

      // the destination matching initial variable that corresponds to the current source bound
      // variable
      if (destinationInitialVariables.contains(v)) {
        Variable varInDes = destinationInitialVariables.get(destinationInitialVariables.indexOf(v));

        // destination matching variable is not null
        if (varInDes != null) {
          // copying the matching initial variable
          Variable varInDesCopy = varInDes.shallowCopy();
          // modifying the value of the destination matching variable
          varInDes = nv.getSource().getBoundVarOfInitialVar(v);
          // removing the destination matching variable from variables
          Integer indexOfVarToRemoveFromDes =
              nv.getTarget().getKeyOfVariableInVariablesByPositionalCompare(varInDesCopy);

          if (indexOfVarToRemoveFromDes >= 0) {
            nv.getTarget().getVariables().get(indexOfVarToRemoveFromDes)
                .assignFromSourceVar(varInDes);
            // adding the new bound value of the destination matching variable to the
            // initialVariables list
            nv.getTarget().getInitialVariables().put(varInDesCopy,
                nv.getTarget().getVariables().get(indexOfVarToRemoveFromDes));
            // nv.getTarget().getVariables().remove(indexOfVarToRemoveFromDes);
          }
        }
      }
    }

    for (Operation op : nv.getOperators()) {
      try {
        logger.info(
            "Performing navigation operation " + op.getName() + " of type " + op.getOperatorName());
        op.accept(mv);
      } catch (Exception e) {
        System.err.println("Navigation exception..." + e.getMessage());
        e.printStackTrace();
      }
    }

  }

}
