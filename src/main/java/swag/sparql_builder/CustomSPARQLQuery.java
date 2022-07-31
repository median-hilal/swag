package swag.sparql_builder;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.log4j.Logger;



public class CustomSPARQLQuery implements Serializable {

  private static final Logger logger = Logger.getLogger(CustomSPARQLQuery.class);
  /**
   * 
   */
  private static final long serialVersionUID = -5576863089470506910L;
  private Query sprqlQuery;

  /**
   * 
   * Gets the first projection variable of the query, null if the variable does not exist.
   * 
   * @return the first projection variable or null if this variable does not exist.
   * 
   */
  public Var getUnaryProjectionVar() {

    Optional<Var> v = Optional.ofNullable(this.getSparqlQuery()).map(Query::getProjectVars)
        .filter(x -> x.size() > 0).map(x -> x.get(0));
    return v.orElse(null);
  }

  /**
   * 
   * Gets the first projection variable of the query, null if the variable does not exist.
   * 
   * @return the first projection variable or null if this variable does not exist.
   * 
   */
  public Var getProjectionVarFrom() {

    return getUnaryProjectionVar();
  }

  /**
   * 
   * Gets the second projection variable of the query, null if the variable does not exist.
   * 
   * @return the second projection variable or null if this variable does not exist.
   * 
   */
  public Var getProjectionVarTo() {

    Optional<Var> v =
        Optional.ofNullable(this.getSparqlQuery()).map(Query::getProjectVars).map(x -> x.get(1));
    return v.orElse(null);
  }

  /**
   * 
   * Gets the name of first projection variable of the query, empty String if the variable does not
   * exist.
   * 
   * @return the name of the first projection variable or an empty String if this variable does not
   *         exist.
   * 
   */
  public String getNameOfUnaryProjectionVar() {

    return Optional.ofNullable(this.getUnaryProjectionVar().getName()).orElse("");

  }

  public Query getSprqlQuery() {
    return sprqlQuery;
  }

  public void setSprqlQuery(Query sprqlQuery) {
    this.sprqlQuery = sprqlQuery;
  }

  @Override
  public String toString() {
    return this.getSparqlQuery().toString();
  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {

    try {
      out.writeUTF(this.getSparqlQuery().toString());
    } catch (Exception ex) {
      out.writeUTF("");
      // ex.printStackTrace();
    }

  }

  public boolean hasProject() {
    return this.getSparqlQuery().getProjectVars().size() > 0;
  }


  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    try {
      this.setSparqlQuery(QueryFactory.create(in.readUTF()));
    } catch (Exception ex) {
      this.setSparqlQuery(null);
      // ex.printStackTrace();
    }
  }

  private void readObjectNoData() throws ObjectStreamException {
    throw new InvalidObjectException("Stream data required");
  }

  public Query getSparqlQuery() {
    return sprqlQuery;
  }

  public void setSparqlQuery(Query query) {
    this.sprqlQuery = query;
  }

  /**
   * 
   * Deep clone constructor
   * 
   * @param q the CustomSPARQLQuery to deeply copy
   * 
   * @return A deeply copied CustomSPARQLQuery
   * 
   */
  public CustomSPARQLQuery(CustomSPARQLQuery q) {
    setSparqlQuery(q.getSparqlQuery().cloneQuery());
  }

  public static CustomSPARQLQuery createCustomSPARQLQueryWithEmptyGroup() {
    CustomSPARQLQuery query = new CustomSPARQLQuery();

    ElementGroup group = new ElementGroup();
    query.getSparqlQuery().setQueryPattern(group);

    return query;
  }

  public void makeEmptyGroupIfPatternIsNull() {
    if (this.sprqlQuery.getQueryPattern() == null) {
      makeEmptyGroupForQuery();
    }
  }

  public void makeEmptyGroupForQuery() {
    ElementGroup group = new ElementGroup();
    this.getSparqlQuery().setQueryPattern(group);
  }

  public CustomSPARQLQuery() {
    this.sprqlQuery = new Query();
    this.sprqlQuery.setQuerySelectType();
  }

