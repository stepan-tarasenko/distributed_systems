package com.example.facadeservice.demo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Text {
    private String text;

    public Text(String text) {
        this.text = text;
    }
}
