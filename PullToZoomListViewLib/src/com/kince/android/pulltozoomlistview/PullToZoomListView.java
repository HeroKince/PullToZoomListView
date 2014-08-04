package com.kince.android.pulltozoomlistview;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

public class PullToZoomListView extends ListView implements
		AbsListView.OnScrollListener {

	private static final int INVALID_VALUE = -1;
	private static final String TAG = "PullToZoomListView";
	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float paramAnonymousFloat) {
			float f = paramAnonymousFloat - 1.0F;
			return 1.0F + f * (f * (f * (f * f)));
		}
	};
	int mActivePointerId = -1;
	private FrameLayout mHeaderContainer;
	private int mHeaderHeight;
	private ImageView mHeaderImage;
	float mLastMotionY = -1.0F;
	float mLastScale = -1.0F;
	float mMaxScale = -1.0F;
	private AbsListView.OnScrollListener mOnScrollListener;
	private ScalingRunnalable mScalingRunnalable;
	private int mScreenHeight;
	private ImageView mShadow;

	private boolean mScrollable = true;
	private boolean mShowHeaderImage = true;
	private boolean mZoomable = true;

	public PullToZoomListView(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public PullToZoomListView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	public PullToZoomListView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext);
	}

	private void endScraling() {
		if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight)
			Log.d("mmm", "endScraling");
		this.mScalingRunnalable.startAnimation(200L);
	}

	private void init(Context paramContext) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		((Activity) paramContext).getWindowManager().getDefaultDisplay()
				.getMetrics(localDisplayMetrics);
		this.mScreenHeight = localDisplayMetrics.heightPixels;
		this.mHeaderContainer = new FrameLayout(paramContext);
		this.mHeaderImage = new ImageView(paramContext);
		int i = localDisplayMetrics.widthPixels;
		setHeaderViewSize(i, (int) (9.0F * (i / 16.0F)));
		this.mShadow = new ImageView(paramContext);
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
				-1, -2);
		localLayoutParams.gravity = 80;
		this.mShadow.setLayoutParams(localLayoutParams);
		this.mHeaderContainer.addView(this.mHeaderImage);
		this.mHeaderContainer.addView(this.mShadow);
		addHeaderView(this.mHeaderContainer);
		this.mScalingRunnalable = new ScalingRunnalable();
		super.setOnScrollListener(this);
	}

	private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
		int i = (paramMotionEvent.getAction()) >> 8;
		Log.d("onSecondaryPointerUp", i + "");
		if (paramMotionEvent.getPointerId(i) == this.mActivePointerId)
			if (i != 0) {
				this.mLastMotionY = paramMotionEvent.getY(1);
				this.mActivePointerId = paramMotionEvent.getPointerId(0);
				return;
			}
	}

	private void reset() {
		this.mActivePointerId = -1;
		this.mLastMotionY = -1.0F;
		this.mMaxScale = -1.0F;
		this.mLastScale = -1.0F;
	}

	public ImageView getHeaderView() {
		return this.mHeaderImage;
	}

	public void hideHeaderImage() {
		this.mShowHeaderImage = false;
		this.mZoomable = false;
		this.mScrollable = false;
		removeHeaderView(this.mHeaderContainer);
	}

	public boolean isScrollable() {
		return this.mScrollable;
	}

	public boolean isZoomable() {
		return this.mZoomable;
	}

	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		if (!this.mZoomable) {
			return super.onInterceptTouchEvent(paramMotionEvent);
		}
		switch (paramMotionEvent.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

			this.mActivePointerId = paramMotionEvent.getPointerId(0);
			this.mMaxScale = (this.mScreenHeight / this.mHeaderHeight);
			break;

		case MotionEvent.ACTION_UP:
			reset();
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			this.mActivePointerId = paramMotionEvent
					.getPointerId(paramMotionEvent.getActionIndex());
			break;

		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(paramMotionEvent);
			break;
		}
		return super.onInterceptTouchEvent(paramMotionEvent);
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
		if (this.mHeaderHeight == 0)
			this.mHeaderHeight = this.mHeaderContainer.getHeight();
	}

	@Override
	public void onScroll(AbsListView paramAbsListView, int paramInt1,
			int paramInt2, int paramInt3) {
		if (this.mScrollable) {
			Log.d(TAG, "onScroll");
			float f = this.mHeaderHeight - this.mHeaderContainer.getBottom();
			Log.d("onScroll", "f|" + f);
			if ((f > 0.0F) && (f < this.mHeaderHeight)) {
				Log.d("onScroll", "1");
				int i = (int) (0.65D * f);
				this.mHeaderImage.scrollTo(0, -i);
			} else if (this.mHeaderImage.getScrollY() != 0) {
				Log.d("onScroll", "2");
				this.mHeaderImage.scrollTo(0, 0);
			}
		}

		if (this.mOnScrollListener != null) {
			this.mOnScrollListener.onScroll(paramAbsListView, paramInt1,
					paramInt2, paramInt3);
		}
	}

	public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
		if (this.mOnScrollListener != null)
			this.mOnScrollListener.onScrollStateChanged(paramAbsListView,
					paramInt);
	}

	public boolean onTouchEvent(MotionEvent ev) {

		Log.d(TAG, "" + (0xFF & ev.getAction()));
		if (!this.mZoomable) {
			Log.i("zoom", "zoom");
			return super.onTouchEvent(ev);
		}
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_DOWN:
			if (!this.mScalingRunnalable.mIsFinished) {
				this.mScalingRunnalable.abortAnimation();
			}
			this.mLastMotionY = ev.getY();
			this.mActivePointerId = ev.getPointerId(0);
			this.mMaxScale = (this.mScreenHeight / this.mHeaderHeight);
			this.mLastScale = (this.mHeaderContainer.getBottom() / this.mHeaderHeight);
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("onTouchEvent", "mActivePointerId" + mActivePointerId);
			int j = ev.findPointerIndex(this.mActivePointerId);
			if (j == -1) {
				Log.e("PullToZoomListView", "Invalid pointerId="
						+ this.mActivePointerId + " in onTouchEvent");
			} else {
				if (this.mLastMotionY == -1.0F)
					this.mLastMotionY = ev.getY(j);
				if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight) {
					ViewGroup.LayoutParams localLayoutParams = this.mHeaderContainer
							.getLayoutParams();
					float f = ((ev.getY(j) - this.mLastMotionY + this.mHeaderContainer
							.getBottom()) / this.mHeaderHeight - this.mLastScale)
							/ 2.0F + this.mLastScale;
					if ((this.mLastScale <= 1.0D) && (f < this.mLastScale)) {
						localLayoutParams.height = this.mHeaderHeight;
						this.mHeaderContainer
								.setLayoutParams(localLayoutParams);
					}
					this.mLastScale = Math.min(Math.max(f, 1.0F),
							this.mMaxScale);
					localLayoutParams.height = ((int) (this.mHeaderHeight * this.mLastScale));
					if (localLayoutParams.height < this.mScreenHeight)
						this.mHeaderContainer
								.setLayoutParams(localLayoutParams);
					this.mLastMotionY = ev.getY(j);
				}
				this.mLastMotionY = ev.getY(j);
			}
			break;
		case MotionEvent.ACTION_UP:
			reset();
			endScraling();
			break;
		case MotionEvent.ACTION_CANCEL:

			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			int i = ev.getActionIndex();
			this.mLastMotionY = ev.getY(i);
			this.mActivePointerId = ev.getPointerId(i);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			this.mLastMotionY = ev.getY(ev
					.findPointerIndex(this.mActivePointerId));
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void setHeaderViewSize(int paramInt1, int paramInt2) {
		if (!this.mShowHeaderImage) {
			return;
		}
		Object localObject = this.mHeaderContainer.getLayoutParams();
		if (localObject == null)
			localObject = new AbsListView.LayoutParams(paramInt1, paramInt2);
		((ViewGroup.LayoutParams) localObject).width = paramInt1;
		((ViewGroup.LayoutParams) localObject).height = paramInt2;
		this.mHeaderContainer
				.setLayoutParams((ViewGroup.LayoutParams) localObject);
		this.mHeaderHeight = paramInt2;
	}

	public void setOnScrollListener(
			AbsListView.OnScrollListener paramOnScrollListener) {
		this.mOnScrollListener = paramOnScrollListener;
	}

	public void setScrollable(boolean paramBoolean) {
		if (!this.mShowHeaderImage) {
			return;
		}
		this.mScrollable = paramBoolean;
	}

	public void setShadow(int paramInt) {
		if (!this.mShowHeaderImage) {
			return;
		}
		this.mShadow.setBackgroundResource(paramInt);
	}

	public void setZoomable(boolean paramBoolean) {
		if (!this.mShowHeaderImage) {
			return;
		}
		this.mZoomable = paramBoolean;
	}

	class ScalingRunnalable implements Runnable {
		long mDuration;
		boolean mIsFinished = true;
		float mScale;
		long mStartTime;

		ScalingRunnalable() {
		}

		public void abortAnimation() {
			this.mIsFinished = true;
		}

		public boolean isFinished() {
			return this.mIsFinished;
		}

		public void run() {
			float f2;
			ViewGroup.LayoutParams localLayoutParams;
			if ((!this.mIsFinished) && (this.mScale > 1.0D)) {
				float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) this.mStartTime)
						/ (float) this.mDuration;
				f2 = this.mScale - (this.mScale - 1.0F)
						* PullToZoomListView.sInterpolator.getInterpolation(f1);
				localLayoutParams = PullToZoomListView.this.mHeaderContainer
						.getLayoutParams();
				if (f2 > 1.0F) {
					Log.d("mmm", "f2>1.0");
					localLayoutParams.height = PullToZoomListView.this.mHeaderHeight;
					localLayoutParams.height = ((int) (f2 * PullToZoomListView.this.mHeaderHeight));
					PullToZoomListView.this.mHeaderContainer
							.setLayoutParams(localLayoutParams);
					PullToZoomListView.this.post(this);
					return;
				}
				this.mIsFinished = true;
			}
		}

		public void startAnimation(long paramLong) {
			this.mStartTime = SystemClock.currentThreadTimeMillis();
			this.mDuration = paramLong;
			this.mScale = ((float) (PullToZoomListView.this.mHeaderContainer
					.getBottom()) / PullToZoomListView.this.mHeaderHeight);
			this.mIsFinished = false;
			PullToZoomListView.this.post(this);
		}
	}
}
