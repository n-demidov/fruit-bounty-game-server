package com.demidovn.fruitbounty.server.persistence.entities;

import com.demidovn.fruitbounty.server.persistence.converters.attributes.TimestampConverter;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_history")
public class ChatHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Convert(converter = TimestampConverter.class)
  @Column(name = "updated_at", columnDefinition = "TIMESTAMPTZ", nullable = false)
  private long updatedAt;

  @Column(name = "message", nullable = false, updatable = false)
  private String message;

  @Column(name = "sender_id", nullable = false, updatable = false)
  private long senderId;

  @PrePersist
  @PreUpdate
  private void onCreate() {
    updatedAt = Instant.now().toEpochMilli();
  }

}
