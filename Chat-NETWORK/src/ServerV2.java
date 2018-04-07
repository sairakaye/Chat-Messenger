import java.awt.BorderLayout;
import java.awt.EventQueue;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ServerV2 extends JFrame {

	private JPanel contentPane;
	private JTextField txtLocalhost;
	private static JTextArea serverLog;
	private static boolean stopped;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
					ServerV2 frame = new ServerV2();
					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}

	/**
	 * Create the frame.
	 */
	public ServerV2() {
		this.setTitle("The BuzzRoom's Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 775);

		stopped = true;
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		serverLog = new JTextArea();
		serverLog.setFont(new Font("Calibri", Font.PLAIN, 12));
		serverLog.setBounds(10, 207, 414, 318);
		
		JScrollPane scrollPane = new JScrollPane(serverLog);
		scrollPane.setBounds(10, 352, 414, 373);
		contentPane.add(scrollPane);
		
		JLabel serverLogoLbl = new JLabel("Server logo");
		serverLogoLbl.setIcon(new ImageIcon("img\\LogoServer.png"));
		serverLogoLbl.setBounds(140, 54, 150, 150);
		contentPane.add(serverLogoLbl);
		
		JLabel lblNewLabel = new JLabel("The BuzzRoom's Server");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(new Color(0, 0, 128));
		lblNewLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 24));
		lblNewLabel.setBounds(10, 11, 414, 29);
		contentPane.add(lblNewLabel);
		
		txtLocalhost = new JTextField();
		txtLocalhost.setHorizontalAlignment(SwingConstants.CENTER);
		txtLocalhost.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtLocalhost.setText("localhost");
		txtLocalhost.setBounds(10, 239, 414, 29);
		contentPane.add(txtLocalhost);
		txtLocalhost.setColumns(10);
		
		JButton btnRun = new JButton("Run Server");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    stopped = false;
				serverLog.append("The server is running\n");
				btnRun.setEnabled(false);

			}
		});
		btnRun.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnRun.setBackground(new Color(0, 0, 128));
		btnRun.setForeground(new Color(255, 255, 255));
		btnRun.setBounds(10, 279, 189, 29);
		contentPane.add(btnRun);
		
		JLabel lblServerIpAddress = new JLabel("Server IP Address:");
		lblServerIpAddress.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		lblServerIpAddress.setBounds(161, 211, 118, 29);
		contentPane.add(lblServerIpAddress);
		
		JLabel lblUnavailable = new JLabel("Invalid IP Address!");
		lblUnavailable.setForeground(new Color(128, 0, 0));
		lblUnavailable.setHorizontalAlignment(SwingConstants.CENTER);
		lblUnavailable.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblUnavailable.setBounds(10, 319, 414, 22);
		contentPane.add(lblUnavailable);
		
		JButton btnStop = new JButton("Stop Server");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    if (!stopped) {
                    stopped = true;
                    serverLog.append("Server is stopped\n");
                    btnRun.setEnabled(true);
                }
                else
                    serverLog.append("Server is already stopped!\n");
			}
		});
		btnStop.setForeground(Color.WHITE);
		btnStop.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
		btnStop.setBackground(new Color(0, 0, 128));
		btnStop.setBounds(235, 279, 189, 29);
		contentPane.add(btnStop);
	}

}
