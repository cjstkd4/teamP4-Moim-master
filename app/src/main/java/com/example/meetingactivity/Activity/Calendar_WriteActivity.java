package com.example.meetingactivity.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.meetingactivity.Fragment.CalendarFragment;
import com.example.meetingactivity.R;
import com.example.meetingactivity.helper.DateTimeHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Calendar_WriteActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    // 객체 선언
    EditText detail_write_name, detail_write_help, detial_write_money;
    Button detail_write_time, detail_write_bt1, detail_write_bt2;
    TextView detail_write_date;
    String str_year, str_month, str_day;
    int HOUR = 0, MINIUTE = 0;

    private GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    boolean permissionCK = false;    // 퍼미션 결과 저장
    Double lat, lot;    // 위도 경도를 알아내기 위해서 사용
    String location;    // 검색한 장소
    String time;

    int item_moimcode;

    // 통신
    AsyncHttpClient client;
    String calendar_write_url = "http://192.168.0.93:8080/moim.4t.spring/insertSchedule.tople";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar__write);

        detail_write_name = findViewById(R.id.detail_write_name);
        detail_write_date = findViewById(R.id.detail_write_date);
        detail_write_time = findViewById(R.id.detail_write_time);
        detail_write_help = findViewById(R.id.detail_write_help);
        detail_write_bt1 = findViewById(R.id.detail_write_bt1);
        detail_write_bt2 = findViewById(R.id.detail_write_bt2);
        detial_write_money = findViewById(R.id.detial_write_money);

        str_year = getIntent().getStringExtra("year");
        str_month = getIntent().getStringExtra("month");
        str_day = getIntent().getStringExtra("day");

        detail_write_date.setText(str_year + " - " + str_month + " - " + str_day);

        // 지도
        searchView = findViewById(R.id.searchView);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // Dialog에 출력하기 위해 현재 시스템 시간를 구하여 전역변수에 미리 셋팅
        int[] time = DateTimeHelper.getInstance().getTime();
        HOUR = time[0];
        MINIUTE = time[1];

        // 맵 실행
        MapActivity();
        mapFragment.getMapAsync(this);

        //모임 코드
        item_moimcode = getIntent().getIntExtra("moimcode", 0);
        client=new AsyncHttpClient();

        // 이벤트 설정
        detail_write_time.setOnClickListener(this);
        detail_write_bt1.setOnClickListener(this);
        detail_write_bt2.setOnClickListener(this);

        // 퍼미션 체크
        permissionCheck();
    }

    //    맵 실행 코드
    private void MapActivity() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색창에서 입력한 값을 location에 저장
                location = searchView.getQuery().toString();
                List<Address> addressesList = null;

                if (location != null || !location.equals("")) {
                    //
                    Geocoder geocoder = new Geocoder(Calendar_WriteActivity.this);
                    try {
                        // 검색으로 위도 경도 값 얻오는 방법
                        addressesList = geocoder.getFromLocationName(location, 1);
                        lat = addressesList.get(0).getLatitude();
                        lot = addressesList.get(0).getLongitude();
                    } catch (IOException e) {
                        // 주소 변환 실패시 실행
                        e.printStackTrace();
                        Toast.makeText(Calendar_WriteActivity.this, "해당 검색에 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Address address = addressesList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng).title(location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                } else {
                    return false;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
        Intent intent;
        switch (v.getId()) {
//          시간 Dialog
            case R.id.detail_write_time:
                showTimePickerDialog();
                break;
//                저장 버튼
            case R.id.detail_write_bt1:
                String sch_year = str_year;
                String sch_month = str_month;
                String sch_day = str_day;
                Log.d("[test]", "month : " + sch_month);
                String sch_title = detail_write_name.getText().toString().trim();
                String sch_time = time;
                String sch_sub = detail_write_help.getText().toString().trim();
                String sch_amount = detial_write_money.getText().toString().trim();
                Double sch_lat = lat;
                Double sch_lot = lot;

                // 입력값이 있으면, 서버로 데이터 전송 및 요청
                RequestParams params = new RequestParams();

                params.put("sch_moimcode", item_moimcode);
                params.put("sch_year", sch_year);
                params.put("sch_month", sch_month);
                params.put("sch_day", sch_day);
                params.put("sch_title", sch_title);
                params.put("sch_time", sch_time);
                params.put("sch_sub", sch_sub);
                params.put("sch_amount", sch_amount);
                params.put("sch_lat", sch_lat);
                params.put("sch_lot", sch_lot);

                client.post(calendar_write_url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String strJson = new String(responseBody);
                        try {
                            JSONObject json = new JSONObject(strJson);
                            String rt  = json.getString("result");
                            if(rt.equals("OK")){
                                Toast.makeText(Calendar_WriteActivity.this ,"저장성공",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(Calendar_WriteActivity.this ,"저장실패",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    }
                });
                finish();
                break;
//                취소 버튼
            case R.id.detail_write_bt2:
                finish();
                break;
        }
    }

    //    시간 dialog
    private void showTimePickerDialog() {
        // 원본 데이터 백업
        final int temp_hh = HOUR;
        final int temp_nn = MINIUTE;
        // TimePickerDialog 객체 생성
        // TimePickerDialog(Context, 이벤트 핸들러, 시, 분, 24시간제 사용여부)
        // 24시간제 사용여부 : treu=24시간제, false=12시간제
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // 사용자가 선택한 값을 전역변수에 저장
                HOUR = hourOfDay;
                MINIUTE = minute;
                detail_write_time.setText(HOUR + ":" + MINIUTE);
            }
        }, HOUR, MINIUTE, false);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                HOUR = temp_hh;
                MINIUTE = temp_nn;
            }
        });
        dialog.setTitle("시간 선택");
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setMessage("약속시간을 선택하세요.");
        dialog.setCancelable(false);
        dialog.show();
        time = HOUR + ":" + MINIUTE;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}
