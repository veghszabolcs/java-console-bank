package bank.repository;

import bank.db.DatabaseConnection;
import bank.exception.AccountNotFoundException;
import bank.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    private static final Connection connection;

    static{
        try{
            connection = DatabaseConnection.getConnection();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static List<Account> getAccounts(int userId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM accounts WHERE user_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, userId);
            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    accounts.add(new Account(resultSet.getInt("id"),  resultSet.getFloat("balance"), resultSet.getInt("user_id")));
                }
                return accounts;
            }
        }
    }

    public static Account getAccount(int accountId) throws SQLException {
        String query = "SELECT * FROM accounts WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, accountId);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return new Account(resultSet.getInt("id"), resultSet.getFloat("balance"), resultSet.getInt("user_id"));
                }
            }
        }
        return null;
    }

    public static void changeBalance(int id, double amount) throws SQLException, AccountNotFoundException {
        String query = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setDouble(1, amount);
            statement.setInt(2, id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0){
                throw new AccountNotFoundException("No such account");
            }
        }
    }

    public static void createAccount(int userId) throws SQLException {
        String query = "INSERT INTO accounts (user_id, balance) VALUES(?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, userId);
            statement.setFloat(2,0.0f);
            statement.executeUpdate();
        }
    }

    public static void deleteAccount(int id) throws SQLException {
        String query = "DELETE FROM accounts WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

}
