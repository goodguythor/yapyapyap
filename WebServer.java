import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WebServer{
    private int port;
    private HttpServer server;
    public static void main(String[] args) throws IOException{
        new WebServer(8080).run();
    }       

    public WebServer(int p){
        this.port = p;
    }

    public void run() throws IOException{
        this.server = HttpServer.create(new InetSocketAddress(port),0);
        server.createContext("/",new Handler());
        server.start();
        System.out.println("Server has started on port " + this.port);
    }
}

class Handler implements HttpHandler{
    @Override
    public void handle(HttpExchange ex) throws IOException{
        String uri = ex.getRequestURI().getPath();
        if(uri.equals("/")){
            uri = "index.html";
        }

        File file = new File(uri);
        if(!file.exists()){
            String missing = "404 Not Found\n";
            ex.sendResponseHeaders(404, missing.length());
            OutputStream os = ex.getResponseBody();
            os.write(missing.getBytes());
            os.close();
            return;
        }
        String mime = Files.probeContentType(Paths.get(file.getAbsolutePath()));
        ex.getResponseHeaders().set("Content-Type", mime);
        byte[] bytes = Files.readAllBytes(file.toPath());
        ex.sendResponseHeaders(200, bytes.length);
        OutputStream os = ex.getResponseBody();
        os.write(bytes);
        os.close();
    }
}