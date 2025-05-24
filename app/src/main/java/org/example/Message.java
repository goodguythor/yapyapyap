package org.example;

public class Message{
    private String sender;
    private String target;
    private String msg;

    public Message(){

    }

    public Message(String s,String m,String t){
        this.sender = s;
        this.target = t;
        this.msg = m;
    }

    public String getSender(){
        return this.sender;
    }

    public String getTarget(){
        return this.target;
    }

    public String getMessage(){
        return this.msg;
    }
}