package org.example;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chat/{username}", encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
public class WebSocket{
    private Session session;
    private Client client;
    private static ExecutorService executor = Executors.newCachedThreadPool();

    @OnOpen
    public void onOpen(Session s,@PathParam("username") String u) throws IOException, EncodeException{
        this.session = s;
        try{
            this.client = new Client("localhost", 5000, u);
            client.sendMessage(u);
            executor.submit(() -> {
                try{
                    String msg;
                    while((msg = client.readMessage()) != null){
                        session.getBasicRemote().sendText(msg);
                    }
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            });
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(Session s, Message m) throws IOException, EncodeException{
        if(client != null){
            client.sendMessage(m.getMessage());
        }
    }

    @OnClose
    public void onClose(Session s) throws IOException, EncodeException{
        if(client != null){
            client.close();
        }
    }

    @OnError
    public void onError(Session s, Throwable t){
        t.printStackTrace();
    }

    public static void main(String[] args) {
        System.out.println("WebSocket endpoint class loaded. Deploy this class in a Jakarta EE compatible server to use.");
    }
}