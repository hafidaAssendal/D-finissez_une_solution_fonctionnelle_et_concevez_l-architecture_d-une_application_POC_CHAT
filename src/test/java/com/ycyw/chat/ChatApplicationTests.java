package com.ycyw.chat;

import com.ycyw.chat.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le PoC Chat.
 * 
 * Vérifie le bon fonctionnement du modèle ChatMessage
 * et le démarrage du contexte Spring Boot avec WebSocket.
 */
@SpringBootTest
class ChatApplicationTests {

    @Test
    @DisplayName("Le contexte Spring Boot démarre correctement avec WebSocket")
    void contextLoads() {
        // Ce test vérifie que l'application démarre sans erreur,
        // y compris la configuration WebSocket (STOMP + SockJS)
    }

    @Test
    @DisplayName("ChatMessage est créé avec les bons attributs")
    void chatMessageCreation() {
        ChatMessage msg = new ChatMessage(
            ChatMessage.MessageType.CHAT,
            ChatMessage.SenderType.USER,
            "John Doe",
            "Bonjour, j'ai un problème avec ma réservation.",
            "conv-001"
        );

        assertEquals(ChatMessage.MessageType.CHAT, msg.getType());
        assertEquals(ChatMessage.SenderType.USER, msg.getSenderType());
        assertEquals("John Doe", msg.getSender());
        assertEquals("Bonjour, j'ai un problème avec ma réservation.", msg.getContent());
        assertEquals("conv-001", msg.getConversationId());
        assertNotNull(msg.getTimestamp());
    }

    @Test
    @DisplayName("ChatMessage de type JOIN génère le bon contenu")
    void chatMessageJoin() {
        ChatMessage msg = new ChatMessage();
        msg.setType(ChatMessage.MessageType.JOIN);
        msg.setSenderType(ChatMessage.SenderType.AGENT);
        msg.setSender("Agent Sophie");
        msg.setConversationId("conv-001");
        msg.setContent("Agent Sophie a rejoint la conversation.");

        assertEquals(ChatMessage.MessageType.JOIN, msg.getType());
        assertEquals(ChatMessage.SenderType.AGENT, msg.getSenderType());
        assertTrue(msg.getContent().contains("a rejoint la conversation"));
    }

    @Test
    @DisplayName("Les enums SenderType correspondent au diagramme de classes")
    void senderTypeEnum() {
        // Conforme au diagramme de classes : sender_type {USER, AGENT}
        assertEquals(2, ChatMessage.SenderType.values().length);
        assertNotNull(ChatMessage.SenderType.valueOf("USER"));
        assertNotNull(ChatMessage.SenderType.valueOf("AGENT"));
    }

    @Test
    @DisplayName("Les enums MessageType couvrent les 3 cas")
    void messageTypeEnum() {
        assertEquals(3, ChatMessage.MessageType.values().length);
        assertNotNull(ChatMessage.MessageType.valueOf("CHAT"));
        assertNotNull(ChatMessage.MessageType.valueOf("JOIN"));
        assertNotNull(ChatMessage.MessageType.valueOf("LEAVE"));
    }
}
