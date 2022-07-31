package swag.predicates;

public class QUERY_STRINGS {

//@formatter:off
  public final static String LITERAL_CONDITION_TYPES =
      "PREFIX pr: <http://www.amcis2021.com/swag/pr#>"
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
          + "PREFIX wdt: <http://www.wikidata.org/prop/direct/>"
          + "PREFIX ag: <http://www.amcis2021.com/swag/ag#>"
          + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
          + "PREFIX wd: <http://www.wikidata.org/entity/>"
          + ""
          + "SELECT distinct ?literalConditionType "
          + "?inputVar ?inputVarType ?inputVarName "
          + "?positionVar ?positionVarType ?positionVarName "
          + "?onMDElement ?conditoinPosition ?conditinPositoinLevel ?hierLevel ?dimLevel"
          + "?conditinPositoinAttribute ?levelOnHierAndDim ?hierAttribute ?dimAttribute"
          + "?conditinPositoinMeasure"
          + "?expression ?label ?comment ?sType "
          + "?hierOfCond ?dimOfCond "
          + "WHERE "
          + " {"
            + " {{ ?literalConditionType rdf:type ag:Predicate}"
            + " UNION { ?literalConditionType rdf:type ag:FreeMDPredicate } "
            + " UNION { ?literalConditionType rdf:type ag:FreeDimensionPredicate } "
            + " UNION { ?literalConditionType rdf:type ag:FreeResultPredicate } "
            + " } "
          + " OPTIONAL { ?literalConditionType ag:predicateMDElement ?positionVar. "
            + " OPTIONAL { ?positionVar ag:name ?positionVarName } "
            + " OPTIONAL { ?positionVar ag:onElement ?onMDElement .} "
            + " OPTIONAL { ?positionVar ag:onElement ?onMDElement . "
            + " ?onMDElement rdf:type ag:QualifiedAttribute ; "
            + " ag:ofAttribute ?conditinPositoinAttribute ; "
            + " ag:inLevel ?levelOnHierAndDim ; "
            + " ag:inHierarchy ?hierAttribute ; "
            + " ag:inDimension ?dimAttribute "
            + " } "
            + "OPTIONAL "
            + " { ?positionVar ag:onElement ?onMDElement . "
            + " ?onMDElement rdf:type ag:QualifiedLevel ; "
            + " ag:ofLevel ?conditinPositoinLevel ;"
            + " ag:inHierarchy ?hierAttribute ; "
            + " ag:inDimension ?dimAttribute }"
          + " } "
          + " OPTIONAL { ?positionVar ag:onElement ?onMDElement . "
                  + " ?positionVar ag:inHierarchy ?hierOfCond . "
                  + " ?positionVar ag:inDimension ?dimOfCond. "
                  + " } "
          + " OPTIONAL "
          + " { {?literalConditionType ag:predicateVariable ?inputVar.} "
            + " OPTIONAL { ?inputVar ag:domain ?typeBNode . "
            + " ?typeBNode ag:dataType ?inputVarType } "
            + " OPTIONAL { ?inputVar ag:name ?inputVarName } "
            + " } "
          + "OPTIONAL { ?literalConditionType "
          + " ag:expression ?expression } "
          + " OPTIONAL { ?literalConditionType rdfs:label ?labelToTrim FILTER ( lang(?labelToTrim) = \"en\" ) BIND(str(?labelToTrim) AS ?label) } "
          + " OPTIONAL { ?literalConditionType rdfs:comment ?comment } "
          + " OPTIONAL { ?literalConditionType ag:clause ?sType } }"
          + "ORDER BY ?literalConditionType";
  
