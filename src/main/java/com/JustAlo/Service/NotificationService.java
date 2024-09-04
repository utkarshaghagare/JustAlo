package com.JustAlo.Service;

import com.JustAlo.Entity.Alert;
import com.JustAlo.Entity.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyVendor(Vendor vendor, Alert alert) {
        // Send a WebSocket message to the vendor's client
        messagingTemplate.convertAndSendToUser(vendor.getUsername(), "/topic/alerts", alert);
    }
}
