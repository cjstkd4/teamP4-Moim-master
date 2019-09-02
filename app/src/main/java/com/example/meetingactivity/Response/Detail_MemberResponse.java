package com.example.meetingactivity.Response;

import com.example.meetingactivity.adapter.Detail_MemberAdapter;
import com.example.meetingactivity.adapter.MemberAdapter;
import com.example.meetingactivity.model.Detail_Todo;
import com.example.meetingactivity.model.MemberTest;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Detail_MemberResponse extends AsyncHttpResponseHandler {
    Detail_MemberAdapter adapter;

    public Detail_MemberResponse(Detail_MemberAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onStart() {
    }

    // 통신 성공시
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        // 통신 데이터 처리
        String content = new String(responseBody);
        try {
            JSONObject json = new JSONObject(content);
            JSONArray users = json.getJSONArray("users");
//            검색 결과 처리
            for (int i = 0; i < users.length(); i++) {
                JSONObject jsonObject = users.getJSONObject(i);
                JSONObject properties = jsonObject.getJSONObject("properties");

                Detail_Todo detail_todo = new Detail_Todo();
                detail_todo.setDetail_item_img(properties.getString("thumbnail_image"));
                detail_todo.setId(properties.getString("id"));
                detail_todo.setTodo(properties.getString("todo"));
                detail_todo.setAmount(properties.getInt("amount"));
                detail_todo.setEx(properties.getString("ex"));
//                중복 체크 확인할 것
                adapter.add(detail_todo);
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
