import javax.net.ssl.HandshakeCompletedEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Server {
    private static final int PORT = 49152;
    private static HashSet<String> names = new HashSet<>();
    //private static HashSet<PrintWriter> writers = new HashSet<>();
    private static ArrayList<ClientInfo> clients = new ArrayList<>();
    //private static ArrayList<Handler> handlers = new ArrayList<Handler>();
    private static HashMap<String, ArrayList<ClientInfo>> groupChats = new HashMap<>();
    private static HashMap<String, ArrayList<ClientInfo>> chatrooms = new HashMap<>();
    private static HashMap<String, String> chatroomPasswords = new HashMap<>();
    private static int groupChatID = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);

        try {
            while (true) {
                /*handlers.add(new Handler(listener.accept()));
                handlers.get(handlers.size()-1).start();
                */

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
                    user.getWriter().println("MESSAGE " + name + " has joined the chat room!");
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

                    } else if (input.startsWith("GET_NAME_CLIENTS")) {
                        toSend = "";

                        for (ClientInfo client : clients)
                            toSend += client.getName() + " ";

                        out.println("NAME_CLIENTS " + toSend);

                    } else if (input.startsWith("GET_CHATROOMS")) {
                        toSend = "";

                        for (String key : chatrooms.keySet())
                            toSend += key + " ";

                        System.out.println(toSend);

                        out.println("CHATROOMS " + toSend);
                    } else if (input.startsWith("CREATE_GC")) {
                        String[] message = input.trim().split("\\s+");
                        ArrayList<ClientInfo> clientsList = new ArrayList<ClientInfo>();
                        String clientNames = "";

                        for (int i = 1; i < message.length; i++) {
                            for (ClientInfo client: clients) {
                                if (message[i].equalsIgnoreCase(client.getName())) {
                                    clientsList.add(client);
                                    break;
                                }
                            }
                        }

                        groupChats.put(Integer.toString(groupChatID), clientsList);

                        for (ClientInfo client: clientsList)
                            clientNames += client.getName() + " ";



                        // For testing purposes
                        out.println("TO_GC " + groupChatID + " " + clientNames);

                        ++groupChatID;
                    } else if (input.startsWith("ADD_GC")){
                        String[] message = input.trim().split("\\s+");
                        ArrayList<ClientInfo> groupChatUsers = groupChats.get(message[1]);
                        String users = "";
                        for (int i = 2; i < message.length; i++){
                            for (ClientInfo client : clients){
                                if (message[i].equalsIgnoreCase(client.getName())){
                                    groupChatUsers.add(client);
                                    break;
                                }
                            }
                        }
                        groupChats.put(message[1], groupChatUsers);

                        for (ClientInfo client: groupChatUsers)
                            users += client.getName() + " ";



                        // For testing purposes
                        out.println("TO_GC " + groupChatID + " " + groupChatUsers);

                    } else if (input.startsWith("GC_MES")){
                        String[] message = input.trim().split("\\s+");
                        ArrayList<ClientInfo> groupChatUsers = groupChats.get(message[1]);
                        String deliver = "";

                        for (int i = 2; i < message.length; i++)
                            deliver += message[i] + " ";

                        for (ClientInfo client: groupChatUsers)
                            client.getWriter().println("SEND_GC " + message[1] + " " + deliver);
                    } else if (input.startsWith("GET_NAMES_IN_GC")) {
                        String[] message = input.trim().split("\\s+");
                        String key = message[1];
                        ArrayList<ClientInfo> groupChatUsers = groupChats.get(key);
                        String names = "";

                        for (ClientInfo c: groupChatUsers)
                            names += c.getName() + " ";

                        out.println("NAMES_IN_GC " + message[1] + " " + names);
                        System.out.println(names);
                    } else if (input.startsWith("CREATE_CHATROOM")) {
                        String[] message = input.split("\\s+");
                        String roomName = message[1];
                        ArrayList<ClientInfo> clientsList = chatrooms.get(roomName);


                        // if not null ( later na yung hindi mag-aadd )
                        if (clientsList == null) {
                            clientsList = new ArrayList<ClientInfo>();

                            for (ClientInfo client : clients) {
                                if (client.getName().equalsIgnoreCase(message[3])) {
                                    clientsList.add(client);
                                    System.out.println(message[2] + " is added to a chatroom.");
                                    break;
                                }
                            }

                            chatrooms.put(roomName, clientsList);
                            chatroomPasswords.put(roomName, message[2]);

                            out.println("CR_MESSAGE " + roomName + " " + name + " has created the " + roomName + " chatroom.");
                        }

                    } else if (input.startsWith("JOIN_CHATROOM")) {
                        String[] message = input.trim().split("\\s+");
                        ArrayList<ClientInfo> curr = chatrooms.get(message[1]);

                        String passwordCompare = chatroomPasswords.get(message[1]);

                        if (passwordCompare.equals(message[2])) {
                            for (ClientInfo client : clients) {
                                if (client.getName().equalsIgnoreCase(message[3])) {
                                    curr.add(client);
                                    System.out.println(message[2] + " has joined the chatroom.");
                                    break;
                                }
                            }

                            for (ClientInfo client : curr) {
                                client.getWriter().println("JOIN_CR_MESSAGE " + message[1] + " " + message[3] + " has joined the chatroom.");
                            }
                        }
                    } else if (input.startsWith("TO_CR")) {
                        String[] messages = input.trim().split("\\s+");
                        ArrayList<ClientInfo> curr = chatrooms.get(messages[1]);

                        String message = "";

                        for (int i = 2; i < messages.length; i++) {
                            message += messages[i] + " ";
                        }

                        for (ClientInfo client : curr) {
                            if (client.getWriter() != null)
                                client.getWriter().println("CR_MESSAGE " + messages[1] + " " + message);
                        }
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