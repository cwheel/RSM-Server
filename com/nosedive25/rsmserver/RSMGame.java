package com.nosedive25.rsmserver;

import java.util.UUID;

public class RSMGame {
	private String name;
	private String motd;
	private String id;

	public RSMGame(String n) {
		id = UUID.randomUUID().toString();
		name = n;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void setMotd(String m) {
		motd = m;
	}
	
	public String name() {
		return name;
	}
	
	public String motd() {
		return motd;
	}
	
	public void setID(String newID) {
		id = newID;
	}
	
	public String toString() {
		return "{name=" + name + ",motd=" + motd + ",id=" + id + "}";
	}
}
