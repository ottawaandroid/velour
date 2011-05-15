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
