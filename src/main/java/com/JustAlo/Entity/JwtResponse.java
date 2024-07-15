package com.JustAlo.Entity;

public class JwtResponse {

	private String username;

	//private Vendor vendor;
	private String jwtToken;

	public JwtResponse(String username, String jwtToken) {
		this.username = username;
		this.jwtToken = jwtToken;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}
}


//package com.BusBookingbackend.entity;
//
//public class JwtResponse {
//
//	private User user;
//	private String jwtToken;
//	public JwtResponse(User user, String jwtToken) {
//		super();
//		this.user = user;
//		this.jwtToken = jwtToken;
//	}
//	public User getUser() {
//		return user;
//	}
//	public void setUser(User user) {
//		this.user = user;
//	}
//	public String getJwtToken() {
//		return jwtToken;
//	}
//	public void setJwtToken(String jwtToken) {
//		this.jwtToken = jwtToken;
//	}
//
//
//
//
//}