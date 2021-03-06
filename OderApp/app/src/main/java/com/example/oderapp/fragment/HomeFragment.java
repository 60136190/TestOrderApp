package com.example.oderapp.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.oderapp.R;
import com.example.oderapp.activities.ApiClient;
import com.example.oderapp.adapters.HotThisMonthAdapter;
import com.example.oderapp.adapters.ItemProductAdappter;
import com.example.oderapp.fragmentinfo.optionaccount.UpdateInformationActivity;
import com.example.oderapp.model.Hot;
import com.example.oderapp.model.InformationUser;
import com.example.oderapp.model.ItemFood;
import com.example.oderapp.model.response.ResponseInformationUser;
import com.example.oderapp.utils.Contants;
import com.example.oderapp.utils.StoreUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment {
    ViewFlipper viewFlipper;
    private RoundedImageView roundedImageView;
    private TextView tvHiName;
    private ImageView imgUser;

    // search bar
    private EditText searchView;
    private CharSequence search = "";

    //get all product
    private RecyclerView mRecyclerView;
    private ItemProductAdappter mitemPizzaAdappter;
    private ArrayList<ItemFood> mitemPizzasList;
    private RequestQueue mRequestQueue;

    // Hot this month
    private RecyclerView rcvHotThisMonth;
    private List<Hot> hotThisMonths;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        roundedImageView = view.findViewById(R.id.pizza);
        tvHiName = view.findViewById(R.id.tv_hi_name);
        searchView = view.findViewById(R.id.search);
        imgUser = view.findViewById(R.id.img_user);

        // set border image
        imgUser.setClipToOutline(true);

        // ----------------------- get Url show on home fragment--------------------
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Contants.accessToken, "Bearer " + StoreUtil.get(getContext(), Contants.accessToken));

        Call<ResponseInformationUser> loginResponeCall = ApiClient.getService().getProfile(hashMap);
        loginResponeCall.enqueue(new Callback<ResponseInformationUser>() {
            @Override
            public void onResponse(Call<ResponseInformationUser> call, retrofit2.Response<ResponseInformationUser> response) {

                    InformationUser informationUser = response.body().getData().get(0);
                if (informationUser.getUrl().isEmpty()){
                    tvHiName.setText(informationUser.getHoten());

                }else{
                    String anh = informationUser.getUrl();
                    Picasso.with(getContext())
                            .load(anh).fit().centerInside().into(imgUser);
                    tvHiName.setText(informationUser.getHoten());
                }
            }


            @Override
            public void onFailure(Call<ResponseInformationUser> call, Throwable t) {

            }
        });
        // ------------------------------------

        // filter
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        int images[] = {R.drawable.promotiona, R.drawable.promotionb, R.drawable.promotionc,
                R.drawable.promotiond, R.drawable.promotione, R.drawable.promotionf, R.drawable
                .promotiong};

        viewFlipper = view.findViewById(R.id.viewflipper_hot_this_month);

        for (int image : images) {
            flipperImages(image);
        }

        // recyclerview hot this month
        rcvHotThisMonth = view.findViewById(R.id.rcv_hot_this_month);
        HotThisMonthAdapter hotThisMonthAdapter = new HotThisMonthAdapter(getContext(), hotThisMonths);
        rcvHotThisMonth.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvHotThisMonth.setAdapter(hotThisMonthAdapter);

        // recyclerview all products
        mRecyclerView = view.findViewById(R.id.rcv_menu);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }

    //--------------------------------
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hotThisMonths = new ArrayList<>();
        hotThisMonths.add(new Hot(R.drawable.hota, "H???t h???n 31-12-2021", "B???a ??n ?????ng gi?? 39k", "10:00 - 23:55", "rat la ngon"));
        hotThisMonths.add(new Hot(R.drawable.hotb, "H???t h???n 26-12-2021", "Combo no n?? gi?? ch??? 99k", "10:00 - 23:55", "M???t chi???c pizza Double chesse k???t v???i v???i x??c x??ch ?? Peperoni, m???t chai pepsi 1,5L"));
        hotThisMonths.add(new Hot(R.drawable.hotc, "H???t h???n 31-12-2021", "Combo pizza Gi??ng sinh", "10:00 - 23:55", "??n ???? ?????i, vui say s??a"));
        hotThisMonths.add(new Hot(R.drawable.hota, "H???t h???n 31-12-2021", "Bua an dong gia 39k", "10:00 - 23:55", "rat la ngon"));
        hotThisMonths.add(new Hot(R.drawable.hota, "H???t h???n 31-12-2021", "Bua an dong gia 39k", "10:00 - 23:55", "rat la ngon"));

        // parse json get all products
        mitemPizzasList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getContext());
        parseJSON();
    }

    // function Slider promotion
    public void flipperImages(int image) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(image);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(3500);
        viewFlipper.setAutoStart(true);
        viewFlipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
    }

    // function parse json to get all product from api
    private void parseJSON() {
        String url = "http://192.168.1.14:5000/product";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dt = jsonArray.getJSONObject(i);
                                int productId = dt.getInt("id");
                                String productName = dt.getString("tensp");
                                String productImage = dt.getString("url");
                                int productPrice = dt.getInt("gia");
                                String productDetail = dt.getString("chitiet");
                                String productSize = dt.getString("size");
                                mitemPizzasList.add(new ItemFood(productId, productName, productPrice, productImage, productDetail, productSize));
                            }
                            mitemPizzaAdappter = new ItemProductAdappter(getActivity(), mitemPizzasList);
                            mRecyclerView.setAdapter(mitemPizzaAdappter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        mRequestQueue.add(request);
    }

    // filter products
    private void filter(String text) {
        ArrayList<ItemFood> filteredList = new ArrayList<>();
        for (ItemFood item : mitemPizzasList) {
            if (item.getTensp().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        mitemPizzaAdappter.filterList(filteredList);
    }


}