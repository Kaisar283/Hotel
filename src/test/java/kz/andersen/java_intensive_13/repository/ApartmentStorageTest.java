package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.db_config.DataSource;
import kz.andersen.java_intensive_13.enums.UserRole;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ApartmentStorageTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private final ApartmentStorage apartmentStorage = ApartmentStorage.getInstance();

    ApartmentStorageTest() {
    }

    @BeforeEach
    public  void setApartmentStorage() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getApartmentById_returnApartment_ifExists() throws SQLException {
        try(MockedStatic<DataSource> mockedStatic = mockStatic(DataSource.class)) {
            mockedStatic.when(DataSource::getConnection).thenReturn(connection);

            Apartment mockapartment = new Apartment(1, 1200);
            mockapartment.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
            mockapartment.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
            User mockUser = new User(1, "Alice");
            mockUser.setUserRole(UserRole.USER);

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getInt(1)).thenReturn(mockapartment.getId());
            when(resultSet.getDouble(2)).thenReturn(mockapartment.getPrice());
            when(resultSet.getBoolean(3)).thenReturn(mockapartment.getIsReserved());
            when(resultSet.getLong(4)).thenReturn(mockUser.getId());
            when(resultSet.getTimestamp(5)).thenReturn(Timestamp.from(mockapartment.getCreatedAt().toInstant()));
            when(resultSet.getTimestamp(6)).thenReturn(Timestamp.from(mockapartment.getUpdatedAt().toInstant()));

            Optional<Apartment> apartmentById = apartmentStorage.getApartmentById(mockapartment.getId());

            assertTrue(apartmentById.isPresent());
            assertEquals(mockapartment.getId(), apartmentById.get().getId());
            verify(preparedStatement, times(1)).executeQuery();
        }
    }

    @Test
    public void getApartmentById_returnEmpty_ifDoesNotExists() throws SQLException {
        try(MockedStatic<DataSource> sourceMockedStatic = mockStatic(DataSource.class)) {
            sourceMockedStatic.when(DataSource::getConnection).thenReturn(connection);
            Apartment mockapartment = new Apartment(1, 1200);

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            Optional<Apartment> apartmentById = apartmentStorage.getApartmentById(mockapartment.getId());

            verify(connection, times(1)).prepareStatement(anyString());
            assertTrue(apartmentById.isEmpty());
        }
    }

    @Test
    public void getApartments_returnListOfApartment() throws SQLException {
        try(MockedStatic<DataSource> sourceMockedStatic = mockStatic(DataSource.class)) {
            sourceMockedStatic.when(DataSource::getConnection).thenReturn(connection);
            Apartment mockapartment = new Apartment(1, 1200);
            mockapartment.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
            mockapartment.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
            User mockUser = new User(1, "Alice");
            mockUser.setUserRole(UserRole.USER);

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getInt(1)).thenReturn(mockapartment.getId());
            when(resultSet.getDouble(2)).thenReturn(mockapartment.getPrice());
            when(resultSet.getBoolean(3)).thenReturn(mockapartment.getIsReserved());
            when(resultSet.getLong(4)).thenReturn(mockUser.getId());
            when(resultSet.getTimestamp(5)).thenReturn(Timestamp.from(mockapartment.getCreatedAt().toInstant()));
            when(resultSet.getTimestamp(6)).thenReturn(Timestamp.from(mockapartment.getUpdatedAt().toInstant()));

            List<Apartment> apartments = apartmentStorage.getApartments();

            assertNotNull(apartments);
            assertEquals(1, apartments.size());
            assertEquals(mockapartment.getId(), apartments.get(0).getId());
        }
    }

    @Test
    public void addApartment_AddApartmentInstanceToDB() throws SQLException {
        try(MockedStatic<DataSource> sourceMockedStatic = mockStatic(DataSource.class)) {
            sourceMockedStatic.when(DataSource::getConnection).thenReturn(connection);
            Apartment apartment = new Apartment();
            apartment.setId(1);
            apartment.setPrice(1200.50);
            apartment.setIsReserved(false);

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            apartmentStorage.addApartment(apartment);

            verify(connection, times(1)).prepareStatement(anyString());
            verify(preparedStatement, times(1)).setLong(1, apartment.getId());
            verify(preparedStatement, times(1)).setDouble(2, apartment.getPrice());
            verify(preparedStatement, times(1)).setBoolean(3, apartment.getIsReserved());
            verify(preparedStatement, times(1)).setNull(4, Types.BIGINT);
            verify(preparedStatement, times(1)).executeUpdate();
        }
    }

    @Test
    void updateApartment() throws SQLException {
        try(MockedStatic<DataSource> sourceMockedStatic = mockStatic(DataSource.class)) {
            sourceMockedStatic.when(DataSource::getConnection).thenReturn(connection);
            // Arrange
            Apartment apartment = new Apartment();
            apartment.setId(1);
            apartment.setPrice(1500.00);
            apartment.setIsReserved(true);
            apartment.setReservedBy(new User(2, "Alice"));
            apartment.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
            apartment.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()));

            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            apartmentStorage.updateApartment(apartment);

            verify(connection, times(1)).prepareStatement(anyString());
            verify(preparedStatement, times(1)).setLong(1, apartment.getId());
            verify(preparedStatement, times(1)).setDouble(2, apartment.getPrice());
            verify(preparedStatement, times(1)).setBoolean(3, apartment.getIsReserved());
            verify(preparedStatement, times(1)).setLong(4, apartment.getReservedBy().getId());
            verify(preparedStatement, times(1)).setTimestamp(5, Timestamp.from(apartment.getCreatedAt().toInstant()));
            verify(preparedStatement, times(1)).setTimestamp(6, Timestamp.from(apartment.getUpdatedAt().toInstant()));
            verify(preparedStatement, times(1)).executeUpdate();
        }
    }

    @Test
    public void sortApartmentByPrice_returnSortedList() throws SQLException {
        try(MockedStatic<DataSource> sourceMockedStatic = mockStatic(DataSource.class)) {
            sourceMockedStatic.when(DataSource::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            List<Apartment> expectedApartments = mockApartmentList();

            when(resultSet.next())
                    .thenReturn(true, true, false);
            mockApartmentResultSetBehavior();

            List<Apartment> actualApartments = apartmentStorage.sortApartmentByPrice();

            assertEquals(expectedApartments.size(), actualApartments.size());
            verify(connection, times(1)).prepareStatement(anyString());
            verify(preparedStatement, times(1)).executeQuery();
        }
    }

    @Test
    public void sortApartmentById_returnSortedList() throws SQLException {
        try(MockedStatic<DataSource> sourceMockedStatic = mockStatic(DataSource.class)) {
            sourceMockedStatic.when(DataSource::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            List<Apartment> expectedApartments = mockApartmentList();

            when(resultSet.next())
                    .thenReturn(true, true, false);
            mockApartmentResultSetBehavior();

            List<Apartment> actualApartments = apartmentStorage.sortApartmentById();

            assertEquals(expectedApartments.size(), actualApartments.size());
            verify(connection, times(1)).prepareStatement(anyString());
            verify(preparedStatement, times(1)).executeQuery();
        }
    }

    @Test
    public void sortApartmentByClientName_returnSortedList() throws SQLException {
        try(MockedStatic<DataSource> sourceMockedStatic = mockStatic(DataSource.class)) {
            sourceMockedStatic.when(DataSource::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            List<Apartment> expectedApartments = mockApartmentList();

            when(resultSet.next())
                    .thenReturn(true, true, false);
            mockApartmentResultSetBehavior();

            List<Apartment> actualApartments = apartmentStorage.sortApartmentByClientName();

            assertEquals(expectedApartments.size(), actualApartments.size());
            verify(connection, times(1)).prepareStatement(anyString());
            verify(preparedStatement, times(1)).executeQuery();
        }
    }

    @Test
    public void sortedApartmentByReservationStatus_returnSortedList() throws SQLException {
        try(MockedStatic<DataSource> sourceMockedStatic = mockStatic(DataSource.class)) {
            sourceMockedStatic.when(DataSource::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            List<Apartment> expectedApartments = mockApartmentList();

            when(resultSet.next()).thenReturn(true, true, false);
            mockApartmentResultSetBehavior();

            List<Apartment> actualApartments = apartmentStorage.sortedApartmentByReservationStatus();

            assertEquals(expectedApartments.size(), actualApartments.size());
            verify(connection, times(1)).prepareStatement(anyString());
            verify(preparedStatement, times(1)).executeQuery();
        }
    }

    private void mockApartmentResultSetBehavior() throws SQLException {
        when(resultSet.getInt(1)).thenReturn(1, 2);
        when(resultSet.getDouble(2)).thenReturn(1500.75, 1600.50);
        when(resultSet.getBoolean(3)).thenReturn(true, false);
        when(resultSet.getLong(4)).thenReturn(1L, 2L);
        when(resultSet.getTimestamp(5)).thenReturn(
                Timestamp.valueOf("2024-11-18 10:00:00"),
                Timestamp.valueOf("2024-11-13 10:00:00")
        );
        when(resultSet.getTimestamp(6)).thenReturn(
                Timestamp.valueOf("2024-01-01 12:00:00"),
                Timestamp.valueOf("2024-02-01 12:00:00")
        );
        when(resultSet.getLong(7)).thenReturn(1L, 2L);
        when(resultSet.getString(8)).thenReturn("Alice", "Bob");
        when(resultSet.getString(9)).thenReturn("Stark", "John");
        when(resultSet.getString(10)).thenReturn(String.valueOf(UserRole.USER),String.valueOf(UserRole.USER));
        when(resultSet.getTimestamp(11)).thenReturn(
                Timestamp.valueOf("2024-11-18 10:00:00"),
                Timestamp.valueOf("2024-11-13 10:00:00"));
        when(resultSet.getTimestamp(12)).thenReturn(
                Timestamp.valueOf("2024-01-01 12:00:00"),
                Timestamp.valueOf("2024-02-01 12:00:00"));
    }

    private List<Apartment> mockApartmentList() {
        List<Apartment> apartments = new ArrayList<>();
        User user = new User(1, "Alice");
        user.setLastName("Stark");
        user.setUserRole(UserRole.USER);
        Apartment apartment1 = new Apartment();
        apartment1.setId(1);
        apartment1.setPrice(1500.75);
        apartment1.setIsReserved(true);
        apartment1.setReservedBy(user);
        apartment1.setCreatedAt(Timestamp.valueOf("2024-02-01 10:00:00")
                .toInstant().atZone(ZoneId.systemDefault()));
        apartment1.setUpdatedAt(Timestamp.valueOf("2024-02-01 12:00:00")
                .toInstant().atZone(ZoneId.systemDefault()));
        apartments.add(apartment1);

        Apartment apartment2 = new Apartment();
        User user2 = new User(1, "Bob");
        user2.setLastName("John");
        user2.setUserRole(UserRole.USER);
        apartment2.setId(2);
        apartment2.setPrice(1600.50);
        apartment2.setIsReserved(true);
        apartment2.setReservedBy(user2);
        apartment2.setCreatedAt(Timestamp.valueOf("2024-01-01 10:00:00")
                .toInstant().atZone(ZoneId.systemDefault()));
        apartment2.setUpdatedAt(Timestamp.valueOf("2024-01-01 12:00:00")
                .toInstant().atZone(ZoneId.systemDefault()));
        apartments.add(apartment2);

        return apartments;
    }
}