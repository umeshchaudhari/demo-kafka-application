package com.kafka.demokafka.dto;

public class EncryptedResponse {

    private String response;

    public EncryptedResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
