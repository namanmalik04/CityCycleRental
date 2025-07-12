package com.example.citycycle;

public class Rental {
    private int id;
    private int cycleId;
    private String name;
    private String type;
    private byte[] image;
    private String start;
    private String end;
    private int count;
    private double totalPrice;
    private boolean isTimeBased;
    private boolean isDayBased;

    public Rental(int id, int cycleId, String name, String type, byte[] image, String start, String end, int count, double totalPrice, boolean isTimeBased, boolean isDayBased) {
        this.id = id;
        this.cycleId = cycleId;
        this.name = name;
        this.type = type;
        this.image = image;
        this.start = start;
        this.end = end;
        this.count = count;
        this.totalPrice = totalPrice;
        this.isTimeBased = isTimeBased;
        this.isDayBased = isDayBased;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCycleId() {
        return cycleId;
    }

    public void setCycleId(int cycleId) {
        this.cycleId = cycleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isTimeBased() {
        return isTimeBased;
    }

    public void setTimeBased(boolean timeBased) {
        isTimeBased = timeBased;
    }


    public boolean isDayBased() {
        return isDayBased;
    }

    public void setDayBased(boolean dayBased) {
        isDayBased = dayBased;
    }

}

