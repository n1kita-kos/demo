package com.example;

public class DiscountDecorator extends HouseDecorator {
    private double discountRate;
    double pr;
    double pr2;
    double pr3=house.getPrice();
    double pr4;

    public DiscountDecorator(House house, double discountRate) {
        super(house); 
        this.discountRate = discountRate;
        pr=discountRate;
        pr2=1-pr;
        pr4=pr3*pr2;
    }

    public double getDiscountedPrice() {
        return pr4; 
    }
    @Override
    public double getPrice(){
        return pr4;
    }

    @Override
    public String toString() {
        return house.toString() + " with discounted price: " + getDiscountedPrice();
    }
}