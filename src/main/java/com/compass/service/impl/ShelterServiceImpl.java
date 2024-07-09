package com.compass.service.impl;

import com.compass.dao.DaoFactory;
import com.compass.dao.ShelterDao;
import com.compass.exception.ResourceNotFoundException;
import com.compass.model.entities.Shelter;
import com.compass.service.ShelterService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ShelterServiceImpl implements ShelterService {

    private final Scanner sc = new Scanner(System.in);

    @Override
    public void create() {
        try {
            System.out.println("Insira os dados do novo abrigo");
            String name = getInput("Digite o nome do abrigo: ");
            String address = getInput("Digite o endereço do abrigo: ");
            String responsible = getInput("Digite o nome do responsável pelo abrigo: ");
            String phoneNumber = getInput("Digite o número de telefone do abrigo (+55(99)99999-9999): ");
            String email = getInput("Digite o email do abrigo: ");
            Integer capacity = Integer.parseInt(getInput("Digite a capacidade do abrigo: "));
            double occupation = parseOccupation(getInput("Digite a ocupação do abrigo (%): "));

            Shelter shelter = createShelter(name, address, responsible, phoneNumber, email, capacity, occupation);

            ShelterDao shelterDao = DaoFactory.createShelterDao();
            shelterDao.save(shelter);

            System.out.println("Abrigo registrado.");
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println("Erro de formatação: os dados foram inseridos inpropriadamente.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (Exception e) {
            System.out.println("Erro ao criar o abrigo: " + e.getMessage());
        }
    }

    @Override
    public void read() {
        try {
            Long id = Long.parseLong(getInput("Digite o id do abrigo a ser buscado: "));

            Shelter shelter = getShelter(id);

            if (shelter == null) {
                System.out.println("Abrigo não encontrado.");
                return;
            }
            System.out.println(shelter);
        } catch (NumberFormatException e) {
            System.out.println("Erro de formatação: o id deve ser um número.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar o abrigo: " + e.getMessage());
        }
    }

    @Override
    public void readAll() {
        try {
            System.out.println("Lista de abrigos registrados");

            ShelterDao shelterDao = DaoFactory.createShelterDao();
            List<Shelter> shelters = shelterDao.findAll();

            if (shelters.isEmpty()) {
                System.out.println("Nenhum abrigo foi encontrado.");
                return;
            }
            shelters.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Erro ao listar os abrigos: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        try {
            Long id = Long.parseLong(getInput("Digite o id do abrigo a ser atualizado: "));

            Shelter shelter = getShelter(id);

            if (shelter == null) throw new ResourceNotFoundException("Doação não encontrada.");

            String address = getInput("Digite o novo endereço do abrigo: ");

            shelter.setAddress(address);

            ShelterDao shelterDao = DaoFactory.createShelterDao();
            shelterDao.update(shelter);

            System.out.println("Abrigo atualizado.");
        } catch (NumberFormatException e) {
            System.out.println("Erro de formatação: o id deve ser um número.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao atualizar o abrigo: " + e.getMessage());
        }
    }

    @Override
    public void delete() {
        try {
            Long id = Long.parseLong(getInput("Digite o id do abrigo a ser excluído: "));

            Shelter shelter = getShelter(id);

            if (shelter == null) throw new ResourceNotFoundException("Abrigo não encontrado.");

            ShelterDao shelterDao = DaoFactory.createShelterDao();
            shelterDao.delete(id);

            System.out.println("Abrigo deletado.");
        } catch (NumberFormatException e) {
            System.out.println("Erro de formatação: o id deve ser um número.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao excluir o abrigo: " + e.getMessage());
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

    private Shelter getShelter(Long id) {
        ShelterDao shelterDao = DaoFactory.createShelterDao();
        return shelterDao.findById(id);
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
