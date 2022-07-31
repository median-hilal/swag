package swag.data_handler;

import java.util.Comparator;

import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;;

public class DimToASComparator implements Comparator<IDimensionQualification> {

  @Override
  public int compare(IDimensionQualification o1, IDimensionQualification o2) {
    if (o1.getD().getName().compareToIgnoreCase(o2.getD().getName()) == 0) {
      return o1.getHierarchy().getName().compareToIgnoreCase(o2.getHierarchy().getName());
    } else {
      return o1.getD().getName().compareToIgnoreCase(o2.getD().getName());
    }
  }
}
