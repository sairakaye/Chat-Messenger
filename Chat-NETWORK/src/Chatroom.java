import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;

public class Chatroom extends JFrame {

	private JPanel contentPane;
	private JTextField messageField;
	private JTextArea chatroomMessageArea;
	private JList listUsers;
	private DefaultListModel listUsersModel;
	private JButton btnSend;
	private JButton btnFileTransfer;
	private JLabel lblChatroomUsers;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String user;
	private String chatroomName;
	private JScrollPane messageScrollPane;
	private JScrollPane userScrollPane;

	private ArrayList<Chatroom> openedChatrooms;

	/*
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GroupChat frame = new GroupChat("e", null, null );
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	*/

	public Chatroom(String chatroomName, ObjectOutputStream out, String user, ArrayList<Chatroom> openedChatrooms) {
		this.setTitle("Chatroom: " + chatroomName);
		this.out = out;
		this.user = user;
		this.chatroomName = chatroomName;
		this.openedChatrooms = openedChatrooms;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				openedChatrooms.remove(this);
				Chatroom.super.dispose();
			}
		});
		setBounds(100, 100, 600, 500);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		chatroomMessageArea = new JTextArea();
		chatroomMessageArea.setBounds(10, 11, 409, 401);
		chatroomMessageArea.setEditable(false);
		
		messageScrollPane = new JScrollPane(chatroomMessageArea);
		messageScrollPane.setBounds(10, 11, 409, 401);
		contentPane.add(messageScrollPane);
		
		messageField = new JTextField();
		messageField.setBounds(10, 423, 409, 27);
		contentPane.add(messageField);

		messageField.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					out.writeObject("TO_CR " + chatroomName + " " + user + ": " + messageField.getText());
					out.flush();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				messageField.setText("");
			}
		});

		listUsersModel = new DefaultListModel();
		messageField.setColumns(10);

		listUsers = new JList(listUsersModel);
		listUsers.setBounds(429, 36, 145, 292);
		
		userScrollPane = new JScrollPane(listUsers);
		userScrollPane.setBounds(429, 36, 145, 292);
		contentPane.add(userScrollPane);
		
		btnSend = new JButton("Send");
		btnSend.setForeground(new Color(255, 255, 255));
		btnSend.setBackground(new Color(0, 0, 128));
		btnSend.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					out.writeObject("TO_CR " + chatroomName + " " + user + ": " + messageField.getText());
					out.flush();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				messageField.setText("");
			}
		});
		btnSend.setBounds(429, 387, 145, 61);
		contentPane.add(btnSend);
		
		btnFileTransfer = new JButton("Send File");
		btnFileTransfer.setBackground(new Color(0, 0, 128));
		btnFileTransfer.setForeground(new Color(255, 255, 255));
		btnFileTransfer.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		btnFileTransfer.setBounds(429, 339, 145, 37);
		btnFileTransfer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
		contentPane.add(btnFileTransfer);
		
		lblChatroomUsers = new JLabel("Chatroom Users");
		lblChatroomUsers.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblChatroomUsers.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatroomUsers.setBounds(429, 11, 145, 27);
		contentPane.add(lblChatroomUsers);

		this.setVisible(true);
	}

	public String getChatroomName() {
		return chatroomName;
	}

	public void appendMessage(String message) {
		chatroomMessageArea.append(message + "\n");
	}

	public DefaultListModel getUserListModel() {
		return listUsersModel;
	}

}
