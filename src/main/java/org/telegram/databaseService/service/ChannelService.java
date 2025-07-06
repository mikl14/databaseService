package org.telegram.databaseService.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.telegram.databaseService.entity.Channel;
import org.telegram.databaseService.repository.ChannelRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChannelService {
    final ChannelRepository channelRepository;

    public ChannelService(@Lazy ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Caching(
            put = {
                    @CachePut(value = "channelsByChatId", key = "#channel.chatId"),
                    @CachePut(value = "channelsByInviteLink", key = "#channel.inviteLink")
            }
    )
    public Channel saveChannel(Channel channel) {
        return channelRepository.save(channel);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "channelsByChatId", key = "#channel.chatId"),
                    @CacheEvict(value = "channelsByInviteLink", key = "#channel.inviteLink")
            }
    )
    public void deleteChannel(Channel channel) {
        channelRepository.delete(channel);
    }

    @Cacheable(value = "channelsByChatId", key = "#chatId")
    public Channel findChannel(long chatId) {
        return channelRepository.findByChatId(chatId).orElseThrow(NoSuchElementException::new);
    }

    @Cacheable(value = "channelsByInviteLink", key = "#inviteLink")
    public Channel findChannelByInviteLink(String inviteLink) {
        return channelRepository.findByInviteLink(inviteLink).orElseThrow(NoSuchElementException::new);
    }

    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }
}
