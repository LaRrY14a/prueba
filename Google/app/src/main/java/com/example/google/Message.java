package com.example.google;

public class Message {
    private String sender;
    private String message;
    private String timestamp;
    private String status;
    private boolean isRead;
    private String subject;  // El nuevo campo

    // Constructor con 6 parámetros (incluyendo 'subject')
    public Message(String sender, String message, String timestamp, String status, boolean isRead, String subject) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.isRead = isRead;
        this.subject = subject;  // Asignación del asunto
    }

    // Métodos getter
    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getSubject() {
        return subject;  // Getter para el asunto
    }
}




