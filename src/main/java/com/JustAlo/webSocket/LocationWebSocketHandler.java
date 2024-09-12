package com.JustAlo.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class LocationWebSocketHandler extends TextWebSocketHandler {

    // Map to hold trip IDs and their corresponding WebSocket sessions
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> tripSessions = new ConcurrentHashMap<>();
    // Map to hold the latest location updates for each trip
    private final ConcurrentHashMap<String, LocationUpdate> latestLocations = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Deserialize the message payload to LocationUpdate object
        LocationUpdate locationUpdate = objectMapper.readValue(message.getPayload(), LocationUpdate.class);

        // Handling driver updates location
        if ("DRIVER_UPDATE".equals(locationUpdate.getType())) {
            // Initialize sessions if not already present, effectively registering the trip
            CopyOnWriteArraySet<WebSocketSession> sessions = tripSessions.computeIfAbsent(locationUpdate.getTripId(), k -> new CopyOnWriteArraySet<>());

            // Add the driver's session to the trip
            sessions.add(session);

            // Store the latest location update for the trip
            latestLocations.put(locationUpdate.getTripId(), locationUpdate);

            // Broadcast location to all subscribed users (including drivers) for that trip
            broadcastLocationUpdate(sessions, locationUpdate);
        }
        // Handling user subscription to location updates for a trip
        else if ("USER_SUBSCRIBE".equals(locationUpdate.getType())) {
            // Add the user's session to the trip's session set
            CopyOnWriteArraySet<WebSocketSession> sessions = tripSessions.computeIfAbsent(locationUpdate.getTripId(), k -> new CopyOnWriteArraySet<>());
            sessions.add(session);
            System.out.println("User subscribed to trip: " + locationUpdate.getTripId());

            // Send the latest location update to the newly subscribed user if available
            LocationUpdate latestLocation = latestLocations.get(locationUpdate.getTripId());
            if (latestLocation != null) {
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(latestLocation)));
                } catch (Exception e) {
                    System.err.println("Error sending initial location update: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // Helper method to broadcast location updates to all sessions
    private void broadcastLocationUpdate(CopyOnWriteArraySet<WebSocketSession> sessions, LocationUpdate locationUpdate) {
        for (WebSocketSession s : sessions) {
            try {
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(locationUpdate)));
            } catch (Exception e) {
                System.err.println("Error broadcasting location update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Remove the session from all trip subscriptions
        tripSessions.values().forEach(sessions -> {
            if (sessions.remove(session)) {
                System.out.println("Session removed from trip.");
            }
        });
    }
}



