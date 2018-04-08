package src;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class NewChatroom extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private ObjectOutputStream out;
	private String clientName;
	private JPasswordField passwordField;

	/**
	 * Launch the application.

	public static void main(String[] args) {
		try {
			NewChatroom dialog = new NewChatroom();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 */

	/**
	 * Create the dialog.
	 */
	public NewChatroom(String clientName, ObjectOutputStream out) {
		this.out = out;
		this.clientName = clientName;
		this.setTitle("Create New Chatroom");
		setBounds(100, 100, 450, 190);
		getContentPane().setLayout(null);
		contentPanel.setBackground(new Color(255, 255, 255));
		contentPanel.setBounds(0, 0, 434, 228);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(134, 22, 290, 33);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JLabel lblChatroomName = new JLabel("Chatroom Name");
		lblChatroomName.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		lblChatroomName.setBounds(10, 22, 133, 30);
		contentPanel.add(lblChatroomName);

		passwordField = new JPasswordField();
		passwordField.setBounds(134, 63, 172, 33);
		contentPanel.add(passwordField);
		
		JCheckBox showPasswordChkBx = new JCheckBox("Show Password");
		showPasswordChkBx.setBounds(311, 62, 113, 33);
		contentPanel.add(showPasswordChkBx);
		showPasswordChkBx.setBackground(new Color(255, 255, 255));
		showPasswordChkBx.setFont(new Font("Trebuchet MS", Font.PLAIN, 11));
		showPasswordChkBx.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		        if (e.getStateChange() == ItemEvent.SELECTED) {
		            passwordField.setEchoChar((char) 0);		            
		        } else {
		        	passwordField.setEchoChar('\u2022');
		        }
		    }
		});
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(new Color(255, 255, 255));
			buttonPane.setBounds(0, 110, 434, 33);
			contentPanel.add(buttonPane);
			buttonPane.setLayout(null);
			{
				JButton okButton = new JButton("OK");
				okButton.setForeground(new Color(255, 255, 255));
				okButton.setBackground(new Color(0, 0, 128));
				okButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							String password = new String(passwordField.getPassword());
							out.writeObject("CREATE_CHATROOM " + textField.getText() + " " + password + " " + clientName);
							out.flush();
						}catch(Exception ex){
							ex.printStackTrace();
						}
						NewChatroom.super.dispose();
					}
				});
				okButton.setBounds(230, 5, 87, 23);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setBackground(new Color(0, 0, 128));
				cancelButton.setForeground(new Color(255, 255, 255));
				cancelButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						NewChatroom.super.dispose();
					}
				});
				cancelButton.setBounds(327, 5, 102, 23);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JLabel lblPassword = new JLabel("Chatroom Password");
			lblPassword.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
			lblPassword.setBounds(10, 67, 133, 30);
			contentPanel.add(lblPassword);
		}
}
