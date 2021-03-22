package com.example1.loggingservice.loggingservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Msg {
    private UUID uuid = UUID.randomUUID();
    private String text;

    public Msg(String text) {
        this.text = text;
    }
}
