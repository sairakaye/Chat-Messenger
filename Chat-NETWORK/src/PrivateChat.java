import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PrivateChat extends JFrame {

	private JPanel contentPane;
	private JTextField messageField;
	private JTextArea privateMessageArea;
	private JButton btnSend;
	private JButton btnFileTransfer;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String user;
	private String toPMUser;

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
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 575, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		privateMessageArea = new JTextArea();
		privateMessageArea.setBounds(10, 11, 542, 363);
		contentPane.add(privateMessageArea);
		privateMessageArea.setEditable(false);
		
		messageField = new JTextField();
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
		btnSend.setBounds(342, 385, 100, 27);
		contentPane.add(btnSend);
		
		btnFileTransfer = new JButton("Send File");
		btnFileTransfer.setBounds(452, 385, 100, 27);
		contentPane.add(btnFileTransfer);

		this.setVisible(true);
	}

	public String getToPMUser() {
		return toPMUser;
	}

	public void appendMessage(String message) {
		privateMessageArea.append(message + "\n");
	}
}
