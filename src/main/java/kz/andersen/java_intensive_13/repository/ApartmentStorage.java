package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.db_config.DataSource;
import kz.andersen.java_intensive_13.enums.UserRole;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static java.sql.Types.BIGINT;

public class ApartmentStorage {

    private static volatile ApartmentStorage instance;

    public static ApartmentStorage getInstance(){
        if(instance == null){
            synchronized (ApartmentStorage.class){
                if (instance == null){
                    instance =  new ApartmentStorage();
                }
            }
        }
        return instance;
    }

    /**
     * Method: add apartment to original list.
     * @param apartment - an Apartment instance
     */
    public void addApartment(Apartment apartment){
        long apartmentId = apartment.getId();
        double price = apartment.getPrice();
        boolean isReserved = apartment.getIsReserved();
        ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime updatedAt = ZonedDateTime.now(ZoneId.systemDefault());

        String SQL_QUERY = """
                INSERT INTO public.apartment(
                	id, price, "isReserved", "reservedBy", created_at, updated_at)
                	VALUES (?, ?, ?, ?, ?, ?);
                """;
        try(Connection connection = DataSource.getConnection();
        ) {
            PreparedStatement pst = connection.prepareStatement(SQL_QUERY);
            pst.setLong(1, apartmentId);
            pst.setDouble(2, price);
            pst.setBoolean(3, isReserved);
            pst.setNull(4, BIGINT);
            pst.setTimestamp(5, Timestamp.from(createdAt.toInstant()));
            pst.setTimestamp(6, Timestamp.from(updatedAt.toInstant()));
            int i = pst.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateApartment(Apartment apartment){
        long apartmentId = apartment.getId();
        double price = apartment.getPrice();
        boolean isReserved = apartment.getIsReserved();
        long userId = apartment.getReservedBy().getId();
        ZonedDateTime createdAt = apartment.getCreatedAt();
        ZonedDateTime updatedAt = apartment.getUpdatedAt();

        String SQL_QUERY = String.format("""
                UPDATE public.apartment
                	SET id=?, price=?, "isReserved"=?, "reservedBy"=?, created_at=?, updated_at=?
                	WHERE id=%d;
                """, apartment.getId() );
        try(Connection connection = DataSource.getConnection();
        ) {
            PreparedStatement pst = connection.prepareStatement(SQL_QUERY);
            pst.setLong(1, apartmentId);
            pst.setDouble(2, price);
            pst.setBoolean(3, isReserved);
            pst.setLong(4, userId);
            pst.setTimestamp(5, Timestamp.from(createdAt.toInstant()));
            pst.setTimestamp(6, Timestamp.from(updatedAt.toInstant()));
            int i = pst.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the original Apartments list
     */
    public List<Apartment> getApartments(){
        List<Apartment> apartmentList = new ArrayList<>();
        String SQLQuery = "SELECT * FROM apartment LEFT JOIN public.user ON apartment.\"reservedBy\" = public.user.id;";

        try(Connection connection = DataSource.getConnection();
            PreparedStatement pst = connection.prepareStatement(SQLQuery);
            ResultSet results = pst.executeQuery();
        ){
            while (results.next()) {
                apartmentList.add(mapResultSetToApartment(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return apartmentList;
    }


    /**
     * Return Optional<Apartment> if given ID it exists in the Apartment Storage,
     * else return an empty Optional
     * @param apartmentId apartment ID
     * @return an Apartment Optional by given ID
     */
    public Optional<Apartment> getApartmentById(int apartmentId){
        String SQLQuery = String.format("""
                SELECT * FROM apartment LEFT JOIN public.user
                ON apartment.\"reservedBy\" = public.user.id
                WHERE apartment.id = '%d';
                """, apartmentId);
        try(Connection connection = DataSource.getConnection();
            PreparedStatement pst = connection.prepareStatement(SQLQuery);
            ResultSet results = pst.executeQuery();
        ){
            Apartment apartment = null;
            if (results.next()) {
                apartment = mapResultSetToApartment(results);
            }
            return apartment == null ? Optional.empty() : Optional.of(apartment);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return a new ArrayList sorted by Apartment price, in ASC order.
     */
    public List<Apartment> sortApartmentByPrice(){
        List<Apartment> apartmentList = new ArrayList<>();
        String SQLQuery = """
                SELECT * FROM apartment LEFT JOIN public.user
                	ON apartment."reservedBy" = public.user.id
                	ORDER BY apartment.price DESC;
                """;
        try(Connection connection = DataSource.getConnection();
            PreparedStatement pst = connection.prepareStatement(SQLQuery);
            ResultSet results = pst.executeQuery();
        ){
            while (results.next()) {
                apartmentList.add(mapResultSetToApartment(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return apartmentList;
    }

    /**
     * @return a new ArrayList sorted by Apartment ID, in ASC order.
     */
    public List<Apartment> sortApartmentById(){
        List<Apartment> apartmentList = new ArrayList<>();
        String SQLQuery = """
                SELECT * FROM apartment LEFT JOIN public.user
                	ON apartment."reservedBy" = public.user.id
                	ORDER BY apartment.id ASC;
                """;
        try(Connection connection = DataSource.getConnection();
            PreparedStatement pst = connection.prepareStatement(SQLQuery);
            ResultSet results = pst.executeQuery();
        ){
            while (results.next()) {
                apartmentList.add(mapResultSetToApartment(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return apartmentList;
    }

    /**
     * @return a new ArrayList sorted by User name, in DESC order.
     */
    public List<Apartment> sortApartmentByClientName() {
        List<Apartment> apartmentList = new ArrayList<>();
        String SQLQuery = """
                SELECT * FROM apartment LEFT JOIN public.user
                	ON apartment."reservedBy" = public.user.id
                	ORDER BY public.user.first_name ASC;
                """;
        try(Connection connection = DataSource.getConnection();
            PreparedStatement pst = connection.prepareStatement(SQLQuery);
            ResultSet results = pst.executeQuery();
        ){
            while (results.next()) {
                apartmentList.add(mapResultSetToApartment(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return apartmentList;
    }

    /**
     * @return a new ArrayList sorted by Reservation status, in DESC order.
     */
    public List<Apartment> sortedApartmentByReservationStatus(){
        List<Apartment> apartmentList = new ArrayList<>();
        String SQLQuery = """
                SELECT * FROM apartment LEFT JOIN public.user
                	ON apartment."reservedBy" = public.user.id
                    ORDER BY apartment."isReserved" DESC;
                """;
        try(Connection connection = DataSource.getConnection();
            PreparedStatement pst = connection.prepareStatement(SQLQuery);
            ResultSet results = pst.executeQuery();
        ){
            while (results.next()) {
                apartmentList.add(mapResultSetToApartment(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return apartmentList;
    }

    private Apartment mapResultSetToApartment(ResultSet results) throws SQLException {
        Apartment apartment = new Apartment(results.getInt(1),
                results.getDouble(2));
        apartment.setIsReserved(results.getBoolean(3));
        apartment.setCreatedAt(timestampToZonedDateTime(results.getTimestamp(5)));
        apartment.setUpdatedAt(timestampToZonedDateTime(results.getTimestamp(6)));

        User user = null;
        if(!results.getBoolean(3)){
            apartment.setReservedBy(user);
        }else {
            user = new User();
            user.setId(results.getLong(7));
            user.setFistName(results.getString(8));
            user.setLastName(results.getString(9));
            user.setUserRole(UserRole.valueOf(results.getString(10)));
            user.setCreatedAt(timestampToZonedDateTime(results.getTimestamp(11)));
            user.setUpdatedAt(timestampToZonedDateTime(results.getTimestamp(12)));
            apartment.setReservedBy(user);
        }
        return apartment;
    }
    private ZonedDateTime timestampToZonedDateTime(Timestamp timestamp){
        return timestamp.toInstant().atZone(ZoneId.systemDefault());
    }
}
