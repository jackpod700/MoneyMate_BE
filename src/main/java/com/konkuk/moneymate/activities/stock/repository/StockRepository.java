package com.konkuk.moneymate.activities.stock.repository;

import com.konkuk.moneymate.activities.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

}