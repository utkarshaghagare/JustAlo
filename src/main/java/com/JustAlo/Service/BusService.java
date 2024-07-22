package com.JustAlo.Service;

import com.JustAlo.Entity.Bus;
import com.JustAlo.Repo.BusRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;
    @Autowired
    private VendorService vendorService;

    public Bus createBus(Bus bus) {
        // Ensure the verified status is false when creating a new bus
        bus.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
        bus.setVerified(false);
        return busRepository.save(bus);
    }

    public Optional<Bus> getBusById(Long id) {
        return busRepository.findById(id);
    }


    public List<Bus> getAllVerifiedBuses() {
        return busRepository.findByVerifiedTrue();
    }

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus updateBus(Long id, Bus busDetails) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
        bus.setBus_number(busDetails.getBus_number());
        bus.setTotal_seats(busDetails.getTotal_seats());
        bus.setType(busDetails.getType());
        bus.setAc(busDetails.getAc());
        bus.setStatus(busDetails.getStatus());
        bus.setLayout(busDetails.getLayout());
        bus.setVerified(busDetails.getVerified());
        bus.setChassis_num(busDetails.getChassis_num());
        return busRepository.save(bus);
    }

    public void deleteBus(Long id) {
        busRepository.deleteById(id);
    }
}
