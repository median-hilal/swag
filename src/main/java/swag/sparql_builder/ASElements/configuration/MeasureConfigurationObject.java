package swag.sparql_builder.ASElements.configuration;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import swag.analysis_graphs.execution_engine.analysis_situations.IMeasure;
import swag.md_elements.MDSchema;
import swag.sparql_builder.reporting.IMeasureReoprter;
import swag.sparql_builder.reporting.MeasureReporterDiscardMissing;

/**
 * Specifies a single measure configuration
 * 
 * @author swag
 *
 */
public class MeasureConfigurationObject implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 252139192010105087L;


  private IMeasure msr;
  private MsrNonStrictConfigurationType nonStrictConf = MsrNonStrictConfigurationType.TREAT_AS_FACT;
  private Object nonStrictVal;
  private MsrIncompleteConfigurationType incompleteConf =
      MsrIncompleteConfigurationType.DISCARD_FACT;
  private Object incompleteVal;

  public MeasureConfigurationObject(IMeasure msr) {
    super();
    Objects.requireNonNull(msr);
    this.msr = msr;
  }

  public MeasureConfigurationObject(IMeasure msr, MsrNonStrictConfigurationType nonStrictConf,
      Object nonStrictVal, MsrIncompleteConfigurationType incompleteConf, Object incompleteVal) {
    super();
    Objects.requireNonNull(msr);
    this.msr = msr;
    this.nonStrictConf = nonStrictConf;
    this.nonStrictVal = nonStrictVal;
    this.incompleteConf = incompleteConf;
    this.incompleteVal = incompleteVal;
  }

  public static boolean isIncompleteNone(MDSchema schema, List<MeasureConfigurationObject> confs,
      IMeasure msr) {
    MeasureConfigurationObject msrConf = new MeasureConfigurationObject(msr);
    return getMatchingIncompleteConf(confs, msrConf)
        .equals(MsrIncompleteConfigurationType.DISCARD_FACT);
  }

  public static boolean isIncompleteNone(MDSchema schema, List<MeasureConfigurationObject> confs,
      MeasureConfigurationObject other) {
    return getMatchingIncompleteConf(confs, other)
        .equals(MsrIncompleteConfigurationType.DISCARD_FACT);
  }

  public static boolean isIncompleteDefaultValue(List<MeasureConfigurationObject> confs,
      IMeasure msr) {
    MeasureConfigurationObject msrConf = new MeasureConfigurationObject(msr);
    return getMatchingIncompleteConf(confs, msrConf)
        .equals(MsrIncompleteConfigurationType.DEFAULT_VALUE);
  }

  public static boolean isIncompleteDefaultValue(List<MeasureConfigurationObject> confs,
      MeasureConfigurationObject other) {
    return getMatchingIncompleteConf(confs, other)
        .equals(MsrIncompleteConfigurationType.DEFAULT_VALUE);
  }

  public boolean isNonStrictInternAgg() {
    return this.getNonStrictConf().equals(MsrNonStrictConfigurationType.INTERN_AGG);
  }

  public boolean isIncompleteOptional(List<IMeasureReoprter> reporters) {
    return this.getIncompleteConf().equals(MsrIncompleteConfigurationType.OPTIONAL) && reporters
        .stream().anyMatch(reporter -> reporter instanceof MeasureReporterDiscardMissing);
  }

  public boolean isIncompleteNone() {
    return this.getIncompleteConf().equals(MsrIncompleteConfigurationType.DISCARD_FACT);
  }

  public static boolean isNonStrictInternAgg(List<MeasureConfigurationObject> confs, IMeasure msr) {
    MeasureConfigurationObject msrConf = new MeasureConfigurationObject(msr);
    return getMatchingNonStrictConf(confs, msrConf)
        .equals(MsrNonStrictConfigurationType.INTERN_AGG);
  }

  public static boolean isNonStrictInternAgg(List<MeasureConfigurationObject> confs,
      MeasureConfigurationObject other) {
    return getMatchingNonStrictConf(confs, other).equals(MsrNonStrictConfigurationType.INTERN_AGG);
  }

  public static boolean isNonStrictNone(List<MeasureConfigurationObject> confs, IMeasure msr) {
    MeasureConfigurationObject msrConf = new MeasureConfigurationObject(msr);
    return getMatchingNonStrictConf(confs, msrConf)
        .equals(MsrNonStrictConfigurationType.TREAT_AS_FACT);
  }

  public static boolean isNonStrictNone(List<MeasureConfigurationObject> confs,
      MeasureConfigurationObject other) {
    return getMatchingNonStrictConf(confs, other)
        .equals(MsrNonStrictConfigurationType.TREAT_AS_FACT);
  }

  public static MsrIncompleteConfigurationType getMatchingIncompleteConf(
      List<MeasureConfigurationObject> confs, IMeasure measure) {
    MeasureConfigurationObject other = new MeasureConfigurationObject(measure);
    return Optional.ofNullable(confs).orElseGet(Collections::emptyList).stream()
        .filter(conf -> conf.getMsr().equals(other.getMsr())).findAny()
        .map(MeasureConfigurationObject::getIncompleteConf)
        .orElse(MsrIncompleteConfigurationType.DISCARD_FACT);
  }

  public static MsrIncompleteConfigurationType getMatchingIncompleteConf(
      List<MeasureConfigurationObject> confs, MeasureConfigurationObject other) {

    return Optional.ofNullable(confs).orElseGet(Collections::emptyList).stream()
        .filter(conf -> conf.getMsr().equals(other.getMsr())).findAny()
        .map(MeasureConfigurationObject::getIncompleteConf)
        .orElse(MsrIncompleteConfigurationType.DISCARD_FACT);
  }

  public static Optional<MeasureConfigurationObject> getMatchingConf(
      List<MeasureConfigurationObject> confs, IMeasure measure) {

    MeasureConfigurationObject other = new MeasureConfigurationObject(measure);

    return Optional.ofNullable(confs).orElseGet(Collections::emptyList).stream()
        .filter(conf -> conf.getMsr().equals(other.getMsr())).findAny();
  }

  public static MsrNonStrictConfigurationType getMatchingNonStrictConf(
      List<MeasureConfigurationObject> confs, IMeasure measure) {

    MeasureConfigurationObject other = new MeasureConfigurationObject(measure);

    return Optional.ofNullable(confs).orElseGet(Collections::emptyList).stream()
        .filter(conf -> conf.getMsr().equals(other.getMsr())).findAny()
        .map(MeasureConfigurationObject::getNonStrictConf)
        .orElse(MsrNonStrictConfigurationType.TREAT_AS_FACT);
  }

  public static MsrNonStrictConfigurationType getMatchingNonStrictConf(
      List<MeasureConfigurationObject> confs, MeasureConfigurationObject other) {

    return Optional.ofNullable(confs).orElseGet(Collections::emptyList).stream()
        .filter(conf -> conf.getMsr().equals(other.getMsr())).findAny()
        .map(MeasureConfigurationObject::getNonStrictConf)
        .orElse(MsrNonStrictConfigurationType.TREAT_AS_FACT);
  }

  public Object getNonStrictVal() {
    return nonStrictVal;
  }

  public void setNonStrictVal(Object nonStrictVal) {
    this.nonStrictVal = nonStrictVal;
  }

  public Object getIncompleteVal() {
    return incompleteVal;
  }

  public void setIncompleteVal(Object incompleteVal) {
    this.incompleteVal = incompleteVal;
  }

  public void setNonStrictConf(MsrNonStrictConfigurationType nonStrictConf) {
    this.nonStrictConf = nonStrictConf;
  }

  public void setIncompleteConf(MsrIncompleteConfigurationType incompleteConf) {
    this.incompleteConf = incompleteConf;
  }

  public IMeasure getMsr() {
    return msr;
  }

  public void setMsr(IMeasure msr) {
    this.msr = msr;
  }

  public MsrNonStrictConfigurationType getNonStrictConf() {
    return nonStrictConf;
  }

  public MsrIncompleteConfigurationType getIncompleteConf() {
    return incompleteConf;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Measure:");
    builder.append(this.getMsr().getName());
    builder.append("/Non-strict configuration:");
    builder.append(this.getNonStrictConf().toString());
    builder.append("-");
    builder.append(this.getNonStrictVal());
    builder.append("/Incomplete configuration:");
    builder.append(this.getIncompleteVal());
    builder.append("-");
    builder.append(this.getIncompleteConf().toString());
    return builder.toString();
  }
}
