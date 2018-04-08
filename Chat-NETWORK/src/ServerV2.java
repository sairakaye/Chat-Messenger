package src;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServerV2 extends JFrame {

	private JPanel contentPane;
	private JTextField txtLocalhost;
	private JTextArea serverLog;
	private Server serverLogic;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    ServerV2 frame = new ServerV2();
		frame.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public ServerV2() {
		this.setTitle("The BuzzRoom's Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 832, 556);

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		serverLog = new JTextArea();
		serverLog.setFont(new Font("Calibri", Font.PLAIN, 12));
		serverLog.setBounds(10, 207, 414, 318);
		
		JScrollPane scrollPane = new JScrollPane(serverLog);
		scrollPane.setBounds(10, 139, 278, 358);
		contentPane.add(scrollPane);
		
		JLabel serverLogoLbl = new JLabel("Server logo");
		serverLogoLbl.setIcon(new ImageIcon("img\\Judy Ann.JPG"));
		serverLogoLbl.setBounds(292, 58, 510, 439);
		contentPane.add(serverLogoLbl);
		
		txtLocalhost = new JTextField();
		txtLocalhost.setHorizontalAlignment(SwingConstants.CENTER);
		txtLocalhost.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtLocalhost.setText("localhost");
		txtLocalhost.setBounds(10, 55, 278, 29);
		contentPane.add(txtLocalhost);
		txtLocalhost.setColumns(10);

		serverLogic = new Server(txtLocalhost.getText(), serverLog);

		JButton btnRun = new JButton("Run Server");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    serverLogic.startServer();
			}
		});
		btnRun.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnRun.setBackground(new Color(0, 0, 128));
		btnRun.setForeground(new Color(255, 255, 255));
		btnRun.setBounds(10, 97, 118, 29);
		contentPane.add(btnRun);

		JLabel lblServerIpAddress = new JLabel("Server IP Address:");
		lblServerIpAddress.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		lblServerIpAddress.setBounds(96, 13, 118, 29);
		contentPane.add(lblServerIpAddress);
		
		JButton btnStop = new JButton("Stop Server");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    serverLogic.stopServer();
			}
		});
		btnStop.setForeground(Color.WHITE);
		btnStop.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnStop.setBackground(new Color(0, 0, 128));
		btnStop.setBounds(170, 97, 118, 29);
		contentPane.add(btnStop);
	}
}
