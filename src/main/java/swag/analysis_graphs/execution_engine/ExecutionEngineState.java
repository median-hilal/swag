package swag.analysis_graphs.execution_engine;

public enum ExecutionEngineState {

  START(0), INITIATED(1), MD_SUPPLIED(2), SPARQL_ENDPOINT_SUPPLIED(3), ANALYSIS_GRAPH_SUPPLIED(4);

  private int order;

  ExecutionEngineState(int order) {
    this.order = order;
  }

  public int getOrder() {
    return order;
  }

}
