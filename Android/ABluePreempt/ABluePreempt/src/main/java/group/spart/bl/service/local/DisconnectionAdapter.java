package group.spart.bl.service.local;

import android.content.Intent;

import group.spart.abl.app.MainActivity;

public class DisconnectionAdapter {

   public void startService() {
      MainActivity activity = MainActivity.instance();
      activity.startService(new Intent(activity.getBaseContext(), DisconnectionService.class));
   }

}
