package com.demidovn.fruitbounty.server.persistence.entities.metrics;

import com.demidovn.fruitbounty.server.persistence.converters.attributes.TimestampConverter;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.Instant;

@Data
@Entity
@Table(
  name = "metrics")
@TypeDef(name = "json", typeClass = JsonType.class)
public class Metrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Convert(converter = TimestampConverter.class)
    @Column(name = "modified_time", columnDefinition = "TIMESTAMPTZ", nullable = false)
    private long modifiedTimeMs;

    @Column(name = "curr_metrics", nullable = false)
    private String currentMetrics;

    @Type(type = "json")
    @Column(name = "hist_metrics", columnDefinition = "json", nullable = false)
    private HistoryMetrics histMetrics;

    @PrePersist
    @PreUpdate
    private void onCreate() {
        modifiedTimeMs = Instant.now().toEpochMilli();
    }

}
