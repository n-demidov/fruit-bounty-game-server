package com.demidovn.fruitbounty.server.persistence.converters.attributes;

import java.sql.Timestamp;
import javax.persistence.AttributeConverter;

public class TimestampConverter implements AttributeConverter<Long, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(Long attribute) {
    return attribute != null ? new Timestamp(attribute) : null;
  }

  @Override
  public Long convertToEntityAttribute(Timestamp dbData) {
    return dbData != null ? dbData.toInstant().toEpochMilli() : null;
  }

}
