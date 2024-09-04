//package com.JustAlo.Service;
//
//import com.JustAlo.Entity.Alert;
//import com.JustAlo.Entity.Vendor;
//import com.JustAlo.Model.AlertRequest;
//import com.JustAlo.Repo.AlertRepository;
//import com.JustAlo.Repo.VendorDao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//public class AlertService {
//
//    @Autowired
//    private VendorDao vendorRepository;
//
//    @Autowired
//    private AlertRepository alertRepository;
//
//    // Assuming you have a notification mechanism, like WebSocket, email, or SMS
//    @Autowired
//    private NotificationService notificationService;
//
//    public void sendAlertToVendor(AlertRequest alertRequest) {
//        // Find the vendor associated with the driver
//        Vendor vendor = vendorRepository.findByDriverId(alertRequest.getDriverId());
//
//        // Create a new alert and save it to the database
//        Alert alert = new Alert();
//        alert.setVendor(vendor);
//        alert.setMessage(alertRequest.getMessage());
//        alert.setTimestamp(LocalDateTime.now());
//        alertRepository.save(alert);
//
//        // Notify the vendor via the desired mechanism (e.g., WebSocket, email, SMS)
//        notificationService.notifyVendor(vendor, alert);
//    }
//}
