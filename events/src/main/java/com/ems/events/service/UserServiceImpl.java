package com.ems.events.service;


import com.ems.events.entity.User;
import com.ems.events.exception.UserAlreadyExistsException;
import com.ems.events.exception.UserNotFoundException;

import com.ems.events.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Existing methods remain unchanged

    /**
     * Create a new user with the
     */
    public User createUserWithRole(User user, String roleName) {
        // Check if the email or contact number already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }
        if (userRepository.existsByContactNumber(user.getContactNumber())) {
            throw new UserAlreadyExistsException("Contact number is already registered");
        }

        // Fetch the role from the database
      

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user
        return userRepository.save(user);
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }
        if (userRepository.existsByContactNumber(user.getContactNumber())) {
            throw new UserAlreadyExistsException("Contact number is already registered");
        }
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Email not found"));

        if (!user.getPassword().equals(password)) {
            throw new UserNotFoundException("Incorrect password");
        }
        return user;
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check for email uniqueness
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        // Check for contact number uniqueness
        if (!existingUser.getContactNumber().equals(updatedUser.getContactNumber()) &&
                userRepository.existsByContactNumber(updatedUser.getContactNumber())) {
            throw new UserAlreadyExistsException("Contact number is already registered");
        }

        // Update user details
        existingUser.setUserName(updatedUser.getUserName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setContactNumber(updatedUser.getContactNumber());

        // Check if the password is being updated
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            // Encode the new password before saving
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Save the updated user
        return userRepository.save(existingUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
