package com.networkanomaly.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final List<Map<String, Object>> connectedDevices = new CopyOnWriteArrayList<>();
    private volatile LocalDateTime lastSyncTime;

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncDevices(@RequestBody Map<String, Object> payload) {
        Object rawDevices = payload.get("devices");
        List<Map<String, Object>> updated = new ArrayList<>();
        if (rawDevices instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> map) {
                    Map<String, Object> clean = new HashMap<>();
                    map.forEach((k, v) -> clean.put(String.valueOf(k), v));
                    updated.add(clean);
                }
            }
        }

        connectedDevices.clear();
        connectedDevices.addAll(updated);
        lastSyncTime = LocalDateTime.now();

        Map<String, Object> broadcast = Map.of(
                "devices", updated,
                "count", updated.size(),
                "lastSyncTime", lastSyncTime.toString()
        );
        messagingTemplate.convertAndSend("/topic/devices", (Object) broadcast);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "deviceCount", updated.size()
        ));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDevices() {
        return ResponseEntity.ok(Map.of(
                "devices", new ArrayList<>(connectedDevices),
                "count", connectedDevices.size(),
                "lastSyncTime", lastSyncTime != null ? lastSyncTime.toString() : ""
        ));
    }
}
