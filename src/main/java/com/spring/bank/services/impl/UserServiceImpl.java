package com.spring.bank.services.impl;

import com.spring.bank.entities.Transaction;
import com.spring.bank.entities.User;
import com.spring.bank.enums.Role;
import com.spring.bank.enums.Status;
import com.spring.bank.repositories.UserRepo;
import com.spring.bank.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Override
    public User createUser(User user) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoderPass = encoder.encode(user.getPassword());
        user.setPassword(encoderPass);
        LocalDate date = LocalDate.now();
        user.setCreatedAt(date);
        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User getUserById(Integer userId) {
        return userRepo.findByid(userId);
    }

    @Override
    public User checkLogin(User user) {
        Optional<User> logUser = getUserByUsername(user.getUsername());
        if (logUser.isPresent()) {
            String userPassword = Base64.getEncoder().encodeToString(user.getPassword().getBytes());
            if (userPassword.equals(logUser.get().getPassword())) {
                // success
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Set<Transaction> getUserHistory(Integer userId) {
        User user = getUserById(userId);
        if (user.getTransactions() == null) {
            return null;
        } else {
            Set<Transaction> set = user.getTransactions();
            for (Transaction tr : set){
                tr.setUser(null);
            }
            return set;
        }
    }

    @Override
    public User changeRoleOfUser(Integer loggedId, User toBeChangedUser) {
        User loggedUser = userRepo.findByid(loggedId);
        if (loggedUser.getRole().name().equals("ADMIN")) {
            Optional<User> userToChangeRole = userRepo.findByUsername(toBeChangedUser.getUsername());
            if (userToChangeRole.isPresent()) {
                userToChangeRole.get().setRole(toBeChangedUser.getRole());
                userRepo.save(userToChangeRole.get());
                return userToChangeRole.get();
            } else {
                return null;
            }
        }
        else{
            return null;
        }
    }
    @Override
    public User loggedInUser(Authentication authentication) {
        String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
        return userRepo.findByUsername(username).get();
    }
}
