# Your Car Your Way — PoC Chat en temps réel

## Description

Preuve de concept (PoC) de la fonctionnalité de chat en temps réel entre un client et un agent du support, développée dans le cadre du projet Your Car Your Way.

Ce PoC démontre la faisabilité technique de la communication synchrone via WebSocket, conformément aux spécifications définies dans :
- **Business Requirements V2** — Section « Communication avec le support client »
- **Architecture Definition Document** — Module Chat/Support (Spring WebSocket, STOMP, SockJS)

## Stack technique

| Technologie        | Version | Rôle                                      |
|--------------------|---------|-------------------------------------------|
| Java               | 17 LTS  | Langage backend                           |
| Spring Boot        | 3.2.5   | Framework backend                         |
| Spring WebSocket   | 6.1     | Communication WebSocket                   |
| STOMP              | —       | Protocole de messagerie sur WebSocket     |
| SockJS             | 1.x     | Fallback pour navigateurs non-compatibles |
| Maven              | 3.9+    | Build et gestion de dépendances           |
| JUnit 5            | 5.10    | Tests unitaires                           |

## Architecture du PoC

```
poc-chat/
├── pom.xml                                    # Dépendances Maven
├── README.md                                  # Ce fichier
└── src/
    ├── main/
    │   ├── java/com/ycyw/chat/
    │   │   ├── ChatApplication.java           # Point d'entrée Spring Boot
    │   │   ├── config/
    │   │   │   └── WebSocketConfig.java       # Configuration STOMP + SockJS
    │   │   ├── controller/
    │   │   │   └── ChatController.java        # Contrôleur WebSocket
    │   │   └── model/
    │   │       └── ChatMessage.java           # Modèle de message
    │   └── resources/
    │       ├── application.properties         # Configuration Spring Boot
    │       └── static/
    │           └── index.html                 # Interface de test (HTML/JS)
    └── test/
        └── java/com/ycyw/chat/
            └── ChatApplicationTests.java      # Tests JUnit 5
```

## Prérequis

- **Java 17** (JDK 17 ou supérieur)
- **Maven 3.9+**

## Installation et lancement

### 1. Cloner le repository

```bash
git clone <url-du-repository>
cd poc-chat
```

### 2. Compiler et lancer les tests

```bash
mvn clean test
```

### 3. Démarrer l'application

```bash
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`.

### 4. Tester le chat

1. Ouvrir **deux onglets** de navigateur sur `http://localhost:8080`
2. Dans le premier onglet : entrer un nom (ex: "John"), sélectionner **Client**, et cliquer sur "Démarrer le chat"
3. Dans le deuxième onglet : entrer un nom (ex: "Sophie"), sélectionner **Agent support**, et cliquer sur "Démarrer le chat"
4. Envoyer des messages depuis les deux onglets — ils s'affichent en temps réel

## Fonctionnalités démontrées

- ✅ Connexion WebSocket via SockJS (fallback HTTP si WebSocket non supporté)
- ✅ Protocole STOMP pour la gestion des messages
- ✅ Conversation identifiée par un `conversationId` unique
- ✅ Distinction entre les rôles USER (client) et AGENT (support)
- ✅ Messages en temps réel bidirectionnels
- ✅ Notifications de connexion/déconnexion (JOIN/LEAVE)
- ✅ Horodatage des messages

## Conformité avec l'Architecture Definition Document

| Spécification ADD                  | Implémentation PoC                        |
|------------------------------------|-------------------------------------------|
| Spring WebSocket                   | ✅ `spring-boot-starter-websocket`        |
| Protocole STOMP                    | ✅ `@MessageMapping`, broker `/topic`     |
| SockJS (compatibilité)            | ✅ `.withSockJS()` dans WebSocketConfig   |
| ChatMessage {USER, AGENT}          | ✅ Enum `SenderType` dans le modèle       |
| ChatConversation (conversationId)  | ✅ Topic par conversation `/topic/conversation/{id}` |
| Spring Boot 3.2 / Java 17         | ✅ Versions conformes dans pom.xml        |

## Limitations du PoC

Ce PoC se concentre uniquement sur la faisabilité technique du chat en temps réel. Les éléments suivants ne sont pas implémentés (car hors périmètre du PoC) :

- Authentification (JWT / Spring Security)
- Persistance des messages (PostgreSQL)
- Historique des conversations
- Visioconférence
- Interface Angular (le frontend est un simple HTML/JS de test)
