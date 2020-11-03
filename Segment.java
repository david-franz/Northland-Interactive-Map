import java.util.*;

public class Segment{

	private int roadID;
	private double length;
	private int nodeID1;
	private int nodeID2;
	
	private ArrayList<Location> coordinates; //coordinates that are points of this Segment
	
	public Segment(int roadID, double length, int nodeID1, int nodeID2, ArrayList<Location> coordinates) {
		this.roadID = roadID;
		this.length = length;
		this.nodeID1 = nodeID1;
		this.nodeID2 = nodeID2;
		this.coordinates = coordinates;
	}
	
	public double getLength() {
		return length;
	}
	
	public ArrayList<Location> getCoordinates(){
		return coordinates;
	}
	
	public int getNodeID2() {
		return nodeID2;
	}
	
	public int getNodeID1() {
		return nodeID1;
	}
	
	public int getOtherNodeID(int nodeID) {
		if(nodeID == nodeID1) return nodeID2;
		return nodeID1;
	}
	
	public String toString() {
		String start = roadID + " " + length + " " + nodeID1 + " " + nodeID2;
		String end = "";
		for(int i = 0; i < coordinates.size(); i++) {
			end = end + " " + coordinates.get(i);
		}
		return start + end;
	}
}