package swag.md_elements;

import java.io.Serializable;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import com.google.common.base.Preconditions;

import swag.sparql_builder.CustomSPARQLQuery;

public class Mapping implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -5771539849523419978L;
  CustomSPARQLQuery query;

  public CustomSPARQLQuery getQuery() {
    return query;
  }

  public void setQuery(CustomSPARQLQuery query) {
    this.query = Preconditions.checkNotNull(query);
  }

  /**
   * 
   * Creates a mapping object with an empty query.
   * 
   */
  public Mapping() {
    this(QueryFactory.create());
  }

  /**
   * 
   * Creates a mapping object with the passed {@code CustomSPARQLQuery} query. Fails with exception
   * if the passed query is not valid.
   * 
   * @param query the mapping query
   */
  public Mapping(CustomSPARQLQuery query) {
    super();
    setQuery(query);
  }

  /**
   * 
   * Creates a mapping object after creating a {@code CustomSPARQLQuery} instance over the passed
   * {@code Query} query. Fails with exception if the passed query is not valid.
   * 
   * @param query
   */
  public Mapping(Query query) {
    this(new CustomSPARQLQuery(query));
  }

  /**
   * Clone constructor
   * 
   * @param m
   */
  public Mapping(Mapping m) {
    setQuery(new CustomSPARQLQuery(m.getQuery()));
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Mapping) {
      Mapping m = (Mapping) o;
      if (this.getQuery().equals(m.getQuery()))
        return true;
    }
    return false;
  }

}