  public final static String LITERAL_CONDITIONS =
      "PREFIX pr: <http://www.amcis2021.com/swag/pr#>"
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
          + "PREFIX wdt: <http://www.wikidata.org/prop/direct/>"
          + "PREFIX ag: <http://www.amcis2021.com/swag/ag#>"
          + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
          + "PREFIX wd: <http://www.wikidata.org/entity/>"
          + ""
          + "SELECT distinct ?literalConditionType "
          + "?inputVar ?inputVarType ?inputVarName "
          + "?positionVar ?positionVarType ?positionVarName "
          + "?onMDElement ?conditoinPosition ?conditinPositoinLevel ?hierLevel ?dimLevel"
          + "?conditinPositoinAttribute ?levelOnHierAndDim ?hierAttribute ?dimAttribute"
          + "?conditinPositoinMeasure"
          + "?expression ?label ?comment ?sType"
          + "?conditionType "
          + "?hierOfCond ?dimOfCond "
          + "WHERE "
          + " {"
            + " {{ ?literalConditionType rdf:type ag:GroundPredicate}"
            + " UNION { ?literalConditionType rdf:type ag:GroundMDPredicate } "
            + " UNION { ?literalConditionType rdf:type ag:GroundDimensionPredicate } "
            + " UNION { ?literalConditionType rdf:type ag:GroundResultPredicate } "
            + " } "
          + " OPTIONAL { ?literalConditionType ag:predicateMDElement ?positionVar. "
            + " OPTIONAL { ?positionVar ag:name ?positionVarName } "
            + " OPTIONAL { ?positionVar ag:onElement ?onMDElement .} "
            + " OPTIONAL { ?positionVar ag:onElement ?onMDElement . "
            + " ?onMDElement rdf:type ag:QualifiedAttribute ; "
            + " ag:ofAttribute ?conditinPositoinAttribute ; "
            + " ag:inLevel ?levelOnHierAndDim ; "
            + " ag:inHierarchy ?hierAttribute ; "
            + " ag:inDimension ?dimAttribute "
            + " } "
            + " OPTIONAL { ?positionVar ag:onElement ?onMDElement . "
            + " ?positionVar ag:inHierarchy ?hierOfCond . "
            + " ?positionVar ag:inDimension ?dimOfCond. "
            + " } "
            + "OPTIONAL "
            + " { ?positionVar ag:onElement ?onMDElement . "
            + " ?onMDElement rdf:type ag:QualifiedLevel ; "
            + " ag:ofLevel ?conditinPositoinLevel ;"
            + " ag:inHierarchy ?hierAttribute ; "
            + " ag:inDimension ?dimAttribute }"
          + " } "
          + "OPTIONAL { ?literalConditionType "
          + " ag:expression ?expression } "
          + " OPTIONAL { ?literalConditionType rdfs:label ?labelToTrim FILTER ( lang(?labelToTrim) = \"en\" ) BIND(str(?labelToTrim) AS ?label) } "
          + " OPTIONAL { ?literalConditionType rdfs:comment ?comment } "
          + " OPTIONAL { ?literalConditionType ag:clause ?sType } "
          + " OPTIONAL { ?literalConditionType ag:derivedFrom ?conditionType } "
          + "}"
          + "ORDER BY ?literalConditionType";

  

//@formatter:off
  public final static String CONDITIONS_WITH_BINDINGS = 
      "PREFIX wd: <http://www.wikidata.org/entity/>\r\n" + 
      "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\r\n" +
      "PREFIX pr:<http://www.amcis2021.com/swag/pr#> \r\n" + 
      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" +
      "PREFIX ag: <http://www.amcis2021.com/swag/ag#> \r\n " +
      "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"  +
      
      "select ?conditoinURI ?conditoinString" +
      " ?conditoinPosition " +
      " ?conditinPositoinLevel ?hierLevel ?dimLevel" +
      " ?conditinPositoinAttribute ?levelOnHierAndDim ?hierAttribute ?dimAttribute" +
      " ?conditinPositoinMeasure \r\n" +
      " ?conditionType \r\n" +
      " ?instanceOf \r\n" +
      " ?bindingVar ?bindingVal \r\n" +
      " ?label ?comment \r\n" +
      
      
      "where{ ?conditoinURI rdf:type pr:InstPredicate.\r\n" + 
      "    optional {?conditoinURI pr:predExpression ?conditoinString.} \r\n" + 
      "    optional {?conditoinURI pr:predElement ?conditoinPosition}" +
      "    optional {?conditoinURI pr:predElement ?conditinPositoinAttribute." +
                    "?conditinPositoinAttribute rdf:type ag:AttributeOnLevel.  " +
                    "?conditinPositoinAttribute ag:onLevelInHierAndDim ?levelOnHierAndDim." +
                    "?levelOnHierAndDim ag:onHierInDim ?hierInDim ." +
                    "?hierInDim ag:hierOfHierInDim ?hierAttribute." +
                    "?hierInDim ag:dimOfHierInDim ?dimAttribute}\r\n" +
                    
