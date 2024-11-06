package kz.andersen.java_intensive_13.services;

import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.Client;
import kz.andersen.java_intensive_13.repository.ApartmentStorage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceTest {

    @Mock
    ApartmentStorage apartmentStorage;

    @Mock
    ApartmentService apartmentService;

    static int page = 1;
    static int pageSize = 5;

    public ApartmentServiceTest(){
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void setUp() throws Exception {
        Field counterField = Apartment.class.getDeclaredField("counter");
        counterField.setAccessible(true);
        counterField.set(null, 1);
    }

    @Test
    public void reserveApartment_returnSuccessCode_whenApartmentIsReserved() {
            Client client = new Client("Gorge");
            int apartmentId = 3;
            given(apartmentService.reserveApartment(apartmentId, client)).willReturn(ResultCode.SUCCESS);
            ResultCode resultCode = apartmentService.reserveApartment(apartmentId, client);
            assertThat(resultCode).isEqualTo(ResultCode.SUCCESS);
            verify(apartmentService).reserveApartment(apartmentId, client);
    }
    @Test
    public void reserveApartment_returnNot_Found_whenApartmentIsNotFound() {
        Client client = new Client("Gorge");
        int apartmentId = 15;
        given(apartmentService.reserveApartment(apartmentId, client)).willReturn(ResultCode.NOT_FOUND);
        ResultCode resultCode = apartmentService.reserveApartment(apartmentId, client);
        assertThat(resultCode).isEqualTo(ResultCode.NOT_FOUND);
        verify(apartmentService).reserveApartment(apartmentId, client);
    }

    @Test
    public void reserveApartment_returnReservedCode_whenApartmentIsReserved() {
        Client client = new Client("Gorge");
        int apartmentId = 9;
        given(apartmentService.reserveApartment(apartmentId, client)).willReturn(ResultCode.RESERVED);
        ResultCode resultCode = apartmentService.reserveApartment(apartmentId, client);
        assertThat(resultCode).isEqualTo(ResultCode.RESERVED);
        verify(apartmentService).reserveApartment(apartmentId, client);
    }

    @Test
    public void registerApartment_returnApartmentId_whenApartmentIsRegistered(){
        int apartmentPrice = 4000;
        given(apartmentService.registerApartment(apartmentPrice)).willReturn(11);
        int apartmentId = apartmentService.registerApartment(apartmentPrice);
        assertThat(apartmentId).isEqualTo(11);
        verify(apartmentService).registerApartment(apartmentPrice);
    }

    @Test
    public void releaseApartment_returnSuccess_whenApartmentIsReleased(){
        int apartmentId = 7;
        given(apartmentService.releaseApartment(apartmentId)).willReturn(ResultCode.SUCCESS);
        ResultCode resultCode = apartmentService.releaseApartment(apartmentId);
        assertThat(resultCode).isEqualTo(ResultCode.SUCCESS);
        verify(apartmentService).releaseApartment(apartmentId);
    }

    @Test
    public void releaseApartment_returnNot_Reserved_whenApartmentIsNotReserved(){
        int apartmentId = 7;
        given(apartmentService.releaseApartment(apartmentId)).willReturn(ResultCode.NOT_RESERVED);
        ResultCode resultCode = apartmentService.releaseApartment(apartmentId);
        assertThat(resultCode).isEqualTo(ResultCode.NOT_RESERVED);
        verify(apartmentService).releaseApartment(apartmentId);
    }
    @Test
    public void releaseApartment_returnNot_Found_whenApartmentIsNotFound(){
        int apartmentId = 17;
        given(apartmentService.releaseApartment(apartmentId)).willReturn(ResultCode.NOT_FOUND);
        ResultCode resultCode = apartmentService.releaseApartment(apartmentId);
        assertThat(resultCode).isEqualTo(ResultCode.NOT_FOUND);
        verify(apartmentService).releaseApartment(apartmentId);
    }

    @Test
    public void getApartmentsSortedByPrice_willReturnSortedList(){
        List<Apartment> mockSortedApartments = setApartmentData();

        given(apartmentService.getApartmentsSortedByPrice(page, pageSize)).willReturn(mockSortedApartments);
        List<Apartment> apartments = apartmentService.getApartmentsSortedByPrice(page, pageSize);
        assertThat(apartments).hasSize(pageSize);
        assertThat(apartments.get(0).getPrice()).isEqualTo(1000);
        assertThat(apartments.get(1).getPrice()).isEqualTo(1500);
        assertThat(apartments.get(2).getPrice()).isEqualTo(2000);
        assertThat(apartments.get(3).getPrice()).isEqualTo(2500);
        assertThat(apartments.get(4).getPrice()).isEqualTo(3000);
        verify(apartmentService).getApartmentsSortedByPrice(page, pageSize);
    }

    @Test
    public void getApartmentsSortedById_willReturnSortedList(){
        List<Apartment> mockApartmentList = setApartmentData();

        given(apartmentService.getApartmentsSortedById(page, pageSize)).willReturn(mockApartmentList);
        List<Apartment> apartments = apartmentService.getApartmentsSortedById(page, pageSize);
        assertThat(apartments.get(0).getId()).isEqualTo(1);
        assertThat(apartments.get(1).getId()).isEqualTo(2);
        assertThat(apartments.get(2).getId()).isEqualTo(3);
        assertThat(apartments.get(3).getId()).isEqualTo(4);
        assertThat(apartments.get(4).getId()).isEqualTo(5);
        verify(apartmentService).getApartmentsSortedById(page, pageSize);
    }

    @Test
    public void getApartmentSortedByReservationStatus_willReturnSortedList(){
        List<Apartment> mockApartmentList = setApartmentData();
        mockApartmentList.getLast().setIsReserved(true);
        List<Apartment> expectedMockApartmentList = mockApartmentList.reversed();

        given(apartmentService.getApartmentSortedByReservationStatus(page, pageSize))
                .willReturn(expectedMockApartmentList);

        List<Apartment> apartments = apartmentService.getApartmentSortedByReservationStatus(page, pageSize);

        assertTrue(apartments.get(0).isReserved());
        verify(apartmentService).getApartmentSortedByReservationStatus(page, pageSize);
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

        given(apartmentService.getApartmentSortedByClientName(page, pageSize))
                .willReturn(mockApartmentList);

        List<Apartment> apartments = apartmentService.getApartmentSortedByClientName(page, pageSize);
        assertThat(apartments.get(0).getReservedBy().getName()).isEqualTo(john.getName());
        assertThat(apartments.get(1).getReservedBy().getName()).isEqualTo(bob.getName());
        assertThat(apartments.get(2).getReservedBy().getName()).isEqualTo(alice.getName());

        verify(apartmentService).getApartmentSortedByClientName(page, pageSize);
    }

    @Test
    public void pagingApartments_willReturnEmptyLis(){
        List<Apartment> apartments = new ArrayList<>();
        given(apartmentService.pagingApartments(0, 0, apartments))
                .willReturn(apartments);
        List<Apartment> pagedList = apartmentService.pagingApartments(0, 0, apartments);
        assertThat(pagedList).isEmpty();

        verify(apartmentService).pagingApartments(0, 0, apartments);
    }

    @Test
    public void pagingApartments_willReturnPagedList(){
        List<Apartment> apartments = setApartmentData();
        given(apartmentService.pagingApartments(page, pageSize, apartments))
                .willReturn(apartments);
        List<Apartment> pagedList = apartmentService.pagingApartments(page, pageSize, apartments);
        assertThat(pagedList).isEqualTo(apartments);

        verify(apartmentService).pagingApartments(page, pageSize, apartments);
    }

    private @NotNull List<Apartment> setApartmentData(){
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