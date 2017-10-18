package com.example.senzec.datingapp.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.senzec.datingapp.R;
import com.example.senzec.datingapp.activities.MainActivity;

import java.util.Date;
import java.util.Locale;

import static com.example.senzec.datingapp.R.id.toolbar;
import static com.example.senzec.datingapp.R.id.view;

/**
 * Created by power hashing on 4/15/2017.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {
    View view;
    private Calendar myCalendar;
    private EditText et_current_location,et_future_location,et_event_name,et_start_date,et_event_location;
    Switch switch_public_private,switch_men,switch_women;
    TextView tv_switch_public_private,tv_distance_value,tv_age_value,tv_distance_value_future,tv_time_in_days_value,
            picker_date_future_distance,tv_event_start_date;
    SeekBar seekbar_distance,seekbar_age,seekbar_future_distance,seekbar_time;
    Spinner spnr_event;
    Button btn_submit_setting_detail,btn_delete_account;
    String setting_gender;
    int min_distance=0;
    int max_distance = 100;
    int min_age = 18;
    int max_age = 55;

    private int whoHasFocus = 0;
    int mYear, mMonth, mDay;
    SettingFragmentCommunicator communicator;

    public void setSettingFragmentCommunicator(SettingFragmentCommunicator communicator){
        this.communicator = communicator;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        init();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(
                "SETTINGS");
        return view;
    }

    public void init() {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        switch_public_private = (Switch) view.findViewById(R.id.switch_public_private);
        switch_men = (Switch) view.findViewById(R.id.switch_men);
        switch_women = (Switch) view.findViewById(R.id.switch_women);

        tv_distance_value = (TextView) view.findViewById(R.id.tv_distance_value);
        tv_age_value = (TextView) view.findViewById(R.id.tv_age_value);
        tv_distance_value_future = (TextView) view.findViewById(R.id.tv_distance_value_future);
        tv_time_in_days_value = (TextView) view.findViewById(R.id.tv_time_in_days_value);
        picker_date_future_distance = (TextView) view.findViewById(R.id.picker_date_future_distance);
        tv_event_start_date = (TextView) view.findViewById(R.id.tv_event_start_date);

        seekbar_distance = (SeekBar) view.findViewById(R.id.seekbar_distance);
        seekbar_age = (SeekBar) view.findViewById(R.id.seekbar_age);
        seekbar_future_distance = (SeekBar) view.findViewById(R.id.seekbar_future_distance);
        seekbar_time = (SeekBar) view.findViewById(R.id.seekbar_time);

        spnr_event = (Spinner) view.findViewById(R.id.spnr_event);

        btn_submit_setting_detail = (Button) view.findViewById(R.id.btn_submit_setting_detail);
        btn_delete_account = (Button) view.findViewById(R.id.btn_delete_account);

        tv_switch_public_private = (TextView) view.findViewById(R.id.tv_switch_public_private);


        tv_distance_value.setOnClickListener(this);
        tv_age_value.setOnClickListener(this);
        tv_distance_value_future.setOnClickListener(this);
        tv_time_in_days_value.setOnClickListener(this);
        picker_date_future_distance.setOnClickListener(this);

        tv_event_start_date.setOnClickListener(this);
        seekbar_distance.setOnClickListener(this);
        seekbar_age.setOnClickListener(this);
        seekbar_future_distance.setOnClickListener(this);
        seekbar_time.setOnClickListener(this);

        btn_submit_setting_detail.setOnClickListener(this);
        btn_delete_account.setOnClickListener(this);
        tv_switch_public_private.setOnClickListener(this);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switch_public_private.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    tv_switch_public_private.setText("Private");
                    switch_public_private.setChecked(true);
                }else{
                    tv_switch_public_private.setText("Public");
                }
            }
        });

        switch_men.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switch_men.isChecked()){
                    setting_gender = "men";
//                    switch_women.setChecked(false);
                }
            }
        });

        switch_women.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switch_women.isChecked()){
                    setting_gender = "women";
//                    switch_men.setChecked(false);
                }
            }
        });

        seekbar_distance.setMax(max_distance);
        seekbar_distance.setProgress(min_distance);
        seekbar_distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_distance_value.setText(i+" kms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar_age.setMax(max_age);
        seekbar_age.setProgress(min_age);
        seekbar_age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int maxAgeVale = i+5;
                tv_age_value.setText(i+"-"+maxAgeVale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar_future_distance.setMax(max_distance);
        seekbar_future_distance.setProgress(min_distance);
        seekbar_future_distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_distance_value_future.setText(i+" km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar_time.setMax(max_distance);
        seekbar_time.setProgress(min_distance);
        seekbar_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_time_in_days_value.setText(i+" days");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.picker_date_future_distance:
                    showFutureDate();
                break;
            case R.id.tv_event_start_date:
                showEventCreateDate();
                break;
            case R.id.btn_submit_setting_detail:
                communicator.submitSettingDetails();
                break;
            case R.id.btn_delete_account:
                communicator.deleteAccount();
                break;
        }
    }

    // ******* its only work from min api level 24.*******//
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        String strDate = "Current Date : " + mdformat.format(calendar.getTime());
        display(strDate);
    }

    private void display(String num) {

        picker_date_future_distance.setText(num);
    }

    private void showFutureDate(){
        final java.util.Calendar c = java.util.Calendar.getInstance();
        mYear = c.get(java.util.Calendar.YEAR);
        mMonth = c.get(java.util.Calendar.MONTH);
        mDay = c.get(java.util.Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        picker_date_future_distance.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        java.util.Calendar d = java.util.Calendar.getInstance();

        datePickerDialog.updateDate(d.get(java.util.Calendar.YEAR),d.get(java.util.Calendar.MONTH),d.get(java.util.Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showEventCreateDate(){
        final java.util.Calendar c = java.util.Calendar.getInstance();
        mYear = c.get(java.util.Calendar.YEAR);
        mMonth = c.get(java.util.Calendar.MONTH);
        mDay = c.get(java.util.Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        tv_event_start_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        java.util.Calendar d = java.util.Calendar.getInstance();

        datePickerDialog.updateDate(d.get(java.util.Calendar.YEAR),d.get(java.util.Calendar.MONTH),d.get(java.util.Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public interface SettingFragmentCommunicator{
        void submitSettingDetails();
        void deleteAccount();
    }
}
