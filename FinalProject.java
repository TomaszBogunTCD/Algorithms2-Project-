import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;


import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;

class Pair implements Comparable<Pair> {
  String tripId;
  String stops;

  public Pair(String tpid) {
    tripId = tpid;
    stops = "";
  }

  public Pair(String tpid, String stps) {
    tripId = tpid;
    stops = stps;
  }

  @Override
  public int compareTo(Pair o) {
    return this.tripId.compareTo(o.tripId);
  }
}

class Edge{
	double weight;
	int src;
	int des;
	public Edge(int src, int des, double weight){
		this.src = src;
		this.des = des;
		this.weight = weight;
	}
}

class Graph{
	public static int V;
	public static int E;
	public static List<List<Node>> adjList = new ArrayList<List<Node>>(); 
	
	Graph(int V, int E){
		Graph.V = V;
		Graph.E = E;
		for(int i=0; i<Graph.V; i++) {
			adjList.add(new ArrayList<Node>());
		}
	}
	
	public static class Node implements Comparator<Node>{
		int index;
		double weight;
		Node(){}
		Node(int index, double weight){
			this.index = index;
			this.weight = weight;
		}
		public int compare(Node o1, Node o2) {
			if(o1.weight < o2.weight) {
				return -1;
			}else if(o1.weight > o2.weight) {
				return 1;
			}
			return 0;
		}
	}

	
	void addNode(int src, int des, double weight) {
		Node newNode = new Node(des, weight);
		adjList.get(src).add(newNode);
	}
	
	static String[] djikstrasAlgorithm(int src, int des) {
		boolean nodesAdded[] = new boolean[V];
		Arrays.fill(nodesAdded, false);
		nodesAdded[src] = true;
		double distTo[] = new double[V];
		Arrays.fill(distTo, Double.POSITIVE_INFINITY);
		distTo[src] = 0;
		int prev[] = new int[V];
		prev[src] = src;
		Arrays.fill(prev, -1);
		PriorityQueue<Node> nodeQueue = new PriorityQueue<Node>(V, new Node());		
		nodeQueue.add(new Node(src, 0));
		while(nodeQueue.size() > 0) {
			Node currentNode = nodeQueue.poll();
			for(int i=0; i<adjList.get(currentNode.index).size(); i++) {
				Node newNode = adjList.get(currentNode.index).get(i);
				if(!nodesAdded[newNode.index]) {
					if(distTo[currentNode.index] + newNode.weight < distTo[newNode.index]) {
						distTo[newNode.index] = distTo[currentNode.index] + newNode.weight;
						prev[newNode.index] = currentNode.index;
					}
					nodesAdded[newNode.index] = true;
					nodeQueue.add(new Node(newNode.index, distTo[newNode.index]));
				}
			}
		}
		int numberOfStopsInBetween = 0;
		for(int stopID=des; stopID!=src; stopID=prev[stopID]) {
			numberOfStopsInBetween++;
		}
		String[] stops = new String[numberOfStopsInBetween+3];
		int index = 3;
		for(int stopID=des; stopID!=src; stopID=prev[stopID]) {
			stops[index] = prev[stopID] +  " -> " + stopID + "	cost: " + (distTo[stopID]-distTo[prev[stopID]]);
			index++;
		}
		stops[0] = "----------------------------------";
		stops[1] = des + " -> " + src + "	Total Cost: " + distTo[des];
		stops[2] = "---------------------------------";
		String[] stopsReversed = new String[numberOfStopsInBetween+3];
		for(int i=0; i<stopsReversed.length; i++) {
			stopsReversed[stopsReversed.length-1-i] = stops[i];
		}
		stops = stopsReversed;
		return stops;
	}
	
}


public class FinalProject {
  public static final String pathToStopTimes = "src/stop_times.txt";

