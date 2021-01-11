package group.spart.abl.ui;

public class TextViewAdapterFactory {
    public static int STYLE_SELECTABLE = 0;
    public static int STYLE_READONLY = 1;
    public static int STYLE_OUTPUT = 2;

    public static TextViewAdapter create(int style, int resource, java.util.List<String> objects) {
        if(style == STYLE_SELECTABLE) return new TextViewAdapter(resource, objects);

        if(style == STYLE_READONLY) return  new ReadOnlyTextViewAdapter(resource, objects);

        if(style == STYLE_OUTPUT) return new OutputTextViewAdapter(resource, objects);

        return null;
    }
}
