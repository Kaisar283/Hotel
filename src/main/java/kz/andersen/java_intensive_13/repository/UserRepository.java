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
                user = new User();
                user.setId(results.getLong(1));
                user.setFistName(results.getString(2));
                user.setLastName(results.getString(3));
                user.setUserRole(UserRole.valueOf(results.getString(4)));
                user.setCreatedAt(results.getTimestamp(5).toInstant().atZone(ZoneId.systemDefault()));
                user.setUpdatedAt(results.getTimestamp(6).toInstant().atZone(ZoneId.systemDefault()));
            }
            return user == null ? Optional.empty() : Optional.of(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int saveUser(User user){
        long userId = user.getId();
        String firstName = user.getFistName();
        String lastName = user.getLastName();
        String userRole = user.getUserRole().toString();
        ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime updatedAt = ZonedDateTime.now(ZoneId.systemDefault());

        String SQL_QUERY = """
                INSERT INTO public."user"(
                	id, first_name, last_name, user_role, created_at, updated_at)
                	VALUES (?, ?, ?, ?, ?, ?);
                """;
        try(Connection connection = DataSource.getConnection();
        ) {
            PreparedStatement pst = connection.prepareStatement(SQL_QUERY);
            pst.setLong(1, userId);
            pst.setString(2, firstName);
            pst.setString(3, lastName);
            pst.setString(4, userRole);
            pst.setTimestamp(5, Timestamp.from(createdAt.toInstant()));
            pst.setTimestamp(6, Timestamp.from(updatedAt.toInstant()));
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
            User user = null;
            while (results.next()) {
                user = new User();
                user.setId(results.getLong(1));
                user.setFistName(results.getString(2));
                user.setLastName(results.getString(3));
                user.setUserRole(UserRole.valueOf(results.getString(4)));
                user.setCreatedAt(results.getTimestamp(5).toInstant().atZone(ZoneId.systemDefault()));
                user.setUpdatedAt(results.getTimestamp(6).toInstant().atZone(ZoneId.systemDefault()));
                userList.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }
}
