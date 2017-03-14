package QRCode;

public class QRCode {
	String NUMERIC_MODE = "0001";
	String ALPHANUMERIC_MODE = "0010";
	String BYTE_MODE = "0100";
	String KANJI_MODE = "1000";
	String ECI_MODE = "0111";
	
	int size = 21;
	int version = 1;
	String mode = ALPHANUMERIC_MODE;
	int[][] qrCode;
	
	/**
	 * @param length
	 */
	public QRCode(int version) {
		this.size = 21+(version-1)*4;
		this.version = version;
		qrCode = new int[size][size];
		templateQRCode();
	}
	
	public void templateQRCode() {
		for(int i = 0; i < size; i++){
	        for(int j = 0; j < size; j++){
	        	if((i == 6 && j%2 == 0) || (j == 6 && i%2 == 0)) qrCode[i][j] = 1;
	        	if((i == 3 && (j == 3 || j == size-4)) || (i == size-4 && j == 3)) generateSeparator(i,j);	        	
	        }
	    }
		qrCode[4*version + 9][8] = 1;
    	int[] coordinatesOfAllignments = getCoordinatesOfAllignments();
		for(int i : coordinatesOfAllignments)
			for(int j : coordinatesOfAllignments)
				genegareAlignment(i, j);
	}
	
	public void generateSeparator(int i,int j) {
		for(int k = i-1; k < i+2; k++)
			for(int n = j-1; n < j+2; n++)
				qrCode[k][n] = 1;
		
		for(int k = i-3; k < i+4; k++) {
			qrCode[k][j+3] = 1;
			qrCode[k][j-3] = 1;
		}
		
		for(int k = j-3; k < j+4; k++) {
			qrCode[i-3][k] = 1;
			qrCode[i+3][k] = 1;
		}		
	}
	
	public void genegareAlignment(int i, int j) {
		if(!(((i < 10 || i > (size-10)) && j < 10) || (j > (size-10) && i < 10))) {
			qrCode[i][j] = 1;
			for(int k = j-2; k < j+3; k++) {
				qrCode[i-2][k] = 1;
				qrCode[i+2][k] = 1;
			}
			for(int k = i-2; k < i+3; k++) {
				qrCode[k][j+2] = 1;
				qrCode[k][j-2] = 1;
			}
		}
	}

	public int[] getCoordinatesOfAllignments() {
        // there is an error in 32 level in this algorithm (right coordinates are: 6,34,60,86,112,138)
		
		if (version <= 1) return new int[0];
        int num = (version / 7) + 2; //number of coordinates to return
        int[] result = new int[num];
        result[0] = 6;
        if (num == 1) return result;
        
        result[num - 1] = 4 * version + 10;
        
        if (num == 2) return result;
        
        // leave these brackets alone, because of integer division they ensure you get a number that's divisible by 2
        result[num - 2] = 2 * ((result[0] + result[num - 1] * (num - 2)) / ((num - 1) * 2)); 
        
        if (num == 3) return result;
        
        int step = result[num - 1] - result[num - 2];
        for (int i = num - 3; i > 0; i--) result[i] = result[i + 1] - step;
        return result;
	}
	
	public String getCharacterCountIndicator(String message) {
		int valCCI = 0;
		if(version <= 9) {
			valCCI = mode == NUMERIC_MODE ? 10 :
						mode == ALPHANUMERIC_MODE ? 9 :
							mode == BYTE_MODE ? 8 :
								mode == KANJI_MODE ? 8 : 10;
		}
		
		if(version > 9 && version <= 26) {
			valCCI = mode == NUMERIC_MODE ? 12 :
						mode == ALPHANUMERIC_MODE ? 11 :
							mode == BYTE_MODE ? 16 :
								mode == KANJI_MODE ? 10 : 10;
		}

		if(version > 26 && version <= 40) {
			valCCI = mode == NUMERIC_MODE ? 14 :
						mode == ALPHANUMERIC_MODE ? 13 :
							mode == BYTE_MODE ? 16 :
								mode == KANJI_MODE ? 12 : 12;
		}
		String res = String.format("%"+valCCI+"s", Integer.toBinaryString(message.length())).replace(' ', '0');
		return res;
	}
	
