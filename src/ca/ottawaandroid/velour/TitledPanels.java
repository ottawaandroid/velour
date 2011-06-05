package ca.ottawaandroid.velour;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitledPanels extends RelativeLayout {
	private Panels mPanels;
	private TextView mTextLeft;
	private TextView mTextRight;
	private int[] mTitleIds;

	public TitledPanels(Context ctx) {
		super(ctx);
	}
	
    public TitledPanels(Context ctx, AttributeSet as, int defStyle) {
        super(ctx, as, defStyle);
    }

    public TitledPanels(Context ctx, AttributeSet as) {
        super(ctx, as);
    }
    
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		build();
	}

	private ArrayList<View> extractChildren() {
		ArrayList<View> ch = new ArrayList<View>();
		final int cc = getChildCount();
		for ( int i = 0; i < cc; i++ ) {
			ch.add(getChildAt(i));
		}
		removeAllViews();
		return ch;
	}
	
	private void build() {
		final Context ctx = getContext();
		final ArrayList<View> children = extractChildren();

		inflateViews(ctx);
		
		for ( View v : children ) {
			addOldChild(ctx, v);
		}
	}

	private void addOldChild(final Context ctx, View v) {
		inflate(ctx, R.layout.titled_panel_content, mPanels);
		ViewGroup ctn = (ViewGroup) mPanels.getChildAt(mPanels.getChildCount() - 1);
		ctn.addView(v);
	}

	private void inflateViews(final Context ctx) {
		inflate(ctx, R.layout.titled_panel_panels, this);
		inflate(ctx, R.layout.titled_panel_text_left, this);
		inflate(ctx, R.layout.titled_panel_text_right, this);
		mPanels = (Panels) getChildAt(0);
		mTextLeft = (TextView) getChildAt(1);
		mTextRight = (TextView) getChildAt(2);
		mPanels.addListener(new Panels.Listener() {
			@Override
			public void onPanelPending(int panelIndex) {
				super.onPanelPending(panelIndex);
				updateText(panelIndex);
			}

			@Override
			public void onPanelChanged(int panelIndex) {
				super.onPanelChanged(panelIndex);
				updateText(panelIndex);
			}
		});
	}

	private void updateTextView(TextView v, int resId) {
		if ( resId > 0 ) {
			v.setText(resId);
		} else {
			v.setText("");
		}
	}
	private void updateText(int panelIndex) {
		if ( mTitleIds.length > 0 ) {
			updateTextView(mTextLeft, panelIndex > 0 ? mTitleIds[panelIndex - 1] : 0);
			updateTextView(mTextRight, panelIndex < mTitleIds.length - 1 ? mTitleIds[panelIndex + 1] : 0);
		}
	}

	public void setTitles(int[] titleIds) {
		mTitleIds = titleIds;
		updateTitles();
		updateText(mPanels.getCurrentPanel());
	}

	private void updateTitles() {
		for ( int i = 0; i < mTitleIds.length; i++ ) {
			updateTitle(i, mTitleIds[i]);
		}
	}

	private void updateTitle(int i, int resId) {
		ViewGroup v = (ViewGroup) mPanels.getChildAt(i);
		((TextView) v.getChildAt(0)).setText(resId);
	}
}
