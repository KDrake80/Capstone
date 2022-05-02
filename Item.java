import java.io.Serializable;

import javafx.scene.image.Image;

public class Item extends Object implements Serializable {
	private String itemName;
	private String description;
	private double itemPrice;
	private final String itemID;
	
	public Item(String name, String description, double price) {
		itemName = name;
		this.setDescription(description);
		itemPrice = price;
		itemID = "IData" + (int)(Math.random() * 1000);
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(double itemPrice) {
		this.itemPrice = itemPrice;
	}
	public String getItemID() {
		return itemID;
	}
}
