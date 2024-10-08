package com.JustAlo.Controller;

import com.JustAlo.Entity.Bus;
import com.JustAlo.Model.BusModel;
import com.JustAlo.Service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/buses")
public class BusController {

    @Autowired
    private BusService busService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<Bus> createBus(
           // @RequestParam("id")  id,
            @RequestParam("busNumber") String busNumber,
            @RequestParam("totalSeats") int totalSeats,
            @RequestParam("amenities") String amenities,
            @RequestParam("ac") Boolean ac,
            @RequestParam("busImg") MultipartFile bus_image,
            @RequestParam("layout") String layout,
            @RequestParam("chassisNum") String chassisNum,
            @RequestParam("noOfRow") int noOfRow,
            @RequestParam("lastRowSeats") int lastRowSeats,
            @RequestParam("insuranceNo") String insuranceNo,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate,
//            @RequestParam("verified") Boolean verified,
            @RequestParam("insuranceImg") MultipartFile insuranceImg
    ) {
        BusModel busModel = new BusModel();
      //  busModel.setId(id);
        busModel.setBusNumber(busNumber);
        busModel.setTotalSeats(totalSeats);
        busModel.setAmenities(amenities);
        busModel.setAc(ac);
        busModel.setBus_image(bus_image);
        busModel.setLayout(layout);
        busModel.setChassisNum(chassisNum);
        busModel.setNoOfRow(noOfRow);
        busModel.setLastRowSeats(lastRowSeats);
        busModel.setInsuranceNo(insuranceNo);
        busModel.setFromDate(fromDate);
        busModel.setToDate(toDate);
       // busModel.setVerified(verified);
        busModel.setInsuranceImg(insuranceImg);

        Bus createdBus = busService.createBus(busModel);
        return ResponseEntity.ok(createdBus);
    }

    //   // @PreAuthorize("hasRole('Vendor')")
//    public ResponseEntity<Bus> createBus(@RequestBody BusModel bus) {
//        Bus createdBus = busService.createBus(bus);
//        return ResponseEntity.ok(createdBus);
//    }
//1. admin api toget unverified buses and
// 2.mark bus verified
    @GetMapping("/verifiedAndAvailable")
    @PreAuthorize("hasRole('Vendor')")
    public List<Bus> getAllVerifiedBuses() {
        List<Bus> buses = busService.getAllVerifiedBuses();
        return buses;
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<Bus> getBusById(@PathVariable Long id) {
        Optional<Bus> bus = busService.getBusById(id);
        return bus.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/allBuses")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<List<Bus>> getAllBuses() {
        List<Bus> buses = busService.getAllBuses();
        return ResponseEntity.ok(buses);
    }

    @PutMapping("updateBus/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<Bus> updateBus(@PathVariable Long id, @RequestBody Bus busDetails) {
        Bus updatedBus = busService.updateBus(id, busDetails);
        return ResponseEntity.ok(updatedBus);
    }

    @DeleteMapping("deleteBus/{id}")
    @PreAuthorize("hasRole('Vendor')")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
}