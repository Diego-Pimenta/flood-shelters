package com.compass.service.impl;

import com.compass.dao.DaoFactory;
import com.compass.dao.DistributionCenterDao;
import com.compass.dao.DonationDao;
import com.compass.exception.ResourceNotFoundException;
import com.compass.exception.StorageLimitException;
import com.compass.model.entities.DistributionCenter;
import com.compass.model.entities.Donation;
import com.compass.model.entities.Item;
import com.compass.model.enums.ClothingGenre;
import com.compass.model.enums.ClothingSize;
import com.compass.model.enums.ItemType;
import com.compass.service.DonationService;
import com.compass.util.CsvUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DonationServiceImpl implements DonationService {

    private final Scanner sc = new Scanner(System.in);

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void create() {
        try {
            DonationDao donationDao = DaoFactory.createDonationDao();
            DistributionCenterDao distributionCenterDao = DaoFactory.createDistributionCenterDao();

            System.out.println("Insira os dados da nova doação");
            Long distributionCenterId = Long.parseLong(getInput("Digite o id do centro de distribuição: "));
            DistributionCenter distributionCenter = findDistributionCenter(distributionCenterDao, distributionCenterId);

            Donation donation = new Donation();
            donation.setDistributionCenter(distributionCenter);
            donation.setQuantity(Integer.parseInt(getInput("Digite a quantidade do item doado: ")));

            String name = getInput("Digite o nome do item doado: ");
            ItemType itemType = ItemType.valueOf(getInput("Digite o tipo do item doado (CLOTHING, HYGIENE, FOOD): "));
            String description = getInput("Digite a descrição do item doado: ");
            ClothingGenre genre = null;
            ClothingSize size = null;
            if (itemType.equals(ItemType.CLOTHING)) {
                genre = ClothingGenre.valueOf(getInput("Digite o gênero da roupa doada (M/F): "));
                size = ClothingSize.valueOf(getInput("Digite o tamanho da roupa doada (CHILDREN, XS, S, M, L, XL): "));
            }
            String measuringUnit = getInput("Digite a unidade de medida do item doado: ");
            LocalDate validity = null;
            if (itemType.equals(ItemType.FOOD)) {
                validity = LocalDate.parse(getInput("Digite a validade do alimento doado (yyyy-MM-dd): "), dtf);
            }

            Item item = createItem(name, itemType, description, genre, size, measuringUnit, validity, donation);
            donation.setItem(item);

            if (isExceedingLimit(donationDao, distributionCenterId, itemType, donation.getQuantity())) {
                throw new StorageLimitException("Quantidade do item excede o limite de estoque");
            }

            donationDao.save(donation);
            System.out.println("Doação registrada.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void read() {
        try {
            DonationDao donationDao = DaoFactory.createDonationDao();
            Long id = Long.parseLong(getInput("Digite o id da doação a ser buscada: "));
            Donation donation = findDonation(donationDao, id);
            System.out.println(donation);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void readAll() {
        try {
            DonationDao donationDao = DaoFactory.createDonationDao();
            System.out.println("Lista de doações registradas");
            List<Donation> donations = donationDao.findAll();
            if (donations.isEmpty()) {
                System.out.println("Nenhuma doação foi encontrada.");
                return;
            }
            donations.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        try {
            DonationDao donationDao = DaoFactory.createDonationDao();
            DistributionCenterDao distributionCenterDao = DaoFactory.createDistributionCenterDao();

            Long id = Long.parseLong(getInput("Digite o id da doação a ser atualizada: "));
            Donation donation = findDonation(donationDao, id);

            Long distributionCenterId = Long.parseLong(getInput("Digite o novo id do centro de distribuição da doação: "));
            DistributionCenter distributionCenter = findDistributionCenter(distributionCenterDao, distributionCenterId);

            donation.setDistributionCenter(distributionCenter);
            donation.setQuantity(Integer.parseInt(getInput("Digite a nova quantidade do item doado: ")));

            if (isExceedingLimit(donationDao, distributionCenterId, donation.getItem().getItemType(), donation.getQuantity())) {
                throw new StorageLimitException("Quantidade do item excede o limite de estoque");
            }

            donationDao.update(donation);
            System.out.println("Doação atualizada.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void delete() {
        try {
            DonationDao donationDao = DaoFactory.createDonationDao();
            Long id = Long.parseLong(getInput("Digite o id da doação a ser excluída: "));
            findDonation(donationDao, id);
            donationDao.delete(id);
            System.out.println("Doação deletada.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    // TODO: Melhoria no importCsv() para que a operação seja cancelada completamente em caso de exceção

    @Override
    public void importCsv() {
        try {
            DonationDao donationDao = DaoFactory.createDonationDao();
            DistributionCenterDao distributionCenterDao = DaoFactory.createDistributionCenterDao();

            String filePath = "/home/diego/repos/java/flood-shelters/src/main/resources/data/donation.csv";
            List<Map<String, String>> lines = CsvUtil.readCsv(filePath);

            Map<Long, DistributionCenter> distributionCentersMap = getDistributionCenters(distributionCenterDao);
            lines.forEach(cols -> {
                Donation donation = new Donation();
                donation.setDistributionCenter(distributionCentersMap.get(Long.parseLong(cols.get("distribution_center_id"))));
                donation.setQuantity(Integer.parseInt(cols.get("quantity")));
                Item item = createItem(donation, cols);
                donation.setItem(item);

                if (isExceedingLimit(donationDao, donation.getDistributionCenter().getId(), item.getItemType(), donation.getQuantity())) {
                    throw new StorageLimitException("Quantidade dos itens excede o limite de estoque");
                }
                donationDao.save(donation);
            });
            System.out.println("Doações importadas.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void transferDonation() {
        try {
            DonationDao donationDao = DaoFactory.createDonationDao();
            DistributionCenterDao distributionCenterDao = DaoFactory.createDistributionCenterDao();

            System.out.println("Insira os dados da transferência");
            Long fromDistributionCenterId = Long.parseLong(getInput("Digite o id do centro que fará a transferência: "));
            findDistributionCenter(distributionCenterDao, fromDistributionCenterId);

            Long toDistributionCenterId = Long.parseLong(getInput("Digite o id do centro que receberá a transferência: "));
            DistributionCenter toDistributionCenter = findDistributionCenter(distributionCenterDao, toDistributionCenterId);

            String itemName = getInput("Digite o nome do item que será transferido: ");
            List<Donation> donations = donationDao.findByItemNameAndDistributionCenterId(itemName, fromDistributionCenterId);
            if (donations.isEmpty()) {
                throw new ResourceNotFoundException("Nenhum item com o nome informado foi encontrado.");
            }

            int quantity = Integer.parseInt(getInput("Digite a quantidade do item que será transferido: "));
            if (isExceedingLimit(donationDao, toDistributionCenterId, donations.get(0).getItem().getItemType(), quantity)) {
                throw new StorageLimitException("Quantidade do item excede o limite de estoque");
            }

            transferDonation(donationDao, donations, toDistributionCenter, quantity);
            System.out.println("Transferência realizada.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private DistributionCenter findDistributionCenter(DistributionCenterDao distributionCenterDao, Long id) {
        DistributionCenter distributionCenter = distributionCenterDao.findById(id);
        if (distributionCenter == null) {
            throw new ResourceNotFoundException("Centro de dist. não encontrado.");
        }
        return distributionCenter;
    }

    private Donation findDonation(DonationDao donationDao, Long id) {
        Donation donation = donationDao.findById(id);
        if (donation == null) {
            throw new ResourceNotFoundException("Doação não encontrada.");
        }
        return donation;
    }

    private Item createItem(String name, ItemType itemType, String description, ClothingGenre genre, ClothingSize size, String measuringUnit, LocalDate validity, Donation donation) {
        Item item = new Item();
        item.setName(name);
        item.setItemType(itemType);
        item.setDescription(description);
        item.setGenre(genre);
        item.setSize(size);
        item.setMeasuringUnit(measuringUnit);
        item.setValidity(validity);
        item.setDonation(donation);
        return item;
    }

    private Item createItem(Donation donation, Map<String, String> cols) {
        String name = cols.get("name");
        ItemType itemType = ItemType.valueOf(cols.get("item_type"));
        String description = cols.get("description");
        ClothingGenre genre = cols.get("genre").isEmpty() ? null : ClothingGenre.valueOf(cols.get("genre"));
        ClothingSize size = cols.get("size").isEmpty() ? null : ClothingSize.valueOf(cols.get("size"));
        String measuringUnit = cols.get("measuring_unit");
        LocalDate validity = cols.get("validity").isEmpty() ? null : LocalDate.parse(cols.get("validity"), dtf);
        return createItem(name, itemType, description, genre, size, measuringUnit, validity, donation);
    }

    private Map<Long, DistributionCenter> getDistributionCenters(DistributionCenterDao distributionCenterDao) {
        List<DistributionCenter> distributionCenters = distributionCenterDao.findAll();
        Map<Long, DistributionCenter> distributionCentersMap = new HashMap<>();
        distributionCenters.forEach(dc -> distributionCentersMap.put(dc.getId(), dc));
        return distributionCentersMap;
    }

    private boolean isExceedingLimit(DonationDao donationDao, Long distributionCenterId, ItemType itemType, Integer quantity) {
        int currentQuantity = getTotalItemsByType(donationDao, distributionCenterId).getOrDefault(itemType, 0);
        return (currentQuantity + quantity) > 1000;
    }

    private Map<ItemType, Integer> getTotalItemsByType(DonationDao donationDao, Long distributionCenterId) {
        List<Donation> donations = donationDao.findByDistributionCenterId(distributionCenterId);
        Map<ItemType, Integer> totalItemsByType = new HashMap<>();
        donations.forEach(donation -> {
            ItemType itemType = donation.getItem().getItemType();
            totalItemsByType.merge(itemType, donation.getQuantity(), Integer::sum);
        });
        return totalItemsByType;
    }

    private void transferDonation(DonationDao donationDao, List<Donation> donations, DistributionCenter toDistributionCenter, int quantity) {
        int totalDCQuantity = donations.stream().mapToInt(Donation::getQuantity).sum();

        if (quantity > totalDCQuantity) {
            throw new StorageLimitException("Quantidade solicitada insuficiente no estoque.");
        }

        donations.sort(Comparator.comparing(Donation::getQuantity).reversed());

        int remainingQuantity = quantity;
        for (Donation donation : donations) {
            int donationQuantity = donation.getQuantity();

            if (donationQuantity > remainingQuantity) {
                donation.setQuantity(donationQuantity - remainingQuantity);
                donationDao.update(donation);
                Donation newDonation = createDonation(donation, toDistributionCenter, remainingQuantity);
                donationDao.save(newDonation);
                break;
            } else {
                donation.setDistributionCenter(toDistributionCenter);
                donationDao.update(donation);
                remainingQuantity -= donationQuantity;
            }
        }
    }

    private Donation createDonation(Donation donation, DistributionCenter distributionCenter, int quantity) {
        Donation newDonation = new Donation();
        newDonation.setDistributionCenter(distributionCenter);
        newDonation.setQuantity(quantity);
        newDonation.setItem(createItem(
                donation.getItem().getName(),
                donation.getItem().getItemType(),
                donation.getItem().getDescription(),
                donation.getItem().getGenre(),
                donation.getItem().getSize(),
                donation.getItem().getMeasuringUnit(),
                donation.getItem().getValidity(),
                newDonation
        ));
        return newDonation;
    }
}
