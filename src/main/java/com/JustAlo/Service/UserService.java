package com.JustAlo.Service;


import com.JustAlo.Entity.Admin;
import com.JustAlo.Entity.JwtResponse;
import com.JustAlo.Entity.Role;
import com.JustAlo.Entity.User;
import com.JustAlo.Exception.InvalidOtpException;
import com.JustAlo.Model.enums.UserStatus;
import com.JustAlo.Repo.AdminRepository;
import com.JustAlo.Security.JwtHelper;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
		private AdminRepository adminDao;

	@Autowired
	private JwtHelper jwtHelper;
	    public void initRoleAndUser() {


			// Create roles
			if (!roleDao.existsById("Admin")) {
				Role adminRole = new Role();
				adminRole.setRoleName("Admin");
				adminRole.setRoleDescription("Admin role");
				roleDao.save(adminRole);
			}

			if (!roleDao.existsById("User")) {
				Role userRole = new Role();
				userRole.setRoleName("User");
				userRole.setRoleDescription("Default role for newly created User");
				roleDao.save(userRole);
			}

			if (!roleDao.existsById("Vendor")) {
				Role vendorRole = new Role();
				vendorRole.setRoleName("Vendor");
				vendorRole.setRoleDescription("Default role for newly created Vendor");
				roleDao.save(vendorRole);
			}

			if (!roleDao.existsById("Driver")) {
				Role driverRole = new Role();
				driverRole.setRoleName("Driver");
				driverRole.setRoleDescription("Default role for newly created Driver");
				roleDao.save(driverRole);
			}




	    }

//	    public User registerNewUser(User user) {
//	        Role role = roleDao.findById("User").get();
//	        Set<Role> userRoles = new HashSet<>();
//	        userRoles.add(role);
//	        user.setRole(userRoles);
//	        user.setPassword(getEncodedPassword(user.getPassword()));
//
//	        return userDao.save(user);
//	    }

	    public String getEncodedPassword(String password) {
	        return passwordEncoder.encode(password);
	    }

	public Admin registerAdmin(Admin admin) {
//		admin.setName("Admin Name");  // Static name
//		admin.setEmail("admin@example.com");  // Static email
//		admin.setPassword(getEncodedPassword("admin@password"));  // Static password, encoded

		// Assign the "Admin" role to the user
		Role role = roleDao.findById("Admin").get();
		Set<Role> userRoles = new HashSet<>();
		userRoles.add(role);
		admin.setRole(userRoles);
		admin.setPassword(getEncodedPassword(admin.getPassword()));

		adminDao.save(admin);
        return admin;
    }


//		Role role = roleDao.findById("Admin").get();
//		Set<Role> userRoles = new HashSet<>();
//		userRoles.add(role);
//		user.setRole(userRoles);
//		user.setPassword(getEncodedPassword(user.getPassword()));
//
//		return userDao.save(user);
	//}


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

	public void deleteUser(Long id) {
			userDao.delete(userDao.findById(id).orElse(null));
	}

	public User blockUser(Long id) {
		Optional<User> optionalUser = userDao.findById(id);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setStatus(UserStatus.BLOCKED);
			userDao.save(user);
			return user;
		} else {
			return null; // User not found, return null or handle it as needed
		}
	}


	public User unblockUser(Long id) {
		Optional<User> optionalUser = userDao.findById(id);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setStatus(UserStatus.ACTIVE); // Assuming ACTIVE is the status for an unblocked user
			userDao.save(user);
			return user;
		} else {
			return null; // User not found, return null or handle it as needed
		}
	}

}
