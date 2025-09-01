package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

}