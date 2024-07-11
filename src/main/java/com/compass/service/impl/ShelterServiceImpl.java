package com.compass.service.impl;

import com.compass.dao.DaoFactory;
import com.compass.dao.ShelterDao;
import com.compass.exception.ResourceNotFoundException;
import com.compass.model.entities.Shelter;
import com.compass.service.ShelterService;

import java.util.List;
import java.util.Scanner;

public class ShelterServiceImpl implements ShelterService {

    private final Scanner sc = new Scanner(System.in);

    @Override
    public void create() {
        try {
            ShelterDao shelterDao = DaoFactory.createShelterDao();

            System.out.println("Insira os dados do novo abrigo");

            String name = getInput("Digite o nome do abrigo: ");
            validateNotBlank(name, "Name");
            validateSize(name, 255, "Name");

            String address = getInput("Digite o endereço do abrigo: ");
            validateNotBlank(address, "Address");
            validateSize(address, 255, "Address");

            String responsible = getInput("Digite o nome do responsável pelo abrigo: ");
            validateNotBlank(responsible, "Responsible person");
            validateSize(responsible, 255, "Responsible person");

            String phoneNumber = getInput("Digite o número de telefone do abrigo (+55(99)99999-9999): ");
            validateNotBlank(phoneNumber, "Phone number");
            validatePattern(phoneNumber, "^\\+55\\(\\d{2}\\)\\d{4,5}-\\d{4}$", "Invalid Brazilian phone number.");

            String email = getInput("Digite o email do abrigo: ");
            validateNotBlank(email, "Email");
            validateSize(email, 255, "Email");
            validateEmail(email);

            Integer capacity = Integer.parseInt(getInput("Digite a capacidade do abrigo: "));
            validateNotNull(capacity, "Capacity");
            validateMinValue(capacity, 1, "Capacity");

            double occupationPercentage = parseOccupation(getInput("Digite a ocupação do abrigo (%): "));
            Integer occupation = (int) (occupationPercentage * capacity) / 100;
            validateNotNull(occupation, "Occupation");
            validateMinValue(occupation, 0, "Occupation");
            validateMaxValue(occupation, 100, "Occupation");

            Shelter shelter = createShelter(name, address, responsible, phoneNumber, email, capacity, occupation);

            shelterDao.save(shelter);
            System.out.println("Abrigo registrado.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void read() {
        try {
            ShelterDao shelterDao = DaoFactory.createShelterDao();
            Long id = Long.parseLong(getInput("Digite o id do abrigo a ser buscado: "));
            Shelter shelter = findShelter(shelterDao, id);
            System.out.println(shelter);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void readAll() {
        try {
            ShelterDao shelterDao = DaoFactory.createShelterDao();
            System.out.println("Lista de abrigos registrados");
            List<Shelter> shelters = shelterDao.findAll();
            if (shelters.isEmpty()) {
                System.out.println("Nenhum abrigo foi encontrado.");
                return;
            }
            shelters.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        try {
            ShelterDao shelterDao = DaoFactory.createShelterDao();

            Long id = Long.parseLong(getInput("Digite o id do abrigo a ser atualizado: "));
            Shelter shelter = findShelter(shelterDao, id);

            String address = getInput("Digite o novo endereço do abrigo: ");
            validateNotBlank(address, "Address");
            validateSize(address, 255, "Address");

            shelter.setAddress(address);

            shelterDao.update(shelter);
            System.out.println("Abrigo atualizado.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void delete() {
        try {
            ShelterDao shelterDao = DaoFactory.createShelterDao();
            Long id = Long.parseLong(getInput("Digite o id do abrigo a ser excluído: "));
            findShelter(shelterDao, id);
            shelterDao.delete(id);
            System.out.println("Abrigo deletado.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private double parseOccupation(String input) {
        if (input.endsWith("%")) {
            return Double.parseDouble(input.substring(0, input.length() - 1));
        }
        return Double.parseDouble(input);
    }

    private Shelter findShelter(ShelterDao shelterDao, Long id) {
        Shelter shelter = shelterDao.findById(id);
        if (shelter == null) {
            throw new ResourceNotFoundException("Abrigo não encontrado.");
        }
        return shelter;
    }

    private Shelter createShelter(String name, String address, String responsible, String phoneNumber, String email, Integer capacity, Integer occupation) {
        return new Shelter(null, name, address, responsible, phoneNumber, email, capacity, occupation, null);
    }

    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
    }

    private void validateSize(String value, int maxSize, String fieldName) {
        if (value.length() > maxSize) {
            throw new IllegalArgumentException(fieldName + " must be less than or equal to " + maxSize + " characters.");
        }
    }

    private void validatePattern(String value, String pattern, String message) {
        if (!value.matches(pattern)) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailPattern)) {
            throw new IllegalArgumentException("Email should be valid.");
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null.");
        }
    }

    private void validateMinValue(Integer value, int minValue, String fieldName) {
        if (value < minValue) {
            throw new IllegalArgumentException(fieldName + " must be at least " + minValue + ".");
        }
    }

    private void validateMaxValue(Integer value, int maxValue, String fieldName) {
        if (value > maxValue) {
            throw new IllegalArgumentException(fieldName + " must be at most " + maxValue + ".");
        }
    }
}
