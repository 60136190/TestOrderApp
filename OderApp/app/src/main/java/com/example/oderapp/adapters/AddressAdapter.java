package com.example.oderapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.metrics.Event;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderapp.R;
import com.example.oderapp.SendData;
import com.example.oderapp.activities.AddressActivity;
import com.example.oderapp.activities.ApiClient;
import com.example.oderapp.activities.DetailActivity;
import com.example.oderapp.activities.PaymentActivity;
import com.example.oderapp.eventbus.EventBack;
import com.example.oderapp.model.Address;
import com.example.oderapp.model.response.ResponseBodyAddress;
import com.example.oderapp.model.response.ResponseDTO;
import com.example.oderapp.utils.Contants;
import com.example.oderapp.utils.StoreUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ItemViewHolder> {

    List<Address> mAddressList;
    Activity mContext;
    SendData sendData;

    // filter

//    public AddressAdapter(List<Address> mAddressList, Activity mContext, SendData sendData) {
//        this.mAddressList = mAddressList;
//        this.mContext = mContext;
//        this.sendData = sendData;
//    }

    public AddressAdapter(Activity activity, List<Address> mAddressList, SendData sendData) {
        this.mContext = activity;
        this.mAddressList = mAddressList;
        this.sendData = sendData;
    }

    @Override
    public AddressAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressAdapter.ItemViewHolder holder, int position) {

        Address currentItem = mAddressList.get(position);
        int id = currentItem.getId();
        String diachi = currentItem.getDiachi();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Log.i("TAG", "onClick: ");
            }
        });

        holder.tvDiaChi.setText(diachi);
        holder.imgDeleteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                Call<ResponseBodyAddress> responseBodyAddressCall = ApiClient.getService().deleteAddress(id,
                        "Bearer " + StoreUtil.get(v.getContext(), Contants.accessToken));
                responseBodyAddressCall.enqueue(new Callback<ResponseBodyAddress>() {
                    @Override
                    public void onResponse(Call<ResponseBodyAddress> call, Response<ResponseBodyAddress> response) {
                        Toast.makeText(v.getContext(), "Added in cart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBodyAddress> call, Throwable t) {

                    }
                });
            }
        });

        holder.imgEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_address);

                Window window = dialog.getWindow();
                if (window == null) {
                    return;
                }

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams windowAtribute = window.getAttributes();
                window.setAttributes(windowAtribute);


                EditText edtEditAddress = dialog.findViewById(R.id.edt_edit_address_dialog);
                Button btnCancel = dialog.findViewById(R.id.btn_cancel);
                Button btnSave = dialog.findViewById(R.id.btn_save_address);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                // show dialog
                dialog.show();

                // set address show on dialog
                edtEditAddress.setText(diachi);

                // save address after update on dialog
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strAddress = edtEditAddress.getText().toString();
                        int id = currentItem.getId();
                        Address address = new Address(strAddress);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put(Contants.accessToken, "Bearer " + StoreUtil.get(v.getContext(), Contants.accessToken));
                        hashMap.put(Contants.contentLength, "<calculated when request is sent>");
                        Call<ResponseDTO> loginResponeCall = ApiClient.getService().updateAddress(id, address, hashMap);
                        loginResponeCall.enqueue(new Callback<ResponseDTO>() {
                            @Override
                            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                                Toast.makeText(v.getContext(), "Update information is successful", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<ResponseDTO> call, Throwable t) {

                            }
                        });
                    }
                });
                //-----------------


            }
        });

        // get address follow id
        holder.lnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "onClick: ");
                EventBus.getDefault().post(new EventBack(currentItem));
//                mContext.onBackPressed();
                sendData.sendData(currentItem);
            }
        });
    }


    @Override
    public int getItemCount() {

        return mAddressList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDiaChi;
        private ImageView imgDeleteAddress;
        private ImageView imgEditAddress;
        private LinearLayout lnAddress;


        public ItemViewHolder(View itemView) {
            super(itemView);
            tvDiaChi = itemView.findViewById(R.id.tv_diaChi);
            imgDeleteAddress = itemView.findViewById(R.id.img_delete_address);
            imgEditAddress = itemView.findViewById(R.id.img_edit_address);
            lnAddress = itemView.findViewById(R.id.ln_address);

        }
    }


}
