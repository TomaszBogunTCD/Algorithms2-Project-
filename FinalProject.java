import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;

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

public class FinalProject {
    public static final String pathToStopTimes = "stop_times.txt";

	/*
	 * Return string array containing one string element "1" if stop1 doesn't exist
	 * Return string array containing one string element "2" if stop2 doesn't exist
	 * Return empty string array if no path between exists between stop1 and stop2
	 * Otherwise, return a string array of the details of all stops traversed by the shortest path, with its cost by each stop
	 * Look at project specification for details
	 * */
	public static String[] getShortestRoute(String stop1, String stop2) {
		return null; //return Trips[]
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
		String[] stringComponents = time.split(":");
		int temp = Integer.parseInt(stringComponents[0]);
		if(temp < 0 || temp > 23) {
			return false;
		}
		for(int i = 1; i <= 2; ++i) {
			temp = Integer.parseInt(stringComponents[i]);
			if(temp < 0 || temp > 59){
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Return empty string array if no stops exist with the given arrival time
	 * Otherwise, return a string array of the details of all stops, sorted by their trip id
	 * Look at project specification for details
	 * */
	public static String[] searchForTripsByArrivalTime(String inputString) {
		List<Pair> validTripIdsAndStops = Collections.emptyList();
        String readString = "";
        String[] splitStrings = new String[9];
        Scanner reader = new Scanner(pathToStopTimes);
		String lastTripId = "";
		Boolean lastTripWasValid = false;
		String stopsInLastTrip = "";
        while(reader.hasNextLine()) {
            readString = reader.nextLine();
            splitStrings = readString.split(",");
			if(checkValidTime(splitStrings[1])) {
				if(splitStrings[0] != lastTripId) {
					if(lastTripWasValid) {
						validTripIdsAndStops.add(new Pair(lastTripId, stopsInLastTrip));
					}
					lastTripWasValid = false;
					stopsInLastTrip = splitStrings[3];
					lastTripId = splitStrings[0];
				} else {
					stopsInLastTrip += " -> " + splitStrings[3];
				}
				if(splitStrings[1] == inputString) {
					lastTripWasValid = true;
				}
			}
        }
       if(lastTripWasValid) {
		validTripIdsAndStops.add(new Pair(lastTripId, stopsInLastTrip));
	}
        if(validTripIdsAndStops.size()==0)
		{
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
				String[] result = getShortestRoute(stop1, stop2);
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