package org.example.repository;

import org.example.UserDTO;
import org.example.modul.BotState;
import org.example.modul.User;
import org.example.util.CustomDataConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final String selectByChatId =
            "SELECT * FROM botusers WHERE chat_id=?";

    private final String insertChatId =
            "INSERT INTO botusers (chat_id,role,bot_state) VALUES (?,?,?)";

    private final String updateFullName =
            "UPDATE botusers SET full_name=? WHERE chat_id=?";

    private final String updateState =
            "UPDATE botusers SET bot_state=? WHERE chat_id=?";

    private final String selectWithStat =
            "SELECT full_name, enter_time FROM botusers INNER JOIN stat ON botusers.chat_id = stat.chat_id WHERE date(stat.enter_time)=DATE(NOW())";
    public List<UserDTO> selectWithStat(){
        List<UserDTO> users = new ArrayList<>();

        try (Connection connection =
                     CustomDataConnector.getInstance().getConnection();
             Statement statement = connection.createStatement()

        ) {
            ResultSet resultSet = statement.executeQuery(selectWithStat);
            while (resultSet.next()) {
                UserDTO user = new UserDTO();
                user.setFullName(resultSet.getString("full_name"));
                user.setEnterTime(resultSet.getTimestamp("enter_time").toString());
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public void updateState(long chatId, BotState state){
        try (
                Connection connection = CustomDataConnector.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(updateState)
        ){
            ps.setString(1, state.name());
            ps.setLong(2, chatId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insertChatIdAndState(long chatId) {
        try (
                Connection connection = CustomDataConnector.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(insertChatId)
        ) {
            ps.setLong(1, chatId);
            ps.setString(2, "user");
            ps.setString(3, BotState.PDP_START.name());
            return ps.execute();
        } catch (SQLException e) {
            return false;
        }
    }

    public User selectByChatId(long chatId) {
        User user = new User();
        try (
                Connection con = CustomDataConnector.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(selectByChatId)
        ) {
            ps.setLong(1, chatId);
            ResultSet resultSet = ps.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            user.setFullName(resultSet.getString("full_name"));
            user.setBotState(BotState.valueOf(resultSet.getString("bot_state")));
            user.setRole(resultSet.getString("role"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
    public User updateFullName(long chatId, String fullName) {
        try (
                Connection connection = CustomDataConnector.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(updateFullName)
        ){
            ps.setObject(1, fullName);
            ps.setObject(2, chatId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
