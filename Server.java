import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class Server{
    private List<User> clients;
    private ServerSocket server;
    private int port;
    
    public static void main(String[] args) throws IOException{
        new Server(5000).run();
    }

    public Server(int p){
        this.port = p;
        this.clients = new java.util.ArrayList<User>();
    }

    public void run() throws IOException{
        this.server = new ServerSocket(port);

        System.out.println("Server is running on port " + port + " and is waiting for connections");
        new Thread(() -> {
            String msg;
            InputStreamReader in = new InputStreamReader(System.in);
            try(BufferedReader s = new BufferedReader(in)){
                while((msg = s.readLine()) != null){
                    msg.trim();
                    if(msg.isEmpty()) continue;
                    if(msg.charAt(0)=='@'){
                        int space = msg.indexOf(" ");
                        if(space!=-1){
                            String target = msg.substring(1, space);
                            sendMsg(msg.substring(space+1), target);
                        }
                    }
                    else{
                        broadcastMsg(msg);
                    }
                }
            } 
            catch (IOException e){
                e.printStackTrace();
            }
            finally{
                try{
                    in.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

        while(true){
            Socket clientSocket = this.server.accept();
            InputStreamReader in = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader s = new BufferedReader(in);
            String clientName = s.readLine();
            System.out.println("New client connected: " +   clientName + "\nHost: " + clientSocket.getInetAddress().getHostAddress());
            User client = new User(clientSocket, clientName);
            clients.add(client);
            new Thread(new ClientHandler(this, client)).start();
        }
    }

    public void broadcastMsg(String msg){
        for(User client:this.clients){
            client.getOutStream().println("Public: " + msg);
        }
    }

    public void sendMsg(String msg, String target){
        for(User client:this.clients){
            if(client.getName().equals(target)){
                System.out.println("To: " + target + "\n" + msg);
                client.getOutStream().println("From: Server\n" + msg);
                return;
            }
        }
        System.out.println("Recipient didn't exist");   
    }

    public void sendMsg(String msg,User sender,String target){
        for(User client:this.clients){
            if(client.getName().equals(target)){
                sender.getOutStream().println("To: " + target + "\n" + msg);
                client.getOutStream().println("From: " + sender.getName() + "\n" + msg);
                return;
            }
        }
        sender.getOutStream().println("Recipient didn't exist");
    }

    public void removeClient(User c){
        this.clients.remove(c);
    }
}

class ClientHandler implements Runnable{
    private Server server;
    private User client;

    public ClientHandler(Server s,User c){
        this.server = s;
        this.client = c;
    }

    public void run(){
        String msg;
        try(BufferedReader s = new BufferedReader(new InputStreamReader(this.client.getInStream()))){
            while((msg = s.readLine()) != null){
                msg.trim();
                if(msg.isEmpty()) continue;
                if(msg.charAt(0)=='@'){
                    int space = msg.indexOf(" ");
                    if(space!=-1){
                        String target = msg.substring(1, space);
                        server.sendMsg(msg.substring(space+1), client, target);
                    }
                }
                else{
                    this.server.broadcastMsg(msg);
                }
            }
        } 
        catch (IOException e){
            e.printStackTrace();
        }
        finally{
            try{
                this.client.getSocket().close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            this.server.removeClient(client);
        }
    }
}

class User{
    private Socket userSocket;
    private PrintStream out;
    private InputStream in;
    private String clientName;
    private int clientID;
    private static int clientCount=0;

    public User(Socket socket,String name) throws IOException{
        this.userSocket = socket;
        this.out = new PrintStream(socket.getOutputStream());
        this.in = socket.getInputStream();
        this.clientName = name;
        this.clientID = clientCount;
        clientCount++;
    }

    public PrintStream getOutStream(){
        return this.out;
    }

    public InputStream getInStream(){
        return this.in;
    }

    public Socket getSocket(){
        return this.userSocket;
    }

    public String getName(){
        return this.clientName;
    }
}