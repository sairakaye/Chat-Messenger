import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
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
	private JScrollPane scrollPane;
	private JLabel lblFiles;
	private DefaultListModel filesListModel;
	private JList listFiles;
	private JButton btnDownloadFile;

	public Chatroom(String chatroomName, ObjectOutputStream out, String user) {
		this.setTitle("Chatroom: " + chatroomName);
		this.out = out;
		this.user = user;
		this.chatroomName = chatroomName;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
			public void actionPerformed(ActionEvent evt) {
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
		userScrollPane.setBounds(429, 36, 145, 130);
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
				FileDialog dialog = new FileDialog((Frame) null, "Select file to Open");
				dialog.setVisible(true);

				File[] files = dialog.getFiles();

				File file = files[0];

				try {
					if(file.exists()) {
						byte[] content = Files.readAllBytes(file.toPath());
						String temp = file.getName();
						String[] name = temp.split("\\.");
						FileToTransfer ftf = new FileToTransfer(content, name[0], name[1], "Chatroom", chatroomName, "");

						out.writeObject(ftf);
						out.flush();
					}
				} catch(IOException e1) {
					e1.printStackTrace();
				}
            }
        });
		contentPane.add(btnFileTransfer);
		
		lblChatroomUsers = new JLabel("Chatroom Users");
		lblChatroomUsers.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblChatroomUsers.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatroomUsers.setBounds(429, 11, 145, 27);
		contentPane.add(lblChatroomUsers);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(431, 189, 143, 87);
		contentPane.add(scrollPane);
		
		filesListModel = new DefaultListModel<String>();
		
		listFiles = new JList(filesListModel);
		scrollPane.setViewportView(listFiles);
		
		lblFiles = new JLabel("Files");
		lblFiles.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblFiles.setBounds(487, 171, 56, 16);
		contentPane.add(lblFiles);
		
		btnDownloadFile = new JButton("Download File");
		btnDownloadFile.setForeground(Color.WHITE);
		btnDownloadFile.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		btnDownloadFile.setBackground(new Color(0, 0, 128));
		btnDownloadFile.setBounds(429, 289, 145, 37);
		contentPane.add(btnDownloadFile);
		btnDownloadFile.addActionListener(e -> {
			String name = (String) listFiles.getSelectedValue();
			String[] temp = name.split("\\.");

			try {
				out.writeObject("DOWNLOAD_FILE " + temp[0]);
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

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

	public DefaultListModel getFilesListModel() { return filesListModel; }

}
