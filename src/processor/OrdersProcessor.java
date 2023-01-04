package processor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;


public class OrdersProcessor{	
	public static void  main(String[] args) throws FileNotFoundException, InterruptedException{
		TreeMap <Integer,Client> clientDatabase = new TreeMap<>();
		TreeMap <String, Double> itemDatabase = new TreeMap<>();
		TreeMap <String, Integer> totalDatabase = new TreeMap<>();
		ArrayList<Thread> threadList = new ArrayList<>();
		boolean multipleThreads = false;
		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter item's data file name: ");
		String itemsFileName = scanner.next();
		File itemFile = new File(itemsFileName);
		Scanner registryScanner = new Scanner(itemFile);
		while (registryScanner.hasNext()) {
			String itemName = registryScanner.next();
			Double individualCost = registryScanner.nextDouble();
			itemDatabase.put(itemName,individualCost);
		}
		registryScanner.close();

		System.out.println("Enter 'y' for multiple threads, any other character otherwise: ");
		if (scanner.next().equals("y")) {
			multipleThreads = true;
		}
		System.out.println("Enter number of orders to process: ");
		int orderNumber = scanner.nextInt();

		System.out.println("Enter order's base filename: ");
		String baseFileName = scanner.next();

		System.out.println("Enter result's filename: ");
		String resultFileName = scanner.next();
		long startTime = System.currentTimeMillis();
		if (!multipleThreads) {
			for (int i = 1; i <= orderNumber; i++) {
				ProcessOrder op = new ProcessOrder(itemsFileName, 
						baseFileName, clientDatabase, itemDatabase);
				op.setFileName(baseFileName + i + ".txt");
				op.run();
			}
		}else {
			String holder = baseFileName;
			for (int clientIndex = 1; clientIndex <= orderNumber; clientIndex++) {
				baseFileName += clientIndex + ".txt";
				threadList.add(new Thread(new ProcessOrder(itemsFileName, 
						baseFileName, clientDatabase, itemDatabase)));
				baseFileName = holder;
			}
			for (Thread thread : threadList) {
				thread.start();
			}
			for (Thread thread : threadList) {
				thread.join();
			}
		}
		FileWriter fw;
		try {
			fw = new FileWriter(resultFileName,true);
			String output = "";
			double grandTotal = 0;
			double itemTotal = 0;
			double clientTotal = 0;
			for (Integer clientId : clientDatabase.keySet()) {
				output += "----- Order details for client with Id: " + clientId + " -----" + "\n";
				for (String itemName : clientDatabase.get(clientId).getItemList().keySet()) {
					if (clientDatabase.get(clientId).getItemList().containsKey(itemName)) {
						double individualCost = itemDatabase.get(itemName);
						int quantity = clientDatabase.get(clientId).getTotalBought(itemName);
						double totalCost = individualCost * quantity;
						clientTotal += totalCost;
						output += "Item's name: " + itemName + ", Cost per item: " + 
								NumberFormat.getCurrencyInstance().format(individualCost)
								+ ", Quantity: " + quantity + ", Cost: " 
								+ NumberFormat.getCurrencyInstance().format(totalCost) + "\n";
						if (!totalDatabase.containsKey(itemName)) {
							totalDatabase.put(itemName, quantity);
						}else {
							totalDatabase.put(itemName, totalDatabase.get(itemName) + quantity);
						}
					}
				}
				output += "Order Total: " + NumberFormat.getCurrencyInstance().format(clientTotal) + "\n";
				clientTotal = 0;
			}
			output += "***** Summary of all orders *****" + "\n";
			for (String itemName : totalDatabase.keySet()) {
				itemTotal = totalDatabase.get(itemName) * itemDatabase.get(itemName);
				grandTotal += itemTotal;
				output += "Summary - Item's name: " + itemName + ", Cost per item: " 
						+ NumberFormat.getCurrencyInstance().format(itemDatabase.get(itemName)) 
						+ ", Number sold: " + totalDatabase.get(itemName) + ", Item's Total: " 
						+ NumberFormat.getCurrencyInstance().format(itemTotal) + "\n";
			}
			output += "Summary Grand Total: " + NumberFormat.getCurrencyInstance().format(grandTotal) + "\n";
			fw.append(output);
			long endTime = System.currentTimeMillis();
			System.out.println("Processing time (msec): " + (endTime - startTime));
			fw.close();
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
