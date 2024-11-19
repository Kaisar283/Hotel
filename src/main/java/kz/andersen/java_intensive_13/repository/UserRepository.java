package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.db_config.DataSource;
import kz.andersen.java_intensive_13.enums.UserRole;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Types.BIGINT;

public class UserRepository {

    public Optional<User> findUserById(long userId){
        String SQLQuery = String.format("""
                SELECT * FROM public."user" WHERE "user".id = '%d';
                """, userId);
        try(Connection connection = DataSource.getConnection();
            PreparedStatement pst = connection.prepareStatement(SQLQuery);
            ResultSet results = pst.executeQuery();
        ){
            User user = null;
            if (results.next()) {
                user = mapResultSetToUser(results);
            }
            return user == null ? Optional.empty() : Optional.of(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int saveUser(User user){
        String SQL_QUERY = """
                INSERT INTO public."user"(
                	id, first_name, last_name, user_role, created_at, updated_at)
                	VALUES (?, ?, ?, ?, ?, ?);
                """;
        try(Connection connection = DataSource.getConnection();
        ) {
            PreparedStatement pst = connection.prepareStatement(SQL_QUERY);
            pst.setLong(1, user.getId());
            pst.setString(2, user.getFistName());
            pst.setString(3, user.getLastName());
            pst.setString(4, user.getUserRole().toString());
            pst.setTimestamp(5, Timestamp.from(
                    ZonedDateTime.now(ZoneId.systemDefault()).toInstant()));
            pst.setTimestamp(6, Timestamp.from(
                    ZonedDateTime.now(ZoneId.systemDefault()).toInstant()));
            return pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAllUsers(){
        List<User> userList = new ArrayList<>();
        String SQLQuery = "SELECT * FROM public.\"user\";";
        try(Connection connection = DataSource.getConnection();
            PreparedStatement pst = connection.prepareStatement(SQLQuery);
            ResultSet results = pst.executeQuery();
        ){
            while (results.next()) {
                userList.add(mapResultSetToUser(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    private User mapResultSetToUser(ResultSet results) throws SQLException {
        User user = new User();
        user.setId(results.getLong(1));
        user.setFistName(results.getString(2));
        user.setLastName(results.getString(3));
        user.setUserRole(UserRole.valueOf(results.getString(4)));
        user.setCreatedAt(results.getTimestamp(5).toInstant().atZone(ZoneId.systemDefault()));
        user.setUpdatedAt(results.getTimestamp(6).toInstant().atZone(ZoneId.systemDefault()));
        return user;
    }
}
