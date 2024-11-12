package com.demidovn.fruitbounty.server.persistence.repository;

import com.demidovn.fruitbounty.server.persistence.entities.ChatHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatHistoryRepository extends CrudRepository<ChatHistory, Long> {

}
