package com.indivaragroup.shop;

import com.indivaragroup.shop.dto.ItemDTO;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

/*
    ADDITIONAL TASK
    1.properties
    2.random tax
    3.uuid
    4.encoding receipt
    5.validation regex object
 */

public class ShoppingService {
    ItemDTO[] listOrders = new ItemDTO[3];

    private Properties loadProperties(){
        Properties properties = new Properties();

        try {
            InputStream input = ShoppingService.class.getClassLoader()
                    .getResourceAsStream("application.properties");

            if (input == null) {
                throw new RuntimeException("Unable to load properties file : File Not Found");
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Could not load properties file", e);
        }

        return properties;
    }

    public void printReceipt(){
        Locale locale = new Locale("id", "ID");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

        Properties properties = loadProperties();

        Arrays.sort(listOrders, Comparator.nullsLast(Comparator.comparing(item -> item.name)));

        UUID uuid = UUID.randomUUID();

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal tax;

        int taxRate = new Random().nextInt(1, 20);

        StringBuilder receipt = new StringBuilder("======= RECEIPT =======\n");
        String receiptEncode;

        receipt.append("Store\t: ").append(properties.getProperty("store.name")).append("\n");
        receipt.append("Cashier\t: ").append(properties.getProperty("cashier.name")).append("\n");
        receipt.append("Receipt\t: ").append(uuid).append("\n");
        receipt.append("-----------------------");

        for (ItemDTO item : listOrders){
            if (item == null) {
                continue;
            }

            receipt.append("\nItem\t: ").
                    append(item.name).
                    append("\n");
            receipt.append("Qty\t\t: ").
                    append(item.quantity).
                    append("\n");
            receipt.append("Price\t: ").
                    append(currencyFormat.format(item.price)).
                    append("\n");
            receipt.append("-----------------------");
            subTotal = subTotal.add(item.price.multiply(item.quantity));
        }

        tax = total.add(subTotal).multiply(BigDecimal.valueOf((float)taxRate / 100));
        total = total.add(subTotal).add(tax);

        receipt.append("\nSubtotal: ").
                append(currencyFormat.format(subTotal)).
                append("\n");
        receipt.append("Tax(").
                append(taxRate).append("%): ").
                append(currencyFormat.format(tax)).
                append("\n");
        receipt.append("-----------------------\n");
        receipt.append("Total\t: ").
                append(currencyFormat.format(total)).
                append("\n");
        receipt.append("=======================");

        System.out.println(receipt);

        receiptEncode = Base64.getEncoder().encodeToString(receipt.toString().getBytes());
        System.out.println("Encode Receipt : " + receiptEncode);
    }

    private void addItem(ItemDTO item,int index){
        listOrders[index] = item;
    }

    public void runShopping() {
        Scanner scan = new Scanner(System.in);

        boolean stillOrder = true;
        int maxOrders = 3;
        int orderCount = 0;

        do {
            System.out.print("\nEnter item name : ");
            String name = scan.nextLine().toUpperCase();
            if (name.isBlank()){
                System.out.println("Name cannot be Empty");
                continue;
            }

            System.out.print("Enter item quantity : ");
            String quantity = scan.nextLine();
            /*
            Regex: ^[1-9]\d*$ artinya wajib angka bulat positif mulai dari 1 ke atas
             */
            if (!quantity.matches("^[1-9]\\d*$")){
                System.out.println("Invalid quantity");
                continue;
            }

            System.out.print("Enter item price : ");
            String price = scan.nextLine();
            /*
            Regex untuk angka positif, boleh desimal dengan titik (.)
            Contoh yang valid: 15000, 15000.50, 0.75
             */
            if (!price.matches("^[0-9]+(\\.[0-9]+)?$") || price.startsWith("0")){
                System.out.println("Invalid price");
                continue;
            }

            ItemDTO item = new ItemDTO(name, new BigDecimal(quantity), new BigDecimal(price));

            addItem(item,orderCount);

            orderCount++;
            if (orderCount >= maxOrders){
                System.out.println("\nMax Orders 3, Thank you for using our shopping\n");
                break;
            } else {
                System.out.print("\nOrder Again (Y/N) : ");
                String choice = scan.nextLine();

                stillOrder = switch (choice.toUpperCase()) {
                    case "Y" -> true;
                    case "N" -> false;
                    default -> {
                        System.out.println("Invalid input : close program");
                        yield false;
                    }
                };
            }

        } while (stillOrder);

        printReceipt();
        scan.close();
    }

}


