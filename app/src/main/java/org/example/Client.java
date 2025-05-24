package org.example;
import java.io.*;
import java.net.*;

public class Client{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String host;
    private String username;
    private int port;

    public Client(String h,int p, String u) throws IOException{
        this.host = h;
        this.port = p;
        this.socket = new Socket(this.host,this.port);
        this.out = new PrintWriter(socket.getOutputStream(),true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = u;
    }

    public String readMessage() throws IOException {
        StringBuilder msg = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            msg.append(line).append(System.lineSeparator());
        }
        return msg.toString();
    }

    public void sendMessage(String msg){
        out.println(msg);
    }

    public void close() throws IOException{
        socket.close();
        out.close();
        in.close();
    }

    public String getUsername(){
        return this.username;
    }
}
