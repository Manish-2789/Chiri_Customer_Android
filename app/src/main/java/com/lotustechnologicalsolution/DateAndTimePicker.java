package com.lotustechnologicalsolution;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.ModelClasses.DateAndTimeModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


public class DateAndTimePicker extends Fragment implements View.OnClickListener {
    View view;
    Preferences preferences;
    DatePicker datePicker;
    TimePicker timePicker;
    LinearLayout btnDateTime, btnOk, custom, btn_checkout;
    TextView txtDateTime;
    TextView textView;
    private String goodsTypeId = "", pickupDate = "", pickupDateShow = "";
    String startTime = "", startTimeShow = "";
    private String date;
    Bundle bundle2 = new Bundle();
    private String time;
    Calendar calandarCurrentTime, calandarStartTime;
    int state = 0;
    private ImageView ivBack;
    DateAndTimeModel model;

    private RelativeLayout  tabPickupDateTime, tabTitlePickupDatetime;

    private TextView tvDeliveryType, tvGoodsType, tvPickupTime, tvPickupDate,
            tvItemsName, tvDiscount, tvDeliveryCharges;
    private TextView txtNext, aditionalDetailsTextCount;
    private Bundle bundle;
    private LinearLayout llFurtherItemDetails, llProceedDetails;
    private EditText etItemName, etPromo;
    private ImageView calandarHelpRight;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_date_and_time_picker, container, false);
        METHOD_init_views();
        model = new DateAndTimeModel();
        return view;
    }

    private void METHOD_init_views() {
        ivBack = view.findViewById(R.id.iv_back);
        custom = view.findViewById(R.id.custom);
        btn_checkout = view.findViewById(R.id.btn_checkout);
        ivBack.setOnClickListener(this);
        custom.setOnClickListener(this);
        btn_checkout.setOnClickListener(this);
        preferences = new Preferences(view.getContext());

        tvPickupDate = view.findViewById(R.id.tv_pickup_date);
        tvPickupTime = view.findViewById(R.id.tv_pickup_time);

        calandarHelpRight =view.findViewById(R.id.calandar_help_right);

        tabPickupDateTime = view.findViewById(R.id.tab_pickup_date_time);
        tabPickupDateTime.setOnClickListener(this);
        tabTitlePickupDatetime =view.findViewById(R.id.tab_title_pickup_datetime);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;

            case R.id.custom:
                tabPickupDateTime.setVisibility(View.VISIBLE);
//                METHOD_openDateTimePicker();
                break;

            case R.id.btn_checkout:
                 addDate();
                break;

            case R.id.tab_pickup_date_time:
                METHOD_openDateTimePicker();
                break;
        }

    }

    private void addDate() {
        if(tvPickupDate != null){
            checkoutFragment frg_ment = new checkoutFragment();
            FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putSerializable("DateAndTimeData",model);
            frg_ment.setArguments(args);
            transaction.addToBackStack("RegisterPhoneNo_A");
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            transaction.replace(R.id.fl_id, frg_ment,"RegisterPhoneNo_A").commit();

        }


    }

    private AlertDialog date_time_alertDialog;
    private View date_time_dialogView;

    private void METHOD_openDateTimePicker() {
        date_time_dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        date_time_alertDialog = new AlertDialog.Builder(getActivity()).create();

        btnDateTime = date_time_dialogView.findViewById(R.id.btn_datetime);
        txtDateTime = date_time_dialogView.findViewById(R.id.txt_datetime);
        txtDateTime.setText(""+view.getContext().getString(R.string.select_date));
        btnOk = date_time_dialogView.findViewById(R.id.btn_ok);

        datePicker = (DatePicker) date_time_dialogView.findViewById(R.id.date_picker);
        timePicker = (TimePicker) date_time_dialogView.findViewById(R.id.time_picker);
        textView = (TextView) date_time_dialogView.findViewById(R.id.textview);
        timePicker.setIs24HourView(false);


        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
        datePicker.setVisibility(View.VISIBLE);
        timePicker.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);
        textView.setText("");
        state = 0;

        btnDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (state == 0){
                    METHOD_selectPickupDate();
                }else if(state == 1){
                    METHOD_selectPickupStartTime(date_time_alertDialog);
                }

            }});

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state==0)
                {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.GONE);
                }
                else if (state==1)
                {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                }

                btnDateTime.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                btnOk.setVisibility(View.GONE);
            }
        });
        calandarHelpRight.setFocusable(false);
        calandarHelpRight.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.newColorLightHint), android.graphics.PorterDuff.Mode.MULTIPLY);

        date_time_alertDialog.setView(date_time_dialogView);
        date_time_alertDialog.setCancelable(true);
        date_time_alertDialog.setCanceledOnTouchOutside(true);
        date_time_alertDialog.show();
    }

    private void METHOD_selectPickupStartTime(AlertDialog alertDialog) {
        datePicker.setVisibility(View.GONE);
        timePicker.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);

        txtDateTime.setText(R.string.select_pickup_time);

        calandarStartTime = new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(),
                timePicker.getCurrentMinute());



        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
        SimpleDateFormat sdf_show = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        calandarCurrentTime = Calendar.getInstance();


        if (calandarCurrentTime.after(calandarStartTime) || calandarCurrentTime.compareTo(calandarStartTime)==0)
        {
            textView.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.GONE);
            datePicker.setVisibility(View.GONE);
            textView.setText(view.getContext().getString(R.string.you_select_past_time_send));

            btnOk.setVisibility(View.VISIBLE);
            btnDateTime.setVisibility(View.GONE);
            startTime = "";
            startTimeShow = "";
            tvPickupTime.setText("");
            txtDateTime.setText(R.string.select_pickup_time);
            state = 1;
        }
        else
        {
            startTime = ""+sdf.format(calandarStartTime.getTimeInMillis());
            startTimeShow = ""+sdf_show.format(calandarStartTime.getTimeInMillis());
            model.setTime(startTimeShow);
            tvPickupTime.setText(startTimeShow);
            txtDateTime.setText(R.string.select_date);
            state =0;
            textView.setText("");
            alertDialog.dismiss();
        }


    }

    private void METHOD_selectPickupDate() {
        datePicker.setVisibility(View.VISIBLE);
        timePicker.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);

        calandarStartTime = new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth());

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        tabTitlePickupDatetime.setVisibility(View.VISIBLE);

        pickupDate = ""+date_format.format(calandarStartTime.getTime());
        pickupDateShow = ""+sdf.format(calandarStartTime.getTime());
        model.setDate(pickupDate);
        tvPickupDate.setText(""+ pickupDateShow);

        datePicker.setVisibility(View.GONE);
        timePicker.setVisibility(View.VISIBLE);

        state = 1;
        txtDateTime.setText(R.string.select_pickup_time);

        startTime = "";
        startTimeShow = "";
        tvPickupTime.setText("");

    }


}