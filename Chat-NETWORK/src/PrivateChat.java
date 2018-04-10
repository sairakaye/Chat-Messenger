import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.border.BevelBorder;

public class PrivateChat extends JFrame {

	private JPanel contentPane;
	private JList listFiles;
	private DefaultListModel filesListModel;
	private JTextField messageField;
	private JTextArea privateMessageArea;
	private JButton btnSend;
	private JButton btnFileTransfer;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String user;
	private String toPMUser;
	private JScrollPane messageScrollPane;
	private ArrayList<PrivateChat> openedPrivateChats;
	private JButton btnDownloadFile;
	private JScrollPane filesScrollPane;

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

	public PrivateChat(String toPMUser, ObjectOutputStream out, String user) {
		this.setTitle("Private Chat with " + toPMUser);
		this.out = out;
		this.user = user;
		this.toPMUser = toPMUser;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


		setBounds(100, 100, 575, 460);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		privateMessageArea = new JTextArea();
		privateMessageArea.setFont(new Font("Calibri", Font.PLAIN, 14));
		privateMessageArea.setBounds(10, 11, 542, 363);
		privateMessageArea.setEditable(false);
		
		messageScrollPane = new JScrollPane(privateMessageArea);
		messageScrollPane.setBounds(10, 11, 322, 363);
		contentPane.add(messageScrollPane);
		
		messageField = new JTextField();
		messageField.setFont(new Font("Calibri", Font.PLAIN, 14));
		messageField.setBounds(10, 385, 322, 27);
		contentPane.add(messageField);

		messageField.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				/*
				out.println("GC_MES " + groupChatID + " " + user + ": " + messageField.getText());
				messageField.setText("");
				*/

				try {
					out.writeObject("PRIVATE_MESSAGE " + toPMUser + " " + user + " " + messageField.getText());
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
//				privateMessageArea.append(user + ": " + messageField.getText() + "\n");
				messageField.setText("");

			}
		});

		messageField.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.setBackground(new Color(0, 0, 128));
		btnSend.setForeground(new Color(255, 255, 255));
		btnSend.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				out.println("GC_MES " + groupChatID + " " + user + ": " + messageField.getText());
				messageField.setText("");
				*/
				try {
					out.writeObject("PRIVATE_MESSAGE " + toPMUser + " " + user + " " + messageField.getText());
					out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
//				privateMessageArea.append(user + ": " + messageField.getText() + "\n");
				messageField.setText("");
			}
		});
		btnSend.setBounds(344, 384, 201, 27);
		contentPane.add(btnSend);
		
		btnFileTransfer = new JButton("Send File");
		btnFileTransfer.setBackground(new Color(0, 0, 128));
		btnFileTransfer.setForeground(new Color(255, 255, 255));
		btnFileTransfer.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		btnFileTransfer.setBounds(344, 347, 201, 27);
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
						FileToTransfer ftf = new FileToTransfer(content, name[0], name[1], "Private", toPMUser, user);

						out.writeObject(ftf);
						out.flush();
					}
				} catch(IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(btnFileTransfer);

		
		filesListModel = new DefaultListModel<>();
		
		JLabel lblFiles = new JLabel("Files");
		lblFiles.setBounds(433, 13, 54, 16);
		contentPane.add(lblFiles);
		
		btnDownloadFile = new JButton("Download File");
		btnDownloadFile.setForeground(Color.WHITE);
		btnDownloadFile.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		btnDownloadFile.setBackground(new Color(0, 0, 128));
		btnDownloadFile.setBounds(344, 307, 201, 27);
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
		
		filesScrollPane = new JScrollPane();
		filesScrollPane.setBounds(344, 29, 201, 265);
		contentPane.add(filesScrollPane);
		
		listFiles = new JList<>(filesListModel);
		filesScrollPane.setViewportView(listFiles);

		this.setVisible(true);
	}

	public String getToPMUser() {
		return toPMUser;
	}

	public String getUser() { return user; }

	public void appendMessage(String message) {
		privateMessageArea.append(message + "\n");
	}

	public DefaultListModel getFilesListModel() {
		return filesListModel;
	}
}
