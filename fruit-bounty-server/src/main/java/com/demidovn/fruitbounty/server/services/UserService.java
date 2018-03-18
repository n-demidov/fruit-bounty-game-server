package com.demidovn.fruitbounty.server.services;

import com.demidovn.fruitbounty.server.AppConfigs;
import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.persistence.entities.User;
import com.demidovn.fruitbounty.server.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class UserService {

  private static final String USER_SCORE_FIELD = "score";

  @Autowired
  private UserRepository userRepository;

  private PageRequest ratingTablePage = new PageRequest(
    0,
    AppConfigs.RATING_TABLE_PLAYERS_COUNT,
    new Sort(Direction.DESC, USER_SCORE_FIELD));

  public User create(User user) {
    return userRepository.save(user);
  }

  public User get(long id) {
    return userRepository.findOne(id);
  }

  public List<User> findByThirdPartyInfo(ThirdPartyAuthedUserInfo thirdPartyAuthedUserInfo) {
    return userRepository.findByThirdPartyIdAndThirdPartyType(
      thirdPartyAuthedUserInfo.getThirdPartyId(), thirdPartyAuthedUserInfo.getThirdPartyType());
  }

  public User update(User user) {
    return userRepository.save(user);
  }

  public List<User> getTopRated() {
    return userRepository.getTopRatedUsers(
      AppConfigs.RATING_TABLE_MIN_PLAYERS_RATING,
      ratingTablePage);
  }

  public long getCount() {
    return userRepository.count();
  }

  /*
    It is needed because there is a limit of rows for my free DB account.
   */
  @Transactional
  public void clearNotActual() {
    if (userRepository.count() >= AppConfigs.USERS_COUNT_LIMIT_TO_CLEAR_DB) {
      log.info("There are too much users in DB. Starting of clear users from DB...");

      int deletedUsersCount = userRepository.deleteByScoreLessThanEqual(AppConfigs.INITIAL_USER_SCORE);

      log.debug("Deleted {} users from DB", deletedUsersCount);
    }
  }

}
