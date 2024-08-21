package com.JustAlo.Service;


import com.JustAlo.Entity.Rent;
import com.JustAlo.Entity.Vehicle;
import com.JustAlo.Repo.RentRepository;
import com.JustAlo.Repo.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentService {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public Rent addRentWithVehicles(Rent rent, List<Vehicle> vehicles) {
        // Persist each vehicle if necessary (depending on your design, this may be optional)
        for (Vehicle vehicle : vehicles) {
            vehicleRepository.save(vehicle);
        }

        // Associate the vehicles with the rent
        rent.setVehicles(vehicles);

        // Persist the rent entity along with associated vehicles
        return rentRepository.save(rent);
    }
}
