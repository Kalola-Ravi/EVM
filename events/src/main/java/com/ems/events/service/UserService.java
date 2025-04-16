package com.ems.events.service;

import com.ems.events.entity.User;
import com.ems.events.exception.UserAlreadyExistsException;
import com.ems.events.exception.UserNotFoundException;
// import com.ems.events.repo.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    User createUser(User user) throws UserAlreadyExistsException;
    User login(String email, String password) throws UserNotFoundException;
    User updateUser(Long id, User updatedUser) throws UserNotFoundException, UserAlreadyExistsException;
    List<User> getAllUsers();
    User getUserById(Long userId) throws UserNotFoundException;
}
