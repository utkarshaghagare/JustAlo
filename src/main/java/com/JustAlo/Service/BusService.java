package com.JustAlo.Service;

import com.JustAlo.Entity.Bus;
import com.JustAlo.Entity.Driver;
import com.JustAlo.Model.BusModel;
import com.JustAlo.Model.BusStatus;
import com.JustAlo.Repo.BusRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BusService {
    private final S3Client s3Client;
    private final String bucketName = "studycycle";
    private final String spaceUrl = "https://studycycle.blr1.digitaloceanspaces.com/";

    @Autowired
    private BusRepository busRepository;
    @Autowired
    private VendorService vendorService;

    public BusService(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public Bus createBus(BusModel bus) {
        Bus b = new Bus();
        try {
            // Upload documents to DigitalOcean Spaces
            String insuranceImgUrl = uploadFileToSpace(bus.getInsuranceImg());
            b.setVendor(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER));
            b.setBus_number(bus.getBusNumber());
            b.setTotal_seats(bus.getTotalSeats());
//            b.setType(bus.getType());
            b.setAc(bus.getAc());
            b.setStatus(BusStatus.AVAILABLE); // Ensure this matches your BusStatus enum
            b.setLayout(bus.getLayout());
            b.setChassis_num(bus.getChassisNum());
            b.setNo_of_row(bus.getNoOfRow());
            b.setLast_row_seats(bus.getLastRowSeats());
            b.setInsurance_no(bus.getInsuranceNo());
            b.setFromDate(dateFormat.parse(bus.getFromDate()));
            b.setToDate(dateFormat.parse(bus.getToDate()));
            b.setInsurance_img(insuranceImgUrl);
            b.setVerified(false);

            return busRepository.save(b);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file or save bus", e);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date", e);
        }
    }

    private String uploadFileToSpace(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + StringUtils.cleanPath(file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Path tempFile = Files.createTempFile("upload-", fileName);

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // Upload to DigitalOcean Spaces
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .acl("public-read")
                .build();

        s3Client.putObject(putObjectRequest, tempFile);

        // Clean up temporary file
        Files.deleteIfExists(tempFile);

        // Return the file URL
        return spaceUrl + fileName;
    }

    public Optional<Bus> getBusById(Long id) {
        return busRepository.findById(id);
    }


    public List<Bus> getAllVerifiedBuses() {
        List<Bus>buses=busRepository.findByVendorId(vendorService.findByUsername(JwtAuthenticationFilter.CURRENT_USER).getId());

        List<Bus> response= new ArrayList<>();
        for(Bus b:buses){
       if(b.getVerified() && b.getStatus().equals(BusStatus.AVAILABLE)){
           response.add(b);
       }
   }
        return response;
    }

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus updateBus(Long id, Bus busDetails) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
        bus.setBus_number(busDetails.getBus_number());
        bus.setTotal_seats(busDetails.getTotal_seats());
//        bus.setType(busDetails.getType());
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

    public Bus getVerfiedBusById(Long id) throws Exception {
         Bus bus=busRepository.findById(id).orElse(null);
         if(bus!=null && bus.getStatus().equals(BusStatus.AVAILABLE)){
             if(bus.getVerified()){
                 bus.setStatus(BusStatus.UNAVAILABLE);
                 busRepository.save(bus);
                 return bus;
             }
             throw new Exception("bus unverified");
         }
         throw new Exception("bus not found");
    }

    public List<Bus> getAllBusByPerticularVendor(Long id) {
        return busRepository.findByVendorId(id);
    }

    public String turnOffBus(Long id, BusStatus b) {
        Bus bus= getBusById(id).orElse(null);
        if(bus!=null){
            bus.setStatus(b);
            busRepository.save(bus);
        }
        return "Bus Turned off";
    }

}
