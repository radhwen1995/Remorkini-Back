package com.onegateafrica.Controllers;

import com.onegateafrica.Entities.ChatConversation;
import com.onegateafrica.Entities.ChatMessage;
import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.DemandeRemorquage;
import com.onegateafrica.Payloads.request.MessageDto;
import com.onegateafrica.Repositories.ChatConversationRepository;
import com.onegateafrica.Repositories.DemandeRemorquageRepository;
import com.onegateafrica.Service.ConsommateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
public class ChatController {


    private final ConsommateurService consommateurService;

    private final DemandeRemorquageRepository demandeRemorquageRepository ;
    private final ChatConversationRepository chatConversationRepository ;


    @Autowired
    public ChatController(ConsommateurService consommateurService, DemandeRemorquageRepository demandeRemorquageRepository, ChatConversationRepository chatConversationRepository) {
        this.consommateurService = consommateurService;
        this.demandeRemorquageRepository = demandeRemorquageRepository;
        this.chatConversationRepository = chatConversationRepository;
    }


    @GetMapping("/verifierMessagesNonVu/{idDemande}/{idUser}")
    public ResponseEntity<Object> verifierMessagesNonVu (@PathVariable Long idDemande ,@PathVariable Long idUser) {

        if(idDemande != null && idUser !=null){
            DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();

            long nbreMessagesNonVu =0 ;
            for(ChatMessage msg : demandeRemorquage.getChatConversation().getListeMessages()){
                if(msg.getUser().getId() != idUser){
                    if(!msg.isSeen()) nbreMessagesNonVu++;
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(nbreMessagesNonVu);

        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("idDemande ne peut pas étre null");
    }

    @GetMapping("/getconversation/{idDemandeRemorquage}/{idUser}")
    public ResponseEntity<Object> getConversation (@PathVariable Long idDemandeRemorquage ,@PathVariable Long idUser) {

        if(idDemandeRemorquage !=null && idUser !=null) {
            //try{
            DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemandeRemorquage).get();






                List<ChatMessage> listeMessage = demandeRemorquage.getChatConversation().getListeMessages();
            Collections.sort(listeMessage, new ChatMessage.DateCreationComparator());
            return ResponseEntity.status(HttpStatus.OK).body(listeMessage);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("idDemande ne peut pas étre null");
    }







    @PutMapping("/mettreAjourMessagesNonVu/{idDemande}/{idUser}")
    public ResponseEntity<Object> mettreAjourMessagesNonVu (@PathVariable Long idDemande ,@PathVariable Long idUser){
        if(idDemande !=null  && idUser !=null ){
            DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();
            //1) mettre tous les messages de l'autre partie comme vu
            if(demandeRemorquage.getChatConversation().getListeMessages().size()>0){
                for(ChatMessage msg : demandeRemorquage.getChatConversation().getListeMessages()){
                    if(msg.getUser().getId() != idUser) msg.setSeen(true);
                }
            }
            chatConversationRepository.save(demandeRemorquage.getChatConversation());
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur");
    }

    @PostMapping("/ajouterMessage")
    public ResponseEntity<Object> ajouterMessageAuConversation (@RequestBody MessageDto messageDto){
        if(messageDto.getIdUser() != null && messageDto.getIdDemande() !=null && messageDto.getContenuMessage() !=null){
            DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(messageDto.getIdDemande()).get();
            Consommateur consommateur = consommateurService.getConsommateur(messageDto.getIdUser()).get();

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContenu(messageDto.getContenuMessage());
            Instant now = Instant.now();
            Timestamp dateCreation = Timestamp.from(now);
            chatMessage.setDateCreation(dateCreation);

            chatMessage.setUser(consommateur);
            chatMessage.setConversation(demandeRemorquage.getChatConversation());
            demandeRemorquage.getChatConversation().getListeMessages().add(chatMessage);

            chatConversationRepository.save(demandeRemorquage.getChatConversation());

            return ResponseEntity.status(HttpStatus.OK).body(demandeRemorquage.getChatConversation().getListeMessages());

        }


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("vérifiez les données");
    }



}
