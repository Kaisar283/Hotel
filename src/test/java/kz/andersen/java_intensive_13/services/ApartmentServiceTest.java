package kz.andersen.java_intensive_13.services;

import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.exception.AlreadyReservedException;
import kz.andersen.java_intensive_13.exception.ResourceNotFoundException;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;
import kz.andersen.java_intensive_13.repository.ApartmentStorage;
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

    @BeforeEach
    public void setUp() throws Exception {
        Field counterField = Apartment.class.getDeclaredField("counter");
        counterField.setAccessible(true);
        counterField.set(null, 1);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void reserveApartment_returnSuccess_whenApartmentIsReserved() {
        try (MockedStatic<ApartmentStorage> mockedStorage = mockStatic(ApartmentStorage.class)) {
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStorage.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);

            Apartment apartment = new Apartment(1, 200.0);
            apartment.setIsReserved(false);
            User user = new User("Gorge");

            when(mockApartmentStorage.getApartmentById(1)).thenReturn(Optional.of(apartment));

            ApartmentService apartmentService = new ApartmentService();
            ResultCode result = apartmentService.reserveApartment(1, user);

            assertEquals(ResultCode.SUCCESS, result);
            assertTrue(apartment.getIsReserved());
            assertEquals(user, apartment.getReservedBy());
            verify(mockApartmentStorage, times(2)).getApartmentById(1);
        }
    }
    @Test
    public void reserveApartment_throwResourceNotFoundException_whenApartmentIsNotFound() {
        try(MockedStatic<ApartmentStorage> mockedStorage = mockStatic(ApartmentStorage.class)) {
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStorage.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);
            int apartmentId = 1;
            User user = new User("Alice");

            given(mockApartmentStorage.getApartmentById(apartmentId)).willReturn(Optional.empty());

            ApartmentService apartmentService = new ApartmentService();
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> apartmentService.reserveApartment(apartmentId, user)
            );
            assertEquals("Apartment with id 1 is not found!", exception.getMessage());
            verify(mockApartmentStorage).getApartmentById(apartmentId);
        }
    }

    @Test
    public void reserveApartment_throwAlreadyReservedException_whenApartmentIsNotAvailable() {
        try(MockedStatic<ApartmentStorage> mockedStorage = mockStatic(ApartmentStorage.class)) {
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStorage.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);
            User user = new User("Gorge");
            Apartment apartment = new Apartment(9, 1500);
            apartment.setIsReserved(true);
            apartment.setReservedBy(user);
            User anotherUser = new User("Alice");

            given(mockApartmentStorage.getApartmentById(9)).willReturn(Optional.of(apartment));
            ApartmentService apartmentService = new ApartmentService();

            AlreadyReservedException exception = assertThrows(
                    AlreadyReservedException.class,
                    () -> apartmentService.reserveApartment(apartment.getId(), anotherUser)
            );

            assertEquals("Apartment with id 9 already reserved!", exception.getMessage());
            verify(mockApartmentStorage, times(1)).getApartmentById(apartment.getId());
        }
    }

    @Test
    public void registerApartment_returnApartmentID_whenApartmentIsRegistered(){
        try(MockedStatic<ApartmentStorage> mockedStorage = mockStatic(ApartmentStorage.class)){
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStorage.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);
            int expectedId = 1;
            doAnswer(invocation -> {
                Apartment apartmentInstance = invocation.getArgument(0);
                apartmentInstance.setId(1);
                return null;
            }).when(mockApartmentStorage).addApartment(any(Apartment.class));
            Apartment apartment = new Apartment(1500);
            ApartmentService apartmentService = new ApartmentService();
            int registerApartment = apartmentService.registerApartment(apartment);
            assertEquals(expectedId, registerApartment);
            verify(mockApartmentStorage).addApartment(any(Apartment.class));
        }
    }

    @Test
    public void releaseApartment_returnSuccess_whenApartmentIsReleased(){
        try(MockedStatic<ApartmentStorage> mockedStatic = mockStatic(ApartmentStorage.class)){
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStatic.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);

            User user = new User("Alice");
            Apartment apartment = new Apartment(7, 1500);
            apartment.setIsReserved(true);
            apartment.setReservedBy(user);

            given(mockApartmentStorage.getApartmentById(apartment.getId())).willReturn(Optional.of(apartment));

            ApartmentService apartmentService = new ApartmentService();
            ResultCode resultCode = apartmentService.releaseApartment(apartment.getId());

            assertThat(resultCode).isEqualTo(ResultCode.SUCCESS);

            assertThat(apartment.getReservedBy()).isNull();
            assertThat(apartment.getIsReserved()).isEqualTo(false);
            verify(mockApartmentStorage, times(2)).getApartmentById(apartment.getId());
        }
    }

    @Test
    public void releaseApartment_returnNot_Reserved_whenApartmentIsNotReserved(){
        try(MockedStatic<ApartmentStorage> mockedStatic = mockStatic(ApartmentStorage.class)) {
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStatic.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);

            Apartment apartment = new Apartment(7, 2000);

            given(mockApartmentStorage.getApartmentById(apartment.getId())).willReturn(Optional.of(apartment));

            ApartmentService apartmentService = new ApartmentService();
            ResultCode resultCode = apartmentService.releaseApartment(apartment.getId());
            assertThat(resultCode).isEqualTo(ResultCode.NOT_RESERVED);
            verify(mockApartmentStorage, times(2)).getApartmentById(apartment.getId());
        }
    }
    @Test
    public void releaseApartment_throwResourceNotFoundException_whenApartmentIsNotFound(){
        try(MockedStatic<ApartmentStorage> mockedStatic = mockStatic(ApartmentStorage.class)) {
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStatic.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);

            int apartmentId = 17;

            when(mockApartmentStorage.getApartmentById(apartmentId)).thenReturn(Optional.empty());

            ApartmentService apartmentService = new ApartmentService();

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> apartmentService.releaseApartment(apartmentId)
            );

            assertEquals("Apartment with id 17 is not found!", exception.getMessage());
            verify(mockApartmentStorage, times(1)).getApartmentById(apartmentId);
        }
    }

    @Test
    public void getApartmentsSortedByPrice_willReturnSortedList(){
        try(MockedStatic<ApartmentStorage> mockedStatic = mockStatic(ApartmentStorage.class)) {
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStatic.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);

            List<Apartment> mockSortedApartments = setApartmentData();

            given(mockApartmentStorage.sortApartmentByPrice()).willReturn(mockSortedApartments);

            ApartmentService apartmentService = new ApartmentService();
            List<Apartment> apartments = apartmentService.getApartmentsSortedByPrice(page, pageSize);
            assertThat(apartments).hasSize(pageSize);
            assertThat(apartments.get(0).getPrice()).isEqualTo(1000);
            assertThat(apartments.get(1).getPrice()).isEqualTo(1500);
            assertThat(apartments.get(2).getPrice()).isEqualTo(2000);
            assertThat(apartments.get(3).getPrice()).isEqualTo(2500);
            assertThat(apartments.get(4).getPrice()).isEqualTo(3000);
            verify(mockApartmentStorage).sortApartmentByPrice();
        }
    }

    @Test
    public void getApartmentsSortedById_willReturnSortedList(){
        try(MockedStatic<ApartmentStorage> mockedStatic = mockStatic(ApartmentStorage.class)) {
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStatic.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);
            List<Apartment> mockApartmentList = setApartmentData();

            given(mockApartmentStorage.sortApartmentById()).willReturn(mockApartmentList);

            ApartmentService apartmentService = new ApartmentService();
            List<Apartment> apartments = apartmentService.getApartmentsSortedById(page, pageSize);
            assertThat(apartments.get(0).getId()).isEqualTo(1);
            assertThat(apartments.get(1).getId()).isEqualTo(2);
            assertThat(apartments.get(2).getId()).isEqualTo(3);
            assertThat(apartments.get(3).getId()).isEqualTo(4);
            assertThat(apartments.get(4).getId()).isEqualTo(5);
            verify(mockApartmentStorage).sortApartmentById();
        }
    }

    @Test
    public void getApartmentSortedByReservationStatus_willReturnSortedList(){
        try(MockedStatic<ApartmentStorage> mockedStatic = mockStatic(ApartmentStorage.class)) {
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStatic.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);

            List<Apartment> mockApartmentList = setApartmentData();
            mockApartmentList.getLast().setIsReserved(true);
            List<Apartment> expectedMockApartmentList = mockApartmentList.reversed();

            given(mockApartmentStorage.sortedApartmentByReservationStatus())
                    .willReturn(expectedMockApartmentList);

            ApartmentService apartmentService = new ApartmentService();
            List<Apartment> apartments = apartmentService.getApartmentSortedByReservationStatus(page, pageSize);

            assertTrue(apartments.get(0).getIsReserved());
            verify(mockApartmentStorage).sortedApartmentByReservationStatus();
        }
    }

    @Test
    public void getApartmentSortedByClientName_willReturnSortedList(){
        try(MockedStatic<ApartmentStorage> mockedStatic = mockStatic(ApartmentStorage.class)) {
            ApartmentStorage mockApartmentStorage = mock(ApartmentStorage.class);
            mockedStatic.when(ApartmentStorage::getInstance).thenReturn(mockApartmentStorage);
            User alice = new User("Alice");
            User bob = new User("Bob");
            User john = new User("John");
            List<Apartment> mockApartmentList = setApartmentData();
            mockApartmentList.get(0).setReservedBy(john);
            mockApartmentList.get(1).setReservedBy(bob);
            mockApartmentList.get(2).setReservedBy(alice);

            given(mockApartmentStorage.sortApartmentByClientName())
                    .willReturn(mockApartmentList);

            ApartmentService apartmentService = new ApartmentService();
            List<Apartment> apartments = apartmentService.getApartmentSortedByClientName(page, pageSize);
            assertThat(apartments.get(0).getReservedBy().getName()).isEqualTo(john.getName());
            assertThat(apartments.get(1).getReservedBy().getName()).isEqualTo(bob.getName());
            assertThat(apartments.get(2).getReservedBy().getName()).isEqualTo(alice.getName());

            verify(mockApartmentStorage).sortApartmentByClientName();
        }
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
        Apartment apartment2 = new Apartment(1500);
        Apartment apartment3 = new Apartment(2000);
        Apartment apartment4 = new Apartment(2500);
        Apartment apartment5 = new Apartment(3000);
        return Arrays.asList(
                apartment1,
                apartment2,
                apartment3,
                apartment4,
                apartment5);
    }
}