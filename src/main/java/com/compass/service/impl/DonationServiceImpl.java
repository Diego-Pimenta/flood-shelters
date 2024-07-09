package com.compass.service.impl;

import com.compass.dao.DaoFactory;
import com.compass.dao.DistributionCenterDao;
import com.compass.dao.DonationDao;
import com.compass.exception.CsvReaderException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class DonationServiceImpl implements DonationService {

    private final Scanner sc = new Scanner(System.in);

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void create() {
        try {
            System.out.println("Insira os dados da nova doação");
            Long distributionCenterId = Long.parseLong(getInput("Digite o id do centro de distribuição: "));

            DistributionCenter distributionCenter = getDistributionCenter(distributionCenterId);

            if (distributionCenter == null) throw new ResourceNotFoundException("Centro de dist. não encontrado.");

            Donation donation = createDonation(distributionCenter);

            String name = getInput("Digite o nome do item doado: ");
            ItemType itemType = ItemType.valueOf(getInput("Digite o tipo do item doado (CLOTHING, HYGIENE, FOOD): "));
            String description = getInput("Digite a descrição do item doado: ");
            ClothingGenre genre = null;
            ClothingSize size = null;

            if (itemType.equals(ItemType.CLOTHING)) {
                genre = ClothingGenre.valueOf(getInput("Digite o gênero da roupa doada (M/F): "));
                size = ClothingSize.valueOf(getInput("Digite o tamanho da roupa doada (CHILDREN, XS, S, M, L, XL): "));
            }
            int quantity = Integer.parseInt(getInput("Digite a quantidade do item doado: "));
            String measuringUnit = getInput("Digite a unidade de medida do item doado: ");
            LocalDate validity = null;

            if (itemType.equals(ItemType.FOOD)) {
                validity = LocalDate.parse(getInput("Digite a validade do alimento doado (yyyy-MM-dd): "), dtf);
            }
            Item item = createItem(name, itemType, description, genre, size, quantity, measuringUnit, validity, donation);

            donation.getItems().add(item);

            DonationDao donationDao = DaoFactory.createDonationDao();
            donationDao.save(donation);

            System.out.println("Doação registrada.");
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println("Erro de formatação: os dados foram inseridos inpropriadamente.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (Exception e) {
            System.out.println("Erro ao criar a doação: " + e.getMessage());
        }
    }

    @Override
    public void read() {
        try {
            Long id = Long.parseLong(getInput("Digite o id da doação a ser buscada: "));

            Donation donation = getDonation(id);

            if (donation == null) {
                System.out.println("Doação não encontrada.");
                return;
            }
            System.out.println(donation);
        } catch (NumberFormatException e) {
            System.out.println("Erro de formatação: o id deve ser um número.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar a doação: " + e.getMessage());
        }
    }

    @Override
    public void readAll() {
        try {
            System.out.println("Lista de doações registradas");

            DonationDao donationDao = DaoFactory.createDonationDao();
            List<Donation> donations = donationDao.findAll();

            if (donations.isEmpty()) {
                System.out.println("Nenhuma doação foi encontrada.");
                return;
            }
            donations.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Erro ao listar as doações: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        try {
            Long id = Long.parseLong(getInput("Digite o id da doação a ser atualizada: "));

            Donation donation = getDonation(id);

            if (donation == null) throw new ResourceNotFoundException("Doação não encontrada.");

            Long distributionCenterId = Long.parseLong(getInput("Digite o novo id do centro de distribuição da doação: "));

            DistributionCenter distributionCenter = getDistributionCenter(distributionCenterId);

            if (distributionCenter == null) throw new ResourceNotFoundException("Centro de dist. não encontrado.");

            donation.setDistributionCenter(distributionCenter);

            DonationDao donationDao = DaoFactory.createDonationDao();
            donationDao.update(donation);
        } catch (NumberFormatException e) {
            System.out.println("Erro de formatação: o id deve ser um número.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao atualizar a doação: " + e.getMessage());
        }
    }

    @Override
    public void delete() {
        try {
            Long id = Long.parseLong(getInput("Digite o id da doação a ser excluída: "));

            Donation donation = getDonation(id);

            if (donation == null) throw new ResourceNotFoundException("Doação não encontrada.");

            DonationDao donationDao = DaoFactory.createDonationDao();
            donationDao.delete(id);
        } catch (NumberFormatException e) {
            System.out.println("Erro de formatação: o id deve ser um número.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao excluir a doação: " + e.getMessage());
        }
    }

    @Override
    public void importCsv() {
        try {
            String filePath = "/home/diego/repos/java/flood-shelters/src/main/resources/data/donation.csv";

            List<Map<String, String>> lines = CsvUtil.readCsv(filePath);

            Map<Long, DistributionCenter> distributionCentersMap = getDistributionCenters();

            List<Donation> donations = new ArrayList<>();

            lines.forEach(cols -> {
                Long distributionCenterId = Long.parseLong(cols.get("distribution_center_id"));
                String name = cols.get("name");
                ItemType itemType = ItemType.valueOf(cols.get("item_type"));
                String description = cols.get("description");
                ClothingGenre genre = cols.get("genre").isEmpty() ? null : ClothingGenre.valueOf(cols.get("genre"));
                ClothingSize size = cols.get("size").isEmpty() ? null : ClothingSize.valueOf(cols.get("size"));
                int quantity = Integer.parseInt(cols.get("quantity"));
                String measuringUnit = cols.get("measuring_unit");
                LocalDate validity = cols.get("validity").isEmpty() ? null : LocalDate.parse(cols.get("validity"), dtf);

                DistributionCenter distributionCenter = distributionCentersMap.get(distributionCenterId);

                Donation donation = createDonation(distributionCenter);

                Item item = createItem(name, itemType, description, genre, size, quantity, measuringUnit, validity, donation);

                donation.getItems().add(item);

                donations.add(donation);
            });

            DonationDao donationDao = DaoFactory.createDonationDao();
            donations.forEach(donationDao::save);
        } catch (CsvReaderException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void transferDonation() {

    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private Map<Long, DistributionCenter> getDistributionCenters() {
        DistributionCenterDao distributionCenterDao = DaoFactory.createDistributionCenterDao();
        List<DistributionCenter> distributionCenters = distributionCenterDao.findAll();
        Map<Long, DistributionCenter> distributionCentersMap = new HashMap<>();

        for (DistributionCenter dc : distributionCenters) {
            distributionCentersMap.put(dc.getId(), dc);
        }
        return distributionCentersMap;
    }

    private DistributionCenter getDistributionCenter(Long id) {
        DistributionCenterDao distributionCenterDao = DaoFactory.createDistributionCenterDao();
        return distributionCenterDao.findById(id);
    }

    private Donation getDonation(Long id) {
        DonationDao donationDao = DaoFactory.createDonationDao();
        return donationDao.findById(id);
    }

    private Donation createDonation(DistributionCenter distributionCenter) {
        Donation donation = new Donation();
        donation.setDistributionCenter(distributionCenter);
        return donation;
    }

    private Item createItem(String name, ItemType itemType, String description, ClothingGenre genre, ClothingSize size, int quantity, String measuringUnit, LocalDate validity, Donation donation) {
        Item item = new Item();
        item.setName(name);
        item.setItemType(itemType);
        item.setDescription(description);
        item.setGenre(genre);
        item.setSize(size);
        item.setQuantity(quantity);
        item.setMeasuringUnit(measuringUnit);
        item.setValidity(validity);
        item.setDonation(donation);
        return item;
    }
}
