package group.spart.abl.ua;

import android.content.Intent;
import android.net.Uri;

import group.spart.abl.app.MainActivity;

public class HelpAction extends UserAction {

    public HelpAction() {
        super(true);
    }

    @Override
    public Object actionPerformed() {
        Uri githubLink = Uri.parse("https://github.com/megre/BluePreempt");
        MainActivity.instance().startActivity(new Intent(Intent.ACTION_VIEW, githubLink));

        return null;
    }
}
