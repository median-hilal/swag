package swag.analysis_graphs.visualization;

public class Link {
	int source;
	int target;
	String type;
	
	public Link(int source, int target, String type) {
		super();
		this.source = source;
		this.target = target;
		this.type = type;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "\n{\n\"source\" : " + source + ", \"target\" : " + target + ", \"type\" : \"" + type + "\"\n}";
	}
}
