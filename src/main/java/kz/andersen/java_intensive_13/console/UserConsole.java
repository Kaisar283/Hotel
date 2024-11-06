package kz.andersen.java_intensive_13.console;

import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.Client;
import kz.andersen.java_intensive_13.services.ApartmentService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Scanner;

public class UserConsole {
    private static final int MIN_COMMAND_PARTS_LENGTH = 2;
    private static final int PAGE_INDEX = 2;
    private static final int PAGE_SIZE_INDEX = 3;
    private ApartmentService apartmentService;
    private final Scanner scanner = new Scanner(System.in);

    public UserConsole(){
    }

    public UserConsole(ApartmentService apartmentService){
        this.apartmentService = apartmentService;
    }

    public void start(){
        System.out.println("""
                              Welcome to the Apartment Management System!
                              Type 'help' for a list of commands.
                            """);

        boolean running = true;

        while (running){
            System.out.println("\n ");
            String input = scanner.nextLine().trim();
            String[] commandParts = input.split(" ");
            String command = commandParts[0].toLowerCase();

            switch (command) {
                case "register" -> registerApartment(commandParts);
                case "reserve" -> reserveApartment(commandParts);
                case "release" -> releaseApartment(commandParts);
                case "list" -> listApartment(commandParts);
                case "sorted" -> sortedApartment(commandParts);
                case "help" -> showHelp();
                case "exit" -> {
                    running = false;
                    System.out.println("Exiting the system. Goodbye!");
                }
                default -> System.out.println("Unknown command. Type 'help' for a list of command.");
            }
        }
        scanner.close();
    }

    private void registerApartment(String @NotNull [] commandParts) {
        if(commandParts.length < MIN_COMMAND_PARTS_LENGTH){
            System.out.println("Usage: register <price>");
            return;
        }
        try{

            double price = Double.parseDouble(commandParts[1]);
            int id = apartmentService.registerApartment(price);
            System.out.println("Apartment registered with price: " + price + ", apartment ID: " + id);
        }catch (NumberFormatException e){
            System.out.println("Invalid price format. Please enter a valid number.");
        }
    }

    private void reserveApartment(String @NotNull [] commandParts){
        if (commandParts.length < MIN_COMMAND_PARTS_LENGTH){
            System.out.println("Usage: reserve <apartmentId> <clientName");
            return;
        }

        try{
            int apartmentId = Integer.parseInt(commandParts[1]);
            String clientName = commandParts[2];
            Client client = new Client(clientName);
            ResultCode resultCode = apartmentService.reserveApartment(apartmentId, client);
            switch (resultCode){
                case NOT_FOUND -> System.out.println("Apartment with id " + apartmentId + " is not found");
                case RESERVED -> System.out.println("Apartment already reserved.");
                case SUCCESS -> System.out.println("Apartment successfully reserved by " + client.getName());
            }
        }catch (NumberFormatException e){
            System.out.println("Invalid apartment ID format. Please enter a valid number.");
        }
    }

    private void releaseApartment(String @NotNull [] commandParts){
        if (commandParts.length < MIN_COMMAND_PARTS_LENGTH){
            System.out.println("Usage: release <apartmentId>");
            return;
        }

        try{
            int apartmentId = Integer.parseInt(commandParts[1]);
            ResultCode resultCode = apartmentService.releaseApartment(apartmentId);
            switch (resultCode){
                case NOT_FOUND -> System.out.println("Apartment with id " + apartmentId + " is not found");
                case NOT_RESERVED -> System.out.println("This apartment with id " + apartmentId + " is not reserved.");
                case SUCCESS -> System.out.println("Apartment with id " + apartmentId + " released.");
            }
        }catch (NumberFormatException e){
            System.out.println("Invalid apartment ID format. Please enter a valid number.");
        }
    }

    private void listApartment(String @NotNull [] commandParts){
        int page = 1;
        int pageSize = 5;
        try{
            if (commandParts.length >= PAGE_INDEX){
                page = Integer.parseInt(commandParts[1]);
            }
            if(commandParts.length >= PAGE_SIZE_INDEX){
                pageSize = Integer.parseInt(commandParts[2]);
            }

            List<Apartment> apartments = apartmentService.pagingApartments(page, pageSize, apartmentService.getAllApartments());
            if (apartments.isEmpty()){
                System.out.println("No apartments found on this page.");
            }else {
                System.out.println("Listing apartments (Page " + page + "):");
                for (Apartment apartment:apartments) {
                    System.out.println(apartment.toString());
                }
            }
        }catch (NumberFormatException e){
            System.out.println("Invalid page format. Please enter a valid number.");
        }
    }

    private void sortedApartment(String @NotNull [] commandParts){
        int page = 1;
        int pageSize = 5;
        if (commandParts.length <= MIN_COMMAND_PARTS_LENGTH){
            System.out.println("Usage: sorted by <field> <page> <pageSize>");
            return;
        }

        if (commandParts.length >= PAGE_INDEX + 2) {
            page = Integer.parseInt(commandParts[3]);
        }
        if (commandParts.length >= PAGE_SIZE_INDEX + 2) {
            pageSize = Integer.parseInt(commandParts[4]);
        }

        String field = commandParts[2].toLowerCase();
        switch (field) {
            case "id" -> apartmentService.getApartmentsSortedById(page, pageSize)
                    .forEach(apartment -> System.out.println(apartment.toString()));
            case "price" -> apartmentService.getApartmentsSortedByPrice(page, pageSize)
                    .forEach(apartment -> System.out.println(apartment.toString()));
            case "isreserved" -> apartmentService.getApartmentSortedByReservationStatus(page, pageSize)
                    .forEach(apartment -> System.out.println(apartment.toString()));
            case "reservedby" -> apartmentService.getApartmentSortedByClientName(page, pageSize)
                    .forEach(apartment -> System.out.println(apartment.toString()));
            default -> System.out.println("Unknown command. Type 'help' for a list of command.");
        }
    }
    private void showHelp(){
        System.out.println("""
                Available commands:
                register <price>                        - Register a new apartment with the given price.
                reserve <id> <name>                     - Reserve an apartment for a client.
                release <id>                            - Release a reservation for an apartment.
                list [<page> <pageSize>]                - List apartments with optional pagination.
                sorted by <field> [<page> <pageSize>]   - List apartments sorted by a specified fields.
                                                          Fields: id, price, isReserved, reservedBy
                help                                    - Show this help message.
                exit                                    - Exit the application.
                """);
    }
}