  public CustomSPARQLQuery(Query query) {
    this.sprqlQuery = query;
    this.sprqlQuery.setQuerySelectType();
  }

  public CustomSPARQLQuery(String query) {
    this.sprqlQuery = QueryFactory.create(query);
    this.sprqlQuery.setQuerySelectType();
  }

  @Override
  public boolean equals(Object rq) {
    if (rq instanceof CustomSPARQLQuery) {
      CustomSPARQLQuery rq1 = (CustomSPARQLQuery) rq;
      if (this.sprqlQuery.equals(rq1.getSparqlQuery()))
        return true;
    }
    return false;
  }

  public boolean hasHeadVars() {
    return this.getSparqlQuery().getProjectVars().size() > 0;
  }

  /**
   * Compares the query at hand with the nullQuery which is a query created via the default
   * constructor
   * 
   * @return true: equal; false: not equal
   */
  public boolean isEmptyQuery() {
    CustomSPARQLQuery nullQuery = new CustomSPARQLQuery(new Query());
    if (this.equals(nullQuery))
      return true;
    else
      return false;
  }

  /**
   * 
   * Returns a NEW query resulting from joining both the headers and the patterns of the current
   * query with the passed query. Note that elements of the current and the passed queries are
   * joined in the new query by reference. Safe to nulls.
   * 
   * @param q the query to join with the query at hand
   * 
   * @return a NEW query of the join result
   * 
   */
  public CustomSPARQLQuery joinWith(CustomSPARQLQuery q) {
    CustomSPARQLQuery q1 = new CustomSPARQLQuery();
    if (q == null) {

      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(this.getSparqlQuery().getProject());
      q1.sprqlQuery.setQueryPattern(this.sprqlQuery.getQueryPattern());
    } else {

      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(
          joinQueryHeaders(q.getSparqlQuery().getProject(), this.getSparqlQuery().getProject()));
      q1.sprqlQuery.setQueryPattern(
          joinQueryPatterns(q.sprqlQuery.getQueryPattern(), this.sprqlQuery.getQueryPattern()));
    }
    return q1;
  }

  /**
   * 
   * Returns a NEW query resulting from joining both headers and patterns the current query with the
   * passed one. The group pattern of the current query hosts the the pattern of the passed query.
   * If the group pattern of the current query is null, an empty one is created instead. Note that
   * elements of the current and the passed queries are joined in the new query by reference. Safe
   * to nulls.
   * 
   * @param q the query to join with the query at hand
   * 
   * @return a NEW query of the join result
   * 
   */
  public CustomSPARQLQuery joinWithFullyAsGroups(CustomSPARQLQuery q) {
    CustomSPARQLQuery q1 = new CustomSPARQLQuery();
    if (q == null) {

      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(this.getSparqlQuery().getProject());
      q1.sprqlQuery.setQueryPattern(this.sprqlQuery.getQueryPattern());
    } else {

      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(
          joinQueryHeaders(q.getSparqlQuery().getProject(), this.getSparqlQuery().getProject()));

      makeEmptyGroupIfPatternIsNull();
      q1.sprqlQuery.setQueryPattern(this.sprqlQuery.getQueryPattern());
      q1.sprqlQuery.setQueryPattern(addPatternToQueryGroupOrSubQuery(
          q1.sprqlQuery.getQueryPattern(), q.sprqlQuery.getQueryPattern()));
    }
    return q1;
  }

  /**
   * 
   * Modifies the calling (this) query. The modified query is generated by from joining the current
   * query with the pattern of the passed one. Headers are not joined. The group pattern of the
   * current query hosts the the pattern of the passed query. If the group pattern of the current
   * query is null, an empty one is created instead. Note that elements of the current and the
   * passed queries are joined in the new query by reference. Safe to nulls.
   * 
   * @param q the query to join with the query at hand
   * 
   */
  public boolean joinWithOnlyPatternsAsGroupsByReference(CustomSPARQLQuery q) {

    if (q == null) {
      return false;
    } else {
      makeEmptyGroupIfPatternIsNull();
      this.sprqlQuery.setQueryPattern(addPatternToQueryGroupOrSubQuery(
          this.sprqlQuery.getQueryPattern(), q.sprqlQuery.getQueryPattern()));

      return true;
    }
  }

