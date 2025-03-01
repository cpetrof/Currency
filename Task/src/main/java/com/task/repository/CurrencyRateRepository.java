package com.task.repository;


import com.task.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    Optional<CurrencyRate> findTopByOrderByTimestampDesc();

    List<CurrencyRate> findByTimestampBetween(LocalDateTime start, LocalDateTime end);



}
