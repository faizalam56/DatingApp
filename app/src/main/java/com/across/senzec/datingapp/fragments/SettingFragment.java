package com.across.senzec.datingapp.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.activities.MainActivity;
import com.across.senzec.datingapp.autocomplete.PlaceJSONParser;
import com.across.senzec.datingapp.controller.AppController;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.requestmodel.CreateEventRequest;
import com.across.senzec.datingapp.requestmodel.UpdateSettingRequest;
import com.across.senzec.datingapp.responsemodel.FetchEventResponse;
import com.across.senzec.datingapp.responsemodel.SettingDataResponse;
import com.across.senzec.datingapp.services.GPSTracker;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * Created by power hashing on 4/15/2017.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {
    View view;
    private Calendar myCalendar;
    private EditText et_event_name,et_start_date;//et_current_location,et_future_location,et_event_location
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
    AppPrefs prefs;
    private int whoHasFocus = 0;
    int mYear, mMonth, mDay;
    SettingFragmentCommunicator communicator;
    SettingDataResponse resource;
    FetchEventResponse eventResource;
    GPSTracker gpsTracker;
    Double latitude,longitude;

    AutoCompleteTextView auto_complete_tv_current_location,auto_complete_tv_future_location,auto_complete_tv_event_location;
    PlacesTask placesTask;
    ParserTask parserTask;

    public void setSettingFragmentCommunicator(SettingFragmentCommunicator communicator){
        this.communicator = communicator;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resource = (SettingDataResponse) getArguments().getSerializable("SettingDataResponse");
//        eventResource = (FetchEventResponse) getArguments().getSerializable("FetchEventResponse");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        init();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(
                "SETTINGS");
        prefs = AppController.getInstance().getPrefs();
        return view;
    }

    public void init() {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        switch_public_private = (Switch) view.findViewById(R.id.switch_public_private);
        switch_men = (Switch) view.findViewById(R.id.switch_men);
        switch_women = (Switch) view.findViewById(R.id.switch_women);

        auto_complete_tv_current_location = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_tv_current_location);
        auto_complete_tv_future_location = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_tv_future_location);
        tv_distance_value = (TextView) view.findViewById(R.id.tv_distance_value);
        tv_age_value = (TextView) view.findViewById(R.id.tv_age_value);
        tv_distance_value_future = (TextView) view.findViewById(R.id.tv_distance_value_future);
        tv_time_in_days_value = (TextView) view.findViewById(R.id.tv_time_in_days_value);
        picker_date_future_distance = (TextView) view.findViewById(R.id.picker_date_future_distance);

        tv_event_start_date = (TextView) view.findViewById(R.id.tv_event_start_date);
        et_event_name = (EditText) view.findViewById(R.id.et_event_name);
        auto_complete_tv_event_location = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_tv_event_location);
        tv_switch_public_private = (TextView) view.findViewById(R.id.tv_switch_public_private);
        spnr_event = (Spinner) view.findViewById(R.id.spnr_event);

        seekbar_distance = (SeekBar) view.findViewById(R.id.seekbar_distance);
        seekbar_age = (SeekBar) view.findViewById(R.id.seekbar_age);
        seekbar_future_distance = (SeekBar) view.findViewById(R.id.seekbar_future_distance);
        seekbar_time = (SeekBar) view.findViewById(R.id.seekbar_time);


        btn_submit_setting_detail = (Button) view.findViewById(R.id.btn_submit_setting_detail);
        btn_delete_account = (Button) view.findViewById(R.id.btn_delete_account);



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

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothicb.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));

        picker_date_future_distance.setText(getTodayDate1());
        switch_public_private.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    tv_switch_public_private.setText("private");
                    switch_public_private.setChecked(true);
                }else{
                    tv_switch_public_private.setText("public");
                }
            }
        });

        switch_men.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switch_men.isChecked()){
                    setting_gender = "male";
//                    switch_women.setChecked(false);
                }else if(switch_men.isChecked()&&switch_women.isChecked()){
                    setting_gender = "both";
                }else if(!switch_men.isChecked()&&!switch_women.isChecked()){
                    setting_gender = "none";
                }
            }
        });

        switch_women.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switch_women.isChecked()){
                    setting_gender = "female";
//                    switch_men.setChecked(false);
                }else if(switch_men.isChecked()&&switch_women.isChecked()){
                    setting_gender = "both";
                }else if(!switch_men.isChecked()&&!switch_women.isChecked()){
                    setting_gender = "none";
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

                int maxAgeVale,minAgevalue;

                minAgevalue = i;
                maxAgeVale = minAgevalue+5;
                tv_age_value.setText(minAgevalue+"-"+maxAgeVale);
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

        seekbar_time.setMax(max_distance-1);
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

        if(resource!=null){
            setSettingDetails();
        }

        if(eventResource!=null){
            setEventDetails();
        }

        findGpsLocation();

        //************************For Auto Complete***************************************
        auto_complete_tv_current_location.setThreshold(1);
        auto_complete_tv_current_location.addTextChangedListener(new MyTextWatcher(auto_complete_tv_current_location));

        auto_complete_tv_future_location.setThreshold(1);
        auto_complete_tv_future_location.addTextChangedListener(new MyTextWatcher(auto_complete_tv_future_location));

        auto_complete_tv_event_location.setThreshold(1);
        auto_complete_tv_event_location.addTextChangedListener(new MyTextWatcher(auto_complete_tv_event_location));

    }

    class MyTextWatcher implements TextWatcher{
        AutoCompleteTextView autoCompleteTextView;
        public MyTextWatcher(AutoCompleteTextView autoCompleteTextView){
            this.autoCompleteTextView=autoCompleteTextView;
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            placesTask = new PlacesTask(this.autoCompleteTextView);
            placesTask.execute(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }
    }

    private void setSettingDetails(){

        SettingDataResponse.Response response = resource.response;
        SettingDataResponse.Setting settingData = response.setting;
        if(settingData!=null){
            auto_complete_tv_current_location.setText(settingData.current_loc_name);
            tv_distance_value.setText(settingData.current_loc_distance+" km");
            tv_age_value.setText(settingData.age_range_start+"-"+settingData.age_range_end);

            int current_distance = Integer.parseInt(settingData.current_loc_distance);
            seekbar_distance.setProgress(current_distance);
            int age_start = Integer.parseInt(settingData.age_range_start);
            seekbar_age.setProgress(age_start);

            if(settingData.show_me.equalsIgnoreCase("male")){
                switch_men.setChecked(true);
            }else if(settingData.show_me.equalsIgnoreCase("female")){
                switch_women.setChecked(true);
            }else if(settingData.show_me.equalsIgnoreCase("both")){
                switch_men.setChecked(true);
                switch_women.setChecked(true);
            }

            auto_complete_tv_future_location.setText(settingData.future_loc_name);
            tv_distance_value_future.setText(settingData.future_loc_distance+" km");

            int future_distance = Integer.parseInt(settingData.future_loc_distance);
            seekbar_future_distance.setProgress(future_distance);

            picker_date_future_distance.setText(getDate(settingData.future_loc_start,"dd/MM/yyyy"));

            seekbar_time.setProgress(futureTime(settingData));
        }
    }

    private int futureTime(SettingDataResponse.Setting response){
        int ftr_endTime = Integer.parseInt(response.future_loc_end);
        int ftr_startTime = Integer.parseInt(response.future_loc_start);

        int dayTimeInSeconds = ftr_endTime-ftr_startTime;
        int day = dayTimeInSeconds/(24*60*60);
        return day;
    }

    String[] eventName;
    private void setEventDetails(){
        if(eventResource.response!=null){
            List<FetchEventResponse.Response> event_responce =eventResource.response;
            eventName = new String[event_responce.size()+1];
            eventName[0] = "Select Event";
            for(int i=0;i<event_responce.size();i++){
                FetchEventResponse.Response response=event_responce.get(i);
                eventName[i+1] = response.name;
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, eventName) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View v = null;
                    // If this is the initial dummy entry, make it hidden
                    if (position == 0) {
                        TextView tv = new TextView(getContext());
                        tv.setHeight(0);
                        tv.setVisibility(View.GONE);
                        v = tv;
                    }
                    else {
                        // Pass convertView as null to prevent reuse of special case views
                        v = super.getDropDownView(position, null, parent);
                    }
                    // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                    parent.setVerticalScrollBarEnabled(false);
                    return v;
                }
            };

            if(eventName.length>0) {
                spnr_event.setAdapter(dataAdapter);
//                spnr_event.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, eventName));
                spnr_event.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0){

                        }else {
                            setEventFormData(eventResource.response.get(position-1));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }

    }

    String eventId;
    private void setEventFormData(FetchEventResponse.Response response){
        tv_event_start_date.setText(getDate(response.time,"dd/MM/yyyy"));
        et_event_name.setText(response.name);
        auto_complete_tv_event_location.setText(response.venue);
        tv_switch_public_private.setText(response.type);
        eventId = response.id;
    }


    private String getTodayDate(){
        String currentDateTimeString = SimpleDateFormat.getDateTimeInstance().format(new Date());
        return currentDateTimeString;
    }
    public static String getTodayDate1(){
        Date now = new Date();
        Date alsoNow = Calendar.getInstance().getTime();
        String nowAsString = new SimpleDateFormat("dd/MM/yyyy").format(now);
        return nowAsString;
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
                if(auto_complete_tv_current_location.getText().length()==0){
                    Toast.makeText(getActivity(),"Please enter the current location",Toast.LENGTH_SHORT).show();
                }else if(auto_complete_tv_future_location.getText().length()==0){
                    Toast.makeText(getActivity(),"Please enter the future location",Toast.LENGTH_SHORT).show();
                }else if(show_me()==null){
                    Toast.makeText(getActivity(),"Please enter show me option",Toast.LENGTH_SHORT).show();
                }else if(et_event_name.getText().length()==0
                        &&auto_complete_tv_event_location.getText().length()==0
                        &&tv_event_start_date.getText().length()==0 ){

                    communicator.submitSettingDetails(getUpdateSettingDetails());
                }else if((spnr_event.getSelectedItem().toString().equalsIgnoreCase("Select Event"))
                        &&et_event_name.getText().length()>0
                        &&auto_complete_tv_event_location.getText().length()>0
                        &&tv_event_start_date.getText().length()>0){

                    communicator.submitSettingDetails(getUpdateSettingDetails());
                    communicator.createEvent(getCreateEventDetails());
                }else if(!(spnr_event.getSelectedItem().toString().equalsIgnoreCase("Select Event"))
                        &&et_event_name.getText().length()>0
                        &&auto_complete_tv_event_location.getText().length()>0
                        &&tv_event_start_date.getText().length()>0){
                    communicator.submitSettingDetails(getUpdateSettingDetails());
                    communicator.updateEvent(getCreateEventDetails(),eventId);
                }
                else {
                    showErrorDialog("If you want to create an event then please enter event details.");
                }
                break;

            case R.id.btn_delete_account:
                communicator.deleteAccount();
                break;
        }
    }

    private void showErrorDialog(String message){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
        dialog.setTitle("ALERT");
        dialog.setMessage(message);
        dialog.setIcon(R.mipmap.logo_icon);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private int getFutureLocEndDateinTime(){
//        int futureTimeDay = Integer.parseInt(tv_time_in_days_value.getText().toString().substring(0,2));
        String[] TimeDay = tv_time_in_days_value.getText().toString().split(" ");
        int futureTimeDay = Integer.parseInt(TimeDay[0]);
        int futureTimeinSecond = daytoSeconds(futureTimeDay);
        int futureLocStartDateinTimeSecond = getDatetime(picker_date_future_distance.getText().toString());

        return futureTimeinSecond+futureLocStartDateinTimeSecond;
    }
    public int daytoSeconds(int day)
    {
        return day * 24 * 60 * 60;
    }

    private UpdateSettingRequest getUpdateSettingDetails(){
        ageRange();
        UpdateSettingRequest updateSettingRequest=new UpdateSettingRequest();
        /*updateSettingRequest.age_range_end=tv_age_value.getText().toString().substring(3);
        updateSettingRequest.age_range_start=tv_age_value.getText().toString().substring(0,tv_age_value.getText().toString().indexOf('-'));*/
        updateSettingRequest.age_range_end=endAge;
        updateSettingRequest.age_range_start=startAge;
        updateSettingRequest.current_loc_distance=tv_distance_value.getText().toString().substring(0,tv_distance_value.getText().toString().indexOf(' '));
        updateSettingRequest.current_loc_lat=latitude;
        updateSettingRequest.current_loc_lon=longitude;
        updateSettingRequest.current_loc_name=auto_complete_tv_current_location.getText().toString();
        updateSettingRequest.future_loc_distance=tv_distance_value_future.getText().toString().substring(0,tv_distance_value_future.getText().toString().indexOf(' '));
        updateSettingRequest.future_loc_end= String.valueOf(getFutureLocEndDateinTime());
        updateSettingRequest.future_loc_lat=latitude;
        updateSettingRequest.future_loc_lon=longitude;
        updateSettingRequest.future_loc_name=auto_complete_tv_future_location.getText().toString();
        updateSettingRequest.future_loc_start= String.valueOf(getDatetime(picker_date_future_distance.getText().toString()));
        updateSettingRequest.show_me=show_me();
        updateSettingRequest.username=prefs.getString(App.Key.ID_LOGGED);

        return updateSettingRequest;
    }

    private CreateEventRequest getCreateEventDetails(){
        CreateEventRequest createEventRequest=new CreateEventRequest();
        createEventRequest.latitude=latitude;
        createEventRequest.longitude=longitude;
        createEventRequest.name=et_event_name.getText().toString();
        createEventRequest.time= String.valueOf(getDatetime(tv_event_start_date.getText().toString()));
        createEventRequest.type=tv_switch_public_private.getText().toString();
        createEventRequest.venue=auto_complete_tv_event_location.getText().toString();
        return createEventRequest;
    }

    String startAge,endAge;
    private void ageRange(){
        String[] age = tv_age_value.getText().toString().split("-");
        startAge = age[0];
        endAge = age[1];
    }

    private String show_me(){
        if(switch_men.isChecked()&&!switch_women.isChecked()){
            return "male";
        }else if(switch_women.isChecked()&&!switch_men.isChecked()){
            return "female";
        }else if(switch_women.isChecked()&&switch_men.isChecked()){
            return "both";
        }
        return null;
    }
    private void findGpsLocation(){
        gpsTracker = new GPSTracker(getContext());
        if(gpsTracker.canGetLocation()){
            longitude = gpsTracker.getLongitude();
            latitude = gpsTracker.getLatitude();
//            Toast.makeText(getContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            gpsTracker.showSettingsAlert();
        }
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

                        picker_date_future_distance.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

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

                        tv_event_start_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        java.util.Calendar d = java.util.Calendar.getInstance();

        datePickerDialog.updateDate(d.get(java.util.Calendar.YEAR),d.get(java.util.Calendar.MONTH),d.get(java.util.Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private static String getDate(String dobInTime, String dateFormat){

        Long milliSeconds = Long.parseLong(dobInTime)*1000;
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private int getDatetime(String selectDate){
        String str_date=selectDate;
        SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = (Date)formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Today is " +(int) (date.getTime()/1000));
        return (int) (date.getTime()/1000);
    }

    public static void getDatetimeFayz(String date){

        Date date1= null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int i = (int) (date1.getTime()/1000);
        System.out.println("Integer : " + i);
        System.out.println("Long : "+ new Date().getTime());
        System.out.println("Long date : " + new Date(new Date().getTime()));
        System.out.println("Int Date : " + new Date(((long)i)*1000L));
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();
            System.out.println("Data "+data);
            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {
       AutoCompleteTextView autoCompleteTextView;
        PlacesTask(AutoCompleteTextView autoCompleteTextView){
            this.autoCompleteTextView=autoCompleteTextView;
        }
        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyBJ4s7dYFq5cswr4f7u3vbv03cCcbw92tA";

            String input="";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input+"&"+types+"&"+sensor+"&"+key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

            try{
                // Fetching the data from web service in background
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask(this.autoCompleteTextView);

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }


    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>> {

        JSONObject jObject;
        AutoCompleteTextView autoCompleteTextView;
        ParserTask(AutoCompleteTextView autoCompleteTextView){
            this.autoCompleteTextView=autoCompleteTextView;
        }
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getContext(), result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            this.autoCompleteTextView.setAdapter(adapter);
        }
    }


    public interface SettingFragmentCommunicator{
        void submitSettingDetails(UpdateSettingRequest updateSettingRequest);
        void updateEvent(CreateEventRequest createEventRequest,String eventId);
        void createEvent(CreateEventRequest createEventRequest);
        void deleteAccount();
    }
}
