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

import android.content.Context;
import android.util.AttributeSet;
import ca.ottawaandroid.velour.Panels;

public class CustomPanels extends Panels {
	public CustomPanels(Context ctx) {
		super(ctx);
	}

	public CustomPanels(Context ctx, AttributeSet as, int defStyle) {
		super(ctx, as, defStyle);
	}

	public CustomPanels(Context ctx, AttributeSet as) {
		super(ctx, as);
	}

	@Override
	protected boolean isWrappingPermitted() {
		return false;
	}

	@Override
	protected int getFlingVelocity() {
		return 500;
	}
}
