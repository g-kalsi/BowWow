package com.codeninjas.bowwow.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.MainActivity;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.models.RemindersModel;
import com.codeninjas.bowwow.utils.AlarmBrodcast;
import com.codeninjas.bowwow.utils.Config;
import com.codeninjas.bowwow.utils.SqlHelperClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddReminderFragment extends BaseFragment {

    Spinner appointmentSpinner;
    TextView dateTV, timeTV;
    EditText addressET;
    String[] appointmentType = {"Grooming Appointment", "Medicine Time", "Doctor Appointment"};

    String timeTonotify, dateToNotify;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("Add Reminders", true, false, true);
        return inflater.inflate(R.layout.fragment_add_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appointmentSpinner = view.findViewById(R.id.appointmentSpinner);
        dateTV = view.findViewById(R.id.dateTV);
        timeTV = view.findViewById(R.id.timeTV);
        addressET = view.findViewById(R.id.addressET);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        ArrayAdapter coursesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, appointmentType);
        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        appointmentSpinner.setAdapter(coursesAdapter);
        view.findViewById(R.id.createBT).setOnClickListener(this);
        view.findViewById(R.id.timeTV).setOnClickListener(this);
        view.findViewById(R.id.dateTV).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.createBT) {
            if (isValid()) {
                String uid = baseActivity.store.getString(Config.userID);
                String appointmentType = appointmentSpinner.getSelectedItem().toString();
                String key = databaseReference.child(Config.reminders).child(uid).push().getKey();
                String message = "";
                String time = timeTV.getText().toString().trim();
                switch (appointmentType) {
                    case "Medicine Time" :
                        message = "Don't forget to eat medicine around " + time + ", " + dateToNotify;
                        break;
                    default:
                        message = appointmentType + " on " + time + ", " + dateToNotify + " at " + addressET.getText().toString().trim();
                        break;
                }

                RemindersModel remindersModel = new RemindersModel(appointmentType, dateToNotify, timeTonotify, message, key);
                processinsert(remindersModel);
                databaseReference.child(Config.reminders).child(uid).child(key).setValue(remindersModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showSnackBar("Reminder created successfully!");
                        baseActivity.onBackPressed();
                    }
                }).addOnFailureListener(e -> baseActivity.showSnackBar("Something went wrong! Please try again later."));


            }
        } else if (v.getId() == R.id.timeTV) {
            selectTime();
        } else if (v.getId() == R.id.dateTV) {
            selectDate();
        }

    }

    private void selectTime() {  //this method performs the time picker task
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(baseActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String formattedMinute;
                if (i1 / 10 == 0) {
                    formattedMinute = "0" + i1;
                } else {
                    formattedMinute = "" + i1;
                }

                timeTonotify = i + ":" + formattedMinute; //temp variable to store the time to set alarm
                timeTV.setText(FormatTime(i, i1)); //sets the button text as selected time
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectDate() {                                                                     //this method performs the date picker task
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(baseActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateTV.setText(day + "-" + (month + 1) + "-" + year);                             //sets the selected date as test for button
            }
        }, year, month, day);
        datePickerDialog.show();
        dateToNotify = day + "-" + (month + 1) + "-" + year;
    }

    public String FormatTime(int hour, int minute) {                                                //this method converts the time into 12hr format and assigns am or pm
        String time;
        time = "";
        String formattedMinute;
        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }
        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }
        return time;
    }

    private void processinsert(RemindersModel remindersModel) {
        long result = new SqlHelperClass(baseActivity).insertRemindersData(remindersModel);                  //inserts the title,date,time into sql lite database
        setAlarm(remindersModel);                                                                //calls the set alarm method to set alarm

    }

    private void setAlarm(RemindersModel remindersModel) {
        AlarmManager am = (AlarmManager) baseActivity.getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm
        Intent intent = new Intent(baseActivity, AlarmBrodcast.class);
        intent.putExtra("event", remindersModel.getMessage());                                                       //sending data to alarm class to create channel and notification
        intent.putExtra("time", remindersModel.getTime());
        intent.putExtra("date", remindersModel.getDate());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(baseActivity, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        String dateandtime = remindersModel.getDate() + " " + timeTonotify;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intentBack = new Intent(baseActivity, MainActivity.class);                //this intent will be called once the setting alarm is complete
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentBack);                                                                  //navigates from adding reminder activity to mainactivity
    }

    private boolean isValid() {

        if (dateToNotify.equals("")) {
            showSnackBar("Please select date for your reminder");
        } else if (timeTonotify.equals("")) {
            showSnackBar("Please select time for your reminder");
        } else {
            return true;
        }

        return false;
    }

}