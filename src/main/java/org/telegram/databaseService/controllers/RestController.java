package org.telegram.databaseService.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.telegram.databaseService.annotations.Logging;
import org.telegram.databaseService.entity.Channel;
import org.telegram.databaseService.entity.Chat;
import org.telegram.databaseService.requests.Status;
import org.telegram.databaseService.service.ChannelService;
import org.telegram.databaseService.service.ChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/getChatByChatId")
    public String getChatByChatId(@RequestHeader("chatId") String chatId) {
        try {
            try {
                return mapper.writeValueAsString(chatService.findChat(Long.parseLong(chatId)));
            } catch (NoSuchElementException e) {
                return null;
            }
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * <b>getAllChats</b> - возвращает все существующие чаты
     *
     * @return
     */
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/getAllChats")
    public String getAllChats() {
        try {
            return mapper.writeValueAsString(chatService.getAllChats());
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }

    /**
     * <b>getAllChatIdOfChats</b> - возвращает все ChatId существующих чатов
     *
     * @return List Long
     */

    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/getAllChatIdOfChats")
    public String getAllChatIdOfChats() {
        try {
            List<Chat> chats = chatService.getAllChats();
            return mapper.writeValueAsString(chats.stream().map(a -> a.getChatId()).toList());
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }

    /**
     * <b>saveChat</b> - сохраняет Chat в базу данных
     *
     * @return Https.status
     */
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/saveChat")
    public String saveChat(@RequestBody String chat) {
        try {
            chatService.saveChat(mapper.readValue(chat, Chat.class));
            return Status.SUCCESS.toString();
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }

    /**
     * <b>addChannelInChatByChatId</b> - Добавляет канал в чат с созданием всех связей
     *
     * @param chatId      chatId чата в который добавляется канал
     * @param bodyChannel объект добавляемого канала
     * @return Https.status
     */

    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/addChannelInChatByChatId")
    public String addChannelInChatByChatId(@RequestHeader("chatId") String chatId, @RequestBody String bodyChannel) {
        try {
            Chat chat = chatService.findChat(Long.parseLong(chatId));

            if (chat != null) {

                Channel channel = mapper.readValue(bodyChannel, Channel.class);

                Channel channelInBase = channelService.findChannel(channel.getChatId());
                if (channelInBase != null) {
                    channelInBase.addChat(chat);
                    channelInBase.setTitle(channel.getTitle());
                    channelInBase.setInviteLink(channel.getInviteLink());
                    channelService.saveChannel(channelInBase);
                    chat.addChannel(channelInBase);
                } else {
                    channel.setChats(new ArrayList<>());
                    channel.addChat(chat);

                    channelService.saveChannel(channel);
                    chat.addChannel(channel);
                }

                chatService.saveChat(chat);

                return Status.SUCCESS.toString();
            }
            return Status.FAIL.toString();
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }

    /**
     * <b>removeChannelFromChat</b> - удаляет канал и чата
     *
     * @param chatId        chatId чата
     * @param channelChatId chatId канала
     * @return Https.status
     */
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/removeChannelFromChat")
    public String removeChannelFromChat(@RequestHeader("chatId") String chatId, @RequestHeader("channelChatId") String channelChatId) {
        try {
            Chat chat = chatService.findChat(Long.parseLong(chatId));
            Channel channel = channelService.findChannel(Long.parseLong(channelChatId));

            if (chat != null && channel != null) {

                chat.deleteChannel(channel.getChatId());
                channel.removeChat(chat.getChatId());

                channelService.saveChannel(channel);
                chatService.saveChat(chat);

                return Status.SUCCESS.toString();
            }
            return Status.FAIL.toString();
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }

    /**
     * <b>getChatChannelsByChatId</b> - возвращает список всех каналов в чате по ChatId
     *
     * @param chatId chatId чата
     * @return List(Channel)
     */

    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/getChatChannelsByChatId")
    public String getChatChannelsByChatId(@RequestHeader("chatId") String chatId) {
        try {
            Chat chat = chatService.findChat(Long.parseLong(chatId));
            if (chat != null) {
                return mapper.writeValueAsString(chat.getChanelList());
            }
            return Status.FAIL.toString();
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }

    /**
     * <b>getChannelChatsByChatId</b> - возвращает список всех чатов из канала по ChatId
     *
     * @param chatId chatId канала
     * @return List(Chat)
     */
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/getChannelChatsByChatId")
    public String getChannelChatsByChannelId(@RequestHeader("chatId") String chatId) {
        try {
            Channel channel = channelService.findChannel(Long.parseLong(chatId));
            if (channel != null) {
                return mapper.writeValueAsString(channel.getChats());
            }
            return Status.FAIL.toString();
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }

    /**
     * <b>getChannelByInviteLink</b> - возвращает объект Channel по inviteLink
     *
     * @param inviteLink ссылка приглашение в канала
     * @return
     */
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/getChannelByInviteLink")
    public String getChannelByInviteLink(@RequestHeader("inviteLink") String inviteLink) {
        try {
            try {
                return mapper.writeValueAsString(channelService.findChannelByInviteLink(inviteLink));
            } catch (NoSuchElementException e) {
                return null;
            }
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
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/getChannelByChatId")
    public String getChannelByChatId(@RequestHeader String chatId) {
        try {
            return mapper.writeValueAsString(channelService.findChannel(Long.parseLong(chatId)));
        } catch (Exception e) {
            return e.toString();
        }
    }


    /**
     * <b>getChannelRating</b> - возвращает 5 наиболее популярных каналов
     *
     * @return List(Channel)
     */
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/getChannelRating")
    public String getChannelRating() {
        try {
            List<Channel> channels = channelService.getAllChannels();
            channels.sort((a, b) -> Integer.compare(b.getChats().size(), a.getChats().size()));

            if (channels.size() > 5) {
                return mapper.writeValueAsString(channels.subList(0, 5));
            } else {
                return mapper.writeValueAsString(channels);
            }
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }


    /**
     * <b>saveChannel</b> - сохраняет Channel
     *
     * @param channel
     * @return Https.status
     */
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/saveChannel")
    public String saveChannel(@RequestBody String channel) {
        try {
            Channel channelEntity = mapper.readValue(channel, Channel.class);
            channelService.saveChannel(channelEntity);
            return Status.SUCCESS.toString();
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }

    /**
     * <b>deleteChannel</b> - удаляет канал
     */
    @Logging(entering = true, exiting = true, returnData = true)
    @PostMapping("/deleteChannel")
    public String deleteChannel(@RequestHeader String chatId) {
        try {
            Channel channel = channelService.findChannel(Long.parseLong(chatId));
            channelService.deleteChannel(channel);
            return Status.SUCCESS.toString();
        } catch (Exception e) {
            return Status.FAIL.toString();
        }
    }

}
