import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;

public class GroupChat extends JFrame {

	private JPanel contentPane;
	private JTextField messageField;
	private JTextArea groupMessageArea;
	private JList listUsers;
	private DefaultListModel listUsersModel;
	private DefaultListModel listFilesModel;
	private JButton btnSend;
	private JButton btnFileTransfer;
	private JLabel lblUsersInGroup;
	private JButton btnInviteUser;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String user;
	private String groupChatID;
	private JScrollPane usersScrollPane;
	private JScrollPane messageScrollPane;
	private ArrayList<GroupChat> openedGroupChat;
	private JLabel lblFiles;
	private JScrollPane filesScrollPane;
	private JButton btnDownloadFile;
	private JList listFiles;

	public GroupChat(String groupChatID, ObjectOutputStream out, String user, DefaultListModel onlineListModel) {
		this.setTitle("Group Chat - ID #" + groupChatID);
		this.out = out;
		this.user = user;
		this.groupChatID = groupChatID;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		groupMessageArea = new JTextArea();
		groupMessageArea.setFont(new Font("Calibri", Font.PLAIN, 14));
		groupMessageArea.setBounds(10, 11, 409, 401);
		groupMessageArea.setEditable(false);
		
		messageScrollPane = new JScrollPane(groupMessageArea);
		messageScrollPane.setBounds(10, 11, 409, 401);
		contentPane.add(messageScrollPane);
		
		messageField = new JTextField();
		messageField.setFont(new Font("Calibri", Font.PLAIN, 14));
		messageField.setBounds(10, 423, 409, 27);
		contentPane.add(messageField);

		messageField.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					out.writeObject("GC_MES " + groupChatID + " " + user + ": " + messageField.getText());
					out.flush();
				}catch(Exception e){
					e.printStackTrace();
				}
				messageField.setText("");
			}
		});

		listUsersModel = new DefaultListModel();
		messageField.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		btnSend.setForeground(new Color(255, 255, 255));
		btnSend.setBackground(new Color(0, 0, 139));
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					out.writeObject("GC_MES " + groupChatID + " " + user + ": " + messageField.getText());
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
		btnFileTransfer.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnFileTransfer.setForeground(new Color(255, 255, 255));
		btnFileTransfer.setBackground(new Color(0, 0, 128));
		btnFileTransfer.setBounds(429, 296, 145, 37);
		btnFileTransfer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
		contentPane.add(btnFileTransfer);
		
		lblUsersInGroup = new JLabel("Group Chat Users");
		lblUsersInGroup.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblUsersInGroup.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsersInGroup.setBounds(429, 11, 145, 14);
		contentPane.add(lblUsersInGroup);
		
		btnInviteUser = new JButton("Invite User");
		btnInviteUser.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnInviteUser.setForeground(new Color(255, 255, 255));
		btnInviteUser.setBackground(new Color(0, 0, 128));
		btnInviteUser.setBounds(429, 339, 145, 37);
		btnInviteUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new InviteUser(GroupChat.this, onlineListModel);
			}
		});
		contentPane.add(btnInviteUser);
		
		listUsers = new JList(listUsersModel);
		listUsers.setFont(new Font("Calibri", Font.BOLD, 14));
		listUsers.setBounds(429, 36, 145, 244);
		
		usersScrollPane = new JScrollPane(listUsers);
		usersScrollPane.setBounds(431, 27, 145, 121);
		contentPane.add(usersScrollPane);
		
		lblFiles = new JLabel("Files");
		lblFiles.setHorizontalAlignment(SwingConstants.CENTER);
		lblFiles.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblFiles.setBounds(431, 150, 145, 14);
		contentPane.add(lblFiles);
		
		filesScrollPane = new JScrollPane();
		filesScrollPane.setBounds(431, 170, 143, 75);
		contentPane.add(filesScrollPane);
		
		listFilesModel = new DefaultListModel<>();
		
		listFiles = new JList(listFilesModel);
		filesScrollPane.setViewportView(listFiles);
		
		btnDownloadFile = new JButton("Download File");
		btnDownloadFile.setForeground(Color.WHITE);
		btnDownloadFile.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnDownloadFile.setBackground(new Color(0, 0, 128));
		btnDownloadFile.setBounds(429, 255, 145, 37);
		contentPane.add(btnDownloadFile);

		this.setVisible(true);
	}

	public String getID() {
		return groupChatID;
	}

	public void refreshListUsers(){
	    listUsers.setModel(listUsersModel);
	    listUsers.repaint();
    }

	public void appendMessage(String message) {
		groupMessageArea.append(message + "\n");
	}

	public DefaultListModel getUserListModel() {
		return listUsersModel;
	}

	public void setDefaultListModel(DefaultListModel model){
	    listUsersModel = model;
    }

	//notify server that a user has been added to the group chat user
	public void addUserSuccess(String user){
		try {
			out.writeObject("ADD_GC " + groupChatID + " " + user);
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
