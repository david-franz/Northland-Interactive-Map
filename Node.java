public class Node{
	
	private boolean isVisited;
	
	private double depth;	
	private int nodeID;
	private double latitude;
	private double longitude;
		
	public Node(int id, double lat, double lon) {
		this.nodeID = id;
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public void setDepth(double depth) {
		this.depth = depth;
	}
	
	public double getDepth() {
		return depth;
	}
	
	public boolean isVisited() {
		return isVisited;
	}
	
	public void setVisited() {
		isVisited = true;
	}
	
	public void resetVisited() {
		isVisited = false;
	}
	
	public int getNodeID() {
		return nodeID;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
		
	public String toString() {
		return Integer.toString(nodeID);
	}
}
