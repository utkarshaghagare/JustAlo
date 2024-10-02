package com.JustAlo.Repo;

import com.JustAlo.Entity.Costing;
import com.JustAlo.Entity.StopPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StopPriceRepository extends JpaRepository<StopPrice, Long> {
}