import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.util.List;
import java.util.ArrayList;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class CloudLogger {
    
    private List<String> _logMessages;
    private String _url;

    public CloudLogger(String url) {
        _logMessages = new ArrayList<String>(500);
        _url = url;
    }

    public void println(String msg) {
        _logMessages.add(msg);
    }

    public void sendAll() throws Exception {
        String fullQuery = "";
        for (String msg : _logMessages) {
            fullQuery += msg + "\n";
        }
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
}