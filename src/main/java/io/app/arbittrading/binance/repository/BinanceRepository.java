package io.app.arbittrading.binance.repository;

import io.app.arbittrading.binance.model.Binance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinanceRepository extends JpaRepository<Binance, Long> {
}
