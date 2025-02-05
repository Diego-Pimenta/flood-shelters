package com.compass.service.impl;

import com.compass.dao.DaoFactory;
import com.compass.dao.DonationDao;
import com.compass.dao.ItemDao;
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

            System.out.println("Lista de centros de distribuição que possuem o item");
            donations.sort(Comparator.comparing(Donation::getQuantity).reversed());
            donations.forEach(System.out::println);

            int quantity = Integer.parseInt(getInput("Digite a quantidade do item que será solicitado: "));
            if (isExceedingLimit(orderDao, shelterId, donations.get(0).getItem().getItemType(), quantity)) {
                throw new StorageLimitException("Quantidade do item excede o limite de estoque.");
            }

            orderRequest(orderDao, donations, shelter, quantity);
            System.out.println("Ordem de pedido realizada.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void checkoutItem() {
        try {
            OrderDao orderDao = DaoFactory.createOrderDao();
            DonationDao donationDao = DaoFactory.createDonationDao();

            List<Order> orders = findOrders(orderDao);
            if (orders.isEmpty()) {
                System.out.println("Nenhum pedido pendente.");
                return;
            }
            orders.forEach(System.out::println);

            Long orderId = Long.parseLong(getInput("Digite o id do pedido que será analisado: "));
            Order order = orders.stream()
                    .filter(o -> o.getId().equals(orderId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado."));

            Boolean accepted = parseBoolean(getInput("O pedido será aceito ou recusado (Y/N)? "));
            if (accepted == null) {
                throw new IllegalArgumentException("Entrada inválida, tente novamente.");
            }

            if (!accepted) {
                order.setRefusalReason(getInput("Informe o motivo da recusa: "));
                orderDao.update(order);
            } else {
                if (isExceedingLimit(orderDao, order.getShelter().getId(), order.getItem().getItemType(), order.getQuantity())) {
                    throw new StorageLimitException("Quantidade do item excede o limite de estoque.");
                }
                processOrder(orderDao, donationDao, order);
            }

            System.out.println("Pedido atualizado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private Boolean parseBoolean(String input) {
        if (input.equalsIgnoreCase("Y")) {
            return true;
        } else if (input.equalsIgnoreCase("N")) {
            return false;
        } else {
            return null;
        }
    }

    private Shelter findShelter(ShelterDao shelterDao, Long id) {
        Shelter shelter = shelterDao.findById(id);
        if (shelter == null) {
            throw new ResourceNotFoundException("Abrigo não encontrado.");
        }
        return shelter;
    }

    private List<Order> findOrders(OrderDao orderDao) {
        return orderDao.findAll()
                .stream()
                .filter(o -> !o.getAccepted() && o.getRefusalReason() == null)
                .toList();
    }

    private Item findItem(String name, ItemType itemType, String description, ClothingGenre genre, ClothingSize size, String measuringUnit, LocalDate validity) {
        ItemDao itemDao = DaoFactory.createItemDao();

        List<Item> items = itemDao.findAll();
        return items.stream()
                .filter(item -> item.getName().equals(name) &&
                        item.getItemType() == itemType &&
                        item.getDescription().equals(description) &&
                        item.getGenre() == genre &&
                        item.getSize() == size &&
                        item.getMeasuringUnit().equals(measuringUnit) &&
                        (item.getValidity() == null ? validity == null : item.getValidity().equals(validity)))
                .findFirst()
                .orElseThrow();
    }

    private boolean isExceedingLimit(OrderDao orderDao, Long shelterId, ItemType itemType, Integer quantity) {
        int currentQuantity = getTotalItemsByType(orderDao, shelterId).getOrDefault(itemType, 0);
        return (currentQuantity + quantity) > 200;
    }

    private Map<ItemType, Integer> getTotalItemsByType(OrderDao orderDao, Long shelterId) {
        List<Order> orders = orderDao.findByShelterId(shelterId);
        Map<ItemType, Integer> totalItemsByType = new HashMap<>();
        orders.stream()
                .filter(Order::getAccepted)
                .forEach(o -> totalItemsByType.merge(o.getItem().getItemType(), o.getQuantity(), Integer::sum));
        return totalItemsByType;
    }

    private void orderRequest(OrderDao orderDao, List<Donation> donations, Shelter shelter, int quantity) {
        int totalDCQuantity = donations.stream().mapToInt(Donation::getQuantity).sum();

        if (quantity > totalDCQuantity) {
            throw new StorageLimitException("Quantidade solicitada insuficiente no estoque.");
        }

        int remainingQuantity = quantity;
        for (Donation donation : donations) {
            if (remainingQuantity == 0) {
                break;
            }
            int donationQuantity = donation.getQuantity();
            int orderQuantity = Math.min(donationQuantity, remainingQuantity);

            Order order = createOrder(donation, shelter, orderQuantity);
            orderDao.save(order);

            remainingQuantity -= orderQuantity;
        }
    }

    private void processOrder(OrderDao orderDao, DonationDao donationDao, Order order) {
        List<Donation> donations = donationDao.findAll()
                .stream()
                .filter(donation -> donation.getItem().getName().equals(order.getItem().getName()) &&
                        donation.getItem().getItemType() == order.getItem().getItemType() &&
                        donation.getItem().getDescription().equals(order.getItem().getDescription()) &&
                        donation.getItem().getGenre() == order.getItem().getGenre() &&
                        donation.getItem().getSize() == order.getItem().getSize() &&
                        donation.getItem().getMeasuringUnit().equals(order.getItem().getMeasuringUnit()) &&
                        (donation.getItem().getValidity() == null ? order.getItem().getValidity() == null : donation.getItem().getValidity().equals(order.getItem().getValidity())))
                .toList();

        int totalDCQuantity = donations.stream().mapToInt(Donation::getQuantity).sum();

        if (order.getQuantity() > totalDCQuantity) {
            throw new StorageLimitException("Quantidade solicitada insuficiente no estoque.");
        }

        int remainingQuantity = order.getQuantity();
        for (Donation donation : donations) {
            if (remainingQuantity == 0) {
                break;
            }

            int donationQuantity = donation.getQuantity();

            if (donationQuantity <= remainingQuantity) {
                donationDao.delete(donation.getId());
                remainingQuantity -= donationQuantity;
            } else {
                donation.setQuantity(donationQuantity - remainingQuantity);
                donationDao.update(donation);
                remainingQuantity = 0;
            }
        }

        order.setAccepted(true);
        order.setRefusalReason(null);
        orderDao.update(order);
    }

    private Order createOrder(Donation donation, Shelter shelter, int quantity) {
        Item item = findItem(donation.getItem().getName(),
                donation.getItem().getItemType(),
                donation.getItem().getDescription(),
                donation.getItem().getGenre(),
                donation.getItem().getSize(),
                donation.getItem().getMeasuringUnit(),
                donation.getItem().getValidity());
        return new Order(null, shelter, item, quantity, false, null);
    }
}
