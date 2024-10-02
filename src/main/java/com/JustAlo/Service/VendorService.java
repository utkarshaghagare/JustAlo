package com.JustAlo.Service;


import com.JustAlo.Entity.*;
import com.JustAlo.Model.TicketBooking;
import com.JustAlo.Model.VendorModel;
import com.JustAlo.OffersRepository;
import com.JustAlo.Repo.RoleDao;
import com.JustAlo.Repo.TripRepository;
import com.JustAlo.Repo.VendorDao;
import com.JustAlo.RequestOffersRepository;
import com.JustAlo.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class VendorService {

    private final S3Client s3Client;
    private final String bucketName = "studycycle";
    private final String spaceUrl = "https://studycycle.blr1.digitaloceanspaces.com/";
    @Autowired
    public VendorService(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    @Autowired
    private VendorDao vendorDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BookingService bookingService;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private RequestOffersRepository requestOffersRepository;
    @Autowired
    private OffersRepository offersRepository;

    //exception
    public Vendor registerVendor(VendorModel vendormodel) {


        if (vendormodel.password.equals(vendormodel.confirm_password)) {

            if(vendorDao==null){
                return saveVendor(vendormodel);
            }
            else{
                Optional<Vendor> vendor1 = vendorDao.findByUsername(vendormodel.getUsername());
                if (vendor1.isPresent()) {
                    //throw new RuntimeException();
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor Already Present");

                }
                else{
                    return saveVendor(vendormodel);
                }
            }
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password not matching");
    }


    public Vendor saveVendor(VendorModel vendormodel) {
        try {
            // Upload documents to DigitalOcean Spaces
            String doc1Url = uploadFileToSpace(vendormodel.getDoc1());
            String doc2Url = uploadFileToSpace(vendormodel.getDoc2());
            String profile_img =uploadFileToSpace(vendormodel.getProfile_img());

            // Save Vendor with document URLs
            Vendor vendor = new Vendor();
            Role role = roleDao.findById("Vendor")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            Set<Role> vendorRoles = new HashSet<>();
            vendorRoles.add(role);
            vendor.setRole(vendorRoles);
            vendor.setPassword(getEncodedPassword(vendormodel.getPassword()));
            vendor.setAddress(vendormodel.getAddress());
            vendor.setEmail(vendormodel.getEmail());
            vendor.setUsername(vendormodel.getUsername());
            vendor.setOrganization_name(vendormodel.getOrganization_name());
            vendor.setPhone_number(vendormodel.getPhone_number());
            vendor.setVerification_status(false);

            // Set the uploaded document URLs
            vendor.setDoc1(doc1Url);
            vendor.setDoc2(doc2Url);
            vendor.setProfile_img(profile_img);

            return vendorDao.save(vendor);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving vendor", e);
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

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public List<Vendor> tobeVerified() {
        return vendorDao.findUnverifiedVendors();
    }

    public Vendor markVerified(Long id) throws Exception {
        Optional<Vendor> optionalVendor = vendorDao.findById(id);
        if (!optionalVendor.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor with ID " + id + " not found");
        }
        Vendor vendor = optionalVendor.get();
        vendor.setVerification_status(true);

        vendorDao.save(vendor);
        return vendor;
    }

//    public Vendor markUnverified(Long id) throws Exception {
//        Optional<Vendor> optionalVendor = vendorDao.findById(id);
//        if (!optionalVendor.isPresent()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor with ID " + id + " not found");
//        }
//        Vendor vendor = optionalVendor.get();
//        vendor.setVerification_status(false);
//
//        vendorDao.save(vendor);
//        return vendor;
//    }

    public Vendor findByUsername(String currentUser) {
        return vendorDao.findByEmail(currentUser);
    }

    public Vendor findById(Long id) {
        return vendorDao.findById(id).orElse(null);
    }

    public List<Vendor> getAllVendor() {

        return vendorDao.findAll();
    }

    public RequestOffers makeOfferRequest(Long id, double percent) throws Exception {
        Trip trip =tripRepository.findById(id).orElse(null);
        if(trip!=null){
            if(trip.getVendor().getEmail().equals(JwtAuthenticationFilter.CURRENT_USER) ){
                return requestOffersRepository.save(new RequestOffers(trip,percent));
            }
            else throw new Exception("Trip is not scheduled by you ");
            }
        else throw new Exception("trip not found");
    }

    public List<RequestOffers> getOfferRequest() {
        return requestOffersRepository.findAll();
    }

    public Offers makeOffer(List<RequestOffers> requestOffersList, MultipartFile image, double percent) throws IOException {
        String banner =uploadFileToSpace(image);
        List<Trip> trips= new ArrayList<>();
        Offers savedoffer = offersRepository.save(new Offers(banner,percent));
        for(RequestOffers r: requestOffersList){
            trips.add(r.getTrip());
            Trip trip=tripRepository.findById(r.getTrip().getId()).orElse(null);
            if(trip!=null){
                trip.setOffers(savedoffer);
            }
        }
        savedoffer.setTrips(trips);
        return offersRepository.save(savedoffer);
    }

//    public String reserveSeat(Long id, List<Integer> seats) {
//        return bookingService.reserveSeat(id,seats);
//    }

//    public String bookSeat(TicketBooking ticketBooking) {
//        return bookingService.bookSeat(ticketBooking,)
//    }
public Vendor markUnverified(Long id) throws Exception {
    Optional<Vendor> optionalVendor = vendorDao.findById(id);
    if (!optionalVendor.isPresent()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor with ID " + id + " not found");
    }
    Vendor vendor = optionalVendor.get();
    vendor.setVerification_status(false);

    vendorDao.save(vendor);
    return vendor;
}
}



