package ca.strangeware.chores;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class Panels extends ViewGroup {
	private static final int INVALID_PANEL = -1;
	// TODO: make configurable
	private static final int FLING_VELOCITY = 1000;
	private Scroller mScroller;
	private int mCurrent;
	private int mNext;
	private int mCurrentScrollX;
	private boolean mFirstLayout = true;

	public Panels(Context ctx) {
		super(ctx);
	}

	public Panels(Context ctx, AttributeSet as, int defStyle) {
		super(ctx, as, defStyle);
		setup();
	}

	public Panels(Context ctx, AttributeSet as) {
		super(ctx, as);
		setup();
	}
	
	private void setup() {
		mScroller = new Scroller(getContext());
		mCurrent = getDefaultPanel();
	}

	// TODO: configurable
	protected int getDefaultPanel() {
		return 0;
	}

	// config - override in derived classes or XML
	protected int getTouchFuzz() {
		return 0;
	}

	private boolean isNextValid() {
		return INVALID_PANEL != mNext;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layoutAllChildren();			
		setInitialPosition();
	}

	private void layoutAllChildren() {
		final int c = getChildCount();
		int left = 0;
		for ( int i = 0; i < c; i++ ) {
			final View ch = getChildAt(i);
			if ( View.GONE != ch.getVisibility() ) {
				final int w = ch.getMeasuredWidth();
				ch.layout(left, 0, left + w, ch.getMeasuredHeight());
				left += w;
			}
		}
	}

	private void setInitialPosition() {
		if ( mFirstLayout ) {
			scrollTo(mCurrent * getMeasuredWidth(), 0);
			mFirstLayout = false;
		}
	}
	
	@Override
	public void computeScroll() {
		final int sx = getScrollX();
		final int sy = getScrollY();
		if ( mScroller.computeScrollOffset() ) {
			int cx = mScroller.getCurrX();
			int cy = mScroller.getCurrY();
			
			if ( cx != sx || cy != sy ){
				scrollTo(cx, cy);
			} else {
				invalidate();
			}
		} else if ( isNextValid() ) {
			mCurrent = Math.max(0, Math.min(mNext, getChildCount() - 1));
			mNext = INVALID_PANEL;

			mDrawState = mNeutralDrawState;
			clearChildrenCache();

			mCurrentScrollX = mCurrent * getWidth();
			if ( sx != mCurrentScrollX ) {
				scrollTo(mCurrentScrollX, sy);
			}	
		}
	}

	private void changeCache(boolean enabled) {
		int c = getChildCount();
		for ( int i = 0; i < c; i++ ) {
			View v = getChildAt(i);
			v.setDrawingCacheEnabled(enabled);
			if ( v instanceof ViewGroup ) {
				((ViewGroup) v).setAlwaysDrawnWithCacheEnabled(enabled);
			}
		}
	}
	
	private void enableChildrenCache() {
		changeCache(true);
	}

	private void clearChildrenCache() {
		changeCache(false);
	}

	private abstract class DrawingState {
		private boolean drawExclusiveCurrent(Canvas can) {
			boolean drew = onlyDrawCurrent();
			if ( drew ) {
				drawCurrent(can);
			}
			return drew;
		}

		private void drawCurrent(Canvas can) {
			drawChild(can, getChildAt(mCurrent), getDrawingTime());
		}
		
		private boolean drawFling(Canvas can) {
			boolean drew = isFling();
			if ( drew ) {
				drawCurrent(can);
				drawNext(can);
			}
			return drew;
		}

		private void drawFullScroll(Canvas can) {
			drawAll(can);
			drawPotentialWrappedViewToCache(can);
		}

		private void drawAll(Canvas can) {
			for (int i = 0; i < getChildCount(); i++) {
				drawChild(can, getChildAt(i), getDrawingTime());
			}
		}
		
		private boolean isFling() {
			return (mNext >= 0
					&& mNext < getChildCount()
					&& (Math.abs(mCurrent - mNext) == 1 || inMotion()));
		}

		private boolean onlyDrawCurrent() {
			return (mState.shouldDrawCurrentChild() && mNext == INVALID_PANEL);
		}
		
		abstract protected void drawPotentialWrappedViewToCache(Canvas can);
		abstract protected void drawNext(Canvas can);
		abstract protected boolean inMotion();

		public void draw(Canvas can) {
			if ( !drawExclusiveCurrent(can) ) {
				if ( !drawFling(can) ) {
					drawFullScroll(can);
				}
			}			
		}
	}

	private class NeutralDrawingState extends DrawingState {
		@Override
		protected void drawPotentialWrappedViewToCache(Canvas can) {
			// nothing will wrap since we're not in motion
		}

		@Override
		protected void drawNext(Canvas can) {
			drawChild(can, getChildAt(mNext), getDrawingTime());
		}

		@Override
		protected boolean inMotion() {
			return false;
		}
	}
	
	private abstract class InMotionDrawingState extends DrawingState {
		protected abstract View getWrappingNext();
		protected abstract int getWidthGivenNext(View nv);
		
		private void drawToCache(Canvas can, final View nv) {
			can.drawBitmap(nv.getDrawingCache(), getWidthGivenNext(nv), nv.getTop(), new Paint());
		}

		@Override
		protected void drawPotentialWrappedViewToCache(Canvas can) {
			drawToCache(can, getWrappingNext());
		}

		@Override
		protected void drawNext(Canvas can) {
			drawToCache(can, getChildAt(mNext));
		}

		@Override
		protected boolean inMotion() {
			return true;
		}
	}
	
	class LeftMotionDrawingState extends InMotionDrawingState {
		protected View getWrappingNext() {
			return getChildAt(getChildCount() - 1);
		}
		
		protected int getWidthGivenNext(View nv) {
			return -nv.getWidth();
		}
	}
	
	class RightMotionDrawingState extends InMotionDrawingState {
		protected View getWrappingNext() {
			return getChildAt(0);
		}
		
		protected int getWidthGivenNext(View nv) {
			return getWidth() - getChildCount();
		}
	}
	
	private final NeutralDrawingState mNeutralDrawState = new NeutralDrawingState();
	private final LeftMotionDrawingState mLeftDrawState = new LeftMotionDrawingState();
	private final RightMotionDrawingState mRightDrawState = new RightMotionDrawingState();

	private DrawingState mDrawState = mNeutralDrawState;

	@Override
	protected void dispatchDraw(Canvas can) {
		if ( getChildCount() > 0 ) {
			mDrawState.draw(can);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int cc = getChildCount();
		for (int i = 0; i < cc; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	public boolean requestChildRectangleOnScreen(View ch, Rect r, boolean im) {
		int pi = indexOfChild(ch);
		if (pi != mCurrent || !mScroller.isFinished()) {
			scrollByPanel(pi);
			return true;
		}
		return false;
	}

	@Override
	protected boolean onRequestFocusInDescendants(int d, Rect pfr) {
		View v = getChildAt(isNextValid() ? mNext : mCurrent);
		if ( null != v ) {
			v.requestFocus(d, pfr);
		}
		return false;
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		boolean handled = false;
		int cpi = getCurrentPanel();
		int pi = 0;
		
		switch (direction) {
			case View.FOCUS_LEFT:
				handled = pi > 0;
				pi = cpi - 1;
				break;
				
			case View.FOCUS_RIGHT:
				handled = pi < getChildCount() - 1;
				pi = cpi + 1;
		}
		
		if ( handled ) {
			scrollByPanel(cpi);
		}

		if ( !handled ) {
			handled = super.dispatchUnhandledMove(focused, direction);
		}
		return handled;
	}

	private int getCurrentPanel() {
		return mCurrent;
	}

	private void scrollByCurrentPosition() {
		final int w = getWidth();
		final int sx = getScrollX();
		final int sw = sx + (w / 2);
		final int cc = getChildCount();
		int pi = - 1;
		
		if (sw > w * cc) {
			pi = cc;
		} else if ( sw > 0 ){
			pi = (sx + (w / 2)) / w;
		}
		scrollByPanel(pi);
	}

	
	public void scrollByPanel(int pi) {
		if ( mScroller.isFinished() ) {
			scrollByPanelFinishScrolling(pi);
		}
	}

	private int changePanel(int pi) {
		final int cc = getChildCount() - 1;
		final int requestedI = pi;

		// wrap around
		// TODO: needs to be configurable
		if (pi < 0) {
			pi = cc;
			mDrawState = mLeftDrawState;
		} else if (pi > cc) {
			pi = 0;
			mDrawState = mRightDrawState;
		} else {
			mDrawState = mNeutralDrawState;
		}
		
		return requestedI * getWidth();
	}	

	private void scrollByPanelFinishScrolling(int pi) {
		enableChildrenCache();
		
		final int nx = changePanel(pi);
		
		mNext = pi;

		clearFocus(pi);
		startScroll(nx);
		invalidate();
	}

	private void clearFocus(int pi) {
		final boolean changing = pi != mCurrent;
		View focusedChild = getFocusedChild();
		if (focusedChild != null
				&& changing
				&& focusedChild == getChildAt(mCurrent)) {
			focusedChild.clearFocus();
		}
	}

	private void startScroll(final int nx) {
		final int delta = nx - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
	}

	class State {
		protected VelocityTracker mTracker;

		private boolean hadLateralMotion(float x) {
			return ((int) Math.abs(x - mLastX) > getTouchFuzz());
		}
		
		public void trackMotion(MotionEvent e) {
			if (mTracker == null) {
				mTracker = VelocityTracker.obtain();
			}
			mTracker.addMovement(e);
		}
		
		public boolean shouldDrawCurrentChild() {
			return true;
		}

		public boolean intercepted() {
			return false;
		}

		public boolean interceptWithoutHandling(int action) {
			return false;
		}

		public State onActionDown(MotionEvent e) {
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			mLastX = e.getX();
			return this;
		}

		public State onActionUp(MotionEvent e) {
			if (mTracker != null) {
				mTracker.recycle();
				mTracker = null;
			}

			return mNeutralState;
		}

		public State onActionMove(MotionEvent e) {
			return mMotionState;			
		}

		public State onActionCancel(MotionEvent e) {
			return mNeutralState;
		}
		
		private State onInterceptActionDown(final float x, final float y) {
			mLastX = x;
//				mAllowLongPress = true;

			return mScroller.isFinished() ? mNeutralState : mMotionState;
		}

		private State onInterceptActionMove(final float x, final float y) {
			State rv = this;
			
			if ( hadLateralMotion(x) ) {
				rv = mMotionState;
				enableChildrenCache();
				// Either way, cancel any pending longpress
//					if (mAllowLongPress) {
//						mAllowLongPress = false;
					// Try canceling the long press. It could also have been
					// scheduled
					// by a distant descendant, so use the mAllowLongPress flag
					// to block
					// everything
//						final View currentScreen = getChildAt(mCurrent);
//						currentScreen.cancelLongPress();
//					}
			}
			
			return rv;
		}
	};
	
	class InMotion extends State {
		@Override
		public boolean interceptWithoutHandling(int action) {
			return (action == MotionEvent.ACTION_MOVE);
		}

		@Override
		public boolean intercepted() {
			return true;
		}

		@Override
		public boolean shouldDrawCurrentChild() {
			return false;
		}

		@Override
		public State onActionUp(MotionEvent e) {
			int vx = (int) mTracker.getXVelocity();

			if (vx > FLING_VELOCITY && mCurrent > 0) {
				scrollByPanel(mCurrent - 1);
			} else if (vx < -FLING_VELOCITY && mCurrent < getChildCount() - 1) {
				scrollByPanel(mCurrent + 1);
			} else {
				scrollByCurrentPosition();
			}

			return super.onActionUp(e);
		}

		@Override
		public State onActionMove(MotionEvent e) {
			final float x = e.getX();
			final int dx = (int) (mLastX - x);

			mLastX = x;

			if (dx < 0) {
				if (getScrollX() <= 0) {
					mDrawState = mLeftDrawState;
				}
				scrollBy(dx, 0);
			} else if (dx > 0) {
				int cr = getChildAt(getChildCount() - 1).getRight();
				int availableToScroll =	cr - getScrollX() - getWidth();
				if (availableToScroll <= 0) {
					mDrawState = mRightDrawState;
					availableToScroll += getWidth() * 2;
				}
				if (availableToScroll > 0) {
					scrollBy(Math.min(availableToScroll, dx), 0);
				}
			}

			return super.onActionMove(e);			
		}
	};
	
	State mNeutralState = new State();
	State mMotionState = new InMotion();

	State mState = mNeutralState;
	
	// shared b/w State instances 
	private float mLastX;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean intercept = false;
		if ( getChildCount() > 0 ) {
			final int action = ev.getAction();
			
			intercept = mState.interceptWithoutHandling(action);
			if ( !intercept ) {
				final float x = ev.getX();
				final float y = ev.getY();
		
				switch (action) {
					case MotionEvent.ACTION_MOVE:
						mState = mState.onInterceptActionMove(x, y);
						break;
			
					case MotionEvent.ACTION_DOWN:
						mState = mState.onInterceptActionDown(x, y);
						break;
			
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						mState = mNeutralState;
			//			mAllowLongPress = false;
						break;
				}
	
				intercept = mState.intercepted();
			}
		}		
		return intercept;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if ( getChildCount() > 0 ) {
			mState.trackMotion(e);
	
			switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mState = mState.onActionDown(e);
					break;
		
				case MotionEvent.ACTION_MOVE:
					mState = mState.onActionMove(e);
					break;
		
				case MotionEvent.ACTION_UP:
					mState = mState.onActionUp(e);
					break;
		
				case MotionEvent.ACTION_CANCEL:
					mState = mState.onActionCancel(e);
					break;
			}
		}
		
		return true;
	}
}
