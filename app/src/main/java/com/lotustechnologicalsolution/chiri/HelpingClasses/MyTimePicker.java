package com.lotustechnologicalsolution.chiri.HelpingClasses;

import android.app.TimePickerDialog;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyTimePicker implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    private EditText mEditText;
    private Calendar mCalendar;
    private MaterialTextView textView;
    private SimpleDateFormat mFormat;

    public MyTimePicker(EditText editText){
        this.mEditText = editText;
        mEditText.setOnFocusChangeListener(this);
        mEditText.setOnClickListener(this);
    }

    public MyTimePicker(MaterialTextView textView){
        this.textView = textView;
        textView.setOnFocusChangeListener(this);
        textView.setOnClickListener(this);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus){
            showPicker(view);
        }
    }

    @Override
    public void onClick(View view) {
        showPicker(view);
    }

    private void showPicker(View view) {
        if (mCalendar == null)
            mCalendar = Calendar.getInstance();

        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        new TimePickerDialog(view.getContext(), this, hour, minute, true).show();
    }


    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);

        if (mFormat == null)
            mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (mEditText != null)
            this.mEditText.setText(mFormat.format(mCalendar.getTime()));
        if (textView != null)
        this.textView.setText(mFormat.format(mCalendar.getTime()));
    }
}