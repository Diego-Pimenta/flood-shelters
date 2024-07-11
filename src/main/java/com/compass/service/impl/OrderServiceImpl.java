package com.compass.service.impl;

import com.compass.dao.DaoFactory;
import com.compass.dao.DonationDao;
import com.compass.dao.OrderDao;
import com.compass.dao.ShelterDao;
import com.compass.exception.ResourceNotFoundException;
import com.compass.exception.StorageLimitException;
import com.compass.model.entities.Donation;
import com.compass.model.entities.Item;
import com.compass.model.entities.Order;
import com.compass.model.entities.Shelter;
import com.compass.model.enums.ClothingGenre;
import com.compass.model.enums.ClothingSize;
import com.compass.model.enums.ItemType;
import com.compass.service.OrderService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class OrderServiceImpl implements OrderService {

    private final Scanner sc = new Scanner(System.in);

    @Override
    public void orderRequest() {
        try {
            OrderDao orderDao = DaoFactory.createOrderDao();
            ShelterDao shelterDao = DaoFactory.createShelterDao();
            DonationDao donationDao = DaoFactory.createDonationDao();

            System.out.println("Insira os dados da ordem de pedido");
            Long shelterId = Long.parseLong(getInput("Digite o id do abrigo que fará o pedido: "));
            Shelter shelter = findShelter(shelterDao, shelterId);

            String itemName = getInput("Digite o nome do item que será solicitado: ");
            List<Donation> donations = donationDao.findByItemName(itemName);
            if (donations.isEmpty()) {
                throw new ResourceNotFoundException("Nenhum item com o nome informado foi encontrado.");
            }

            System.out.println("Lista de centros de dist. que possuem o item");
            donations.sort(Comparator.comparing(Donation::getQuantity).reversed());
            donations.forEach(System.out::println);

            int quantity = Integer.parseInt(getInput("Digite a quantidade do item que será solicitado: "));
            if (isExceedingLimit(orderDao, shelterId, donations.get(0).getItem().getItemType(), quantity)) {
                throw new StorageLimitException("Quantidade do item excede o limite de estoque.");
            }

            orderRequest(orderDao, donations, shelter, quantity);
            System.out.println("Order de pedido realizado.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void checkoutItem() {

    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private Shelter findShelter(ShelterDao shelterDao, Long id) {
        Shelter shelter = shelterDao.findById(id);
        if (shelter == null) {
            throw new ResourceNotFoundException("Abrigo não encontrado.");
        }
        return shelter;
    }

    private boolean isExceedingLimit(OrderDao orderDao, Long shelterId, ItemType itemType, Integer quantity) {
        int currentQuantity = getTotalItemsByType(orderDao, shelterId).getOrDefault(itemType, 0);
        return (currentQuantity + quantity) > 200;
    }

    private Map<ItemType, Integer> getTotalItemsByType(OrderDao orderDao, Long shelterId) {
        List<Order> orders = orderDao.findByShelterId(shelterId);
        Map<ItemType, Integer> totalItemsByType = new HashMap<>();
        orders.forEach(order -> {
            ItemType itemType = order.getItem().getItemType();
            totalItemsByType.merge(itemType, order.getQuantity(), Integer::sum);
        });
        return totalItemsByType;
    }

    private void orderRequest(OrderDao orderDao, List<Donation> donations, Shelter shelter, int quantity) {
        int totalDCQuantity = donations.stream().mapToInt(Donation::getQuantity).sum();

        if (quantity > totalDCQuantity) {
            throw new StorageLimitException("Quantidade solicitada insuficiente no estoque.");
        }

        int remainingQuantity = quantity;
        for (Donation donation : donations) {
            if (remainingQuantity <= 0) {
                break;
            }
            int donationQuantity = donation.getQuantity();
            int orderQuantity = Math.min(donationQuantity, remainingQuantity);

            Order order = createOrder(donation, shelter, orderQuantity);
            orderDao.save(order);

            remainingQuantity -= orderQuantity;
        }
    }

    private Order createOrder(Donation donation, Shelter shelter, int quantity) {
        Order newOrder = new Order();
        newOrder.setShelter(shelter);
        newOrder.setQuantity(quantity);
        newOrder.setAccepted(false);
        newOrder.setItem(createItem(
                donation.getItem().getName(),
                donation.getItem().getItemType(),
                donation.getItem().getDescription(),
                donation.getItem().getGenre(),
                donation.getItem().getSize(),
                donation.getItem().getMeasuringUnit(),
                donation.getItem().getValidity(),
                newOrder
        ));
        return newOrder;
    }

    private Item createItem(String name, ItemType itemType, String description, ClothingGenre genre, ClothingSize size, String measuringUnit, LocalDate validity, Order order) {
        return new Item(null, name, itemType, description, genre, size, measuringUnit, validity, null, order);
    }
}
