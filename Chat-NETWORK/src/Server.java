import java.io.*;
import java.net.InetAddress;
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

        //Change the localhost into an IP address of your computer in the network if it will be the server.
        InetAddress addr = InetAddress.getByName("localhost");
        ServerSocket listener = new ServerSocket(PORT, 50, addr);

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
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private ClientInfo user;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public synchronized void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    out.writeObject("GET_NAME");
                    out.flush();

                    try {
                        name = (String) in.readObject();
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }

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

                out.writeObject("NAME_OK " + clients.size() + " " + toSend);
                out.flush();

                for (ClientInfo user : clients) {
                    user.getWriter().writeObject("MESSAGE " + name + " has joined the chat room!");
                    user.getWriter().flush();
                }

                user = new ClientInfo(name, out);
                clients.add(user);

                while (true) {
                    String input = "";
                    Object inz;
                    try {
                        inz = in.readObject();
                        if (inz instanceof String){
                            input = (String) inz;
                            if (input == null) {
                                return;
                            }

                            if (input.startsWith("PRIVATE_MESSAGE")) {
                                String[] messages = input.split("\\s+");

                                // Inefficient solution
                                String message = "";
                                for (int i = 3; i < messages.length; i++) {
                                    message += messages[i] + " ";
                                }

                                for (ClientInfo client : clients) {
                                    if (client.getName().equalsIgnoreCase(messages[1])) {
                                        client.getWriter().writeObject("SEND_PM " + messages[2] + " " + client.getName() + " " + name + ": " + message);
                                        break;
                                    }
                                }

                                out.writeObject("SEND_PM " + messages[1] + " " + messages[2] + " " + name + ": " + message);
                                out.flush();
                            } else if (input.startsWith("GET_NAME_CLIENTS")) {
                                toSend = "";

                                for (ClientInfo client : clients)
                                    toSend += client.getName() + " ";

                                out.writeObject("NAME_CLIENTS " + toSend);
                                out.flush();

                            } else if (input.startsWith("GET_CHATROOMS")) {
                                toSend = "";

                                for (String key : chatrooms.keySet())
                                    toSend += key + " ";

                                System.out.println(toSend);

                                out.writeObject("CHATROOMS " + toSend);
                                out.flush();
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
                                out.writeObject("TO_GC " + groupChatID + " " + clientNames);
                                out.flush();

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
                                out.writeObject("TO_GC " + groupChatID + " " + groupChatUsers);
                                out.flush();

                            } else if (input.startsWith("GC_MES")){
                                String[] message = input.trim().split("\\s+");
                                ArrayList<ClientInfo> groupChatUsers = groupChats.get(message[1]);
                                String deliver = "";

                                for (int i = 2; i < message.length; i++)
                                    deliver += message[i] + " ";

                                for (ClientInfo client: groupChatUsers) {
                                    client.getWriter().writeObject("SEND_GC " + message[1] + " " + deliver);
                                    client.getWriter().flush();
                                }

                            } else if (input.startsWith("GET_NAMES_IN_GC")) {
                                String[] message = input.trim().split("\\s+");
                                String key = message[1];
                                ArrayList<ClientInfo> groupChatUsers = groupChats.get(key);
                                String names = "";

                                for (ClientInfo c: groupChatUsers)
                                    names += c.getName() + " ";

                                out.writeObject("NAMES_IN_GC " + message[1] + " " + names);
                                out.flush();
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

                                    out.writeObject("CR_MESSAGE " + roomName + " " + name + " has created the " + roomName + " chatroom.");
                                    out.flush();
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
                                        client.getWriter().writeObject("JOIN_CR_MESSAGE " + message[1] + " " + message[3] + " has joined the chatroom.");
                                        client.getWriter().flush();
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
                                    if (client.getWriter() != null) {
                                        client.getWriter().writeObject("CR_MESSAGE " + messages[1] + " " + message);
                                        client.getWriter().flush();
                                    }
                                }
                            } else {
                                for (ClientInfo client : clients) {
                                    client.getWriter().writeObject("MESSAGE " + name + ": " + input);
                                    client.getWriter().flush();
                                }
                            }
                        }
                        else{

                        }
                    }catch(ClassNotFoundException ex){
                        ex.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    names.remove(name);
                    System.out.println(name + " is disconnected from the server.");

                    for (ClientInfo client : clients) {
                        if (clients.size() > 1) {
                            try {
                                client.getWriter().writeObject("MESSAGE " + name + " has disconnected.");
//                              client.getWriter().flush();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    for (ClientInfo client : clients){
                        try {
                            client.getWriter().writeObject("DISCONNECT " + name);
//                            client.getWriter().flush();
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
                if (out != null) {
                    clients.remove(user);
                }
                try {
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException e) {

                }
            }
        }
    }
}