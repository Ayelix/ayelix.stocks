package ayelix.stocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	/** URL to which a stock query is appended. */
	private static final String BASE_URL = "http://www.google.com/finance?q=";

	/** Main activity layout. */
	private LinearLayout m_resultsLayout;
	/** EditText for search query input. */
	private EditText m_searchEditText;
	/** Button to start a search. */
	private Button m_goButton;
	// TextViews for results.
	private TextView nameTextView, priceTextView;

	/** List of properties to find and display. */
	private final List<StockProperty> propertyList = Arrays.asList(
	/** Company name */
	new StockProperty("name", "Name", this),
	/** Stock exchange for this stock */
	new StockProperty("exchange", "Exchange", this),
	/** Stock price */
	new StockProperty("price", "Price", this),
	/** Currency being used */
	new StockProperty("priceCurrency", "Currency", this),
	/** Price change in currency */
	new StockProperty("priceChange", "Change", this),
	/** Price change in percent */
	new StockProperty("priceChangePercent", "Change (%)", this),
	/** Source of provided data */
	new StockProperty("dataSource", "Data Source", this));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get the pre-created views
		m_resultsLayout = (LinearLayout) findViewById(R.id.resultsLayout);
		m_searchEditText = (EditText) findViewById(R.id.searchEditText);
		m_goButton = (Button) findViewById(R.id.goButton);

		// Create and add the views for each property
		createAndAddViews();

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
	 * Creates and adds rows in the UI for each parameter (label and value).
	 */
	private void createAndAddViews() {
		// Create the layout parameters for each field
		final LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
				0, LayoutParams.WRAP_CONTENT, 0.3f);
		labelParams.gravity = Gravity.RIGHT;
		labelParams.setMargins(0, 25, 0, 0);
		final LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
				0, LayoutParams.WRAP_CONTENT, 0.7f);
		valueParams.gravity = Gravity.LEFT;
		valueParams.setMargins(0, 25, 0, 0);

		// Add a layout and text views for each property
		for (final StockProperty property : propertyList) {
			Log.d(TAG, "Adding row for property: " + property.getPropertyName());

			// Create a horizontal layout for the label and value
			final LinearLayout layout = new LinearLayout(this);
			layout.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			// Create a TextView for the label
			final TextView label = new TextView(this);
			label.setLayoutParams(labelParams);
			label.setText(property.getLabelText());
			label.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			layout.addView(label);

			// Configure and add the value TextView (created when the property
			// was constructed)
			final TextView value = property.getView();
			value.setLayoutParams(valueParams);
			value.setHint("None");
			value.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			layout.addView(value);

			// Add the row to the main layout
			m_resultsLayout.addView(layout);
		}
	}

	/**
	 * AsyncTask to execute the HTTP operations.
	 */
	private class HTTPTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "HTTPTask requesting " + params[0]);

			String retVal = null;

			// Get the HTTP client
			final HttpClient client = new DefaultHttpClient();

			// Build the request by appending the query to the URL
			final HttpGet request = new HttpGet(BASE_URL + params[0]);

			try {
				// Get the response
				HttpResponse response = client.execute(request);
				BufferedReader responseReader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				// Get the results as a string
				String line = new String();
				StringBuilder sb = new StringBuilder();
				while ((line = responseReader.readLine()) != null) {
					sb.append(line + "\n");
				}
				retVal = sb.toString();

				responseReader.close();

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Return the response
			return retVal;

		} // End method doInBackground()

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				// Start a Parser with the results
				if (!result.equals("") && (null != result)) {
					new ParserTask().execute(result);
					Log.d(TAG, "Request complete, ParserTask started.");
				} else {
					Log.e(TAG, "ParserTask not started: null or empty string.");
				}
			}
		} // End method onPostExecute()

	} // End class HTTPTask

	/**
	 * AsyncTask to parse HTTP results.
	 */
	private class ParserTask extends AsyncTask<String, StockProperty, Void> {
		@Override
		protected Void doInBackground(String... params) {
			Log.d(TAG, "ParserTask parsing results.");

			// Get a Jsoup document for the string
			final Document doc = Jsoup.parse(params[0]);

			// Get all the meta elements from the document
			final Elements metaElements = doc.getElementsByTag("meta");
			
			// String to be displayed for the first error
			String errorValue = "Not found";

			// Parse the value for each property
			for (final StockProperty property : propertyList) {
				// Select the Element(s) containing the current property
				final Elements currentElements = metaElements
						.select("[itemprop=" + property.getPropertyName() + "]");

				// Make sure there is at least one result
				if (!currentElements.isEmpty()) {
					// Get the value from the first element (we'll just assume
					// the first one is the right one).
					final String value = currentElements.first()
							.attr("content");

					// Set the StockProperty value to the parsed value
					property.setNextValue(value);

				} else {
					// Set the value to something indicating an error
					property.setNextValue(errorValue);
					errorValue = "";
				}

				// Publish the values as they are parsed
				publishProgress(property);
			}

			return null;
		} // End method doInBackground()

		@Override
		protected void onProgressUpdate(StockProperty... properties) {
			// Update the value for any properties provided
			for (final StockProperty property : properties) {
				property.updateValue();
			}
		}

	} // End class ParserTask

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
