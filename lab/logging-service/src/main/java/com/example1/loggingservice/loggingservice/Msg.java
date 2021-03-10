package com.example1.loggingservice.loggingservice;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Msg {
    private UUID uuid = UUID.randomUUID();
    private String text;

    public Msg(String text) {
        this.text = text;
    }
}
