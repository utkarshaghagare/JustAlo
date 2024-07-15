package com.JustAlo.Service;


import com.JustAlo.Entity.Role;
import com.JustAlo.Entity.User;
import com.JustAlo.JwtHelper;
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
			userRole.setRoleName("Vendor");
			userRole.setRoleDescription("Default role for newly created vendor");
			roleDao.save(userRole);

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


}
