import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Server {
    private static final int PORT = 49152;
    private static HashSet<String> names = new HashSet<>();
    //private static HashSet<PrintWriter> writers = new HashSet<>();
    private static ArrayList<ClientInfo> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);

        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private ClientInfo user;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("GET_NAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                String toSend = "";

                for (ClientInfo client: clients)
                    toSend += client.getName() + " ";

                out.println("NAME_OK " + clients.size() + " " + toSend);


                for (ClientInfo user : clients) {
                    user.getWriter().println("GLOBAL " + name + " has joined the chat room!");
                }

                user = new ClientInfo(name, out);
                clients.add(user);

                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }

                    if (input.startsWith("PRIVATE_MESSAGE")) {
                        String[] messages = input.split(" ");

                        // Inefficient solution
                        String message = "";
                        for (int i = 2; i < messages.length; i++) {
                            message += messages[i] + " ";
                        }


                        for (ClientInfo client : clients) {
                            if (client.getName().equalsIgnoreCase(messages[1])) {
                                client.getWriter().println("MESSAGE " + name + " [Private Message]: " + message);
                                break;
                            }
                        }

                        out.println("MESSAGE " + name + " [Private Message]: " + message);

                    } else if (input.startsWith("GET_NAME_CLIENTS")){
                        toSend = "";

                        for (ClientInfo client: clients)
                            toSend += client.getName() + " ";

                        out.println("NAME_CLIENTS " + toSend);

                    } else {
                        for (ClientInfo client : clients) {
                            client.getWriter().println("MESSAGE " + name + ": " + input);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    names.remove(name);
                    System.out.println(name + " is disconnected from the server.");

                    for (ClientInfo client : clients) {
                        client.getWriter().println("MESSAGE " + name + " has disconnected.");
                    }

                    for (ClientInfo client : clients){
                        System.out.println("Client: " + client.getName() + " removes " + name);
                        client.getWriter().println("DISCONNECT " + name);
                    }
                }
                if (out != null) {
                    clients.remove(user);
                }
                try {
                    socket.close();
                } catch (IOException e) {

                }
            }
        }
    }
}