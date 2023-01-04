package processor;

import java.util.Comparator;

public class Sorted implements Comparator<Client>{
	@Override
	public int compare (Client a, Client b) {
		return a.getId() - (b.getId());
	}
}
