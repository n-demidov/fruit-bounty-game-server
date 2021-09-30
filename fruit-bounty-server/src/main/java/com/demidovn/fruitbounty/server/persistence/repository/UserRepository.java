package com.demidovn.fruitbounty.server.persistence.repository;

import com.demidovn.fruitbounty.server.persistence.entities.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  List<User> findByThirdPartyIdAndThirdPartyType(String thirdPartyId, String thirdPartyType);

  @Query("select u from User u where u.score >= :minRating")
  List<User> getTopRatedUsers(@Param("minRating") int minRating, Pageable pageable);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from User u where u.wins <= :winsThreshold and now() - u.lastLogin >= make_interval(0, 0, 0, 0, :hoursThreshold)")
  int deleteByWinsAndHours(@Param("winsThreshold") int winsThreshold, @Param("hoursThreshold") int hoursThreshold);

  @Transactional
  @Modifying(clearAutomatically  = true)
  @Query(value = "delete from accounts where id in (SELECT id \n" +
          "             FROM accounts \n" +
          "             where now() - last_login >= make_interval(0, 0, 0, 0, :hoursThreshold) \n" +
          "             order by score \n" +
          "             LIMIT :limit)"
          , nativeQuery = true)
  int deleteByHours(@Param("hoursThreshold") int hoursThreshold, @Param("limit") long limit);

}
