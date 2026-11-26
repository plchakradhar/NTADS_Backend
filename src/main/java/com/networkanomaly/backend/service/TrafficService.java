package com.networkanomaly.backend.service;

import com.networkanomaly.backend.dto.TrafficFlowDTO;
import com.networkanomaly.backend.model.AnomalyTraffic;
import com.networkanomaly.backend.model.NormalTraffic;
import com.networkanomaly.backend.repository.AnomalyTrafficRepository;
import com.networkanomaly.backend.repository.NormalTrafficRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TrafficService {

    @Autowired
    private NormalTrafficRepository normalTrafficRepository;

    @Autowired
    private AnomalyTrafficRepository anomalyTrafficRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public TrafficFlowDTO processAndSaveTraffic(TrafficFlowDTO dto) {
        if (dto.getTimestamp() == null) {
            dto.setTimestamp(LocalDateTime.now());
        }

        boolean isAnomaly = "ANOMALY".equalsIgnoreCase(dto.getStatus());

        if (isAnomaly) {
            AnomalyTraffic anomaly = new AnomalyTraffic(
                    dto.getSensorId(),
                    dto.getSrcIp(),
                    dto.getDstIp(),
                    dto.getSrcPort(),
                    dto.getDstPort(),
                    dto.getProtocol(),
                    dto.getDuration(),
                    dto.getPacketCount(),
                    dto.getTotalBytes(),
                    dto.getAveragePacketSize(),
                    dto.getPacketsPerSecond(),
                    dto.getBytesPerSecond(),
                    "ANOMALY",
                    dto.getSeverity() != null ? dto.getSeverity() : "HIGH",
                    dto.getAttackType() != null ? dto.getAttackType() : "Suspicious Pattern Detected",
                    dto.getDetails(),
                    dto.getTimestamp()
            );
            AnomalyTraffic savedAnomaly = anomalyTrafficRepository.save(anomaly);
            dto.setId(savedAnomaly.getId());
            dto.setReviewStatus(savedAnomaly.getReviewStatus());

            // Broadcast to all traffic subscribers and dedicated high-priority anomaly channel
            messagingTemplate.convertAndSend("/topic/traffic", dto);
            messagingTemplate.convertAndSend("/topic/anomalies", dto);

        } else {
            NormalTraffic normal = new NormalTraffic(
                    dto.getSensorId(),
                    dto.getSrcIp(),
                    dto.getDstIp(),
                    dto.getSrcPort(),
                    dto.getDstPort(),
                    dto.getProtocol(),
                    dto.getDuration(),
                    dto.getPacketCount(),
                    dto.getTotalBytes(),
                    dto.getAveragePacketSize(),
                    dto.getPacketsPerSecond(),
                    dto.getBytesPerSecond(),
                    "NORMAL",
                    dto.getTimestamp()
            );
            NormalTraffic savedNormal = normalTrafficRepository.save(normal);
            dto.setId(savedNormal.getId());

            // Broadcast to traffic subscribers
            messagingTemplate.convertAndSend("/topic/traffic", dto);
        }

        return dto;
    }

    public List<NormalTraffic> getRecentNormalTraffic() {
        return normalTrafficRepository.findTop50ByOrderByTimestampDesc();
    }

    public List<AnomalyTraffic> getAllAnomalies() {
        return anomalyTrafficRepository.findAllByOrderByTimestampDesc();
    }

    public List<AnomalyTraffic> getRecentAnomalies() {
        return anomalyTrafficRepository.findTop100ByOrderByTimestampDesc();
    }

    public Map<String, Object> getTrafficStats() {
        long normalCount = normalTrafficRepository.count();
        long anomalyCount = anomalyTrafficRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNormalFlows", normalCount);
        stats.put("totalAnomalyFlows", anomalyCount);
        stats.put("totalFlows", normalCount + anomalyCount);
        stats.put("retentionDays", 7);
        return stats;
    }

    @Transactional
    public int purgeNormalTrafficOlderThanDays(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return normalTrafficRepository.deleteByTimestampBefore(cutoff);
    }

    @Transactional
    public AnomalyTraffic reviewAnomaly(Long id, String decision) {
        if (!"CONFIRMED_ATTACK".equalsIgnoreCase(decision) && !"FALSE_POSITIVE".equalsIgnoreCase(decision)) {
            throw new IllegalArgumentException("Invalid review decision: " + decision);
        }
        AnomalyTraffic anomaly = anomalyTrafficRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anomaly record not found: " + id));
        anomaly.setReviewStatus(decision.toUpperCase());
        anomaly.setReviewedAt(LocalDateTime.now());
        return anomalyTrafficRepository.save(anomaly);
    }

    public List<Map<String, Object>> getVerifiedTrainingFlows() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (NormalTraffic n : normalTrafficRepository.findTop5000ByOrderByTimestampDesc()) {
            result.add(toTrainingMap(
                    n.getSrcIp(), n.getDstIp(), n.getSrcPort(), n.getDstPort(),
                    n.getProtocol(), n.getDuration(), n.getPacketCount(), n.getTotalBytes(),
                    n.getAveragePacketSize(), n.getPacketsPerSecond(), n.getBytesPerSecond(),
                    n.getTimestamp()
            ));
        }

        for (AnomalyTraffic fp : anomalyTrafficRepository.findByReviewStatus("FALSE_POSITIVE")) {
            result.add(toTrainingMap(
                    fp.getSrcIp(), fp.getDstIp(), fp.getSrcPort(), fp.getDstPort(),
                    fp.getProtocol(), fp.getDuration(), fp.getPacketCount(), fp.getTotalBytes(),
                    fp.getAveragePacketSize(), fp.getPacketsPerSecond(), fp.getBytesPerSecond(),
                    fp.getTimestamp()
            ));
        }

        return result;
    }

    private Map<String, Object> toTrainingMap(
            String srcIp, String dstIp, Integer srcPort, Integer dstPort,
            String protocol, Double duration, Long packetCount, Long totalBytes,
            Double avgPacketSize, Double pps, Double bps, LocalDateTime timestamp) {
        Map<String, Object> row = new HashMap<>();
        row.put("srcIp", srcIp);
        row.put("dstIp", dstIp);
        row.put("srcPort", srcPort);
        row.put("dstPort", dstPort);
        row.put("protocol", protocol);
        row.put("duration", duration);
        row.put("packetCount", packetCount);
        row.put("totalBytes", totalBytes);
        row.put("averagePacketSize", avgPacketSize);
        row.put("packetsPerSecond", pps);
        row.put("bytesPerSecond", bps);
        row.put("timestamp", timestamp);
        return row;
    }
}
