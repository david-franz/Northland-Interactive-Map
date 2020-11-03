public class SearchNode{
	
	private Node node;
	private SearchNode prev;
	private double costFromStart;
	private double estimatedTotalCost;
	
	public SearchNode(Node node, SearchNode prev, double costFromStart, double estimatedTotalCost) {
		this.node = node;
		this.prev = prev;
		this.costFromStart = costFromStart;
		this.estimatedTotalCost = estimatedTotalCost;
	}
	
	public Node getNode(){
		return node;
	}

	public void setNode(Node node){
		this.node = node;
	}

	public SearchNode getPrev(){
		return prev;
	}

	public void setPrev(SearchNode prev){
		this.prev = prev;
	}

	public double getCostFromStart(){
		return costFromStart;
	}

	public void setCostFromStart(double costFromStart){
		this.costFromStart = costFromStart;
	}

	public double getEstimatedTotalCost(){
		return estimatedTotalCost;
	}

	public void setEstimatedTotalCost(double estimatedTotalCost){
		this.estimatedTotalCost = estimatedTotalCost;
	}
	
	public String toString() {
		return "< " + node + ", " + prev + ", " + costFromStart + ", " + estimatedTotalCost + ">";
	}
}