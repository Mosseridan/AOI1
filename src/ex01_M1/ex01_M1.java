/**
 * 
 */
package ex01_M1;

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
	public static void main(String[] args) {

		ParseArgs(args);

		String time = sendRequest(INPUT_URL);

		System.out.println(time);
	}

	public static String sendRequest(String url) {

		String curl_cmd = CURL_CMD_START + INPUT_URL;
		String res = "";
		String time = "";
		try {
			Process process = Runtime.getRuntime().exec(curl_cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			res = stdInput.readLine();
			time = stdInput.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return time;
	}

	private static void ParseArgs(String[] args) {

		INPUT_URL = args[0];
	}


}
