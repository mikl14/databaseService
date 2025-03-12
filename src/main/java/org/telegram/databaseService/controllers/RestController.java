package org.telegram.databaseService.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.telegram.databaseService.annotations.Logging;
import org.telegram.databaseService.entity.Channel;
import org.telegram.databaseService.service.ChannelService;
import org.telegram.databaseService.service.ChatService;

import java.util.Comparator;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/dataBase/api")
public class RestController {

    private final ChatService chatService;

    private final ChannelService channelService;
    private final ObjectMapper mapper;

    public RestController(ChatService chatService, ChannelService channelService, ObjectMapper mapper) {
        this.chatService = chatService;
        this.channelService = channelService;
        this.mapper = mapper;
    }

    /**
     * <b>getChatByChatId</b> - возвращает объект Chat по chatId
     *
     * @param chatId
     * @return
     */
    @Logging(entering = true,exiting = true)
    @GetMapping("/getChatByChatId")
    public String getChatByChatId(@RequestBody String chatId) {
        try {
            return mapper.writeValueAsString(chatService.findChat(Long.parseLong(chatId)));
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * <b>getChannelByInviteLink</b> - возвращает объект Channel по InviteLink
     *
     * @param InviteLink
     * @return
     */
    @GetMapping("/getChannelByInviteLink")
    public String getChannelByInviteLink(@RequestBody String InviteLink) {
        try {
            return mapper.writeValueAsString(channelService.findChannelByInviteLink(InviteLink));
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * <b>getChannelByChatId</b> - возвращает Channel по его chatId
     *
     * @param chatId
     * @return
     */
    @Logging(entering = true,exiting = true)
    @GetMapping("/getChannelByChatId")
    public String getChannelByChatId(@RequestBody String chatId) {
        try {
            return mapper.writeValueAsString(channelService.findChannel(Long.parseLong(chatId)));
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * <b>getPopularChannels</b> - возвращает 5 каналов с наибольшим числом подписчиков
     */
    @GetMapping("/getPopularChannels")
    public String getPopularChannels() {
        try {
            List<Channel> channelList = channelService.getAllChannels().stream().sorted(Comparator.comparingInt(a -> a.getChats().size())).limit(5).toList();
            return mapper.writeValueAsString(channelList);
        } catch (Exception e) {
            return e.toString();
        }
    }
}
