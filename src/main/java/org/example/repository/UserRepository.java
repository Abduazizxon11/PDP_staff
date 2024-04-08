package org.example.repository;

import org.example.modul.BotState;
import org.example.modul.Role;
import org.example.modul.User;
import org.example.util.CustomDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final String selectAll = "SELECT * FROM users";
    private final String selectById = "SELECT * FROM users WHERE id=?";
    private final String insert = "INSERT INTO users (chat_id, full_name, role, bot_state) VALUES (?,?,?,?)";
    private final String update = "UPDATE users SET full_name=?, bot_state=? WHERE id=?";
    private final String delete = "DELETE FROM actor WHERE actor_id=?";

    public List<User> selectAll() {
        List<User> users = new ArrayList<>();

        try (Connection connection =
                     CustomDataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()

        ) {
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                User user = new User();
                user.setChatId(resultSet.getLong("chat_id"));
                user.setFullName(resultSet.getString("full_name"));
                user.setRole(Role.valueOf(resultSet.getString("role")));
                user.setState(BotState.valueOf(resultSet.getString("bot_state")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public User selectById(long id) {
        User user = new User();
        try (
                Connection connection = CustomDataSource.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(selectById, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setObject(1, id);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                user.setFullName(resultSet.getString("full_name"));
                user.setRole(Role.valueOf(resultSet.getString("role")));
                user.setId(resultSet.getLong("id"));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public void create(User user) {
        System.out.println(user.getRole());
        long id = 0;
        try (
                Connection connection = CustomDataSource.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setObject(1, user.getChatId());
            ps.setObject(2, user.getFullName());
            ps.setObject(3, String.valueOf(user.getRole()));
            ps.setObject(4, String.valueOf(user.getState()));
            int i = ps.executeUpdate();
            System.out.println(i);
//            ResultSet resultSet = ps.getGeneratedKeys();
//            if (resultSet.next()) {
//                id = resultSet.getLong(1);
//            }
            System.out.println(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User update(User user) {
        try (
                Connection connection = CustomDataSource.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(update, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setObject(1, user.getFullName());
            ps.setObject(2, user.getState());
            ps.setObject(3, user.getId());

            if (ps.executeUpdate() == 0) throw new SQLException("No user");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public User delete(long id) {
        try (
                Connection connection = CustomDataSource.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(delete, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setObject(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
