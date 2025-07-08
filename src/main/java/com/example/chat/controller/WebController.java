package com.example.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }

    @GetMapping("/chat/{roomId}")
    public String chatRoom(@PathVariable String roomId) {
        return "chat";
    }
}
