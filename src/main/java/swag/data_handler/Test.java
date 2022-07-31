package swag.data_handler;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import openllet.owlapi.OpenlletReasonerFactory;

import org.apache.jena.rdf.model.*;

public class Test {	
				
	public static void main (String [] args) throws UnsupportedEncodingException, InterruptedException{
				
	    String OWLPath = "C:\\pizza.owl";
	    String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX pz: <http://www.co-ode.org/ontologies/pizza/pizza.owl#> SELECT DISTINCT ?x WHERE {     ?b owl:someValuesFrom pz:MozzarellaTopping. ?x rdfs:subClassOf ?b.      ?x rdfs:subClassOf* pz:Pizza.}";
	    for(int j=0; j<100;j++){
	    double starttime = System.currentTimeMillis();
	                InputStream in = FileManager.get().open(OWLPath);
	        if (in == null) {
	            throw new IllegalArgumentException("File: " + OWLPath + " not found");
	        }
	        
	        Model model = ModelFactory.createOntologyModel();	
			// opening input owl file				
			model.read(in, "");
			
	

	        org.apache.jena.query.Query query = QueryFactory.create(queryString);
	        QueryExecution qe = QueryExecutionFactory.create(query, model);
	        ResultSet results = qe.execSelect();

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();   
	        PrintStream ps = new PrintStream(baos);

	        double time1 = System.currentTimeMillis();
	        ResultSetRewindable r = ResultSetFactory.copyResults(results);
	        
	        /*List <String> vars = results.getResultVars();
			List <List <String>> finalResults = new ArrayList<List <String>>();
			finalResults.add(vars);
			List<String> resultsHeader = new ArrayList<String>();
			resultsHeader.add("#");
			resultsHeader.addAll(results.getResultVars());
			
			int rowsCntr = 0;
			for ( ; results.hasNext() ; ){
				
				List <String> varValues = new ArrayList<String>();
				QuerySolution soln = results.nextSolution();
				varValues.add("" + ++rowsCntr);
				for (String v : vars){						
					varValues.add((soln.get(v) !=null)? soln.get(v).toString() : null);
				}
				finalResults.add(varValues);					
			}
	        */
	        double time2 = System.currentTimeMillis();  

	        ResultSetFormatter.out(ps, r, query);	
	        String queryOutput = new String(baos.toByteArray(), "UTF-8");
	        String[] resultText = queryOutput.split("\n");  
	        for(int i=0; i<resultText.length;i++){
	            System.out.println(resultText[i]);
	        }
	    double endtime = System.currentTimeMillis();
	    System.out.println("Time: "+ (endtime-starttime) +"     Time for ResultSetFactory.copyResults(results): "+ (time2-time1));
	    }
	 
	}
}


