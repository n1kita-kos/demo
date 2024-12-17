package com.example;

import java.util.ArrayList;
import java.util.List;

public class HouseCollection {
    private List<House> houses = new ArrayList<>();

    public void addHouse(House house) {
        houses.add(house);
    }

    public void removeHouse(int id) {
        houses.removeIf(h -> h.getId() == id);
    }

    public List<House> getAllHouses() {
        return houses;
    }

    public House getHouseById(int id) {
        for (House house : houses) {
            if (house.getId() == id) {
                return house;
            }
        }
        return null; 
    }
}
