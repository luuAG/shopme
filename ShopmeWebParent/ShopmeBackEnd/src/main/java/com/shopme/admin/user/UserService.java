package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
public class UserService {//DI = Dependency Injection
	public static final int USERS_PER_PAGE = 4;
	
	@Autowired private IUserRepository userRepo;
	
	@Autowired
	private IRoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<User> listAllUsers(){
		return (List<User>) userRepo.findAll();
	}
	
	public List<Role> listAllRoles(){
		
		return (List<Role>) roleRepo.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = user.getId() != null;
		
		if (isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();
			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			}
			else {
				encodePassword(user);
			}
		}
		else {
			encodePassword(user);
		}
		return userRepo.save(user);
	}
	
	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		User user = userRepo.getUserByEmail(email);
		if(user == null) return true;
		
		boolean isCreatingNew = (id==null);
		if (isCreatingNew) {
			if (user != null) return false;
		} else {
			if (user.getId() != id) {
				return false;
			}
		}
		
		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		}catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID="+id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		try {
			userRepo.deleteById(id);
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID="+id);
		}
	}
	
	public Page<User> listByPage(int pageNum, String keyword) {
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE);
		
		if (keyword != null) {
			return userRepo.findAll(keyword, pageable);
		}
		
		return userRepo.findAll(pageable);
	}
	
	public User getUserByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}
	
	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get();
		
		if (!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}
		
		if (userInForm.getPhoto() != null) {
			userInDB.setPhoto(userInForm.getPhoto());
		}
		
		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());
		
		return userRepo.save(userInDB);
	}

}
