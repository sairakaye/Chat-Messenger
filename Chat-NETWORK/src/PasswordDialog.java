import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ObjectOutputStream;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import java.awt.Color;

public class PasswordDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private String clientName;
	private ObjectOutputStream out;
	private String chatroomName;
	private JPasswordField passwordField;
	private JLabel lblIncorrectPassword;
	/**
	 * Launch the application.

	public static void main(String[] args) {
		try {
			PasswordDialog dialog = new PasswordDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 */
	public PasswordDialog(String chatroomName, String clientName, ObjectOutputStream out) {
		this.chatroomName = chatroomName;
		this.clientName = clientName;
		this.out = out;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(255, 255, 255));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblEnterTheChatroom = new JLabel("Enter the Chatroom Password:");
			lblEnterTheChatroom.setForeground(new Color(0, 0, 128));
			lblEnterTheChatroom.setHorizontalAlignment(SwingConstants.CENTER);
			lblEnterTheChatroom.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
			lblEnterTheChatroom.setBounds(10, 88, 414, 14);
			contentPanel.add(lblEnterTheChatroom);
		}
		{
			passwordField = new JPasswordField();
			passwordField.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
			passwordField.setBounds(69, 115, 309, 29);
			contentPanel.add(passwordField);
		}
		{
			lblIncorrectPassword = new JLabel("Incorrect password!");
			lblIncorrectPassword.setHorizontalAlignment(SwingConstants.CENTER);
			lblIncorrectPassword.setForeground(new Color(128, 0, 0));
			lblIncorrectPassword.setFont(new Font("Tahoma", Font.PLAIN, 11));
			lblIncorrectPassword.setBounds(69, 155, 309, 14);
			contentPanel.add(lblIncorrectPassword);
			lblIncorrectPassword.setVisible(false);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(new Color(255, 255, 255));
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setForeground(new Color(255, 255, 255));
				okButton.setBackground(new Color(0, 0, 128));
				okButton.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							out.writeObject("JOIN_CHATROOM " + chatroomName + " " + String.valueOf(passwordField.getPassword()) + " " + clientName);
							out.flush();
						}catch(Exception ex){
							ex.printStackTrace();
						}
						PasswordDialog.super.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setBackground(new Color(0, 0, 128));
				cancelButton.setForeground(new Color(255, 255, 255));
				cancelButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public JLabel getLblIncorrectPassword() {
		return lblIncorrectPassword;
	}

}
