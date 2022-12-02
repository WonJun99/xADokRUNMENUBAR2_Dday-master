package com.ceedlive.ceeday.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceedlive.ceeday.R;
import com.ceedlive.ceeday.activity.MainActivity;
import com.ceedlive.ceeday.Constant;
import com.ceedlive.ceeday.data.DdayItem;
import com.ceedlive.ceeday.holder.DdayViewHolder;
import com.ceedlive.ceeday.util.CalendarUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class DdayListAdapter extends BaseAdapter {
    /**
     * ListView에 세팅할 Item 정보들
     */
    private List<DdayItem> mArrayGroup;

    /**
     * ListView에 Item을 세팅할 요청자의 정보가 들어감
     */
    private Context mContext;

    private Calendar mTargetCalendar, mBaseCalendar;

    private DdayViewHolder ddayViewHolder;

    private int mChedkedItemSize = 0;

    /**
     * 생성자
     * @param arrayGroup
     * @param context
     */
    public DdayListAdapter(List<DdayItem> arrayGroup, Context context) {
        this.mArrayGroup = arrayGroup;
        this.mContext = context;

        // 날짜와 시간을 가져오기위한 Calendar 인스턴스 선언
        this.mTargetCalendar = new GregorianCalendar();
        this.mBaseCalendar = new GregorianCalendar();
    }

    @Override
    public int getCount() {
        return mArrayGroup.size();
    }

    @Override
    public Object getItem(int i) {
        return mArrayGroup.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Adapter 가 가지고 있는 data 를 어떻게 보여줄 것인가를 정의하는 데 쓰인다.
     * 리스트뷰를 예를 들면 하나의 리스트 아이템의 모양을 결정하는 역할을 하는 것이다.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 뷰홀더 패턴 적용
        if (convertView == null) {
            // Item Cell에 Layout을 적용시킬 Inflator 객체를 생성한다.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            // Item Cell에 Layout을 적용시킨다.
            // 실제 객체는 이곳에 있다.
            convertView = inflater.inflate(R.layout.listview_group, parent, false);
            ddayViewHolder = new DdayViewHolder();
            {
                // 화면에 표시될 view로부터 위젯에 대한 데이터 획득
                ddayViewHolder.checkBox = convertView.findViewById(R.id.lv_group_checkbox);
                ddayViewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                ddayViewHolder.textViewDate = (TextView) convertView.findViewById(R.id.tvDate);
                ddayViewHolder.textViewDay = (TextView) convertView.findViewById(R.id.tvDay);
                ddayViewHolder.textViewDescription = (TextView) convertView.findViewById(R.id.listview_group_tv_description);

                ddayViewHolder.btnDetail = (ImageView) convertView.findViewById(R.id.listview_group_btn_detail);
                ddayViewHolder.btnEdit = (ImageView) convertView.findViewById(R.id.listview_group_btn_edit);
                ddayViewHolder.btnDelete = (ImageView) convertView.findViewById(R.id.listview_group_btn_delete);
                ddayViewHolder.btnNoti = (ImageView) convertView.findViewById(R.id.listview_group_btn_noti);

                ddayViewHolder.generalLayout = convertView.findViewById(R.id.listview_group_general);
                ddayViewHolder.detailLayout = convertView.findViewById(R.id.listview_group_detail);

                ddayViewHolder.ivStatusIcon = convertView.findViewById(R.id.iv_status_icon);
            }

            convertView.setTag(ddayViewHolder);
        } else {
            // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용한다.
            ddayViewHolder = (DdayViewHolder) convertView.getTag();
        }

        // Data Set 에서 position 에 위치한 데이터 획득
        final DdayItem ddayItem = mArrayGroup.get(position);

        // 각 위젯에 데이터 반영
        ddayViewHolder.textViewTitle.setText(ddayItem.getTitle());
        ddayViewHolder.textViewDate.setText(ddayItem.getDate());
        ddayViewHolder.textViewDescription.setText(ddayItem.getDescription());

        // Set Date
        String selectedDate = ddayItem.getDate();
        String[] arrDate = selectedDate.split(Constant.REGEX.SLASH);

        String strYear = arrDate[0];
        String strMonth = arrDate[1];
        String strDay = arrDate[2];

        int year = Integer.parseInt(strYear);
        int month = Integer.parseInt(strMonth);
        int day = Integer.parseInt(strDay);

        String diffDays = CalendarUtil.getDiffDays(mContext,
                mTargetCalendar,
                mBaseCalendar,
                year,
                month,
                day,
                Constant.DIRECTION.FORWARD);

//        ddayViewHolder.textViewDay.setText(getDiffDays(year, month - 1, day));
        ddayViewHolder.textViewDay.setText(diffDays);

        mTargetCalendar.set(Calendar.YEAR, year);
        mTargetCalendar.set(Calendar.MONTH, month - 1);
        mTargetCalendar.set(Calendar.DAY_OF_MONTH, day);

        ddayViewHolder.generalLayout.setTag("generalLayout" + position);
        ddayViewHolder.detailLayout.setTag("detailLayout" + position);
        ddayViewHolder.checkBox.setTag("checkBox" + position);

        ddayViewHolder.btnDetail.setTag(ddayItem.get_id());
        ddayViewHolder.btnEdit.setTag(ddayItem.get_id());
        ddayViewHolder.btnDelete.setTag(ddayItem.get_id());
        ddayViewHolder.btnNoti.setTag(ddayItem.get_id());

        boolean isNotification = ddayItem.getNotification() == 1;
//        ddayViewHolder.btnNoti.setEnabled(isEnabled);

        ddayViewHolder.btnDetail.setImageResource(R.drawable.ic_dday_search);

        ddayViewHolder.btnNoti.setImageResource(isNotification ?
                R.drawable.ic_noti_star_unchecked : R.drawable.ic_noti_star_checked);

        ddayViewHolder.btnEdit.setImageResource(R.drawable.ic_dday_edit);

        ddayViewHolder.btnDelete.setImageResource(R.drawable.ic_dday_trash_can);

        ddayViewHolder.ivStatusIcon.setImageResource(isNotification ?
                R.drawable.ic_noti_star_checked : R.drawable.ic_noti_star_unchecked);

        // 롱클릭/온클릭
        // 로우별 isChecked, isVisibleDetail 값에 따른 체크상태를 표시
        ddayViewHolder.checkBox.setChecked(ddayItem.getIsChecked());
        ddayViewHolder.checkBox.setVisibility(ddayItem.getIsChecked() ? View.VISIBLE : View.GONE);
        ddayViewHolder.detailLayout.setVisibility(ddayItem.getIsVisibleDetail() ? View.VISIBLE : View.GONE);

        // 롱클릭
        ddayViewHolder.generalLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                CheckBox checkBox = view.findViewWithTag("checkBox" + position);
                // findViewWithTag

                if ( checkBox.isChecked() ) {
                    checkBox.setVisibility(View.GONE);
                    checkBox.setChecked(false);
                    ddayItem.setIsChecked(false);

                    if (mChedkedItemSize > 0) {
                        mChedkedItemSize--;
                        MainActivity activity = (MainActivity) mContext;
                        activity.removeChecked(ddayItem.get_id());
                    }

                    if (mChedkedItemSize < 1) {
                        MainActivity activity = (MainActivity) mContext;
                        activity.handleFabVisibility(false);
                    }

                } else {
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked(true);
                    ddayItem.setIsChecked(true);

                    mChedkedItemSize++;

                    MainActivity activity = (MainActivity) mContext;
                    activity.addChecked(ddayItem.get_id());
                    activity.handleFabVisibility(true);
                }

                return true;
                // 롱클릭 시 온클릭 이벤트 발생 방지: return true 이어야 함.
                // 다음 이벤트 계속 진행 false, 이벤트 완료 true
            }
        });

        // 온클릭
        ddayViewHolder.generalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout parent = (LinearLayout) view.getParent();
                LinearLayout detail = parent.findViewWithTag("detailLayout" + position);

                // View.GONE: 8 - 값이 0이 아닌 8인 것에 주의
                if ( View.VISIBLE == detail.getVisibility() ) {
                    detail.setVisibility(View.GONE);
                    ddayItem.setIsVisibleDetail(false);
                } else {
                    detail.setVisibility(View.VISIBLE);
                    ddayItem.setIsVisibleDetail(true);
                }
            }
        });

        // 상세
        ddayViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) mContext;
                activity.onClickDetail(ddayItem.get_id());
            }
        });

        // 노티
        ddayViewHolder.btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) mContext;
                activity.onClickNoti(isNotification, ddayItem.get_id());
            }
        });

        // 수정
        ddayViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) mContext;
                activity.onClickEdit(ddayItem.get_id());
            }
        });

        // 삭제
        ddayViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) mContext;
                activity.onClickDelete(ddayItem.get_id());
            }
        });

        return convertView;
    }

}
