package com.JustAlo.Service;


import com.JustAlo.Entity.JwtResponse;
import com.JustAlo.Entity.Role;
import com.JustAlo.Entity.User;
import com.JustAlo.Exception.InvalidOtpException;
import com.JustAlo.Security.JwtHelper;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

	
	  @Autowired
	    private UserDao userDao;

	    @Autowired
	    private RoleDao roleDao;

	    @Autowired
	    private PasswordEncoder passwordEncoder;
		@Autowired
		private OtpService otpService;


	@Autowired
	private JwtHelper jwtHelper;
	    public void initRoleAndUser() {
//
	        Role adminRole = new Role();
	        adminRole.setRoleName("Admin");
	        adminRole.setRoleDescription("Admin role");
			boolean a= roleDao.existsById("Admin");
			if(!a){
				roleDao.save(adminRole);
			}


	        Role userRole = new Role();
	        userRole.setRoleName("User");
	        userRole.setRoleDescription("Default role for newly created record");
	        roleDao.save(userRole);

			Role vendorRole = new Role();
			vendorRole.setRoleName("Vendor");
			vendorRole.setRoleDescription("Default role for newly created vendor");
			roleDao.save(userRole);

			Role driverRole = new Role();
			driverRole.setRoleName("Driver");
			driverRole.setRoleDescription("Default role for newly created Driver");
			roleDao.save(driverRole);



	    }

	    public User registerNewUser(User user) {
	        Role role = roleDao.findById("User").get();
	        Set<Role> userRoles = new HashSet<>();
	        userRoles.add(role);
	        user.setRole(userRoles);
	        user.setPassword(getEncodedPassword(user.getPassword()));

	        return userDao.save(user);
	    }

	    public String getEncodedPassword(String password) {
	        return passwordEncoder.encode(password);
	    }

	public User registerAdmin(User user) {
		Role role = roleDao.findById("Admin").get();
		Set<Role> userRoles = new HashSet<>();
		userRoles.add(role);
		user.setRole(userRoles);
		user.setPassword(getEncodedPassword(user.getPassword()));

		return userDao.save(user);
	}


    public List<User> getAllUser() {
			return userDao.findAll();
    }

	public User updateUser(Long id, User user) {
			Optional<User> user1= userDao.findById(id);
			if(user1.isPresent()){
				return userDao.save(user);
			}
			return null;
	}




	public User findByEmail(String email) {

			return  userDao.findByEmail(email);
	}
	public void save(User user) {
		userDao.save(user);
	}

	public JwtResponse validateOtp(String email, String otp) {
		if (!otpService.validateOtp(email, otp)) {
			throw new InvalidOtpException("Invalid OTP");
		}

		User user = userDao.findByEmail(email);
		if (user == null) {
			user = new User();
			user.setEmail(email);

			// Assign a default role

           User user1 =new User();
			Role role = roleDao.findById("User")
					.orElseThrow(() -> new RuntimeException("Role not found"));
			Set<Role> userRoles = new HashSet<>();
			userRoles.add(role);
			user1.setRole(userRoles);
			userDao.save(user);
		}

		// Generate JWT token
		String token = jwtHelper.generateToken(email);
	    String username = jwtHelper.getEmailFromToken(token);
		return new JwtResponse(token, username);
	}
}
