package com.lotustechnologicalsolution.chiri.ActivitiesFragments.DeliveryDetails;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome.FinalDeliveryA;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome.FinalDeliveryF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.OrderCreate.PaymentWebViewA;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.PaymentMethod.AddNewCardActivity;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Variables;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;
import com.lotustechnologicalsolution.chiri.ModelClasses.CardModel;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryDetails;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryModeModel;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryTypeModel;
import com.lotustechnologicalsolution.chiri.ModelClasses.ProductModel;
import com.lotustechnologicalsolution.chiri.RecyclerViewAdapters.DebitCreditCardAdapter;
import com.lotustechnologicalsolution.chiri.RecyclerViewAdapters.DeliveryModeAdapter;
import com.lotustechnologicalsolution.chiri.RecyclerViewAdapters.DeliveryTypeAdapter;
import com.lotustechnologicalsolution.chiri.RecyclerViewAdapters.ProductCategoryAdapter;
import com.lotustechnologicalsolution.databinding.FragmentCheckoutNewBinding;
import com.ycuwq.datepicker.WheelPicker;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.lotustechnologicalsolution.chiri.HelpingClasses.Functions.ShowToast;

public class CheckoutNewF extends RootFragment implements View.OnClickListener,
        DebitCreditCardAdapter.CardItemSelectListener,
        DeliveryModeAdapter.ModeItemSelectListener,
        ProductCategoryAdapter.ProductCategoryItemSelectListener,
        DeliveryTypeAdapter.DeliveryTypeItemSelectListener {

    // This number controls the speed of smooth scroll
    private static final float MILLISECONDS_PER_INCH = 70f;
    private final static int DIMENSION = 2;
    private final static int HORIZONTAL = 0;
    private final static int VERTICAL = 1;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    //MyExpandableAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<ProductModel>> expandableListDetail;
    List<String> days;
    List<String> times;
    private Context context;
    private FragmentManager fragmentManager;
    private DeliveryDetails deliveryDetails;
    private FragmentCheckoutNewBinding binding;
    private View view;
    private Preferences preferences;
    private BottomSheetDialog paymentBottomSheetDialog;
    private RelativeLayout addNewCard;
    private ImageView ivBack;
    private Button btnConfirmDelivery;
    private RecyclerView cardRV;
    private ArrayList<CardModel> cardModels;
    private CardModel cardModel;
    private ArrayList<ProductModel> productModels;
    private ProductModel productModel;
    private ArrayList<DeliveryModeModel> deliveryModeModelArrayList;
    private DeliveryModeModel deliveryModeModel;
    private ArrayList<DeliveryTypeModel> deliveryTypeModelArrayList;
    private DeliveryTypeModel deliveryTypeModel;
    private DebitCreditCardAdapter debitCreditCardAdapter;
    private DeliveryModeAdapter deliveryModeAdapter;
    private ProductCategoryAdapter productCategoryAdapter;
    private TextView TvSchedule;

    //private Animation viewShow, viewHide;

    //private DeliveryTypeAdapter deliveryTypeAdapter;
    private String selectedDay, selectedDate, selectedTimeFrom, selectedTimeTo;
    private int selectedDayPosition = -1, selectedTimePosition = -1;
    private int selectedDeliveryMode;

    private boolean isSheetIsOpen;

    private ViewPager vehiclieListViewPager;

    private String paystackRef = null;
    private String paystackAuth = null;

    private final int REQUEST_PAYMENT = 405;

    private String selectedModeID = "1";

    @Override
    public void onResume() {
        super.onResume();

        //getCardList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout_new, container, false);

        view = binding.getRoot();

        context = getActivity();

        builder = new AlertDialog.Builder(context);

        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        preferences = new Preferences(getActivity());

        //viewShow = AnimationUtils.loadAnimation(getActivity(), R.anim.view_show);
        //viewHide = AnimationUtils.loadAnimation(getActivity(), R.anim.view_hide);

        Bundle bundle = getArguments();
        if (bundle != null) {
            deliveryDetails = bundle.getParcelable("deliveryDetails");

            binding.tvFromLoaction.setText(deliveryDetails.getFromAddress());
            binding.tvToLocation.setText(deliveryDetails.getToAddress());
        }

        //expandableListView = view.findViewById(R.id.expandableListView);


        if (deliveryDetails == null) {
            // Toast.makeText(context, "Enter required details.", Toast.LENGTH_SHORT).show();
            fragmentManager.popBackStack();
        }

        //binding.ivBack.setOnClickListener(this);
        binding.notesAdd.setOnClickListener(this);
        binding.relativeCardNumber.setOnClickListener(this);
        binding.linearSchedule.setOnClickListener(this);
        binding.llHead.setOnClickListener(this);
        binding.btnMainConfirmDelivery.setOnClickListener(this);
        binding.pcAdd.setOnClickListener(this);

        getProductCategoryList();

        getDeliveryModeList();

        binding.viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onModeSelect(position,deliveryModeModelArrayList.get(position));
                selectedModeID = deliveryModeModelArrayList.get(position).getModeID();
                Toast.makeText(getActivity(),selectedModeID,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return binding.getRoot();
    }

    AlertDialog.Builder builder;

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:
                fragmentManager.popBackStack();
                break;

            case R.id.pc_add:
                if (binding.pcAdd.getText().equals("+")) {
                    binding.rvProductCategory.setVisibility(View.VISIBLE);
                    //binding.rvProductCategory.startAnimation(viewShow);
                    binding.pcAdd.setText("-");
                } else {
                    //binding.rvProductCategory.startAnimation(viewHide);
                    binding.rvProductCategory.setVisibility(View.GONE);
                    binding.pcAdd.setText("+");
                }
                break;

            case R.id.notes_add:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.notesAdd.getText().equals("+")) {
                            binding.noteDiscription.setVisibility(View.VISIBLE);
                            //binding.noteDiscription.startAnimation(viewShow);
                            binding.notesAdd.setText("-");
                        } else {
                            //binding.noteDiscription.startAnimation(viewHide);
                            binding.noteDiscription.setVisibility(View.GONE);
                            binding.notesAdd.setText("+");
                        }
                    }
                }, 300);
                break;

            case R.id.relativeCardNumber:
                openCardListBottomSheet();
                break;

            case R.id.linearSchedule:
                openScheduleDialog();
                break;

            case R.id.ll_head:
                if (Objects.requireNonNull(getFragmentManager()).getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                    return;
                }
                break;

            case R.id.btnMainConfirmDelivery:

                if (productModel != null) {
                    //if (cardModel==null){
                    if (paystackAuth==null){

                        //Uncomment the below code to Set the message and title from the strings.xml file
                        //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

                        //Setting message manually and performing action on button click
                        /*builder.setMessage("Do you want to Add Card Detail ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //finish();
                                        openCardListBottomSheet();

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        setAllDataToModel();
                                        dialog.cancel();

                                    }
                                });
                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle("Alert");
                        alert.show();*/

                        initiatePaystack();

                    }else {
                        setAllDataToModel();
                    }

                } else {
                    ShowToast(getActivity(), "Please select product category.");
                }
                break;
        }
    }

    private void initiatePaystack()
    {
        Functions.ShowProgressLoader(getActivity(),false,false);
        JSONObject request = new JSONObject();
        try {
            int amount = (int)(Float.parseFloat(deliveryTypeModel.getPer_km_mile_charge())*100);
            request.put("user_id", preferences.getKeyUserId());
            request.put("amount",amount); //it get fraction by 100 in paystack
            //request.put("amount","30000");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            ApiRequest.CallApi(context, ApiUrl.URL_INITIATE_PAYSTACK_TRASACTION, request, new Callback() {
                @Override
                public void Responce(String resp) {
                    Functions.CancelProgressLoader();
                    if (resp != null) {

                        try {

                            JSONObject respobj = new JSONObject(resp);

                            if (respobj.getString("code").equals("200")) {
                                String paymentURL = respobj.getJSONObject("msg").getJSONObject("data").getString("authorization_url");
                                Intent intent = new Intent(getActivity(), PaymentWebViewA.class);
                                intent.putExtra("url",paymentURL);
                                startActivityForResult(intent,REQUEST_PAYMENT);
                            }
                            else if(respobj.getString("code").equals("201"))
                            {
                                Toast.makeText(getActivity(),respobj.getString("msg"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            /*ApiRequest.CallApi(context, ApiUrl.URL_INITIATE_PAYSTACK_TRASACTION, request, resp -> {

                if (resp != null) {

                    try {

                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            String paymentURL = respobj.getJSONObject("msg").getJSONObject("data").getString("authorization_url");
                            Intent intent = new Intent(getActivity(), PaymentWebViewA.class);
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, Request.Method.GET);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCardListBottomSheet() {

        paymentBottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
        paymentBottomSheetDialog.setContentView(R.layout.fragment_add_card_selection_bottom_sheet);

        addNewCard = paymentBottomSheetDialog.findViewById(R.id.relativeAddNewCard);
        ivBack = paymentBottomSheetDialog.findViewById(R.id.ivBack);
        //btnConfirmDelivery = paymentBottomSheetDialog.findViewById(R.id.btnConfirmDelivery);
        cardRV = paymentBottomSheetDialog.findViewById(R.id.rvCard);

        //Set Default card Selection
        if (cardModel == null) {
            /*CardModel temp = new CardModel();
            temp = cardModels.get(0);
            temp.setSelected(true);
            cardModels.set(0, temp);*/
        }

        debitCreditCardAdapter = new DebitCreditCardAdapter(cardModels, this);
        cardRV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        cardRV.setItemAnimator(new DefaultItemAnimator());
        cardRV.setAdapter(debitCreditCardAdapter);

        ivBack.setOnClickListener(view -> paymentBottomSheetDialog.dismiss());

        addNewCard.setOnClickListener(view -> {
            paymentBottomSheetDialog.dismiss();
            isSheetIsOpen = true;
            Intent addCardIntent = new Intent(getActivity(), AddNewCardActivity.class);
            startActivity(addCardIntent);
        });

        /*btnConfirmDelivery.setOnClickListener(view -> {

            paymentBottomSheetDialog.dismiss();

            FinalDeliveryF fragments = new FinalDeliveryF();
            FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            ft.add(R.id.fl_id, fragments, "FinalDeliveryF").addToBackStack("FinalDeliveryF").commit();
        });*/

        paymentBottomSheetDialog.show();
    }

    @SuppressLint("DefaultLocale")
    private void openScheduleDialog() {

        final int[] tempSelectedDayPosition = {-1};
        final int[] tempSelectedTimePosition = {-1};

        Dialog dialogFileOptions = new Dialog(getActivity());
        dialogFileOptions.setCancelable(true);
        dialogFileOptions.setContentView(R.layout.dialog_schedule);
        dialogFileOptions.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogFileOptions.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button notScheduleButton = dialogFileOptions.findViewById(R.id.btnNotSchedule);
        notScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDayPosition = -1;
                selectedTimePosition = -1;
                selectedDay = "";
                selectedDate = "";
                selectedTimeFrom = "";
                selectedTimeTo = "";

                binding.tvSchedule.setTextColor(getActivity().getResources().getColor(R.color.gray));

                if (dialogFileOptions.isShowing())
                    dialogFileOptions.dismiss();
            }
        });
        Button confirmScheduleButton = dialogFileOptions.findViewById(R.id.btnConfirmSchedule);
        ImageView btnDialogClose = dialogFileOptions.findViewById(R.id.btnCloseDialog);
        confirmScheduleButton.setOnClickListener(v -> {

            selectedDayPosition = tempSelectedDayPosition[0] > -1 ? tempSelectedDayPosition[0] : selectedDayPosition;
            selectedTimePosition = tempSelectedTimePosition[0] > -1 ? tempSelectedTimePosition[0] : selectedTimePosition;

            if (selectedDayPosition >= 0) {
                selectedDay = days.get(selectedDayPosition);
                selectedDate = getDate(selectedDayPosition);
            } else {
                selectedDay = days.get(0);
                selectedDate = getDate(0);
            }

            if (selectedTimePosition >= 0) {
                selectedTimeFrom = times.get(selectedTimePosition).split("-")[0].trim();
                selectedTimeTo = times.get(selectedTimePosition).split("-")[1].trim();
            } else {
                selectedTimeFrom = times.get(0).split("-")[0].trim();
                selectedTimeTo = times.get(0).split("-")[1].trim();
            }

            binding.tvSchedule.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));

            if (dialogFileOptions.isShowing())
                dialogFileOptions.dismiss();
        });

        btnDialogClose.setOnClickListener(view -> {
            if (dialogFileOptions.isShowing())
                dialogFileOptions.dismiss();
        });

        dialogFileOptions.setCancelable(false);

        days = new ArrayList<>();
        days.add("Today");
        days.add("Tomorrow");
        days.add(getDate(2, "EEE, dd MMM"));

        times = new ArrayList<>();
        for (int i = 0; i < 24; i++)
            times.add(String.format("%02d", i) + ":00 - " + String.format("%02d", (i + 1)) + ":00");

        WheelPicker<String> dayPicker = dialogFileOptions.findViewById(R.id.dayPicker);

        dayPicker.setDataList(days);
        if (selectedDayPosition > -1)
            dayPicker.setCurrentPosition(selectedDayPosition);

        dayPicker.setOnWheelChangeListener((item, position) -> tempSelectedDayPosition[0] = position);

        WheelPicker<String> timePicker = dialogFileOptions.findViewById(R.id.timePicker);
        timePicker.setDataList(times);

        timePicker.setSelected(true);

        if (selectedTimePosition > -1)
            timePicker.setCurrentPosition(selectedTimePosition);

        timePicker.setOnWheelChangeListener((item, position) -> tempSelectedTimePosition[0] = position);

        dialogFileOptions.show();
    }

    public void getProductCategoryList() {

        JSONObject request = new JSONObject();

        try {

            ApiRequest.CallApi(context, ApiUrl.getProductCategoryList, new JSONObject(), resp -> {

                if (resp != null) {

                    try {

                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {

                            JSONArray jsonArray = respobj.getJSONArray("msg");

                            if (jsonArray.length() > 0) {

                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<ProductModel>>() {
                                }.getType();
                                productModels = gson.fromJson(jsonArray.toString(), type);

                                productCategoryAdapter = new ProductCategoryAdapter(productModels, CheckoutNewF.this);
                                binding.rvProductCategory.setAdapter(productCategoryAdapter);
                                binding.rvProductCategory.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
                                binding.rvProductCategory.setItemAnimator(new DefaultItemAnimator());

                                expandableListDetail = new HashMap<>();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, Request.Method.GET);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDeliveryModeList() {

        JSONObject request = new JSONObject();

        try {

            ApiRequest.CallApi(context, ApiUrl.getModesList, new JSONObject(), resp -> {

                if (resp != null) {

                    try {

                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {

                            JSONArray jsonArray = respobj.getJSONArray("msg");

                            if (jsonArray.length() > 0) {

                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<DeliveryModeModel>>() {
                                }.getType();
                                deliveryModeModelArrayList = gson.fromJson(jsonArray.toString(), type);

                                deliveryModeModelArrayList.get(0).setSelected(true);

                                DeliveryModeModel tempMode = deliveryModeModelArrayList.get(0);
                                /*for (int i = 0; i < 10; i++) {
                                    DeliveryModeModel mode = new DeliveryModeModel();
                                    mode.setModeID("100" + (i + 1));
                                    mode.setModeName("100" + (i + 1));
                                    deliveryModeModelArrayList.add(mode);
                                }*/

                                //getDeliverTypeList(tempMode.getModeID());

                                deliveryModeAdapter = new DeliveryModeAdapter(deliveryModeModelArrayList, CheckoutNewF.this);
                                binding.rvDeliveryMode.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                                binding.rvDeliveryMode.setItemAnimator(new DefaultItemAnimator());
                                binding.rvDeliveryMode.setAdapter(deliveryModeAdapter);

                                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
                                for(int i=0;i<deliveryModeModelArrayList.size();i++)
                                {
                                    DeliveryModeModel deliveryModeModel = deliveryModeModelArrayList.get(i);
                                    VehicleListF vehicleListF = new VehicleListF(this);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("mode_id",deliveryModeModel.getModeID());
                                    bundle.putSerializable("delivery_detail_model",deliveryDetails);
                                    vehicleListF.setArguments(bundle);
                                    viewPagerAdapter.addFrag(vehicleListF,deliveryModeModel.getModeName());
                                }
                                binding.viewpager.setAdapter(viewPagerAdapter);

                                final LinearSnapHelper snapHelper = new LinearSnapHelper();
                                snapHelper.attachToRecyclerView(binding.rvDeliveryMode);

                                binding.rvDeliveryMode.setOnFlingListener(snapHelper);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, Request.Method.GET);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void getDeliverTypeList(String modeID) {

        JSONObject request = new JSONObject();

        try {

            request.put("modeID", modeID);

            ApiRequest.CallApi(context, ApiUrl.getDeliveryModeList, request, resp -> {

                if (resp != null) {

                    try {

                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {

                            JSONArray jsonArray = respobj.getJSONArray("msg");

                            if (jsonArray.length() > 0) {

                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<DeliveryTypeModel>>() {
                                }.getType();
                                deliveryTypeModelArrayList = gson.fromJson(jsonArray.toString(), type);

                                deliveryTypeAdapter = new DeliveryTypeAdapter(deliveryTypeModelArrayList, CheckoutNewF.this);
                                binding.rvDeliveryType.setAdapter(deliveryTypeAdapter);
                                binding.rvDeliveryType.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
                                runItemAnimation(binding.rvDeliveryType);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void runItemAnimation(final RecyclerView recyclerView) {

        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.delivery_type_item_animation);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public void getCardList() {

        JSONObject request = new JSONObject();

        try {

            request.put("user_id", preferences.getKeyUserId());

            ApiRequest.CallApi(getActivity(), ApiUrl.getUserCardDetails, request, resp -> {

                if (resp != null) {

                    try {

                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {

                            JSONArray jsonArray = respobj.getJSONArray("UserCardDetails");

                            if (jsonArray.length() > 0) {

                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<CardModel>>() {
                                }.getType();
                                cardModels = gson.fromJson(jsonArray.toString(), type);

                                if (cardModel != null) {

                                    for (int i = 0; i < cardModels.size(); i++) {

                                        CardModel temp = cardModels.get(i);

                                        if (cardModel.getCardId().equalsIgnoreCase(temp.getCardId())) {
                                            temp.setSelected(true);
                                        }
                                        cardModels.set(i, temp);
                                    }

                                    if (debitCreditCardAdapter != null)
                                        debitCreditCardAdapter.notifyDataSetChanged();

                                    if (isSheetIsOpen)
                                        openCardListBottomSheet();

                                } else {

                                    cardModel = cardModels.get(0);
                                    cardModel.setSelected(true);
                                    binding.tvCardNumber.setText(cardModel.getCardNumber());

                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCardSelect(int itemPosition, Object object) {

        for (int i = 0; i < cardModels.size(); i++) {
            CardModel cardModel = cardModels.get(i);
            if (i == itemPosition)
                this.cardModel = cardModel;
            cardModel.setSelected(i == itemPosition);
            cardModels.set(i, cardModel);
        }

        debitCreditCardAdapter.notifyDataSetChanged();

        if (cardModel != null) {
            binding.tvCardNumber.setText(cardModel.getCardNumber());
            if (paymentBottomSheetDialog.isShowing())
                paymentBottomSheetDialog.dismiss();
        }
    }

    @Override
    public void onModeSelect(int itemPosition, Object object) {

        LinearLayoutManager layoutManager = ((LinearLayoutManager) binding.rvDeliveryMode.getLayoutManager());
        /*int totalVisibleItems = layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition();
        int centeredItemPosition = totalVisibleItems / 2;
        binding.rvDeliveryMode.smoothScrollToPosition(itemPosition);
        binding.rvDeliveryMode.setScrollY(centeredItemPosition);*/

        scrollToCenter(layoutManager, binding.rvDeliveryMode, itemPosition);

        for (int i = 0; i < deliveryModeModelArrayList.size(); i++) {
            DeliveryModeModel deliveryModeModel = deliveryModeModelArrayList.get(i);
            if (i == itemPosition)
                this.deliveryModeModel = deliveryModeModel;
            deliveryModeModel.setSelected(i == itemPosition);
            deliveryModeModelArrayList.set(i, deliveryModeModel);
        }

        //getDeliverTypeList(this.deliveryModeModel.getModeID());

        deliveryModeAdapter.notifyDataSetChanged();
        binding.viewpager.setCurrentItem(itemPosition,true);
    }

    @Override
    public void onCategorySelect(int itemPosition, Object object) {

        for (int i = 0; i < productModels.size(); i++) {
            ProductModel productModel = productModels.get(i);
            if (i == itemPosition)
                this.productModel = productModel;
            productModel.setSelected(i == itemPosition);
            productModels.set(i, productModel);
        }

        //binding.rvProductCategory.startAnimation(viewHide);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rvProductCategory.setVisibility(View.GONE);
            }
        }, 300);
        binding.pcAdd.setText("+");

        productCategoryAdapter.notifyDataSetChanged();

    }

    @Override
    public void onTypeSelect(int itemPosition, Object object) {

       /* for (int i = 0; i < deliveryTypeModelArrayList.size(); i++) {
            DeliveryTypeModel deliveryTypeModel = deliveryTypeModelArrayList.get(i);
            if (i == itemPosition)
                this.deliveryTypeModel = deliveryTypeModel;
            deliveryTypeModel.setSelected(i == itemPosition);
            deliveryTypeModelArrayList.set(i, deliveryTypeModel);
        }

        deliveryTypeAdapter.notifyDataSetChanged();*/

        this.deliveryTypeModel = (DeliveryTypeModel)object;
        selectedModeID = this.deliveryTypeModel.getModeID();


        binding.btnMainConfirmDelivery.setVisibility(View.VISIBLE);
    }

    private void setAllDataToModel() {

        JSONObject request = new JSONObject();

        try {

            request.put("user_id", preferences.getKeyUserId());
            request.put("good_type_id", productModel.getId());
            request.put("delivery_type_id", deliveryTypeModel.getId());
            request.put("mode_id",selectedModeID);
            request.put("isFragile", binding.cbFragile.isChecked() ? 1 : 0);
            request.put("isSchedule", selectedDayPosition > -1 && selectedTimePosition > -1 ? 1 : 0);
            if (selectedDayPosition > -1 && selectedTimePosition > -1) {
                request.put("sheduledDate", selectedDate);
                request.put("sheduledTimeFrom", selectedTimeFrom);
                request.put("sheduledTimeTo", selectedTimeTo);
            }
            request.put("price", deliveryTypeModel.getPer_km_mile_charge());
            request.put("total", deliveryTypeModel.getPer_km_mile_charge());
            request.put("item_description", binding.noteDiscription.getText().toString());

            request.put("sender_location_lat", deliveryDetails.getFromLatitude());
            request.put("sender_location_long", deliveryDetails.getFromLongitude());
            request.put("sender_location_string", deliveryDetails.getFromAddress());
            request.put("sender_address_detail", deliveryDetails.getFromAddress());

            request.put("receiver_location_lat", deliveryDetails.getToLatitude());
            request.put("receiver_location_long", deliveryDetails.getToLongitude());
            request.put("receiver_location_string", deliveryDetails.getToAddress());
            request.put("receiver_address_detail", deliveryDetails.getToAddress());

            request.put("status", 0);
            request.put("paystack_auth",paystackAuth);
            request.put("paystack_ref",paystackRef);

            String requestString = new Gson().toJson(request);

            Log.d("Request String", requestString);

            Functions.ShowProgressLoader(getActivity(), false, false);

            ApiRequest.CallApi(getActivity(), ApiUrl.saveNewOrderDetails, request, new Callback() {
                @Override
                public void Responce(String resp) {

                    Functions.CancelProgressLoader();

                    if (resp != null) {

                        try {
                            JSONObject respobj = new JSONObject(resp);
                            if (respobj.getString("code").equals("200")) {
                                Toast.makeText(getContext(), "New order details added successfully.", Toast.LENGTH_SHORT).show();

                                /*FinalDeliveryF fragments = new FinalDeliveryF();
                                FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                                ft.add(R.id.fl_id, fragments, "FinalDeliveryF").addToBackStack("FinalDeliveryF").commit();*/

                                try {
                                    fragmentManager.popBackStack();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                startActivity(new Intent(getActivity(), FinalDeliveryA.class));
                                getActivity().finish();
                            } else {
                                Toast.makeText(context, "Something went wrong! Try after sometime", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getDate(int noOfDays, String... dateFormat) {

        String format = "yyyy-MM-dd";

        if (dateFormat != null && dateFormat.length > 0)
            format = dateFormat[0];

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, noOfDays);
        return sdf.format(cal.getTime());
    }

    public void scrollToCenter(LinearLayoutManager layoutManager, RecyclerView recyclerList, int clickPosition) {
        RecyclerView.SmoothScroller smoothScroller = createSnapScroller(recyclerList, layoutManager);

        if (smoothScroller != null) {
            smoothScroller.setTargetPosition(clickPosition);
            layoutManager.startSmoothScroll(smoothScroller);
        }
    }

    @Nullable
    private LinearSmoothScroller createSnapScroller(RecyclerView mRecyclerView, RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
                int[] snapDistances = calculateDistanceToFinalSnap(layoutManager, targetView);
                final int dx = snapDistances[HORIZONTAL];
                final int dy = snapDistances[VERTICAL];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };
    }

    private int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[DIMENSION];
        if (layoutManager.canScrollHorizontally()) {
            out[HORIZONTAL] = distanceToCenter(layoutManager, targetView,
                    OrientationHelper.createHorizontalHelper(layoutManager));
        }

        if (layoutManager.canScrollVertically()) {
            out[VERTICAL] = distanceToCenter(layoutManager, targetView,
                    OrientationHelper.createHorizontalHelper(layoutManager));
        }
        return out;
    }

    private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager,
                                 @NonNull View targetView, OrientationHelper helper) {
        final int childCenter = helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2);
        final int containerCenter;
        if (layoutManager.getClipToPadding()) {
            containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else {
            containerCenter = helper.getEnd() / 2;
        }
        return childCenter - containerCenter;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("TAG",""+position);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            Log.e("TAG","test");
            //return mFragmentList.size();
            return 2;
        }



        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PAYMENT && resultCode == getActivity().RESULT_OK)
        {
            Toast.makeText(getActivity(),"Payment token generated successfully!",Toast.LENGTH_LONG).show();
            paystackAuth = data.getStringExtra("auth");
            paystackRef = data.getStringExtra("ref");
            binding.btnMainConfirmDelivery.performClick();
        }
    }
}