      "    optional {?conditoinURI ag:predMDElement ?conditinPositoinLevel." +
                    "?conditinPositoinLevel rdf:type ag:LevelInHierAndDim.  " +                    
                    "?conditinPositoinLevel ag:onHierInDim ?hierInDim ." +
                    "?hierInDim ag:hierOfHierInDim ?hierLevel." +
                    "?hierInDim ag:dimOfHierInDim ?dimLevel}\r\n"
                    +      
      "    optional {?conditoinURI pr:derivedFrom ?instanceOf.}" +
                    
      "    optional {?conditoinURI pr:varBinding ?varBinding." +
      "              ?varBinding pr:bindingVar ?bindingVar."  +
      "              ?varBinding pr:bindingValue ?bindingVal. }" +
      
     "    optional {?conditoinURI rdfs:label ?labelToTrim." +
     "              FILTER (lang(?labelToTrim) = 'en')." +
     "              BIND (str(?labelToTrim) as ?label)}." +
   
      "    optional {?conditoinURI rdfs:comment ?comment}." +
      
      "} ORDER BY ?conditoinURI";


  
//@formatter:off
  public final static String CONDITIONS = 
      "PREFIX wd: <http://www.wikidata.org/entity/>\r\n" + 
      "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\r\n" +
      "PREFIX pr:<http://www.amcis2021.com/swag/pr#> \r\n" + 
      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" +
      "PREFIX ag: <http://www.amcis2021.com/swag/ag#> \r\n " +
      "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"  +
      
      "select ?conditoinURI ?conditoinString" +
      " ?conditoinPosition " +
      " ?conditinPositoinLevel ?hierLevel ?dimLevel" +
      " ?conditinPositoinAttribute ?levelOnHierAndDim ?hierAttribute ?dimAttribute" +
      " ?conditinPositoinMeasure \r\n" +
      " ?conditionType \r\n" +
      " ?label ?comment \r\n" +
      
      
      "where{ ?conditoinURI rdf:type pr:GroundPredicate.\r\n" + 
      "    optional {?conditoinURI pr:predExpression ?conditoinString.} \r\n" + 
      "    optional {?conditoinURI pr:predElement ?conditoinPosition}" +
      "    optional {?conditoinURI pr:predElement ?conditinPositoinAttribute." +
                    "?conditinPositoinAttribute rdf:type ag:AttributeOnLevel.  " +
                    "?conditinPositoinAttribute ag:onLevelInHierAndDim ?levelOnHierAndDim." +
                    "?levelOnHierAndDim ag:onHierInDim ?hierInDim ." +
                    "?hierInDim ag:hierOfHierInDim ?hierAttribute." +
                    "?hierInDim ag:dimOfHierInDim ?dimAttribute}\r\n" +
                    
      "    optional {?conditoinURI ag:predMDElement ?conditinPositoinLevel." +
                    "?conditinPositoinLevel rdf:type ag:LevelInHierAndDim.  " +                    
                    "?conditinPositoinLevel ag:onHierInDim ?hierInDim ." +
                    "?hierInDim ag:hierOfHierInDim ?hierLevel." +
                    "?hierInDim ag:dimOfHierInDim ?dimLevel}\r\n"
                    +      
      "    optional {?conditoinURI pr:predType ?conditionType.}" +
                    
      
      "    optional {?conditoinURI rdfs:label ?labelToTrim." +
      "              FILTER (lang(?labelToTrim) = 'en')." +
      "              BIND (str(?labelToTrim) as ?label)}." +
   
