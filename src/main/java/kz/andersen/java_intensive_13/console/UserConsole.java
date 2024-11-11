package kz.andersen.java_intensive_13.console;

import kz.andersen.java_intensive_13.collector.StateCollector;
import kz.andersen.java_intensive_13.collector.CollectorManager;
import kz.andersen.java_intensive_13.config.PropertyLoader;
import kz.andersen.java_intensive_13.enums.ApplicationOperations;
import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.Client;
import kz.andersen.java_intensive_13.services.ApartmentService;

import java.time.LocalDateTime;
import java.util.*;

public class UserConsole {
    private static final int MIN_COMMAND_PARTS_LENGTH = 2;
    private static final int PAGE_INDEX = 2;
    private static final int PAGE_SIZE_INDEX = 3;
    private ApartmentService apartmentService;
    private final CollectorManager<StateCollector> collectorManager = new CollectorManager();
    private final PropertyLoader propertyLoader = new PropertyLoader();
    private final Scanner scanner = new Scanner(System.in);
    private final String filePath = propertyLoader.getStateFilePath();

    public UserConsole() {
    }

    public UserConsole(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    public void start() {
        System.out.println("""
                  Welcome to the Apartment Management System!
                  Type 'help' for a list of commands.
                """);

        boolean running = true;

        while (running) {
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
                case "changeable" -> setReservationStatusChangeability(commandParts);
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
        if (commandParts.length < MIN_COMMAND_PARTS_LENGTH) {
            System.out.println("Usage: register <price>");
            return;
        }
        try {

            double price = Double.parseDouble(commandParts[1]);
            int id = apartmentService.registerApartment(price);
            System.out.println("Apartment registered with price: " + price + ", apartment ID: " + id);
            Apartment apartment = apartmentService.getApartment(id).orElse(null);
            collectorManager.saveState(StateCollector.collectState(apartment,
                    ApplicationOperations.REGISTER,
                    Arrays.toString(commandParts),
                    ResultCode.SUCCESS), filePath);
        } catch (Exception e) {
            System.out.println("Invalid price format. Please enter a valid number.");
        }
    }

    private void reserveApartment(String[] commandParts) {
        if (commandParts.length < MIN_COMMAND_PARTS_LENGTH) {
            System.out.println("Usage: reserve <apartmentId> <clientName");
            return;
        }

        if(propertyLoader.getReservationStatusState().equals("false")) {
            System.out.println("""
                                Reservation status is not changeable.
                                Try: "changeable <true>" command to set changeability
                                """);
            return;
        }

        try {
            int apartmentId = Integer.parseInt(commandParts[1]);
            String clientName = commandParts[2];
            Client client = new Client(clientName);
            ResultCode resultCode = apartmentService.reserveApartment(apartmentId, client);
            switch (resultCode) {
                case NOT_FOUND -> System.out.println("Apartment with id " + apartmentId + " is not found");
                case RESERVED -> System.out.println("Apartment already reserved.");
                case SUCCESS -> System.out.println("Apartment successfully reserved by " + client.getName());
            }
            Apartment apartment = apartmentService.getApartment(apartmentId).orElse(null);
            collectorManager.saveState(StateCollector.collectState(apartment,
                    ApplicationOperations.RESERVE,
                    Arrays.toString(commandParts),
                    resultCode), filePath);
        } catch (Exception e) {
            System.out.println("Invalid apartment ID format. Please enter a valid number.");
        }
    }

    private void releaseApartment(String[] commandParts) {
        if (commandParts.length < MIN_COMMAND_PARTS_LENGTH) {
            System.out.println("Usage: release <apartmentId>");
            return;
        }
        if (propertyLoader.getReservationStatusState().equals("false")) {
            System.out.println("""
                    Reservation status is not changeable.
                    Try: "changeable <true>" command to set changeability
                    """);
            return;
        }

        try {
            int apartmentId = Integer.parseInt(commandParts[1]);
            ResultCode resultCode = apartmentService.releaseApartment(apartmentId);
            switch (resultCode) {
                case NOT_FOUND -> System.out.println("Apartment with id " + apartmentId + " is not found");
                case NOT_RESERVED -> System.out.println("This apartment with id " + apartmentId + " is not reserved.");
                case SUCCESS -> System.out.println("Apartment with id " + apartmentId + " released.");
            }
            Apartment apartment = apartmentService.getApartment(apartmentId).orElse(null);
            collectorManager.saveState(StateCollector.collectState(apartment,
                    ApplicationOperations.RELEASE,
                    Arrays.toString(commandParts),
                    resultCode), filePath);
        } catch (Exception e) {
            System.out.println("Invalid apartment ID format. Please enter a valid number.");
        }
    }

    private void listApartment(String[] commandParts) {
        int page = 1;
        int pageSize = 5;
        try {
            if (commandParts.length >= PAGE_INDEX) {
                page = Integer.parseInt(commandParts[1]);
            }
            if (commandParts.length >= PAGE_SIZE_INDEX) {
                pageSize = Integer.parseInt(commandParts[2]);
            }
            ResultCode resultCode;
            List<Apartment> apartments = apartmentService.pagingApartments(page,
                    pageSize, apartmentService.getAllApartments());
            if (apartments.isEmpty()) {
                System.out.println("No apartments found on this page.");
                resultCode = ResultCode.ERROR;
            } else {
                System.out.println("Listing apartments (Page " + page + "):");
                for (Apartment apartment : apartments) {
                    System.out.println(apartment.toString());
                }
                resultCode = ResultCode.SUCCESS;
            }
            collectorManager.saveState(StateCollector.collectState(apartments,
                    ApplicationOperations.LISTING,
                    Arrays.toString(commandParts), resultCode
            ), filePath);
        } catch (Exception e) {
            System.out.println("Invalid page format. Please enter a valid number.");
        }
    }

    private void sortedApartment(String[] commandParts) {
        int page = 1;
        int pageSize = 5;
        if (commandParts.length <= MIN_COMMAND_PARTS_LENGTH) {
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
            case ("id") -> {
                List<Apartment> apartmentsSortedById =
                        apartmentService.getApartmentsSortedById(page, pageSize);
                collectorManager.saveState(StateCollector.collectState(apartmentsSortedById,
                        ApplicationOperations.SORTING_BY_ID,
                        Arrays.toString(commandParts), ResultCode.SUCCESS), filePath);
                apartmentsSortedById.forEach(apartment -> System.out.println(apartment.toString()));
            }
            case ("price") -> {
                List<Apartment> apartmentsSortedByPrice =
                        apartmentService.getApartmentsSortedByPrice(page, pageSize);
                collectorManager.saveState(StateCollector.collectState(apartmentsSortedByPrice,
                        ApplicationOperations.SORTING_BY_PRICE,
                        Arrays.toString(commandParts), ResultCode.SUCCESS), filePath);
                apartmentsSortedByPrice.forEach(apartment -> System.out.println(apartment.toString()));
            }
            case ("isreserved") -> {
                List<Apartment> apartmentsSortedByReservationStatus =
                        apartmentService.getApartmentSortedByReservationStatus(page, pageSize);
                collectorManager.saveState(StateCollector.collectState(apartmentsSortedByReservationStatus,
                        ApplicationOperations.SORTING_BY_RESERVATION_STATUS,
                        Arrays.toString(commandParts), ResultCode.SUCCESS), filePath);
                apartmentsSortedByReservationStatus.forEach(apartment -> System.out.println(apartment.toString()));
            }
            case ("reservedby") -> {
                List<Apartment> apartmentsSortedByClientName =
                        apartmentService.getApartmentSortedByClientName(page, pageSize);
                collectorManager.saveState(StateCollector.collectState(apartmentsSortedByClientName,
                        ApplicationOperations.SORTING_BY_CLIENT_NAME,
                        Arrays.toString(commandParts), ResultCode.SUCCESS), filePath);
                apartmentsSortedByClientName.forEach(apartment -> System.out.println(apartment.toString()));
            }
            default -> System.out.println("Unknown command. Type 'help' for a list of command.");
        }
    }

    public void setReservationStatusChangeability(String[] commandParts){
        if (commandParts.length < MIN_COMMAND_PARTS_LENGTH){
            System.out.println("Usage: changeable <true/false>");
            return;
        }
        String changeable = commandParts[1];
        switch (changeable) {
            case ("off") -> {
                propertyLoader.setReservationStatusState(false);
                System.out.println("You have disabled the reserve changeability status");
            }
            case ("on") -> {
                propertyLoader.setReservationStatusState(true);
                System.out.println("You have enabled the reserve changeability status");
            }
            default -> System.out.println("Unknown command. Type 'help' for a list of command.");
        }

    }

    private void showHelp() {
        System.out.println("""
                Available commands:
                register <price>                        - Register a new apartment with the given price.
                reserve <id> <name>                     - Reserve an apartment for a client.
                release <id>                            - Release a reservation for an apartment.
                list [<page> <pageSize>]                - List apartments with optional pagination.
                sorted by <field> [<page> <pageSize>]   - List apartments sorted by a specified fields.
                                                          Fields: id, price, isReserved, reservedBy
                changeable <on/off>                     - Set reservation status changeability                         
                help                                    - Show this help message.
                exit                                    - Exit the application.
                """);
    }
}
