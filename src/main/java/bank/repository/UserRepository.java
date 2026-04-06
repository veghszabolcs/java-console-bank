package bank.repository;

import bank.db.DatabaseConnection;
import bank.exception.UserAlreadyExistsException;
import bank.model.*;
import bank.util.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    private static final Connection connection;

    static {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User getUserById(int id) throws SQLException {
        String query = "SELECT id, email, password_hash FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(resultSet.getInt("id"), resultSet.getString("email"), resultSet.getString("password_hash"));
                }
                return null;
            }
        }
    }

    public static User getUserByEmail(String email) throws SQLException {
        String query = "SELECT id, email, password_hash FROM users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(resultSet.getInt("id"), resultSet.getString("email"), resultSet.getString("password_hash"));
                }
                return null;
            }
        }
    }

    public static void createUser(String email, String password) throws SQLException, UserAlreadyExistsException {

        if (getUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        String passwordHash = PasswordHasher.hashPassword(password);

        String query = "INSERT INTO users (email, password_hash) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, passwordHash);
            statement.executeUpdate();
        }
    }

    public static void updateUser(User user) throws SQLException {
        String query = "UPDATE users SET email = ?, password_hash = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPasswordHash());
            statement.setInt(3, user.getId());
            statement.executeUpdate();
        }
    }

    public static void deleteUser(User user) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        }
    }

}
