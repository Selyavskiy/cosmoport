package com.space.service;

import com.space.model.Ship;
import java.util.List;

import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Transactional
@Service("shipService")
public class ShipServiceImpl implements ShipService{


    // Хранилище Ship
    private static final Map<Long, Ship> SHIP_REPOSITORY_MAP = new HashMap<>();

    // Переменная для генерации ID Ship
    private static final AtomicInteger SHIP_ID_HOLDER = new AtomicInteger();

    //Репозиторий
    private ShipRepository shipRepository;



    @Override
    public Ship save(Ship ship) {





        //Проверка указания параметра isUsed
        if (ship.getUsed() == null){
            ship.setUsed(false);
        }


        //присвоение рейтинга
        ship.setRating(getRaiting(ship));

        //добавляем корабль в БД
        return shipRepository.saveAndFlush(ship);


    }

    //к удалению
    @Override
    public List<Ship> findAll() {

        List<Ship> listShip = new ArrayList<>();
        Iterable<Ship> iterator = shipRepository.findAll();
        iterator.forEach(listShip :: add);

        return listShip;

    }


    @Override
    public List<Ship> gelAllShips(Specification<Ship> specification) {
        return shipRepository.findAll(specification);
    }

    @Override
    public Page<Ship> gelAllShips(Specification<Ship> specification, Pageable sortedByName) {
        return shipRepository.findAll(specification, sortedByName);
    }






    @Override
    @Transactional(readOnly = true)
    public Ship findById(Long id) {
        return shipRepository.findById(id).get();
    }


    @Override
    public Ship update(Ship ship, Long id) {

        Ship updatedShip = shipRepository.findById(id).get();

        if (ship.getName() != null){
           updatedShip.setName(ship.getName());
        }

        if (ship.getPlanet() != null){
            updatedShip.setPlanet(ship.getPlanet());
        }

        if (ship.getShipType() != null){
            updatedShip.setShipType(ship.getShipType());
        }

        if (ship.getProdDate() != null){
            updatedShip.setProdDate(ship.getProdDate());
        }

        if (ship.getUsed() != null){
            updatedShip.setUsed(ship.getUsed());
        }

        if (ship.getSpeed() != null){
            updatedShip.setSpeed(ship.getSpeed());
        }

        if (ship.getCrewSize() != null){
            updatedShip.setCrewSize(ship.getCrewSize());
        }

        updatedShip.setRating(getRaiting(updatedShip));

        return shipRepository.saveAndFlush(updatedShip);

    }

    @Override
    public boolean existById (Long id){
        return shipRepository.existsById(id);
    }


    @Override
    public boolean delete(Long id) {

        shipRepository.deleteById(id);
        return true;
    }



    @Override
    public int shipCount() {

        return (int) shipRepository.count();
    }

    @Autowired
    public void setShipRepository (ShipRepository shipRepository){
        this.shipRepository = shipRepository;
    }

    private Double getRaiting (Ship ship){

        //присвоение рейтинга кораблю
        Double rait;
        Double k = 1.0;
        int y0 = 3019;

        //Получение года выпуска из даты
        Calendar y1 = Calendar.getInstance();
        if (ship.getProdDate() != null) {
            y1.setTime(ship.getProdDate());
        }

        //Получение коэффициента новый/не новый
        if (ship.getUsed()) {
            k = 0.5;
        }

        //Расчет рейтинга корабля
        rait = (80 * ship.getSpeed() * k)/(y0 - y1.get(Calendar.YEAR) + 1);

        //округление рейтинга до сотых
        rait = rait * 100;
        int raitMiddle = (int) Math.round(rait);
        rait = Double.valueOf(raitMiddle) / 100;

        return rait;
    }


    //Specifications
    @Override
    public Specification<Ship> filterByName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Ship> filterByPlanet(String planet) {
        return (root, query, cb) -> planet == null ? null : cb.like(root.get("planet"), "%" + planet + "%");
    }

    @Override
    public Specification<Ship> filterByShipType(ShipType shipType) {
        return (root, query, cb) -> shipType == null ? null : cb.equal(root.get("shipType"), shipType);
    }


    @Override
    public Specification<Ship> filterByDate(Long after, Long before) {
        return (root, query, cb) -> {
            if (after == null && before == null)
                return null;
            if (after == null) {
                Date before1 = new Date(before);
                return cb.lessThanOrEqualTo(root.get("prodDate"), before1);
            }
            if (before == null) {
                Date after1 = new Date(after);
                return cb.greaterThanOrEqualTo(root.get("prodDate"), after1);
            }
            Date before1 = new Date(before);
            Date after1 = new Date(after);
            return cb.between(root.get("prodDate"), after1, before1);
        };
    }

    @Override
    public Specification<Ship> filterByUsage(Boolean isUsed) {
        return (root, query, cb) -> {
            if (isUsed == null)
                return null;
            if (isUsed)
                return cb.isTrue(root.get("isUsed"));
            else return cb.isFalse(root.get("isUsed"));
        };
    }

    @Override
    public Specification<Ship> filterBySpeed(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("speed"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("speed"), min);

            return cb.between(root.get("speed"), min, max);
        };
    }

    @Override
    public Specification<Ship> filterByCrewSize(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("crewSize"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("crewSize"), min);

            return cb.between(root.get("crewSize"), min, max);
        };
    }

    @Override
    public Specification<Ship> filterByRating(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("rating"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("rating"), min);

            return cb.between(root.get("rating"), min, max);
        };
    }




}
