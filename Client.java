import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private String host;
    private int port;
    public static void main(String[] args) throws IOException {
        new Client("localhost", 5000).run();
    }

    public Client(String h,int p){
        this.host = h;
        this.port = p;
    }

    public void run() throws IOException{
        Socket socket = new Socket(host,port);
        System.out.println("Connected to the chat server");
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(() -> {
            try{
                String res;
                while((res=in.readLine())!=null){
                    System.out.println(res);
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        String userIn;
        while(scanner.hasNextLine()){
            userIn = scanner.nextLine();
            out.println(userIn);
        }

        scanner.close();
        out.close();
        in.close();
        socket.close();
    }
}
