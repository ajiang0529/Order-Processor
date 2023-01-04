package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

public class ProcessOrder implements Runnable{
	private TreeMap <Integer,Client> clientDatabase = new TreeMap<>();
	private TreeMap <String, Double> itemDatabase = new TreeMap<>();
	private String clientFileName;

	public ProcessOrder(String itemFileName, String clientFileName, 
			TreeMap<Integer,Client> clientDatabase, TreeMap<String,Double> itemDatabase) {
		this.clientDatabase = clientDatabase;
		this.itemDatabase = itemDatabase;
		this.clientFileName = clientFileName;
	}

	public void scanClientDatabase(File file) throws FileNotFoundException {
		Scanner clientScanner = new Scanner(file);
		clientScanner.next();
		int clientId = clientScanner.nextInt();
		Client client = new Client(clientId);
		while (clientScanner.hasNext()) {
			String itemName = clientScanner.next();
			clientScanner.next();
			double individualCost = itemDatabase.get(itemName);
			RegistryItem item = new RegistryItem(itemName,individualCost);
			client.addItem(item);
		}
		synchronized(clientDatabase) {
			clientDatabase.put(clientId, client);
			clientScanner.close();
		}
	}
	public void scanRegistry(File file) throws FileNotFoundException {
		Scanner registryScanner = new Scanner(file);
		while (registryScanner.hasNext()) {
			String itemName = registryScanner.next();
			Double individualCost = registryScanner.nextDouble();
			itemDatabase.put(itemName,individualCost);
		}
		registryScanner.close();
	}
	public void setFileName(String clientFileName){
		this.clientFileName = clientFileName;
	}

	@Override
	public void run() {
		File clientFile = new File(clientFileName);
		try {
			scanClientDatabase(clientFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
