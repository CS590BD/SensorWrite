package group.seven.sensorwrite;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class HttpAsyncTask extends AsyncTask<String, Integer, String> {
	
	public IHttpAsyncResponse delegate = null;
	
	private Context context;
	private String url;
	private String message;
	private String data; //store http response here
	
	public HttpAsyncTask(Context context, String url) {
		this.context = context;
		this.url = url;
		this.message = "";
	}

	/**
	 * String... params is the param in the AsyncTask.execute(string) method called by another class to get this going
	 */
	@Override
	protected String doInBackground(String... params) {
		String method = params[0];
		if(method.equalsIgnoreCase("get")) {
			httpGet();
		} else if (method.equalsIgnoreCase("post")) {
			httpPost(params[1]);
		}
		return data;
	}
	
	@Override
	protected void onPreExecute() {
		//do nothing
	}

	/**
	 * result = whatever is returned from doInBackground method
	 */
	@Override
	protected void onPostExecute(String result){
		//pb.setVisibility(View.GONE);
		if(delegate != null) {
			delegate.processFinish(result);
		}
		if(message.length() > 0) {
			toast();
		}
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress){
		//pb.setProgress(progress[0]);
	}
	
	/**
	 * GET
	 */
	private void httpGet() {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			data = new BasicResponseHandler().handleResponse(response);
			message = "";
		} catch (ClientProtocolException e) {
			message = "ClientProtocolException";
		} catch (IOException e) {
			message = "IOException";
		}
	}

	/**
	 * POST
	 * @param data
	 */
	private void httpPost(String data) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			StringEntity entity = new StringEntity(data);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			data = new BasicResponseHandler().handleResponse(response);
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
}
