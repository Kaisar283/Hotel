package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.db_config.DataSource;
import kz.andersen.java_intensive_13.enums.UserRole;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarOutputStream;

import static kz.andersen.java_intensive_13.statics.ApartmentHQL.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentStorageTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private ApartmentStorage apartmentStorage;


    @BeforeEach
    void setUp() {
        when(sessionFactory.openSession()).thenReturn(session);
    }

    @Test
    public void getApartmentById_returnApartment_ifExists(){
        Apartment mockApartment = new Apartment();
        mockApartment.setId(1);
        mockApartment.setPrice(2000);

        when(session.get(Apartment.class, 1)).thenReturn(mockApartment);

        Optional<Apartment> apartmentById = apartmentStorage.getApartmentById(1);

        assertTrue(apartmentById.isPresent());
        assertEquals(mockApartment.getId(), apartmentById.get().getId());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    @Test
    public void getApartmentById_returnEmpty_ifDoesNotExists(){
        Integer apartmentId = 3;
        when(session.get(Apartment.class, 3)).thenReturn(null);

        Optional<Apartment> apartmentById = apartmentStorage.getApartmentById(apartmentId);

        assertTrue(apartmentById.isEmpty());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    @Test
    public void getApartments_returnListOfApartment(){
        List<Apartment> mockApartments = getPreparedApartment();
        when(session.createQuery(FIND_ALL_APARTMENT, Apartment.class)).thenReturn(mock(Query.class));
        Query<Apartment> mockQuery = mock(Query.class);
        when(session.createQuery(FIND_ALL_APARTMENT, Apartment.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockApartments);

        List<Apartment> apartments = apartmentStorage.getApartments();

        assertEquals(mockApartments.size(), apartments.size());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    @Test
    public void addApartment_AddApartmentInstanceToDB(){
        Apartment apartment = new Apartment(5000);

        when(session.beginTransaction()).thenReturn(transaction);

        apartmentStorage.addApartment(apartment);

        verify(sessionFactory).openSession();
        verify(session).persist(apartment);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    void updateApartment(){
        Apartment apartment = new Apartment(5000);

        when(session.beginTransaction()).thenReturn(transaction);

        apartmentStorage.updateApartment(apartment);

        verify(sessionFactory).openSession();
        verify(session).merge(apartment);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void sortApartmentByPrice_returnSortedList(){
        List<Apartment> apartments = getPreparedApartment();

        when(session.createQuery(SORT_BY_PRICE_HQL, Apartment.class)).thenReturn(mock(Query.class));
        Query<Apartment> mockQuery = mock(Query.class);
        when(session.createQuery(SORT_BY_PRICE_HQL, Apartment.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(apartments);


        List<Apartment> sortApartmentByPrice = apartmentStorage.sortApartmentByPrice();

        assertEquals(apartments.size(), sortApartmentByPrice.size());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    @Test
    public void sortApartmentById_returnSortedList(){
        List<Apartment> apartments = getPreparedApartment();

        when(session.createQuery(SORTED_BY_ID_HQL, Apartment.class)).thenReturn(mock(Query.class));
        Query<Apartment> mockQuery = mock(Query.class);
        when(session.createQuery(SORTED_BY_ID_HQL, Apartment.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(apartments);

        List<Apartment> sortApartmentById = apartmentStorage.sortApartmentById();

        assertEquals(apartments.size(), sortApartmentById.size());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    @Test
    public void sortApartmentByClientName_returnSortedList(){
        List<Apartment> apartments = getPreparedApartment();

        when(session.createQuery(SORT_BY_USERNAME_HQL, Apartment.class)).thenReturn(mock(Query.class));
        Query<Apartment> mockQuery = mock(Query.class);
        when(session.createQuery(SORT_BY_USERNAME_HQL, Apartment.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(apartments);

        List<Apartment> sortApartmentByClientName = apartmentStorage.sortApartmentByClientName();

        assertEquals(apartments.size(), sortApartmentByClientName.size());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    @Test
    public void sortedApartmentByReservationStatus_returnSortedList(){
        List<Apartment> apartments = getPreparedApartment();

        when(session.createQuery(SORT_BY_RESERVATION_STATUS_HQL, Apartment.class)).thenReturn(mock(Query.class));
        Query<Apartment> mockQuery = mock(Query.class);
        when(session.createQuery(SORT_BY_RESERVATION_STATUS_HQL, Apartment.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(apartments);;

        List<Apartment> sorteded = apartmentStorage.sortedApartmentByReservationStatus();

        assertEquals(apartments.size(), sorteded.size());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    private List<Apartment> getPreparedApartment(){
        List<Apartment> apartments = new ArrayList<>();

        Apartment aliceApartment = new Apartment(1000);
        Apartment bobsApartment = new Apartment(2000);
        Apartment johnsApartment = new Apartment(3000);

        User alice = new User(1, "Alice");
        User bob = new User(2, "Bob");
        User john = new User(3, "John");

        aliceApartment.setUser(alice);
        aliceApartment.setReserved(true);
        bobsApartment.setUser(bob);
        bobsApartment.setReserved(true);
        johnsApartment.setUser(john);
        johnsApartment.setReserved(true);

        apartments.add(aliceApartment);
        apartments.add(bobsApartment);
        apartments.add(johnsApartment);

        return apartments;
    }
}