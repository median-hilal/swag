package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import swag.md_elements.MDElement;

public interface ISpecification extends Serializable {

  /**
   * 
   * Sets the set that a specification belongs to.
   *
   */
  public void setSet(ISetOfComparison set);

  /**
   * 
   * Gets the set that a specification belongs to.
   * 
   * @return the set that a specification belongs to.
   * 
   */
  public ISetOfComparison getSet();


  /**
   * 
   * If the current element is a variable, it is added to the passed analysis situation variables.
   * 
   * @param as
   * 
   */
  public void addToAnalysisSituationVariables(AnalysisSituation as);

  /**
   * Gets the MDElement on which the specification holds.
   * 
   * @return MDElement. The (sub)type of the element It depends on the concrete extending class
   */
  public MDElement getPosition();

  /**
   * Gets the MDElements on which the specification holds. In case only one MD element is there, a
   * list with one item should be returned.
   * 
   * @return MDElement. The (sub)type of the element It depends on the concrete extending class
   */
  public default List<MDElement> getListOfPositions() {
    List<MDElement> elems = new ArrayList<MDElement>();
    elems.add(getPosition());
    return elems;
  }

  /**
   * Compares the MDElement in the calling object to its counterpart in the passed object.
   * 
   * @param si
   * @return true when the the MDElement in the passed object equals the MDElement in the calling
   *         object; false otherwise.
   */
  public boolean positionAsMDElementBasedEquals(ISpecification si);


  /**
   * Shallow copies the calling object
   * 
   * @return a shallow copy of the calling object
   */
  public ISpecification shallowCopy();

  /**
   * 
   * @return true if both specification position and value are not nulls and not variables.
   * 
   */
  public boolean isDue();
  /**
   * 
   * TODO
   * 
   * Generate the SPARQL query in the context of the containing analysis situaiton.
   * 
   * @return
   * 
   *         public CustomSPARQLQuery generateQuery ();
   */
}
