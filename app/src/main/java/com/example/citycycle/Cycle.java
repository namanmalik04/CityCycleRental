package com.example.citycycle;

public class Cycle {
    private int id;
    private String name;

    private String description;
    private String type;
    private String city;
    private double price;
    private int available;
    private byte[] bikeImage;


    public Cycle(int id, String name, String description, String type, String city, double price, int available, byte[] bikeImage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.city = city;
        this.price = price;
        this.available = available;
        this.bikeImage = bikeImage;
    }


    public Cycle() {
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getCity() {
        return city;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailable() {
        return available;
    }

    public byte[] getBikeImage() {
        return bikeImage;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public void setBikeImage(byte[] bikeImage) {
        this.bikeImage = bikeImage;
    }


    @Override
    public String toString() {
        return "Cycle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", city='" + city + '\'' +
                ", price=" + price +
                ", available=" + available +
                '}';
    }
}