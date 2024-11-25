package kz.andersen.java_intensive_13.services;

import kz.andersen.java_intensive_13.config.HibernateConfig;
import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.exception.AlreadyReservedException;
import kz.andersen.java_intensive_13.exception.ResourceNotFoundException;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;
import kz.andersen.java_intensive_13.repository.ApartmentStorage;
import kz.andersen.java_intensive_13.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceTest{

    static int page = 1;
    static int pageSize = 5;

    @Mock
    ApartmentStorage apartmentStorage;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ApartmentService apartmentService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void reserveApartment_returnSuccess_whenApartmentIsReserved() {

        Apartment apartment = new Apartment(200.0);
        apartment.setId(1);
        apartment.setReserved(false);
        User user = new User("Gorge");
        user.setId(1L);

        when(apartmentStorage.getApartmentById(apartment.getId())).thenReturn(Optional.of(apartment));
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));

        ResultCode result = apartmentService.reserveApartment(apartment.getId(), user);

        assertEquals(ResultCode.SUCCESS, result);
        assertTrue(apartment.isReserved());
        assertEquals(user, apartment.getUser());
        verify(apartmentStorage, times(2)).getApartmentById(1);
    }
    @Test
    public void reserveApartment_throwResourceNotFoundException_whenApartmentIsNotFound() {
            int apartmentId = 1;
            User user = new User(1, "Alice");

            given(apartmentStorage.getApartmentById(apartmentId)).willReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> apartmentService.reserveApartment(apartmentId, user)
            );
            assertEquals("Apartment with id 1 is not found!", exception.getMessage());
            verify(apartmentStorage).getApartmentById(apartmentId);
    }

    @Test
    public void reserveApartment_throwAlreadyReservedException_whenApartmentIsNotAvailable() {

            User user = new User("Gorge");
            Apartment apartment = new Apartment( 1500);
            apartment.setId(9);
            apartment.setReserved(true);
            apartment.setUser(user);
            User anotherUser = new User(1, "Alice");

            given(apartmentStorage.getApartmentById(9)).willReturn(Optional.of(apartment));

            AlreadyReservedException exception = assertThrows(
                    AlreadyReservedException.class,
                    () -> apartmentService.reserveApartment(apartment.getId(), anotherUser)
            );

            assertEquals("Apartment with id 9 already reserved!", exception.getMessage());
            verify(apartmentStorage, times(1)).getApartmentById(apartment.getId());

    }

    @Test
    public void registerApartment_returnApartmentID_whenApartmentIsRegistered(){

            int expectedId = 1;
            doAnswer(invocation -> {
                Apartment apartmentInstance = invocation.getArgument(0);
                apartmentInstance.setId(1);
                return null;
            }).when(apartmentStorage).addApartment(any(Apartment.class));
            Apartment apartment = new Apartment(1500);
            int registerApartment = apartmentService.registerApartment(apartment);
            assertEquals(expectedId, registerApartment);
            verify(apartmentStorage).addApartment(any(Apartment.class));
    }

    @Test
    public void releaseApartment_returnSuccess_whenApartmentIsReleased(){

            User user = new User("Alice");
            Apartment apartment = new Apartment( 1500);
            apartment.setId(3);
            apartment.setReserved(true);
            apartment.setUser(user);

            given(apartmentStorage.getApartmentById(apartment.getId())).willReturn(Optional.of(apartment));

            ResultCode resultCode = apartmentService.releaseApartment(apartment.getId());

            assertThat(resultCode).isEqualTo(ResultCode.SUCCESS);

            assertThat(apartment.getUser()).isNull();
            assertThat(apartment.isReserved()).isEqualTo(false);
            verify(apartmentStorage, times(1)).getApartmentById(apartment.getId());
    }

    @Test
    public void releaseApartment_returnNot_Reserved_whenApartmentIsNotReserved(){

            Apartment apartment = new Apartment( 2000);
            apartment.setId(7);

            given(apartmentStorage.getApartmentById(apartment.getId())).willReturn(Optional.of(apartment));

            ResultCode resultCode = apartmentService.releaseApartment(apartment.getId());
            assertThat(resultCode).isEqualTo(ResultCode.NOT_RESERVED);
            verify(apartmentStorage, times(1)).getApartmentById(apartment.getId());

    }
    @Test
    public void releaseApartment_throwResourceNotFoundException_whenApartmentIsNotFound(){

            int apartmentId = 17;

            when(apartmentStorage.getApartmentById(apartmentId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> apartmentService.releaseApartment(apartmentId)
            );

            assertEquals("Apartment with id 17 is not found!", exception.getMessage());
            verify(apartmentStorage, times(1)).getApartmentById(apartmentId);
    }

    @Test
    public void getApartmentsSortedByPrice_willReturnSortedList(){

            List<Apartment> mockSortedApartments = setApartmentData();

            given(apartmentStorage.sortApartmentByPrice()).willReturn(mockSortedApartments);

            List<Apartment> apartments = apartmentService.getApartmentsSortedByPrice(page, pageSize);
            assertThat(apartments).hasSize(pageSize);
            assertThat(apartments.get(0).getPrice()).isEqualTo(1000);
            assertThat(apartments.get(1).getPrice()).isEqualTo(1500);
            assertThat(apartments.get(2).getPrice()).isEqualTo(2000);
            assertThat(apartments.get(3).getPrice()).isEqualTo(2500);
            assertThat(apartments.get(4).getPrice()).isEqualTo(3000);
            verify(apartmentStorage).sortApartmentByPrice();

    }

    @Test
    public void getApartmentsSortedById_willReturnSortedList(){

            List<Apartment> mockApartmentList = setApartmentData();

            given(apartmentStorage.sortApartmentById()).willReturn(mockApartmentList);

            List<Apartment> apartments = apartmentService.getApartmentsSortedById(page, pageSize);
            assertThat(apartments.get(0).getId()).isEqualTo(1);
            assertThat(apartments.get(1).getId()).isEqualTo(2);
            assertThat(apartments.get(2).getId()).isEqualTo(3);
            assertThat(apartments.get(3).getId()).isEqualTo(4);
            assertThat(apartments.get(4).getId()).isEqualTo(5);
            verify(apartmentStorage).sortApartmentById();
    }

    @Test
    public void getApartmentSortedByReservationStatus_willReturnSortedList(){

            List<Apartment> mockApartmentList = setApartmentData();
            mockApartmentList.getLast().setReserved(true);
            List<Apartment> expectedMockApartmentList = mockApartmentList.reversed();

            given(apartmentStorage.sortedApartmentByReservationStatus())
                    .willReturn(expectedMockApartmentList);

            List<Apartment> apartments = apartmentService.getApartmentSortedByReservationStatus(page, pageSize);

            assertTrue(apartments.get(0).isReserved());
            verify(apartmentStorage).sortedApartmentByReservationStatus();
    }

    @Test
    public void getApartmentSortedByClientName_willReturnSortedList(){

            User alice = new User("Alice");
            User bob = new User("Bob");
            User john = new User("John");
            List<Apartment> mockApartmentList = setApartmentData();
            mockApartmentList.get(0).setUser(john);
            mockApartmentList.get(1).setUser(bob);
            mockApartmentList.get(2).setUser(alice);

            given(apartmentStorage.sortApartmentByClientName())
                    .willReturn(mockApartmentList);

            List<Apartment> apartments = apartmentService.getApartmentSortedByClientName(page, pageSize);
            assertThat(apartments.get(0).getUser().getFirstName()).isEqualTo(john.getFirstName());
            assertThat(apartments.get(1).getUser().getFirstName()).isEqualTo(bob.getFirstName());
            assertThat(apartments.get(2).getUser().getFirstName()).isEqualTo(alice.getFirstName());

            verify(apartmentStorage).sortApartmentByClientName();
    }

    // Here method pagingApartments is not interacting with ApartmentStorage class
    @Test
    public void pagingApartments_willReturnEmptyLis(){
        List<Apartment> apartments = new ArrayList<>();
        ApartmentService apartmentService = new ApartmentService();
        List<Apartment> pagedList = apartmentService.pagingApartments(0, 0, apartments);
        assertEquals(apartments.isEmpty(), pagedList.isEmpty());
    }

    // Here method pagingApartments is not interacting with ApartmentStorage class
    @Test
    public void pagingApartments_willReturnPagedList(){
        List<Apartment> apartments = setApartmentData();
        ApartmentService apartmentService = new ApartmentService();
        List<Apartment> pagedList = apartmentService.pagingApartments(page, pageSize, apartments);
        assertThat(pagedList).isEqualTo(apartments);
    }

    private List<Apartment> setApartmentData(){
        Apartment apartment1 = new Apartment(1000);
        apartment1.setId(1);
        Apartment apartment2 = new Apartment(1500);
        apartment2.setId(2);
        Apartment apartment3 = new Apartment(2000);
        apartment3.setId(3);
        Apartment apartment4 = new Apartment(2500);
        apartment4.setId(4);
        Apartment apartment5 = new Apartment(3000);
        apartment5.setId(5);
        return Arrays.asList(
                apartment1,
                apartment2,
                apartment3,
                apartment4,
                apartment5);
    }
}