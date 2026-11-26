package com.networkanomaly.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "normal_traffic", indexes = {
    @Index(name = "idx_normal_timestamp", columnList = "timestamp")
})
public class NormalTraffic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private String status; // NORMAL

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public NormalTraffic() {}

    public NormalTraffic(String sensorId, String srcIp, String dstIp, Integer srcPort, Integer dstPort,
                         String protocol, Double duration, Long packetCount, Long totalBytes,
                         Double averagePacketSize, Double packetsPerSecond, Double bytesPerSecond,
                         String status, LocalDateTime timestamp) {
        this.sensorId = sensorId;
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.protocol = protocol;
        this.duration = duration;
        this.packetCount = packetCount;
        this.totalBytes = totalBytes;
        this.averagePacketSize = averagePacketSize;
        this.packetsPerSecond = packetsPerSecond;
        this.bytesPerSecond = bytesPerSecond;
        this.status = status != null ? status : "NORMAL";
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
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

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
