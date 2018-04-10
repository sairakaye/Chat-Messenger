import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Server {
    private static final int PORT = 49152;
    private static final HashSet<String> names = new HashSet<>();
    //private static HashSet<PrintWriter> writers = new HashSet<>();
    private static ArrayList<ClientInfo> clients = new ArrayList<>();
    //private static ArrayList<Handler> handlers = new ArrayList<Handler>();
    private static HashMap<String, ArrayList<ClientInfo>> groupChats = new HashMap<>();
    private static HashMap<String, ArrayList<ClientInfo>> chatrooms = new HashMap<>();
    private static HashMap<String, String> chatroomPasswords = new HashMap<>();
    private static ArrayList<FileToTransfer> files = new ArrayList<>();
    private static int groupChatID = 0;
    private ServerSocket listener;
    private static boolean running;
    private static JTextArea serverLog;

    Server(JTextArea serverLog){
        Server.serverLog = serverLog;
    }

    public void startServer(String name){
        try {
            InetAddress addr = InetAddress.getByName(name);
            listener = new ServerSocket(PORT, 50, addr);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        running = true;
        serverLog.append("The server is running\n");

        new Thread(() -> {
            while (running){
                try {
                    Socket client = listener.accept();
                    new Handler(client).start();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void stopServer(){
        running = false;
        serverLog.append("The server is stopped\n");
        try {
            listener.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.exit(1);
    }

    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private ClientInfo user;

        Handler(Socket socket) {
            this.socket = socket;
        }

        public synchronized void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                while (running) {
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

                StringBuilder toSend = new StringBuilder();

                for (ClientInfo client: clients)
                    toSend.append(client.getName()).append(" ");

                System.out.println(toSend);

                out.writeObject("NAME_OK " + toSend);
                out.flush();
                serverLog.append(name + " has connected to the server\n");

                for (ClientInfo user : clients) {
                    user.getWriter().writeObject("MESSAGE " + name + " has joined the chat room!");
                    user.getWriter().flush();
                }

                user = new ClientInfo(name, out);
                clients.add(user);

                while (running) {
                    String input;
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
                                StringBuilder message = new StringBuilder();
                                for (int i = 3; i < messages.length; i++) {
                                    message.append(messages[i]).append(" ");
                                }

                                for (ClientInfo client : clients) {
                                    if (client.getName().equalsIgnoreCase(messages[1])) {
                                        client.getWriter().writeObject("SEND_PM " + messages[2] + " " + client.getName() + " " + name + ": " + message);
                                        serverLog.append(name + " is sending a private message to " + client.getName());
                                        break;
                                    }
                                }

                                out.writeObject("SEND_PM " + messages[1] + " " + messages[2] + " " + name + ": " + message);
                                out.flush();
                            } else if (input.startsWith("GET_NAME_CLIENTS")) {
                                serverLog.append("Someone is sending a message to the chatroom \n");
                                toSend = new StringBuilder();

                                for (ClientInfo client : clients)
                                    toSend.append(client.getName()).append(" ");

                                out.writeObject("NAME_CLIENTS " + toSend);
                                out.flush();

                            } else if (input.startsWith("GET_CHATROOMS")) {
                                toSend = new StringBuilder();

                                for (String key : chatrooms.keySet())
                                    toSend.append(key).append(" ");

                                System.out.println(toSend);

                                for (ClientInfo user : clients) {
                                    user.getWriter().writeObject("CHATROOMS " + toSend);
                                    user.getWriter().flush();
                                }
                                /*out.writeObject("CHATROOMS " + toSend);
                                out.flush();*/
                            } else if (input.startsWith("CREATE_GC")) {
                                String[] message = input.trim().split("\\s+");
                                ArrayList<ClientInfo> clientsList = new ArrayList<ClientInfo>();
                                StringBuilder clientNames = new StringBuilder();

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
                                    clientNames.append(client.getName()).append(" ");



                                // For testing purposes
                                out.writeObject("TO_GC " + groupChatID + " " + clientNames);
                                out.flush();

                                ++groupChatID;
                            } else if (input.startsWith("ADD_GC")) {
                                String[] message = input.trim().split("\\s+");
                                ArrayList<ClientInfo> groupChatUsers = groupChats.get(message[1]);
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
                                    ;



                                // For testing purposes
                                out.writeObject("TO_GC " + groupChatID + " " + groupChatUsers);
                                out.flush();

                            } else if (input.startsWith("GC_MES")){
                                String[] message = input.trim().split("\\s+");
                                ArrayList<ClientInfo> groupChatUsers = groupChats.get(message[1]);
                                StringBuilder deliver = new StringBuilder();

                                for (int i = 2; i < message.length; i++)
                                    deliver.append(message[i]).append(" ");

                                for (ClientInfo client: groupChatUsers) {
                                    client.getWriter().writeObject("SEND_GC " + message[1] + " " + deliver);
                                    client.getWriter().flush();
                                }

                            } else if (input.startsWith("GET_NAMES_IN_GC")) {
                                String[] message = input.trim().split("\\s+");
                                String key = message[1];
                                ArrayList<ClientInfo> groupChatUsers = groupChats.get(key);
                                StringBuilder names = new StringBuilder();

                                for (ClientInfo c: groupChatUsers)
                                    names.append(c.getName()).append(" ");

                                out.writeObject("NAMES_IN_GC " + message[1] + " " + names);
                                out.flush();
                                System.out.println(names);
                            } else if (input.startsWith("GET_NAMES_IN_CR")){
                                String[] message = input.trim().split("\\s+");
                                String key = message[1];
                                ArrayList<ClientInfo> chatroomUsers = chatrooms.get(key);
                                StringBuilder names = new StringBuilder();

                                for (ClientInfo c : chatroomUsers)
                                    names.append(c.getName()).append(" ");

                                out.writeObject("NAMES_IN_CR " + message[1] + " " + names);
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
                                serverLog.append(message[3] + " is joining the group chat\n");

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
                                } else {
                                    for (ClientInfo client: clients) {
                                        if (client.getName().equalsIgnoreCase(message[3])) {
                                            client.getWriter().writeObject("JOIN_CR_MESSAGE " + "REJECTED");
                                            break;
                                        }
                                    }
                                }
                            } else if (input.startsWith("TO_CR")) {
                                String[] messages = input.trim().split("\\s+");
                                ArrayList<ClientInfo> curr = chatrooms.get(messages[1]);

                                StringBuilder message = new StringBuilder();

                                for (int i = 2; i < messages.length; i++) {
                                    message.append(messages[i]).append(" ");
                                }

                                for (ClientInfo client : curr) {
                                    if (client.getWriter() != null) {
                                        client.getWriter().writeObject("CR_MESSAGE " + messages[1] + " " + message);
                                        client.getWriter().flush();
                                    }
                                }
                            } else if (input.startsWith("DOWNLOAD_FILE")) {
                                String[] messages = input.trim().split("\\s+");
                                String target = messages[1];

                                FileToTransfer targetFile = null;

                                for(FileToTransfer file : files)
                                    if(target.equals(file.getName()))
                                        targetFile = file;

                                String path = System.getProperty("user.dir") + "\\" +
                                         targetFile.getName() + targetFile.getExtension();
                                System.out.println(path);

                                try (FileOutputStream fos = new FileOutputStream(path)) {
                                    fos.write(targetFile.getContent());
                                    serverLog.append("File is being downloaded\n");
                                }

                            } else {
                                for (ClientInfo client : clients) {
                                    client.getWriter().writeObject("MESSAGE " + name + ": " + input);
                                    client.getWriter().flush();
                                }
                            }
                        }
                        else {
                            serverLog.append("Uploading a file \n");
                            FileToTransfer file = (FileToTransfer) inz;

                            files.add(file);

                            for(ClientInfo client : clients) {
                                client.getWriter().writeObject("UPLOAD_FILE " + file.getName() + file.getExtension());
                                client.getWriter().flush();
                            }

                        }
                    }catch(ClassNotFoundException ex){
                        ex.printStackTrace();
                    }
                }
            } catch (IOException e) {
                serverLog.append(e.getMessage() + "\n");
            } finally {
                if (name != null) {
                    names.remove(name);
                    serverLog.append(name + " has disconnected from the server.\n");

                    if (out != null) {
                        clients.remove(user);
                    }

                    for (ClientInfo client : clients) {
                        if (clients.size() > 1) {
                            try {
                                client.getWriter().writeObject("MESSAGE " + name + " has disconnected.");
                                client.getWriter().flush();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    for (ClientInfo client : clients){
                        try {
                            client.getWriter().writeObject("DISCONNECT " + name);
                            client.getWriter().flush();
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }

                try {
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException ignored) {

                }
            }
        }
    }
}