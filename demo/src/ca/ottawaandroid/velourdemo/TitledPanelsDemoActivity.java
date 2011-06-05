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

import ca.ottawaandroid.velour.TitledPanels;
import android.app.Activity;
import android.os.Bundle;

public class TitledPanelsDemoActivity extends Activity
{
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        int[] titleIds = { R.string.title0, R.string.title1, R.string.title2 };
        setContentView(R.layout.titled_panels_demo);
        TitledPanels pnls = (TitledPanels) findViewById(R.id.panels);
        pnls.setTitles(titleIds);
    }
}
