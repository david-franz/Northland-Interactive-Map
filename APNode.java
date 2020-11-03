public class APNode{
	
	private Node node;
	private int depth;
	private Node parent;
	
	public APNode(Node node, int depth, Node parent) {
		this.node = node;
		this.depth = depth;
		this.parent = parent;
	}

	public Node getNode(){
		return node;
	}

	public void setNode(Node node){
		this.node = node;
	}

	public int getDepth(){
		return depth;
	}

	public void setDepth(int depth){
		this.depth = depth;
	}

	public Node getParent(){
		return parent;
	}

	public void setParent(Node parent){
		this.parent = parent;
	}
	
	public String toString() {
		return "<" + node + ", " + depth + ", " + parent + ">";
	}
}
