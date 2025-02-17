package org.example;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws SQLException {

        Connection dbConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test",
                "root",
                "12345678"
        );

        System.out.println("1. Išvesti visų darbuotojų vardus ir pavardes:");
        printWorkersFromMysql(dbConnection);
        System.out.println("2. Įšvesti visus projektus ir juose dirbančius žmones:");
        printProjectsWithWorkersFromMysql(dbConnection);
        System.out.println("3. Parašyti programą, leidžiančią įvesti naujus darbuotojus");

        Scanner sc = new Scanner(System.in);
        // asmenskodas-vardas-pavarde-dribanuo-gimimometai-pareigos-skyrius_pavadinimas-projektas_id

        while (true){
            System.out.println("Ar norite įvesti darbuotoją? taip/ne");
            String answer = sc.nextLine();
            if (answer.equalsIgnoreCase("ne")){
                break;
            }
            System.out.println("Įveskite asmens kodą:");
            String pc = sc.nextLine();
            System.out.println("Įveskite vardą:");
            String name = sc.nextLine();
            System.out.println("Įveskite pavardę:");
            String lastname = sc.nextLine();
            System.out.println("Įveskite nuo kada dirba, formatu (YYYY-MM-DD) :");
            LocalDate employedWhen = LocalDate.parse(sc.nextLine(),DateTimeFormatter.ISO_LOCAL_DATE );
            System.out.println("Įveskite gimimo datą, formatu (YYYY-MM-DD) :");
            LocalDate dateOfBirth = LocalDate.parse(sc.nextLine(),DateTimeFormatter.ISO_LOCAL_DATE);
            System.out.println("Įveskite pareigas (Programuotojas, Testuotojas, Porjekto vadovas) :");
            String position = sc.nextLine();
            System.out.println("Įveskite skyriaus pavadinimą (C#, Java, Testavimo) :");
            String depName = sc.nextLine();
            System.out.println("Įveskite projekto ID (1,2,3) :");
            int projectID = Integer.parseInt(sc.nextLine());
            insertWorkerPreparedStatement(dbConnection, pc, name, lastname, employedWhen, dateOfBirth,
                    position, depName, projectID);
        }

        System.out.println("4. Papildyti programą galimybe priskirti darbuotoją projektui");
        while (true){
            System.out.println("Ar norite priskirti darbuotoją projektui? taip/ne: ");
            String answer = sc.nextLine();
            if (answer.equalsIgnoreCase("ne")){
                break;
            }
            System.out.println("Įveskite darbuotojo pavardę: ");
            String lastName = sc.nextLine();
            System.out.println("Įvesktie norimo priskirti projekto ID (1,2,3) : ");
            int projectID = Integer.parseInt(sc.nextLine());
            assignWorkerToProject(dbConnection,lastName,projectID);
        }
    }

    private static void assignWorkerToProject(Connection connection, String workerLastName, int projectID) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(
                "UPDATE darbuotojas SET projektas_id = ? WHERE pavarde = ?"
        )){
            connection.setAutoCommit(false);
            statement.setInt(1,projectID);
            statement.setString(2,workerLastName);
            statement.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            System.out.println(e);
        } finally {
            connection.setAutoCommit(true);
            System.out.println("Update complete!");
        }
    }
    private static void insertWorkerPreparedStatement(Connection connection, String pc,
            String name, String lastname, LocalDate employedWhen, LocalDate dateOfBirth, String position,
            String depName, int projectID)
            throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO darbuotojas VALUES(?,?,?,?,?,?,?,?)");){
            connection.setAutoCommit(false);
            statement.setString(1,pc);
            statement.setString(2,name);
            statement.setString(3,lastname);
            statement.setDate(4, Date.valueOf(employedWhen));
            statement.setDate(5, Date.valueOf(dateOfBirth));
            statement.setString(6,position);
            statement.setString(7,depName);
            statement.setInt(8,projectID);
            statement.execute();
            connection.commit();
        } catch (SQLException e){
            connection.rollback();
            System.out.println(e);
        } finally {
            connection.setAutoCommit(true);
            System.out.println("Worker added to databse!");
        }
    }


    public static void printWorkersFromMysql(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM darbuotojas");
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            System.out.print(rs.getString("vardas"));
            System.out.print(" ");
            System.out.print(rs.getString("pavarde"));
            System.out.println();
        }
    }

    public static void printProjectsWithWorkersFromMysql(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT projektas.pavadinimas, " +
                "darbuotojas.vardas, darbuotojas.pavarde " +
                "FROM darbuotojas JOIN projektas ON darbuotojas.projektas_id=projektas.id;");

        ResultSet rs = statement.executeQuery();
        List<Persons> personsFromMysql = new ArrayList<>();
        while (rs.next()) {
            personsFromMysql.add(new Persons(
                    rs.getString("pavadinimas"),
                    rs.getString("vardas"),
                    rs.getString("pavarde")
            ));
        }

        Map<String, List<Persons>> groupedByName = personsFromMysql.stream()
                .collect(Collectors.groupingBy(Persons::getPavadinimas));

        groupedByName.forEach((pavadinimas, persons) -> {
            String names = persons.stream()
                    .map(p -> p.getVardas() + " " + p.getPavarde())
                    .collect(Collectors.joining(", "));
            System.out.println(pavadinimas + " : \n" + names + "\n");
        });

    }
}