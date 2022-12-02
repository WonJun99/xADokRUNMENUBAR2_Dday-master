package com.ceedlive.ceeday.util;

import android.content.Context;

import com.ceedlive.ceeday.Constant;
import com.ceedlive.ceeday.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarUtil {

    /**
     * 두 날짜 간 차이 구하기
     * @param context
     * @param mTargetCalendar
     * @param mBaseCalendar
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getDiffDays(Context context, Calendar mTargetCalendar, Calendar mBaseCalendar, int year, int month, int day, int direction) {
        // Calendar 두 날짜 간 차이 구하기
        mTargetCalendar.set(Calendar.YEAR, year);
        mTargetCalendar.set(Calendar.MONTH, month - 1);
        mTargetCalendar.set(Calendar.DAY_OF_MONTH, day);

        // 밀리초(1000분의 1초) 단위로 두 날짜 간 차이를 변환 후 초 단위로 다시 변환
        long diffSec = (mTargetCalendar.getTimeInMillis() - mBaseCalendar.getTimeInMillis()) / 1000;
        // 1분(60초), 1시간(60분), 1일(24시간) 이므로 다음과 같이 나누어 1일 단위로 다시 변환
        long diffDays = diffSec / (60 * 60 * 24);

        int flag = 0;

        if ( direction == Constant.DIRECTION.FORWARD ) {
            flag = diffDays > 0 ? 1 : diffDays < 0 ? -1 : 0;
        } else if ( direction == Constant.DIRECTION.REVERSE ) {
            flag = diffDays > 0 ? -1 : diffDays < 0 ? 1 : 0;
        }

        final String msg;

        switch (flag) {
            case 1:
                msg = context.getString(R.string.dday_valid_prefix) + Math.abs(diffDays);
                break;
            case 0:
                msg = context.getString(R.string.dday_today);
                break;
            case -1:
                msg = context.getString(R.string.dday_invalid_prefix) + Math.abs(diffDays);
                break;
            default:
                msg = "";
        }

        return msg;
    }

    /**
     * 주어진 값 다음의 날짜 구하기(과거는 - 마이너스)
     * @param nextDateInt
     * @return
     */
    public static Date getNextDate(int nextDateInt){
        return getNextDate(nextDateInt, new Date());
    }

    /**
     * 주어진 값 다음의 날짜 구하기(과거는 - 마이너스)
     * @param nextDateInt   날짜에 더하거나 빼야할 값
     * @param nowDateStr    현재 날짜 및 기준날짜( new Date(), 없을 경우 new Date(), yyyymmdd 8자리)
     * @return
     */
    public static Date getNextDate(int nextDateInt, String nowDateStr){
        int year = Integer.parseInt(nowDateStr.substring(0, 4));
        int month = ( Integer.parseInt( nowDateStr.substring(4, 6) ) -1 );
        int day = Integer.parseInt( nowDateStr.substring(6, 8) );

        GregorianCalendar gregorianCalendar = new GregorianCalendar ( year, month, day );
        return getNextDate(nextDateInt, gregorianCalendar.getTime());
    }

    /**
     * 주어진 값 다음의 날짜 구하기(과거는 - 마이너스)
     * @param nextDateInt
     * @param nowDate
     * @return
     */
    public static Date getNextDate(int nextDateInt, Date nowDate){
        long oneDate = 1000 * 3600 * 24;    // 하루
        return new Date( nowDate.getTime() + (oneDate * nextDateInt) );
    }

}
