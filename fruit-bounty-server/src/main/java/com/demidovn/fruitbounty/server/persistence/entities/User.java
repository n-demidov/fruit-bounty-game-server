package com.demidovn.fruitbounty.server.persistence.entities;

import com.demidovn.fruitbounty.server.persistence.converters.attributes.TimestampConverter;
import java.time.Instant;
import javax.annotation.Nullable;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@Entity
@Table(
  name = "accounts",
  uniqueConstraints =
    {@UniqueConstraint(columnNames={User.THIRD_PARTY_TYPE_COLUMN, User.THIRD_PARTY_ID_COLUMN})})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {

  public static final String THIRD_PARTY_TYPE_COLUMN = "third_party_type";
  public static final String THIRD_PARTY_ID_COLUMN = "third_party_id";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = THIRD_PARTY_TYPE_COLUMN, length = 2, nullable = false, updatable = false)
  private String thirdPartyType;

  @Column(name = THIRD_PARTY_ID_COLUMN, nullable = false, updatable = false)
  private String thirdPartyId;

  @Column(name = "public_name", length = 40, nullable = false)
  private String publicName;

  private String img;
  private int score;
  private int wins, defeats, draws;

  @Column(name = "is_admin", nullable = false)
  private boolean admin;

  @Setter(AccessLevel.NONE)
  @Convert(converter = TimestampConverter.class)
  @Column(name = "created_on", columnDefinition = "TIMESTAMPTZ", nullable = false, updatable = false)
  private long created;

  @Convert(converter = TimestampConverter.class)
  @Column(name = "last_login", columnDefinition = "TIMESTAMPTZ", nullable = false)
  private long lastLogin;

  @Nullable
  @Convert(converter = TimestampConverter.class)
  @Column(name = "last_game_ended", columnDefinition = "TIMESTAMPTZ", insertable = false)
  private Long lastGameEnded;

  @PrePersist
  private void onCreate() {
    created = Instant.now().toEpochMilli();
    lastLogin = created;
  }

}
