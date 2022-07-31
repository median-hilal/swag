package swag.analysis_graphs.visualization;

public class Node {

  int id;
  String name = "";
  String type = "";
  String group = "";
  String uri = "";


  public Node(int id, String name, String type, String group, String uri) {
    super();
    this.id = id;
    this.name = name;
    this.type = type;
    this.group = group;
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public String getGroup() {
    return group;
  }

  @Override
  public String toString() {
    return "\n{\n\"id\" : \"" + id + "\", \"name\" : \"" + name + "\", \"type\" : \"" + type
        + "\", \"group\" : \"" + group + "\", \"uri\" : \"" + uri + "\" \n}";
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o instanceof Node) {
      Node node = (Node) o;
      if (this.getGroup().equals(node.getGroup()) && this.getName().equals(node.getName())
          && this.getType().equals(node.getType()) && this.getUri().equals(node.getUri())) {
        return true;
      }
    }

    return false;
  }

}
