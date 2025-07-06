package org.telegram.databaseService.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
public class Channel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Setter
    @Getter
    private Long chatId;

    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private String inviteLink;

    @Setter
    @Getter
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_channel",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id")
    )
    private List<Chat> chats;

    public Channel() {
    }

    public Channel(Long chatId, String title, List<Chat> chats) {
        this.chatId = chatId;
        this.title = title;
        this.chats = chats;
    }

    public Channel(Long chatId, String title, String inviteLink, List<Chat> chats) {
        this.chatId = chatId;
        this.title = title;
        this.inviteLink = inviteLink;
        this.chats = chats;
    }

    public void addChat(Chat chat) {
        if (chats.stream().noneMatch(x -> x.getChatId().equals(chat.getChatId()))) {
            chats.add(chat);
        }
    }

    public void removeChat(Long chatId) {
        chats.removeIf(a -> a.getChatId().equals(chatId));
    }
}
