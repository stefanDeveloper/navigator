package dhbw.navigator.models;

import dhbw.navigator.generated.Osm;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Node {

	private String name;
	private ArrayList<Edge> edges = new ArrayList<>();
	private BigDecimal lon;
	private BigDecimal lat;

	//Autobahnkreuznummer ...
	private String ref;
	private long id;
	private boolean isJunction;
	private int junctions = 1;

	
	public Node(String name){
		this.name = name;
	}

	public Node(Osm.Node node)
	{
		//String name = null;
		//if(node.doesTagExist("name")) {
		//	name = (String)node.getTag("name");
		//}
		this.setName((String)node.getTag("name"));
		this.setLon(node.getLon());
		this.setLat(node.getLat());
		this.setId(node.getId());
		if(node.doesTagExist("ref"))
		{
			this.setRef((String)node.getTag("ref"));
			this.setIsJunction(true);
		}
	}

	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}
	
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	
	//Add edge
	public ArrayList<Edge> addEdge(Edge edge){
		edges.add(edge);
		return edges;
	}

	public BigDecimal getLon() {
		return lon;
	}

	public void setLon(BigDecimal lon) {
		this.lon = lon;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isSameNode(Node compareNode)
	{
		if(compareNode.getRef()==null || this.getRef()==null) return false;
		return (compareNode.getRef().equals(this.getRef()));
	};

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public boolean getIsJunction() {
		return isJunction;
	}

	public void setIsJunction(boolean junction) {
		isJunction = junction;
	}

	public int getJunctions() {
		return junctions;
	}

	public void setJunctions(int junctions) {
		this.junctions = junctions;
	}
}
