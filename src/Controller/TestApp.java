package Controller;

import QRCode.QRCode;

public class TestApp {
	
	public static void main(String[] args) {
		QRCode qrCode = new QRCode(1);
		String hw = "HELLO WORLD";
		
		String qrMode = qrCode.getMode();
		String cci = qrCode.getCharacterCountIndicator(hw);
		String encodedMsg = qrCode.encodeMessage(hw);
		
		String encodedString = qrMode + cci + encodedMsg;
		encodedString = qrCode.addPadBytes(encodedString);
		
		String encodedStringHelloWorld = "0101110011111100110000011100101101111010100010000001001000100001101001011110001100110110001011000100001101000010011101101011100111111001000101110101010100000111001110011001110101010011110110111011011101000111";
		
		System.out.println(encodedStringHelloWorld);
		qrCode.fillData(encodedStringHelloWorld);
		qrCode.applyMask(6);
		qrCode.displayQRCode();
		System.out.println(qrCode.readData());
		qrCode.applyMask(6);
		System.out.println(qrCode.readData());
	}

}
