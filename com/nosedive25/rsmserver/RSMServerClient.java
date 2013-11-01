package com.nosedive25.rsmserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.UUID;

public class RSMServerClient {
	private Socket socket;
	private String game;
	private String clientID;
	private String playerID;
	
	public RSMServerClient(Socket s) {
		socket = s;
		clientID = UUID.randomUUID().toString();
		game = "-1";
		
		Runnable fetchClientData = new Runnable() {
			  public void run() {
				  while (true) {
					try {
						retreiveClientData();
					} catch (IOException e) {
						e.printStackTrace();
					}
				 }
			  }
			};
			
	    Thread fcd = new Thread(fetchClientData);
	    fcd.start();
	}
	
	private void retreiveClientData() throws IOException {
		if (!socket.isClosed()) {
			BufferedReader clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream serverOut = new DataOutputStream(socket.getOutputStream());
			String input = clientIn.readLine();
			
			if (input != null) {
				if (input.contains("REQUEST_SERVER_PROPERTIES")) {
					serverOut.writeBytes("SERVER_PROPERTIES:" + RSMServer.serverProperties().toString() + '\n');
				} else if (input.contains("REQUEST_ACTIVE_GAMES")) {
					serverOut.writeBytes("ACTIVE_GAMES:" + RSMServer.activeGames().toString() + '\n');
				} else if (input.contains("JOIN_GAME:")) {
					game = input.split(":")[1];
					serverOut.writeBytes("JOINED_GAME:" + input.split(":")[1] + '\n');
				} else if (input.contains("LEAVE_GAME")) {
					game = "-1";
					serverOut.writeBytes("LEFT_GAME:" + input.split(":")[1] + '\n');
				} else if (input.contains("SET_PLAYER_ID:")) {
					playerID = input.split(":")[1];
					System.out.println("User \"" + playerID + "\" has joined the server");
				} else if (input.contains("CREATE_GAME:")) {
					if (RSMServer.serverProperties().get("ClientsCanStartGames").equals("YES")) {
						RSMServer.newGame(input.split(":")[1].split(",")[0], input.split(":")[1].split(",")[1]);
					}
					
					System.out.println("User \"" + playerID + "\" created the game \"" + input.split(":")[1].split(",")[0] + "\"");
				} else {
					RSMServer.recivedClientData(clientID, input);
				}
			} else {
				socket.close();
				RSMServer.dropClient(clientID);
				
				System.out.println("User \"" + playerID + "\" has left the server");
			}
		}
	}
	
	public void setSocket(Socket s) {
		socket = s;
	}
	
	public void setGame(String g) {
		game = g;
	}
	
	public String game() {
		return game;
	}
	
	public Socket socket() {
		return socket;
	}
	
	public String clientID() {
		return clientID;
	}
	
	public String playerID() {
		return playerID;
	}
	
}