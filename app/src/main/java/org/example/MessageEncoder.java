package org.example;

import com.google.gson.Gson;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message>{
    private static Gson gson = new Gson();

    public MessageEncoder(){

    }
    
    @Override
    public String encode(Message m) throws EncodeException{
        return gson.toJson(m);
    }

    @Override
    public void init(EndpointConfig ec){
        System.out.println("Encoder initialized at " + java.time.LocalDateTime.now());
    }

    @Override
    public void destroy(){
        System.out.println("Encoder destroyed at " + java.time.LocalDateTime.now());
    }
}