package com.example;

public abstract class HouseDecorator extends House {
    protected House house; 

    public HouseDecorator(House house) {
        super(house.getId(), house.getType(), house.getArea(), house.getFloors(), house.getPrice());
        this.house = house;
    }

    @Override
    public String toString() {
        return house.toString(); 
    }
    
    
}
