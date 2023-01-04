package processor;

import java.util.TreeMap;

public class Client {
	private int id;
	private TreeMap<String, Integer> itemList;

	public Client(int id) {
		this.id = id;
		itemList = new TreeMap<>();
	}
	public int getId() {
		return id;
	}
	public void addItem(RegistryItem item) {
		String name = item.getItemName();
		if (itemList.get(name) == null) {
			itemList.put(name, 1);
		}else {
			itemList.put(name, itemList.get(name) + 1);
		}
	}
	public TreeMap<String, Integer> getItemList() {
		return itemList;
	}
	public int getTotalBought(String itemName) {
		if (itemList.containsKey(itemName)) {
			return itemList.get(itemName);
		}
		return 0;
	}
}
