package com.networkanomaly.backend.controller;

import com.networkanomaly.backend.dto.TrafficFlowDTO;
import com.networkanomaly.backend.model.AnomalyTraffic;
import com.networkanomaly.backend.model.NormalTraffic;
import com.networkanomaly.backend.service.TrafficService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/traffic")
@CrossOrigin(origins = "*")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    private final Random random = new Random();

    /**
     * Endpoint where Raspberry Pi sends classified traffic flows.
     */
    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingestTraffic(@RequestBody TrafficFlowDTO flowDTO) {
        TrafficFlowDTO saved = trafficService.processAndSaveTraffic(flowDTO);
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Traffic flow ingested and broadcasted successfully",
                "flowStatus", saved.getStatus(),
                "timestamp", saved.getTimestamp()
        ));
    }

    /**
     * Fetch recent normal flows.
     */
    @GetMapping("/recent-normal")
    public ResponseEntity<List<NormalTraffic>> getRecentNormalTraffic() {
        return ResponseEntity.ok(trafficService.getRecentNormalTraffic());
    }

    /**
     * Fetch all permanently stored anomaly records.
     */
    @GetMapping("/anomalies")
    public ResponseEntity<List<AnomalyTraffic>> getAnomalies() {
        return ResponseEntity.ok(trafficService.getAllAnomalies());
    }

    /**
     * Admin feedback endpoint: mark an anomaly as CONFIRMED_ATTACK or FALSE_POSITIVE.
     */
    @PatchMapping("/anomalies/{id}/review")
    public ResponseEntity<AnomalyTraffic> reviewAnomaly(
            @PathVariable Long id,
            @RequestParam String decision) {
        return ResponseEntity.ok(trafficService.reviewAnomaly(id, decision));
    }

    /**
     * Export verified normal traffic (plus admin-marked FALSE_POSITIVE flows) from PostgreSQL for ML retraining.
     */
    @GetMapping("/training-data")
    public ResponseEntity<List<Map<String, Object>>> getTrainingData() {
        return ResponseEntity.ok(trafficService.getVerifiedTrainingFlows());
    }

    /**
     * Fetch traffic statistics.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(trafficService.getTrafficStats());
    }

    /**
     * Manual endpoint to trigger the retention cleanup for testing.
     */
    @PostMapping("/cleanup-now")
    public ResponseEntity<Map<String, Object>> triggerRetentionCleanup(@RequestParam(defaultValue = "7") int days) {
        int deleted = trafficService.purgeNormalTrafficOlderThanDays(days);
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Purged normal traffic older than " + days + " days",
                "deletedCount", deleted
        ));
    }

    /**
     * Simulation endpoint to test dashboard without having the Pi running yet.
     */
    @PostMapping("/simulate")
    public ResponseEntity<TrafficFlowDTO> simulateTraffic(@RequestParam(defaultValue = "NORMAL") String type) {
        TrafficFlowDTO flow = new TrafficFlowDTO();
        flow.setSensorId("rpi4-node-01");
        flow.setTimestamp(LocalDateTime.now());

        String[] sampleDstIps = {"104.244.42.1", "142.250.190.46", "13.107.42.16", "185.199.108.153", "192.168.1.1"};
        String[] protocols = {"TCP", "UDP"};
        int[] ports = {80, 443, 22, 53, 8080};

        flow.setSrcIp("192.168.1." + (100 + random.nextInt(50)));
        flow.setDstIp(sampleDstIps[random.nextInt(sampleDstIps.length)]);
        flow.setSrcPort(30000 + random.nextInt(30000));
        flow.setDstPort(ports[random.nextInt(ports.length)]);
        flow.setProtocol(protocols[random.nextInt(protocols.length)]);

        if ("ANOMALY".equalsIgnoreCase(type)) {
            flow.setStatus("ANOMALY");
            flow.setSeverity("CRITICAL");
            flow.setAttackType("SYN Flood / Volumetric Spike");
            flow.setDetails("Unusually high packets per second detected exceeding 3.5 sigma of learned baseline");
            flow.setDuration(0.35 + (random.nextDouble() * 0.5));
            flow.setPacketCount(1200L + random.nextInt(5000));
            flow.setTotalBytes(780000L + random.nextInt(2000000));
            flow.setAveragePacketSize(650.0);
            flow.setPacketsPerSecond(3500.0 + random.nextInt(12000));
            flow.setBytesPerSecond(2200000.0 + random.nextInt(8000000));
        } else {
            flow.setStatus("NORMAL");
            flow.setDuration(1.2 + (random.nextDouble() * 4.0));
            flow.setPacketCount(10L + random.nextInt(60));
            flow.setTotalBytes(1200L + random.nextInt(8500));
            flow.setAveragePacketSize(140.0 + random.nextInt(200));
            flow.setPacketsPerSecond(5.0 + (random.nextDouble() * 25.0));
            flow.setBytesPerSecond(800.0 + (random.nextDouble() * 3500.0));
        }

        trafficService.processAndSaveTraffic(flow);
        return ResponseEntity.ok(flow);
    }
}
