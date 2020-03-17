package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import connect.Connector;
import javax.swing.*;

public class MainGUI implements ActionListener {

	private JFrame frame;
	private JTextField thLable;
	private JLabel duraLable;
	private JTextField duraText;
	private JButton submitDura;
	private JLabel tWarnLabel;
	private JTextField tWarnText;
	private JButton subTWarn;
	private JLabel hWarnLabel;
	private JTextField hWarnText;
	private JButton subHWarn;
	private JTextArea textArea;
	private JScrollPane jsp;
	Connector con;

	private int temp;
	private int hum;
	private int duration;
	private int tWarn;
	private int hWarn;
	boolean close;

	BufferedWriter bw;
	File log;

	private void setup() {

		close = false;
		temp = 0;
		hum = 0;
		duration = 5;
		tWarn = 100;
		hWarn = 100;
		frame = new JFrame("iot");
		frame.setLayout(new GridLayout(11, 1));

		thLable = new JTextField("temp: " + temp + "   hum: " + hum + "   duration: " + duration
				+ "s   warning temp: " + tWarn + "   warning humidity: " + hWarn);
		frame.add(thLable);

		duraLable = new JLabel("Please set the duration:");
		frame.add(duraLable);

		duraText = new JTextField();
		frame.add(duraText);

		submitDura = new JButton("submit");
		submitDura.addActionListener(this);
		frame.add(submitDura);

		tWarnLabel = new JLabel("Please set the warning temp:");
		frame.add(tWarnLabel);

		tWarnText = new JTextField();
		frame.add(tWarnText);

		subTWarn = new JButton("submit");
		subTWarn.addActionListener(this);
		frame.add(subTWarn);

		hWarnLabel = new JLabel("Please set the warning humidity");
		frame.add(hWarnLabel);

		hWarnText = new JTextField();
		frame.add(hWarnText);

		subHWarn = new JButton("submit");
		subHWarn.addActionListener(this);
		frame.add(subHWarn);

		textArea = new JTextArea("Serail Log\n");
		textArea.setLineWrap(true);
		jsp = new JScrollPane(textArea);
		frame.add(jsp);

		frame.setSize(600, 600);
		frame.setVisible(true);

		frame.addWindowListener(new WindowListener() {

			public void windowActivated(WindowEvent arg0) {
			}

			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("Closing the serial port...\nClosing the bufferwritter...");
				try {
					close = true;
					con.close();
					bw.close();
				} catch (Exception e) {

				}

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});

		File log = new File("log.csv");
		try {
			bw = new BufferedWriter(new FileWriter(log, false));
			bw.write("temp,hum,duration,warning temp,warning humidity");
			bw.newLine();
			con = new Connector();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		run();
	}

	private void run() {

		while (true) {
			char first = con.receive();
			textArea.append("" + first);
			if (first == 't') {
				char second = con.receive();
				textArea.append("" + second);
				char third = con.receive();
				textArea.append("" + third);
				if (second >= '0' && second <= '9' && third >= '0' && third <= '9') {
					String t = "" + second + third;
					try {
						temp = Integer.parseInt(t);
						thLable.setText("temp: " + temp + "   hum: " + hum + "   duration: " + duration + "s   warning temp: "
								+ tWarn + "   warning humidity: " + hWarn);
						bw.write(temp + "," + hum + "," + duration + "," + tWarn + "," + hWarn + ",\n");
					} catch (Exception e) {
					}
				}

			} else if (first == 'h') {
				char second = con.receive();
				textArea.append("" + second);
				char third = con.receive();
				textArea.append("" + third);
				if (second >= '0' && second <= '9' && third >= '0' && third <= '9') {
					String h = "" + second + third;
					try {
						hum = Integer.parseInt(h);
						thLable.setText("temp: " + temp + "   hum: " + hum + "   duration: " + duration + "s   warning temp: "
								+ tWarn + "   warning humidity: " + hWarn);
						bw.write(temp + "," + hum + "," + duration + "," + tWarn + "," + hWarn + ",\n");
					} catch (Exception e) {
					}
				}
			}
			
			if(close) {
				break;
			}
			
		}

	}


	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == submitDura) {
			try {
				String s = duraText.getText();
				if (s.length() == 0 || s.length() > 2) {
					duraText.setText("");
					return;
				} else if (s.length() == 1) {
					s = "0" + s;
				}
				duration = Integer.parseInt(s);
				con.sendByte('d');
				for (int i = 0; i < s.length(); i++) {
					con.sendByte(s.charAt(i));
				}
				thLable.setText("temp: " + temp + "   hum: " + hum + "   duration: " + duration + "s   warning temp: "
						+ tWarn + "   warning humidity: " + hWarn);
			} catch (Exception e1) {
				duraText.setText("");
			}
		} else if (e.getSource() == subTWarn) {
			try {
				String s = tWarnText.getText();
				if (s.length() == 0 || s.length() > 2) {
					tWarnText.setText("");
					return;
				} else if (s.length() == 1) {
					s = "0" + s;
				}
				tWarn = Integer.parseInt(s);
				con.sendByte('t');
				for (int i = 0; i < s.length(); i++) {
					con.sendByte(s.charAt(i));
				}
				thLable.setText("temp: " + temp + "   hum: " + hum + "   duration: " + duration + "s   warning temp: "
						+ tWarn + "   warning humidity: " + hWarn);
			} catch (Exception e1) {
				tWarnText.setText("");
			}
		} else if (e.getSource() == subHWarn) {
			try {
				String s = hWarnText.getText();
				if (s.length() == 0 || s.length() > 2) {
					hWarnText.setText("");
					return;
				} else if (s.length() == 1) {
					s = "0" + s;
				}
				hWarn = Integer.parseInt(s);
				con.sendByte('h');
				for (int i = 0; i < s.length(); i++) {
					con.sendByte(s.charAt(i));
				}
				thLable.setText("temp: " + temp + "   hum: " + hum + "   duration: " + duration + "s   warning temp: "
						+ tWarn + "   warning humidity: " + hWarn);
			} catch (Exception e1) {
				hWarnText.setText("");
			}
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainGUI m = new MainGUI();
		m.setup();
	}

}
