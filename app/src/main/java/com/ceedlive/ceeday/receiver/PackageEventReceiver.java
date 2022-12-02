package com.ceedlive.ceeday.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class PackageEventReceiver extends BroadcastReceiver {

    // 안드로이드에서 어플리케이션(이하 App)을 설치하거나 삭제할 때는 그 이벤트가 Broadcast로 전달됩니다.
    // 즉, BroadcastReceiver를 등록해놓은 각 App들의 설치/삭제 이벤트를 수신할 수 있습니다.

    public interface ReceiveListener {
        void onReceive(String action);
    }

    private static final String TAG = "PackageEventReceiver";

    private ReceiveListener listener;

    public void callback(ReceiveListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        String packageName = intent.getData().getSchemeSpecificPart();
        String action = intent.getAction();
        String data = intent.getDataString();

        // ACTION_PACKAGE_ADDED : 앱이 설치되었을 때
        // ACTION_PACKAGE_REMOVED : 앱이 삭제되었을 때
        // ACTION_PACKAGE_REPLACED : 앱이 업데이트 되었을 때

        // fetching package names from extras
        String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");

        if (packageNames != null) {
            for (String packageName: packageNames) {
//                if ( packageName != null && packageName.equals("YOUR_APPLICATION_PACKAGE_NAME") ) {

                if ( packageName != null && packageName.equals("com.ceedlive.dday") ) {
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
//                    new ListenActivities(context).start();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            boolean exit = false;
                            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

                            Looper.prepare();

                            while (!exit) {

                                // get the info from the currently running task
                                List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(Thread.MAX_PRIORITY);

                                String activityName = taskInfo.get(0).topActivity.getClassName();

                                if ( activityName.equals("com.ceedlive.dday.activity.LoadingActivity") ) {
                                    // User has clicked on the Uninstall button under the Manage Apps settings

                                    //do whatever pre-uninstallation task you want to perform here
                                    // show dialogue or start another activity or database operations etc..etc..

                                    // context.startActivity(new Intent(context, MyPreUninstallationMsgActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    exit = true;
                                    Toast.makeText(context, "Done with preuninstallation tasks... Exiting Now", Toast.LENGTH_SHORT).show();
                                } else if(activityName.equals("com.android.settings.ManageApplications")) {
                                    // back button was pressed and the user has been taken back to Manage Applications window
                                    // we should close the activity monitoring now
                                    exit=true;
                                }
                            }
                            Looper.loop();
                        }
                    });

                    thread.start();
                }
            }
        }


        if ( Intent.ACTION_PACKAGE_ADDED.equals( action ) ) {
            // Google Play와 같은 스토어를 통해서 앱이 설치
            // do something...
//            if ( intent.getDataString().contains("com.ceedlive.dday") ) {
//
//            }

//            listener.onReceive(Intent.ACTION_PACKAGE_ADDED);
        }

        if ( Intent.ACTION_PACKAGE_REMOVED.equals( action ) ) {
            // Google Play와 같은 스토어를 통해서 앱이 설치
            // do something...
//            if ( intent.getDataString().contains("com.ceedlive.dday") ) {
//
//            }

//            listener.onReceive(Intent.ACTION_DATE_CHANGED);
        }

        if( Intent.ACTION_PACKAGE_REPLACED.equals( action ) ) {
            // Broadcast Action: A new version of an application package has been installed, replacing an existing version that was previously installed.
            // 새로운 버전의 앱 패키지가 설치 되거나 업데이트 되었을 때
//            if ( intent.getDataString().contains("com.ceedlive.dday") ) {
//                // do something...
//                Log.e(TAG, "ACTION_PACKAGE_REPLACED: 새로운 버전의 앱 패키지가 설치 되거나 업데이트 되었을 때");
//            }

//            listener.onReceive(Intent.ACTION_PACKAGE_REPLACED);
        }
    }
}