  /**
   * 
   * Returns a NEW query resulting from joining the current query with the pattern of the passed one
   * and the grouping by variables from both queries. Headers are not joined. The group pattern of
   * the current query hosts the the pattern of the passed query. If the group pattern of the
   * current query is null, an empty one is created instead. Note that elements of the current and
   * the passed queries are joined in the new query by reference. Safe to nulls.
   * 
   * @param q the query to join with the query at hand
   * 
   * @return a NEW query of the join result
   * 
   */
  public CustomSPARQLQuery joinWithOnlyPatternsAndGroupByAsGroups(CustomSPARQLQuery q) {
    CustomSPARQLQuery q1 = new CustomSPARQLQuery();
    if (q == null) {

      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(this.getSparqlQuery().getProject());
      q1.sprqlQuery.setQueryPattern(this.sprqlQuery.getQueryPattern());

    } else {
      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(this.getSparqlQuery().getProject());

      makeEmptyGroupIfPatternIsNull();
      q1.getSparqlQuery().setQueryPattern(this.sprqlQuery.getQueryPattern());
      q1.sprqlQuery.setQueryPattern(addPatternToQueryGroupOrSubQuery(
          q1.sprqlQuery.getQueryPattern(), q.sprqlQuery.getQueryPattern()));

      /* Add grouping by variables */
      if (!q.getSparqlQuery().getGroupBy().isEmpty()) {
        q1.getSparqlQuery().getGroupBy().addAll(q.getSparqlQuery().getGroupBy());
      }
      if (!this.getSparqlQuery().getGroupBy().isEmpty()) {
        q1.getSparqlQuery().getGroupBy().addAll(this.getSparqlQuery().getGroupBy());
      }
    }
    return q1;
  }


  /**
   * 
   * Returns a NEW query resulting from joining the current query with the pattern of the passed
   * one. Headers are not joined. The group pattern of the current query hosts the the pattern of
   * the passed query. If the group pattern of the current query is null, an empty one is created
   * instead. Note that elements of the current and the passed queries are joined in the new query
   * by reference. Safe to nulls.
   * 
   * @param q the query to join with the query at hand
   * 
   * @return a NEW query of the join result
   * 
   */
  public CustomSPARQLQuery joinWithOnlyPatternsAsGroups(CustomSPARQLQuery q) {
    CustomSPARQLQuery q1 = new CustomSPARQLQuery();
    if (q == null) {

      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(this.getSparqlQuery().getProject());
      q1.sprqlQuery.setQueryPattern(this.sprqlQuery.getQueryPattern());

    } else {
      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(this.getSparqlQuery().getProject());

      makeEmptyGroupIfPatternIsNull();
      q1.getSparqlQuery().setQueryPattern(this.sprqlQuery.getQueryPattern());
      q1.sprqlQuery.setQueryPattern(addPatternToQueryGroupOrSubQuery(
          q1.sprqlQuery.getQueryPattern(), q.sprqlQuery.getQueryPattern()));
    }
    return q1;
  }

  /**
   * 
   * Returns a NEW query resulting from joining the current query with the pattern of the passed
   * one. Headers are not joined. The group pattern of the current query hosts the the pattern of
   * the passed query. If the group pattern of the current query is null, an empty one is created
   * instead. Note that elements of the current and the passed queries are joined in the new query
   * by reference. Safe to nulls.
   * 
   * @param q the query to join with the query at hand
   * 
   * @return a NEW query of the join result
   * 
   */
  public CustomSPARQLQuery joinWithOnlyPatterns(CustomSPARQLQuery q) {
    CustomSPARQLQuery q1 = new CustomSPARQLQuery();
    if (q == null) {

      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(this.getSparqlQuery().getProject());
      q1.sprqlQuery.setQueryPattern(this.sprqlQuery.getQueryPattern());

    } else {
      q1.sprqlQuery.setQuerySelectType();
      q1.setQueryHeader(this.getSparqlQuery().getProject());

      q1.sprqlQuery.setQueryPattern(
          joinQueryPatterns(q.sprqlQuery.getQueryPattern(), this.sprqlQuery.getQueryPattern()));
    }
    return q1;
  }

