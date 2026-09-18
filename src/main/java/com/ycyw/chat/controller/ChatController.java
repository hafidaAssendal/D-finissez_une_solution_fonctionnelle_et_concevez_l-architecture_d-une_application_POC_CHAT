package com.ycyw.chat.controller;

import com.ycyw.chat.model.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Contrôleur WebSocket pour le chat en temps réel.
 * 
 * Gère les messages STOMP envoyés par les clients (USER et AGENT).
 * Chaque conversation est identifiée par un conversationId unique.
 * 
 * Flux de communication :
 * 1. Le client envoie un message vers /app/chat.send/{conversationId}
 * 2. Le serveur le reçoit, ajoute le timestamp
 * 3. Le serveur le diffuse vers /topic/conversation/{conversationId}
 * 4. Tous les abonnés de cette conversation reçoivent le message
 * 
 * Conforme au diagramme de composants : module Chat/Support,
 * utilisant Spring WebSocket, STOMP, SockJS.
 */
@Controller
public class ChatController {

    /**
     * Réception et diffusion d'un message de chat.
     * 
     * @param conversationId identifiant unique de la conversation
     * @param message le message envoyé par le client ou l'agent
     * @return le message enrichi, diffusé à tous les abonnés de la conversation
     */
    @MessageMapping("/chat.send/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public ChatMessage sendMessage(
            @DestinationVariable String conversationId,
            @Payload ChatMessage message) {
        
        message.setConversationId(conversationId);
        // Le timestamp est déjà défini dans le constructeur de ChatMessage
        return message;
    }

    /**
     * Gestion de l'événement "un utilisateur rejoint la conversation".
     * 
     * @param conversationId identifiant de la conversation
     * @param message le message de type JOIN
     * @return notification de connexion diffusée à tous les abonnés
     */
    @MessageMapping("/chat.join/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public ChatMessage joinConversation(
            @DestinationVariable String conversationId,
            @Payload ChatMessage message) {
        
        message.setType(ChatMessage.MessageType.JOIN);
        message.setConversationId(conversationId);
        message.setContent(message.getSender() + " a rejoint la conversation.");
        return message;
    }

    /**
     * Gestion de l'événement "un utilisateur quitte la conversation".
     * 
     * @param conversationId identifiant de la conversation
     * @param message le message de type LEAVE
     * @return notification de déconnexion diffusée à tous les abonnés
     */
    @MessageMapping("/chat.leave/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public ChatMessage leaveConversation(
            @DestinationVariable String conversationId,
            @Payload ChatMessage message) {
        
        message.setType(ChatMessage.MessageType.LEAVE);
        message.setConversationId(conversationId);
        message.setContent(message.getSender() + " a quitté la conversation.");
        return message;
    }
}
