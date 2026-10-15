package com.kafka.demokafka.dto;

public class EncryptedResponse {

    private String data;

    public EncryptedResponse(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