  /*
   * Return string array containing one string element "1" if stop1 doesn't exist
   * Return string array containing one string element "2" if stop2 doesn't exist
   * Return empty string array if no path between exists between stop1 and stop2
   * Otherwise, return a string array of the details of all stops traversed by the shortest path, with its cost by each stop
   * Look at project specification for details
   * */
  public static String[] getShortestRoute(String stop1, String stop2) throws IOException {
		int largestStopID = 0;
			//checks if inputed stops exist and counts number of stops from stops file
			BufferedReader stopsReaderPresent = new BufferedReader(new FileReader("src/stops.txt"));
			int lineIndexStops = 0;
			Boolean stop1Found = false; 
			Boolean stop2Found = false;
			int stop1ID = 0;
			int stop2ID = 0;
			try {
				String lineStops = stopsReaderPresent.readLine();
				while(lineStops != null) {
					try {
						if(Integer.parseInt(lineStops.split(",")[0]) > largestStopID) {
							largestStopID = Integer.parseInt(lineStops.split(",")[0]);
						}
					}catch (NumberFormatException e) {}
					String[] splitStopsPresent = lineStops.split(",");
					String stopName = splitStopsPresent[2];
					if(stop1.equalsIgnoreCase(stopName)) {
						stop1Found = true;
						stop1ID = Integer.parseInt(splitStopsPresent[0]);
					}
					if(stop2.equalsIgnoreCase(stopName)) {
						stop2Found = true;
						stop2ID = Integer.parseInt(splitStopsPresent[0]);
					}
					lineIndexStops++;
					lineStops = stopsReaderPresent.readLine();
				}
				if(!stop1Found && stop2Found) {
					return new String[] {"1"};
				}else if(stop1Found && !stop2Found) {
					return new String[] {"2"};
				}else if(!stop1Found && !stop2Found) {
					return new String[] {"12"};
				}
				//otherwise it will continue to the rest of the function
		}catch(FileNotFoundException e) {
			 System.out.println("FIle not found!");
		}

		BufferedReader stopTimesReader = new BufferedReader(new FileReader("src/stop_times.txt"));
		BufferedReader stopTransfersReader = new BufferedReader(new FileReader("src/transfers.txt"));
		String stopTimesCurrentLine = stopTimesReader.readLine();
		stopTimesCurrentLine = stopTimesReader.readLine();
		String transfersCurrentLine = stopTransfersReader.readLine();
		transfersCurrentLine = stopTransfersReader.readLine();
		
		int numberOfEdges = 0;
		//counts edges from stop_times file
		int index = 0;
		while(stopTimesCurrentLine != null) {
			numberOfEdges++;
			stopTimesCurrentLine = stopTimesReader.readLine();
		}
		// counts edges from transfers file
		while(transfersCurrentLine != null) {
			numberOfEdges++;
			transfersCurrentLine = stopTransfersReader.readLine();
		}
		stopTimesReader.close();
		stopTransfersReader.close();
		
		Graph graph = new Graph(largestStopID+1, numberOfEdges);
		
		stopTimesReader = new BufferedReader(new FileReader("src/stop_times.txt"));
		stopTransfersReader = new BufferedReader(new FileReader("src/transfers.txt"));
		String stopTimesLastLine = stopTimesReader.readLine();
		stopTimesCurrentLine = stopTimesReader.readLine();
		transfersCurrentLine = stopTransfersReader.readLine();
		transfersCurrentLine = stopTransfersReader.readLine();
		
		//fills graph with edges from stop_times file
		String[] stopTimesLastLineComponents;
		String[] stopTimesCurrentLineComponents;
		while(stopTimesCurrentLine != null) {
			stopTimesLastLineComponents = stopTimesLastLine.split(",");
			stopTimesCurrentLineComponents = stopTimesCurrentLine.split(",");
			if(stopTimesLastLineComponents[0].equalsIgnoreCase(stopTimesCurrentLineComponents[0])){
				graph.addNode(Integer.parseInt(stopTimesLastLineComponents[3]), Integer.parseInt(stopTimesCurrentLineComponents[3]), 1);
			}
			stopTimesLastLine = stopTimesCurrentLine;
			stopTimesCurrentLine = stopTimesReader.readLine();
		}
		//fills graph with edges from transfers file
		String[] transfersCurrentLineComponents;
		while(transfersCurrentLine != null) {
			transfersCurrentLineComponents = transfersCurrentLine.split(",");
			if(transfersCurrentLineComponents[2].equalsIgnoreCase("2")) {
				graph.addNode(Integer.parseInt(transfersCurrentLineComponents[0]), Integer.parseInt(transfersCurrentLineComponents[1]), Float.parseFloat(transfersCurrentLineComponents[3])/100);
			}else if(transfersCurrentLineComponents[2].equalsIgnoreCase("0")) {
				graph.addNode(Integer.parseInt(transfersCurrentLineComponents[0]), Integer.parseInt(transfersCurrentLineComponents[1]), 2);
			}
			transfersCurrentLine = stopTransfersReader.readLine();
		}
		String[] result = Graph.djikstrasAlgorithm(stop1ID, stop2ID);
		return result;
	}

