package com.example.parentalcontrol;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.Cache;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blankj.utilcode.util.ServiceUtils;
import com.example.parentalcontrol.service.CheckRunningAppService;
import com.example.parentalcontrol.utils.Constants;
import com.example.parentalcontrol.utils.TinyDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
//screen for user to select start date,time,end date,time
public class SelectTime extends AppCompatActivity implements View.OnClickListener {
    private Button startTime,endTime,btnStartDate,btnEendDate,done;
    private TextView tvStartTime,tvEndTime,tvStartDate,tvEndDate;
    private String strStartTime="";
    private String strEndTime="";
    Calendar startCalendar,endCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
        startTime=findViewById(R.id.btnStartTime);
        endTime=findViewById(R.id.btnEndTime);
        tvStartTime=findViewById(R.id.tvStartTime);
        tvEndTime=findViewById(R.id.tvEndTime);
        btnStartDate=findViewById(R.id.btnStartDate);
        btnEendDate=findViewById(R.id.btnEndDate);
        tvStartDate=findViewById(R.id.tvStartDate);
        tvEndDate=findViewById(R.id.tvEndDate);
        done=findViewById(R.id.btnDone);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        btnStartDate.setOnClickListener(this);
        btnEendDate.setOnClickListener(this);
        done.setOnClickListener(this);
        startCalendar=Calendar.getInstance();
        endCalendar=Calendar.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        //select start date,time end date,time
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        DatePickerDialog datePickerDialog;
        switch (view.getId()){
            case R.id.btnStartDate:
                datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        int realMonth=month+1;
                        startCalendar.set(Calendar.YEAR,year);
                        startCalendar.set(Calendar.MONTH,month);
                        startCalendar.set(Calendar.DATE,day);
                    tvStartDate.setText(year+"-"+realMonth+"-"+day);
                    }
                },mcurrentTime.get(Calendar.YEAR), mcurrentTime.get(Calendar.MONTH),
                        mcurrentTime.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(mcurrentTime.getTimeInMillis()-1000);
                datePickerDialog.show();
                break;
            case R.id.btnStartTime:
                mTimePicker = new TimePickerDialog(SelectTime.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        strStartTime=selectedHour + " " + selectedMinute;
                        startCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                        startCalendar.set(Calendar.MINUTE,selectedMinute);
                        startCalendar.set(Calendar.SECOND,0);
                        tvStartTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            case R.id.btnEndDate:
                datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        int realMonth=month+1;
                        endCalendar.set(Calendar.YEAR,year);
                        endCalendar.set(Calendar.MONTH,month);
                        endCalendar.set(Calendar.DATE,day);
                        tvEndDate.setText(year+"-"+realMonth+"-"+day);
                    }
                },mcurrentTime.get(Calendar.YEAR), mcurrentTime.get(Calendar.MONTH),
                        mcurrentTime.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(mcurrentTime.getTimeInMillis()-1000);
                datePickerDialog.show();
                break;
            case R.id.btnEndTime:
                mTimePicker = new TimePickerDialog(SelectTime.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        strEndTime=selectedHour + " " + selectedMinute;
                        endCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                        endCalendar.set(Calendar.MINUTE,selectedMinute);
                        endCalendar.set(Calendar.SECOND,0);
                        tvEndTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            case R.id.btnDone:
                if (tvStartTime.getText().toString().equals("")||tvStartTime.getText().toString().equals("")){
                    Toast.makeText(this, "Select time range", Toast.LENGTH_SHORT).show();
                }
                else {
                    TinyDB tinyDB=new TinyDB(SelectTime.this);
                    tinyDB.putString(Constants.START_TIME_AppLock,strStartTime);
                    tinyDB.putString(Constants.END_TIME_AppLock,strEndTime);
                    Date startDate=startCalendar.getTime();
                    Date endDate=endCalendar.getTime();
                    Log.e("starttime",startCalendar.getTime()+"");
                    Log.e("endtime",endCalendar.getTime()+"");
                    Log.e("starttimemillis",startCalendar.getTimeInMillis()+"");
                    Log.e("endtimemillis",endCalendar.getTimeInMillis()+"");
                    tinyDB.putLong(Constants.START_TIME_AppLock,startCalendar.getTimeInMillis());
                    tinyDB.putLong(Constants.END_TIME_AppLock,endCalendar.getTimeInMillis());
                    tinyDB.putBoolean(Constants.APP_LOCK_STATUS,true);
                    //when done button clicked start service
                    ServiceUtils.startService(CheckRunningAppService.class);
                    onBackPressed();
                }
                break;
        }
    }
}