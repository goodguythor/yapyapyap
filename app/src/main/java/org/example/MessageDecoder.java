package org.example;

import com.google.gson.Gson;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message>{
    private static Gson gson = new Gson();

    public MessageDecoder(){

    }

    @Override
    public Message decode(String s) throws DecodeException{
        return gson.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s){
        return s!=null;
    }

    @Override
    public void init(EndpointConfig ec){
        System.out.println("Decoder initialized at " + java.time.LocalDateTime.now());
    }

    @Override
    public void destroy(){
        System.out.println("Decoder destroyed at " + java.time.LocalDateTime.now());
    }
}