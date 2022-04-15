import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import javafx.scene.image.Image;

import java.util.*;

public class Account implements Serializable, Collection {
	private String accountName;
	private String accountPass;
	private Date dateCreated;
	private String location;
	private final String accountID;
	private List<Item> items;
	private double balance = 0;
	private int itemCount = 0;
	private transient Image image;
	
	public Account() {
		this.dateCreated = new Date();
		items = new ArrayList<Item>();
		accountID = "BID" + (int)(Math.random() * 1000);
	}
	public Account(String name, String pass) {
		accountName = name;
		accountPass = pass;
		dateCreated = new Date();
		accountID = "BID" + (int)(Math.random() * 1000);
		items = new ArrayList<Item>();
	}
	public Account(Account alt) {
		this.accountName = alt.getAccountName();
		this.accountPass = alt.getAccountPass();
		this.dateCreated = alt.getDateCreated();
		this.location = alt.getLocation();
		accountID = alt.getAccountID();
		this.items = alt.getItems();
		this.balance = alt.getBalance();
		this.itemCount = alt.getItemCount();
		this.image = alt.getImage();
	}
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountPass() {
		return accountPass;
	}

	public void setAccountPass(String accountPass) {
		this.accountPass = accountPass;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAccountID() {
		return accountID;
	}
	public List<Item> getItems() {
		return items;
	}
	public void addItem(Item i) {
		items.add(i);
		itemCount++;
	}
	public void addItem(String name, String description, double price) {
		Item i = new Item(name, description, price);
		items.add(i);
		itemCount++;
	}
	public Item getItem(int index) {
		return items.get(index);
	}
	public int getItemIndex(Item i) {
		int index = 0;
		for (Item x: items) {
			if (x.getItemName().equalsIgnoreCase(i.getItemName()))
				index = items.indexOf(x);
		}
		return index;
	}
	public void removeItem(int index) {
		items.remove(index);
		itemCount--;
	}
	public void removeItem(Item i) {
		items.remove(i);
		itemCount--;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}	
	public double getBalance() {
		return balance;
	}
	public void deposit(double a) {
		balance = getBalance() + a;
	}
	public void withdraw(double m) {
		if (balance > m)
			balance = getBalance() - m;
		else 
			System.out.println("Insufficent Funds");
	}
	public int checkBalance() {
		if (balance > 0)
			return 1;
		else if (balance == 0)
			return 0;
		else
			return -1;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		itemCount = items.size();
	}
	@Override
	public int size() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}
	public Image getImage() {
		return image;
	}
	public void setImage(String imagePath) {
		Image accountImage = new Image(imagePath);
		this.image = accountImage;
	}

	@Override
	public boolean contains(Object o) {
		Item i = (Item)o;
		return items.contains(i);
	}

	@Override
	public Iterator iterator() {
		return items.iterator();
	}

	@Override
	public Object[] toArray() {
		return items.toArray();
	}

	@Override
	public Object[] toArray(Object[] a) {
		return null;
	}
	@Override
	public boolean add(Object o) {
		Item i = (Item)o;
		items.add(i);
		if (items.contains(i))
			return true;
		else
			return false;
	}

	@Override
	public boolean remove(Object o) {
		Item i = (Item)o;
		items.remove(i);
		if (items.contains(i))
			return false;
		else
			return true;
	}

	@Override
	public boolean containsAll(Collection c) {
		if (items.containsAll(c))
			return true;
		else
			return false;
	}

	@Override
	public boolean addAll(Collection c) {
		items.addAll(c);
		if (items.containsAll(c))
			return true;
		else
			return false;
	}

	@Override
	public boolean removeAll(Collection c) {
		items.removeAll(c);
		if (items.containsAll(c))
			return false;
		else
			return true;
	}

	@Override
	public boolean retainAll(Collection c) {
		items.retainAll(c);
		if (items.containsAll(c))
			return true;
		else
			return false;
	}

	@Override
	public void clear() {
		items.clear();
		
	}
}
