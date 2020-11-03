import java.util.*;

public class Trie{

	TrieNode root;
	
	public Trie() {
		root = new TrieNode();
	}
	
	/**
	 * Add to Trie
	 *  
	 * @param word
	 * @param road
	 */
	public void add(char[] word, Road road) {
		TrieNode node = root;
		
		for(char c : word){
			if(!node.getChildren().containsKey(c)){
		 		node.getChildren().put(c, new TrieNode()); //create a new child of node, connecting to node via c
			}
		 	node = node.getChildren().get(c); //move node to the child corresponding to c
		}
		node.getRoads().add(road); //add road into node.roads 
	}
	
	/**
	 * Get from Trie
	 * 
	 * @param word
	 * @return
	 */
	public List<Road> get(char[] word){
		TrieNode node = root;
		
		for(char c : word) {
			if(!node.getChildren().containsKey(c)) {
				return null;
			}
			node = node.getChildren().get(c);
		}
		
		return node.getRoads();
	}
	
	/**
	 * Get all from Trie- uses getAllFrom()
	 * 
	 * @param prefix
	 * @return
	 */
	public ArrayList<Road> getAll(char[] prefix){
		ArrayList<Road> results = new ArrayList<Road>();
		
		TrieNode node = root;
		
		for(char c : prefix) {
			if(!node.getChildren().keySet().contains(c)) {
				return null;
			}
			node = node.getChildren().get(c);
		}
		getAllFrom(node, results);
		
		return results;
	}
	
	/**
	 * 
	 * @param node
	 * @param results
	 */
	public void getAllFrom(TrieNode node, List<Road> results) {
		results.addAll(node.getRoads());
		
		for(TrieNode child : node.getChildren().values()) {
			getAllFrom(child, results);
		}
	}
}
