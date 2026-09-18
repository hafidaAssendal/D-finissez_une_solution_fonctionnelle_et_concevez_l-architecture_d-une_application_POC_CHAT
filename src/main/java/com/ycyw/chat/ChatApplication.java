package com.ycyw.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application PoC Chat.
 * 
 * Cette preuve de concept démontre la faisabilité de la fonctionnalité
 * de chat en temps réel entre un client et un agent du support,
 * conformément aux spécifications du Business Requirements V2
 * et de l'Architecture Definition Document.
 * 
 * Technologies : Spring Boot 3.2, Spring WebSocket, STOMP, SockJS.
 */
@SpringBootApplication
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}
