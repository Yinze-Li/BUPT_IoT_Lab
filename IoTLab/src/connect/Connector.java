/**
 * 
 */
package connect;

import gnu.io.*;
import java.io.*;

/**
 * @author Yinze Li
 * @Date 2019年5月14日下午5:04:15
 */
public class Connector {
	static CommPortIdentifier portId;
	static CommPort com;
	static SerialPort ser;

	public Connector() {
		init();
	}

	private void init() {
		try {
			// TODO: identify the COM port from Windows' control panel
			portId = CommPortIdentifier.getPortIdentifier("COM5");
			com = portId.open("MCS51COM", 2000);
			ser = (SerialPort) com;
			// Baud rate = 9600, Data bits = 8, 1 stop bit, Parity OFF
			ser.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

//		try {
//			// Test TX: send out chars 'D', 'O', 'G', 'S'
//			OutputStream comOut = ser.getOutputStream();
//			comOut.write('D');
//            comOut.write('O');
//            comOut.write('G');
//            comOut.write('S');
//
//			// Test RX: display first 4 chars received
//			InputStream comIn = ser.getInputStream();
//            for (int i = 0; i < 4; i++){
//			// while (comIn.available() == 0);
//			byte c = (byte) comIn.read();
//                System.out.println(c);
//			 }
//
//			// close the streams
//			comIn.close();
//			comOut.close();
//		} catch (Exception e) {
//			e.printStackTrace(System.out);
//		}
//		// close the port
//		ser.close();
	}

	public void sendByte(char data) {
		try {
			System.out.println("send: " + data);
			OutputStream comOut = ser.getOutputStream();
			comOut.write(data);
			comOut.close();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public char receive() {
		char data = 0;
		try {
			InputStream comIn = ser.getInputStream();
			while (comIn.available() == 0)
				;
			data = (char) comIn.read();
			System.out.print(data);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return data;
	}

	public void close() {
		ser.close();
	}
}
