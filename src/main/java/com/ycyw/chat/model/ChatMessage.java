package com.ycyw.chat.model;

import java.time.LocalDateTime;

/**
 * Modèle représentant un message dans le chat.
 * 
 * Correspond à l'entité ChatMessage du diagramme de classes UML :
 * - senderType : USER ou AGENT
 * - content : contenu textuel du message
 * - conversationId : identifiant de la conversation
 * - timestamp : date et heure d'envoi
 * 
 * Le type JOIN/LEAVE permet de gérer les événements de connexion/déconnexion.
 */
public class ChatMessage {

    public enum MessageType {
        CHAT,   // Message normal
        JOIN,   // Un utilisateur rejoint la conversation
        LEAVE   // Un utilisateur quitte la conversation
    }

    public enum SenderType {
        USER,   // Client Your Car Your Way
        AGENT   // Agent du support
    }

    private MessageType type;
    private SenderType senderType;
    private String sender;
    private String content;
    private String conversationId;
    private LocalDateTime timestamp;

    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage(MessageType type, SenderType senderType, String sender, 
                       String content, String conversationId) {
        this.type = type;
        this.senderType = senderType;
        this.sender = sender;
        this.content = content;
        this.conversationId = conversationId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters et Setters

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
