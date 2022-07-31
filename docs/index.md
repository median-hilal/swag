# Semantic Web Analysis Graphs

The linked open data (LOD) comprise hundreds of data sets and billions of triples of different domains, which represent an important resource of knowledge for Multidimensional (MD) data analysis.
Modeling data according to the multidimensional data model renders these data accessible to Online Analytical Processing (OLAP).
The QB4OLAP vocabulary allows to represent statistical LOD on the semantic web.
Furthermore, superimposition of MD schemas allows OLAP to access non-statistical LOD.

There is a variety of users who may be interested in conducting MD analysis over LOD sources, e.g., ordinary people and non-governmental organizations.
Users who are willing to make MD analysis of LOD sources face various obstacles.
In particular, users may lack the technical skills to conduct an analysis, e.g., dealing with RDF and SPARQL, and are also unlikely to have appropriate analytical domain knowledge to know which analytical processes can be interesting or sense-making.

Semantic web analysis graphs (SWAG) allow to model generic analytical processes over the semantic web.
SWAGs can contain variables which allow to customize analytical processes to particular scenarios.
Modeling analytical processes with SWAGs allows for the creation of tools that empower users to conduct guided MD analysis over LOD.

As the name suggests, a SWAG is a graph where nodes are analysis situations and edges are navigation steps that connect analysis situations.
An analysis situation can be seen as a high-level representation of a generic MD query.
A navigation step is a set of generic OLAP operations that, when applied to a source analysis situation, transform it into a target analysis situation.

# Implementation: SWAG-BI

For a demo of the prototype, refer to [demo page](demo.md).
We have employed semantic web analysis graphs as a foundation to develop a guided OLAP analysis tool for LOD sources.
Semantic web recommendations (RDF and RDFS) serve to represent and publish SWAGs.
The [SWAG vocabulary](https://github.com/swag-bi/swag/blob/master/src/main/webapp/WEB-INF/resources/AG.ttl) is used to represent and publish [concrete SWAGs](https://github.com/swag-bi/swag/blob/master/src/main/webapp/WEB-INF/resources/Uploaded/AGs/eurostat_AG_AMCIS2021.ttl).
[QB](https://raw.githubusercontent.com/UKGovLD/publishing-statistical-data/master/specs/src/main/vocab/cube.ttl) and [QB4OAP](https://github.com/lorenae/qb4olap/blob/master/rdf/qb4olap.ttl) vocabularies are employed as the underlying MD data model, and SPARQL serves as the underlying query language against LOD sources.
SWAG vocabulary refers to elements from QB4OLAP vocabulary and concrete SWAGs refer to elements from an MD schema represented using QB4OLAP vocabulary.
Other LOD sources, not represented using QB4OLAP, can also be employed within SWAG via, e.g., superimposition.
Furthermore, domain-specific business terms can be referenced within a SWAG.

We have implemented the SWAG-BI proof-of-concept prototype as a Java Dynamic Web Application.
HTML, JavaScript, jQuery, d3, and AJAX are employed for the frontend.
Dealing with web requests and responses is handled via the controller.
The SWAG Engine conducts the main logic of SWAG and manages the collaboration among the other modules.
Reading RDF data, sending SPARQL queries, and obtaining results are carried out by the Data Handler.
Generation of SPARQL queries is done by the SPARQL Generator.

The prototype depicts a particular SWAG using bird's eye view.
Whereas circles represent analysis situations, directed edges are navigation steps.
Details panel serves to display details of the selected analysis situation or navigation step.
User can provide values for the situation's variables supported by auto-completion.
As soon as the variables of an analysis situation are bound, it is possible to view its results, as well as selecting an outgoing navigation step to continue the analysis.
Once a navigation step is selected, user can bind its variables, and then navigate to the target situation and view its results.

# Usability Study
We employed the SWAG-BI prototype in a usability study which used the System Usability Scale.
First, a demonstration of the prototype was presented to participants, as one study administrator narrated from a [script](usability/script.pdf), while the other demonstrated on a PC connected to a projector.

Identical [tasks and questionnaire sheets](usability/questionnaire.pdf) were distributed to participants, which included an introduction, six tasks, and finally the SUS questionnaire.
In order to solve the tasks, participants had to obtain answers for an MD analysis by performing the binding of variables and navigation, so that the participants could interact with the system before they proceed to the SUS questionnaire.

Results from the [data](usability/data.csv) demonstrate that SWAG can be employed to construct intuitive user interfaces for guided MD analysis over LOD.