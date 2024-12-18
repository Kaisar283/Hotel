package kz.andersen.java_intensive_13.console;

import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;
import kz.andersen.java_intensive_13.repository.ApartmentStorage;
import kz.andersen.java_intensive_13.services.ApartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("These tests are disabled, because UserConsole.class no longer in use")
class UserConsoleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private UserConsole userConsole;

    private ApartmentService apartmentService;

    @BeforeEach
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        Field counterField = Apartment.class.getDeclaredField("counter");
        counterField.setAccessible(true);
        counterField.set(null, 1);
    }


    public void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        prepareData();
        this.userConsole = new UserConsole(apartmentService);
    }

    @Test
    public void registerApartment_successfullyRegistred() {
        String simulatedInput = "register 1500\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Apartment registered with price: 1500");
    }

    @Test
    public void registerApartment_invalidPriceFormat() {
        String simulatedInput = "register abc\nexit\n";
        provideInput(simulatedInput);

        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Invalid price format. Please enter a valid number.");
    }

    @Test
    public void registerApartment_tooShortCommand() {
        String simulatedInput = "register\nexit\n";
        provideInput(simulatedInput);

        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Usage: register <price>");
    }

    @Test
    public void apartmentReservedSuccessfully() {
        String simulatedInput = "reserve 1 John\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Apartment successfully reserved by");
    }
    @Test
    public void apartmentAlreadyReserved() {
        String simulatedInput = "reserve 2 John\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Apartment already reserved.");
    }

    @Test
    public void reserveApartment_apartmentNotFound(){
        String simulatedInput = "reserve 15 John\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Apartment with id 15 is not found");
    }

    @Test
    public void reserveApartment_tooShortCommand(){
        String simulatedInput = "reserve\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Usage: reserve <apartmentId> <clientName");
    }

    @Test
    public void reserveApartment_invalidIdFormat(){
        String simulatedInput = "reserve something\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Invalid apartment ID format. Please enter a valid number.");
    }

    @Test
    public void releaseApartment_tooShortCommand(){
        String simulatedInput = "release\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Usage: release <apartmentId");
    }

    @Test
    public void releaseApartment_apartmentNotFound(){
        String simulatedInput = "release 10\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Apartment with id 10 is not found");
    }

    @Test
    public void releaseApartment_apartmentNotReserved(){
        String simulatedInput = "release 3\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("This apartment with id 3 is not reserved.");
    }

    @Test
    public void releaseApartment_successfullyReleased(){
        String simulatedInput = "release 2\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Apartment with id 2 released.");
    }

    @Test
    public void releaseApartment_invalidIdFormat(){
        String simulatedInput = "release something\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Invalid apartment ID format. Please enter a valid number.");
    }

    @Test
    public void listApartment_pagingApartmentList(){
        String simulatedInput = "list 1\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Listing apartments");
    }

    @Test
    public void listApartment_emptyListChosen(){
        String simulatedInput = "list 3\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("No apartments found on this page.");
    }

    @Test
    public void listApartment_invalidIdFormat(){
        String simulatedInput = "list something\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Invalid page format. Please enter a valid number.");
    }

    @Test
    public void sortedApartment_tooShortCommand(){
        String simulatedInput = "sorted by\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Usage: sorted by <field> <page> <pageSize>");
    }

    @Test
    public void sortedApartment_invalidFormat(){
        String simulatedInput = "sorted by something\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Unknown command. Type 'help' for a list of command.");
    }

    @Test
    public void unknownCommand() {
        String simulatedInput = "unknownCommand\nexit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Unknown command. Type 'help' for a list of command.");
    }

    @Test
    public void exitCommand() {
        String simulatedInput = "exit\n";
        provideInput(simulatedInput);
        userConsole.start();

        String output = outContent.toString();

        assertThat(output).contains("Exiting the system. Goodbye!");
    }



    private void prepareData(){

        ApartmentStorage apartmentStorage = new ApartmentStorage();
        this.apartmentService = new ApartmentService();
        User alice = new User("Alice");
    }
}
