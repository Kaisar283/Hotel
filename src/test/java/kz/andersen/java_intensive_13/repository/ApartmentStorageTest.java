package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ApartmentStorageTest {

    private  ApartmentStorage apartmentStorage;

    @BeforeEach
    public  void setApartmentStorage() throws Exception {
        apartmentStorage = ApartmentStorage.getInstance();
        Field counterField = Apartment.class.getDeclaredField("counter");
        counterField.setAccessible(true);
        counterField.set(null, 1);
        apartmentStorage.addApartment(new Apartment(1000));
        apartmentStorage.addApartment(new Apartment(2000));
        apartmentStorage.addApartment(new Apartment(1300));
        apartmentStorage.addApartment(new Apartment(1500));
        apartmentStorage.addApartment(new Apartment(1900));
        apartmentStorage.addApartment(new Apartment(2200));
        apartmentStorage.addApartment(new Apartment(4400));
        apartmentStorage.addApartment(new Apartment(4000));
        apartmentStorage.addApartment(new Apartment(5000));
        apartmentStorage.addApartment(new Apartment(3500));
    }

    @Test
    public void getApartmentById_returnApartment_ifExists(){
        Apartment apartmentById = apartmentStorage.getApartmentById(3).get();
        assertThat(apartmentById).isNotNull();
        assertThat(apartmentById.getId()).isEqualTo(3);
    }

    @Test
    public void getApartmentById_returnEmpty_ifDoesNotExists(){
        Optional<Apartment> apartmentById = apartmentStorage.getApartmentById(20);
        assertThat(apartmentById).isEmpty();
    }

    @Test
    public void getApartments_returnListOfApartment(){
        List<Apartment> apartments = apartmentStorage.getApartments();
        int expectedSize = 10;
        assertEquals(10, apartments.size());
        assertEquals(1000, apartments.get(0).getPrice());
        assertEquals(2000, apartments.get(1).getPrice());
        assertEquals(1300, apartments.get(2).getPrice());
        assertEquals(1500, apartments.get(3).getPrice());
        assertEquals(1900, apartments.get(4).getPrice());
        assertEquals(2200, apartments.get(5).getPrice());
        assertEquals(4400, apartments.get(6).getPrice());
        assertEquals(4000, apartments.get(7).getPrice());
        assertEquals(5000, apartments.get(8).getPrice());
        assertEquals(3500, apartments.get(9).getPrice());
    }

    @Test
    public void addApartment_AddApartmentInstanceToList(){
        Apartment apartment = new Apartment(5000);
        apartmentStorage.addApartment(apartment);
        List<Apartment> apartments = apartmentStorage.getApartments();
        assertTrue(apartments.contains(apartment));
    }

    @Test
    public void sortApartmentByPrice_returnSortedList(){
        List<Apartment> sortedApartmentList = apartmentStorage.sortApartmentByPrice();

        for(int i = 0; i < sortedApartmentList.size() - 1; i++){
            assertTrue(
                    sortedApartmentList.get(i).getPrice() <= sortedApartmentList.get(i + 1).getPrice()
            );
        }
        assertEquals(apartmentStorage.getApartments().size(), sortedApartmentList.size());
    }

    @Test
    public void sortApartmentById_returnSortedList(){
        List<Apartment> sortedApartmentList = apartmentStorage.sortApartmentById();

        for (int i = 0; i < sortedApartmentList.size() - 1; i++) {
            assertTrue(
                    sortedApartmentList.get(i).getId() <= sortedApartmentList.get(i + 1).getId()
            );
        }
    }

    @Test
    public void sortApartmentByClientName_returnSortedList(){
        User alice = new User("Alice");
        User john = new User("John");
        User zara = new User("Zara");
        Apartment apartmentById = apartmentStorage.getApartmentById(5).get();
        apartmentById.setReservedBy(alice);
        Apartment johnsApartment = apartmentStorage.getApartmentById(4).get();
        johnsApartment.setReservedBy(john);
        Apartment zaraApartment = apartmentStorage.getApartmentById(7).get();
        zaraApartment.setReservedBy(zara);

        List<Apartment> sortedApartment = apartmentStorage.sortApartmentByClientName();

        assertEquals("Zara", sortedApartment.get(0).getReservedBy().getFistName());
        assertEquals("John", sortedApartment.get(1).getReservedBy().getFistName());
        assertEquals("Alice", sortedApartment.get(2).getReservedBy().getFistName());
        assertNull(null, String.valueOf(sortedApartment.get(3).getReservedBy()));
    }

    @Test
    public void sortedApartmentByReservationStatus_returnSortedList(){
        Apartment apartmentById = apartmentStorage.getApartmentById(9).get();
        apartmentById.setIsReserved(true);

        List<Apartment> apartments = apartmentStorage.sortedApartmentByReservationStatus();
        assertEquals(apartmentById.getId(), apartments.get(0).getId());
    }
}