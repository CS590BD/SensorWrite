package group.seven.sensorwrite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class HTTP {

	public static String post(String url, String message) 
			throws UnsupportedEncodingException, ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		StringEntity input = new StringEntity(message);
		//input.setContentType(contentType);
		post.setEntity(input);
		HttpResponse response = client.execute(post);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));	
		String line = "";
		while ((line += reader.readLine()) != null) {}
		return line;
	}
}
