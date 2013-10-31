package com.nosedive25.rsmserver;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class RSMServer {
	private static ServerSocket connSocket;
    private static ArrayList<RSMServerClient> clients = new ArrayList<RSMServerClient>();
    private static ArrayList<RSMGame> games = new ArrayList<RSMGame>();
    private static Hashtable<String, String> props = new  Hashtable<String, String>();
    
	public static void main(String argv[]) throws Exception {
		connSocket = new ServerSocket(34674);
        Runnable clientAcceptor = new Runnable() {
			  public void run() {
				 try {
					acceptClients();
				} catch (IOException e) {
					e.printStackTrace();
				}
			  }
			};
		 
		 Thread ca = new Thread(clientAcceptor);
		 ca.start();
		 
		 /*
		  * Debugging
		  */
		 addServerProperty("ServerName","DebugServer");
		 addServerProperty("ClientsCanStartGames","YES");
		 
		 newGame("TestRoom", "Test motd");
		 newGame("TestRoom2", "Test motd2");
	 }
	
	public static void newGame(String name, String motd) {
		RSMGame newGame = new RSMGame(name);
		newGame.setMotd(motd);
		
		games.add(newGame);
	}
	
	public static void addServerProperty(String property, String value) {
		props.put(property, value);
	}
	
	 public static void acceptClients() throws IOException {
		 while(true) {
	        	Socket socket = connSocket.accept();
	        	
	        	RSMServerClient newClient = new RSMServerClient(socket); //TODO: Set additional info about client
	        	clients.add(newClient);
	        }
	 }
	 
	 public static void recivedClientData(String clientID, String data) throws IOException {
		 String gameGroup = getClientWithID(clientID).game();
		 
		 for (RSMServerClient client : clients) {
			 if (client.game().equals(gameGroup)) {
				 DataOutputStream clientOut = new DataOutputStream(client.socket().getOutputStream());
				 
				 if (!data.contains("GAME_CMD:")) {
					 clientOut.writeBytes("PLAYER_UPDATE(" + client.playerID() + "):" + data.replace("PLAYER_UPDATE:", "") + '\n');
				 } else {
					 clientOut.writeBytes("GAME_CMD(" + client.playerID() + "):" + data.replace("GAME_CMD:", "") + '\n'); 
				 }
			 }
		 }
	 }
	 
	 public static String serverProperties() {
		 return props.toString();
	 }
	 
	 public static ArrayList<RSMGame> activeGames() {
		 return games;
	 }
	 
	 private static RSMServerClient getClientWithID(String id) {
		 for (RSMServerClient client: clients) {
			 if (id.contains(client.clientID())){
				 return client;
			 }
		 }
		 return null;
	 }
	 
	 public static void dropClient(String id) {
		 RSMServerClient client = getClientWithID(id);
		 clients.remove(client);
		
		 client = null;
	 }
}