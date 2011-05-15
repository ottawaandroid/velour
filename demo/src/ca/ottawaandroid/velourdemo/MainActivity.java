package ca.ottawaandroid.velourdemo;

import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity
{
	private static final String[] mNames = { "Panels", "Panels Custom" };
    private final HashMap<String, Class<?>> mActivities = new HashMap<String, Class<?>>();

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        buildActivities();
        setupList();
    }

	private void buildActivities() {
		mActivities.put("Panels", PanelsDemoActivity.class);
	}
	
	private void setupList() {
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main_list_item, mNames)); 

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String name = ((TextView) view).getText().toString();
			if ( mActivities.containsKey(name)) {
				startActivity(new Intent(MainActivity.this, mActivities.get(name)));
			} else {
				Toast.makeText(getApplicationContext(), "Not implemented", Toast.LENGTH_SHORT).show();
			}
          }
        });
	}
}
