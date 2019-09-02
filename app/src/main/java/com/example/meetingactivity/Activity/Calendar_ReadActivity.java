package com.example.meetingactivity.Activity;

import android.Manifest;
import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetingactivity.Fragment.CalendarFragment;
import com.example.meetingactivity.R;
import com.example.meetingactivity.Response.Detail_MemberResponse;
import com.example.meetingactivity.Response.Detail_Response;
import com.example.meetingactivity.adapter.Detail_MemberAdapter;
import com.example.meetingactivity.adapter.MemberAdapter;
import com.example.meetingactivity.model.Calendar;
import com.example.meetingactivity.model.Detail_Todo;
import com.example.meetingactivity.model.Mypage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Calendar_ReadActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    Animation fb_open, fb_close;
    FloatingActionButton fab_main, fab_sub1, fab_sub2, fab_sub3;
    Button detail_read_bt2;
    LinearLayout fabLayout_main, fabLayout_sub1, fabLayout_sub2, fabLayout_sub3;
    Boolean isFabOnOff = false;
    TextView detail_write_name, detail_read_date, detail_read_time, detail_read_help, detail_write_mony;

    GoogleMap map;
    SupportMapFragment mapFragment;
    boolean permissionCK = false;    // 퍼미션 결과 저장
    Double lat, lot;    // 좌표

    Mypage mypage_item;
    String user_id;

    //    navigation 기능 객체 선언
    DrawerLayout mDrawerLayout;
    ListView calendar_right;
    ActionBarDrawerToggle mDrawerToggle;
    Calendar calendar_item;
    List<Detail_Todo> detail_todo_list;

    //    클라이언트
    AsyncHttpClient client;
    Detail_MemberAdapter detail_memberAdapter;
    Detail_MemberResponse detail_memberResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar__read);
        calendar_item = (Calendar) getIntent().getSerializableExtra("calendar_item");

        user_id = getIntent().getStringExtra("user_id");
        mypage_item = (Mypage) getIntent().getSerializableExtra("mypage_item");

        detail_todo_list = new ArrayList<>();

        detail_memberAdapter = new Detail_MemberAdapter(this, R.layout.detail_member_item, detail_todo_list);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fb_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fb_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab_main = (FloatingActionButton) findViewById(R.id.fab_main);
        fab_sub1 = (FloatingActionButton) findViewById(R.id.fab_sub1);
        fab_sub2 = (FloatingActionButton) findViewById(R.id.fab_sub2);
        fab_sub3 = (FloatingActionButton) findViewById(R.id.fab_sub3);
        fabLayout_main = (LinearLayout) findViewById(R.id.fabLayout_main);
        fabLayout_sub1 = (LinearLayout) findViewById(R.id.fabLayout_sub1);
        fabLayout_sub2 = (LinearLayout) findViewById(R.id.fabLayout_sub2);
        fabLayout_sub3 = (LinearLayout) findViewById(R.id.fabLayout_sub3);
        detail_read_bt2 = findViewById(R.id.detail_read_bt2);
        detail_write_name = findViewById(R.id.detail_write_name);
        detail_read_date = findViewById(R.id.detail_read_date);
        detail_read_time = findViewById(R.id.detail_read_time);
        detail_read_help = findViewById(R.id.detail_read_help);
        detail_write_mony = findViewById(R.id.detail_write_mony);



        // 지도
        mapFragment.getMapAsync(this);

