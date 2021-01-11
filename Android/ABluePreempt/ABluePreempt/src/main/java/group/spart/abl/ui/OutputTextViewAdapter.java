package group.spart.abl.ui;

import android.view.View;
import android.widget.TextView;

import java.util.List;

public class OutputTextViewAdapter extends ReadOnlyTextViewAdapter {

    public OutputTextViewAdapter(int resource, List<String> objects) {
        super(resource, objects);
    }

    @Override
    protected int getFontColor() {
        return 0xFF018786;
    }

    @Override
    protected void setText(View view, String text) {
        TextView textView = (TextView) view;
        textView.setTextColor(getFontColor());
        textView.setText(getSpannableString(view, "> ", text));
    }
}
