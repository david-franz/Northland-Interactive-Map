import java.util.*;

public class Road{
	
	private int roadID;
	private int type;
	private String name;
	private String city;
	private boolean oneWay;
	private int speed;
	private int roadClass;
	private boolean notForCar;
	private boolean notForPeople;
	private boolean notForBike;
	
	private ArrayList<Segment> segments = new ArrayList<Segment>(); //all segments associated with this Road
		
	public Road(int roadID, int type, String name, String city, boolean oneWay, int speed, 
			int roadClass, boolean notForCar, boolean notForPeople, boolean notForBike) {
		this.roadID = roadID;
		this.type = type;
		this.name = name;
		this.city = city;
		this.oneWay = oneWay;
		this.speed = speed;
		this.roadClass = roadClass;
		this.notForCar = notForCar;
		this.notForPeople = notForPeople;
		this.notForBike = notForBike;	
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Segment> getSegments() {
		return segments;
	}
	
	public void addSegment(Segment segment) {
		segments.add(segment);
	}
	
	public String toString() {
		return segments.toString();
	}
}