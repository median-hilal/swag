package swag.analysis_graphs.visualization;

import java.util.ArrayList;
import java.util.List;

import swag.md_elements.HasDescriptor;
import swag.md_elements.HasLevel;
import swag.md_elements.HasMeasure;
import swag.md_elements.InDimension;
import swag.md_elements.MDRelation;
import swag.md_elements.MDSchema;
import swag.md_elements.QB4OHierarchyStep;

public class MDSchemaJSONizer {

  public static String jsonizeMDSchema(MDSchema schema, StringBuilder sbNodes,
      StringBuilder sbLinks) {


    List<Node> nodes = new java.util.ArrayList<>();
    List<Link> links = new ArrayList<>();

    int linkCounter = -1;
    int nodesCounter = -1;

    int sourceNodeID = -1;
    int targetNodeID = -1;

    for (MDRelation rel : schema.getAllEdges()) {


      if (rel instanceof HasLevel) {


        Node fromNode =
            new Node(0, rel.getFrom().getName(), "fact", "1", rel.getFrom().getIdentifyingName());
        Node correspondingNodeInList1 = getNodeByNonIDAttributes(nodes, fromNode);

        // Node doesn't already exist
        if (correspondingNodeInList1 == null) {
          sourceNodeID = ++nodesCounter;
          fromNode.setId(sourceNodeID);
          nodes.add(fromNode);
        } else {
          sourceNodeID = correspondingNodeInList1.getId();
        }

        Node toNode = new Node(0, rel.getTo().getName(), "level",
            schema.getDimensionOfLevel(rel.getTo().getIdentifyingName()).getName(),
            rel.getTo().getIdentifyingName());
        Node correspondingNodeInList2 = getNodeByNonIDAttributes(nodes, toNode);

        // Node doesn't already exist
        if (correspondingNodeInList2 == null) {
          targetNodeID = ++nodesCounter;
          toNode.setId(targetNodeID);
          nodes.add(toNode);
        } else {
          targetNodeID = correspondingNodeInList2.getId();
        }

        links.add(new Link(sourceNodeID, targetNodeID, "hasLevel"));
        continue;
      }

      if (rel instanceof HasDescriptor) {


        Node fromNode = new Node(0, rel.getFrom().getName(), "level",
            schema.getDimensionOfLevel(rel.getFrom().getIdentifyingName()).getName(),
            rel.getFrom().getIdentifyingName());
        Node correspondingNodeInList1 = getNodeByNonIDAttributes(nodes, fromNode);

        // Node doesn't already exist
        if (correspondingNodeInList1 == null) {
          sourceNodeID = ++nodesCounter;
          fromNode.setId(sourceNodeID);
          nodes.add(fromNode);
        } else {
          sourceNodeID = correspondingNodeInList1.getId();
        }

        Node toNode =
            new Node(0, rel.getTo().getName(), "descriptor", "1", rel.getTo().getIdentifyingName());
        Node correspondingNodeInList2 = getNodeByNonIDAttributes(nodes, toNode);

        // Node doesn't already exist
        if (correspondingNodeInList2 == null) {
          targetNodeID = ++nodesCounter;
          toNode.setId(targetNodeID);
          nodes.add(toNode);
        } else {
          targetNodeID = correspondingNodeInList2.getId();
        }

        links.add(new Link(sourceNodeID, targetNodeID, "hasDescriptor"));
        continue;
      }

      if (rel instanceof HasMeasure) {


        Node fromNode =
            new Node(0, rel.getFrom().getName(), "fact", "1", rel.getFrom().getIdentifyingName());
        Node correspondingNodeInList1 = getNodeByNonIDAttributes(nodes, fromNode);

        // Node doesn't already exist
        if (correspondingNodeInList1 == null) {
          sourceNodeID = ++nodesCounter;
          fromNode.setId(sourceNodeID);
          nodes.add(fromNode);
        } else {
          sourceNodeID = correspondingNodeInList1.getId();
        }

        Node toNode =
            new Node(0, rel.getTo().getName(), "measure", "1", rel.getTo().getIdentifyingName());
        Node correspondingNodeInList2 = getNodeByNonIDAttributes(nodes, toNode);

        // Node doesn't already exist
        if (correspondingNodeInList2 == null) {
          targetNodeID = ++nodesCounter;
          toNode.setId(targetNodeID);
          nodes.add(toNode);
        } else {
          targetNodeID = correspondingNodeInList2.getId();
        }

        links.add(new Link(sourceNodeID, targetNodeID, "hasMeasure"));
        continue;
      }

      if (rel instanceof InDimension) {


        Node fromNode = new Node(0, rel.getFrom().getName(), "level",
            schema.getDimensionOfLevel(rel.getFrom().getIdentifyingName()).getName(),
            rel.getFrom().getIdentifyingName());
        Node correspondingNodeInList1 = getNodeByNonIDAttributes(nodes, fromNode);

        // Node doesn't already exist
        if (correspondingNodeInList1 == null) {
          sourceNodeID = ++nodesCounter;
          fromNode.setId(sourceNodeID);
          nodes.add(fromNode);
        } else {
          sourceNodeID = correspondingNodeInList1.getId();
        }

        Node toNode =
            new Node(0, rel.getTo().getName(), "dimension", "1", rel.getTo().getIdentifyingName());
        Node correspondingNodeInList2 = getNodeByNonIDAttributes(nodes, toNode);

        // Node doesn't already exist
        if (correspondingNodeInList2 == null) {
          targetNodeID = ++nodesCounter;
          toNode.setId(targetNodeID);
          nodes.add(toNode);
        } else {
          targetNodeID = correspondingNodeInList2.getId();
        }

        links.add(new Link(sourceNodeID, targetNodeID, "belongsToDimension"));
        continue;
      }


      if (rel instanceof QB4OHierarchyStep) {


        Node fromNode = new Node(0, rel.getFrom().getName(), "level",
            schema.getDimensionOfLevel(rel.getFrom().getIdentifyingName()).getName(),
            rel.getFrom().getIdentifyingName());
        Node correspondingNodeInList1 = getNodeByNonIDAttributes(nodes, fromNode);

        // Node doesn't already exist
        if (correspondingNodeInList1 == null) {
          sourceNodeID = ++nodesCounter;
          fromNode.setId(sourceNodeID);
          nodes.add(fromNode);
        } else {
          sourceNodeID = correspondingNodeInList1.getId();
        }

        Node toNode = new Node(0, rel.getTo().getName(), "level",
            schema.getDimensionOfLevel(rel.getTo().getIdentifyingName()).getName(),
            rel.getTo().getIdentifyingName());
        Node correspondingNodeInList2 = getNodeByNonIDAttributes(nodes, toNode);

        // Node doesn't already exist
        if (correspondingNodeInList2 == null) {
          targetNodeID = ++nodesCounter;
          toNode.setId(targetNodeID);
          nodes.add(toNode);
        } else {
          targetNodeID = correspondingNodeInList2.getId();
        }

        links.add(new Link(sourceNodeID, targetNodeID, "rollsUpTo"));
        continue;
      }
    }

    sbNodes.append(nodes.toString().replace("'", "\\'").replace("\n", "").replace("\r", ""));
    sbLinks.append(links.toString().replace("'", "\\'").replace("\n", "").replace("\r", ""));

    // System.out.println("nodes" + nodes);
    // System.out.println("links" + links);
    return "" + nodes + "- end of nodes -" + links;
  }

  public static Node getNodeByNonIDAttributes(List<Node> nodesList, Node nodeToGet) {

    for (Node node : nodesList) {
      if (node.getGroup().equals(nodeToGet.getGroup()) && node.getName().equals(nodeToGet.getName())
          && node.getType().equals(nodeToGet.getType())
          && node.getUri().equals(nodeToGet.getUri())) {
        return node;
      }
    }
    return null;
  }
}
