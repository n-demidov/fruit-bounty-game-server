package com.demidovn.fruitbounty.server.services.operations.handlers;

import static com.demidovn.fruitbounty.server.AppConstants.MAX_PLAYER_NAME;
import static com.demidovn.fruitbounty.server.AppConstants.MIN_PLAYER_NAME;

import com.demidovn.fruitbounty.server.MetricsConsts;
import com.demidovn.fruitbounty.server.dto.operations.request.OperationType;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import com.demidovn.fruitbounty.server.persistence.entities.User;
import com.demidovn.fruitbounty.server.services.ClientNotifier;
import com.demidovn.fruitbounty.server.services.UserService;
import com.demidovn.fruitbounty.server.services.metrics.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
public class RenameOperationHandler implements OperationHandler {

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Autowired
  private UserService userService;

  @Autowired
  private ClientNotifier clientNotifier;

  @Autowired
  private StatService statService;

  @Override
  public OperationType getOperationType() {
    return OperationType.Rename;
  }

  @Override
  public void process(RequestOperation requestOperation) {
    statService.incCounter(MetricsConsts.OTHER.PLAYER_RENAME_TRY_STAT);

    String newName = conversionService.convert(requestOperation, String.class);
    newName = HtmlUtils.htmlEscape(newName).trim();

    if (isNameNotValid(newName)) {
      return;
    }

    long userId = requestOperation.getConnection().getUserId();
    User user = userService.get(userId);
    if (user.getPublicName().equals(newName)) {
      statService.incCounter(MetricsConsts.OTHER.PLAYER_RENAME_NOT_CHANGED_STAT);
      return;
    }

    user.setPublicName(newName);
    userService.update(user);
    clientNotifier.sendSelfUserInfo(user, requestOperation.getConnection());

    statService.incCounter(MetricsConsts.OTHER.PLAYER_RENAME_SUCCESS_STAT);
  }

  private boolean isNameNotValid(String name) {
    return name == null || name.length() < MIN_PLAYER_NAME || name.length() > MAX_PLAYER_NAME;
  }

}
