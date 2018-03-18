package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import org.springframework.stereotype.Component;

@Component
public class RequestOperation2ChatMessageConverter implements FruitServerConverter<RequestOperation, String> {

  private static final String CHAT_MESSAGE = "msg";

  @Override
  public String convert(RequestOperation requestOperation) {
    return requestOperation.getData().get(CHAT_MESSAGE);
  }

}
