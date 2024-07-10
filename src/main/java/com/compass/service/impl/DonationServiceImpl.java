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
import jakarta.persistence.NoResultException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DonationServiceImpl implements DonationService {

    private final Scanner sc = new Scanner(System.in);

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void create() {
        try {
            System.out.println("Insira os dados da nova doação");

            // Verifica se o centro de dist. informado existe
            Long distributionCenterId = Long.parseLong(getInput("Digite o id do centro de distribuição: "));
            DistributionCenter distributionCenter = getDistributionCenter(distributionCenterId);
            if (distributionCenter == null) throw new ResourceNotFoundException("Centro de dist. não encontrado.");

            Donation donation = new Donation();

            // Faz a leitura dos dados para instanciar o item
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

            // Verifica se a adição do item excede o limite de armazenamento do centro de dist.
            Integer quantity = Integer.parseInt(getInput("Digite a quantidade do item doado: "));
            if (isExceedingLimit(distributionCenterId, itemType, quantity)) {
                throw new StorageLimitException("Erro: quantidade do item excede o limite");
            }

            // Conclui de preencher os dados da doação e salva no banco de dados
            Item item = createItem(name, itemType, description, genre, size, measuringUnit, validity, donation);

            donation.setDistributionCenter(distributionCenter);
            donation.setQuantity(quantity);
            donation.setItem(item);

            DonationDao donationDao = DaoFactory.createDonationDao();
            donationDao.save(donation);

            System.out.println("Doação registrada.");
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println("Erro de formatação: os dados foram inseridos incorretamente.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (StorageLimitException e) {
            System.out.println(e.getMessage());
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
            // Verifica a existência da doação a ser atualizada
            Long id = Long.parseLong(getInput("Digite o id da doação a ser atualizada: "));
            Donation donation = getDonation(id);
            if (donation == null) throw new ResourceNotFoundException("Doação não encontrada.");

            // Verifica a existência do novo valor para o campo centro de dist. da doação
            Long distributionCenterId = Long.parseLong(getInput("Digite o novo id do centro de distribuição da doação: "));
            DistributionCenter distributionCenter = getDistributionCenter(distributionCenterId);
            if (distributionCenter == null) throw new ResourceNotFoundException("Centro de dist. não encontrado.");

            // Verifica se a atualização da doação excede o limite de armazenamento do centro de dist.
            Integer quantity = Integer.parseInt(getInput("Digite a nova quantidade do item doado: "));
            ItemType itemType = donation.getItem().getItemType();
            if (isExceedingLimit(distributionCenterId, itemType, quantity)) {
                throw new StorageLimitException("Erro: quantidade do item excede o limite");
            }

            // Conclui de atualizar os dados da doação e salva no banco de dados
            donation.setDistributionCenter(distributionCenter);
            donation.setQuantity(quantity);

            DonationDao donationDao = DaoFactory.createDonationDao();
            donationDao.update(donation);

            System.out.println("Doação atualizada.");
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println("Erro de formatação: os dados foram inseridos incorretamente.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (ResourceNotFoundException | StorageLimitException e) {
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

            System.out.println("Doação deletada.");
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

    // TODO: Melhoria no importCsv para que a operação seja cancelada completamente em caso de exceção

    @Override
    public void importCsv() {
        try {
            String filePath = "/home/diego/repos/java/flood-shelters/src/main/resources/data/donation.csv";

            List<Map<String, String>> lines = CsvUtil.readCsv(filePath);

            Map<Long, DistributionCenter> distributionCentersMap = getDistributionCenters();

            DonationDao donationDao = DaoFactory.createDonationDao();

            lines.forEach(cols -> {
                Long distributionCenterId = Long.parseLong(cols.get("distribution_center_id"));
                String name = cols.get("name");
                ItemType itemType = ItemType.valueOf(cols.get("item_type"));
                String description = cols.get("description");
                ClothingGenre genre = cols.get("genre").isEmpty() ? null : ClothingGenre.valueOf(cols.get("genre"));
                ClothingSize size = cols.get("size").isEmpty() ? null : ClothingSize.valueOf(cols.get("size"));
                String measuringUnit = cols.get("measuring_unit");
                LocalDate validity = cols.get("validity").isEmpty() ? null : LocalDate.parse(cols.get("validity"), dtf);
                Integer quantity = Integer.parseInt(cols.get("quantity"));

                DistributionCenter distributionCenter = distributionCentersMap.get(distributionCenterId);

                // Verifica se a adição do item excede o limite de armazenamento do centro de dist.
                if (isExceedingLimit(distributionCenterId, itemType, quantity)) {
                    throw new StorageLimitException("Erro: quantidade de itens excede o limite");
                }
                Donation donation = new Donation();

                Item item = createItem(name, itemType, description, genre, size, measuringUnit, validity, donation);

                donation.setDistributionCenter(distributionCenter);
                donation.setQuantity(quantity);
                donation.setItem(item);

                donationDao.save(donation);
            });
            System.out.println("Doações importadas.");
        } catch (CsvReaderException | StorageLimitException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void transferDonation() {
        try {
            // Verifica a existência do centro de dist. que fará a transferência
            Long fromDistributionCenterId = Long.parseLong(getInput("Digite o id do centro que fará a transferência: "));
            DistributionCenter fromDistributionCenter = getDistributionCenter(fromDistributionCenterId);
            if (fromDistributionCenter == null) throw new ResourceNotFoundException("Centro de dist. não encontrado.");

            // Verifica a existência do centro de dist. que receberá a transferência
            Long toDistributionCenterId = Long.parseLong(getInput("Digite o id do centro que receberá a transferência: "));
            DistributionCenter toDistributionCenter = getDistributionCenter(toDistributionCenterId);
            if (toDistributionCenter == null) throw new ResourceNotFoundException("Centro de dist. não encontrado.");

            // Verifica a existência do item com o nome informado no centro de dist.
            String itemName = getInput("Digite o nome do item que será transferido: ");
            List<Donation> donations = getDonationsByItemNameAndDistributionCenterId(itemName, fromDistributionCenterId);
            if (donations.isEmpty()) throw new ResourceNotFoundException("Nenhum item com o nome informado foi encontrado.");

            // Verifica se a quantidade da transferência excede o limite do centro de dist.
            int quantity = Integer.parseInt(getInput("Digite a quantidade do item que será transferido: "));
            ItemType itemType = donations
                    .stream()
                    .findFirst()
                    .map(Donation::getItem)
                    .map(Item::getItemType)
                    .orElseThrow();
            if (isExceedingLimit(toDistributionCenterId, itemType, quantity)) {
                throw new StorageLimitException("Erro: quantidade do item excede o limite");
            }

            // Conclui de transferir o item do centro de dist. conforme a quantidade especificada e salva no banco de dados
            int totalDCQuantity = donations
                    .stream()
                    .mapToInt(Donation::getQuantity)
                    .sum();

            DonationDao donationDao = DaoFactory.createDonationDao();

            if (quantity < totalDCQuantity) {
                // Organiza as doações pela quantidade de itens
                donations.sort(Comparator.comparingInt(Donation::getQuantity).reversed());
                int remainingQuantity = quantity;

                for (Donation donation : donations) {
                    int donationQuantity = donation.getQuantity();

                    if (donationQuantity <= remainingQuantity) {
                        donation.setDistributionCenter(toDistributionCenter);
                        donationDao.update(donation);
                        remainingQuantity -= donationQuantity;
                    } else {
                        // Atualiza a doação existente com a quantidade restante
                        donation.setQuantity(donationQuantity - remainingQuantity);
                        donationDao.update(donation);

                        // Cria uma nova doação com a quantidade transferida
                        Donation newDonation = new Donation();
                        Item item = createItem(
                                donation.getItem().getName(),
                                donation.getItem().getItemType(),
                                donation.getItem().getDescription(),
                                donation.getItem().getGenre(),
                                donation.getItem().getSize(),
                                donation.getItem().getMeasuringUnit(),
                                donation.getItem().getValidity(),
                                newDonation
                        );
                        newDonation.setDistributionCenter(toDistributionCenter);
                        newDonation.setItem(item);
                        newDonation.setQuantity(remainingQuantity);
                        donationDao.save(newDonation);
                        break;
                    }
                }
            } else if (quantity == totalDCQuantity) {
                donations.forEach(donation -> {
                    donation.setDistributionCenter(toDistributionCenter);
                    donationDao.update(donation);
                });
            } else {
                throw new StorageLimitException("Erro: quantidade de item no estoque insuficiente.");
            }
            System.out.println("Transferência realizada.");
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println("Erro de formatação: os dados foram inseridos incorretamente.");
        } catch (NoSuchElementException e) {
            System.out.println("Erro: entrada não esperada.");
        } catch (ResourceNotFoundException | StorageLimitException e) {
            System.out.println(e.getMessage());
        } catch (NoResultException e) {
            System.out.println("Erro: ");
        } catch (Exception e) {
            System.out.println("Erro ao realizar transferência de item: " + e.getMessage());
        }
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private DistributionCenter getDistributionCenter(Long id) {
        DistributionCenterDao distributionCenterDao = DaoFactory.createDistributionCenterDao();
        return distributionCenterDao.findById(id);
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

    private Donation getDonation(Long id) {
        DonationDao donationDao = DaoFactory.createDonationDao();
        return donationDao.findById(id);
    }

    private List<Donation> getDonationsByItemNameAndDistributionCenterId(String itemName, Long distributionCenterId) {
        DonationDao donationDao = DaoFactory.createDonationDao();
        return donationDao.findByItemNameAndDistributionCenterId(itemName, distributionCenterId);
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

    // Verifica se a adição de x quantidade de itens atinge o limite estabelecido por tipo
    private boolean isExceedingLimit(Long distributionCenterId, ItemType itemType, Integer quantity) {
        int currentQuantity = getTotalItemsByType(distributionCenterId).getOrDefault(itemType, 0);
        return (currentQuantity + quantity) > 1000;
    }

    // Obtém todos os registros de doações pelo centro de dist. e separa a quantidade por tipo de item recebido
    private Map<ItemType, Integer> getTotalItemsByType(Long distributionCenterId) {
        DonationDao donationDao = DaoFactory.createDonationDao();
        List<Donation> donations = donationDao.findByDistributionCenterId(distributionCenterId);

        Map<ItemType, Integer> totalItemsByType = new HashMap<>();

        donations.forEach(donation -> {
            ItemType itemType = donation.getItem().getItemType();
            totalItemsByType.merge(itemType, donation.getQuantity(), Integer::sum);
        });
        return totalItemsByType;
    }
}
