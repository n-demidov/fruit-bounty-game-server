package com.demidovn.fruitbounty.server.persistence.repository;

import com.demidovn.fruitbounty.server.persistence.entities.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  List<User> findByThirdPartyIdAndThirdPartyType(String thirdPartyId, String thirdPartyType);

  @Query("select u from User u where u.score >= :minRating")
  List<User> getTopRatedUsers(@Param("minRating") int minRating, Pageable pageable);

  @Modifying(clearAutomatically  = true)
  @Query("delete from User u where u.score <= :minScoreThreshold")
  int deleteByScoreLessThanEqual(@Param("minScoreThreshold") int minScoreThreshold);

}
