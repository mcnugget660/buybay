package code.DataStorage;

import code.Util.ProductData;

public class Product {

    private static int nextId = 0;

    private String name;
    private String description;
    private int id;
    private double price;
    private int stock;
    private String seller;
    private boolean visible;

    public Product(ProductData data, String sellerUsername) {
        name = data.getName();
        price = data.getCost();
        description = data.getDescription();
        stock = 1;
        id = nextId++;
        seller = sellerUsername;
    }

    public static void setNextId(int id) {
        nextId = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean removeStock(int amount) {
        if (stock - amount >= 0) {
            stock -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getID() + " " + getName() + " by " + getSeller() + " at " + getPrice() + " with a stock of " + getStock();
    }

    public boolean isVisible() {
        return visible;
    }

    public String getSeller() {
        return seller;
    }

    public int getID() {
        return id;
    }

    public int getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
