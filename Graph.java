import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class Graph extends GUI{
	
	private static Graph graph;
	private String dataSize;
	
	private HashMap<Integer, Node> nodes; //a map where nodeID is the key and Node object is the value
	private HashMap<Integer, Road> roads; //a map where roadID is the key and Road object is the value
	
	private HashMap<Node, HashSet<Segment>> adjList; //each node stores incoming node neighbours
	private HashMap<Node, HashSet<Node>> adjListNode; //each node stores outgoing node neighbours
	
	private HashSet<Road> highlightedRoads;	
	private HashSet<Segment> highlightedSegments;
	private HashSet<Node> selectedNodes;
	private Queue<Node> path;

	private HashSet<Node> articulationPoints;
	
	private Trie trie;
	
	private String printPath;
	
	private Location origin;
	private double scale;
	private int moveFactor;
	
	public static final double ZOOM_FACTOR = 1.1;
	
	public Graph() {
		adjList = new HashMap<Node, HashSet<Segment>>();
		adjListNode = new HashMap<Node, HashSet<Node>>();
		
		nodes = new HashMap<Integer, Node>();
		roads = new HashMap<Integer, Road>();
		
		highlightedRoads = new HashSet<Road>();
		highlightedSegments = new HashSet<Segment>();		
		selectedNodes = new HashSet<Node>();
		path = new ArrayDeque<Node>();
		
		printPath = null;
						
		getFrame().setSize(new Dimension(880, 695));	
	}
	
	@Override
	public void onLoad(File nodes, File roads, File segments, File polygons){
	    FileReader filereader = null;
		if(nodes != null) {
			try {
				filereader = new FileReader(nodes);
			} catch (FileNotFoundException e) {}
			Parser.readNodes(filereader);
		}
		
		if(roads != null) {
			try {
				filereader = new FileReader(roads);
			} catch (FileNotFoundException e) {}
			Parser.readRoads(filereader);
		}
		
		if(segments != null) {
			try {
				filereader = new FileReader(segments);
			} catch (FileNotFoundException e) {}
			Parser.readSegments(filereader);
		}
		
		try {
			dataSize = (nodes.getCanonicalPath().contains("large"))? "large" : "small";
		} catch (IOException e) {System.err.println("error " + e);}

		scale = dataSize.equals("small")?90:3;
		moveFactor = (int)(1*(90/scale))+1;
		
		origin = findOrigin();
		
		trie = new Trie();
		buildTrie();		
		findArticulationPoints();
		
		redraw();
	}
	
	@Override
	protected void redraw(Graphics g) {		
		if(nodes.isEmpty() || roads.isEmpty()) {
			return;
		}
		
		//highlight articulation points
		if(displayAP) {
			for(Node node : articulationPoints) {
				Location loc = new Location(node.getLatitude(), node.getLongitude());			
				Point p = Location.newFromLatLon(loc.x, loc.y).asPoint(origin, scale);
				g.setColor(Color.RED);
				g.drawOval(p.x-2, p.y-2, 4, 4);
			}
		}
		
		//highlight selected nodes
		for(Node selectedNode : selectedNodes) {
			if(selectedNode != null) {
				Location loc = new Location(selectedNode.getLatitude(), selectedNode.getLongitude());			
				Point p = Location.newFromLatLon(loc.x, loc.y).asPoint(origin, scale);
				g.setColor(Color.BLUE);
				if(p != null)
					g.drawOval(p.x-2, p.y-2, 4, 4);
			}
		}
				
		//drawing points between nodes
		for(Road road : roads.values()) {
			ArrayList<Segment> segments = road.getSegments();
			for(Segment segment : segments) {
				ArrayList<Location> location = segment.getCoordinates();
				for(int i = 0; i < location.size()-1; i++) {
					//reading the coordinates of the roads and generating pixel point locations
					Location loc1 = location.get(i);
					Location loc2 = location.get(i+1);				
					Point p1 = Location.newFromLatLon(loc1.x, loc1.y).asPoint(origin, scale);
					Point p2 = Location.newFromLatLon(loc2.x, loc2.y).asPoint(origin, scale);
					
					g.setColor(highlightedRoads.contains(road)? Color.RED : 
						highlightedSegments.contains(segment)? Color.BLUE : Color.BLACK);
					g.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}
		}
	}
	
	@Override
	protected void onSearch(){
		selectedNodes = new HashSet<Node>();
		highlightedRoads = new HashSet<Road>();
		getTextOutputArea().setText(null); //JTextArea
		
		char[] search = getSearchBox().getText().toLowerCase().toCharArray();
		
		if(search.length == 0) { 
			getTextOutputArea().append(printPath);
			return;
		}
		
		List<Road> roads = new ArrayList<Road>();		
		roads = trie.get(search);
		
		if(roads != null && roads.isEmpty())
			roads = trie.getAll(search);
		if(roads != null)
			highlightedRoads.addAll(roads);
		
		HashSet<String> roadNames = new HashSet<String>();
		for(Road road : highlightedRoads)
			roadNames.add(road.getName());
		
		for(String name : roadNames)
			getTextOutputArea().append(name + "\n");
	}

	@Override
	protected void onMove(Move direction){
		Dimension d = getDrawingAreaDimension();
		
		double width = d.width / scale;
		double height = d.height / scale;
		int dx = (int)((width - (width / ZOOM_FACTOR)) / 2);
		int dy = (int)((height - (height / ZOOM_FACTOR)) / 2);
		
		switch(direction) {
			case NORTH:
				origin = origin.moveBy(0, moveFactor);
				break;
			case SOUTH:
				origin = origin.moveBy(0, -moveFactor);
				break;
			case EAST:
				origin = origin.moveBy(moveFactor, 0);
				break;
			case WEST:
				origin = origin.moveBy(-moveFactor, 0);
				break;
			case ZOOM_IN:		
				origin = origin.moveBy(dx, dy);
				scale *= ZOOM_FACTOR;
				break;
			case ZOOM_OUT:
				origin = origin.moveBy(-dx, -dy);
				scale /= ZOOM_FACTOR;
				break;
		}
		
		moveFactor = (int)(1*(90/scale))+1;
	}
	
	@Override
	protected void onClick(MouseEvent e){
		if(selectedNodes.size() > 1) selectedNodes = new HashSet<Node>();
		highlightedSegments = new HashSet<Segment>();
		path = new ArrayDeque<Node>();
		printPath = null;
		getTextOutputArea().setText(null);
		
		int weight = 4;
		boolean wasCloseTo = false;
		for(Node node : nodes.values()) {
			Point p = Location.newFromLatLon(node.getLatitude(), node.getLongitude()).asPoint(origin, scale);
			if(isCloseTo(p.x, e.getX(), weight) && isCloseTo(p.y, e.getY(), weight)) {
				selectedNodes.add(node);
				wasCloseTo = true;
				break;
			}	
		}
		if(!wasCloseTo) selectedNodes = new HashSet<Node>();
		if(selectedNodes.size() > 1)  
			findPath();
	}
	 
	/**
	 * Returns true if the 2 parameters are within @param weight numbers from each other
	 * 
	 * @param num1
	 * @param num2
	 * @param weight
	 * @return
	 */
	public boolean isCloseTo(int num1, int num2, int weight) {
		return Math.abs(num1 - num2) < weight;
	}
	
	/**
	 * 
	 */
	public void findPath() {
		highlightedSegments = new HashSet<Segment>();
		
		if(selectedNodes.size() < 2) return;
		
		Node[] searchNodes = new Node[selectedNodes.size()];
		int index = 0;
		for(Node node : selectedNodes) {
			searchNodes[index] = node;
			index++;
		}
		
		double length = findPath(searchNodes[0], searchNodes[1]);
		printPath = makePrintPath(length);
		getTextOutputArea().setText(null);
		getTextOutputArea().append(printPath);
	}
	
	/**
	 * 
	 * @param start
	 * @param goal
	 * @return
	 */
	public double findPath(Node start, Node goal) {
		
		if(start == null || goal == null) return Double.NaN;
				
		//reset all nodes to be unvisited
		for(Node node : adjList.keySet())
			node.resetVisited();
		
		Queue<SearchNode> fringe = new PriorityQueue<SearchNode>(adjList.size(), 
				(a, b) -> Double.compare(a.getEstimatedTotalCost(), b.getEstimatedTotalCost()));
		
		fringe.add(new SearchNode(start, null, 0, heuristic(start, goal))); //adding start node to fringe
		SearchNode endNode = null;
				
		while(!fringe.isEmpty()) {
			SearchNode searchNode = fringe.poll();
			Node node = searchNode.getNode();
			
			if(!node.isVisited()) {
				node.setVisited();
				
				if(node.equals(goal)) {
					endNode = searchNode;
					break;
				}
				
				for(Segment segment : adjList.get(searchNode.getNode())) {
					int otherNodeID = segment.getOtherNodeID(node.getNodeID());
					Node neighbour = nodes.get(otherNodeID);
					
					if(!neighbour.isVisited()) {
						double newCost;
						double newEstimatedCost;
						if(isDistance) {
							newCost = searchNode.getCostFromStart() + segment.getLength();
							newEstimatedCost = newCost + heuristic(neighbour, goal);
						}
						else {
							Road road = null;
							for(Road r : roads.values()) {
								if(r.getSegments().contains(segment)) {
									road = r;
									break;
								}
							}
														
							newCost = searchNode.getCostFromStart() + (segment.getLength() / road.getSpeed());
							newEstimatedCost = newCost + heuristic(neighbour, goal);
						}
						fringe.offer(new SearchNode(neighbour, searchNode, newCost, newEstimatedCost));
					}
				}
			}
		}
		
		if(endNode == null) return Double.NaN;
		
		path.add(goal);
		SearchNode searchNode = endNode;
		
		while(!searchNode.getNode().equals(start)) {
			searchNode = searchNode.getPrev();
			path.offer(searchNode.getNode());
		}
		
		return endNode.getCostFromStart();
	}
	
	/**
	 * 
	 * @param node
	 * @param goal
	 * @return
	 */
	public double heuristic(Node node, Node goal) {
		Location nodeLocation = Location.newFromLatLon(node.getLatitude(), node.getLongitude());
		Location goalLocation = Location.newFromLatLon(goal.getLatitude(), goal.getLongitude());
		if(isDistance) return nodeLocation.distance(goalLocation); //heuristic function for distance
		return nodeLocation.distance(goalLocation) / 100; //heuristic function for time
	}
	
	/**
	 * 
	 * @param l
	 * @return
	 */
	public String makePrintPath(double l) {
		StringBuilder sb = new StringBuilder();		
		String length = String.format("%.2f", l);
		sb.append(((isDistance)? "Distance: " : "Time: ") + length + "\n");
		Stack<Road> pathRoads = processPath();
		int count = 1;
		for(Road road : pathRoads) {
			sb.append(road.getName());
			if(count < pathRoads.size())
				sb.append("\u2192" + " ");
			count++;
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public Stack<Road> processPath() {
		Queue<Segment> pathSegments = new ArrayDeque<Segment>();
		while(!path.isEmpty()) {
			Node a = path.poll();
			Node b = path.peek();
			if(b == null) break;
			for(Segment segment : adjList.get(a)) {
				if(segment.getOtherNodeID(a.getNodeID()) == b.getNodeID()) {
					pathSegments.offer(segment);
					break;
				}
			}
		}
		
		for(Segment segment : pathSegments)
			highlightedSegments.add(segment);
		
		Stack<Road> pathRoadsDuplicates = new Stack<Road>();
		for(Segment segment : pathSegments) {
			for(Road road : roads.values()) {
				if(road.getSegments().contains(segment))
					pathRoadsDuplicates.push(road);
			}
		}
		
		Stack<Road> pathRoads = new Stack<Road>();	
		while(!pathRoadsDuplicates.isEmpty()) {
			Road a = pathRoadsDuplicates.pop();
			if(pathRoadsDuplicates.isEmpty()) break;
			Road b = pathRoadsDuplicates.peek();
			if(!a.getName().equals(b.getName()))
				pathRoads.push(b);
		}
		
		return pathRoads;
	}
	
	/**
	 * 
	 * @return
	 */
	public Location findOrigin() {
		ArrayList<Node> nodeList = new ArrayList<Node>(nodes.values());
		
		Collections.sort(nodeList, (a,b) -> Double.compare(b.getLatitude(), a.getLatitude()));
		double lat = nodeList.get(0).getLatitude();
		
		Collections.sort(nodeList, (a,b) -> Double.compare(a.getLongitude(), b.getLongitude()));
		double lon = nodeList.get(0).getLongitude();
				
		return Location.newFromLatLon(lat, lon);
	}
	
	/**
	 * 
	 */
	public void buildTrie() {
		for(Road road : roads.values())
			trie.add(road.getName().toCharArray(), road);
	}
	
	/**
	 * 
	 */
	public void findArticulationPoints() {
		if(dataSize.equals("large")) return;
		
		for(Node node : adjList.keySet())
			node.setDepth(Double.POSITIVE_INFINITY);	
		
		articulationPoints = new HashSet<Node>();

		Node root = nodes.get(Collections.min(nodes.keySet()));
		root.setDepth(0);
		
		int numSubTrees = 0;
		
		for(Node neighbour : adjListNode.get(root)) {
			if(neighbour.getDepth() == Double.POSITIVE_INFINITY) {
				recArtPts(neighbour, 1, root); 
				numSubTrees++;
			}
		
			if(numSubTrees > 1) articulationPoints.add(root);		
		}		
	}
	
	/**
	 * 
	 * @param node
	 * @param depth
	 * @param parent
	 * @return
	 */
	public double recArtPts(Node node, double depth, Node parent) {
		node.setDepth(depth);
		double reachBack = depth;
		
		for(Node neighbour : adjListNode.get(node)) {
			if(neighbour.getDepth() < Double.POSITIVE_INFINITY)
				reachBack = Math.min(neighbour.getDepth(), reachBack);
			
			else {
				double childReach = recArtPts(neighbour, depth+1, node);
				reachBack = Math.min(childReach, reachBack);
				
				if(childReach >= depth) articulationPoints.add(node);
			}
		}
		return reachBack;
	}
	
	public HashMap<Integer, Node> getNodes(){
		return nodes;
	}
	
	public HashMap<Integer, Road> getRoads(){
		return roads;
	}
	
	public HashMap<Node, HashSet<Segment>> getAdjList(){
		return adjList;
	}
	
	public HashMap<Node, HashSet<Node>> getAdjListNode(){
		return adjListNode;
	}
	
	public static Graph getGraph() {
		return graph;
	}
	
	public static void main(String[] args) {
		graph = new Graph();
	}
}