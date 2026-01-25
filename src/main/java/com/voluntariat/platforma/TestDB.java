package com.voluntariat.platforma;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDB {
    public static void main(String[] args) {
        // Aceleasi date ca in application.properties
        String url = "jdbc:mysql://localhost:3306/aplicatie_web_voluntariat";
        String user = "root";
        String password = "Tudor2005"; // Lasa gol daca nu ai parola in XAMPP

        System.out.println("--- TEST CONEXIUNE MYSQL ---");
        try {
            // 1. Incarcam driverul manual (ca sa fim siguri)
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("1. Driver MySQL gasit!");

            // 2. Incercam conectarea
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("2. CONEXIUNE REUSITA! Baza de date raspunde.");

            connection.close();
            System.out.println("--- TEST FINALIZAT CU SUCCES ---");
        } catch (Exception e) {
            System.err.println("!!! EROARE GRAVA: NU MA POT CONECTA LA MYSQL !!!");
            System.err.println("Cauza: " + e.getMessage());
            e.printStackTrace();
        }
    }
}