package com.ceedlive.ceeday.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ceedlive.ceeday.R;
import com.ceedlive.ceeday.activity.MergeActivity;
import com.ceedlive.ceeday.Constant;
import com.ceedlive.ceeday.data.DdayItem;
import com.ceedlive.ceeday.receiver.NotificationReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NotificationService extends Service {

    // 서비스는 쉽게 말하면 메인스레드에서 동작하는 UI가 없는 액티비티
    // UI가 없으므로 라이프사이클은 다음과 같이 동작한다.
    // onCreate() -> onStart() -> onDestory()
    // 메인스레드에서 관리하기 때문에 UI가 종료되어도 살아서 서비스를 계속한다.

    NotificationManager mNotificationManager;
    NotificationChannel mNotificationChannel;
    NotificationCompat.Builder mNotificationBuilder;

    Calendar mTargetCalendar = new GregorianCalendar();
    Calendar mBaseCalendar = new GregorianCalendar();

    Resources mResources;

    private static final String TAG = "NotificationService";

    public NotificationService() {
    }

    /**
     * Service 객체와 (화면단 Activity 사이에서) 통신(데이터를 주고받을) 때 사용하는 메서드
     * @param intent
     * @return 데이터를 전달할 필요가 없으면 return null
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");

        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        // reference: https://okky.kr/article/533867

        // 액티비티에서 bindService() 를 실행하면 호출됨
        // 리턴한 IBinder 객체는 서비스와 클라이언트 사이의 인터페이스 정의한다

        // 다른 컴포넌트가 bindService()를 호출해서 서비스와 연결을 시도하면 이 메소드가 호출됩니다.
        // 이 메소드에서 IBinder를 반환해서 서비스와 컴포넌트가 통신하는데 사용하는 인터페이스를 제공해야 합니다.
        // 만약 시작 타입의 서비스를 구현한다면 null을 반환하면 됩니다.

        //
//        sendMessage(intent);
        //

//        return mIBinder; // 서비스 객체를 리턴

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        mResources = getResources();
    }

    // Service와 Thread가 사용될 시점을 생각해 보자.
    // Thread는 앱이 사용자와 상호작용하는 과정에서 UI Thread가 Block 되지 않기 위한 작업등을 처리하기 위한 Foreground 작업에 적합하고
    // Service는 앱이 사용자와 상호작용하지 않아도 계속 수행되어야 하는 Background 작업에 적합하다고 볼 수 있다.
    // 물론 Service 내부에서 Thread가 사용되어야 하지만 큰 틀에서 봤을 때 위와 같은 개념으로 나눌 수 있을 것이다.

    /**
     * 시작 서비스를 구현하고 비동기 태스크 실행을 초기화하기 위한 중요한 메서드
     * 백그라운드에서 실행되는 동작들이 들어가는 곳
     * 서비스가 호출될 때마다 실행
     *
     * onStartCommand() is always called on the main application thread in any service.
     * You cannot be called with onStartCommand() in two threads simultaneously.
     *
     * @param intent 인텐트 : 비동기 실행을 위해 사용되는 데이터, 예를 들면 네트워크 자원의 URL
     * @param flags 전달 메서드 : 시작 요청의 기록을 반영하는 플래그, 0, START_FLAG_REDELIVERY (1), START_FLAG_RETRY(2)
     * @param startId 시작 ID : 런타임에서 제공하는 고유 식별자
     * @return
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // 백그라운드에서 실행되는 동작들이 들어가는 곳입니다.
        // 서비스가 호출될 때마다 실행

        // 다른 컴포넌트가 startService()를 호출해서 서비스가 시작되면 이 메소드가 호출됩니다.
        // 만약 연결된 타입의 서비스를 구현한다면 이 메소드는 재정의 할 필요가 없습니다.

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final InnerNotificationServiceHandler handler = new InnerNotificationServiceHandler();

        Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        if ( intent.getExtras() != null && intent.getExtras().containsKey(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID) ) {
                            int mId = intent.getIntExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, 0);
                            if (mId > 0) {
                                DdayItem ddayItem = intent.getParcelableExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM);
                                Message message = handler.obtainMessage();
                                message.obj = ddayItem;
                                handler.sendMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThread.start();

        // 서비스 호출 후 안드로이드 기본 메뉴 터치하여 모든 앱 닫기 실행 시 앱이 죽는 버그 발생
        // START_STICKY -> START_REDELIVER_INTENT 로 변경
        // reference: https://www.androidpub.com/831853
        // reference: https://hashcode.co.kr/questions/1082/%EC%84%9C%EB%B9%84%EC%8A%A4%EC%97%90%EC%84%9C-start_sticky%EC%99%80-start_not_sticky%EC%9D%98-%EC%B0%A8%EC%9D%B4%EB%8A%94-%EB%AD%94%EA%B0%80%EC%9A%94

        // 앱이 구동 중에 시스템에 의해서 프로세스가 강제 종료되었다고 생각해보자.
        // Service는 onStartCommand 의 반환 값에 따라서 강제 종료된 Service를 시스템이 다시 자동으로 시작하게 만든다.
        // 하지만 Thread는 Android 시스템이 다시 복구시켜 주진 않는다.
        // reference: https://medium.com/@joongwon/android-service%EC%99%80-thread%EC%9D%98-%EC%B0%A8%EC%9D%B4-a9175016450

        return START_REDELIVER_INTENT;

        // 상시동작
        // 시간기반 동작
        // Service 를 상속 받아 startService 서비스 시작
        // bindService 를 통해 서비스와 연결하여 커뮤니케이션 해당 Service 는 START_STICKY 로 실행
        // reference: https://soundlly.github.io/2016/04/12/android-background-service-checklist/
    }

    /**
     * 서비스가 종료될 때 할 작업
     */
    public void onDestroy() {
        // 서비스가 종료될 때 실행되는 함수가 들어갑니다.
//        synchronized (mThread) {
//            mIsRun = false;
//        }
//        mThread = null;// 쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    /**
     * 두 날짜 간 차이 구하기
     * @param year
     * @param month
     * @param day
     * @return
     */
    private String getDiffDays(int year, int month, int day) {
        mTargetCalendar.set(Calendar.YEAR, year);
        mTargetCalendar.set(Calendar.MONTH, month);
        mTargetCalendar.set(Calendar.DAY_OF_MONTH, day);

        // 밀리초(1000분의 1초) 단위로 두 날짜 간 차이를 변환 후 초 단위로 다시 변환
        long diffSec = (mTargetCalendar.getTimeInMillis() - mBaseCalendar.getTimeInMillis()) / 1000;
        // 1분(60초), 1시간(60분), 1일(24시간) 이므로 다음과 같이 나누어 1일 단위로 다시 변환
        long diffDays = diffSec / (60 * 60 * 24);

        int flag = diffDays > 0 ? 1 : diffDays < 0 ? -1 : 0;

        final String msg;

        switch (flag) {
            case 1:
                msg = getString(R.string.dday_valid_prefix) + Math.abs(diffDays);
                break;
            case 0:
                msg = getString(R.string.dday_today);
                break;
            case -1:
                msg = getString(R.string.dday_invalid_prefix) + Math.abs(diffDays);
                break;
            default:
                msg = "";
        }

        return msg;
    }

    class InnerNotificationServiceHandler extends Handler {

        Handler handler = null;

        // 핸들러 메시지큐에 있는 작업을 처리 ( 실제 처리 메소드)
        @Override
        public void handleMessage(android.os.Message msg) {
            final DdayItem ddayItem = (DdayItem) msg.obj;
            final int requestCode = ddayItem.get_id();
            final int notificationId = requestCode;

            // 알림 클릭시 MergeActivity 화면에 띄운다.
            Intent intent = new Intent(NotificationService.this, MergeActivity.class);
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ROWID, ddayItem.get_id()); //전달할 값
            intent.putExtra(Constant.INTENT.EXTRA.KEY.SQLITE_TABLE_CLT_DDAY_ITEM, ddayItem); //전달할 값
            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            // TODO Notification 개념 더 알아보기
            // 별로 중요하지 않은 알림은 소리나 진동없이 왔으면 좋겠고 중요하다고 생각하는 알림은 잠금화면에서도 알려준다면?!
            // 이럴때 유용한게 알림채널(Notification Channel)입니다.
            // Notification Channel을 통해 Notification을 여러가지 용도로 나누어서 관리할 수 있게 만들어 줍니다.
            // 사용자가 직접 각 채널별로 알림중요도나 기타 설정을 변경할 수도 있습니다.
            // 오레오에서부터는 이 Notification Channel을 필수로 만들어 주어야 합니다.
            // 오레오에서 Notification Channel을 만들어 주지 않으면 알림이 오지 않습니다.

            // 해당 기기의 OS 버전이 오레오 이상일때 Notification Channel 을 만들어주고 필요한 설정을 해준뒤
            // NotificationManager의 createNotificationChannel()을 호출해주면 됩니다.

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationChannel = new NotificationChannel(Constant.NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel.
                mNotificationChannel.setDescription("Channel description");
                mNotificationChannel.enableLights(true);
                mNotificationChannel.setLightColor(Color.RED);
                mNotificationChannel.enableVibration(true);
                mNotificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                mNotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mNotificationManager.createNotificationChannel(mNotificationChannel);
            }

            mNotificationBuilder = new NotificationCompat.Builder(getApplicationContext(), Constant.NOTIFICATION_CHANNEL_ID);

            // Notification 객체는 다음을 반드시 포함해야 합니다.

            // setSmallIcon()이 설정한 작은 아이콘
            // setContentTitle()이 설정한 제목
            // setContentText()이 설정한 세부 텍스트

            // reference: https://developer.android.com/guide/topics/ui/notifiers/notifications?hl=ko

            mNotificationBuilder
                    .setContentTitle(ddayItem.getTitle())
                    .setContentText(ddayItem.getDiffDays())
                    .setTicker(ddayItem.getTitle())
                    .setLargeIcon(BitmapFactory.decodeResource(mResources, R.mipmap.ic_launcher_round))
                    .setSmallIcon(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ?
                            R.drawable.ic_notification_star_white : R.mipmap.ic_launcher)
                    .setBadgeIconType(R.drawable.ic_noti_star_checked)
//                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                    .setOngoing(true)
                    .setShowWhen(true)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL);

            // FIXME 알림 메시지 수량이 늘어나 그룹으로 묶이는 경우 그룹을 스와이프 하면 노티 삭제됨

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mNotificationBuilder
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVisibility(Notification.VISIBILITY_PUBLIC);
            }

            mNotificationManager.notify(notificationId, mNotificationBuilder.build());

            // Set Date
            String selectedDate = ddayItem.getDate();
            String[] arrDate = selectedDate.split(Constant.REGEX.SLASH);

            String strYear = arrDate[0];
            String strMonth = arrDate[1];
            String strDay = arrDate[2];

            final int year = Integer.parseInt(strYear);
            final int month = Integer.parseInt(strMonth);
            final int day = Integer.parseInt(strDay);

            final String diffDays = getDiffDays(year, month - 1, day);

            // FIXME 재귀호출, 브로드캐스트 수신자 사용으로 코드 리팩토링 해보기
            // reference: http://la-stranger.blogspot.com/2013/09/blog-post_26.html

            // 인텐트 필터 설정
            IntentFilter intentFilter = new IntentFilter();

            // reference: http://la-stranger.blogspot.com/2013/09/blog-post_26.html

            // TEST 를 위해 임시로 추가한 인텐트
            // 매 분마다 이벤트가 발생한다.
            // 이 이벤트는 AndroidManifest에 Intent filter를 적용하는 것으로 캐치할 수 없고 코드내에서 동적으로 등록을 해야 한다.
            // 아마도 실수로 이 이벤트에 대한 로직을 추가하여 디바이스 배터리 광탈을 막기위한 목적이 아닌가 한다.
//            intentFilter.addAction(Intent.ACTION_TIME_TICK);

            // 날짜가 변경 될 때 발생한다.
            // 다시 설명하면 어느 날의 11:59  PM에서 자정으로 넘어가 날짜가 변경되는 경우 브로드캐스트 되는 인텐트

            // FIXME it doesn't work.
            intentFilter.addAction(Intent.ACTION_DATE_CHANGED);

            // 동적리시버 생성
            NotificationReceiver mNotificationReceiver = new NotificationReceiver();

            // 위에서 설정한 인텐트필터+리시버정보로 리시버 등록
            registerReceiver(mNotificationReceiver, intentFilter);

            mNotificationReceiver.callback(new NotificationReceiver.ReceiveListener() {
                @Override
                public void onReceive(String action) {
                    mTargetCalendar.set(Calendar.YEAR, year);
                    mTargetCalendar.set(Calendar.MONTH, month - 1);
                    mTargetCalendar.set(Calendar.DAY_OF_MONTH, day);

                    Date today = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    sdf.format(today);
                    mBaseCalendar = sdf.getCalendar();

//                    mNotificationBuilder.setContentText(diffDays + "/" + ddayItem.get_id() + "/" + today.toString());
                    mNotificationBuilder.setContentText(diffDays);
                    mNotificationManager.notify(notificationId, mNotificationBuilder.build());
                }
            });

            // 2019-04-19 금요일 발견한 버그 해결 내용 정리

            // 버그 발생 시나리오
            // MainActivity -> MergeActivity
            // 신규로 디데이 일정 등록, 이때 상단바 디데이 고정 선택
            // 저장
            // 알림 메시지 내려옴
            // 해당 알림 메시지 터치 == 노티피케이션에 등록된 알림 터치
            // MergeActivity 로 이동
            // 날짜 변경
            // 저장
            // MainActivity에 반영 안 됨
            // 로그캣을 보면
            // MainActivity onCreate 메소드 호출 안됨
            // NotificationService 클래스의 onStartCommand 호출
            // InnerNotificationServiceHandler > handleMessage: 핸들러 메시지큐에 있는 작업을 처리 ( 실제 처리 메소드)

            // 그래서
            // 서비스 내 해당 코드 말미에 브로드캐스트 리시버 추가, onReceive 메소드 안에 setSQLiteData 함수 추가
            // 버그 해결

            Intent fromNotificationToDetailActivityIntent = new Intent(Constant.ACTION_INTENT_FILTER_NOTIFICATION_ON_START_COMMAND);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(fromNotificationToDetailActivityIntent);

            // TODO https://academy.realm.io/kr/posts/android-thread-looper-handler/ 살펴보기

//            Intent broadcastIntent = new Intent(Intent.ACTION_TIME_TICK);

            // 수신자에게 보낼 데이터를 준비한다.
//            broadcastIntent.putExtra("date", selectedDate);
//            sendBroadcast(broadcastIntent);
            // sendBroadcast: Receiver 의 우선순위와 관계없이 실행한다.
            // sendOrderedBroadcast: Receiver 의 우선순위에 따라 순서대로 호출한다.


            // FIXME 재귀호출로 매 1분마다 디데이 텍스트 업데이트 하는 예제
//            Runnable runnable = null;
//            final int delayMillis = 1000 * 60;// 매 1분

            // Warning - The application may be doing too much work on its main thread. 경고 발생 방지 코드
            // UI 업데이트 시 다음과 같이 코드를 작성하면 경고를 피할 수 있다.
//            if (handler == null) {
//                handler = new Handler(Looper.getMainLooper());
//            }
//            handler.postDelayed(runnable = new Runnable() {
//                @Override
//                public void run() {
//
//                    // ======================================================================
//
//                    mTargetCalendar.set(Calendar.YEAR, year);
//                    mTargetCalendar.set(Calendar.MONTH, month - 1);
//                    mTargetCalendar.set(Calendar.DAY_OF_MONTH, day);
//
//                    Date today = new Date();
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//                    sdf.format(today);
//                    mBaseCalendar = sdf.getCalendar();
//
//                    Log.d(TAG, "mTargetCalendar" + String.valueOf(mTargetCalendar.getTimeInMillis()));
//                    Log.d(TAG, "mBaseCalendar" + String.valueOf(mBaseCalendar.getTimeInMillis()));
//                    Log.d(TAG, "diffDays" + diffDays);
//
//                    // ======================================================================
//
//                    mNotificationBuilder.setContentText(diffDays);
//                    mNotificationManager.notify(notificationId, mNotificationBuilder.build());
//
//                    handler.postDelayed(this, delayMillis);
//                }
//                // execute code that must be run on UI thread
//            }, delayMillis);

            // NotificationManager
            // notify(int id, Notification notification): 알림을 발생시킨다. id는 알림을 구분하는 식별자, 존재하는 알림의 id를 사용하면 알림이 update된다.
            // cancel(int id): 주어지는 id에 해당하는 알림을 취소한다.
            // cancelAll(): 현재 발생된 모든 알림을 취소한다.

            // FIXME TEST 토스트 띄우기
//            Toast.makeText(NotificationService.this, "뜸?", Toast.LENGTH_LONG).show();
        }
    }

}
