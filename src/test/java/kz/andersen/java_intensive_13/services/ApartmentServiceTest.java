package kz.andersen.java_intensive_13.services;

import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.Client;
import kz.andersen.java_intensive_13.repository.ApartmentStorage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
    private final ApartmentStorage apartmentStorage = Mockito.mock(ApartmentStorage.class);

    @InjectMocks
    ApartmentService apartmentService;

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
        int apartmentId = 1;
        Apartment apartment = new Apartment(200.0);
        apartment.setId(apartmentId);
        apartment.setIsReserved(false);
        Client client = new Client("Gorge");

        given(apartmentStorage.getApartmentById(apartmentId)).willReturn(Optional.of(apartment));

        ResultCode result = apartmentService.reserveApartment(apartmentId, client);

        assertEquals(ResultCode.SUCCESS, result);
        assertTrue(apartment.isReserved());
        assertEquals(client, apartment.getReservedBy());
        verify(apartmentStorage, times(2)).getApartmentById(apartmentId);
    }
    @Test
    public void reserveApartment_returnNot_Found_whenApartmentIsNotFound() {
        int apartmentId = 1;
        Client client = new Client("Alice");

        given(apartmentStorage.getApartmentById(apartmentId)).willReturn(Optional.empty());

        ResultCode resultCode = apartmentService.reserveApartment(apartmentId, client);

        assertEquals(ResultCode.NOT_FOUND, resultCode);
        verify(apartmentStorage).getApartmentById(apartmentId);
    }

    @Test
    public void reserveApartment_returnReservedCode_whenApartmentIsNotAvailable() {
        Client client = new Client("Gorge");
        int apartmentId = 9;
        Apartment apartment = new Apartment(1500);
        apartment.setId(apartmentId);
        apartment.setIsReserved(true);
        apartment.setReservedBy(client);

        given(apartmentStorage.getApartmentById(9)).willReturn(Optional.of(apartment));
        ResultCode resultCode = apartmentService.reserveApartment(apartmentId, client);

        assertThat(resultCode).isEqualTo(ResultCode.RESERVED);
        verify(apartmentStorage).getApartmentById(apartmentId);
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
        int apartmentId = 7;
        Client client = new Client("Alice");
        Apartment apartment = new Apartment(1500);
        apartment.setId(7);
        apartment.setIsReserved(true);
        apartment.setReservedBy(client);

        given(apartmentStorage.getApartmentById(apartmentId)).willReturn(Optional.of(apartment));

        ResultCode resultCode = apartmentService.releaseApartment(apartmentId);

        assertThat(resultCode).isEqualTo(ResultCode.SUCCESS);
        assertThat(apartment.getReservedBy()).isNull();
        assertThat(apartment.isReserved()).isEqualTo(false);
        verify(apartmentStorage, times(2)).getApartmentById(apartmentId);
    }

    @Test
    public void releaseApartment_returnNot_Reserved_whenApartmentIsNotReserved(){
        int apartmentId = 7;
        Apartment apartment = new Apartment(2000);
        apartment.setId(apartmentId);

        given(apartmentStorage.getApartmentById(apartmentId)).willReturn(Optional.of(apartment));

        ResultCode resultCode = apartmentService.releaseApartment(apartmentId);
        assertThat(resultCode).isEqualTo(ResultCode.NOT_RESERVED);
        verify(apartmentStorage, times(2)).getApartmentById(apartmentId);
    }
    @Test
    public void releaseApartment_returnNot_Found_whenApartmentIsNotFound(){
        int apartmentId = 17;

        given(apartmentStorage.getApartmentById(apartmentId)).willReturn(Optional.empty());

        ResultCode resultCode = apartmentService.releaseApartment(apartmentId);
        assertThat(resultCode).isEqualTo(ResultCode.NOT_FOUND);
        verify(apartmentStorage).getApartmentById(apartmentId);
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
        mockApartmentList.getLast().setIsReserved(true);
        List<Apartment> expectedMockApartmentList = mockApartmentList.reversed();

        given(apartmentStorage.sortedApartmentByReservationStatus())
                .willReturn(expectedMockApartmentList);

        List<Apartment> apartments = apartmentService.getApartmentSortedByReservationStatus(page, pageSize);

        assertTrue(apartments.get(0).isReserved());
        verify(apartmentStorage).sortedApartmentByReservationStatus();
    }

    @Test
    public void getApartmentSortedByClientName_willReturnSortedList(){
        Client alice = new Client("Alice");
        Client bob = new Client("Bob");
        Client john = new Client("John");
        List<Apartment> mockApartmentList = setApartmentData();
        mockApartmentList.get(0).setReservedBy(john);
        mockApartmentList.get(1).setReservedBy(bob);
        mockApartmentList.get(2).setReservedBy(alice);

        given(apartmentStorage.sortApartmentByClientName())
                .willReturn(mockApartmentList);

        List<Apartment> apartments = apartmentService.getApartmentSortedByClientName(page, pageSize);
        assertThat(apartments.get(0).getReservedBy().getName()).isEqualTo(john.getName());
        assertThat(apartments.get(1).getReservedBy().getName()).isEqualTo(bob.getName());
        assertThat(apartments.get(2).getReservedBy().getName()).isEqualTo(alice.getName());

        verify(apartmentStorage).sortApartmentByClientName();
    }

    // Here method pagingApartments is not interacting with ApartmentStorage class
    @Test
    public void pagingApartments_willReturnEmptyLis(){
        List<Apartment> apartments = new ArrayList<>();

        List<Apartment> pagedList = apartmentService.pagingApartments(0, 0, apartments);
        assertEquals(apartments.isEmpty(), pagedList.isEmpty());
    }

    // Here method pagingApartments is not interacting with ApartmentStorage class
    @Test
    public void pagingApartments_willReturnPagedList(){
        List<Apartment> apartments = setApartmentData();

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