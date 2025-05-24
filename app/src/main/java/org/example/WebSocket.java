package org.example;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jakarta.websocket.EncodeException;
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
    private Database db;
    private static ExecutorService executor = Executors.newCachedThreadPool();

    @OnOpen
    public void onOpen(Session s,@PathParam("username") String u) throws IOException, EncodeException, ClassNotFoundException{
        this.session = s;
        this.db = new Database();
        try{
            String query = "SELECT 1 FROM users where username = ? LIMIT 1";
            try(Connection conn = db.getConnection()){
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, u);
                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()){
                        System.out.println("User not found: " + u);
                        session.close();
                        return;
                    }
                }
            }
            catch(SQLException e){
                e.printStackTrace();
                session.close();
            }
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
            session.close();
        }
    }

    @OnMessage
    public void onMessage(Session s, Message m) throws IOException, EncodeException{
        if(client != null){
            String msg = m.getMessage();
            System.err.println(msg + " " + m.getTarget() + " " + m.getSender());
            Integer senderId = findUserIDfromUsername(m.getSender());
            Integer recipientID = findUserIDfromUsername(m.getTarget());
            if(senderId == null) {
                System.err.println(m.getSender() + " not found");
                return;
            }
            if(recipientID == null) {
                System.err.println(m.getTarget() + " not found");
                return;
            }
            client.sendMessage(msg);
            String query = "INSERT INTO messages (message, sender_id, recipient_id, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            try(Connection conn = db.getConnection()){
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, msg);
                ps.setInt(2, senderId);
                ps.setInt(3, recipientID);
                ps.executeUpdate();
            }
            catch(SQLException e){
                e.printStackTrace();
            }
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

    private Integer findUserIDfromUsername(String u){  
        String query = "SELECT user_id FROM users WHERE username = ?";
        try(Connection conn = db.getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, u);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt("user_id");
                }
            }
        }       
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}