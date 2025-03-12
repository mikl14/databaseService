package org.telegram.databaseService.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private Long chatId;

    @Setter
    @Getter
    @ManyToMany(mappedBy = "chats", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Channel> chanelList;

    public Chat() {
    }

    public Chat(Long chatId, List<Channel> chanelList) {
        this.chatId = chatId;
        this.chanelList = chanelList;
    }

    public void addChannel(Channel channel) {
        if (chanelList.stream().noneMatch(x -> x.getChatId().equals(channel.getChatId()))) {
            chanelList.add(channel);
        }
    }
    public void deleteChannel(Long channelId) {
        chanelList.removeIf(a -> a.getChatId().equals(channelId));
    }
}
