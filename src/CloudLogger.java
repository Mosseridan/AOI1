
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.util.List;
import java.util.ArrayList;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class CloudLogger {

	private static int MAX_LIST_SIZE = 10000;

	private List<List<String>> _logMessages;
	private String _url;
	private int _currList;

	public CloudLogger(String url) {
		_logMessages = new ArrayList<List<String>>(5);
		_logMessages.add(new ArrayList<String>(MAX_LIST_SIZE));
		_url = url;
		_currList=0;
	}

	public void println(String msg) {
		if(_logMessages.get(_currList).size() >= MAX_LIST_SIZE) {
			_logMessages.add(new ArrayList<String>(MAX_LIST_SIZE));
			_currList++;
		}

		_logMessages.get(_currList).add(msg);
	}

	public void sendAll() throws Exception {
		for (List<String> msglList: _logMessages) {
			String fullQuery = "";

			for (String msg : msglList) {
				fullQuery += msg + "\n";
			}

			sendMsg(fullQuery); 
		}
	}

	private void sendMsg(String fullQuery) throws Exception {
		URL myurl = new URL(_url);
		HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-length", String.valueOf(fullQuery.length())); 
		con.setRequestProperty("Content-Type","text/plain"); 
		con.setRequestProperty("User-Agent", "CloudLogger"); 
		con.setDoOutput(true); 
		con.setDoInput(true); 
		DataOutputStream output = new DataOutputStream(con.getOutputStream());  
		output.writeBytes(fullQuery);
		output.close();
		DataInputStream input = new DataInputStream( con.getInputStream() ); 
		for( int c = input.read(); c != -1; c = input.read() );
		input.close();
	}


	static {
		disableSslVerification();
	}

	private static void disableSslVerification() {
		try
		{
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[]  certs, String authType) throws CertificateException {
					// TODO Auto-generated method stub
				}
				@Override
				public void checkServerTrusted(X509Certificate[]  certs, String authType) throws CertificateException {
					// TODO Auto-generated method stub
				}
			}
			};

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
}