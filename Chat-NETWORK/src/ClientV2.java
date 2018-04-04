import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.util.List;

public class ClientV2 extends JFrame {

    private JPanel contentPane;
    private JTextField messageField;
    private JList listOnline;
    private JTextArea messageArea;
    private JButton btnSend;
    private DefaultListModel onlineListModel;
    private DefaultListModel chatroomListModel;
    private DefaultListModel filesListModel;
    private JButton btnPrivateMessage;
    private JButton btnFileTransfer;
    private JButton btnJoinChatroom;
    private JButton btnCreateChatroom;
    private JList listChatroom;
    private JList listFiles;

    ObjectInputStream in;
    ObjectOutputStream out;
    private ArrayList<Chatroom> openedChatrooms;
    private ArrayList<GroupChat> groupChatWindows;
    private ArrayList<PrivateChat> privateChatWindows;

    //add here windows for private message;
    private String clientName;
    private JButton btnGroupChat;
    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        ClientV2 frame = new ClientV2();
        frame.setVisible(true);

        try {
            frame.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the frame.
     */
    public ClientV2() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 570);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBounds(10, 40, 432, 435);
        contentPane.add(messageArea);

        messageField = new JTextField();
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
        btnSend.setBounds(452, 450, 133, 70);
        contentPane.add(btnSend);

        // If needed
        onlineListModel = new DefaultListModel();
        chatroomListModel = new DefaultListModel();
        filesListModel = new DefaultListModel();

        listOnline = new JList(onlineListModel);
        listOnline.setBounds(452, 40, 133, 248);
        contentPane.add(listOnline);

        listOnline.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listFiles = new JList(filesListModel);
        listFiles.setBounds(597, 319, 179, 90);
        contentPane.add(listFiles);
        
