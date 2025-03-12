package org.telegram.databaseService.repository;

import org.telegram.databaseService.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findById(Integer id);

    Optional<Chat> findByChatId(Long chatId);

    @Query("SELECT u FROM Chat u JOIN u.chanelList p WHERE p.chatId = :chatId")
    List<Chat> findChatByChannelId(@Param("chatId") long chatId);
}
