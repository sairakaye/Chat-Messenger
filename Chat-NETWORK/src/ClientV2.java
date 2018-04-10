import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;

public class ClientV2 extends JFrame {

    private JPanel contentPane;
    private JTextField messageField;
    private JList<String> listOnline;
    private JTextArea messageArea;
    private JButton btnSend;
    private DefaultListModel<String> onlineListModel;
    private DefaultListModel<String> chatroomListModel;
    private DefaultListModel<String> filesListModel;
    private JButton btnPrivateMessage;
    private JButton btnFileTransfer;
    private JButton btnJoinChatroom;
    private JButton btnCreateChatroom;
    private JButton btnDownload;
    private JList<String> listChatroom;
    private JList<String> listFiles;
    private LoginDialog login;
    private boolean existNameTrigger;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ArrayList<Chatroom> openedChatrooms;
    private ArrayList<GroupChat> groupChatWindows;
    private ArrayList<PrivateChat> privateChatWindows;

    private String clientName;
    private JButton btnGroupChat;
    private JScrollPane messageScrollPane;
    private JScrollPane onlineUsersScrollPane;
    private JScrollPane chatroomsScrollPane;
    private JScrollPane filesScrollPane;
    private JLabel lblHello;

    public static void main(String[] args) {
        ClientV2 frame = new ClientV2();

        try {
            frame.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientV2() {
    	super.setTitle("The BuzzRoom (NETWORK - MP)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 570);
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        messageArea = new JTextArea();
        messageArea.setFont(new Font("Calibri", Font.PLAIN, 14));
        messageArea.setEditable(false);
        messageArea.setBounds(10, 40, 432, 435);

        
        messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBounds(10, 46, 432, 429);
        contentPane.add(messageScrollPane);   
       

        messageField = new JTextField();
        messageField.setFont(new Font("Calibri", Font.PLAIN, 14));
        messageField.setBounds(10, 486, 432, 34);
        contentPane.add(messageField);
        messageField.setColumns(10);

        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    out.writeObject(messageField.getText());
                    out.flush();
                }catch(IOException e){
                    e.printStackTrace();
                }
                messageField.setText("");
            }
        });

