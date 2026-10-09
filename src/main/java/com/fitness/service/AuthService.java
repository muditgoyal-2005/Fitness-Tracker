package com.fitness.service;

import com.fitness.dao.UserDAO;
import com.fitness.enums.Role;
import com.fitness.exception.DuplicateEmailException;
import com.fitness.exception.InvalidCredentialsException;
import com.fitness.model.User;
import com.fitness.util.PasswordUtil;
import com.fitness.util.ValidationUtil;

public class AuthService {
    private final UserDAO users;
    public AuthService(UserDAO users){this.users=users;}
    public User login(String email,String password)throws InvalidCredentialsException{
        return users.findByEmail(email==null?"":email.trim().toLowerCase()).filter(u->PasswordUtil.verify(password,u.getPasswordHash())).orElseThrow(InvalidCredentialsException::new);
    }
    public User register(String name,String email,String password)throws DuplicateEmailException{
        String normalized=ValidationUtil.required(email,"Email",190).toLowerCase();
        if(!ValidationUtil.validEmail(normalized))throw new IllegalArgumentException("Enter a valid email address.");
        if(!ValidationUtil.validPassword(password))throw new IllegalArgumentException("Password must be 8-72 characters with uppercase, lowercase, and a number.");
        if(users.findByEmail(normalized).isPresent())throw new DuplicateEmailException();
        User user=new User();user.setName(ValidationUtil.required(name,"Name",100));user.setEmail(normalized);user.setPasswordHash(PasswordUtil.hash(password));user.setWeightKg(70);user.setHeightCm(170);user.setRole(Role.USER);return users.save(user);
    }
}