package bank.service;

import bank.exception.AuthenticationServiceException;
import bank.exception.InvalidCredentialsException;
import bank.exception.UserAlreadyExistsException;
import bank.model.User;
import bank.repository.UserRepository;
import bank.util.PasswordHasher;

import java.sql.SQLException;

public class UserService {

    private UserService(){};

    public static void createUser(String email, String password) throws AuthenticationServiceException, UserAlreadyExistsException {
        try{
            UserRepository.createUser(email, password);
        } catch (SQLException e) {
            throw new AuthenticationServiceException("Could not create user");
        }
    }

    public static User loginUser(String email, String password) throws InvalidCredentialsException, AuthenticationServiceException {
        try{
            User foundUser = UserRepository.getUserByEmail(email);

            if (foundUser == null || !PasswordHasher.checkPassword(password, foundUser.getPasswordHash())){
                throw new InvalidCredentialsException("Wrong email or password");
            }

            return foundUser;
        } catch (SQLException e) {
            throw new AuthenticationServiceException("Database error");
        }
    }


}
