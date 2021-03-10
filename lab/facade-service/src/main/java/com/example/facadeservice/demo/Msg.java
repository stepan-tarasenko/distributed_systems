package com.example.facadeservice.demo;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Msg {
    private UUID uuid;
    private String text;

    public Msg(UUID uuid, String text) {
        this.uuid = uuid;
        this.text = text;
    }
}
