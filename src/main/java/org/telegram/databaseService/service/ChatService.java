package org.telegram.databaseService.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.telegram.databaseService.entity.Chat;
import org.telegram.databaseService.repository.ChatRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChatService {
    final ChatRepository chatRepository;

    public ChatService(@Lazy ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @CachePut(value = "chatById", key = "#chat.chatId")
    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }

    @CacheEvict(value = "chatById", key = "#chat.chatId")
    public void deleteChat(Chat chat) {
        chatRepository.delete(chat);
    }

    @Cacheable(value = "chatById", key = "#chatId")
    public Chat findChat(long chatId) {
        return chatRepository.findByChatId(chatId).orElseThrow(NoSuchElementException::new);
    }

    public List<Chat> findChatByChannelId(long channelId) {
        return chatRepository.findChatByChannelId(channelId);
    }

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
}
