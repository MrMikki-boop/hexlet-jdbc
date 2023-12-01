package io.hexlet;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws SQLException {
        try (var conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test")) {
            var userDAO = new UserDAO(conn);

            var alice = new User("Alice", "111111111");
            userDAO.save(alice);

            var bob = new User("Bob", "222222222");
            userDAO.save(bob);

            var charlie = new User("Charlie", "333333333");
            userDAO.save(charlie);

            userDAO.findAll().forEach(user -> {
                System.out.println(user.getUsername());
                System.out.println(user.getPhone());
            });

            var foundUser = userDAO.findById(bob.getId());
            foundUser.ifPresent(user -> {
                System.out.println("Found user:");
                System.out.println(user.getUsername());
                System.out.println(user.getPhone());
            });

            userDAO.delete(bob.getId());
        }
    }
}
