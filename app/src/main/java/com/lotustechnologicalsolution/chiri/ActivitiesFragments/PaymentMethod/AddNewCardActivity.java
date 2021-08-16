package com.lotustechnologicalsolution.chiri.ActivitiesFragments.PaymentMethod;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;
import com.lotustechnologicalsolution.databinding.ActivityAddNewCardBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddNewCardActivity extends AppCompatActivity implements View.OnClickListener {

    private final Context mContext = this;
    Preferences preferences;
    Calendar calandarStartTime, calandarCurrentTime;
    String a;
    int keyDel;
    private ActivityAddNewCardBinding binding;
    private String month, year;
    private SparseArray<Pattern> mCCPatterns = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView((Activity) mContext, R.layout.activity_add_new_card);

        preferences = new Preferences(AddNewCardActivity.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setLocale("" + preferences.getKeyLocale());

        // For FileURIExposedException Handling
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // To Prevent NetworkOnMainThread Exception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (mCCPatterns == null) {
            mCCPatterns = new SparseArray<>();
            // Without spaces for credit card masking
            mCCPatterns.put(R.drawable.ic_visa_card, Pattern.compile("^4[0-9]{2,12}(?:[0-9]{3})?$"));
            mCCPatterns.put(R.drawable.ic_master_card, Pattern.compile("^5[1-5][0-9]{1,14}$"));
            mCCPatterns.put(R.drawable.ic_american_express, Pattern.compile("^3[47][0-9]{1,13}$"));
        }

        //  binding.edExpairyDate.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.btnAddNewCard.setOnClickListener(this);


        binding.edCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {

                int mDrawableResId = 0;
                for (int i = 0; i < mCCPatterns.size(); i++) {
                    int key = mCCPatterns.keyAt(i);
                    // get the object by the key.
                    Pattern p = mCCPatterns.get(key);

                    Matcher m = p.matcher((("" + text).replace(" ", "")));
                    if (m.find()) {
                        mDrawableResId = key;
                        break;
                    }
                }
                /*if (mDrawableResId > 0 && mDrawableResId != mCurrentDrawableResId) {
                    mCurrentDrawableResId = mDrawableResId;
                } else if (mDrawableResId == 0) {
                    mCurrentDrawableResId = mDefaultDrawableResId;
                }*/

                /*if (mCurrentDrawableResId==0)
                {
                    imgCardType.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_other_card));
                }
                else
                {
                    imgCardType.setImageDrawable(ContextCompat.getDrawable(view.getContext(),mCurrentDrawableResId));
                }*/

                boolean flag = true;
                String[] eachBlock = binding.edCardNumber.getText().toString().split(" ");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 4) {
                        flag = false;
                    }
                }
                if (flag) {

                    binding.edCardNumber.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((binding.edCardNumber.getText().length() + 1) % 5) == 0) {

                            if (binding.edCardNumber.getText().toString().split(" ").length <= 3) {
                                binding.edCardNumber.setText(binding.edCardNumber.getText() + " ");
                                binding.edCardNumber.setSelection(binding.edCardNumber.getText().length());
                            }
                        }
                        a = binding.edCardNumber.getText().toString();
                    } else {
                        a = binding.edCardNumber.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    binding.edCardNumber.setText(a);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


      /*  ExpiryDateTextWatcher expiryDateTextWatcher = new ExpiryDateTextWatcher();
        binding.edExpairyDate.addTextChangedListener(expiryDateTextWatcher);*/

        binding.edExpairyDate.addTextChangedListener(new TextWatcher() {
            String mLastInput = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String input = s.toString();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy", Locale.GERMANY);
                Calendar expiryDateDate = Calendar.getInstance();
                try {
                    expiryDateDate.setTime(formatter.parse(input));
                } catch (ParseException e) {

                    if (s.length() == 2 && !mLastInput.endsWith("/")) {
                        int month = Integer.parseInt(input);
                        if (month <= 12) {
                            binding.edExpairyDate.setText(binding.edExpairyDate.getText().toString() + "/");
                            binding.edExpairyDate.setSelection(binding.edExpairyDate.getText().toString().length());
                        }
                    } else if (s.length() == 2 && mLastInput.endsWith("/")) {
                        int month = Integer.parseInt(input);
                        if (month <= 12) {
                            binding.edExpairyDate.setText(binding.edExpairyDate.getText().toString().substring(0, 1));
                            binding.edExpairyDate.setSelection(binding.edExpairyDate.getText().toString().length());
                        } else {
                            binding.edExpairyDate.setText("");
                            binding.edExpairyDate.setSelection(binding.edExpairyDate.getText().toString().length());
                            Toast.makeText(getApplicationContext(), "Enter a valid month", Toast.LENGTH_LONG).show();
                        }
                    } else if (s.length() == 1) {
                        int month = Integer.parseInt(input);
                        if (month > 1) {
                            binding.edExpairyDate.setText("0" + binding.edExpairyDate.getText().toString() + "/");
                            binding.edExpairyDate.setSelection(binding.edExpairyDate.getText().toString().length());
                        }
                    } else {

                    }
                    mLastInput = binding.edExpairyDate.getText().toString();
                    return;
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
          /*  case R.id.edExpairyDate:
                METHOD_openDateTimePicker();
                break;*/
            case R.id.btnAddNewCard:
                Validate_card();
                break;
        }
    }

    private void METHOD_openDateTimePicker() {
        final View dialogView = View.inflate(mContext, R.layout.add_card_date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();


        LinearLayout btn_date_time = dialogView.findViewById(R.id.btn_datetime);
        DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());


        btn_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calandarStartTime = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth());

                SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                binding.edExpairyDate.setText("" + sdf.format(calandarStartTime.getTime()));
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
    }

    public void Validate_card() {


        if (binding.edCardNumber.getText().toString().isEmpty()) {
            binding.edCardNumber.setError(getString(R.string.cant_empty));
            return;
        }
        if (binding.edCardNumber.getText().toString().isEmpty()) {
            binding.edCardNumber.setError(getString(R.string.cant_empty));
            return;
        }
        if (binding.edCardNumber.getText().toString().length() != 19) {
            binding.edCardNumber.setError(getString(R.string.invalid_card_number));
            return;
        }
        if (binding.edCvvNumber.getText().toString().isEmpty()) {
            binding.edCvvNumber.setError(getString(R.string.cant_empty));
            return;
        }
        if (binding.edExpairyDate.getText().toString().isEmpty()) {
            Functions.ShowToast(mContext, "Enter valid card validity");
            return;
        }
        /*calandarCurrentTime = Calendar.getInstance();
        if (calandarCurrentTime.after(calandarStartTime) || calandarCurrentTime.compareTo(calandarStartTime) == 0) {
            Functions.ShowToast(mContext, mContext.getString(R.string.select_the_card_validity));
            return;
        }*/
        String expire_date_field = binding.edExpairyDate.getText().toString();

        if (expire_date_field.length() < 5 || expire_date_field.length() == 6) {
            Functions.ShowToast(mContext, "Enter valid card validity.");
            return;
        } else if (expire_date_field.length() <= 5) {
            month = expire_date_field.substring(0, 2);
            year = expire_date_field.substring(3, 5);
        } else if (expire_date_field.length() <= 7) {
            month = expire_date_field.substring(0, 2);
            year = expire_date_field.substring(3, 7);
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        if (year.length() == 2) {
            year = "20" + year + " ";
        }

        if (Integer.parseInt(year.trim()) < currentYear) {
            Functions.ShowToast(mContext, mContext.getString(R.string.select_the_card_validity));
            return;
        }

        if (Integer.parseInt(year.trim()) == currentYear && Integer.parseInt(month) < currentMonth) {
            Functions.ShowToast(mContext, mContext.getString(R.string.select_the_card_validity));
            return;
        }

        /*if (Integer.parseInt(year.trim()) == currentYear && Integer.parseInt(month) <= currentMonth) {
            Functions.ShowToast(mContext, mContext.getString(R.string.select_the_card_validity));
            return;
        }*/

        callAPIAddCardDetails();
    }

    private void callAPIAddCardDetails() {
        JSONObject setData = new JSONObject();
        try {
            setData.put("user_id", "" + preferences.getKeyUserId());
            setData.put("card_name", "" + binding.edNameOfCardHolder.getText().toString());
            setData.put("card_number", "" + binding.edCardNumber.getText().toString());
            setData.put("card_expiry_month", "" + month);
            setData.put("card_expiry_year", "" + year);
            setData.put("card_cvv", "" + binding.edCvvNumber.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(mContext, false, false);
        ApiRequest.CallApi(AddNewCardActivity.this, ApiUrl.saveUserCardDetails, setData, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {

                            Toast.makeText(mContext, respobj.getString("msg"), Toast.LENGTH_SHORT).show();
                            AddNewCardActivity.this.finish();

                        } else {
                            Functions.Show_Alert(mContext, "" + "Save Card Status", "" + respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}