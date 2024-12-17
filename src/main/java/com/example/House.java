package com.example;

import java.util.Objects;

public abstract class House {
    protected int id;
    protected String type;
    protected int area;
    protected int floors;
    protected double price;
    protected double discountRate;

    public House(int id, String type, int area, int floors, double price) {
        this.id = id;
        this.type = type;
        this.area = area;
        this.floors = floors;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public double getDiscountRate() {
        return price;
    }

    public void setDiscountRate(double price) {
        this.discountRate = price;
    }

    @Override
    public String toString() {
        return "House{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", area=" + area +
                ", floors=" + floors +
                ", price=" + price +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
    if (this == obj) {
        return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
        return false;
    }
    House house = (House) obj;
    return id == house.id &&
           Double.compare(house.area, area) == 0 &&
           floors == house.floors &&
           Double.compare(house.price, price) == 0 &&
           type.equals(house.type);
    }

    @Override
    public int hashCode() {
    return Objects.hash(id, area, floors, price, type);
    }

}
