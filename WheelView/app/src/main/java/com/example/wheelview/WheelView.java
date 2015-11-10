package com.example.wheelview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WheelView extends ScrollView {
	public static final String TAG = WheelView.class.getSimpleName();
	private Context context;
	List<String> items;
	//主要背景
	private LinearLayout views;
	// 每页显示的数量
	int displayItemCount; 

	int selectedIndex = 1;
	int initialY;
	Runnable scrollerTask;
	int itemHeight = 0;
	int newCheck = 50;

	/**
	 * 构造函数
	 */

	public WheelView(Context context) {
		super(context);
		init(context);
	}

	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		//禁用滚动块
		this.setVerticalScrollBarEnabled(false);
		views = new LinearLayout(context);
		//设置view
		views.setOrientation(LinearLayout.VERTICAL);
		this.addView(views);
		//
		scrollerTask = new Runnable() {
			public void run() {
				int newY = getScrollY();
				if (initialY - newY == 0) { // stopped
					final int remainder = initialY % itemHeight;
					final int divided = initialY / itemHeight;
					if (remainder == 0) {
						selectedIndex = divided + offset;
						onSeletedCallBack();
					} else {
						if (remainder > itemHeight / 2) {
							WheelView.this.post(new Runnable() {
								@Override
								public void run() {
									WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
									selectedIndex = divided + offset + 1;
									onSeletedCallBack();
								}
							});
						} else {
							WheelView.this.post(new Runnable() {
								@Override
								public void run() {
									WheelView.this.smoothScrollTo(0, initialY - remainder);
									selectedIndex = divided + offset;
									onSeletedCallBack();
								}
							});
						}
					}
				} else {
					initialY = getScrollY();
					WheelView.this.postDelayed(scrollerTask, newCheck);
				}
			}
		};
	}



	public void setItems(List<String> list) {
		if (null == items) {
			items = new ArrayList<String>();
		}
		items.clear();
		items.addAll(list);

		// 前面和后面补全
		for (int i = 0; i < offset; i++) {
			items.add(0, "");
			items.add("");
		}
		initData();
	}

	private void initData() {
		displayItemCount = offset * 2 + 1;
		for (String item : items) {
			views.addView(createView(item));
		}
		refreshItemView(0);
	}

	private void refreshItemView(int y) {
		int position = y / itemHeight + offset;
		int remainder = y % itemHeight;
		int divided = y / itemHeight;
		if (remainder == 0) {
			position = divided + offset;
		} else {
			if (remainder > itemHeight / 2) {
				position = divided + offset + 1;
			}
		}

		int childSize = views.getChildCount();
		for (int i = 0; i < childSize; i++) {
			TextView itemView = (TextView) views.getChildAt(i);
			if (null == itemView) {
				return;
			}
			if (position == i) {
				itemView.setTextColor(Color.parseColor("#0288ce"));
			} else {
				itemView.setTextColor(Color.parseColor("#bbbbbb"));
			}
		}
	}


	public static final int OFF_SET_DEFAULT = 1;
	int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）

	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}



	/**
	 * TODO 设置条目
	 * @param item
	 * @return
	 */
	private TextView createView(String item) {
		TextView tv = new TextView(context);
		tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
				.LayoutParams.WRAP_CONTENT));
		tv.setSingleLine(true);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		tv.setText(item);
		tv.setGravity(Gravity.CENTER);
		int padding = ABTextUtil.dip2px(context, 15);
		tv.setPadding(padding, padding, padding, padding);
		tv.setTextColor(Color.GRAY);
		//设置第一个高度
		if (0 == itemHeight) {
			itemHeight = ABViewUtil.getViewMeasuredHeight(tv);
			views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
			this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
		}
		return tv;
	}

	/**
	 *  scrollTo (int x, int y)会调用此方法]　
	 *  设置当前视图滚动到的位置。此函数会引起对onScrollChanged(int, int, int, int)函数的调用并且会让视图更新。
		当前版本取消了在子视图中的滚动。
		left,top,oldleft,oldtop
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		refreshItemView(t);
		if (t > oldt) {
			//Logger.d(TAG, "向下滚动");
			scrollDirection = SCROLL_DIRECTION_DOWN;
		} else {
			//Logger.d(TAG, "向上滚动");
			scrollDirection = SCROLL_DIRECTION_UP;
		}
	}

	/**
	 * TODO 获取选中区域的边界
	 */
	int[] selectedAreaBorder;
	private int[] obtainSelectedAreaBorder() {
		if (null == selectedAreaBorder) {
			selectedAreaBorder = new int[2];
			selectedAreaBorder[0] = itemHeight * offset;
			selectedAreaBorder[1] = itemHeight * (offset + 1);
		}
		return selectedAreaBorder;
	}


	private int scrollDirection = -1;
	private static final int SCROLL_DIRECTION_UP = 0;
	private static final int SCROLL_DIRECTION_DOWN = 1;
	Paint paint;
	int viewWidth;

	/**
	 * 绘制背景
	 */
	@Override
	public void setBackgroundDrawable(Drawable background) {

		if (viewWidth == 0) {
			viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
		}
		if (null == paint) {
			paint = new Paint();
			/**
			 * TODO *******中间条目的边线
			 */
			paint.setColor(Color.parseColor("#83cde6"));
			paint.setStrokeWidth(ABTextUtil.dip2px(context, 1f));
		}
		background = new Drawable() {
			@Override
			public void draw(Canvas canvas) {

				/**
				 * TODO 画选中区域
				 */
				//划线
				//drawLine(float startX, float startY, float stopX, float stopY, Paint paint)
				//canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[0], viewWidth * 5 / 6, obtainSelectedAreaBorder()[0], paint);
				//canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[1], viewWidth * 5 / 6, obtainSelectedAreaBorder()[1], paint);
				//画方形
				//startX 起始X坐标
				//stopX 结束X坐标
				//startY起始Y坐标
				//stopY 结束Y坐标
				float left=0;
				float top=obtainSelectedAreaBorder()[0];
				float right=viewWidth;
				float bottom=obtainSelectedAreaBorder()[1];
				RectF rectF = new RectF(left,top,right,bottom);
				canvas.drawRect(rectF, paint);
			}
			@Override
			public void setAlpha(int alpha) {
			}
			@Override
			public void setColorFilter(ColorFilter cf) {
			}
			@Override
			public int getOpacity() {
				return 0;
			}
		};
		super.setBackgroundDrawable(background);
	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewWidth = w;
		setBackgroundDrawable(null);
	}
	/**
	 * 选中回调
	 */
	private void onSeletedCallBack() {
		if (null != onWheelViewListener) {
			onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
		}
	}
	public void setSeletion(int position) {
		final int p = position;
		selectedIndex = p + offset;
		this.post(new Runnable() {
			@Override
			public void run() {
				WheelView.this.smoothScrollTo(0, p * itemHeight);
			}
		});
	}
	public String getSeletedItem() {
		return items.get(selectedIndex);
	}
	public int getSeletedIndex() {
		return selectedIndex - offset;
	}



	@Override
	public void fling(int velocityY) {
		super.fling(velocityY / 3);
	}

	/**
	 * TODO 滑动事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			startScrollerTask();
		}
		return super.onTouchEvent(ev);
	}
	public void startScrollerTask() {
		//获取view相对于屏幕原点
		initialY = getScrollY();
		this.postDelayed(scrollerTask, newCheck);
	}


	/**
	 * TODO 设置回调接口
	 */
	public static class OnWheelViewListener {
		public void onSelected(int selectedIndex, String item) {
		};
	}
	private OnWheelViewListener onWheelViewListener;
	public OnWheelViewListener getOnWheelViewListener() {
		return onWheelViewListener;
	}
	public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
		this.onWheelViewListener = onWheelViewListener;
	}
}