        btnPrivateMessage = new JButton("Private Message");
        btnPrivateMessage.setBounds(452, 398, 133, 41);
        contentPane.add(btnPrivateMessage);
        btnPrivateMessage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (privateChatWindows == null) {
                    privateChatWindows = new ArrayList<>();
                    privateChatWindows.add(new PrivateChat((String)listOnline.getSelectedValue(), out, clientName));
                } else {
                    for (PrivateChat p : privateChatWindows) {
                        if (p.getToPMUser().equalsIgnoreCase((String)listOnline.getSelectedValue()))
                            p.setVisible(true);
                    }
                }
            }
        });

        btnFileTransfer = new JButton("Send File");
        btnFileTransfer.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {

        	}
        });
        btnFileTransfer.setBounds(452, 299, 133, 36);
        contentPane.add(btnFileTransfer);

        btnJoinChatroom = new JButton("Join Chatroom");
        btnJoinChatroom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PasswordDialog pd = new PasswordDialog((String)listChatroom.getSelectedValue(), clientName, out);
                pd.setVisible(true);
            }
        });
        btnJoinChatroom.setBounds(595, 422, 179, 41);
        contentPane.add(btnJoinChatroom);

        btnCreateChatroom = new JButton("Create Chatroom");
        btnCreateChatroom.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	    NewChatroom dialog = new NewChatroom(clientName, out);
        	    dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        	    dialog.setVisible(true);
        	}
        });
        btnCreateChatroom.setBounds(595, 474, 179, 46);
        contentPane.add(btnCreateChatroom);

        listChatroom = new JList(chatroomListModel);
        listChatroom.setBounds(595, 40, 179, 248);
        contentPane.add(listChatroom);
        
        JLabel lblOnlineUsers = new JLabel("Online Users");
        lblOnlineUsers.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblOnlineUsers.setHorizontalAlignment(SwingConstants.CENTER);
        lblOnlineUsers.setBounds(452, 16, 133, 18);
        contentPane.add(lblOnlineUsers);
        
        JLabel lblChatrooms = new JLabel("Chatrooms");
        lblChatrooms.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblChatrooms.setHorizontalAlignment(SwingConstants.CENTER);
        lblChatrooms.setBounds(595, 16, 179, 18);
        contentPane.add(lblChatrooms);
        
        JLabel lblChatroom = new JLabel("Hello! Let's Chat.");
        lblChatroom.setFont(new Font("Tahoma", Font.PLAIN, 26));
        lblChatroom.setBounds(10, 0, 332, 34);
        contentPane.add(lblChatroom);
        
        btnGroupChat = new JButton("Group Chat");
        btnGroupChat.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                List list = listOnline.getSelectedValuesList();
                String toSend = "CREATE_GC " + clientName + " ";

                for (int i = 0; i < list.size(); i++) {
                    toSend += list.get(i) + " ";
                }

                try {
                    out.writeObject(toSend);
                    out.flush();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
        	}
        });
        btnGroupChat.setBounds(452, 346, 133, 41);
        contentPane.add(btnGroupChat);
        
        JLabel lblFiles = new JLabel("Files");
        lblFiles.setHorizontalAlignment(SwingConstants.CENTER);
        lblFiles.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblFiles.setBounds(597, 299, 179, 18);
        contentPane.add(lblFiles);
        
    }

    private String getUserName() {
        return JOptionPane.showInputDialog(
                this,
                "Choose a screen name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE);
    }

    private synchronized void run() throws IOException {

        // Make connection and initialize streams

        // Change the server address to the server's IP address if ever.
        String serverAddress = "localhost";
        Socket socket = new Socket(serverAddress, 49152);

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        String userName = null;
        String[] names = null;

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = "";
            Object linez;
            try {
                linez = in.readObject();
                if (linez instanceof String) {
                    line = (String) linez;

                    if (line.startsWith("GET_NAME")) {
                        userName = getUserName();
                        out.writeObject(userName);
                        out.flush();
                    } else if (line.startsWith("NAME_OK")) {
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
                            privateChatWindows.get(privateChatWindows.size()-1).appendMessage(toSend);
                        } else {
                            for (PrivateChat p : privateChatWindows) {
                                if (p.getToPMUser().equalsIgnoreCase(message[1]))
                                    p.appendMessage(toSend);
                            }
                        }
                    } else if (line.startsWith("TO_GC")){
                        String[] message = line.trim().split("\\s+");
                        String toSend = "";

                        if (groupChatWindows == null) {
                            groupChatWindows = new ArrayList<>();
                            groupChatWindows.add(new GroupChat(message[1], out, userName, onlineListModel));

                            for (int i = 2; i < message.length; i++)
                                groupChatWindows.get(groupChatWindows.size()-1).getUserListModel().addElement(message[i]);
                        }
                    } else if (line.startsWith("SEND_GC")){
                        String[] message = line.trim().split("\\s+");

                        String toSend = "";

                        for (int i = 2; i < message.length; i++)
                            toSend += message[i] + " ";

                        if (groupChatWindows != null) {
                            for (GroupChat c : groupChatWindows)
                                if (c.getID().equalsIgnoreCase(message[1])) {
                                    c.appendMessage(toSend);
                                    out.writeObject("GET_NAMES_IN_GC " + message[1]);
                                    out.flush();
                                    break;
                                }
                        } else {
                            groupChatWindows = new ArrayList<>();
                            groupChatWindows.add(new GroupChat(message[1], out, userName, onlineListModel));
                            groupChatWindows.get(groupChatWindows.size()-1).appendMessage(toSend);
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


                        if (openedChatrooms == null) {
                            openedChatrooms = new ArrayList<>();
                            openedChatrooms.add(new Chatroom(message[1], out, userName));
                            openedChatrooms.get(openedChatrooms.size() - 1).appendMessage(toSend);
                        } else {
                            for (Chatroom c : openedChatrooms) {
                                if (c.getChatroomName().equalsIgnoreCase(message[1])) {
                                    c.appendMessage(toSend);
                                    out.writeObject("GET_NAMES_IN_CR " + message[1]);
                                    out.flush();
                                }
                            }
                        }
                    } else if (line.startsWith("NAMES_IN_GC")) {
                        String[] message = line.trim().split("\\s+");

                        if (groupChatWindows != null) {
                            for (GroupChat g: groupChatWindows)
                                if (message[1].equalsIgnoreCase(g.getID())) {
                                    for (int i = 2; i < message.length; i++) {
                                        int j = 0;
                                        boolean noDuplicate = true;
                                        while (j < g.getUserListModel().getSize() && noDuplicate){
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
                    } else if (line.startsWith("NAMES_IN_CR")){
                        String[] message = line.trim().split("\\s+");

                        if (openedChatrooms != null){
                            for (Chatroom c : openedChatrooms)
                                if(message[1].equalsIgnoreCase(c.getChatroomName())) {
                                    for (int i = 2; i < message.length; i++){
                                        int j = 0;
                                        boolean noDuplicate = true;

                                        while (j < c.getUserListModel().getSize() && noDuplicate){
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
                    }

                    if (line.startsWith("DISCONNECT")){
                        String[] temp = line.trim().split("\\s+");
                        System.out.println("dc " + temp[1]);

                        for (int j = 0; j < onlineListModel.getSize(); j++){
                            System.out.println(onlineListModel.get(j));
                            String compare = (String) onlineListModel.get(j);

                            if (compare.equalsIgnoreCase(temp[1]))
                                onlineListModel.removeElementAt(j);
                        }
                    }

                    // Start of something
                    if (line.startsWith("NAME_CLIENTS")) {
                        String[] temp = line.split("\\s+");

                        for (int i = 1; i < temp.length; i++) {
                            boolean isToAdd = true;
                            for (int j = 0; j < onlineListModel.getSize(); j++) {
                                String compare = (String)onlineListModel.get(j);

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
                                String compare = (String) chatroomListModel.get(j);

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

                    //exceptions occur if this is included since the outputstream was already flushed.
            /*out.writeUTF("GET_NAME_CLIENTS");
            out.flush();*/

                }
                else{

                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
