package kz.andersen.java_intensive_13;

import kz.andersen.java_intensive_13.console.UserConsole;
import kz.andersen.java_intensive_13.services.ApartmentService;

public class EntryPoint {
    public static void main(String[] args) {
        ApartmentService apartmentService = new ApartmentService();
        UserConsole console = new UserConsole(apartmentService);
        console.start();
    }
}

