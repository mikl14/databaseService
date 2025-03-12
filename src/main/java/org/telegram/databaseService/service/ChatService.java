package org.telegram.databaseService.service;

import org.springframework.context.annotation.Lazy;
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

    public void saveChat(Chat chat) {
        chatRepository.save(chat);
    }

    public void deleteChat(Chat chat) {
        chatRepository.delete(chat);
    }

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
