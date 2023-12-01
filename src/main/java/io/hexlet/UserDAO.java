package io.hexlet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection conn) {
        connection = conn;
    }

    public void save(User user) throws SQLException {
        if (user.getId() == null) {
            var sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
            try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPhone());
                preparedStatement.executeUpdate();
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("DB have not returned an id after saving an entity");
                }
            }
        } else {
            var sql = "UPDATE users SET username = ?, phone = ? WHERE id = ?";
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPhone());
                preparedStatement.setLong(3, user.getId());
                preparedStatement.executeUpdate();
            }
        }
    }

    public Optional<User> findById(Long id) throws SQLException {
        var sql = "SELECT * FROM users WHERE id = ?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var username = resultSet.getString("username");
                var phone = resultSet.getString("phone");
                var user = new User(username, phone);
                user.setId(id);
                return Optional.of(user);
            }
            return Optional.empty();
        }
    }

    public List<User> findAll() throws SQLException {
        var sql = "SELECT * FROM users";
        try (var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var username = resultSet.getString("username");
                var phone = resultSet.getString("phone");
                var user = new User(username, phone);
                user.setId(id);
                users.add(user);
            }
            return users;
        }
    }

    public void delete(Long id) throws SQLException {
        var sql = "DELETE FROM users WHERE id = ?";
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
