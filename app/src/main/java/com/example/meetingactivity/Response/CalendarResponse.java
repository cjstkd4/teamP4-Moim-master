package com.example.meetingactivity.Response;

import android.util.Log;
import android.util.LongSparseArray;

import com.example.meetingactivity.Activity.CustomCalendarView;
import com.example.meetingactivity.Fragment.CalendarFragment;
import com.example.meetingactivity.adapter.CalendarAdapter;
import com.example.meetingactivity.model.Calendar;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class CalendarResponse extends AsyncHttpResponseHandler {
    CalendarAdapter adapter;

    // 시스템 날짜
    String syste_data = "";
    // 캘린터 날짜
    String calendar_data = "";
    // test
    String test = "";

    public CalendarResponse(CalendarAdapter adapter) {
        this.adapter = adapter;
    }

    // 통신 성공시
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        // 통신 데이터 처리
        String content = new String(responseBody);
        Log.d("[test]_cal_reponse", "값 : " + content);
        try {
            JSONObject json = new JSONObject(content);
            JSONArray items = json.getJSONArray("items");
//            검색 결과 처리
            for(int i = 0; i < items.length(); i++){
                JSONObject jsonObject = items.getJSONObject(i);
                Calendar calendar = new Calendar();
                calendar.setSch_moimcode(jsonObject.getInt("sch_moimcode"));
                calendar.setSch_month(jsonObject.getString("sch_month"));
                calendar.setSch_schnum(jsonObject.getInt("sch_schnum"));
                calendar.setSch_amount(jsonObject.getInt("sch_amount"));
                calendar.setSch_day(jsonObject.getString("sch_day"));
                calendar.setSch_lot(jsonObject.getDouble("sch_lot"));
                calendar.setSch_lat(jsonObject.getDouble("sch_lat"));
                calendar.setSch_sub(jsonObject.getString("sch_sub"));
                calendar.setSch_title(jsonObject.getString("sch_title"));
                calendar.setSch_time(jsonObject.getString("sch_time"));
                calendar.setSch_year(jsonObject.getString("sch_year"));
//
//                test = CustomCalendarView.Calendar_YM();
//                calendar_data = calendar.getSch_year() + "-" + calendar.getSch_month();

                adapter.add(calendar);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 통신 실패시
    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
    }
}
