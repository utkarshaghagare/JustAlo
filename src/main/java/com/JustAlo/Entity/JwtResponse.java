package com.JustAlo.Entity;

public class JwtResponse {

	private String email;

	//private Vendor vendor;
	private String jwtToken;

	public JwtResponse(String email, String jwtToken) {
		this.email = email;
		this.jwtToken = jwtToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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