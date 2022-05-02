# Barter
## Motivation
I chose this program as my capstone project because it is an application multiple users can use at the same time. I want to write programs that help make life easier for clients. This program allows a user to add items they want to sell, and view other accounts to buy their items.
## Hot to Run
Download all the files from this repository. Run the Barter.java. You can create an account, edit that account. Add items, remove items. View Accounts to buy items. Save and log out. The only exception you will get is if you try to view accounts twice, without logging out before the second time. It is a very easy, user-friendly application.
![This is a screenshot of hte program](https://github.com/KDrake80/Capstone/blob/main/barterScreenShot.png)
## Code Snippet
I chose this portion of the code because it was the hardest part of the whole program. I was having issues with the tableview inside the pagination. I got it working everything is smooth now
```
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
```
## Tests
I have performed many tests throughout this program, from test Accounts, to test Items. I have no ran any JUnit Tests. However, this program and been tested over and over until I have gotten this exact version. I am very happy with it, as everything was made from scratch.
## Contributors
[Kevin Drake](Gihub.com/KDrake80)