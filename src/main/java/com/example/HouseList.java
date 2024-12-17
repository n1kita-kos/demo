package com.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HouseList extends HouseCollection {

    public HouseList() {
    }
    private List<House> houses = new ArrayList<>();

    @Override
    public void addHouse(House house) {
        houses.add(house);
    }

    @Override
    public void removeHouse(int id) {
        houses.removeIf(h -> h.getId() == id);
    }

    // @Override
    // public void updateHouse(House house) {
    //     for (int i = 0; i < houses.size(); i++) {
    //         if (houses.get(i).getId() == house.getId()) {
    //             houses.set(i, house);
    //         }
    //     }
    // }

    @Override
    public List<House> getAllHouses() {
        return houses;
    }
    public void sortByPrice() {
        houses.sort(Comparator.comparingDouble(House::getPrice));
    }

    public void sortByArea() {
        houses.sort(Comparator.comparingInt(House::getArea));
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
