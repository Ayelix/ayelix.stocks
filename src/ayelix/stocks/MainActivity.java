package ayelix.stocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private static final String BASE_URL = "http://www.google.com/finance?q=";

	private EditText m_searchEditText;
	private Button m_goButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get the views
		m_searchEditText = (EditText) findViewById(R.id.searchEditText);
		m_goButton = (Button) findViewById(R.id.goButton);

		// Create the listener for the search box "Go" action
		m_searchEditText
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						boolean handled = false;
						// The GO action will start a search
						if (EditorInfo.IME_ACTION_GO == actionId) {
							search(m_searchEditText.getText().toString());
							handled = true;
						}
						return handled;
					}
				});

		// Create the listener for the Go button
		m_goButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				search(m_searchEditText.getText().toString());
			}
		});

	} // End method onCreate()

	/**
	 * Starts a search for the given string.
	 * 
	 * @param searchString
	 *            The string to search for (stock symbol or company name).
	 */
	private void search(final String searchString) {
		Log.d(TAG, "Searching string: " + searchString);
		new HTTPTask().execute(searchString);
	} // End method search()

	/**
	 * AsyncTask to execute the HTTP operations.
	 */
	private class HTTPTask extends AsyncTask<String, Void, BufferedReader> {

		@Override
		protected BufferedReader doInBackground(String... params) {
			BufferedReader retVal = null;

			// Get the HTTP client
			HttpClient client = new DefaultHttpClient();

			// Build the request by appending the query to the URL
			HttpGet request = new HttpGet(BASE_URL + params[0]);

			try {
				// Get the response
				HttpResponse response = client.execute(request);
				retVal = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return retVal;

		} // End method doInBackground()

		@Override
		protected void onPostExecute(BufferedReader result) {
			if (result != null) {
				// Start the Parser with the resulting reader
				new ParserTask().execute(result);
			}
		} // End method onPostExecute()

	} // End class HTTPTask

	/**
	 * AsyncTask to parse HTTP results.
	 */
	private class ParserTask extends AsyncTask<BufferedReader, String, Void> {

		@Override
		protected Void doInBackground(BufferedReader... params) {
			// TODO Auto-generated method stub
			return null;
		} // End method doInBackground()

	} // End class ParserTask

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
