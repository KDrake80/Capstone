import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Barter extends Application {
	Account mainAccount = new Account();
	List<Account> accounts = new ArrayList<>();
	Stage stage;
	public void start(Stage stage) {
		getAccounts();
		LogInPane logIn = new LogInPane();
		Scene logInScene = new Scene(logIn, 360, 260);
		logInScene.getStylesheets().add("barter.css");
		stage.setTitle("Barter");
		stage.setScene(logInScene);
		stage.show();
		AccountPane accountPane = new AccountPane();
		CreateAccountPane createPane = new CreateAccountPane();
		EditAccount editPane = new EditAccount();
		ViewPane viewPane = new ViewPane();
		Scene accountScene = new Scene(accountPane, 750, 500);
		accountScene.getStylesheets().add("barter.css");
		logIn.btSubmit.setOnAction(e -> {
			String accountName = logIn.tfName.getText();
			String accountPass = logIn.tfPass.getText();
			for (int i = 0; i < accounts.size(); i++) {
				if (accounts.get(i).getAccountName().equalsIgnoreCase(accountName)) {
					if (accounts.get(i).getAccountPass().equals(accountPass)) {
						mainAccount = accounts.get(i);
						accountPane.tbItems.getTabs().clear();
						accountPane.setAccount(mainAccount);
						stage.setScene(accountScene);
						accounts.remove(mainAccount);
						accounts.add(mainAccount);
					}
					else if (!accounts.get(i).getAccountPass().equals(accountPass)) {
						logIn.lblPass.setText("Incorrect Password");
					}
				}
			}
		});
		logIn.btCreate.setOnAction(e -> {
			stage.setScene(new Scene(createPane, 350, 250));
			stage.getScene().getStylesheets().add("barter.css");
		});
		logIn.btCancel.setOnAction(e -> System.exit(0));
		logIn.miHelp.setOnAction(e -> {
			HelpPane helpPane = new HelpPane();
			Scene help = new Scene(helpPane, 550, 400);
			help.getStylesheets().add("barter.css");
			stage.setScene(help);
			helpPane.btBack.setOnAction(s -> stage.setScene(logInScene));
		});
		createPane.btSubmit.setOnAction(e -> {
			mainAccount = new Account(createPane.tfName.getText(), createPane.tfPass.getText());
			accounts.add(mainAccount);
			accountPane.setAccount(mainAccount);
			stage.setScene(accountScene);
		});
		createPane.btCancel.setOnAction(e -> System.exit(1));
		accountPane.btAdd.setOnAction(e -> {
			String price = accountPane.tfPrice.getText();
			price = price.replace(",", "");
			double newPrice = Double.parseDouble(price);
			mainAccount.addItem(accountPane.tfName.getText(),
					accountPane.tfDescription.getText(), newPrice);
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
		accountPane.miSave.setOnAction(e -> {
			if (checkAccounts(mainAccount)) {
				accounts.remove(mainAccount);
				accounts.add(mainAccount);
			}
			else {
				accounts.add(mainAccount);
			}	
			saveAccounts();
			System.exit(2);
		});
		accountPane.miDelete.setOnAction(e -> {
			BorderPane bp = new BorderPane();
			VBox vbAll = new VBox(10);
			Scene delete = new Scene(bp, 350, 250);
			delete.getStylesheets().add("barter.css");
			Label lbSure = new Label("Delete Account?");
			Button btYes = new Button("Yes");
			Button btCancel = new Button("Cancel");
			HBox hb = new HBox(15);
			lbSure.getStyleClass().add("plainlabel");
			btYes.getStyleClass().add("plainbutton");
			btCancel.getStyleClass().add("plainbutton");
			vbAll.getStyleClass().add("infopane");
			bp.getStyleClass().add("mainpane");
			hb.setAlignment(Pos.CENTER);
			vbAll.setAlignment(Pos.CENTER);
			hb.getChildren().addAll(btYes, btCancel);
			vbAll.getChildren().addAll(lbSure, hb);
			bp.setCenter(vbAll);
			stage.setScene(delete);
			btYes.setOnAction(x -> {
				accounts.remove(mainAccount);
				saveAccounts();
				System.exit(3);
			});
			btCancel.setOnAction(p -> {
				stage.setScene(accountScene);
			});
		});
		accountPane.miHelp.setOnAction(e -> {
			HelpPane help = new HelpPane();
			stage.setScene(new Scene(help, 550, 400));
			stage.getScene().getStylesheets().add("barter.css");
			help.getStyleClass().add("mainpane");
			help.btBack.setOnAction(b -> stage.setScene(accountScene));	
		});

		accountPane.miEdit.setOnAction(e -> {
			Scene editScene = new Scene(editPane, 550, 450);
			editScene.getStylesheets().add("barter.css");
			stage.setScene(editScene);
		});
		editPane.btCancel.setOnAction(e -> {
			accountPane.tbItems.getTabs().clear();
			accountPane.setAccount(mainAccount);
			stage.setScene(accountScene);
		});
		editPane.btDeposit.setOnAction(e -> {
			String d = editPane.tfBalance.getText();
			d = d.replace(",", "");
			double deposit = Double.parseDouble(d);
			mainAccount.deposit(deposit);
			editPane.tfBalance.clear();
		});
		editPane.btWithdraw.setOnAction(e -> {
			String w = editPane.tfBalance.getText();
			w = w.replace(",", "");
			double withdraw = Double.parseDouble(w);
			mainAccount.withdraw(withdraw);
			editPane.tfBalance.clear();
		});
		editPane.btEditLocation.setOnAction(e -> {
			mainAccount.setLocation(editPane.tfLocation.getText());
			editPane.tfLocation.clear();
		});
		editPane.btEditName.setOnAction(e -> {
			mainAccount.setAccountName(editPane.tfName.getText());
			editPane.tfName.clear();
		});
		editPane.btEditPass.setOnAction(e -> {
			mainAccount.setAccountPass(editPane.tfPass.getText());
			editPane.tfPass.clear();
		});
		accountPane.miView.setOnAction(e -> {
			viewPane.setPages();
			Scene viewScene = new Scene(viewPane, 550, 450);
			viewScene.getStylesheets().add("barter.css");
			viewPane.setInfo();
			stage.setScene(viewScene);
		});
		viewPane.btCancel.setOnAction(q -> {
			accountPane.tbItems.getTabs().clear();
			accountPane.setAccount(mainAccount);
			stage.setScene(accountScene);
		});
		viewPane.btBuy.setOnAction(t -> {
			Account seller = accounts.get(viewPane.pag.getCurrentPageIndex());
			Item item = seller.getItem(viewPane.tbvAccounts.getSelectionModel().getSelectedIndex());
			if (mainAccount.getBalance() < item.getItemPrice()) {
				Stage secondStage = new Stage();
				BorderPane bp = new BorderPane();
				bp.setCenter(new Label("Insufficient Funds"));
				secondStage.setScene(new Scene(bp));
				secondStage.getScene().getStylesheets().add("barter.css");
				bp.getStyleClass().add("mainpane");
				bp.getCenter().getStyleClass().add("largelabel");
				secondStage.show();
			}
			else {
				mainAccount.withdraw(item.getItemPrice());
				seller.deposit(item.getItemPrice());
				seller.removeItem(item);
				mainAccount.addItem(item);
				saveAccounts();
				viewPane.setInfo();
				viewPane.setPages();
			}
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
	public void saveAccounts() {
		try {
			ObjectOutputStream output = new ObjectOutputStream(
					new DataOutputStream(new FileOutputStream("BData.dat")));
			for (int i = 0; i < accounts.size(); i++) {
				output.writeObject(accounts.get(i));
			}
			output.close();
		}
		catch (IOException l) {
			l.printStackTrace();

		}
	}
	public void getAccounts() {
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
	}
	public static void main(String[] args) {
		launch(args);
	}
	class LogInPane extends BorderPane {
		MenuBar menu = new MenuBar();
		MenuItem miHelp = new MenuItem("Help");
		Menu mHelp = new Menu("Help");
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
			setTop(menu);
			getStyleClass().add("mainpane");
			vbLabels.getStyleClass().add("minorpane");
			vbText.getStyleClass().add("minorpane");
			btSubmit.getStyleClass().add("plainbutton");
			btCreate.getStyleClass().add("plainbutton");
			btCancel.getStyleClass().add("plainbutton");
			tfName.getStyleClass().add("plaintextfield");
			tfPass.getStyleClass().add("plaintextfield");
			menu.getStyleClass().add("menubar");
			lblName.getStyleClass().add("plainlabel");
			lblPass.getStyleClass().add("plainlabel");
			hbPanes.getStyleClass().add("infopane");
			menu.getMenus().add(mHelp);
			mHelp.getItems().add(miHelp);
			vbLabels.setAlignment(Pos.CENTER);
			vbLabels.getChildren().addAll(lblName, lblPass);
			vbText.setAlignment(Pos.CENTER);
			vbText.getChildren().addAll(tfName, tfPass);
			hbPanes.getChildren().addAll(vbLabels, vbText);
			hbPanes.setAlignment(Pos.CENTER);
			hbButtons.getChildren().addAll(btSubmit, btCreate, btCancel);
			hbButtons.setAlignment(Pos.CENTER);
			setRight(hbPanes);
			setBottom(hbButtons);
			setPadding(new Insets(5, 10, 10, 10));
		}
	}
	class AccountPane extends BorderPane {
		MenuBar menuBar = new MenuBar();
		Menu menuAccount = new Menu("Account");
		MenuItem miEdit = new MenuItem("Edit Account");
		MenuItem miDelete = new MenuItem("Delete Account");
		MenuItem miSave = new MenuItem("Log Out");
		Menu menuBuy = new Menu("Buy Items");
		MenuItem miView = new MenuItem("View Accounts");
		Menu menuHelp = new Menu("Help");
		MenuItem miHelp = new MenuItem("Help");
		TabPane tbItems = new TabPane();
		VBox vbItems = new VBox(25);
		HBox hbLabels = new HBox(100);
		HBox hbText = new HBox(30);
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
			btRemove.getStyleClass().add("plainbutton");
			btAdd.getStyleClass().add("plainbutton");
			tfName.getStyleClass().add("plaintextfield");
			tfDescription.getStyleClass().add("plaintextfield");
			tfPrice.getStyleClass().add("plaintextfield");
			menuBar.getStyleClass().add("menubar");
			vbInfo.getStyleClass().add("infopane");
			tbItems.getStyleClass().add("tabpane");
			getStyleClass().add("mainpane");
			lbName.getStyleClass().add("largelabel");
			lbLocation.getStyleClass().add("plainlabel");
			lbBalance.getStyleClass().add("plainlabel");
			lbItemCount.getStyleClass().add("plainlabel");
			lbItemName.getStyleClass().add("plainlabel");
			lbItemDescription.getStyleClass().add("plainlabel");
			lbItemPrice.getStyleClass().add("plainlabel");
			vbItems.getStyleClass().add("infopane");
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
			lbName.setText(a.getAccountName());
			lbLocation.setText(a.getLocation());
			lbBalance.setText("Balance\n$" + a.getBalance() + "0");
			lbItemCount.setText("Items: " + a.getItemCount());
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
			getStyleClass().add("mainpane");
			lblName.getStyleClass().add("plainlabel");
			lblPass.getStyleClass().add("plainlabel");
			tfName.getStyleClass().add("plaintextfield");
			tfPass.getStyleClass().add("plaintextfield");
			btSubmit.getStyleClass().add("plainbutton");
			btCancel.getStyleClass().add("plainbutton");
			hbPanes.getStyleClass().add("infopane");
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
		}

	}
	class ItemTab extends Tab {
		TextField itemName = new TextField();
		TextArea description = new TextArea();
		TextField itemPrice = new TextField();
		VBox vb = new VBox(15);

		public ItemTab(Item i) {
			super(i.getItemName());
			itemName.setText(i.getItemName());
			itemName.setEditable(false);
			itemName.getStyleClass().add("plaintextfield");
			description.getStyleClass().add("plaintextfield");
			itemPrice.getStyleClass().add("plaintextfield");
			vb.getStyleClass().add("infopane");
			description.setText(i.getDescription());
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
		Label lbSave = new Label("Make sure to \nlog out to \nsave changes");
		TextField tfName = new TextField();
		TextField tfPass = new TextField();
		TextField tfBalance = new TextField();
		TextField tfLocation = new TextField();
		Button btEditName = new Button("Edit");
		Button btEditPass = new Button("Edit");
		Button btDeposit = new Button("Deposit");
		Button btWithdraw = new Button("Withdraw");
		Button btEditLocation = new Button("Edit");
		Button btCancel = new Button("Back");

		public EditAccount() {
			getStyleClass().add("mainpane");
			lbEdit.getStyleClass().add("largelabel");
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
			btDeposit.getStyleClass().add("plainbutton");
			btWithdraw.getStyleClass().add("plainbutton");
			btEditLocation.getStyleClass().add("plainbutton");
			btCancel.getStyleClass().add("plainbutton");
			vbName.getStyleClass().add("infopane");
			vbBalance.getStyleClass().add("infopane");
			setAlignment(lbEdit, Pos.CENTER);
			setTop(lbEdit);
			hb.setAlignment(Pos.CENTER);
			hb.getChildren().addAll(vbName, vbSave, vbBalance);
			vbName.setAlignment(Pos.CENTER);
			lbSave.setTextAlignment(TextAlignment.CENTER);
			vbName.getChildren().addAll(lbName, tfName, btEditName, lbPass, tfPass, btEditPass);
			vbBalance.setAlignment(Pos.CENTER);
			HBox hbMoney = new HBox(15);
			hbMoney.setAlignment(Pos.CENTER);
			hbMoney.getChildren().addAll(btDeposit, btWithdraw);
			vbBalance.getChildren().addAll(lbBalance, tfBalance, hbMoney,  lbLocation, tfLocation, btEditLocation);
			vbSave.setAlignment(Pos.CENTER);
			vbSave.getChildren().addAll(lbSave, btCancel);
			setCenter(hb);
		}
	}
	class ViewPane extends BorderPane {
		BorderPane bpAccounts = new BorderPane();
		VBox vbMain = new VBox(10);
		Label lbMName = new Label(mainAccount.getAccountName());
		Label lbMItemCount = new Label();
		Label lbMBalance = new Label();
		HBox hbAccounts = new HBox(10);
		HBox hbButtons = new HBox(10);
		Button btBuy = new Button("Buy Item");
		Button btCancel = new Button("Cancel");
		Pagination pag = new Pagination(accounts.size() - 1, 0);
		TableView<Item> tbvAccounts = new TableView<>();

		public ViewPane() {
			getStyleClass().add("mainpane");
			lbMName.getStyleClass().add("largelabel");
			lbMItemCount.getStyleClass().add("plainlabel");
			lbMBalance.getStyleClass().add("plainlabel");
			vbMain.getStyleClass().add("infopane");
			tbvAccounts.getStyleClass().add("tableview");
			setPadding(new Insets(15));
			hbButtons.getChildren().addAll(btBuy, btCancel);
			hbButtons.setAlignment(Pos.TOP_CENTER);
			setLeft(vbMain);
			setCenter(pag);
			vbMain.getChildren().addAll(lbMName, lbMItemCount, lbMBalance);
			vbMain.setAlignment(Pos.TOP_CENTER);
			vbMain.setPadding(new Insets(15));
		}
		public void setInfo() {
			lbMName.setText(mainAccount.getAccountName());
			lbMItemCount.setText("Items: " + mainAccount.getItemCount());
			lbMBalance.setText("Balance: $" + mainAccount.getBalance() + "0");
		}
		public void setPages() {
			pag.setPageFactory(new Callback<Integer, Node>() {
				public Node call(Integer pageIndex) {
					VBox vbAccount = new VBox(10);
					vbAccount.getStyleClass().add("infopane");
					Label lbName = new Label(accounts.get(pageIndex).getAccountName());
					lbName.getStyleClass().add("largelabel");
					Account a = accounts.get(pageIndex);
					Label lbItemCount = new Label("Items: " + accounts.get(pageIndex).getItemCount());
					lbItemCount.getStyleClass().add("plainlabel");
					vbAccount.getChildren().addAll(lbName, lbItemCount, tbvAccounts, hbButtons);
					vbAccount.setAlignment(Pos.TOP_CENTER);
					Account current = accounts.get(pageIndex);
					ObservableList<Item> itemList = FXCollections.observableArrayList(current.getItems());
					tbvAccounts.getColumns().clear();
					tbvAccounts.setMaxSize(439, 320);
					tbvAccounts.setItems(itemList);
					TableColumn nameCol = new TableColumn("Item Name");
					nameCol.setMinWidth(100);
					nameCol.setCellValueFactory(
							new PropertyValueFactory<Item, String>("itemName"));

					TableColumn descriptionCol = new TableColumn("Description");
					descriptionCol.setMinWidth(150);
					descriptionCol.setCellValueFactory(
							new PropertyValueFactory<Item, String>("description"));
					TextArea taDescription = new TextArea(descriptionCol.getText());

					TableColumn priceCol = new TableColumn("Item Price");
					priceCol.setMinWidth(100);
					priceCol.setCellValueFactory(
							new PropertyValueFactory<Item, Double>("itemPrice"));
					tbvAccounts.getColumns().addAll(nameCol, descriptionCol, priceCol);
					tbvAccounts.setOnMouseClicked(e -> {
						Item i = current.getItem(tbvAccounts.getSelectionModel().getSelectedIndex());
						ViewItemPane view = new ViewItemPane();
						view.setItem(i);
						Stage secondStage = new Stage();
						secondStage.setScene(new Scene(view, 300, 250));
						secondStage.getScene().getStylesheets().add("barter.css");
						secondStage.show();
						view.btBack.setOnAction(x -> secondStage.close());
					});
					return vbAccount;
				}
			});
		}
	}
	class HelpPane extends BorderPane {
		TabPane tabs = new TabPane();
		Button btBack = new Button("Back");
		Tab logInTab = new Tab("Log In");
		Tab createTab = new Tab("Create Account");
		Tab editTab = new Tab("Edit Account");
		Tab itemTab = new Tab("Add/Remove Items");
		Tab buyTab = new Tab("View Accounts/Buy Items");
		Tab deleteTab = new Tab("Delete Account");
		Tab logOutTab = new Tab("Log Out");

		public HelpPane() {
			getStyleClass().add("mainpane");
			setCenter(tabs);
			tabs.getTabs().addAll(logInTab, createTab, editTab, itemTab, buyTab, deleteTab, logOutTab);
			TextArea taLogIn = new TextArea("\t\t\t How to Log In\n\nStep 1: Run the Program\n\nStep 2: Enter Account Name/Account Password"
					+ "\n\nStep 3: Click Submit\n\n\n`````````````````````````````````````````````````````````````````````````\n\n"
					+ "[> If you have not created an account yet, Click the Create Account button\n\n[> Click Cancel button to exit");
			taLogIn.setEditable(false);
			taLogIn.setWrapText(true);
			taLogIn.getStyleClass().add("plaintextfield");
			logInTab.setContent(taLogIn);
			TextArea taCreate = new TextArea("\t\t\t How to Create an Account\n\nStep 1: Run Program\n\nStep 2: Click Create Account button"
					+ "\n\nStep 3: Enter Desired Account Name/Account Password\n\nStep 4: Click Submit button");
			taCreate.setEditable(false);
			taCreate.setWrapText(true);
			taCreate.getStyleClass().add("plaintextfield");
			createTab.setContent(taCreate);
			TextArea taEdit = new TextArea("\t\t\t How to Edit Account\n\nStep 1: Log in\n\nStep 2: Click Account on the top menu bar\n\n"
					+ "Step 3: Click Edit Account\n\nStep 4: Choose which data you want to change\n\nStep 5: Click Edit Button\n\n"
					+ "Step 6: Click Back Button\n\nStep 7: Click the Log Out button under the Account Menu Bar\n\n\n"
					+ "`````````````````````````````````````````````````````````````````````````\n\n[> Make sure to Log Out "
					+ "by clicking the log out menu button. If you do not click the log out button, nothing you edited will be saved. This includes "
					+ "[Items added or removed, Name, Password, Location, Balance]");
			taEdit.setEditable(false);
			taEdit.setWrapText(true);
			editTab.setContent(taEdit);
			taEdit.getStyleClass().add("plaintextfield");
			TextArea taItem = new TextArea("\t\t\t How to Add Items\n\nStep 1: Log In to Account\n\nStep 2: Enter Item information "
					+ "[Item Name, Description, and Price]\n\nStep 3: Click the Add Button\n\n\n\t\t\t How to Remove Items\n\nStep 1: "
					+ "Log In to Account\n\nStep 2: Select Whatever Item you want removed in your tab list on the account screen.\n\n"
					+ "Step 3: Click the Remove button\n\n\n`````````````````````````````````````````````````````````````````````````\n\n"
					+ "[> Make Sure to SAVE by clicking the Log Out menu button");
			taItem.setEditable(false);
			taItem.setWrapText(true);
			taItem.getStyleClass().add("plaintextfield");
			itemTab.setContent(taItem);
			TextArea taBuy = new TextArea("\t\t\t How to View Accounts/Buy Items\n\nStep 1: Log In to Account\n\nStep 2: Click View"
					+ " Accounts menu item, then click View\n\nStep 3: Choose which account to buy from, by selecting which page to look at"
					+ "\n\nStep 4: If you click an item, it will bring up a small screen with the full description. Click back to back out. If the item you "
					+ "want is selected, press the buy button \n\n\n"
					+ "`````````````````````````````````````````````````````````````````````````\n\n"
					+ "[> When buying an Item on the View Account Screen. It saves the data. So anything you have edited can be saved this way"
					+ " as well. Still make sure to log out by click the log out button.");
			taBuy.setEditable(false);
			taBuy.setWrapText(true);
			taBuy.getStyleClass().add("plaintextfield");
			buyTab.setContent(taBuy);
			TextArea taDelete = new TextArea("\t\t\t How to Delete Account\n\nStep 1: Log In to Account\n\nStep 2: Click Delete Account menu"
					+ " button, in Account menu\n\nStep 3: It will then verify you want to delete your account, You must click the YES button to"
					+ " completely remove account\n\n\n`````````````````````````````````````````````````````````````````````````\n\n"
					+ "[> If you click the cancel button. Your account will not be deleted. ");
			taDelete.setEditable(false);
			taDelete.setWrapText(true);
			taDelete.getStyleClass().add("plaintextfield");
			deleteTab.setContent(taDelete);
			TextArea taLogOut = new TextArea("\t\t\t How to Log Out/Save Accounts\n\nStep 1: Log In to Account\n\nStep 2: Edit account"
					+ " (Add/Remove Items, Edit Account Information, Buy Items from other Users) Step 3: You can Save and log out by clicking the"
					+ " Account menu Item, then clicking the Log Out button\n\n\n`````````````````````````````````````````````````````````````````````````"
					+ "\n\n[> When you buy items from another user the accounts get saved, still log out by clicking the Log out button in the Menu");
			taLogOut.setEditable(false);
			taLogOut.setWrapText(true);
			taLogOut.getStyleClass().add("plaintextfield");
			logOutTab.setContent(taLogOut);
			setBottom(btBack);
			setPadding(new Insets(10, 10, 10, 10));
			setAlignment(btBack, Pos.CENTER);
		}
	}
	class ViewItemPane extends BorderPane {
		VBox vb = new VBox(15);
		TextField tfName = new TextField();
		TextField tfPrice = new TextField();
		TextArea taDescription = new TextArea();
		Button btBack = new Button("Back");
		
		public ViewItemPane() {
			getStyleClass().add("mainpane");
			tfName.getStyleClass().add("plaintextfield");
			tfPrice.getStyleClass().add("plaintextfield");
			taDescription.getStyleClass().add("plaintextfield");
			setPadding(new Insets(10));
			vb.getChildren().addAll(tfName, tfPrice, taDescription, btBack);
			vb.setAlignment(Pos.CENTER);
			setCenter(vb);
			taDescription.setWrapText(true);
			taDescription.setPrefColumnCount(10);
			taDescription.setEditable(false);
			tfName.setEditable(false);
			tfName.setPrefColumnCount(10);
			tfPrice.setEditable(false);
			tfPrice.setPrefColumnCount(10);
			
		}
		public void setItem(Item i) {
			tfName.setText(i.getItemName());
			tfPrice.setText("$" + i.getItemPrice() + "0");
			taDescription.setText(i.getDescription());
		}
	}
}
