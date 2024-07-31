package com.JustAlo.Service;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//
//import java.io.IOException;
//
//
//@Service
//public class DigitalOceanService {
//
//    @Autowired
//    private AmazonS3 s3Client;
//
//    private final String bucketName = "studycycle";
//
//    public String uploadFile(MultipartFile file) throws IOException {
//        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(file.getSize());
//        metadata.setContentType(file.getContentType());
//
//        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
//        return s3Client.getUrl(bucketName, fileName).toString();
//    }
//}