      "    optional {?conditoinURI rdfs:comment ?comment}." +

      "} "
      + " ORDER BY ?conditoinURI";

  //@formatter:off
  public final static String PREDICATE_INSTANCES = 
      "PREFIX wd: <http://www.wikidata.org/entity/>\r\n" + 
      "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\r\n"
      + "PREFIX pr:<http://www.amcis2021.com/swag/pr#> "
      + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
      + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
      + ""
      + "select ?predicateInstance ?description ?bindingVar ?bindingVal ?varProjection ?resVar ?resVal " 
      + "where{"
      + "?predicateInstance rdf:type pr:PredicateInstance."
      + "?predicateInstance pr:derivedFrom ?xXxPredicatexXx."
      + "optional {?predicateInstance pr:hasDescription ?description.}" 
      + "optional {?predicateInstance pr:hasVarBinding ?varBinding."
      + "?varBinding pr:bindingOnVar ?bindingVar." 
      + "?varBinding pr:bindingHasValue ?bindingVal. }"
      + "optional {?predicateInstance pr:hasVarProjection ?varProjection.}" 
      + "optional {?predicateInstance pr:hasResult ?res."
      + "?res pr:resOnVar ?resVar."
      + "?res pr:resHasValue ?resVal.} "
      + "}"
      + "order by ?predicateInstance";
  
