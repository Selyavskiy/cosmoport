package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import com.space.service.ShipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipController {


    private final ShipService shipService;


    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }



    @PostMapping(value = "/rest/ships")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> create(@RequestBody Ship ship) {


        try {
            if (ship.getName().length() > 50 || ship.getName() == null || ship.getName().length() == 0 ||
                    ship.getPlanet().length() > 50 || ship.getPlanet() == null || ship.getPlanet().length() == 0 ||
                    ship.getSpeed() == null || ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99 ||
                    ship.getCrewSize() == null || ship.getCrewSize() < 1 || ship.getCrewSize() > 9999 ||
                    ship.getShipType() == null || ship.getProdDate().getTime() < 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        catch (NullPointerException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }



        return new ResponseEntity<>(shipService.save(ship), HttpStatus.OK);
    }


    @GetMapping(value = "/rest/ships")
    public ResponseEntity<List<Ship>> read(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "planet", required = false) String planet,
                                           @RequestParam(value = "shipType", required = false) ShipType shipType,
                                           @RequestParam(value = "after", required = false) Long after,
                                           @RequestParam(value = "before", required = false) Long before,
                                           @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                           @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                           @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                           @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                           @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                           @RequestParam(value = "minRating", required = false) Double minRating,
                                           @RequestParam(value = "maxRating", required = false) Double maxRating,
                                           @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                                           @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {


        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        final List<Ship> ships = shipService.gelAllShips(
                Specification.where(shipService.filterByName(name)
                        .and(shipService.filterByPlanet(planet)))
                        .and(shipService.filterByShipType(shipType))
                        .and(shipService.filterByDate(after, before))
                        .and(shipService.filterByUsage(isUsed))
                        .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                        .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                        .and(shipService.filterByRating(minRating, maxRating)), pageable)
                .getContent();


        return ships != null &&  !ships.isEmpty()
                ? new ResponseEntity<>(ships, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> read(@PathVariable(name = "id") Long id) {

        if(id == 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            final Ship ship = shipService.findById(id);
            if(ship == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return ship != null
                    ? new ResponseEntity<>(ship, HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }


    @GetMapping(value = "/rest/ships/count")
    public int shipCount(@RequestParam(value = "name", required = false) String name,
                         @RequestParam(value = "planet", required = false) String planet,
                         @RequestParam(value = "shipType", required = false) ShipType shipType,
                         @RequestParam(value = "after", required = false) Long after,
                         @RequestParam(value = "before", required = false) Long before,
                         @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                         @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                         @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                         @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                         @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                         @RequestParam(value = "minRating", required = false) Double minRating,
                         @RequestParam(value = "maxRating", required = false) Double maxRating) {
        final int shipCount = shipService.gelAllShips(
                Specification.where(shipService.filterByName(name)
                        .and(shipService.filterByPlanet(planet)))
                        .and(shipService.filterByShipType(shipType))
                        .and(shipService.filterByDate(after, before))
                        .and(shipService.filterByUsage(isUsed))
                        .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                        .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                        .and(shipService.filterByRating(minRating, maxRating)))
                .size();

        return shipCount;
    }


    @PostMapping(value = "/rest/ships/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @RequestBody Ship ship) {



            if (id == 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (!shipService.existById(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }



            if (ship.getName() != null && (ship.getName().length() > 50 || ship.getName().length() == 0)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (ship.getPlanet() != null && (ship.getPlanet().length() > 50 || ship.getPlanet().length() == 0)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (ship.getProdDate() != null && (ship.getProdDate().getTime() < 0)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }


            final Ship updatedShip = shipService.update(ship, id);

            return new ResponseEntity<>(updatedShip, HttpStatus.OK);





    }



    @DeleteMapping(value = "/rest/ships/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {

        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!shipService.existById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final boolean deleted = shipService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }




}