	public String encodeMessage(String message) {
		String resString = "";
		char[] hwChars = message.toCharArray();
		for (int i = 1; i < hwChars.length; i++) {
			if(i%2 == 1) {
				if((hwChars[i-1]-55) == -23) hwChars[i-1] = 36+55; 
				if((hwChars[i]-55) == -23) hwChars[i] = 36+55;
				int res = (hwChars[i-1]-55)*45 + (hwChars[i]-55);
				resString += String.format("%11s", Integer.toBinaryString(res)).replace(' ', '0');
			}
			
		}
		
		if(hwChars.length%2 != 0) {
			resString += String.format("%6s", Integer.toBinaryString((int)(hwChars[hwChars.length-1])-55)).replace(' ', '0');
		}
		return resString;		
	}
	
	public String addPadBytes(String message) {
		String resString = message;
		if(message.length() <= 104) {
			if((message.length()+4) <= 104)
				resString += "0000";
			else if((message.length()+3) <= 104)
				resString += "000";
			else if((message.length()+2) <= 104)
				resString += "00";
			else if((message.length()+1) <= 104)
				resString += "0";
		}
		if(resString.length()%8 == 1) resString += "0";
		if(resString.length()%8 == 2) resString += "0";
		if(resString.length()%8 == 3) resString += "0";
		if(resString.length()%8 == 4) resString += "0";
		if(resString.length()%8 == 5) resString += "0";
		if(resString.length()%8 == 6) resString += "0";
		if(resString.length()%8 == 7) resString += "0";
		
		while(resString.length() < 104) {
			if(resString.length() < 104) resString += "11101100";
			if(resString.length() < 104) resString += "00010001";
		}
		
		return resString;
	}
	
	public int getValue(int i, int j) {
		return qrCode[i][j]; 
	}
	
	public void applyMask(int maskNum) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if(checkDataArea(i, j))
					switch(maskNum){
					case 0 : if((i+j)%2 == 0) qrCode[i][j] = qrCode[i][j] ^ 1;
					break;
					case 1 : if((i)%2 == 0) qrCode[i][j] = qrCode[i][j] ^ 1;
					break;
					case 2 : if((j)%2 == 0) qrCode[i][j] = qrCode[i][j] ^ 1;
					break;
					case 3 : if((i+j)%3 == 0) qrCode[i][j] = qrCode[i][j] ^ 1;
					break;
					case 4 : if(( Math.floor(i/2) + Math.floor(j/3) )%2 == 0) qrCode[i][j] = qrCode[i][j] ^ 1;
					break;
					case 5 : if((i*j)%2 + (i*j)%3 == 0) qrCode[i][j] = qrCode[i][j] ^ 1;
					break;
					case 6 : if(((i*j)%2 + (i*j)%3) % 2 == 0) qrCode[i][j] = qrCode[i][j] ^ 1;
					break;
					case 7 : if(((i+j)%2 + (i*j)%3) % 2 == 0) qrCode[i][j] = qrCode[i][j] ^ 1;
					break;
				}
			}
			
		}
	}

	public void fillData(String message) {
		int k = 0;
		for (int j = 20; j > 0; j--) {
			for (int i = 20; i >= 0; i--) {
				if(checkDataArea(i, j)) qrCode[i][j] = Integer.parseInt("" + message.charAt(k++));
				if(checkDataArea(i, (j-1))) qrCode[i][j-1] = Integer.parseInt("" + message.charAt(k++));
			}
			
			j = j-3;
			if(j+1 == 6){j--;}
			for (int i = 0; i < 21; i++) {
				if(checkDataArea(i, (j+1))) qrCode[i][j+1] = Integer.parseInt("" + message.charAt(k++));
				if(checkDataArea(i, j)) qrCode[i][j] = Integer.parseInt("" + message.charAt(k++));
			}
		}
	}
	
	public String readData() {
		String message = "";
		for (int j = 20; j > 0; j--) {
			for (int i = 20; i >= 0; i--) {
				if(checkDataArea(i, j)) message += qrCode[i][j];
				if(checkDataArea(i, (j-1))) message += qrCode[i][j-1];
			}
			
			j = j-3;
			if(j+1 == 6){j--;}
			for (int i = 0; i < 21; i++) {
				if(checkDataArea(i, (j+1)))  message += qrCode[i][j+1];
				if(checkDataArea(i, j))  message += qrCode[i][j];
			}
		}
		return message;
	}
	
	public void displayQRCode() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print("" + getValue(i, j) + " ");
			}
			System.out.println();
		}
	}
	
	public boolean checkDataArea(int i, int j) {
		if(i == 6 || j == 6) return false;
		if(i < 9 && (j < 9 || j > 12)) return false;
		if(i > 12 && j < 9) return false;
		return true;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
}