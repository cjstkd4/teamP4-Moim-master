package com.example.meetingactivity.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.meetingactivity.Activity.Calendar_ReadActivity;
import com.example.meetingactivity.Activity.CustomCalendarView;
import com.example.meetingactivity.R;
import com.example.meetingactivity.Response.CalendarResponse;
import com.example.meetingactivity.Response.MemberResponse;
import com.example.meetingactivity.adapter.CalendarAdapter;
import com.example.meetingactivity.adapter.MemberAdapter;
import com.example.meetingactivity.model.Calendar;
import com.example.meetingactivity.model.Mypage;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    // 객체 설정
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //    캘린더 리스트 뷰 설정
    String user_id;
    Mypage item;
    CustomCalendarView customCalendarView;
    AsyncHttpClient client;
    List<Calendar> calendar_List;
    ListView calendar_listView;
    CalendarAdapter calendarAdapter;
    CalendarResponse calendarResponse;
    Calendar calendar_item;
    String year, month, str_year, str_month;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // intent와 bundle로 가져온 데이터
        if (getArguments() != null) {
            user_id = getArguments().getString("user_id");
            item = (Mypage) getArguments().getSerializable("item");
        }
        CustomCalendarView.CustomCalendar_date(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        이유는 모르지만 fragment에서는 View를 통해서 layout을 초기화 해줘야 한다.
        final View view = inflater.inflate(R.layout.fragment_calendar, container, false);

//        달력 객체 초기화
        customCalendarView = (CustomCalendarView) view.findViewById(R.id.custom_calendar_view);


//        intent와 bundle로 가져온 데이터
        if (getArguments() != null) {
            user_id = getArguments().getString("user_id");
            item = (Mypage) getArguments().getSerializable("item");
        }
//        달력 listview 선언
        client = new AsyncHttpClient();
        calendar_List = new ArrayList<>();
        calendarAdapter = new CalendarAdapter(getActivity(), R.layout.calendar_item, calendar_List);
        calendar_listView = view.findViewById(R.id.Calendar_listView);
        calendar_listView.setAdapter(calendarAdapter);
        calendarResponse = new CalendarResponse(calendarAdapter);


        getCalendar_list();

        calendar_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                calendar_item = calendarAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), Calendar_ReadActivity.class);
                intent.putExtra("calendar_item", calendar_item);
                intent.putExtra("mypage_item", item);
                intent.putExtra("user_id", user_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        str_year = bundle.getString("str_year");
        str_month = bundle.getString("str_month");
        Log.d("[test]_cal_frag", "값 2");
        Log.d("[test]_cal_frag", "값 : " + str_year + " / " + str_month);
        getCalendar_list();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void getCalendar_list() {
        calendarAdapter.clear();

        calendarDate();

//        if(str_year.equals("") || str_month.equals("")){
        String URL = "http://192.168.0.93:8080/moim.4t.spring/selectScheduleMonth.tople";
        RequestParams params = new RequestParams();
        params.put("sch_moimcode", item.getMoimcode());
        params.put("sch_year", year);
        params.put("sch_month", month);
        client.post(URL, params, calendarResponse);
//        } else {
//            String URL = "http://192.168.0.93:8080/moim.4t.spring/selectScheduleMonth.tople";
//            RequestParams params = new RequestParams();
//            params.put("sch_moimcode", item.getMoimcode());
//            params.put("sch_year", str_year);
//            params.put("sch_month", str_month);
//            client.post(URL, params, calendarResponse);
//        }
    }

    private void calendarDate() {
        // 시스템의 오늘 날짜 년, 월 구하기
        Date TodayDate = java.util.Calendar.getInstance().getTime();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());

        year = yearFormat.format(TodayDate);
        month = monthFormat.format(TodayDate);
    }
}
