package group.spart.abl.ui;

import java.util.List;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 3, 2021 3:59:15 PM 
 */
public class ReadOnlyTextViewAdapter extends TextViewAdapter {

	public ReadOnlyTextViewAdapter(int resource, List<String> objects) {
		super(resource, objects);
	}

	@Override
	protected boolean isSelectionEnabled() { return  false; }
}
