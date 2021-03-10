package com.example2.messagesservice.messagesservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagesServiceController {
    @GetMapping("/messages")
    @ResponseBody
    public String message(){
        return "Hello, World from MessagesService!";
    }
}
