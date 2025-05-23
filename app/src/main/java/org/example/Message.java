package org.example;

public class Message{
    private String sender;
    // private String target;
    private String msg;

    public Message(){

    }

    public Message(String s,String m){
        this.sender = s;
        // this.target = t;
        this.msg = m;
    }

    public void setSender(String s){
        this.sender = s;
    }

    public String getSender(){
        return this.sender;
    }

    // public String getTarget(){
    //     return this.target;
    // }

    public String getMessage(){
        return this.msg;
    }
}