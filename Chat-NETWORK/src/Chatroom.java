import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Chatroom extends JFrame {

	private JPanel contentPane;
	private JTextField messageField;
	private JTextArea charoomMessageArea;
	private JList listUsers;
	private DefaultListModel listUsersModel;
	private JButton btnSend;
	private JButton btnFileTransfer;
	private JLabel lblChatroomUsers;
	private BufferedReader in;
	private PrintWriter out;
	private String user;
	private String chatroomName;

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

	public Chatroom(String chatroomName, PrintWriter out, String user) {
		this.setTitle("Chatroom: " + chatroomName);
		this.out = out;
		this.user = user;
		this.chatroomName = chatroomName;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		charoomMessageArea = new JTextArea();
		charoomMessageArea.setBounds(10, 11, 409, 401);
		contentPane.add(charoomMessageArea);
		charoomMessageArea.setEditable(false);
		
		messageField = new JTextField();
		messageField.setBounds(10, 423, 409, 27);
		contentPane.add(messageField);

		messageField.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				out.println("TO_CR " + chatroomName + " " + user + ": " + messageField.getText());
				messageField.setText("");
			}
		});

		listUsersModel = new DefaultListModel();
		messageField.setColumns(10);

		listUsers = new JList(listUsersModel);
		listUsers.setBounds(429, 36, 145, 292);
		contentPane.add(listUsers);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println("TO_CR " + chatroomName + " " + user + ": " + messageField.getText());
				messageField.setText("");
			}
		});
		btnSend.setBounds(429, 387, 145, 61);
		contentPane.add(btnSend);
		
		btnFileTransfer = new JButton("Send File");
		btnFileTransfer.setBounds(429, 339, 145, 37);
		contentPane.add(btnFileTransfer);
		
		lblChatroomUsers = new JLabel("Chatroom Users");
		lblChatroomUsers.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatroomUsers.setBounds(429, 11, 145, 14);
		contentPane.add(lblChatroomUsers);

		this.setVisible(true);
	}

	public String getChatroomName() {
		return chatroomName;
	}

	public void appendMessage(String message) {
		charoomMessageArea.append(message + "\n");
	}

	public DefaultListModel getUserListModel() {
		return listUsersModel;
	}

}
