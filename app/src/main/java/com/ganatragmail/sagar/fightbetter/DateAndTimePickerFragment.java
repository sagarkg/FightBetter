package com.ganatragmail.sagar.fightbetter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 *  Gets current date from Intent from CrimeFragment
 *  Inflates date and Time Picker dialog
 *  Helps user select desired date
 *  Returns the date to CrimeFragment using intent
 */
public class DateAndTimePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "criminal_intent.date";
    public final static String TAG = "DateTimeDialogFragment";

    public static int dateYear,dateDay,dateMonth, dateHour, dateMin;

    public Date mDate;

    //Initialize DatePicker to show current date (date from crime)
    public static DateAndTimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);

        DateAndTimePickerFragment fragment = new DateAndTimePickerFragment();
        fragment.setArguments(args);

        return fragment;


    }

    //Send results back to CrimeFragment
    private void sendResult (int resultCode) {

        if (getTargetFragment() == null){
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode, i);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

        //Create Calendar to get year month and date

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);

        int year =  calendar.get(Calendar.YEAR);
        int month=  calendar.get(Calendar.MONTH);
        int day_of_month =  calendar.get(Calendar.DAY_OF_MONTH);

        //New Code for time Picker -


        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        Log.d("TAG", "Time: " + hour + "Min: " + min);






        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        TimePicker timePicker = (TimePicker)v.findViewById(R.id.time_picker);

        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);
        DatePicker datePicker = (DatePicker)v.findViewById(R.id.date_picker);
        datePicker.init(year, month, day_of_month, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                dateYear = year;
                dateMonth = monthOfYear;
                dateDay = dayOfMonth;



            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                dateHour = hourOfDay;
                dateMin = minute;
            }
        });



                return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDate = new GregorianCalendar(dateYear, dateMonth, dateDay, dateHour, dateMin).getTime();

                        getArguments().putSerializable(EXTRA_DATE, mDate);
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();


    }


}
