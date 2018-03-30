import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;

public class PasswordDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JLabel lblPassword;
	private String clientName;
	private PrintWriter out;
	private String chatroomName;
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
	public PasswordDialog(String chatroomName, String clientName, PrintWriter out) {
		this.chatroomName = chatroomName;
		this.clientName = clientName;
		this.out = out;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblPassword = new JLabel("Enter the password:");
		}
		{
			textField = new JTextField();
			textField.setColumns(10);
		}
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		contentPanel.add(lblPassword);
		contentPanel.add(textField);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						out.println("JOIN_CHATROOM " + chatroomName + " " + textField.getText() + " " + clientName);
						PasswordDialog.super.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
