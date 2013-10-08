package ayelix.stocks;

import android.app.Activity;
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
	
	private EditText searchEditText;
	private Button goButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get the views
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		goButton = (Button) findViewById(R.id.goButton);

		// Create the listener for the search box "Go" action
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				// The GO action will start a search
				if (EditorInfo.IME_ACTION_GO == actionId) {
					search(searchEditText.getText().toString());
					handled = true;
				}
				return handled;
			}
		});
		
		// Create the listener for the Go button
		goButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				search(searchEditText.getText().toString());
			}
		});

	}
	
	/**
	 * Starts a search for the given string.
	 * @param searchString The string to search for (stock symbol or company name).
	 */
	private void search(final String searchString) {
		Log.d(TAG, "Searching string: " + searchString);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
