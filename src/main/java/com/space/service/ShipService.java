package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {

    List<Ship> findAll();
    Page<Ship> gelAllShips(Specification<Ship> specification, Pageable sortedByName);
    List<Ship> gelAllShips(Specification<Ship> specification);

    Ship findById(Long id);
    Ship save(Ship ship);

    Ship update (Ship ship, Long id);

    boolean existById(Long id);

    public boolean delete(Long id);
    public int shipCount();


    //Specifications
    Specification<Ship> filterByName(String name);

    Specification<Ship> filterByPlanet(String planet);

    Specification<Ship> filterByShipType(ShipType shipType);

    Specification<Ship> filterByDate(Long after, Long before);

    Specification<Ship> filterByUsage(Boolean isUsed);

    Specification<Ship> filterBySpeed(Double min, Double max);

    Specification<Ship> filterByCrewSize(Integer min, Integer max);

    Specification<Ship> filterByRating(Double min, Double max);
}
