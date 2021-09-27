package com.demidovn.fruitbounty.server.persistence.repository;

import com.demidovn.fruitbounty.server.persistence.entities.metrics.Metrics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricsRepository extends CrudRepository<Metrics, Long> {
}
