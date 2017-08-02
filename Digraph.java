import java.util.ArrayList;
import java.util.Scanner;
/**
 * 
 * This class is a blueprint for a Directed Graph that stores edges in an Adjacency Matrix and vertices in an array.
 * There are also subclasses for a city vertex, a road edge, and a priority queue (which is uses a MinHeap)
 * 
 * @author Daniel Le
 *
 */
class Digraph {
	/**
	 * Declare an adjacency matrix and an array to hold vertices.
	 * Also declare an integer which shows the size of the arrays.
	 */
	private int[][] roadEdges;
	private City[] cityVertices;
	private int numCities;
	/**
	 * Constructor
	 * @param nodeInfo is an array of Strings which holds city data
	 * @param edgeInfo is a String which holds road data
	 */
	public Digraph(ArrayList<String> nodeInfo, String edgeInfo){
		numCities = nodeInfo.size();
		roadEdges = new int[numCities][numCities];
		//initialize roadEdges to 0
		for(int i=0;i<numCities;i++){
			for(int j=0;j<numCities;j++){
				roadEdges[i][j] = 0;
			}
		}
		cityVertices = new City[numCities];
		//initialize cityVertices and their data
		for(int i=0;i<numCities;i++){
			cityVertices[i] = new City(nodeInfo.get(i));
		}
		//initialize roadEdges and their data
		Scanner kb = new Scanner(edgeInfo);
		while(kb.hasNext()){
			setRoad(kb.nextInt(),kb.nextInt(),kb.nextInt());
		}
		kb.close();
	}
	/**
	 * Allows the user to create a road edge by adding a number value into the adjacency matrix
	 * @param startCity is the beginning of the edge
	 * @param endCity is the endpoint of the edge
	 * @param distance is the weight of the edge
	 * @return true if the road is created
	 */
	public boolean setRoad(int startCity, int endCity, int distance){
		if(roadEdges[startCity-1][endCity-1] == 0){
			roadEdges[startCity-1][endCity-1] = distance;
			return true;
		}else{
			if(distance == 0)
				roadEdges[startCity-1][endCity-1] = distance;
			return false;
		}
	}
	/**
	 * Searches through the city array for a city using a city code
	 * @param code is the city code as a String
	 * @return the City matched
	 */
	public City getCity(String code){
		City city = null;
		for(int i=0;i<cityVertices.length;i++){
			if(cityVertices[i].getCode().equals(code)){
				city = cityVertices[i];
				break;
			}
			if(i==cityVertices.length-1){
				break;
			}
		}
		return city;
	}
	/**
	 * @return the dimension of the city array and edge matrix
	 */
	public int getNumCities(){
		return numCities;
	}
	/**
	 * This finds the shortest path from one city to another
	 * @param cityStart is the vertex to start from
	 * @param cityEnd is the vertex to end at
	 * @param path is a String which will hold city codes of vertices found on the path
	 * @return the total distance of the shortest path
	 */
	public int djikstra(City cityStart, City cityEnd, ArrayList<String> path){
		/**
		 * edges queue will hold neighbors for the algorithm
		 * pathEdges will hold the visited edges with adc
		 */
		int distance = 0;
		EdgePriorityQueue edges = new EdgePriorityQueue();
		ArrayList<Edge> pathEdges = new ArrayList<>();
		City backCity = cityStart;
		
		boolean pathFound = false;
		boolean searchForPath = true;
		/**
		 * main outer loop that will continuously run through the algorithm
		 */
		while(searchForPath){
			//add neighbors of backCity to queue
			for(int i=0;i<numCities;i++){
				if(roadEdges[backCity.getNumber()-1][i] != 0){
					edges.enqueue(new Edge(backCity,numberToCity(i+1),roadEdges[backCity.getNumber()-1][i]));
				}
			}
			
			//pop edge from queue and check for completion of path or set new backCity and reloop in outer loop
			Edge tempEdge;
			while(true){
				tempEdge = edges.dequeue();
				if(tempEdge.getFront() == cityEnd){
					pathEdges.add(tempEdge);
					pathFound = true;
					searchForPath = false;
					break;
				}else if(tempEdge.getWeight() == 0){
					searchForPath = false;
					break;
				}else{
					//check to see if end vertex of the edge has already been visited
					boolean alreadyVisited = false;
					for(int i=0;i<pathEdges.size();i++){
						if(tempEdge.getFront() == pathEdges.get(i).getBack()){
							alreadyVisited = true;
							break;
						}
					}
					//adds edge into the visited edges before relooping
					if(!alreadyVisited){
						pathEdges.add(tempEdge);
						backCity = tempEdge.getFront();
						break;
					}
				}
			}
		}
		//when a path is found, the shortest path is chosen and then distance is calculated.
		Edge tempEdge = pathEdges.get(pathEdges.size()-1);
		if(pathFound){
			distance+=pathEdges.get(pathEdges.size()-1).getWeight();
			path.add(cityToCode(pathEdges.get(pathEdges.size()-1).getFront()));
			for(int i=pathEdges.size()-2;i>=0;i--){
				if(pathEdges.get(i).getFront() == tempEdge.getBack()){
					int lowestWeight = pathEdges.get(i).getWeight();
					City tempCity = pathEdges.get(i).getFront();
					
					//check to see if any smaller road possible
					for(int j=i-1;j>=0;j--){
						if(pathEdges.get(j).getFront() == tempEdge.getBack() && pathEdges.get(j).getWeight() < lowestWeight){
							lowestWeight = pathEdges.get(j).getWeight();
							tempCity = pathEdges.get(j).getFront();
							i=j;
						}
					}
					distance+=lowestWeight;
					path.add(cityToCode(tempCity));
					tempEdge = pathEdges.get(i);
				}
			}
			path.add(cityStart.getCode());
		}		
		
		return distance;
	}
	/**
	 * Converter used to get a city code from a City
	 * @param city
	 * @return city code as String
	 */
	private String cityToCode(City city){
		String code = "";
		for(int i=0;i<numCities;i++){
			if(cityVertices[i] == city){
				code = cityVertices[i].getCode();
				break;
			}
		}
		return code;
	}
	/**
	 * Converter used to get a city from its number
	 * @param x is the city number
	 * @return city
	 */
	private City numberToCity(int x){
		City city = new City();
		for(int i=0;i<numCities;i++){
			if(cityVertices[i].getNumber() == x){
				city = cityVertices[i];
				break;
			}
		}
		return city;
	}
}
/**
 * This class is for creating a priority queue for edges
 * 
 */
