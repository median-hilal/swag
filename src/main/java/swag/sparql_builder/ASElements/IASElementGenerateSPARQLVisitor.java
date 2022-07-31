package swag.sparql_builder.ASElements;

import java.util.List;

import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerivedInAS;
import swag.web.IVariableVisitor;

public interface IASElementGenerateSPARQLVisitor extends IVariableVisitor {

  public List<String> getReturn();

  public void visit(IGranularitySpecification gsi);

  public void visit(ISliceSinglePosition ssi) throws Exception;

  public void visit(IDiceSpecification dsi);

  public void visit(MeasureDerivedInAS m) throws Exception;

  public void visit(MeasureAggregatedInAS m);

  public void visit(MeasureDerived m) throws Exception;

  public void visit(MeasureAggregated m) throws Exception;


}
