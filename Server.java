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

        System.out.println("Server is running on port " + port + "and is waiting for connections");
        new Thread(() -> {
            Scanner s = new Scanner(System.in);
            while(s.hasNextLine()){
                String msg = s.nextLine();
                broadcastMsg(msg);
            }
            s.close();
        }).start();

        while(true){
            Socket clientSocket = this.server.accept();
            BufferedReader s = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientName = s.readLine();
            System.out.println("New client connected: " +   clientName + "\nHost: " + clientSocket.getInetAddress().getHostAddress());
            User client = new User(clientSocket, clientName);
            clients.add(client);
            new Thread(new ClientHandler(this, client)).start();
        }
    }

    public void broadcastClient(){
        for(User client:this.clients){
            client.getOutStream().println(this.clients);
        }
    }

    public void broadcastMsg(String msg){
        for(User client:this.clients){
            client.getOutStream().println(msg);
        }
    }

    public void sendMsg(String msg,User sender,String target){
        for(User client:this.clients){
            if(client.getName().equals(target)){
                sender.getOutStream().println(msg);
                client.getOutStream().println(msg);
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
                this.server.broadcastMsg(msg);
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
            this.server.broadcastClient();
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