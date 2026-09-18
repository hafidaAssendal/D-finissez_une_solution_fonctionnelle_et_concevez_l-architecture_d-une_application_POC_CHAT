package com.ycyw.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration WebSocket avec STOMP et SockJS.
 * 
 * - Endpoint WebSocket : /ws (avec fallback SockJS pour compatibilité navigateurs)
 * - Préfixe des destinations applicatives : /app
 * - Préfixe du broker de messages : /topic (broadcast) et /queue (point-à-point)
 * 
 * Conforme à l'Architecture Definition Document :
 * - Protocole STOMP pour la messagerie
 * - SockJS pour la compatibilité navigateurs anciens
 * - Séparation des canaux (topic pour le broadcast, queue pour le privé)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Active le broker de messages en mémoire
        // /topic : messages diffusés à tous les abonnés d'une conversation
        // /queue : messages privés (point-à-point)
        config.enableSimpleBroker("/topic", "/queue");

        // Préfixe pour les messages envoyés par le client vers le serveur
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket avec fallback SockJS
        // Les clients se connectent via : ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
