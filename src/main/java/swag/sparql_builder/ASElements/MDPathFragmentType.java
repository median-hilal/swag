package swag.sparql_builder.ASElements;

public enum MDPathFragmentType implements Comparable<MDPathFragmentType> {
  Granularity(0), Dice(1), Slice(1), Shared(1), Mandatory(2);

  private final int strength;

  public int getStrength() {
    return strength;
  }

  private MDPathFragmentType(int strength) {
    this.strength = strength;
  }

  /**
   * 
   * compares strength of two fragment types
   * 
   * @param typ1
   * @param typ2
   * 
   * @return 1 if {@code typ1} is stronger than {@code typ2}, 0 if equal, -1 if {@code typ2} is
   *         stronger than {@code typ1}
   * 
   */
  public static int isStrongerThan(MDPathFragmentType typ1, MDPathFragmentType typ2) {

    if (typ1.getStrength() > typ2.getStrength()) {
      return 1;
    }

    if (typ1.getStrength() == typ2.getStrength()) {
      return 0;
    }

    return -1;
  }

}
