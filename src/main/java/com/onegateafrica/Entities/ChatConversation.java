package com.onegateafrica.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private Timestamp dateCreation ;

    @OneToMany(mappedBy = "conversation",cascade = CascadeType.ALL)
    private List<ChatMessage> listeMessages ;

    @JsonIgnore
    @OneToOne(mappedBy = "chatConversation",cascade = CascadeType.ALL)
    private DemandeRemorquage demandeRemorquage ;
}
