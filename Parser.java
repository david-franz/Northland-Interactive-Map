import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Parser{

	public static void readNodes(FileReader filereader) {
		try {
			BufferedReader reader = new BufferedReader(filereader);
			String line;
			
			while((line = reader.readLine()) != null) {
				String[] split = line.split("\t");
				
				int nodeID = Integer.parseInt(split[0]);
				double latitude = Double.parseDouble(split[1]);
				double longitude = Double.parseDouble(split[2]);
				
				Node node = new Node(nodeID, latitude, longitude);
				Graph.getGraph().getNodes().put(nodeID, node);
				
				Graph.getGraph().getAdjList().put(node, new HashSet<Segment>());
				Graph.getGraph().getAdjListNode().put(node, new HashSet<Node>());
			}
		}
		catch(Exception e) {System.err.println("Error " + e);}
	}
	
	public static void readRoads(FileReader filereader) {
		try {
			BufferedReader reader = new BufferedReader(filereader);
			String line = reader.readLine();
			
			while((line = reader.readLine()) != null) {
				String[] split = line.split("\t");
	
				int roadID = Integer.parseInt(split[0]);
				int type = Integer.parseInt(split[1]);
				String name = split[2];
				String city = split[3];
				boolean oneWay = (Integer.parseInt(split[4])==0)?false:true;
				int speed = Integer.parseInt(split[5]);
				int roadClass = Integer.parseInt(split[6]);
				boolean notForCar = (Integer.parseInt(split[7])==0)?false:true;
				boolean notForPeople = (Integer.parseInt(split[8])==0)?false:true;
				boolean notForBike = (Integer.parseInt(split[9])==0)?false:true;
				
				Road road = new Road(roadID, type, name, city, oneWay, speed, roadClass, notForCar, notForPeople, notForBike);
				Graph.getGraph().getRoads().put(roadID, road);
			}
		}
		catch(Exception e) {System.err.println("Error " + e);}
	}	
	
	public static void readSegments(FileReader filereader) {
		try {
			BufferedReader reader = new BufferedReader(filereader);
			String line = reader.readLine();
			
			while((line = reader.readLine()) != null) {
				String[] split = line.split("\t");

				int roadID = Integer.parseInt(split[0]);
				double length = Double.parseDouble(split[1]);
				int nodeID1 = Integer.parseInt(split[2]);
				int nodeID2 = Integer.parseInt(split[3]);
				
				ArrayList<Location> coordinates = new ArrayList<Location>();
				
				for(int index = 4; index < split.length; index += 2) {
					double lat = Double.parseDouble(split[index]);
					double lon = Double.parseDouble(split[index+1]);
					
					Location loc = new Location(lat, lon);
					coordinates.add(loc);
				}
				Segment segment = new Segment(roadID, length, nodeID1, nodeID2, coordinates);
				
				Graph.getGraph().getRoads().get(roadID).addSegment(segment);
				
				Graph.getGraph().getAdjList().get(Graph.getGraph().getNodes().get(nodeID1)).add(segment);
				Graph.getGraph().getAdjList().get(Graph.getGraph().getNodes().get(nodeID2)).add(segment);
				
				Graph.getGraph().getAdjListNode().get(Graph.getGraph().getNodes().get(nodeID1)).add(Graph.getGraph().getNodes().get(nodeID2));
				Graph.getGraph().getAdjListNode().get(Graph.getGraph().getNodes().get(nodeID2)).add(Graph.getGraph().getNodes().get(nodeID1));
			}			
		}
		catch(Exception e) {System.err.println("error " + e);}	
	} 
	
}
