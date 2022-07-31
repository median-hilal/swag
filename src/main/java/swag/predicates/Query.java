package swag.predicates;

public class Query {

  private QueryType queryType;
  private String query;

  public QueryType getQueryType() {
    return queryType;
  }

  public void setQueryType(QueryType queryType) {
    this.queryType = queryType;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public Query(String query) {
    super();
    this.query = query;
  }

  public Query(QueryType queryType, String query) {
    super();
    this.queryType = queryType;
    this.query = query;
  }
}