  /**
   * 
   * Alters the current query by adding the passed subquery to it.
   * 
   * @param q the subquery to add
   * 
   * @return The altered current query resulting resulting from adding the passed subquery to it
   * 
   */
  public void addSubQuery(CustomSPARQLQuery q) {
    this.sprqlQuery.setQueryPattern(
        joinQueryPatterns(this.sprqlQuery.getQueryPattern(), new ElementSubQuery(q.sprqlQuery)));
    // System.out.println("join query: " + this.sprqlQuery.toString());
  }


  /**
   * 
   * Alters the current query by adding the passed subquery to it. If this query pattern is a group,
   * then the passed query is inserted into the group.
   * 
   * @param q the subquery to add
   * 
   * @return The altered current query resulting resulting from adding the passed subquery to it
   * 
   */
  public void insertSubQuery(CustomSPARQLQuery q) {
    this.sprqlQuery.setQueryPattern(joinQueryPatternsAsSubqueries(this.sprqlQuery.getQueryPattern(),
        new ElementSubQuery(q.sprqlQuery)));
    // System.out.println("join query: " + this.sprqlQuery.toString());
  }

  public void setQueryHeader(VarExprList qh) {
    this.sprqlQuery.getProject().clear();
    this.sprqlQuery.getProject().addAll(qh);
  }

  /**
   * 
   * Given two query headers, this function joins the two headers and puts the result in anew
   * header. The function is null safe.
   * 
   * @param qh1 first query header
   * @param qh2 second query header
   * 
   * @return a new header resulting from joining the headers.
   * 
   */
  public static VarExprList joinQueryHeaders(VarExprList qh1, VarExprList qh2) {

    VarExprList exprList = new VarExprList();
    if (qh1 != null && !qh1.isEmpty()) {
      exprList.addAll(qh1);
    }
    if (qh2 != null && !qh2.isEmpty()) {
      exprList.addAll(qh2);
    }
    return exprList;
  }


  /**
   * could be called from a FOR loop for all elements in the top query ElementGroup Recursively
   * keeps simplifying elementGroup to its elements, until it cannot be further divided, then it is
   * added to a set that is, by default, doesn't accept duplications
   * 
   * @param elmList the final set of element to be returned without duplications
   * @param elm the current element to check
   */
  protected static void removeDuplicatesFromSingleElementGroup(List<Element> elmList, Element elm) {
    ElementGroup elemGrp = new ElementGroup();

    if (elm instanceof ElementOptional) {
      ElementOptional opt = (ElementOptional) elm;
      List<Element> optionalElmList = new ArrayList<>();
      removeDuplicatesFromSingleElementGroup(optionalElmList, opt.getOptionalElement());
      ElementGroup grp = new ElementGroup();
      for (Element e : optionalElmList) {
        grp.addElement(e);
      }
      Element newOptElm = new ElementOptional(grp);
      opt = (ElementOptional) newOptElm;
      elmList.add(opt);
      return;
    }

    try {
      elemGrp = (ElementGroup) elm;
    }

    // element cannot be further broken down
    catch (Exception ex) {
      elmList.add(elm);
      return;
    }

    List<Element> elmListSub = new ArrayList<>();

    for (Element newElm : elemGrp.getElements()) {
      removeDuplicatesFromSingleElementGroup(elmList, newElm);
    }
  }

