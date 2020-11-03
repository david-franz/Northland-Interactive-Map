import java.util.*;

public class TrieNode{

	private List<Road> roads;
	private Map<Character, TrieNode> children;
	
	public TrieNode() {
		this.roads = new ArrayList<Road>();
		this.children = new HashMap<Character, TrieNode>();
	}
	
	public List<Road> getRoads(){
		return roads;
	}
	
	public Map<Character, TrieNode> getChildren(){
		return children;
	}
}
