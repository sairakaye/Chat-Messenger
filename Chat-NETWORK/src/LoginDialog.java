import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class LoginDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private ObjectOutputStream out;
	private String userName;
	private JLabel lblBoyAbunda;
	private JLabel unavailableNameLabel;
	private boolean isToShowUnavailable;

	/**
	 * Launch the application.

	public static void main(String[] args) {
		try {
			LoginDialog dialog = new LoginDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 */
	public LoginDialog(ObjectOutputStream out, boolean isToShowUnavailable) {
		this.out = out;
		this.isToShowUnavailable = isToShowUnavailable;
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setTitle("Halika, mag-login ka!");

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		lblBoyAbunda = new JLabel("New label");
		lblBoyAbunda.setIcon(new ImageIcon("img\\LoginPic.png"));
		lblBoyAbunda.setBounds(0, 35, 152, 255);
		contentPanel.add(lblBoyAbunda);
		
		JLabel lblTaraUsapTayo = new JLabel("Tara! Usap tayo.");
		lblTaraUsapTayo.setForeground(new Color(0, 0, 128));
		lblTaraUsapTayo.setFont(new Font("Trebuchet MS", Font.PLAIN, 19));
		lblTaraUsapTayo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTaraUsapTayo.setBounds(10, 11, 152, 35);
		contentPanel.add(lblTaraUsapTayo);
		
		JLabel lblChatroomName = new JLabel("The BuzzRoom");
		lblChatroomName.setForeground(new Color(0, 0, 128));
		lblChatroomName.setFont(new Font("Trebuchet MS", Font.BOLD, 30));
		lblChatroomName.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatroomName.setBounds(183, 76, 221, 46);
		contentPanel.add(lblChatroomName);
		
		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setBounds(183, 147, 221, 30);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JLabel labelAskName = new JLabel("Enter your name:");
		labelAskName.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		labelAskName.setHorizontalAlignment(SwingConstants.CENTER);
		labelAskName.setBounds(183, 118, 221, 30);
		contentPanel.add(labelAskName);
		
		JButton btnNameOK = new JButton("Enter");
		btnNameOK.setFont(new Font("Trebuchet MS", Font.BOLD, 11));
		btnNameOK.setBackground(new Color(0, 0, 128));
		btnNameOK.setForeground(new Color(255, 255, 255));
		btnNameOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					userName = textField.getText();
					out.writeObject(textField.getText());
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnNameOK.setBounds(183, 188, 221, 30);
		contentPanel.add(btnNameOK);
		btnNameOK.setOpaque(true);
		
		JLabel lblLogo = new JLabel("LOGO");
		lblLogo.setIcon(new ImageIcon("img\\LogoLogin.png"));
		lblLogo.setBounds(259, 11, 70, 70);
		contentPanel.add(lblLogo);
		
		unavailableNameLabel = new JLabel("Name is already taken! Parang siya.");
		unavailableNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		unavailableNameLabel.setForeground(new Color(139, 0, 0));
		unavailableNameLabel.setBounds(183, 229, 221, 14);
		contentPanel.add(unavailableNameLabel);

		unavailableNameLabel.setVisible(isToShowUnavailable);
		this.setVisible(true);
	}

	public String getUserName() {
		return userName;
	}
}
