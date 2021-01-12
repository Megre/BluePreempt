package group.spart.bl.ua;

import group.spart.bl.app.GUI;

public abstract class UserAction implements Performable {

    private static transient boolean isBusy = false;
    private boolean isSequential = false;

    public UserAction() {
    	
    }

    public UserAction(boolean sequential) {
        isSequential = sequential;
    }

    protected void act() {
        if(!start()) {
            notifyUser("processing command. please wait...");
            return;
        }

        if(isSequential) {
            UserAction.this.execute();
            return;
        }
        GUI.instance().execute(() -> { UserAction.this.execute(); });
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

    protected void postAction(Object result) {
        if(isSequential) finish();
    }

    protected void notifyUser(String message) {
        GUI.instance().notifyUser(message);
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
