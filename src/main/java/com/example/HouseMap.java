package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HouseMap extends HouseCollection {
    private Map<Integer, House> houseMap = new HashMap<>();

    @Override
    public void addHouse(House house) {
        houseMap.put(house.getId(), house);
    }

    @Override
    public void removeHouse(int id) {
        houseMap.remove(id);
    }

    // @Override
    // public void updateHouse(House house) {
    //     houseMap.put(house.getId(), house);
    // }

    @Override
    public List<House> getAllHouses() {
        return new ArrayList<>(houseMap.values());
    }

    public void sortById() {
        houseMap = new TreeMap<>(houseMap);
    }
}
