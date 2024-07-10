package com.compass.service.impl;

import com.compass.dao.DaoFactory;
import com.compass.service.ApplicationFeaturesService;
import com.compass.service.DonationService;
import com.compass.service.OrderService;
import com.compass.service.ShelterService;

import java.util.Scanner;

public class ApplicationFeaturesServiceImpl implements ApplicationFeaturesService {

    private final Scanner sc = new Scanner(System.in);

    private final DonationService donationService = new DonationServiceImpl();

    private final ShelterService shelterService = new ShelterServiceImpl();

    private final OrderService orderService = new OrderServiceImpl();

    @Override
    public void start() {
        DaoFactory.initDbOperations();

        clearScreen();

        boolean running = true;
        while (running) {
            System.out.println("Bem-vindo ao menu do sistema");
            System.out.println("1. Registro de doações");
            System.out.println("2. Registro de abrigos");
            System.out.println("3. Ordem de pedido");
            System.out.println("4. Checkout de itens");
            System.out.println("5. Transferência de doações");
            System.out.println("6. Limpar console");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                int option = Integer.parseInt(sc.nextLine());
                switch (option) {
                    case 1:
                        registerDonations();
                        break;
                    case 2:
                        registerShelters();
                        break;
                    case 3:
                        orderRequest();
                        break;
                    case 4:
                        checkoutItems();
                        break;
                    case 5:
                        transferDonations();
                        break;
                    case 6:
                        clearScreen();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.err.println("Erro: Opção inválida, tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Erro: Entrada inválida, tente novamente.");
            }
        }
        System.out.println("Sistema encerrado.");

        DaoFactory.closeAll();
    }

    @Override
    public void registerDonations() {
        clearScreen();
        System.out.println("Registro de doações:");
        System.out.println("1. Cadastrar doação");
        System.out.println("2. Ler doação");
        System.out.println("3. Ler doações");
        System.out.println("4. Editar doação");
        System.out.println("5. Excluir doação");
        System.out.println("6. Cadastrar doações via arquivo csv");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(sc.nextLine());
            switch (option) {
                case 1:
                    donationService.create();
                    break;
                case 2:
                    donationService.read();
                    break;
                case 3:
                    donationService.readAll();
                    break;
                case 4:
                    donationService.update();
                    break;
                case 5:
                    donationService.delete();
                    break;
                case 6:
                    donationService.importCsv();
                    break;
                default:
                    System.err.println("Erro: Opção inválida, tente novamente.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: Entrada inválida, tente novamente.");
        }
    }

    @Override
    public void registerShelters() {
        clearScreen();
        System.out.println("Registro de abrigos:");
        System.out.println("1. Cadastrar abrigo");
        System.out.println("2. Ler abrigo");
        System.out.println("3. Ler abrigos");
        System.out.println("4. Editar abrigo");
        System.out.println("5. Excluir abrigo");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(sc.nextLine());
            switch (option) {
                case 1:
                    shelterService.create();
                    break;
                case 2:
                    shelterService.read();
                    break;
                case 3:
                    shelterService.readAll();
                    break;
                case 4:
                    shelterService.update();
                    break;
                case 5:
                    shelterService.delete();
                    break;
                default:
                    System.err.println("Erro: Opção inválida, tente novamente.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Erro: Entrada inválida, tente novamente.");
        }
    }

    @Override
    public void orderRequest() {
        clearScreen();
        orderService.orderRequest();
    }

    @Override
    public void checkoutItems() {
        clearScreen();
        orderService.checkoutItem();
    }

    @Override
    public void transferDonations() {
        clearScreen();
        donationService.transferDonation();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
