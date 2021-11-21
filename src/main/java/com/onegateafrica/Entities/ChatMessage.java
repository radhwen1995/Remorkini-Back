package com.onegateafrica.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private Timestamp dateCreation ;

    private String contenu ;

    private boolean isSeen ;

    @OneToOne(cascade = CascadeType.ALL)
    private Consommateur user ;

    @JsonIgnore
    @ManyToOne
    private ChatConversation conversation ;

    public static class DateCreationComparator implements Comparator<ChatMessage>
    {
        //    @Override
        public int compare(ChatMessage m1, ChatMessage m2) {
            return m1.getDateCreation().compareTo(m2.getDateCreation());
        }


    }
}
