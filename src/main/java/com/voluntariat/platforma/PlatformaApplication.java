package com.voluntariat.platforma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlatformaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformaApplication.class, args);

        System.out.println("=============================================");
        System.out.println("   SERVER PORNIT - FORTAT SA RAMANA ACTIV    ");
        System.out.println("   Acceseaza: http://localhost:8080/login    ");
        System.out.println("=============================================");

        // Blocare fir de executie pentru a preveni inchiderea
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}