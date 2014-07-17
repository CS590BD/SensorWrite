package group.seven.sensorwrite;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class HttpAsyncTask extends AsyncTask<String, Integer, Double>{
	
	private Context context;
	private String url;
	
	public HttpAsyncTask(Context context, String url) {
		this.context = context;
		this.url = url;
	}

	/**
	 * String... params is the param in the AsyncTask.execute(string) method called by another class to get this going
	 */
	@Override
	protected Double doInBackground(String... params) {
		// TODO Auto-generated method stub
		post(params[0]);
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		Toast.makeText(context, "Saving...", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPostExecute(Double result){
		//pb.setVisibility(View.GONE);
		Toast.makeText(context, "Saved.", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress){
		//pb.setProgress(progress[0]);
	}

	private void post(String data) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			StringEntity entity = new StringEntity(data);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
}
