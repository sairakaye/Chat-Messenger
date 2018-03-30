import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;

public class NewChatroom extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private PrintWriter out;
	private String clientName;
	private JTextField passwordTextField;

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
	public NewChatroom(String clientName, PrintWriter out) {
		this.out = out;
		this.clientName = clientName;
		setBounds(100, 100, 450, 190);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 434, 228);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(153, 11, 271, 33);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JLabel lblChatroomName = new JLabel("Chatroom Name");
		lblChatroomName.setBounds(10, 12, 133, 30);
		contentPanel.add(lblChatroomName);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 110, 434, 33);
			contentPanel.add(buttonPane);
			buttonPane.setLayout(null);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						out.println("CREATE_CHATROOM " + textField.getText() + " " + passwordTextField.getText() + " " + clientName);
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
				cancelButton.setBounds(327, 5, 102, 23);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			passwordTextField = new JTextField();
			passwordTextField.setBounds(153, 66, 271, 33);
			contentPanel.add(passwordTextField);
			passwordTextField.setColumns(10);
		}
		{
			JLabel lblPassword = new JLabel("Chatroom Password");
			lblPassword.setBounds(10, 67, 133, 30);
			contentPanel.add(lblPassword);
		}
	}
}
