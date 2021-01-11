package group.spart.abl.ui;

import java.util.ArrayList;

import android.widget.ListView;

import group.spart.abl.app.R;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 2, 2021 12:37:22 AM 
 */
public class DisplayList {
	
	private final ListView fListView;
	private final TextViewAdapter fAdapter;
	
	public DisplayList(ListView listView) {
		this(listView, TextViewAdapterFactory.STYLE_SELECTABLE);
	}

	public DisplayList(ListView listView, int style) {
		fListView = listView;
		listView.setDivider(null);

		ArrayList<String> list = new ArrayList<>();
		fAdapter = TextViewAdapterFactory.create(style, R.layout.list_view, list);
		fListView.setAdapter(fAdapter);

		fListView.setOnItemClickListener((parent, view, position, id) -> {
			setSelection(position);
			assert fAdapter != null;
			fAdapter.notifyDataSetChanged();
		});
	}

	public void removeAll() {
		fAdapter.clear();
		fAdapter.notifyDataSetInvalidated();
	}

	public int getItemCount() {
		return fAdapter.getCount();
	}

	public void add(String item) {
		fAdapter.add(item);
		fListView.setSelection(fListView.getBottom());
	}

	public int getSelection() {
		return fAdapter.getSelectedIdx();
	}

	public void setSelection(int position) { fAdapter.select(position); }

	public void setMark(int position) {
		fAdapter.mark(position);
		fAdapter.notifyDataSetChanged();
	}

	public int getMark() { return fAdapter.getMarkIdx(); }


}
