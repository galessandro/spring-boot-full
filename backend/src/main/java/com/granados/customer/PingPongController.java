package com.granados.customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingPongController {

    record PingPong(String result){}

    @GetMapping("/ping")
    public PingPong getPingPong(){
        return new PingPong("Pong");
    }

    @GetMapping("/pong")
    public PingPong getPongPing(){
        return new PingPong("Ping");
    }

    @GetMapping("/pung")
    public PingPong getPongPung(){
        return new PingPong("Pung");
    }
}