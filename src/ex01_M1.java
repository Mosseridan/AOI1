/**
 * 
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author daniel portnoy
 * @author idan mosseri
 *
 */
public class ex01_M1 {

	public static final String CURL_CMD_START = "curl -s -w \\n%{time_total} -o - ";

	public static String INPUT_URL = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		ParseArgs(args);

		double time = sendRequest(INPUT_URL);

		System.out.println(time);
	}

	public static double sendRequest(String url) {

		String curl_cmd = CURL_CMD_START + INPUT_URL;
		double res = 0d;
		double time = 0d;
		try {
			Process process = Runtime.getRuntime().exec(curl_cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			res = Double.parseDouble(stdInput.readLine());
			time = Double.parseDouble(stdInput.readLine());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return time;
	}

	private static void ParseArgs(String[] args) {

		INPUT_URL = args[0];
	}


}