        btnSend = new JButton("Send");
        btnSend.setBackground(new Color(0, 0, 128));
        btnSend.setForeground(new Color(255, 255, 255));
        btnSend.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    out.writeObject(messageField.getText());
                    out.flush();
                }catch(IOException e){
                    e.printStackTrace();
                }
                messageField.setText("");
            }
        });
        btnSend.setBounds(452, 461, 133, 59);
        contentPane.add(btnSend);

        // If needed
        onlineListModel = new DefaultListModel<>();
        chatroomListModel = new DefaultListModel<>();
        filesListModel = new DefaultListModel<>();

        listOnline = new JList<>(onlineListModel);
        listOnline.setFont(new Font("Calibri", Font.BOLD, 14));
        listOnline.setBounds(452, 40, 133, 240);

        onlineUsersScrollPane = new JScrollPane(listOnline);
        onlineUsersScrollPane.setBounds(452, 161, 133, 138);
        contentPane.add(onlineUsersScrollPane);

        listOnline.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listFiles = new JList<>(filesListModel);
        listFiles.setFont(new Font("Calibri", Font.BOLD, 14));
        listFiles.setBounds(597, 255, 179, 100);
        
        filesScrollPane = new JScrollPane(listFiles);
        filesScrollPane.setBounds(595, 46, 179, 100);
        contentPane.add(filesScrollPane);
        
        btnPrivateMessage = new JButton("Private Message");
        btnPrivateMessage.setBackground(new Color(0, 0, 128));
        btnPrivateMessage.setForeground(new Color(255, 255, 255));
        btnPrivateMessage.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
        btnPrivateMessage.setBounds(452, 409, 133, 41);
        contentPane.add(btnPrivateMessage);
        btnPrivateMessage.addActionListener(arg0 -> {
            if (privateChatWindows == null) {
                privateChatWindows = new ArrayList<>();
                privateChatWindows.add(new PrivateChat(listOnline.getSelectedValue(), out, clientName));
            } else {
                for (PrivateChat p : privateChatWindows) {
                    if (p.getToPMUser().equalsIgnoreCase(listOnline.getSelectedValue()))
                        p.setVisible(true);
                }
            }
        });

        btnFileTransfer = new JButton("Send File");
        btnFileTransfer.setBackground(new Color(0, 0, 128));
        btnFileTransfer.setForeground(new Color(255, 255, 255));
        btnFileTransfer.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
        btnFileTransfer.addActionListener(arg0 -> {
            FileDialog dialog = new FileDialog((Frame) null, "Select file to Open");
            dialog.setVisible(true);

            File[] files = dialog.getFiles();

            File file = files[0];

            try {
                if(file.exists()) {
                    byte[] content = Files.readAllBytes(file.toPath());
                    String temp = file.getName();
                    String[] name = temp.split("\\.");
                    FileToTransfer ftf = new FileToTransfer(content, name[0], name[1], "", "", "");

                    out.writeObject(ftf);
                    out.flush();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
        btnFileTransfer.setBounds(452, 310, 133, 36);
        contentPane.add(btnFileTransfer);

        btnJoinChatroom = new JButton("Join Chatroom");
        btnJoinChatroom.setBackground(new Color(0, 0, 128));
        btnJoinChatroom.setForeground(new Color(255, 255, 255));
        btnJoinChatroom.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
        btnJoinChatroom.addActionListener(e -> {
            PasswordDialog pd = new PasswordDialog(listChatroom.getSelectedValue(), clientName, out);
            pd.setVisible(true);
        });
        btnJoinChatroom.setBounds(595, 423, 179, 41);
        contentPane.add(btnJoinChatroom);

        btnCreateChatroom = new JButton("Create Chatroom");
        btnCreateChatroom.setBackground(new Color(0, 0, 128));
        btnCreateChatroom.setForeground(new Color(255, 255, 255));
        btnCreateChatroom.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
        btnCreateChatroom.addActionListener(e -> {
            NewChatroom dialog = new NewChatroom(clientName, out);
            dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
        btnCreateChatroom.setBounds(595, 474, 179, 46);

        contentPane.add(btnCreateChatroom);

        listChatroom = new JList<>(chatroomListModel);
        listChatroom.setFont(new Font("Calibri", Font.BOLD, 14));
        listChatroom.setBounds(595, 40, 179, 191);
        
        chatroomsScrollPane = new JScrollPane(listChatroom);
        chatroomsScrollPane.setBounds(595, 232, 179, 180);
        contentPane.add(chatroomsScrollPane);
        
        JLabel lblOnlineUsers = new JLabel("Online Users");
        lblOnlineUsers.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
        lblOnlineUsers.setHorizontalAlignment(SwingConstants.CENTER);
        lblOnlineUsers.setBounds(452, 132, 133, 18);
        contentPane.add(lblOnlineUsers);
        
        JLabel lblChatrooms = new JLabel("Chatrooms");
        lblChatrooms.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
        lblChatrooms.setHorizontalAlignment(SwingConstants.CENTER);
        lblChatrooms.setBounds(595, 209, 179, 18);
        contentPane.add(lblChatrooms);
        
        btnGroupChat = new JButton("Group Chat");
        btnGroupChat.setBackground(new Color(0, 0, 128));
        btnGroupChat.setForeground(new Color(255, 255, 255));
        btnGroupChat.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
        btnGroupChat.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                List<String> list = listOnline.getSelectedValuesList();
                StringBuilder toSend = new StringBuilder("CREATE_GC " + clientName + " ");

                for (String aList : list) {
                    toSend.append(aList).append(" ");
                }

                try {
                    out.writeObject(toSend.toString());
                    out.flush();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
        	}
        });
        btnGroupChat.setBounds(452, 357, 133, 41);
        contentPane.add(btnGroupChat);
        
        JLabel lblFiles = new JLabel("Files");
        lblFiles.setHorizontalAlignment(SwingConstants.CENTER);
        lblFiles.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
        lblFiles.setBounds(595, 19, 179, 27);
        contentPane.add(lblFiles);
        
        btnDownload = new JButton("Download File");
        btnDownload.setBackground(new Color(0, 0, 128));
        btnDownload.setForeground(new Color(255, 255, 255));
        btnDownload.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
        btnDownload.setBounds(595, 157, 179, 41);
        contentPane.add(btnDownload);
        
        JLabel lblLogo = new JLabel("New label");
        lblLogo.setIcon(new ImageIcon("img\\LogoChatroom.png"));
        lblLogo.setBounds(467, 16, 100, 100);
        contentPane.add(lblLogo);
        
        lblHello = new JLabel("Global Chismis Room");
        lblHello.setForeground(new Color(0, 0, 128));
        lblHello.setFont(new Font("Trebuchet MS", Font.BOLD, 26));
        lblHello.setBounds(10, 0, 432, 46);
        contentPane.add(lblHello);
        btnDownload.addActionListener(e -> {
            String name = listFiles.getSelectedValue();
            String[] temp = name.split("\\.");

            try {
                out.writeObject("DOWNLOAD_FILE " + temp[0]);
                out.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        
    }

    private String getServer() {
        return JOptionPane.showInputDialog(
                this,
                " IP Address of Server",
                "Enter IP Address of the Server",
                JOptionPane.QUESTION_MESSAGE);
    }

    private synchronized void run() throws IOException {
        // Make connection and initialize streams
        Socket socket = null;
        String serverAddress = null;

        while (socket == null) {
            try {
                serverAddress = getServer();

                if (serverAddress == null)
                    System.exit(0);

                socket = new Socket(serverAddress, 49152);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        String userName = null;

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = "";
            Object linez;
            try {
                linez = in.readObject();
                if (linez instanceof String) {
                    line = (String) linez;

                    if (line.startsWith("GET_NAME")) {
                        if (login == null)
                            login = new LoginDialog(out, existNameTrigger);
                        else {
                            login.dispose();
                            existNameTrigger = true;
                            login = new LoginDialog(out, existNameTrigger);
                        }
                    } else if (line.startsWith("NAME_OK")) {
                        this.setVisible(true);
                        login.dispose();
                        userName = login.getUserName();
                        onlineListModel.addElement(userName);
                        clientName = userName;
                        out.writeObject("GET_CHATROOMS");
                        out.flush();
                    } else if (line.startsWith("MESSAGE")) {
                        messageArea.append(line.substring(8) + "\n");
                        out.writeObject("GET_NAME_CLIENTS");
                        out.flush();
                    } else if (line.startsWith("SEND_PM")) {
                        String[] message = line.trim().split("\\s+");
                        String toSend = "";

                        for (int i = 3; i < message.length; i++)
                            toSend += message[i] + " ";

                        if (privateChatWindows == null) {
                            privateChatWindows = new ArrayList<>();
                            privateChatWindows.add(new PrivateChat(message[1], out, userName));
                            privateChatWindows.get(privateChatWindows.size() - 1).appendMessage(toSend);
                        } else {
                            for (PrivateChat p : privateChatWindows) {
                                if (p.getToPMUser().equalsIgnoreCase(message[1])) {
                                    p.appendMessage(toSend);

                                    if (!p.isVisible())
                                        p.setVisible(true);
                                }
                            }
                        }
                    } else if (line.startsWith("TO_GC")) {
                        String[] message = line.trim().split("\\s+");

                        if (groupChatWindows == null) {
                            groupChatWindows = new ArrayList<>();
                            groupChatWindows.add(new GroupChat(message[1], out, userName, onlineListModel));

                            for (int i = 2; i < message.length; i++)
                                groupChatWindows.get(groupChatWindows.size() - 1).getUserListModel().addElement(message[i]);
                        }
                    } else if (line.startsWith("SEND_GC")) {
                        String[] message = line.trim().split("\\s+");

                        String toSend = "";

                        for (int i = 2; i < message.length; i++)
                            toSend += message[i] + " ";

                        if (groupChatWindows != null) {
                            for (GroupChat c : groupChatWindows)
                                if (c.getID().equalsIgnoreCase(message[1])) {
                                    c.appendMessage(toSend);

                                    if (c.isVisible() == false)
                                        c.setVisible(true);

                                    out.writeObject("GET_NAMES_IN_GC " + message[1]);
                                    out.flush();
                                    break;
                                }
                        } else {
                            groupChatWindows = new ArrayList<>();
                            groupChatWindows.add(new GroupChat(message[1], out, userName, onlineListModel));
                            groupChatWindows.get(groupChatWindows.size() - 1).appendMessage(toSend);
                        }
                    } else if (line.startsWith("CR_MESSAGE")) {
                        String[] message = line.trim().split("\\s+");
                        String toSend = "";


                        for (int i = 2; i < message.length; i++)
                            toSend += message[i] + " ";

                        if (openedChatrooms == null) {
                            openedChatrooms = new ArrayList<>();
                            openedChatrooms.add(new Chatroom(message[1], out, userName));
                            openedChatrooms.get(openedChatrooms.size() - 1).appendMessage(toSend);
                        } else {
                            for (Chatroom c : openedChatrooms) {
                                if (c.getChatroomName().equalsIgnoreCase(message[1]))
                                    c.appendMessage(toSend);

                                if (c.isVisible() == false)
                                    c.setVisible(true);
                                out.writeObject("GET_NAMES_IN_CR " + message[1]);
                                out.flush();


                            }
                        }

                        out.writeObject("GET_CHATROOMS");
                        out.flush();

                    } else if (line.startsWith("JOIN_CR_MESSAGE")) {
                        String[] message = line.trim().split("\\s+");

                        String toSend = "";

                        for (int i = 2; i < message.length; i++)
                            toSend += message[i] + " ";


                        if (!message[1].equalsIgnoreCase("REJECTED")) {
                            if (openedChatrooms == null) {
                                openedChatrooms = new ArrayList<>();
                                openedChatrooms.add(new Chatroom(message[1], out, userName));
                                openedChatrooms.get(openedChatrooms.size() - 1).appendMessage(toSend);
                            } else {
                                for (Chatroom c : openedChatrooms) {
                                    if (c.getChatroomName().equalsIgnoreCase(message[1])) {
                                        c.appendMessage(toSend);

                                        if (c.isVisible() == false)
                                            c.setVisible(true);

                                        out.writeObject("GET_NAMES_IN_CR " + message[1]);
                                        out.flush();
                                    }
                                }
                            }
                        } else {
                            PasswordDialog pd = new PasswordDialog(listChatroom.getSelectedValue(), clientName, out);
                            pd.getLblIncorrectPassword().setVisible(true);
                            pd.setVisible(true);
                        }
                    } else if (line.startsWith("NAMES_IN_GC")) {
                        String[] message = line.trim().split("\\s+");

                        if (groupChatWindows != null) {
                            for (GroupChat g : groupChatWindows)
                                if (message[1].equalsIgnoreCase(g.getID())) {
                                    for (int i = 2; i < message.length; i++) {
                                        int j = 0;
                                        boolean noDuplicate = true;
                                        while (j < g.getUserListModel().getSize() && noDuplicate) {
                                            if (g.getUserListModel().get(j).toString().equals(message[i]))
                                                noDuplicate = false;
                                            else j++;
                                        }
                                        if (noDuplicate)
                                            g.getUserListModel().addElement(message[i]);
                                    }
                                    break;
                                }
                        }
                    } else if (line.startsWith("UPDATED_GC")){
                        String[] message = line.trim().split("\\s+");

                        if (groupChatWindows != null) {
                            for (GroupChat g : groupChatWindows)
                                if (message[1].equalsIgnoreCase(g.getID())) {
                                    g.getUserListModel().removeAllElements();
                                    g.refreshListUsers();
                                    for (int i = 2; i < message.length; i++) {
                                        int j = 0;
                                        boolean noDuplicate = true;
                                        while (j < g.getUserListModel().getSize() && noDuplicate) {
                                            if (g.getUserListModel().get(j).toString().equals(message[i]))
                                                noDuplicate = false;
                                            else j++;
                                        }
                                        if (noDuplicate)
                                            g.getUserListModel().addElement(message[i]);
                                    }
                                    break;
                                }
                        }
                    } else if (line.startsWith("NAMES_IN_CR")) {
                        String[] message = line.trim().split("\\s+");

                        if (openedChatrooms != null) {
                            for (Chatroom c : openedChatrooms)
                                if (message[1].equalsIgnoreCase(c.getChatroomName())) {
                                    for (int i = 2; i < message.length; i++) {
                                        int j = 0;
                                        boolean noDuplicate = true;

                                        while (j < c.getUserListModel().getSize() && noDuplicate) {
                                            if (c.getUserListModel().get(j).toString().equals(message[i]))
                                                noDuplicate = false;
                                            else j++;
                                        }
                                        if (noDuplicate)
                                            c.getUserListModel().addElement(message[i]);
                                    }
                                    break;
                                }
                        }
                    } else if (line.startsWith("UPLOAD_FILE")) {
                        String[] message = line.trim().split("\\s+");
                        filesListModel.addElement(message[1]);

                    } else if  (line.startsWith("UPLOAD_TO_CHAT")) {
                        String[] message = line.trim().split("\\s+");

                        for (Chatroom openedChatroom : openedChatrooms)
                            if (openedChatroom.getChatroomName().equals(message[2]))
                                openedChatroom.getFilesListModel().addElement(message[1]);


                    } else if  (line.startsWith("UPLOAD_TO_PRIVATE")) {
                        String[] message = line.trim().split("\\s+");

                        for (PrivateChat privateChat : privateChatWindows)
                            if (privateChat.getToPMUser().equals(message[2]) || privateChat.getUser().equals(message[3])
                                    || privateChat.getToPMUser().equals(message[3]) || privateChat.getUser().equals(message[2]))
                                privateChat.getFilesListModel().addElement(message[1]);

                    } else if  (line.startsWith("UPLOAD_TO_GROUP")) {
                        String[] message = line.trim().split("\\s+");

                        for (GroupChat groupChat : groupChatWindows)
                            if (groupChat.getID().equals(message[2]))
                                groupChat.getListFilesModel().addElement(message[1]);

                    }

                    if (line.startsWith("DISCONNECT")) {
                        String[] temp = line.trim().split("\\s+");
                        System.out.println("dc " + temp[1]);

                        for (int j = 0; j < onlineListModel.getSize(); j++) {
                            System.out.println(onlineListModel.get(j));
                            String compare = onlineListModel.get(j);

                            if (compare.equalsIgnoreCase(temp[1]))
                                onlineListModel.removeElementAt(j);
                        }
                    }

                    if (line.startsWith("NAME_CLIENTS")) {
                        String[] temp = line.split("\\s+");

                        for (int i = 1; i < temp.length; i++) {
                            boolean isToAdd = true;
                            for (int j = 0; j < onlineListModel.getSize(); j++) {
                                String compare = onlineListModel.get(j);

                                if (compare.equalsIgnoreCase(temp[i])) {
                                    isToAdd = false;
                                    break;
                                }

                            }

                            if (isToAdd) {
                                onlineListModel.addElement(temp[i]);
                            }
                        }
                    }

                    if (line.startsWith("CHATROOMS")) {
                        String[] temp = line.split("\\s+");

                        for (int i = 1; i < temp.length; i++) {
                            boolean isToAdd = true;
                            for (int j = 0; j < chatroomListModel.getSize(); j++) {
                                String compare = chatroomListModel.get(j);

                                if (compare.equalsIgnoreCase(temp[i])) {
                                    isToAdd = false;
                                    break;
                                }

                            }

                            if (isToAdd) {
                                chatroomListModel.addElement(temp[i]);
                            }
                        }
                    }

                }
            } catch(Exception ex) {
                System.exit(1);
            }
        }
    }
}