//    navigation 기능 객체 초기화
        mDrawerLayout = findViewById(R.id.calendar_read);
        calendar_right = findViewById(R.id.Detail_right);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        client = new AsyncHttpClient();

        calendar_right.setAdapter(detail_memberAdapter);

        fab_main.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);
        fab_sub3.setOnClickListener(this);
        detail_read_bt2.setOnClickListener(this);

        permissionCheck();
    }

    @Override
    protected void onResume() {
        super.onResume();
        client_connection();
    }

    private void client_connection() {
        String Detail_one_URL = "http://192.168.0.93:8080/moim.4t.spring/selectSchedule.tople";
        RequestParams params = new RequestParams();
        params.put("sch_schnum", calendar_item.getSch_schnum());
        client.get(Detail_one_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(content);
                    JSONObject item = json.getJSONObject("item");

                    calendar_item.setSch_moimcode(item.getInt("moimcode"));
                    calendar_item.setSch_schnum(item.getInt("schnum"));
                    calendar_item.setSch_title(item.getString("title"));
                    calendar_item.setSch_sub(item.getString("sub"));
                    calendar_item.setSch_year(item.getString("year"));
                    calendar_item.setSch_month(item.getString("month"));
                    calendar_item.setSch_day(item.getString("day"));
                    calendar_item.setSch_time(item.getString("time"));
                    calendar_item.setSch_amount(item.getInt("amount"));
                    calendar_item.setSch_lot(item.getDouble("lot"));
                    calendar_item.setSch_lat(item.getDouble("lat"));

                    setData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void setData() {
        Detail_Todo detail_todo = new Detail_Todo();
        detail_todo.setMoimcode(calendar_item.getSch_moimcode());
        detail_todo.setSch_schnum(calendar_item.getSch_schnum());
        detail_todo.setId(user_id);
        lat = calendar_item.getSch_lat();
        lot = calendar_item.getSch_lot();
        Log.d("[test]_read","값 : " + lat + " / " + lot);
        detail_write_name.setText(calendar_item.getSch_title());
        detail_read_date.setText(calendar_item.getSch_year() + " - " + calendar_item.getSch_month() + " - " + calendar_item.getSch_day());
        detail_read_time.setText(calendar_item.getSch_time());
        detail_read_help.setText(calendar_item.getSch_sub());
        String amout = Integer.toString(calendar_item.getSch_amount());
        detail_write_mony.setText(amout);
    }

    private void permissionCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        } else {
            permissionCK = true;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.detail_read_bt2:
                finish();
                break;
            case R.id.fab_main:
                if (!isFabOnOff) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
                break;
//                참석 여부 묻기
            case R.id.fab_sub1:
                joinDialog();
                break;
//                참가자 인원 확인
            case R.id.fab_sub2:
//                닫힘 동작
                if (mDrawerLayout.isDrawerOpen(calendar_right)) {
                    mDrawerLayout.closeDrawer(calendar_right);
                }
//                    열림 동작
                else if (!mDrawerLayout.isDrawerOpen(calendar_right)) {
                    mDrawerLayout.openDrawer(calendar_right);
//                    if (!detail_memberAdapter.isEmpty()) {
//                        detail_memberAdapter.clear();
//                    }
                    String Detail_URL = "http://192.168.0.93:8080/moim.4t.spring/insertSchemem.tople";
                    RequestParams params = new RequestParams();
//                    params.put("detail_todo", detail_todo);
                    client.get(Detail_URL, params, detail_memberResponse);
                }
                break;
//                관리자가 편집하는 페이지
            case R.id.fab_sub3:
                intent = new Intent(Calendar_ReadActivity.this, Calendar_UpdateActivity.class);
                intent.putExtra("calendar_item", calendar_item);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;
        }
    }

    //    참석 여부 Dialog
    private void joinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("[참석여부]");
        builder.setMessage("참가 하시겠습니까??");
        builder.setPositiveButton("예",
//                참가 : 예
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String Detail_join_URL = "http://192.168.0.93:8080/moim.4t.spring/insertSchemem.tople";

                        RequestParams params = new RequestParams();
                        params.put("sch_schnum", calendar_item.getSch_schnum());
                        params.put("id", user_id);

                        client.post(Detail_join_URL, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String strJson = new String(responseBody);
                                try {
                                    JSONObject json = new JSONObject(strJson);
                                    String rt = json.getString("result");
                                    if (rt.equals("OK")) {
                                        Toast.makeText(Calendar_ReadActivity.this, "참석 성공", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Calendar_ReadActivity.this, "참석 실패", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            }
                        });
                    }
                });
        builder.setNegativeButton("아니오",
//                참가 : 아니요
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "아니오를 선택했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    private void closeFABMenu() {
        isFabOnOff = false;
        fabLayout_sub1.setVisibility(View.GONE);
        fabLayout_sub2.setVisibility(View.GONE);
        fabLayout_sub3.setVisibility(View.GONE);
        fab_main.animate().rotationBy(45);
        fabLayout_sub1.animate().translationY(-55);
        fabLayout_sub2.animate().translationY(-100);
        fabLayout_sub3.animate().translationY(-145);
    }

    private void showFABMenu() {
        isFabOnOff = true;
        fab_main.animate().rotationBy(-45);
        fabLayout_sub1.animate().translationY(0);
        fabLayout_sub2.animate().translationY(0);
        fabLayout_sub3.animate().translationY(0);
        fabLayout_sub3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                작성자 또는 권한이 1인 경우
                if(mypage_item.getPermit() == 1){
                    fabLayout_sub1.setVisibility(View.VISIBLE);
                    fabLayout_sub2.setVisibility(View.VISIBLE);
                    fabLayout_sub3.setVisibility(View.VISIBLE);
                } else {
                    fabLayout_sub1.setVisibility(View.VISIBLE);
                    fabLayout_sub2.setVisibility(View.VISIBLE);
                    fabLayout_sub3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(calendar_item.getSch_lat(), calendar_item.getSch_lot());
        // 해당 위치로 이동
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // 마크 찍기
        map.addMarker(new MarkerOptions().position(latLng)).showInfoWindow();
        // 카메라 줌
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }
}
