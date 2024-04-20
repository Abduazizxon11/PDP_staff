package org.example.repository;

import org.example.modul.Stat;
import org.example.modul.User;
import org.example.util.CustomDataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatRepository {
    private final String selectAll =
            "SELECT * FROM stat";

    private final String insertStat =
            "INSERT INTO stat (chat_id, enter_time, keyword) VALUES(?,?,?)";

    public List<Stat> selectAllStat() {
        List<Stat> stats = new ArrayList<>();

        try (Connection connection =
                     CustomDataSource.getInstance().getConnection();
             Statement statement = connection.createStatement()

        ) {
            ResultSet resultSet = statement.executeQuery(selectAll);
            while (resultSet.next()) {
                Stat stat = new Stat();
                stat.setChatId(resultSet.getLong("chat_id"));
                stat.setEnterTime(resultSet.getTimestamp("enter_time").toLocalDateTime());
                stat.setKeyword(resultSet.getString("keyword"));


                stats.add(stat);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stats;
    }
    public boolean create(long chatId, String keyword) {
        try (
                Connection connection = CustomDataSource.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(insertStat)
        ) {
            ps.setLong(1,chatId);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3,keyword);
            return ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
