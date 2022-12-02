package com.ceedlive.ceeday.holder;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DdayViewHolder {

    public LinearLayout generalLayout;
    public LinearLayout detailLayout;

    public CheckBox checkBox;
    public ImageView ivStatusIcon;
    public TextView textViewTitle;
    public TextView textViewDate;
    public TextView textViewDay;
    public TextView textViewDescription;

    public ImageView btnDetail;
    public ImageView btnEdit;
    public ImageView btnDelete;
    public ImageView btnNoti;

    public int position;

    // 여기서 조금 특이한 점은 대부분이 맴버변수는 private으로 선언한 뒤에 getter/setter를 사용하는데
    // 이 방식을 취하지 않고 맴버변수에 직접적으로 접근을 한다는 점입니다.
    // [이글]을 참고해보시면 메서드내에서 맴버변수(필드)에 접근하는것조차 상대적으로 비용이 크다는 언급이 나옵니다.

    // 결론적으로 실행에 드는 비용을 줄일려고 ViewHolder를 사용하므로 ViewHolder내에서도 메서드 호출의 숫자까지 줄이는것이 중요해 보입니다.
    // 결론적으로 viewHolder에서 Row내의 요소 위젯들을 직접적으로 가지고 있으므로 바로바로 값을 변경할 수 있습니다.
    // 실제로 안드로이드 개발자랩에서 보여준 데모에서는 많은 Row를 가진 ListView라도 매우 빠르게 동작하더군요.

    // http://theeye.pe.kr/archives/1253
}
