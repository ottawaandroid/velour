/*
 *  Part of velour - a collection of useful widgets
 *
 *  Copyright 2011 Don Kelly <karfai@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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
		mActivities.put("Panels Custom", CustomPanelsDemoActivity.class);
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
