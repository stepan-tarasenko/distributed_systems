package com.example1.loggingservice.loggingservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Msg {
    private UUID uuid = UUID.randomUUID();
    private String text;

    public Msg(String text) {
        this.text = text;
    }
}
