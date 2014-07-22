package group.seven.sensorwrite;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class HttpAsyncTask extends AsyncTask<String, Integer, Double>{
	
	private Context context;
	private String url;
	private String message = "";
	private String data; //store http response here
	
	public HttpAsyncTask(Context context, String url) {
		this.context = context;
		this.url = url;
	}

	/**
	 * String... params is the param in the AsyncTask.execute(string) method called by another class to get this going
	 */
	@Override
	protected Double doInBackground(String... params) {
		String method = params[0];
		if(method.equalsIgnoreCase("get")) {
			httpGet();
		} else if (method.equalsIgnoreCase("post")) {
			httpPost(params[1]);
		}
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		//do nothing
	}

	@Override
	protected void onPostExecute(Double result){
		//pb.setVisibility(View.GONE);
		toast();
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress){
		//pb.setProgress(progress[0]);
	}
	
	private void httpGet() {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			data = response.toString();
			message = "Data retrieved.";
		} catch (ClientProtocolException e) {
			message = "ClientProtocolException";
		} catch (IOException e) {
			message = "IOException";
		}
	}

	private void httpPost(String data) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			StringEntity entity = new StringEntity(data);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			message = "Saved.";
		} catch (ClientProtocolException e) {
			message = "ClientProtocolException";
		} catch (IOException e) {
			message = "IOException";
		}
	}
	private void toast() {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	public String getData() {
		return data;
	}
}
