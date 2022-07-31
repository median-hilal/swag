package swag.sparql_builder.ASElements;

import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.md_elements.Mapping;
import swag.md_elements.MappingFunctions;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;

public class OptionalDimensionalQueryBuilder /* implements IDimensionalQueryGroupVarBodyBuilder */
{

  private static final Logger logger = Logger.getLogger(OptionalDimensionalQueryBuilder.class);

  public CustomSPARQLQuery addDimensionalQueryGroupByVarBody(IDimensionQualification dimToAS,
      CustomSPARQLQuery rq, MDSchema mdSchema) throws Exception {



    // boolean queryContainsHeadVar = true;
    CustomSPARQLQuery granularityLevelQuery = new CustomSPARQLQuery();

    if (dimToAS.getGranularities().size() != 0) {
      // the granularity query is added as-is in case it is neither null nor a variable
      if (dimToAS.getGranularities().get(0).getPosition() != null
          && !dimToAS.getGranularities().get(0).getPosition().getIdentifyingName().equals("")
          && !dimToAS.getGranularities().get(0).getPosition().getSignature().isVariable()) {


        granularityLevelQuery = new CustomSPARQLQuery(dimToAS.getGranularities().get(0).getPosition()
            .getMapping().getQuery().getSparqlQuery());
        granularityLevelQuery.removeQueryPatternDuplications();

        boolean diceExists = false;
        int compareToDiceLevel = -1;
        boolean sliceExists = false;
        int compareToSliceLevel = -1;

        if (dimToAS.getDices().size() > 0) {
          diceExists = true;
          compareToDiceLevel = mdSchema.compareItemsInDimension(
              dimToAS.getGranularities().get(0).getPosition().getIdentifyingName(),
              dimToAS.getDices().get(0).getPosition().getIdentifyingName());
        }

        if (dimToAS.getSliceConditions().size() > 0) {
          sliceExists = true;
          compareToSliceLevel = mdSchema.compareItemsInDimension(
              dimToAS.getGranularities().get(0).getPosition().getIdentifyingName(),
              dimToAS.getSliceConditions().get(0).getPositionOfCondition().getIdentifyingName());
        }

        // Here avoiding redundancies. If the granularity is used in slice and dice, then it should
        // not be put it in an optional query because it is not optional anyways
        if (diceExists || sliceExists) {

          // Only dice exists
          if (diceExists && !sliceExists) {

            // Granularity is before or equal
            if (compareToDiceLevel > 0) {

              rq.getSparqlQuery().getProjectVars()
                  .add(granularityLevelQuery.getUnaryProjectionVar());
            } else {
              if (compareToDiceLevel == 0) {

                try {
                  Mapping m = MappingFunctions.getPathQueryExclusive(mdSchema,
                      dimToAS.getDices().get(0).getPosition().getIdentifyingName(),
                      dimToAS.getGranularities().get(0).getPosition().getIdentifyingName());
                  rq = SPARQLUtilities.joinWithAsOptional(rq, m.getQuery());

                } catch (NoMappingExistsForElementException ex) {
                  logger.error("Cannot find path.", ex);
                  throw (ex);
                }

              } else {
                MDElement elem = mdSchema.getLeastCommonAncestor(
                    dimToAS.getGranularities().get(0).getPosition().getIdentifyingName(),
                    dimToAS.getDices().get(0).getPosition().getIdentifyingName());
                try {
                  Mapping m =
                      MappingFunctions.getPathQueryExclusive(mdSchema, elem.getIdentifyingName(),
                          dimToAS.getGranularities().get(0).getPosition().getIdentifyingName());
                  rq = SPARQLUtilities.joinWithAsOptional(rq, m.getQuery());

                } catch (NoMappingExistsForElementException ex) {
                  logger.error("Cannot find path.", ex);
                  throw (ex);
                }
              }
            }
          }

          // Only sice exists
          if (sliceExists && !diceExists) {

            // Granularity is before or equal
            if (compareToSliceLevel > 0) {

              rq.getSparqlQuery().getProjectVars()
                  .add(granularityLevelQuery.getUnaryProjectionVar());
            } else {
              if (compareToSliceLevel == 0) {
                try {
                  Mapping m = MappingFunctions.getPathQueryExclusive(mdSchema,
                      dimToAS.getSliceConditions().get(0).getPositionOfCondition()
                          .getIdentifyingName(),
                      dimToAS.getGranularities().get(0).getPosition().getIdentifyingName());
                  rq = SPARQLUtilities.joinWithAsOptional(rq, m.getQuery());

                } catch (NoMappingExistsForElementException ex) {
                  logger.error("Cannot find path.", ex);
                  throw (ex);
                }
              } else {
                MDElement elem = mdSchema.getLeastCommonAncestor(
                    dimToAS.getGranularities().get(0).getPosition().getIdentifyingName(),
                    dimToAS.getSliceConditions().get(0).getPositionOfCondition()
                        .getIdentifyingName());
                try {
                  Mapping m =
                      MappingFunctions.getPathQueryExclusive(mdSchema, elem.getIdentifyingName(),
                          dimToAS.getGranularities().get(0).getPosition().getIdentifyingName());
                  rq = SPARQLUtilities.joinWithAsOptional(rq, m.getQuery());

                } catch (NoMappingExistsForElementException ex) {
                  logger.error("Cannot find path.", ex);
                  throw (ex);
                }
              }
            }
          }

          // Both dice and slice exist
          if (sliceExists && diceExists) {
            // Granularity is before or equal
            if (compareToDiceLevel > 0 || compareToSliceLevel > 0) {
              // Nothing to do
              rq.getSparqlQuery().getProjectVars()
                  .add(granularityLevelQuery.getUnaryProjectionVar());
            } else {
              if (compareToDiceLevel == 0 && compareToSliceLevel == 0) {

                MDElement usedMDElement;

                int compareDiceLevelToSlice = mdSchema.compareItemsInDimension(
                    dimToAS.getDices().get(0).getPosition().getIdentifyingName(),
                    dimToAS.getSliceConditions().get(0).getPositionOfCondition()
                        .getIdentifyingName());

                if (compareDiceLevelToSlice > 0) {
                  usedMDElement = dimToAS.getSliceConditions().get(0).getPositionOfCondition();
                } else {
                  if (compareDiceLevelToSlice == 0) {
                    usedMDElement = dimToAS.getDices().get(0).getPosition();
                  } else {
                    Exception ex = new Exception("Uneven dimension.");
                    logger.error("Uneven dimension.", ex);
                    throw (ex);
                  }
                }


              } else {
                if ((compareToDiceLevel == 0 && compareToSliceLevel < 0)
                    || (compareToDiceLevel < 0 && compareToSliceLevel == 0)
                    || (compareToDiceLevel < 0 && compareToSliceLevel < 0)) {

                  MDElement elem1 = mdSchema.getLeastCommonAncestor(
                      dimToAS.getGranularities().get(0).getPosition().getIdentifyingName(),
                      dimToAS.getDices().get(0).getPosition().getIdentifyingName());

                  MDElement elem2 = mdSchema.getLeastCommonAncestor(
                      dimToAS.getGranularities().get(0).getPosition().getIdentifyingName(),
                      dimToAS.getSliceConditions().get(0).getPositionOfCondition()
                          .getIdentifyingName());

                  if (mdSchema.rollsUpDirectlyOrIndirectlyTo(elem1.getIdentifyingName(),
                      elem2.getIdentifyingName())) {
                    try {
                      Mapping m = MappingFunctions.getPathQueryExclusive(mdSchema,
                          dimToAS.getSliceConditions().get(0).getPositionOfCondition()
                              .getIdentifyingName(),
                          dimToAS.getGranularities().get(0).getPosition().getIdentifyingName());
                      rq = SPARQLUtilities.joinWithAsOptional(rq, m.getQuery());

                    } catch (NoMappingExistsForElementException ex) {
                      logger.error("Cannot find path.", ex);
                      throw (ex);
                    }
                  } else {
                    if (mdSchema.rollsUpDirectlyOrIndirectlyTo(elem2.getIdentifyingName(),
                        elem1.getIdentifyingName())) {
                      try {
                        Mapping m = MappingFunctions.getPathQueryExclusive(mdSchema,
                            dimToAS.getSliceConditions().get(0).getPositionOfCondition()
                                .getIdentifyingName(),
                            dimToAS.getGranularities().get(0).getPosition().getIdentifyingName());
                        rq = SPARQLUtilities.joinWithAsOptional(rq, m.getQuery());

                      } catch (NoMappingExistsForElementException ex) {
                        logger.error("Cannot find path.", ex);
                        throw (ex);
                      }
                    } else {
                      Exception ex = new Exception("Uneven dimension.");
                      logger.error("Uneven dimension.", ex);
                      throw (ex);
                    }
                  }

                } else {
                  Exception ex = new Exception("Uneven dimension.");
                  logger.error("Uneven dimension.", ex);
                  throw (ex);
                }
              }
            }
          }

          // In case there is a granularity, but its query is not added to the joined queries, the
          // head variable of the granularity should be added anyways.
          // rq.getSparqlQuery().getProjectVars()
          // .add(granularityLevelQuery.getUnaryProjectionVar());
        } else {
          rq = SPARQLUtilities.joinWithAsOptional(rq, granularityLevelQuery);
        }

        // the case there is no granularity, i.e. it is either null or a non-bound variable
      } else {
        if (dimToAS.getGranularities().get(0).getPosition().getIdentifyingName().equals("")) {

          rq.getSparqlQuery().addResultVar("xXxArtificialxXx"); // Artificial temporal head variable
                                                                // is adde and removed at the end
        }
      }
    } else {
      rq.getSparqlQuery().addResultVar("xXxArtificialxXx");
    }
    // return queryContainsHeadVar;
    return rq;

  }

}