  /**
   * could be called from a FOR loop for all elements in the top query ElementGroup Recursively
   * keeps simplifying elementGroup to its elements, until it cannot be further divided, then it is
   * added to a set that is, by default, doesn't accept duplications
   * 
   * @param elmSet the final set of element to be returned without duplications
   * @param elm the current element to check
   */
  protected static void removeDuplicatesFromSingleElementGroup1(ElementGroup targetGroup,
      Element elm) {

    ElementGroup elemGrp = new ElementGroup();

    if (elm instanceof ElementGroup) {
      elemGrp = (ElementGroup) elm;
    }

    // Element elm cannot be further broken down
    else {
      if (!targetGroup.getElements().contains(elm)) {
        targetGroup.getElements().add(elm);
      }
      return;
    }

    ElementGroup newGroup = new ElementGroup();

    if (elemGrp.getElements().size() == 1 && elemGrp.getElements().get(0) instanceof ElementGroup) {
      removeDuplicatesFromSingleElementGroup1(targetGroup, elemGrp.getElements().get(0));

    } else {

      for (Element newElm : elemGrp.getElements()) {
        removeDuplicatesFromSingleElementGroup1(newGroup, newElm);
      }
      if (!newGroup.isEmpty()) {
        targetGroup.getElements().add(newGroup);
      }
    }


  }

  /**
   * Removes the duplicated triple patterns in a query
   */
  public void removeQueryPatternDuplications1() {

    if (this.getSparqlQuery().getQueryPattern() != null
        && this.getSparqlQuery().getQueryPattern() instanceof ElementGroup) {

      ElementGroup sourceGroup = (ElementGroup) this.getSparqlQuery().getQueryPattern();
      ElementGroup targetGroup = new ElementGroup();
      List<Element> elmList = sourceGroup.getElements();


      for (Element elm : elmList) {
        removeDuplicatesFromSingleElementGroup1(targetGroup, elm);
      }

      elmList.clear();
      elmList.addAll(targetGroup.getElements());
    }
  }

  /**
   * Removes the duplicated triple patterns in a query
   */
  public void removeQueryPatternDuplications() {

    ElementGroup elmGrp = (ElementGroup) this.getSparqlQuery().getQueryPattern();
    List<Element> elmList = elmGrp.getElements();
    List<Element> elmListNew = new ArrayList<>();

    for (Element elm : elmList) {
      removeDuplicatesFromSingleElementGroup(elmListNew, elm);
    }
    elmList.clear();
    elmList.addAll(elmListNew);

    // Moving subqueries and binds to the end
    List<Element> tempSubQueryElemList = new ArrayList<Element>();
    List<Element> tempBindElemList = new ArrayList<Element>();


    for (Element elm : elmList) {
      if (elm instanceof ElementSubQuery) {
        tempSubQueryElemList.add(elm);
      } else if (elm instanceof ElementBind) {
        tempBindElemList.add(elm);
      }
      // if (elm.toString().contains("SELECT"))
    }

    for (Element elm : tempSubQueryElemList) {
      elmList.remove(elm);
    }

    for (Element elm : tempBindElemList) {
      elmList.remove(elm);
    }

    elmList.addAll(elmList.size(), tempSubQueryElemList);
    elmList.addAll(elmList.size(), tempBindElemList);

    // Removing duplications but keeping elements order
    List<Element> elmListAsSet = new ArrayList<>();
    elmList.stream().filter(e -> !elmListAsSet.contains(e)).map(e -> elmListAsSet.add(e));

    for (Element e : elmList) {
      if (!elmListAsSet.contains(e)) {
        elmListAsSet.add(e);
      }
    }

    elmList.clear();
    elmList.addAll(elmListAsSet);
  }

  /**
   * 
   * Generates a new query {@code ElementGroup} (the new query pattern) that is the result of
   * joining the two passed.
   * 
   * @param e1 the first element to join (the first query pattern)
   * @param e2 the second element to join (the second query pattern)
   * 
   * @return an Element combining the two parameters of the function; null if not applicable
   * 
   */
  public static Element joinQueryPatterns(Element e1, Element e2) {

    if (e1 == null && e2 == null)
      return null;
    if (e1 == null && e2 != null)
      return e2;
    if (e1 != null && e2 == null)
      return e1;
    if (e2 instanceof Element && e1 instanceof Element) {
      ElementGroup block2 = new ElementGroup();
      block2.addElement(e1);
      block2.addElement(e2);
      return block2;
    }
    return null;
  }