  /*
   * Return empty string array if no stops exist containing the string name
   * Otherwise, return string array of the details of all stops containing the string name
   * Look at project specification for details
   * */
  public static String[] getStopInformation(String name) {
    return null; //return Trips[]
  }

  public static Boolean checkValidTime(String time) {
    try {
      String[] stringComponents = time.split(":");
      int temp = Integer.parseInt(stringComponents[0].trim());
      if (temp < 0 || temp > 23) {
        return false;
      }
      for (int i = 1; i <= 2; ++i) {
        temp = Integer.parseInt(stringComponents[i]);
        if (temp < 0 || temp > 59) {
          return false;
        }
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static Boolean compareTimes(String time1, String time2) {
    try {
      String[] stringComponents1 = time1.split(":");
      String[] stringComponents2 = time2.split(":");
      if (stringComponents1.length != stringComponents2.length) {
        return false;
      }
      int temp1 = 0, temp2 = 0;
      for (int i = 0; i < stringComponents1.length; ++i) {
        temp1 = Integer.parseInt(stringComponents1[i].trim());
        temp2 = Integer.parseInt(stringComponents2[i].trim());
        if (temp1 != temp2) {
          return false;
        }
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /*
   * Return empty string array if no stops exist with the given arrival time
   * Otherwise, return a string array of the details of all stops, sorted by their trip id
   * Look at project specification for details
   * */
  public static String[] searchForTripsByArrivalTime(String inputString) {
    List<Pair> validTripIdsAndStops = new LinkedList<>();
    String readString = "";
    String[] splitStrings = new String[9];
    File fileObj = new File(pathToStopTimes);
    try {
      Scanner reader = new Scanner(fileObj);
      String lastTripId = "";
      Boolean lastTripWasValid = false;
      String stopsInLastTrip = "";
      while (reader.hasNextLine()) {
        readString = reader.nextLine();
        splitStrings = readString.split(",");
        if (splitStrings.length > 2 && checkValidTime(splitStrings[1])) {
          if (splitStrings[0].compareTo(lastTripId) != 0) {
            if (lastTripWasValid) {
              validTripIdsAndStops.add(new Pair(lastTripId, stopsInLastTrip));
            }
            lastTripWasValid = false;
            stopsInLastTrip = splitStrings[3];
            lastTripId = splitStrings[0];
          } else {
            stopsInLastTrip += " -> " + splitStrings[3];
          }
          if (compareTimes(inputString, splitStrings[1])) {
            lastTripWasValid = true;
          }
        }
      }
      if (lastTripWasValid) {
        validTripIdsAndStops.add(new Pair(lastTripId, stopsInLastTrip));
      }
      if (validTripIdsAndStops.size() == 0) {
        reader.close();
        return new String[0];
      }
      String[] array_ans = new String[validTripIdsAndStops.size()];
      Collections.sort(validTripIdsAndStops);
      int i = 0;
      // Using foreach as order is preserved in it
      for (Pair p : validTripIdsAndStops) {
        array_ans[i++] = "Trip Id: " + p.tripId + " with stops : " + p.stops;
      }
      reader.close();
      return array_ans;
    } catch (Exception e) {
    	System.out.println(e);
      return new String[0];
    }
  }


  public static void main(String[] args){
		System.out.println("Choose one of of three functionalities");
		System.out.println("1. Finding shortest paths between 2 bus stops");
		System.out.println("2. Searching for a bus stop by full name or by the first few characters in the name");
		System.out.println("3. Searching for all trips with a given arrival time");

		Scanner scanner = new Scanner(System.in);
		boolean exited = false;
		boolean validInput = false;
		int mode = -1;
		while(!validInput && !exited) {
			System.out.print("Type either 1, 2 or 3 (or exit):");
			String inputString = scanner.next();
			try {
				int inputNumber = Integer.parseInt(inputString);
				if(inputNumber == 1 || inputNumber == 2 || inputNumber == 3){
					validInput = true;
					mode = inputNumber;
				}else {
					System.out.println("Input number must be either 1, 2 or 3, not " + inputNumber);
				}
			}catch(NumberFormatException e){
				if(inputString.equalsIgnoreCase("exit")) {
					exited = true;
				}else {
					System.out.println("Input must be a sinlge digit number either 1, 2 or 3, not a string");
				}
			}
		}
		if(mode == 1) {
			String stop1 = "";
			String stop2 = "";
			boolean validInput1 = false;
			stop1 = scanner.nextLine();
			while(!validInput1 && !exited) {
				System.out.print("Enter first bus stop name:");
				while(!scanner.hasNextLine()) {}
				stop1 = scanner.nextLine();
				if(stop1.equalsIgnoreCase("exit")) {
					exited = true;
				}else if(Pattern.matches(".*[a-zA-Z]+.*", stop1)) {
					validInput1 = true;
				}else {
					System.out.println("Please input text with at least one letter");
				}
			}
			validInput1 = false;
			//stop2 = scanner.nextLine();
			while(!validInput1 && !exited) {
				System.out.print("Enter second bus stop name:");
				while(!scanner.hasNextLine()) {}
				stop2 = scanner.nextLine();
				if(stop2.equalsIgnoreCase("exit")) {
					exited = true;
				}else if(Pattern.matches(".*[a-zA-Z]+.*", stop2)) {
					validInput1 = true;
				}else {
					System.out.println("Please input text with at least one letter");
				}
			}
			if(!exited){
				String[] result;
				try {
					result = getShortestRoute(stop1, stop2);
				}catch(IOException e) {
					result = new String[0];
				}
				if(result.length == 0) {
					System.out.println("No path route exists between these stops");
				}else if(result[0].equalsIgnoreCase("1") || result[0].equalsIgnoreCase("2")) {
					System.out.println("Invalid bus stop " + result[0] + " name");
				}else if(result[0].equalsIgnoreCase("12")) {
					System.out.println("Invalid bus stop 1 and bus stop 2");
				}else {
					for(int i=0; i<result.length; i++) {
						System.out.println(result[i]);
					}
				}
			}


		}else if(mode == 2) {
			boolean validInput2 = false;
			String inputString = "";
			while(!validInput2 && !exited) {
				System.out.print("Enter the bus stop name:");
				inputString = scanner.next();
				if(inputString.equalsIgnoreCase("exit")) {
					exited = true;
				}else if(Pattern.matches(".*[a-zA-Z]+.*", inputString)) {
					validInput2 = true;
				}else {
					System.out.println("Please input text with at least one letter");
				}
			}
			if(!exited) {
				String[] results = getStopInformation(inputString.toUpperCase());
				if(results.length == 0) {
					System.out.println("There are no stops with this name");
				}else {
					for(int i=0; i<results.length; i++) {
						System.out.println(results[i]);
					}
				}
			}

		}else if(mode == 3){
			boolean validInput3 = false;
			String inputString = "";
			int hours = 0;
			int minutes = 0;
			int seconds = 0;
			while(!validInput3 && !exited) {
				System.out.print("Enter the arrival time in the format hh:mm:ss :");
				inputString = scanner.next();
				String[] stringComponents = inputString.split(":");
				if(inputString.equalsIgnoreCase("exit")) {
					exited = true;
				}else if(stringComponents.length != 3) {
					System.out.println("Please input 3 values, one each for hours, minutes, and seconds");
				}else {
					try {
						hours = Integer.parseInt(stringComponents[0]);
						if(hours < 0 || hours > 23) {
							System.out.println("Please a number for for the hours value of 00-23");
						}else {
							try {
								minutes = Integer.parseInt(stringComponents[1]);
								if(minutes < 0 || minutes > 59) {
									System.out.println("Please a number for for the minutes value of 00-59");
								}else {
									try {
										seconds = Integer.parseInt(stringComponents[2]);
										if(seconds < 0 || seconds > 59) {
											System.out.println("Please a number for for the seconds value of 00-59");
										}else {
											validInput3 = true;
										}
									}catch(NumberFormatException e){
										System.out.println("Please input a number for the seconds value and not a string");
									}
								}
							}catch(NumberFormatException e){
								System.out.println("Please input a number for the minutes value and not a string");
							}
						}
					}catch(NumberFormatException e){
						System.out.println("Please input a number for the hours value and not a string");
					}
				}
			}
			if(!exited) {
				String[] result = searchForTripsByArrivalTime(inputString);
				if(result.length == 0) {
					System.out.println("No trips exist with this arrival time");
				}else{
					for(int i=0; i<result.length; i++) {
						System.out.println(result[i]);
					}
				}
			}
		}
		System.out.println("Exited");
	}
}