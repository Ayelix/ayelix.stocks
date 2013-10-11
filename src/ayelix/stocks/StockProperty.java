package ayelix.stocks;

import android.content.Context;
import android.widget.TextView;

public class StockProperty {
	private String m_propertyName;
	private String m_labelText;
	private Context m_context;
	private TextView m_view;

	public StockProperty(String propertyName, String labelText, Context context) {
		m_propertyName = propertyName;
		m_labelText = labelText;
		m_context = context;
	}

	public String getPropertyName() {
		return m_propertyName;
	}
	
	public String getLabelText() {
		return m_labelText;
	}
	
	public TextView getView() {
		if (null == m_view) {
			m_view = new TextView(m_context);
		}
		return m_view;
	}
	
	public void setNextValue(String value) {
		m_nextValue = value;
	}
	
	public void updateValue() {
		m_view.setText(m_nextValue);
	}

	public void updateValue(String value) {
		m_view.setText(value);
	}
}
