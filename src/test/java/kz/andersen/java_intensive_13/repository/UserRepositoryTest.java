package kz.andersen.java_intensive_13.repository;

import static org.junit.jupiter.api.Assertions.*;

import kz.andersen.java_intensive_13.db_config.DataSource;
import kz.andersen.java_intensive_13.enums.UserRole;
import kz.andersen.java_intensive_13.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findUserById() throws SQLException {
        try(MockedStatic<DataSource> mockedStatic = mockStatic(DataSource.class)) {
            mockedStatic.when(DataSource::getConnection).thenReturn(connection);

            long userId = 1L;
            User mockUser = new User();
            mockUser.setId(userId);
            mockUser.setFistName("Rock");
            mockUser.setLastName("Johnson");
            mockUser.setUserRole(UserRole.ADMIN);
            mockUser.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
            mockUser.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()));

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getLong(1)).thenReturn(mockUser.getId());
            when(resultSet.getString(2)).thenReturn(mockUser.getFistName());
            when(resultSet.getString(3)).thenReturn(mockUser.getLastName());
            when(resultSet.getString(4)).thenReturn(mockUser.getUserRole().toString());
            when(resultSet.getTimestamp(5)).thenReturn(Timestamp.from(mockUser.getCreatedAt().toInstant()));
            when(resultSet.getTimestamp(6)).thenReturn(Timestamp.from(mockUser.getUpdatedAt().toInstant()));

            Optional<User> userOptional = userRepository.findUserById(userId);

            assertTrue(userOptional.isPresent());
            assertEquals(userId, userOptional.get().getId());
            verify(preparedStatement, times(1)).executeQuery();
        }
    }

    @Test
    void saveUser() throws SQLException {
        try(MockedStatic<DataSource> mockedStatic = mockStatic(DataSource.class)) {
            mockedStatic.when(DataSource::getConnection).thenReturn(connection);

            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setFistName("Rock");
            mockUser.setLastName("Johnson");
            mockUser.setUserRole(UserRole.ADMIN);

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            int result = userRepository.saveUser(mockUser);

            assertEquals(1, result);
            verify(preparedStatement, times(1)).executeUpdate();
        }
    }

    @Test
    void findAllUsers() throws SQLException {
        try(MockedStatic<DataSource> mockedStatic = mockStatic(DataSource.class)) {
            mockedStatic.when(DataSource::getConnection).thenReturn(connection);

            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setFistName("Rock");
            mockUser.setLastName("Johnson");
            mockUser.setUserRole(UserRole.ADMIN);
            mockUser.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
            mockUser.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()));

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getLong(1)).thenReturn(mockUser.getId());
            when(resultSet.getString(2)).thenReturn(mockUser.getFistName());
            when(resultSet.getString(3)).thenReturn(mockUser.getLastName());
            when(resultSet.getString(4)).thenReturn(mockUser.getUserRole().toString());
            when(resultSet.getTimestamp(5)).thenReturn(Timestamp.from(mockUser.getCreatedAt().toInstant()));
            when(resultSet.getTimestamp(6)).thenReturn(Timestamp.from(mockUser.getUpdatedAt().toInstant()));

            List<User> users = userRepository.findAllUsers();

            assertNotNull(users);
            assertEquals(1, users.size());
            assertEquals(mockUser.getId(), users.get(0).getId());
            verify(preparedStatement, times(1)).executeQuery();
        }
    }
}
