package console;

import models.Apartment;
import models.Client;
import services.ApartmentService;

import java.util.List;
import java.util.Scanner;

public class UserConsole {
    private static final int MIN_COMMAND_PARTS_LENGTH = 2;
    private static final int PAGE_INDEX = 2;
    private static final int PAGE_SIZE_INDEX = 3;
    private final ApartmentService apartmentService = new ApartmentService();
    private final Scanner scanner = new Scanner(System.in);

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

    private void registerApartment(String[] commandParts) {
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

    private void reserveApartment(String[] commandParts){
        if (commandParts.length < MIN_COMMAND_PARTS_LENGTH){
            System.out.println("Usage: reserve <apartmentId> <clientName");
        }

        try{
            int apartmentId = Integer.parseInt(commandParts[1]);
            String clientName = commandParts[2];
            Client client = new Client(clientName);
            apartmentService.reserveApartment(apartmentId, client);
        }catch (NumberFormatException e){
            System.out.println("Invalid apartment ID format. Please enter a valid number.");
        }
    }

    private void releaseApartment(String[] commandParts){
        if (commandParts.length < MIN_COMMAND_PARTS_LENGTH){
            System.out.println("Usage: release <apartmentId>");
            return;
        }

        try{
            int apartmentId = Integer.parseInt(commandParts[1]);
            apartmentService.releaseApartment(apartmentId);
        }catch (NumberFormatException e){
            System.out.println("Invalid apartment ID format. Please enter a valid number.");
        }
    }

    private void listApartment(String[] commandParts){
        int page = 1;
        int pageSize = 5;

        if (commandParts.length >= PAGE_INDEX){
            page = Integer.parseInt(commandParts[1]);
        }
        if(commandParts.length >= PAGE_SIZE_INDEX){
            pageSize = Integer.parseInt(commandParts[2]);
        }

        List<Apartment> apartments = apartmentService.pagingApartments(page, pageSize);
        if (apartments.isEmpty()){
            System.out.println("No apartments found on this page.");
        }else {
            System.out.println("Listing apartments (Page " + page + "):");
            for (Apartment apartment:apartments) {
                System.out.println(apartment.toString());
            }
        }
    }

    private void sortedApartment(String[] commandParts){
        int page = 1;
        int pageSize = 5;
        if (commandParts.length < MIN_COMMAND_PARTS_LENGTH){
            System.out.println("Usage: sorted by <field> <page> <pageSize>");
            return;
        }

        if(commandParts.length >= PAGE_INDEX + 2){
            page = Integer.parseInt(commandParts[3]);
        }
        if(commandParts.length >= PAGE_SIZE_INDEX + 2){
            pageSize = Integer.parseInt(commandParts[4]);
        }

        String field = commandParts[2].toLowerCase();
        switch (field){
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