class EdgePriorityQueue {
	private ArrayList<Edge> queue;
	public EdgePriorityQueue(){
		queue = new ArrayList<Edge>();
		//add placeholder edge to fill index 0
		queue.add(new Edge(new City(),new City(),0));
	}
	/**
	 * Adds an edge to the bottom or end of the queue
	 * @param e is an edge
	 */
	public void enqueue(Edge e){
		queue.add(e);
		int now = queue.size() - 1;
		//swap parent with child if parent is greater than child
		while(queue.get(now).getWeight() < queue.get(now/2).getWeight()){
			Edge temp = queue.get(now);
			queue.set(now, queue.get(now/2));
			queue.set(now/2, temp);
			now = now/2;
			if(now == 0)
				break;
		}
	}
	/**
	 * This function heaps everything from the index to the end of the array.
	 * @param index is the index to heap at
	 */
	private void reheap(int index){
		//left child exists
		if(index >= queue.size()/2&& index<= queue.size()){
			//parent greater than child
			if(queue.get(index).getWeight() > queue.get(index*2).getWeight() ||
					queue.get(index).getWeight() > queue.get(index*2 + 1).getWeight()){
				//left child less than right child
				if(queue.get(index*2).getWeight() < queue.get(index*2 + 1).getWeight()){
					Edge temp = queue.get(index);
					queue.set(index, queue.get(index*2));
					queue.set(index*2, temp);
					//reloop function for left child location
					reheap(index*2);
				}else{
					//right child less than left child
					Edge temp = queue.get(index);
					queue.set(index, queue.get(index*2 + 1));
					queue.set(index*2 + 1, temp);
					//reloop function for right child location
					reheap(index*2 + 1);
				}
			}
		}
	}
	/**
	 * Takes the minimum out of the Minimum Heap and returns it.
	 * @return minimum Edge
	 */
	public Edge dequeue(){
		Edge min = queue.get(1);
		queue.set(1, queue.remove(queue.size() - 1));
		reheap(1);
		return min;
	}
}
/**
 * This class defines an Edge class that has a weight and beginning and end vertices.
 *
 */
class Edge {
	private City backCity;
	private City frontCity;
	private int weight;
	public Edge(City b, City f, int w){
		backCity = b;
		frontCity = f;
		weight = w;
	}
	public City getBack(){ return backCity; }
	public City getFront(){ return frontCity; }
	public int getWeight(){ return weight; }
}
/**
 * This class defines a City which holds 5 variables of Strings.
 *
 */
class City {
	private String number;
	private String code;
	private String name;
	private String population;
	private String elevation;
	public City(){
		number="";
		code="";
		name="";
		population="";
		elevation="";
	}
	public City(String s){
		Scanner kb = new Scanner(s);
		number = kb.next();
		code = kb.next();
		name = kb.next();
		String tempStr = kb.next();
		if(tempStr.matches("\\d+")){
			population = tempStr;
		}else{
			name+=(" "+tempStr);
			population = kb.next();
		}
		elevation = kb.next();
		kb.close();
	}
	public int getNumber() { return Integer.parseInt(number); }
	public String getCode() { return code; }
	public String getName() { return name; }
	public String getPopulation() { return population; }
	public String getElevation() { return elevation; }
	public String getAllInfo() { return number+" "+code+" "+name+" "+population+" "+elevation; }
}