package group.spart.abl.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import group.spart.abl.app.MainActivity;
import group.spart.abl.app.R;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 3, 2021 3:59:15 PM 
 */
public class TextViewAdapter extends ArrayAdapter<String> {

	private int fSelectedIdx = -1, fMarkIdx = -1, fPosition = -1;
	private static final int fBgColorSelected = Color.LTGRAY;
	private static final int fBgColorOriginal = Color.WHITE;
	
	public TextViewAdapter(int resource, List<String> objects) {
		super(MainActivity.instance(), resource, objects);
	}

	 @SuppressLint("InflateParams")
	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater layoutInflater = ((MainActivity)getContext()).getLayoutInflater();
			convertView = layoutInflater.inflate(R.layout.list_view, null);
		}

		if(isSelectionEnabled()) {
			ViewGroup.LayoutParams params = parent.getLayoutParams();
			params.width = parent.getMeasuredWidth();
			convertView.setLayoutParams(params);
			convertView.setBackgroundColor(position == fSelectedIdx ? fBgColorSelected : fBgColorOriginal);
		}

		fPosition = position;
		setText(convertView, getItem(position));

		return convertView;
     }

     public void select(int index) {
		fSelectedIdx = index;
	 }

	 public int getSelectedIdx() {
		return fSelectedIdx;
	 }

	 public void mark(int index) { fMarkIdx = index; }

	 public int getMarkIdx() { return  fMarkIdx; }

	 protected boolean isSelectionEnabled() {
		return true;
	 }

	 protected int getFontColor() {
		return Color.BLACK;
	 }

	 protected void setText(View view, String text) {
		String tag = (isSelectionEnabled() && fMarkIdx == fPosition ? "* ":"");
		SpannableString string = getSpannableString(view, tag, text);
		TextView textView = (TextView) view;
		textView.setTextColor(getFontColor());
		textView.setText(string);
	 }

	 protected SpannableString getSpannableString(View view, String prefix, String text) {
		 Paint tvPaint = ((TextView)view).getPaint();
		 float rawIndentWidth = tvPaint.measureText(prefix);
		 SpannableString spannableString = new SpannableString(prefix + text);
		 LeadingMarginSpan.Standard what = new LeadingMarginSpan.Standard(0, (int) rawIndentWidth);
		 spannableString.setSpan(what, 0, spannableString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
		 return spannableString;
	 }

}