  //@formatter:off
  public final static String PREDICATE_INSTANCE_BY_NAME = 
      "PREFIX wd: <http://www.wikidata.org/entity/>\r\n" + 
      "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\r\n" + 
      "PREFIX pr:<http://www.amcis2021.com/swag/pr#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
      "select ?predicate ?query ?subjectVar ?bindingVar ?bindingVal ?selectionVar ?resVar ?resVal\r\n" + 
      "where{ \r\n" + 
      "    ?xXxPredicateInstancexXx rdf:type pr:PredicateInstance.\r\n" + 
      "    ?predicateInstance pr:derivedFrom ?predicate.\r\n" + 
      "    optional {?predicate pr:hasQuery ?query.}optional {?predicate pr:hasTopic ?topic.}\r\n" + 
      "    optional {?predicate pr:hasSubjectVar ?subjectVar.}\r\n" + 
      "    optional {wd:Q178810 pr:hasVarBinding ?varBinding.}optional { ?varBinding pr:bindingOnVar ?bindingVar.}optional { ?varBinding pr:bindingHasValue ?bindingVal. }\r\n" + 
      "    optional {wd:Q178810 pr:hasVarSelection ?varSelection.}optional {?varSelection pr:selectionVar ?selectionVar.}\r\n" + 
      "    optional {wd:Q178810 pr:hasResult ?res.}optional {?varBinding pr:resOnVar ?resVar.}optional {?varBinding pr:resHasValue ?resVal.}\r\n" + 
      "}";
  
//@formatter:off
  public final static String PREDICATE_SUBJECTS = 
      "PREFIX wd: <http://www.wikidata.org/entity/>\r\n" + 
      "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\r\n"
      + "PREFIX pr:<http://www.amcis2021.com/swag/pr#> \r\n" + 
      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
      "select ?predicate ?outputVar ?outputVarType ?outputVarName ?inputVar ?inputVarType ?inputVarName ?subjectVar ?subjectVarType ?subjectVarName ?descriptionVar ?descriptionVarType ?descriptionVarName ?qurey ?topic\r\n" + 
      "where{ ?predicate rdf:type pr:Predicate.\r\n" + 
      "    optional {?predicate pr:outputVar ?outputVar.} optional {?outputVar pr:type ?outputVarType.} optional {?outputVar pr:hasVarName ?outputVarName.} \r\n" + 
      "    optional {?predicate pr:inputVar ?inputVar.} optional {?inputVar pr:type ?inputVarType.} optional {?inputVar pr:hasVarName ?inputVarName.}\r\n" + 
      "    optional {?predicate pr:hasSubjectVar ?subjectVar.} optional {?subjectVar pr:type ?subjectVarType.} optional {?subjectVar pr:hasVarName ?subjectVarName.} \r\n" + 
      "    optional {?predicate pr:hasDescriptionVar ?descriptionVar.} optional {?descriptionVar pr:type ?descriptionVarType.} optional {?descriptionVar pr:hasVarName ?descriptionVarName.} \r\n" + 
      "    optional {?predicate pr:hasQuery ?query.}optional {?predicate pr:hasTopic ?topic.}\r\n" + 
      "}order by ?predicate";
  
//@formatter:off
  public final static String PREDICATES = 
      "PREFIX wd: <http://www.wikidata.org/entity/>\r\n" + 
      "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\r\n"
      + "PREFIX pr:<http://www.amcis2021.com/swag/pr#> \r\n" + 
      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
      "select ?predicate ?outputVar ?outputVarType ?outputVarName ?inputVar ?inputVarType ?inputVarName ?subjectVar ?subjectVarType ?subjectVarName ?descriptionVar ?descriptionVarType ?descriptionVarName ?query ?topic\r\n" + 
      "where{ ?predicate rdf:type pr:Predicate.\r\n" + 
      "    optional {?predicate pr:outputVar ?outputVar.} optional {?outputVar pr:type ?outputVarType.} optional {?outputVar pr:hasVarName ?outputVarName.} \r\n" + 
      "    optional {?predicate pr:inputVar ?inputVar.} optional {?inputVar pr:type ?inputVarType.} optional {?inputVar pr:hasVarName ?inputVarName.}\r\n" + 
      "    optional {?predicate pr:hasSubjectVar ?subjectVar.} optional {?subjectVar pr:type ?subjectVarType.} optional {?subjectVar pr:hasVarName ?subjectVarName.} \r\n" + 
      "    optional {?predicate pr:hasDescriptionVar ?descriptionVar.} optional {?descriptionVar pr:type ?descriptionVarType.} optional {?descriptionVar pr:hasVarName ?descriptionVarName.} \r\n" + 
      "    optional {?predicate pr:hasQuery ?query.} optional {?predicate pr:hasTopic ?topic.}\r\n" + 
      "} order by ?predicate";
  
//@formatter:off
  public final static String PREDICATE_BY_NAME = 
      "PREFIX wd: <http://www.wikidata.org/entity/>\r\n" + 
      "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\r\n"
      + "PREFIX pr:<http://www.amcis2021.com/swag/pr#> \r\n" + 
      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
      "select ?predicate ?outputVar ?outputVarType ?outputVarName ?inputVar ?inputVarType ?inputVarName ?subjectVar ?subjectVarType ?subjectVarName ?descriptionVar ?descriptionVarType ?descriptionVarName ?qurey ?topic\r\n" + 
      "where{ ?xXxPredicatexXx rdf:type pr:Predicate.\r\n" + 
      "    optional {?xXxPredicatexXx pr:outputVar ?outputVar.} optional {?outputVar pr:type ?outputVarType.} optional {?outputVar pr:hasVarName ?outputVarName.} \r\n" + 
      "    optional {?xXxPredicatexXx pr:inputVar ?inputVar.} optional {?inputVar pr:type ?inputVarType.} optional {?inputVar pr:hasVarName ?inputVarName.}\r\n" + 
      "    optional {?xXxPredicatexXx pr:hasSubjectVar ?subjectVar.} optional {?subjectVar pr:type ?subjectVarType.} optional {?subjectVar pr:hasVarName ?subjectVarName.} \r\n" + 
      "    optional {?xXxPredicatexXx pr:hasDescriptionVar ?descriptionVar.} optional {?descriptionVar pr:type ?descriptionVarType.} optional {?descriptionVar pr:hasVarName ?descriptionVarName.} \r\n" + 
      "    optional {?xXxPredicatexXx pr:hasQuery ?query.}optional {?predicate pr:hasTopic ?topic.}\r\n" + 
      "}order by ?xXxPredicatexXx";
}
