import java.util.*;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class BarterServer extends Application {
	List<Account> accounts = new ArrayList<>();
	ObjectOutputStream outputClient;
	ObjectInputStream inputClient;
	ObjectInputStream inputFile;
	ObjectOutputStream outputFile;
	ServerSocket server;
	Socket socket;
	Account clientAccount;
	// H 54 S 97 B 28 R 2 G 72 B 42
	@Override
	public void start(Stage stage) {
		try {
			server = new ServerSocket(8000);
			socket = server.accept();
			// Connection made
			inputFile = new ObjectInputStream(
					new DataInputStream(new FileInputStream("BData.dat")));
			
			// Method retrieves the accounts from file and stores it in an ArrayList
			accounts = retrieveList();
			sendList();
			inputClient = new ObjectInputStream(socket.getInputStream());
			outputClient = new ObjectOutputStream(socket.getOutputStream());

			System.out.println("Written Successfully");
			outputClient.writeObject(clientAccount);
			Object o = inputClient.readObject();
			clientAccount = (Account)o;
			accounts.add(clientAccount);
			outputFile = new ObjectOutputStream(
					new DataOutputStream(new FileOutputStream("BData.dat")));
			for (int i = 0; i < accounts.size(); i++) {
				outputFile.writeObject(accounts.get(i));
			}
			while (true) {
				Object st = inputClient.readObject();
				Account a = (Account)st;
				accounts.add(a);
			}
		}
		catch (EOFException of) {
			System.out.println(": )");
		}
		catch (IOException io) {
			io.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Account> retrieveList(){
		List<Account> result = new ArrayList<>();
		try {
			while(true) {
				result.add((Account)(inputFile.readObject()));
			}
		}
		catch (EOFException ex) {

		}
		catch (IOException io) {
			System.out.println("Retrieval Error");
		}
		catch (Exception ex) {

		}
		try {
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void saveAccounts() {
		try {
			outputFile = new ObjectOutputStream(
					new DataOutputStream(new FileOutputStream("BData.dat")));
			for (int i = 0; i < accounts.size(); i++) {
				outputFile.writeObject(accounts.get(i));
			}
			outputFile.close();
			System.out.println("Written Successfully");
		}
		catch (IOException io) {
			io.printStackTrace();
		}
	}
	public void getList() {
		try {
			while (true) {
				Object o = inputClient.readObject();
				Account a = (Account)o;
				accounts.add(a);
				inputClient.close();
			}
		}
		catch (IOException io) {
			io.printStackTrace();
		}
		catch (Exception p) {
			p.printStackTrace();
		}
		System.out.println(accounts.size());
	}
	public void sendList() {
		try {
			outputClient = new ObjectOutputStream(socket.getOutputStream());
			for (int i = 0; i < accounts.size(); i++) {
				outputClient.writeObject(accounts.get(i));
			}
			outputClient.close();
		}
		catch (IOException io) {
			io.printStackTrace();
		}
		accounts.clear();
	}
	class LogIn implements Runnable {
		public void run() {
			try {
				outputFile = new ObjectOutputStream(
						new DataOutputStream(new FileOutputStream("BData.dat")));
				String name = inputClient.readUTF();
				String pass = inputClient.readUTF();
				for (Account a: accounts) {
					if (name.equalsIgnoreCase(a.getAccountName())) {
						clientAccount = a;
					}	
				}
			}
			catch (IOException i) {
				i.printStackTrace();
			}
		}
	}
}
