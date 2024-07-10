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
            String address = getInput("Digite o endereço do abrigo: ");
            String responsible = getInput("Digite o nome do responsável pelo abrigo: ");
            String phoneNumber = getInput("Digite o número de telefone do abrigo (+55(99)99999-9999): ");
            String email = getInput("Digite o email do abrigo: ");
            Integer capacity = Integer.parseInt(getInput("Digite a capacidade do abrigo: "));
            double occupation = parseOccupation(getInput("Digite a ocupação do abrigo (%): "));

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

            shelter.setAddress(getInput("Digite o novo endereço do abrigo: "));

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

    private Shelter createShelter(String name, String address, String responsible, String phoneNumber, String email, Integer capacity, double occupation) {
        Shelter shelter = new Shelter();
        shelter.setName(name);
        shelter.setAddress(address);
        shelter.setResponsible(responsible);
        shelter.setPhoneNumber(phoneNumber);
        shelter.setEmail(email);
        shelter.setCapacity(capacity);
        shelter.setOccupation(occupation);
        return shelter;
    }
}