  /**
   * 
   * Generates a new query {@code ElementGroup} (the new query pattern) that is the result of
   * joining the two passed. If the first passed element is a subquery, then the second passed item
   * is inserted into the group.
   * 
   * @param e1 the first element to join (the first query pattern)
   * @param e2 the second element to join (the second query pattern)
   * 
   * @return an Element combining the two parameters of the function; null if not applicable
   * 
   */
  public static Element joinQueryPatternsAsSubqueries(Element e1, Element e2) {

    if (e1 == null && e2 == null)
      return null;
    if (e1 == null && e2 != null)
      return e2;
    if (e1 != null && e2 == null)
      return e1;
    if (e2 instanceof Element && e1 instanceof Element) {
      if (e1 instanceof ElementGroup) {
        ElementGroup block2 = (ElementGroup) e1;
        block2.addElement(e2);
        return block2;
      } else {
        ElementGroup block2 = new ElementGroup();
        block2.addElement(e1);
        block2.addElement(e2);
        return block2;
      }
    }
    return null;
  }


  /**
   * 
   * Adds a query pattern {@code e2} to an ElementGroup {@code group} which is supposed to represent
   * a the main group of a query patter, to which a new pattern will be added.
   * 
   * @param group the group to add a query pattern to it
   * @param e2 the element to join with the group contents
   * 
   * @return true if success; false otherwise
   * 
   */
  public static boolean addPatternToQueryGroup(Element group, Element e2) {

    if (group == null && !(group instanceof ElementGroup)) {
      return false;
    }

    if (group == null && e2 == null) {
      return false;
    }

    ElementGroup castedGroup = (ElementGroup) group;

    if (e2 != null) {
      castedGroup.addElement(e2);
    }

    return true;

  }

  /**
   * 
   * Adds a query pattern {@code e2} to an ElementGroup {@code group} which is supposed to represent
   * a the main group of a query pattern, to which a new pattern will be added. This function is
   * tolerant to the cases where group is a subquery.
   * 
   * @param group the group to add a query pattern to it
   * @param e2 the element to join with the group contents
   * 
   * @return the newly created ElementGroup when success, an empty ElementGroup otherwise.
   * 
   */
  public static ElementGroup addPatternToQueryGroupOrSubQuery(Element group, Element e2) {

    ElementGroup castedGroup = new ElementGroup();
    if (group == null && e2 == null) {
      return castedGroup;
    }
    if (group instanceof ElementSubQuery) {
      castedGroup = new ElementGroup();
      castedGroup.addElement(group);
    } else {
      if (group instanceof ElementGroup) {
        castedGroup = (ElementGroup) group;
      } else {
        return castedGroup;
      }
    }
    if (e2 != null) {
      castedGroup.addElement(e2);
    }
    return castedGroup;
  }

  /**
   * Not needed anymore checks if the calling rooted query contains containedQuery TODO now it is a
   * stupid String comparison, should be developed
   * 
   * @param containedQuery
   * @return true if the calling rooted query contains containedQuery, false otherwise
   */
  public boolean checkRootedQueryContainment(CustomSPARQLQuery containedQuery) {
    if (this.getSparqlQuery().toString().contains(containedQuery.getSparqlQuery().toString()))
      return true;
    return false;
  }


  /**
   * Non used checks if the String inside the top outer where of the calling RootedQuery contains
   * the string of the top outer where of the checked query q
   * 
   * @param q the query to check if contained in the calling query
   * @return true if contained, false otherwise
   */
  private boolean checkQueryBodyContinament(CustomSPARQLQuery q) {
    if (SPARQLUtilities.removeSelectFromQueryString(this.getSparqlQuery().toString())
        .contains(SPARQLUtilities.removeSelectFromQueryString(q.getSparqlQuery().toString())))
      return true;
    return false;
  }
}
