import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GroupChat extends JFrame {

	private JPanel contentPane;
	private JTextField messageField;
	private JTextArea groupMessageArea;
	private JList listUsers;
	private DefaultListModel listUsersModel;
	private JButton btnSend;
	private JButton btnFileTransfer;
	private JLabel lblUsersInGroup;
	private JButton btnInviteUser;
	private BufferedReader in;
	private PrintWriter out;
	private String user;
	private String groupChatID;

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

	public GroupChat(String groupChatID, PrintWriter out, String user) {
		this.setTitle("Group Chat - ID #" + groupChatID);
		this.out = out;
		this.user = user;
		this.groupChatID = groupChatID;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		groupMessageArea = new JTextArea();
		groupMessageArea.setBounds(10, 11, 409, 401);
		contentPane.add(groupMessageArea);
		groupMessageArea.setEditable(false);
		
		messageField = new JTextField();
		messageField.setBounds(10, 423, 409, 27);
		contentPane.add(messageField);

		messageField.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				out.println("GC_MES " + groupChatID + " " + user + ": " + messageField.getText());
				messageField.setText("");
			}
		});

		listUsersModel = new DefaultListModel();
		messageField.setColumns(10);

		listUsers = new JList(listUsersModel);
		listUsers.setBounds(429, 36, 145, 244);
		contentPane.add(listUsers);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println("GC_MES " + groupChatID + " " + user + ": " + messageField.getText());
				messageField.setText("");
			}
		});
		btnSend.setBounds(429, 387, 145, 61);
		contentPane.add(btnSend);
		
		btnFileTransfer = new JButton("Send File");
		btnFileTransfer.setBounds(429, 291, 145, 37);
		contentPane.add(btnFileTransfer);
		
		lblUsersInGroup = new JLabel("Users in Group Chat");
		lblUsersInGroup.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsersInGroup.setBounds(429, 11, 145, 14);
		contentPane.add(lblUsersInGroup);
		
		btnInviteUser = new JButton("Invite User");
		btnInviteUser.setBounds(429, 339, 145, 37);
		contentPane.add(btnInviteUser);

		this.setVisible(true);
	}

	public String getID() {
		return groupChatID;
	}

	public void appendMessage(String message) {
		groupMessageArea.append(message + "\n");
	}

	public DefaultListModel getUserListModel() {
		return listUsersModel;
	}

}
