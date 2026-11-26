package com.networkanomaly.backend.dto;

import java.time.LocalDateTime;

public class TrafficFlowDTO {
    private Long id;
    private String sensorId;
    private String srcIp;
    private String dstIp;
    private Integer srcPort;
    private Integer dstPort;
    private String protocol;
    private Double duration;
    private Long packetCount;
    private Long totalBytes;
    private Double averagePacketSize;
    private Double packetsPerSecond;
    private Double bytesPerSecond;
    private String status; // NORMAL or ANOMALY
    private String severity; // HIGH, CRITICAL, MEDIUM (for anomaly)
    private String attackType;
    private String details;
    private String reviewStatus; // UNREVIEWED, CONFIRMED_ATTACK, FALSE_POSITIVE
    private LocalDateTime timestamp;

    public TrafficFlowDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    public String getSrcIp() { return srcIp; }
    public void setSrcIp(String srcIp) { this.srcIp = srcIp; }

    public String getDstIp() { return dstIp; }
    public void setDstIp(String dstIp) { this.dstIp = dstIp; }

    public Integer getSrcPort() { return srcPort; }
    public void setSrcPort(Integer srcPort) { this.srcPort = srcPort; }

    public Integer getDstPort() { return dstPort; }
    public void setDstPort(Integer dstPort) { this.dstPort = dstPort; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public Double getDuration() { return duration; }
    public void setDuration(Double duration) { this.duration = duration; }

    public Long getPacketCount() { return packetCount; }
    public void setPacketCount(Long packetCount) { this.packetCount = packetCount; }

    public Long getTotalBytes() { return totalBytes; }
    public void setTotalBytes(Long totalBytes) { this.totalBytes = totalBytes; }

    public Double getAveragePacketSize() { return averagePacketSize; }
    public void setAveragePacketSize(Double averagePacketSize) { this.averagePacketSize = averagePacketSize; }

    public Double getPacketsPerSecond() { return packetsPerSecond; }
    public void setPacketsPerSecond(Double packetsPerSecond) { this.packetsPerSecond = packetsPerSecond; }

    public Double getBytesPerSecond() { return bytesPerSecond; }
    public void setBytesPerSecond(Double bytesPerSecond) { this.bytesPerSecond = bytesPerSecond; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getAttackType() { return attackType; }
    public void setAttackType(String attackType) { this.attackType = attackType; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
