package com.nosedive25.rsmserver;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class RSMServer {
	private static SSLServerSocket connSocket;
    private static ArrayList<RSMServerClient> clients = new ArrayList<RSMServerClient>();
    private static ArrayList<RSMGame> games = new ArrayList<RSMGame>();
    private static Hashtable<String, String> props = new  Hashtable<String, String>();
    
    private static KeyStore keyStore;
    private static TrustManagerFactory tm;
    private static KeyManagerFactory km;
    private static String keyPass;
    
	public static void main(String argv[]) throws Exception {
		File propsFile = new File("server.props");
		if (propsFile.exists()) { //TODO: Fix really bad properties system
			 FileInputStream fs = new FileInputStream("server.props");
			 DataInputStream in = new DataInputStream(fs);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 props = stringToHashtable(br.readLine());
			 games = gamesFromServerString(br.readLine()); 
			 keyPass = br.readLine();
		} else {
			addServerProperty("ServerName","DebugServer");
            addServerProperty("ClientsCanStartGames","NO");
		}

    	SecureRandom sr = new SecureRandom();
    	sr.nextInt();
    	
        keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("serverkeystore"), keyPass.toCharArray());
        tm = TrustManagerFactory.getInstance("SunX509");
        tm.init(keyStore);
        km = KeyManagerFactory.getInstance("SunX509");
        km.init(keyStore, keyPass.toCharArray());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(km.getKeyManagers(), tm.getTrustManagers(), sr);
        SSLServerSocketFactory sslserversocketfactory = sslContext.getServerSocketFactory();
        connSocket = (SSLServerSocket)sslserversocketfactory.createServerSocket(34674);
	
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
	        	SSLSocket socket = (SSLSocket)connSocket.accept();
	        	
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
	 
	 private static ArrayList<RSMGame> gamesFromServerString(String serverString) {
			ArrayList<RSMGame> serverGames = new ArrayList<RSMGame>();
			serverString = serverString.replace("{", "").replace("[", "").replace("]", "").replace("ACTIVE_GAMES:", "");
			String[] gameObjectStrings = serverString.split("},");
			
			for (int i = 0; i < gameObjectStrings.length; i++) {
				String[] gameObjectValues = gameObjectStrings[i].split(",");
				RSMGame newGame = new RSMGame(gameObjectValues[0].split("=")[1]);
				newGame.setMotd(gameObjectValues[1].split("=")[1].replace("}", ""));
				newGame.setID(gameObjectValues[2].split("=")[1]);
				
				serverGames.add(newGame);
			}
			
			return serverGames;
		}
	 
	 public static Hashtable<String, String> serverProperties() {
		 return props;
	 }
	 
	 public static String serverName() {
		 return props.get("ServerName");
	 }
	 
	 public static ArrayList<RSMGame> activeGames() {
		 return games;
	 }
	 
	 private static Hashtable<String, String> stringToHashtable(String hashstring) {
			hashstring = hashstring.replace("{", "").replace("}", "");
			String[] elements = hashstring.split(",");
			Hashtable<String, String> newTable = new Hashtable<String, String>();
			
			for (int i = 0; i < elements.length; i++) {
				String key = elements[i].split("=")[0];
				String value = elements[i].split("=")[1];
				
				newTable.put(key, value);
			}
			
			return newTable;
	 }
	 
	 private static RSMServerClient getClientWithID(String id) { //TODO: Implement a more efficient search
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