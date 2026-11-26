package com.networkanomaly.backend.repository;

import com.networkanomaly.backend.model.AnomalyTraffic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomalyTrafficRepository extends JpaRepository<AnomalyTraffic, Long> {

    List<AnomalyTraffic> findTop100ByOrderByTimestampDesc();

    List<AnomalyTraffic> findAllByOrderByTimestampDesc();

    List<AnomalyTraffic> findByReviewStatus(String reviewStatus);
}
