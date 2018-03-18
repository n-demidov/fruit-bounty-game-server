package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class RequestOperation2GameRequestActionConverter
  implements FruitServerConverter<RequestOperation, Boolean> {

  private static final String GAME_REQUEST_ACTION_TYPE = "ack", SUBMIT_GAME_REQUEST_VALUE = "y";

  @Override
  public Boolean convert(RequestOperation requestOperation) {
    String ack = requestOperation.getData().get(GAME_REQUEST_ACTION_TYPE);
    return Objects.equals(ack, SUBMIT_GAME_REQUEST_VALUE);
  }

}
