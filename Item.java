import java.io.Serializable;

import javafx.scene.image.Image;

public class Item extends Object implements Serializable {
	private String itemName;
	private String description;
	private double itemPrice;
	private final String itemID;
	private transient Image image;
	
	public Item(String name, String description, double price) {
		itemName = name;
		this.setDescription(description);
		itemPrice = price;
		this.image = new Image("No-Img.png");
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
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public void setImage(String imagePath) {
		Image itemImage = new Image(imagePath);
		this.image = itemImage;
	}
	public String getItemID() {
		return itemID;
	}
}
