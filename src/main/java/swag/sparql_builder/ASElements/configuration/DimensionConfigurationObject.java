package swag.sparql_builder.ASElements.configuration;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import swag.analysis_graphs.execution_engine.analysis_situations.asUtilities;
import swag.md_elements.Dimension;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.md_elements.Measure;
import swag.md_elements.QB4OHierarchy;

/**
 * 
 * Specifies a single dimension configuration
 * 
 * @author swag
 *
 */
public class DimensionConfigurationObject implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3192036184485381621L;

  private Measure msr;
  private MDElement elm;

  private DimensionNonStrictConfigurationType nonStrictConf =
      DimensionNonStrictConfigurationType.TREAT_AS_FACT;
  private DimensionIncompleteConfigurationType incompleteConf =
      DimensionIncompleteConfigurationType.DISCARD_FACT;

  public static boolean isAnyNonStrictSplit(List<DimensionConfigurationObject> confs) {
    return confs.stream().anyMatch(conf -> conf.isNonStrictSplit());
  }

  public boolean isNonStrictSplit() {
    return this.getNonStrictConf().equals(DimensionNonStrictConfigurationType.SPLIT_EQULALY);
  }

  public static boolean isIncompleteOther(MDSchema schema, List<DimensionConfigurationObject> confs,
      MDElement otherMDElement) {
    DimensionConfigurationObject other = new DimensionConfigurationObject(otherMDElement);
    return getMatchingIncompleteConf(schema, confs, other)
        .equals(DimensionIncompleteConfigurationType.OTHER);
  }

  public static boolean isIncompleteOther(MDSchema schema, List<DimensionConfigurationObject> confs,
      DimensionConfigurationObject other) {
    return getMatchingIncompleteConf(schema, confs, other)
        .equals(DimensionIncompleteConfigurationType.OTHER);
  }

  public static boolean isIncompleteNone(MDSchema schema, List<DimensionConfigurationObject> confs,
      MDElement otherMDElement) {
    DimensionConfigurationObject other = new DimensionConfigurationObject(otherMDElement);
    return getMatchingIncompleteConf(schema, confs, other)
        .equals(DimensionIncompleteConfigurationType.DISCARD_FACT);
  }

  public static boolean isIncompleteNone(MDSchema schema, List<DimensionConfigurationObject> confs,
      DimensionConfigurationObject other) {
    return getMatchingIncompleteConf(schema, confs, other)
        .equals(DimensionIncompleteConfigurationType.DISCARD_FACT);
  }

  public static boolean isIncompleteSubElm(MDSchema schema,
      List<DimensionConfigurationObject> confs, MDElement otherMDElement) {
    DimensionConfigurationObject other = new DimensionConfigurationObject(otherMDElement);
    return getMatchingIncompleteConf(schema, confs, other)
        .equals(DimensionIncompleteConfigurationType.SUBELEMENT);
  }

  public static boolean isIncompleteSubElm(MDSchema schema,
      List<DimensionConfigurationObject> confs, DimensionConfigurationObject other) {
    return getMatchingIncompleteConf(schema, confs, other)
        .equals(DimensionIncompleteConfigurationType.SUBELEMENT);
  }

  public static boolean isNonStrictSplit(MDSchema schema, List<DimensionConfigurationObject> confs,
      MDElement otherMDElement) {
    DimensionConfigurationObject other = new DimensionConfigurationObject(otherMDElement);
    return getMatchingNonStrictConf(schema, confs, other)
        .equals(DimensionNonStrictConfigurationType.SPLIT_EQULALY);
  }

  public static boolean isNonStrictSplit(MDSchema schema, List<DimensionConfigurationObject> confs,
      DimensionConfigurationObject other) {
    return getMatchingNonStrictConf(schema, confs, other)
        .equals(DimensionNonStrictConfigurationType.SPLIT_EQULALY);
  }

  public static boolean isNonStrictNone(MDSchema schema, List<DimensionConfigurationObject> confs,
      MDElement otherMDElement) {
    DimensionConfigurationObject other = new DimensionConfigurationObject(otherMDElement);
    return getMatchingNonStrictConf(schema, confs, other)
        .equals(DimensionNonStrictConfigurationType.TREAT_AS_FACT);
  }

  public static boolean isNonStrictNone(MDSchema schema, List<DimensionConfigurationObject> confs,
      DimensionConfigurationObject other) {
    return getMatchingNonStrictConf(schema, confs, other)
        .equals(DimensionNonStrictConfigurationType.TREAT_AS_FACT);
  }

  public static DimensionIncompleteConfigurationType getMatchingIncompleteConf(MDSchema schema,
      List<DimensionConfigurationObject> confs, DimensionConfigurationObject other) {

    return Optional.ofNullable(confs).orElseGet(Collections::emptyList).stream()
        .filter(conf -> conf.getIncompleteConf(schema, other) != null).findAny()
        .map(DimensionConfigurationObject::getIncompleteConf)
        .orElse(DimensionIncompleteConfigurationType.DISCARD_FACT);

  }

  public static DimensionNonStrictConfigurationType getMatchingNonStrictConf(MDSchema schema,
      List<DimensionConfigurationObject> confs, DimensionConfigurationObject other) {

    return Optional.ofNullable(confs).orElseGet(Collections::emptyList).stream()
        .filter(conf -> conf.getNonStrictConf(schema, other) != null).findAny()
        .map(DimensionConfigurationObject::getNonStrictConf)
        .orElse(DimensionNonStrictConfigurationType.TREAT_AS_FACT);

  }

  public DimensionIncompleteConfigurationType getIncompleteConf(MDSchema schema,
      DimensionConfigurationObject other) {

    Measure otherMsr = other.getMsr();

    if (other.getElm() instanceof Level) {

      Level otherLevel = (Level) other.getElm();

      if (this.getElm() instanceof Level) {
        Level thisLevel = (Level) this.getElm();
        if (thisLevel.equals(otherLevel) && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getIncompleteConf();
        }
      }

      if (this.getElm() instanceof QB4OHierarchy) {
        QB4OHierarchy thisHier = (QB4OHierarchy) this.getElm();
        if (schema.getHierarchyOfLevel(otherLevel.getIdentifyingName()).equals(thisHier)
            && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getIncompleteConf();
        }
      }

      if (this.getElm() instanceof Dimension) {
        Dimension thisDim = (Dimension) this.getElm();
        if (schema.getDimensionOfLevel(otherLevel.getIdentifyingName()).equals(thisDim)
            && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getIncompleteConf();
        }
      }

    }

    if (other.getElm() instanceof QB4OHierarchy) {

      QB4OHierarchy otherHier = (QB4OHierarchy) other.getElm();

      if (this.getElm() instanceof QB4OHierarchy) {
        QB4OHierarchy thisHier = (QB4OHierarchy) this.getElm();
        if (otherHier.equals(thisHier) && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getIncompleteConf();
        }
      }

      if (this.getElm() instanceof Dimension) {
        Dimension thisDim = (Dimension) this.getElm();
        if (thisDim.equals(
            schema.getHierarchiesOnDimension(thisDim.getIdentifyingName()).contains(otherHier))
            && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getIncompleteConf();
        }
      }

    }

    if (other.getElm() instanceof Dimension) {

      Dimension otherDim = (Dimension) other.getElm();

      if (this.getElm() instanceof Dimension) {
        Dimension thisDim = (Dimension) this.getElm();
        if (thisDim.equals(otherDim) && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getIncompleteConf();
        }
      }

    }

    return DimensionIncompleteConfigurationType.DISCARD_FACT;
  }


  public DimensionNonStrictConfigurationType getNonStrictConf(MDSchema schema,
      DimensionConfigurationObject other) {

    Measure otherMsr = other.getMsr();

    if (other.getElm() instanceof Level) {

      Level otherLevel = (Level) other.getElm();

      if (this.getElm() instanceof Level) {
        Level thisLevel = (Level) this.getElm();
        if (thisLevel.equals(otherLevel) && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getNonStrictConf();
        }
      }

      if (this.getElm() instanceof QB4OHierarchy) {
        QB4OHierarchy thisHier = (QB4OHierarchy) this.getElm();
        if (schema.getHierarchyOfLevel(otherLevel.getIdentifyingName()).equals(thisHier)
            && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getNonStrictConf();
        }
      }

      if (this.getElm() instanceof Dimension) {
        Dimension thisDim = (Dimension) this.getElm();
        if (schema.getDimensionOfLevel(otherLevel.getIdentifyingName()).equals(thisDim)
            && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getNonStrictConf();
        }
      }

    }

    if (other.getElm() instanceof QB4OHierarchy) {

      QB4OHierarchy otherHier = (QB4OHierarchy) other.getElm();

      if (this.getElm() instanceof QB4OHierarchy) {
        QB4OHierarchy thisHier = (QB4OHierarchy) this.getElm();
        if (otherHier.equals(thisHier) && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getNonStrictConf();
        }
      }

      if (this.getElm() instanceof Dimension) {
        Dimension thisDim = (Dimension) this.getElm();
        if (thisDim.equals(
            schema.getHierarchiesOnDimension(thisDim.getIdentifyingName()).contains(otherHier))
            && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getNonStrictConf();
        }
      }

    }

    if (other.getElm() instanceof Dimension) {

      Dimension otherDim = (Dimension) other.getElm();

      if (this.getElm() instanceof Dimension) {
        Dimension thisDim = (Dimension) this.getElm();
        if (thisDim.equals(otherDim) && asUtilities.equalsWithNull(this.getMsr(), otherMsr)) {
          return getNonStrictConf();
        }
      }

    }

    return DimensionNonStrictConfigurationType.TREAT_AS_FACT;
  }

  public DimensionIncompleteConfigurationType getIncompleteConf(
      DimensionConfigurationObject other) {

    if (asUtilities.equalsIfNotNull(this.getElm(), other.getElm())
        && asUtilities.equalsWithNull(this.getMsr(), other.getMsr())) {
      return getIncompleteConf();
    }
    return DimensionIncompleteConfigurationType.DISCARD_FACT;
  }

  public DimensionConfigurationObject(Measure msr, MDElement elm) {
    super();
    Objects.requireNonNull(elm);
    this.msr = msr;
    this.elm = elm;
  }

  public DimensionConfigurationObject(MDElement elm) {
    super();
    Objects.requireNonNull(elm);
    this.elm = elm;
  }

  public DimensionConfigurationObject(Measure msr, MDElement elm,
      DimensionNonStrictConfigurationType nonStrictConf,
      DimensionIncompleteConfigurationType incompleteConf) {
    super();
    Objects.requireNonNull(elm);
    this.msr = msr;
    this.elm = elm;
    this.nonStrictConf = nonStrictConf;
    this.incompleteConf = incompleteConf;
  }

  public Measure getMsr() {
    return msr;
  }

  public void setMsr(Measure msr) {
    this.msr = msr;
  }

  public DimensionNonStrictConfigurationType getNonStrictConf() {
    return nonStrictConf;
  }

  public void setNonStrictConf(DimensionNonStrictConfigurationType nonStrictConf) {
    this.nonStrictConf = nonStrictConf;
  }

  public DimensionIncompleteConfigurationType getIncompleteConf() {
    return incompleteConf;
  }

  public void setIncompleteConf(DimensionIncompleteConfigurationType incompleteConf) {
    this.incompleteConf = incompleteConf;
  }

  public MDElement getElm() {
    return elm;
  }

  public void setElm(MDElement elm) {
    this.elm = elm;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Dimension Element:");
    builder.append(this.getElm().getName());
    if (getMsr() != null) {
      builder.append("/Measure:");
      builder.append(this.getMsr().getName());
    }
    builder.append("/Non-strict configuration:");
    builder.append(this.getNonStrictConf().toString());
    builder.append("/Incomplete configuration:");
    builder.append(this.getIncompleteConf().toString());
    return builder.toString();
  }
}
