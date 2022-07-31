package swag.data_handler;

import org.apache.jena.ontology.OntModelSpec;

public class OWLConnectionFactory {

	public static OWlConnection createOWLConnectionWithoutReasoning() {
		OWlConnection conn = new OWlConnection(OntModelSpec.OWL_MEM, OntModelSpec.OWL_MEM);
		conn.createOntologyModel();
		return conn;
	}

	public static boolean appendQB(OWlConnection conn, String path, String name, boolean isLocal) {
		OWlConnection tempConn = new OWlConnection();
		tempConn.createOntologyModel();

		if (isLocal) {
			tempConn.readOwlFromFile(path, name);
			String qb = Constants.QB_NS;
			conn.addToNamespaces(Constants.QB, qb);
			conn.readOwlFromFile(path, name);
		} else {
			conn.readOwlFromURI(Constants.qb_link);
			String qb = Constants.QB_NS;
			conn.addToNamespaces(Constants.QB, qb);
			conn.readOwlFromURI(Constants.qb_link);
		}
		return true;
	}

	public static boolean appendQB4O(OWlConnection conn, String path, String name, boolean isLocal) {
		OWlConnection tempConn = new OWlConnection();

		if (isLocal) {
			tempConn.createOntologyModel();
			tempConn.readOwlFromFile(path, name);
			String qb4o = Constants.QB4O_NS;
			conn.addToNamespaces(Constants.QB4O, qb4o);
			conn.readOwlFromFile(path, name);
		} else {
			tempConn.createOntologyModel();
			conn.readOwlFromURI(Constants.qb4o_link);
			String qb4o = Constants.QB4O_NS;
			conn.addToNamespaces(Constants.QB4O, qb4o);
			conn.readOwlFromURI(Constants.qb4o_link);
		}
		return true;
	}

	public static boolean appendSMD(OWlConnection conn, String path, String name) {
		OWlConnection tempConn = new OWlConnection();
		tempConn.createOntologyModel();
		tempConn.readOwlFromFile(path, name);
		String smd = tempConn.getModel().getNsPrefixURI("");
		if (smd == null || smd.equals("")) {
			smd = Constants.SMD_NS;
		}
		conn.addToNamespaces(Constants.SMD, smd);
		conn.readOwlFromFile(path, name);
		return true;
	}

	public static boolean appendAG(OWlConnection conn, String path, String name) {
		if (conn.checkIfInNameSpaces(Constants.SMD)) {
			OWlConnection tempConn = new OWlConnection();
			tempConn.createOntologyModel();
			tempConn.readOwlFromFile(path, name);
			String ag = tempConn.getModel().getNsPrefixURI("");
			if (ag == null || ag.equals("")) {
				ag = Constants.AG_NS;
			}
			conn.addToNamespaces(Constants.AG, ag);
			conn.readOwlFromFile(path, name);
			return true;
		} else {
			return false;
		}
	}

	public static boolean appendPredicates(OWlConnection conn, String path, String name) {

		OWlConnection tempConn = new OWlConnection();
		tempConn.createOntologyModel();
		tempConn.readOwlFromFile(path, name);
		conn.readOwlFromFile(path, name);
		return true;
	}

	public static boolean appendSMDIns(OWlConnection conn, String path, String name, boolean isLocal) {
		if (conn.checkIfInNameSpaces(Constants.AG) && conn.checkIfInNameSpaces(Constants.SMD)) {

			OWlConnection tempConn = new OWlConnection();
			tempConn.createOntologyModel();

			if (isLocal) {
				tempConn.readOwlFromFile(path, name);
				String smd_ins = tempConn.getModel().getNsPrefixURI("");
				conn.addToNamespaces(Constants.SMD_INS, smd_ins);
				conn.readOwlFromFile(path, name);
			} else {
				conn.readOwlFromURI(name);
				String smd_ins = tempConn.getModel().getNsPrefixURI("");
				conn.addToNamespaces(Constants.SMD_INS, smd_ins);
				conn.readOwlFromURI(name);
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean appendAGIns(OWlConnection conn, String path, String name) {
		if (conn.checkIfInNameSpaces(Constants.AG) && conn.checkIfInNameSpaces(Constants.SMD)
				&& conn.checkIfInNameSpaces(Constants.SMD_INS)) {

			OWlConnection tempConn = new OWlConnection();
			tempConn.createOntologyModel();
			tempConn.readOwlFromFile(path, name);
			String ag_ins = tempConn.getModel().getNsPrefixURI("");
			conn.addToNamespaces(Constants.AG_INS, ag_ins);
			conn.readOwlFromFile(path, name);
			return true;
		} else {
			return false;
		}
	}

	public static String getQB4ONamespace(OWlConnection conn) {
		return conn.getNamespacebyKey(Constants.QB4O);
	}

	public static String getSMDNamespace(OWlConnection conn) {
		return conn.getNamespacebyKey(Constants.SMD);
	}

	public static String getAGNamespace(OWlConnection conn) {
		return conn.getNamespacebyKey(Constants.AG);
	}

	public static String getSMDInstanceNamespace(OWlConnection conn) {
		return conn.getNamespacebyKey(Constants.SMD_INS);
	}

	public static String getAGInstanceNamespace(OWlConnection conn) {
		return conn.getNamespacebyKey(Constants.AG_INS);
	}
}
