package com.networkanomaly.backend.repository;

import com.networkanomaly.backend.model.NormalTraffic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NormalTrafficRepository extends JpaRepository<NormalTraffic, Long> {

    List<NormalTraffic> findTop50ByOrderByTimestampDesc();

    List<NormalTraffic> findTop5000ByOrderByTimestampDesc();

    @Modifying
    @Transactional
    @Query("DELETE FROM NormalTraffic n WHERE n.timestamp < :cutoff")
    int deleteByTimestampBefore(@Param("cutoff") LocalDateTime cutoff);
}
