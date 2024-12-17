package com.example.utils;

import com.example.House;

public class ConcreteHouse extends House {

    public ConcreteHouse(int id, String type, int area, int floors, double price) {
        super(id, type, area, floors, price);
    }

    @Override
    public String toString() {
        return "House ID: " + getId() + ", Type: " + getType() + ", Area: " + getArea() +
               ", Floors: " + getFloors() + ", Price: " + getPrice();
    }
}
