package group.spart.abl.ua;

import android.view.View;

import group.spart.abl.app.MainActivity;

public abstract class UserAction implements View.OnClickListener {

    private static transient boolean isBusy = false;
    private boolean isSequential = false;

    public UserAction() {

    }

    public UserAction(boolean sequential) {
        isSequential = sequential;
    }

    @Override
    public void onClick(View v) {
        System.out.println("UserAction.onClick");
        if(!start()) {
            notifyUser("processing command. please wait...");
            return;
        }

        if(isSequential) {
            UserAction.this.execute();
            return;
        }

        MainActivity.instance().execute(() -> UserAction.this.execute());
    }

    private void execute() {
        Object result = null;
        try {
            preAction();
            result = actionPerformed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            postAction(result);
        }
    }

    protected void preAction() {

    }

    protected Object actionPerformed() {
        return null;
    }

    protected void postAction(Object result) {
        finish();
    }

    protected void notifyUser(String message) {
        MainActivity.instance().notifyUser(message);
    }

    private boolean start() {
        if(isBusy) {
            return false;
        }

        synchronized (UserAction.class) {
            if(!isBusy) isBusy = true;
        }

        return true;
    }

    protected void finish() {
        synchronized (UserAction.class) {
            if(isBusy) isBusy = false;
        }
    }
}
