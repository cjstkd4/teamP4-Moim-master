package com.example.meetingactivity.Response;

import android.util.Log;

import com.example.meetingactivity.Activity.CustomCalendarView;
import com.example.meetingactivity.model.Calendar;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Detail_Response extends AsyncHttpResponseHandler {
    Calendar calendar;

    public Detail_Response(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        String content = new String(responseBody);
        try {
            JSONObject json = new JSONObject(content);
            JSONObject item = json.getJSONObject("item");

            calendar.setSch_moimcode(item.getInt("moimcode"));
            calendar.setSch_schnum(item.getInt("schnum"));
            calendar.setSch_title(item.getString("title"));
            calendar.setSch_sub(item.getString("sub"));
            calendar.setSch_year(item.getString("year"));
            calendar.setSch_month(item.getString("month"));
            calendar.setSch_day(item.getString("day"));
            calendar.setSch_time(item.getString("time"));
            calendar.setSch_amount(item.getInt("amount"));
            calendar.setSch_lot(item.getDouble("lot"));
            calendar.setSch_lat(item.getDouble("lat"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

    }
}
