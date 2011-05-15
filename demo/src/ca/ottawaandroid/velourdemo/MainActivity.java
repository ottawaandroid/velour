package ca.ottawaandroid.velourdemo;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity
{
    private static final String[] WIDGETS = {"Panels"};

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.main_list_item, WIDGETS));

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(
            		getApplicationContext(), ((TextView) view).getText(),
            		Toast.LENGTH_SHORT).show();
          }
        });
    }
}
