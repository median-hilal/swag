package swag.sparql_builder;

import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementDataset;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitor;
// import org.apache.lucene.index.AtomicReaderContext;
// import org.apache.lucene.spatial.prefix.AbstractVisitingPrefixTreeFilter;
// import org.apache.lucene.spatial.prefix.AbstractVisitingPrefixTreeFilter.VisitorTemplate;
// import org.apache.lucene.util.Bits;



public class ElementVisitorImpl implements ElementVisitor {

  private String oldVarName;
  private String newVarName;

  public ElementVisitorImpl(String oldVarName, String newVarName) {
    super();
    this.oldVarName = oldVarName;
    this.newVarName = newVarName;
  }

  @Override
  public void visit(ElementSubQuery arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementService arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementMinus arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementNotExists arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementExists arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementNamedGraph arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementDataset arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementGroup arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementOptional arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementUnion arg0) {
    for (Element e : arg0.getElements()) {
      e.visit(this);
    }
  }

  @Override
  public void visit(ElementData arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementBind arg0) {}

  @Override
  public void visit(ElementAssign arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementFilter arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementPathBlock arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ElementTriplesBlock arg0) {
    // TODO Auto-generated method stub

  }

}
