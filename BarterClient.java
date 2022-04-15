import java.net.Socket;
import java.util.*;
import java.io.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;

public class BarterClient extends Application {
	Account mainAccount;
	ObjectInputStream input;
	ObjectOutputStream output;
	Socket socket;
	LogInPane logIn = new LogInPane();
	AccountPane accountPane = new AccountPane();
	CreateAccountPane create = new CreateAccountPane();
	Scene accountScene = new Scene(accountPane, 650, 450);
	EditAccount edit = new EditAccount();
	List<Account> accounts = new ArrayList<>();
	Stage stage;

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		Scene scene = new Scene(logIn, 350, 250);
		scene.getStylesheets().add("barter.css");
		Scene editScene = new Scene(edit, 550, 390);
		edit.getStylesheets().add("barter.css");
		create.getStylesheets().add("barter.css");
		stage.setTitle("Barter");
		stage.setScene(scene);
		stage.show();
		
		try {
			getAccounts();
		}
		catch (IOException io) {
			io.printStackTrace();
		}
		for (int i = 0; i < accounts.size(); i++) {
			System.out.println(accounts.get(i).getAccountName());
		}
//		accounts.clear();
//		accounts.add(new Account("Kevin", "Hey"));
//		accounts.get(0).addItem("Chair", "Brown Leather Chair", 125);
//		try {
//			ObjectOutputStream outputFile = new ObjectOutputStream(
//					new DataOutputStream(new FileOutputStream("BData.dat")));
//			for (int i = 0; i < accounts.size(); i++) {
//				outputFile.writeObject(accounts.get(i));
//			}
//			System.out.println("saved");
//		}
//		catch (IOException io) {
//			io.printStackTrace();
//		}
		logIn.btSubmit.setOnAction(e ->{
			String name = logIn.tfName.getText();
			String pass = logIn.tfPass.getText();
			for (int i = 0; i < accounts.size(); i++) {
				if (accounts.get(i).getAccountName().equalsIgnoreCase(name)) {
					if (accounts.get(i).getAccountPass().equals(pass)) {
						mainAccount = accounts.get(i);
						System.out.println("before: " + accounts.size());
						accounts.remove(mainAccount);
						System.out.println("After: " + accounts.size());
						accountPane.tbItems.getTabs().clear();
						accountPane.setAccount(mainAccount);
						stage.setScene(accountScene);
					}
					else {
						logIn.lblPass.setText("Incorrect Password");
					}
				}
				else {
					logIn.lblName.setText("Incorrect User Name");
				}
			}
		});
		logIn.btCreate.setOnAction(e -> stage.setScene(new Scene(create, 350, 250)));
		logIn.btCancel.setOnAction(e -> System.exit(0));
		create.btSubmit.setOnAction(e -> {
			Account a = new Account(create.tfName.getText(), create.tfPass.getText());
			accounts.add(a);
			
			accountPane.setAccount(mainAccount);
			stage.setScene(accountScene);
		});
		create.btCancel.setOnAction(e -> stage.setScene(scene));
		accountPane.btAdd.setOnAction(e -> {
			mainAccount.addItem(accountPane.tfName.getText(), accountPane.tfDescription.getText(),
					Double.parseDouble(accountPane.tfPrice.getText()));
			accountPane.tbItems.getTabs().clear();
			accountPane.setAccount(mainAccount);
			accountPane.tfName.clear();
			accountPane.tfDescription.clear();
			accountPane.tfPrice.clear();
		});
		accountPane.btRemove.setOnAction(e -> {
			mainAccount.removeItem(accountPane.tbItems.getSelectionModel().getSelectedIndex());
			accountPane.tbItems.getTabs().clear();
			accountPane.setAccount(mainAccount);
		});
		accountPane.miEdit.setOnAction(e -> stage.setScene(editScene));
		edit.btEditName.setOnAction(e -> {
			mainAccount.setAccountName(edit.tfName.getText());
			edit.tfName.clear();
		});
		accountPane.miDelete.setOnAction(e -> {
			BorderPane bp = new BorderPane();
			Scene delete = new Scene(bp, 350, 250);
			delete.getStylesheets().add("barter.css");
			Label lbSure = new Label("Delete Account?");
			Button btYes = new Button("Yes");
			Button btCancel = new Button("Cancel");
			HBox hb = new HBox(15);
			bp.setCenter(lbSure);
			hb.getChildren().addAll(btYes, btCancel);
			bp.setBottom(hb);
			hb.setAlignment(Pos.CENTER);
			lbSure.setAlignment(Pos.CENTER);
			lbSure.getStyleClass().add("largelabel");
			bp.getStyleClass().add("mainpane");
			hb.getStyleClass().add("minorpane");
			btYes.getStyleClass().add("plainbutton");
			btCancel.getStyleClass().add("plainbutton");
			stage.setScene(delete);
			btCancel.setOnAction(q -> stage.setScene(accountScene));
			btYes.setOnAction(t -> {
				accounts.remove(mainAccount);
				saveAccounts();
				stage.setScene(scene);
				mainAccount = null;
			});
		});
		accountPane.miSave.setOnAction(e -> {
			if (checkAccounts(mainAccount)) {
				accounts.remove(mainAccount);
				accounts.add(mainAccount);
			}
			else {
				accounts.add(mainAccount);
			}
			saveAccounts();
			stage.setScene(scene);
			logIn.tfName.clear();
			logIn.tfPass.clear();
			mainAccount = null;
		});
		accountPane.miView.setOnAction(e -> {
			stage.setScene(new Scene(new ViewPane()));
		});
		edit.btEditPass.setOnAction(e -> {
			mainAccount.setAccountPass(edit.tfPass.getText());
			edit.tfPass.clear();
		});
		edit.btEditBalance.setOnAction(e -> {
			double price = Double.parseDouble(edit.tfBalance.getText());
			mainAccount.deposit(price);
			edit.tfBalance.clear();
		});
		edit.btEditLocation.setOnAction(e -> {
			mainAccount.setLocation(edit.tfLocation.getText());
			edit.tfLocation.clear();
		});
		edit.btSave.setOnAction(e -> {
			if (checkAccounts(mainAccount)) {
				accounts.remove(mainAccount);
				accounts.add(mainAccount);
			}
			else {
				accounts.add(mainAccount);
			}
			saveAccounts();
		});
		edit.btCancel.setOnAction(e -> {
			accountPane.tbItems.getTabs().clear();
			accountPane.setAccount(mainAccount);
			stage.setScene(accountScene);
		});
	}
	public boolean checkAccounts(Account a) {
		boolean isInList = false;
		for (int i = 0; i < accounts.size(); i++) {
			if (a.getAccountID().equals(accounts.get(i).getAccountID())) {
				isInList = true;
			}
		}
		return isInList;
	}
	class LogInTask implements Runnable {
		@Override 
		public void run() {
			try {
				output = new ObjectOutputStream(socket.getOutputStream());
				String accountName = logIn.tfName.getText();
				String accountPass = logIn.tfPass.getText();

				output.writeUTF(accountName);
				output.writeUTF(accountPass);
				output.flush();

				input = new ObjectInputStream(socket.getInputStream());
				Object o = input.readObject();
				Account a = (Account)o;
				mainAccount = a;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						accountPane.setAccount(mainAccount);
						stage.setScene(new Scene(accountPane, 650, 450));
					}
				});
			}
			catch (EOFException ox) {

			}
			catch (IOException io) {
				io.printStackTrace();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	public void saveAccounts() {
		try {
			ObjectOutputStream output = new ObjectOutputStream(
					new DataOutputStream(new FileOutputStream("BData.dat")));
			for (int i = 0; i < accounts.size(); i++) {
				output.writeObject(accounts.get(i));
			}
			System.out.println("Transferred: " + accounts.size());
			output.close();
		}
		catch (IOException l) {
			l.printStackTrace();
		}
	}
	public void getAccounts() throws IOException {
		accounts = new ArrayList<Account>();
		try {
			ObjectInputStream input = new ObjectInputStream(
					new DataInputStream(new FileInputStream("BData.dat")));
			while (true) {
				Object o = input.readObject();
				Account a = (Account)o;
				accounts.add(a);
			}
		}
		catch (EOFException e) {
			e.toString();
		}
		catch (IOException io) {
			io.printStackTrace();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Received: " + accounts.size());
	}
	public static void main(String[] args) {
		launch(args);
	}
	class LogInPane extends BorderPane {
		Label lblName = new Label("Account Name: ");
		Label lblPass = new Label("Password: ");
		TextField tfName = new TextField();
		TextField tfPass = new TextField();
		Button btSubmit = new Button("Submit");
		Button btCreate = new Button("Create Account");
		Button btCancel = new Button("Cancel");
		VBox vbLabels = new VBox(10);
		VBox vbText = new VBox(10);
		HBox hbPanes = new HBox(10);
		HBox hbButtons = new HBox(10);

		public LogInPane() {
			getStyleClass().add("mainpane");
			vbLabels.setAlignment(Pos.CENTER);
			vbLabels.getChildren().addAll(lblName, lblPass);
			lblName.getStyleClass().add("plainlabel");
			lblPass.getStyleClass().add("plainlabel");
			vbText.setAlignment(Pos.CENTER);
			vbText.getChildren().addAll(tfName, tfPass);
			hbPanes.getStyleClass().add("minorpane");
			tfName.getStyleClass().add("plaintextfield");
			tfPass.getStyleClass().add("plaintextfield");
			hbPanes.getChildren().addAll(vbLabels, vbText);
			hbPanes.setAlignment(Pos.CENTER);
			hbButtons.getChildren().addAll(btSubmit, btCreate, btCancel);
			hbButtons.setAlignment(Pos.CENTER);
			hbButtons.getStyleClass().add("minorpane");
			btSubmit.getStyleClass().add("plainbutton");
			btCreate.getStyleClass().add("plainbutton");
			btCancel.getStyleClass().add("plainbutton");
			setCenter(hbPanes);
			setBottom(hbButtons);
			setPadding(new Insets(15));
		}
	}
	class AccountPane extends BorderPane {
		MenuBar menuBar = new MenuBar();
		Menu menuAccount = new Menu("Account");
		MenuItem miEdit = new MenuItem("Edit Account");
		MenuItem miDelete = new MenuItem("Delete Account");
		MenuItem miSave = new MenuItem("Save Account");
		Menu menuBuy = new Menu("Buy Items");
		MenuItem miView = new MenuItem("View Accounts");
		Menu menuHelp = new Menu("Help");
		MenuItem miHelp = new MenuItem("Help");
		TabPane tbItems = new TabPane();
		VBox vbItems = new VBox(25);
		HBox hbLabels = new HBox(90);
		HBox hbText = new HBox(10);
		HBox hbButtons = new HBox(35);
		Button btAdd = new Button("Add Item");
		Button btRemove = new Button("Remove");
		TextField tfName = new TextField();
		TextField tfDescription = new TextField();
		TextField tfPrice = new TextField();
		VBox vbInfo = new VBox(15);
		Label lbName = new Label();
		Label lbLocation = new Label();
		Label lbBalance = new Label();
		Label lbItemCount = new Label();
		Label lbItemName = new Label("Item Name");
		Label lbItemDescription = new Label("Description");
		Label lbItemPrice = new Label("Item Price");

		public AccountPane() {
			setPadding(new Insets(5, 15, 15, 15));
			setTop(menuBar);
			menuBar.getMenus().addAll(menuAccount, menuBuy, menuHelp);
			menuAccount.getItems().addAll(miEdit, miDelete, miSave);
			menuBuy.getItems().add(miView);
			menuHelp.getItems().add(miHelp);
			setLeft(vbInfo);
			vbInfo.setPrefWidth(137);
			vbInfo.getChildren().addAll(lbName, lbLocation, lbBalance, lbItemCount);
			vbInfo.setAlignment(Pos.TOP_CENTER);
			setRight(vbItems);
			tbItems.setPrefSize(125, 235);
			vbItems.getChildren().addAll(tbItems, hbLabels, hbText, hbButtons);
			vbItems.setAlignment(Pos.CENTER);
			hbLabels.getChildren().addAll(lbItemName, lbItemDescription, lbItemPrice);
			hbLabels.setAlignment(Pos.CENTER);
			hbText.getChildren().addAll(tfName, tfDescription, tfPrice);
			hbText.setAlignment(Pos.CENTER);
			hbButtons.getChildren().addAll(btAdd, btRemove);
			hbButtons.setAlignment(Pos.CENTER);

		}
		public void setAccount(Account a) {
			for (int i = 0; i < a.size(); i++) {
				ItemTab it = new ItemTab(a.getItem(i));
				tbItems.getTabs().add(it);
			}
			accountPane.lbName.setText(a.getAccountName());
			accountPane.lbLocation.setText((a.getLocation() == null) ? "Not Entered" : a.getLocation());
			accountPane.lbBalance.setText("Balance: $" + a.getBalance() + "0");
			accountPane.lbItemCount.setText("Items: " + a.getItemCount());
		}
	}
	class CreateAccountPane extends BorderPane {
		Label lblName = new Label("Account Name: ");
		Label lblPass = new Label("Password: ");
		TextField tfName = new TextField();
		TextField tfPass = new TextField();
		Button btSubmit = new Button("Submit");
		Button btCancel = new Button("Cancel");
		VBox vbLabels = new VBox(10);
		VBox vbText = new VBox(10);
		HBox hbPanes = new HBox(10);
		HBox hbButtons = new HBox(10);

		public CreateAccountPane() {
			vbLabels.setAlignment(Pos.CENTER);
			vbLabels.getChildren().addAll(lblName, lblPass);
			vbText.setAlignment(Pos.CENTER);
			vbText.getChildren().addAll(tfName, tfPass);
			hbPanes.getChildren().addAll(vbLabels, vbText);
			hbPanes.setAlignment(Pos.CENTER);
			hbButtons.getChildren().addAll(btSubmit, btCancel);
			hbButtons.setAlignment(Pos.CENTER);
			setCenter(hbPanes);
			setBottom(hbButtons);
			setPadding(new Insets(15));
			getStyleClass().add("mainpane");
			hbPanes.getStyleClass().add("minorpane");
			hbButtons.getStyleClass().add("minorpane");
			lblName.getStyleClass().add("plainlabel");
			lblPass.getStyleClass().add("plainlabel");
			tfName.getStyleClass().add("plaintextfield");
			tfPass.getStyleClass().add("plaintextfield");
			btSubmit.getStyleClass().add("plainbutton");
			btCancel.getStyleClass().add("plainbutton");
		}

	}
	class ItemTab extends Tab {
		TextField itemName = new TextField();
		TextArea description = new TextArea();
		TextField itemPrice = new TextField();
		VBox vb = new VBox(15);

		public ItemTab(Item i) {
			super(i.getItemName());
			itemName.setText("Item Name: " + i.getItemName());
			itemName.setEditable(false);
			description.setText("Item Description: " + i.getDescription());
			description.setPrefHeight(80);
			description.setPrefWidth(100);
			description.setWrapText(true);
			description.setEditable(false);
			itemPrice.setText("$" + i.getItemPrice() + "0");
			itemPrice.setEditable(false);
			vb.getChildren().addAll(itemName, itemPrice, description);
			vb.setAlignment(Pos.CENTER);
			setContent(vb);

		}
	}
	class EditAccount extends BorderPane {
		Label lbEdit = new Label("Edit Account");
		HBox hb = new HBox(40);
		VBox vbName = new VBox(20);
		VBox vbBalance = new VBox(20);
		VBox vbSave = new VBox(25);
		Label lbName = new Label("Account Name");
		Label lbPass = new Label("Password");
		Label lbBalance = new Label("Balance");
		Label lbLocation = new Label("Location");
		Label lbSave = new Label("Save Changes");
		TextField tfName = new TextField();
		TextField tfPass = new TextField();
		TextField tfBalance = new TextField();
		TextField tfLocation = new TextField();
		Button btEditName = new Button("Edit");
		Button btEditPass = new Button("Edit");
		Button btEditBalance = new Button("Edit");
		Button btEditLocation = new Button("Edit");
		Button btSave = new Button("Save");
		Button btCancel = new Button("Cancel");

		public EditAccount() {
			lbEdit.getStyleClass().add("largelabel");
			getStyleClass().add("mainpane");
			setAlignment(lbEdit, Pos.CENTER);
			setStyle("-fx-background-color: seagreen");
			setTop(lbEdit);
			hb.setAlignment(Pos.CENTER);
			hb.getChildren().addAll(vbName, vbSave, vbBalance);
			vbName.setAlignment(Pos.TOP_CENTER);
			vbName.getChildren().addAll(lbName, tfName, btEditName, lbPass, tfPass, btEditPass);
			vbBalance.setAlignment(Pos.TOP_CENTER);
			vbBalance.getChildren().addAll(lbBalance, tfBalance, btEditBalance, lbLocation, tfLocation, btEditLocation);
			vbSave.setAlignment(Pos.CENTER);
			vbSave.getChildren().addAll(lbSave, btSave, btCancel);
			lbName.getStyleClass().add("plainlabel");
			lbPass.getStyleClass().add("plainlabel");
			lbBalance.getStyleClass().add("plainlabel");
			lbLocation.getStyleClass().add("plainlabel");
			lbSave.getStyleClass().add("plainlabel");
			tfName.getStyleClass().add("plaintextfield");
			tfPass.getStyleClass().add("plaintextfield");
			tfBalance.getStyleClass().add("plaintextfield");
			tfLocation.getStyleClass().add("plaintextfield");
			btEditName.getStyleClass().add("plainbutton");
			btEditPass.getStyleClass().add("plainbutton");
			btEditBalance.getStyleClass().add("plainbutton");
			btEditLocation.getStyleClass().add("plainbutton");
			btSave.getStyleClass().add("plainbutton");
			btCancel.getStyleClass().add("plainbutton");
			hb.getStyleClass().add("minorpane");
			setCenter(hb);
			vbName.getStyleClass().add("minipane");
		}
	}
	class ViewPane extends BorderPane {
		BorderPane bpAccounts = new BorderPane();
		VBox vbMain = new VBox(10);
		VBox vbAccount = new VBox(10);
		Label lbMName = new Label(mainAccount.getAccountName());
		Label lbMItemCount = new Label("Items: " + mainAccount.getItemCount());
		Label lbMBalance = new Label("Balance: " + mainAccount.getBalance());
		Label lbName = new Label();
		Label lbItemCount = new Label();
		HBox hbAccounts = new HBox(10);
		TableView<Account> tbvAccounts = new TableView<>();
		
		public ViewPane() {
			ArrayList<String> accountInfo = new ArrayList<>();
			for (Account a: accounts) {
				String s = a.getAccountName() + ", Items: " + a.getItemCount();
				accountInfo.add(s);
			}
			
			setLeft(vbMain);
			setCenter(tbvAccounts);
			vbMain.getChildren().addAll(lbMName, lbMItemCount, lbMBalance);
			ObservableList<Account> listAccounts = FXCollections.observableArrayList(accounts);
			tbvAccounts.setItems(listAccounts);
			vbAccount.getChildren().addAll(lbName, lbItemCount);
			TableColumn accountCol = new TableColumn("Account");
			accountCol.setMinWidth(100);
			accountCol.setCellValueFactory(
	                new PropertyValueFactory<Account, String>("accountName"));
			
			TableColumn itemNameCol = new TableColumn("Item Name");
			itemNameCol.setMinWidth(100);
			TableColumn itemDescriptionCol = new TableColumn("Description");
			itemDescriptionCol.setMinWidth(100);
			TableColumn itemPriceCol = new TableColumn("Price");
			itemPriceCol.setMinWidth(100);
			TableCell accountCell = new TableCell();
			accountCell.setGraphic(vbAccount);
			tbvAccounts.getColumns().addAll(accountCol, itemNameCol, itemDescriptionCol, itemPriceCol);
			
			
		}
	}
}

