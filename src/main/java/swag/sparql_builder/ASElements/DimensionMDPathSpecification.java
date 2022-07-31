package swag.sparql_builder.ASElements;

import java.util.ArrayList;
import java.util.List;

import swag.md_elements.MDElement;

public class DimensionMDPathSpecification {

  List<MDPathFragment> fragments = new ArrayList<>();
  List<String> filters = new ArrayList<>();
  List<MDElement> granularities = new ArrayList<>();

  public List<MDPathFragment> getFragments() {
    return fragments;
  }

  public List<String> getFilters() {
    return filters;
  }

  public List<MDElement> getGranularities() {
    return granularities;
  }

  public DimensionMDPathSpecification(List<MDPathFragment> fragments, List<String> filters,
      List<MDElement> granularities) {
    super();
    this.fragments = fragments;
    this.filters = filters;
    this.granularities = granularities;
  }

  public void addFragment(MDPathFragment frag) {
    fragments.add(frag);
  }

  public void addFilter(String fil) {
    filters.add(fil);
  }

  public void addGranularity(MDElement gran) {
    granularities.add(gran);
  }

}
