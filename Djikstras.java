import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * 
 * This class utilizes a Directed Graph to store, access, and alter cities and roads.
 * It also has a function of finding the shortest path from one city to another using
 * Djikstra's Algorithm.
 * 
 * @author Daniel Le
 *
 */
public class Djikstras{
	/**
	 * Declaration of the Digraph and a Scanner to be used for input
	 */
	private static Scanner kb;
	private static Digraph cityGraph;
	/**
	 * The main function calls all other subfunctions as well as allows the user to choose from a menu.
	 * @param args
	 */
	public static void main(String[] args){
		createGraph();
		kb = new Scanner(System.in);
		boolean exit = false;
		String input;
		do{
			System.out.print("Command? ");
			input = kb.next();
			switch(input){
			case "q":
			case "Q":
				queryGraph();
				break;
			case "d":
			case "D":
				minimumDistance();
				break;
			case "i":
			case "I":
				insertRoad();
				break;
			case "r":
			case "R":
				removeRoad();
				break;
			case "h":
			case "H":
				displayMenu();
				break;
			case "e":
			case "E":
				System.out.println("Goodbye.");
				exit = true;
				break;
			default:
				System.out.println("Incorrect input.");
				break;
			}
		}while(!exit);
	}
	/**
	 * Finds the shortest path from one city vertex to another.
	 */
	private static void minimumDistance(){
		System.out.print("City Codes: ");
		//receive input and changes to uppercase in order to locate a city in the Digraph
		City city1 = cityGraph.getCity(kb.next().toUpperCase());
		City city2 = cityGraph.getCity(kb.next().toUpperCase());
		ArrayList<String> minPath = new ArrayList<>();
		//function calls djikstra function to find minimum distance as well as alters a String array to
		//	contain all the cities on the path
		int distance = cityGraph.djikstra(city1, city2, minPath);
		if(distance != 0){
			System.out.print("The minimum distance between "+city1.getName()+" and "+city2.getName()
					+" is "+distance+" through the route ");
			for(int i=minPath.size()-1;i>=0;i--){
				System.out.print(minPath.get(i));
				if(i!=0){
					System.out.print(" ");
				}else{
					System.out.print(".");
				}
			}
			System.out.println();
		}else{
			System.out.println("No path from "+city1.getName()+" to "+city2.getName()+".");
		}
	}
	/**
	 * Allows user to remove a road between 2 cities, and outputs an error message if incorrect cities entered,
	 * 	or if no road exists.
	 */
	private static void removeRoad(){
		System.out.print("City Codes: ");
		City city1 = cityGraph.getCity(kb.next().toUpperCase());
		City city2 = cityGraph.getCity(kb.next().toUpperCase());
		if(city1 != null && city2 != null){
			if(!cityGraph.setRoad(city1.getNumber(), city2.getNumber(), 0)){
				System.out.println("You have removed the road from "+city1.getName()
							+" to "+city2.getName()+".");
			}else{
				System.out.println("The road from "+city1.getName()+" to "+city2.getName()
							+" does not exist.");
			}
		}else{
			System.out.println("One of both of the cities do(es) not exist.");
		}
	}
	/**
	 * Allows the user to insert a road between 2 cities, shows an error message if a road already exists.
	 */
	private static void insertRoad(){
		System.out.print("City Codes and Distance: ");
		City city1 = cityGraph.getCity(kb.next().toUpperCase());
		City city2 = cityGraph.getCity(kb.next().toUpperCase());
		String distance = kb.next();
		if(city1 != null && city2 != null){
			if(distance.matches("\\d+")){//makes sure user enters integers for the city number
				if(cityGraph.setRoad(city1.getNumber(), city2.getNumber(), Integer.parseInt(distance))){
					System.out.println("You have inserted a road from "+city1.getName()
								+" to "+city2.getName()+" with a distance of "+distance+".");
				}else{
					System.out.println("A road already exists.");
				}
			}else{
				System.out.println("Not a valid distance.");
			}
		}else{
			System.out.println("One of both of the cities do(es) not exist.");
		}
	}
	/**
	 * Allows user to get all information about a city by entering in the city code
	 */
	private static void queryGraph(){
		System.out.print("City Code (ex. CA): ");
		String input = kb.next();
		City tempCity = cityGraph.getCity(input.toUpperCase());
		if(tempCity != null){
			System.out.println(tempCity.getAllInfo());
		}else{
			System.out.println("The city does not exist.");
		}
	}
	/**
	 * Creates the graph by taking in data from 2 files and sending them in an array and a String to Digraph constructor
	 */
	private static void createGraph(){
		ArrayList<String> cityInfo = new ArrayList<>();
		String roadInfo = "";
		try{
			//file location may change depending on program used
			kb = new Scanner(new File("city.dat"));
			while(kb.hasNextLine()){
				cityInfo.add(kb.nextLine());
				if(cityInfo.get(cityInfo.size()-1).matches("^[ ]*$")){
					cityInfo.remove(cityInfo.size()-1);
				}
			}
			//file location may change depending on program used
			kb = new Scanner(new File("road.dat"));
			while(kb.hasNextLine()){
				roadInfo += (kb.nextLine()+"\n");
			}
			kb.close();
		}catch(FileNotFoundException fne){
			fne.printStackTrace();
		}
		cityGraph = new Digraph(cityInfo, roadInfo);
	}
	/**
	 * Displays menu for the user to select which option
	 */
	private static void displayMenu(){
		System.out.println("  Q Query the city information by entering the city code.");
		System.out.println("  D Find the minimum distance between two cities.");
		System.out.println("  I insert aroad by entering two city codes and distance.");
		System.out.println("  R Remove an existing road by entering two city codes.");
		System.out.println("  H Display this message.");
		System.out.println("  E Exit.");
	}
}
