package com.JustAlo.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BusModel {

    private Long id;
    private Long vendorId; // Assuming you want to store the vendor ID
    private String busNumber;
    private int totalSeats;
    private String type;
    private Boolean ac;
//    private String status; // Assuming you have an Enum for BusStatus
    private String layout;

    private String chassisNum;
    private int noOfRow;
    private int lastRowSeats;
    private String insuranceNo;
    private String fromDate; // Assuming you will handle the date format appropriately
    private String toDate;   // Assuming you will handle the date format appropriately
    private MultipartFile insuranceImg; // This would be the URL or path to the image
//    private Boolean verified;

    // Add constructors, if needed
    // public BusModel() {}
    // public BusModel(...) { ... }
}
