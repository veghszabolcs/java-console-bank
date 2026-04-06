package bank.service;

import bank.exception.AccountNotFoundException;
import bank.exception.AuthenticationServiceException;
import bank.exception.BusinessRuleViolationException;
import bank.model.Account;
import bank.model.User;
import bank.repository.AccountRepository;

import java.sql.SQLException;
import java.util.List;

public class AccountService {
    private AccountService(){};

    public static void createAccount(User user) throws AuthenticationServiceException {

        try{
            AccountRepository.createAccount(user.getId());
        }catch(SQLException e){
            throw new AuthenticationServiceException("Database error");
        }
    }

    public static Account accountExists(int id) throws AccountNotFoundException {
        try {
            return AccountRepository.getAccount(id);
        } catch (SQLException e) {
            throw new AccountNotFoundException("No such account");
        }
    }

    public static List<Account> getAllAccounts(User user) throws AuthenticationServiceException {
        try{
            return AccountRepository.getAccounts(user.getId());
        } catch (SQLException e) {
            throw new AuthenticationServiceException("Database error");
        }
    }

    public static void transferMoney(Account from, int toId, double amount) throws AuthenticationServiceException, AccountNotFoundException, BusinessRuleViolationException {

        if (amount <= 0) throw new IllegalArgumentException("Amount must greater than 0");

        if (from.getBalance()<amount) throw new BusinessRuleViolationException("Insufficient funds");

        //exists
        Account to = accountExists(toId);

        //transfer
        try{
            AccountRepository.changeBalance(from.getId(), -amount);
            AccountRepository.changeBalance(to.getId(), amount);
        }catch(SQLException e){
            throw new AuthenticationServiceException("Database error");
        }
    }

    public static void deleteAccount(int id) throws AccountNotFoundException, BusinessRuleViolationException, AuthenticationServiceException {
        try {
            Account acc = accountExists(id);
            if(acc == null ){
                throw new AccountNotFoundException("No such account");
            }
            if (acc.getBalance()!=0){
                throw new BusinessRuleViolationException("Balance must be zero to delete account");
            }
            AccountRepository.deleteAccount(id);
        } catch (SQLException e) {
            throw new AuthenticationServiceException("Server error");
        }
    }

    public static void withdrawMoney(Account account, double amount) throws AccountNotFoundException, AuthenticationServiceException, BusinessRuleViolationException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must greater than 0");

        if (account.getBalance()<amount) throw new BusinessRuleViolationException("Insufficient funds");

        try{
            AccountRepository.changeBalance(account.getId(), -amount);
        } catch (SQLException e) {
            throw new AuthenticationServiceException("Database error");
        }
    }

    public static void depositMoney(Account account, double amount) throws AccountNotFoundException, AuthenticationServiceException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must greater than 0");

        try{
            AccountRepository.changeBalance(account.getId(), amount);
        } catch (SQLException e) {
            throw new AuthenticationServiceException("Database error");
        }
    }

}
