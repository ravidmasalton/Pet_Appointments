package com.example.vetappointment.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vetappointment.Interfaces.ResponseToOnlineAppointmentCallBack;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.Models.OnlineAppointment;
import com.example.vetappointment.Models.User;
import com.example.vetappointment.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class OnlineAppointmentAdapter extends RecyclerView.Adapter<OnlineAppointmentAdapter.OnlineAppointmentViewHolder> {

    private ArrayList<OnlineAppointment> onlineAppointments;
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();
    private ResponseToOnlineAppointmentCallBack responseCallBack;
    private Context context;
    private boolean isVet=false; // Flag to check if the user is a vet

    public OnlineAppointmentAdapter(Context context, ArrayList<OnlineAppointment> onlineAppointments) {
        this.onlineAppointments = onlineAppointments;
        this.context = context;// Set whether the user is a vet
    }
    public void setVet(boolean isVet) {
        this.isVet = isVet;
    }

    public void updateOnlineAppointments(ArrayList<OnlineAppointment> onlineAppointments) {
        this.onlineAppointments = onlineAppointments;
        notifyDataSetChanged();
    }

    public OnlineAppointmentAdapter setResponseCallBack(ResponseToOnlineAppointmentCallBack responseCallBack) {
        this.responseCallBack = responseCallBack;
        return this;
    }

    public ArrayList<OnlineAppointment> getOnlineAppointments() {
        return onlineAppointments;
    }

    @NonNull
    @Override
    public OnlineAppointmentAdapter.OnlineAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_online_appointment, parent, false);
        return new OnlineAppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineAppointmentAdapter.OnlineAppointmentViewHolder holder, int position) {
        OnlineAppointment onlineAppointment = getItem(position);
        fireBaseManager.getUser(onlineAppointment.getUserId(), new FireBaseManager.CallBack<User>() {
            @Override
            public void res(User user) {
                if (user == null) {
                    return;
                }
                holder.onlineAppointment_username.setText(user.getName());
                holder.onlineAppointment_message.setText("The message is: " + onlineAppointment.getMessage());
                holder.onlineAppointment_LBL_date.setText(onlineAppointment.getDate() + " , " + onlineAppointment.getTime());

                if (onlineAppointment.getImageUrl().isEmpty()) {
                    holder.onlineAppointment_image.setImageResource(R.drawable.unavailable_photo);
                    holder.onlineAppointment_image.setOnClickListener(null);
                }

                else {
                    Glide.with(context)
                            .load(onlineAppointment.getImageUrl())
                            .centerCrop()
                            .into(holder.onlineAppointment_image);
                    holder.onlineAppointment_image.setOnClickListener(v -> {
                        showImageZoomDialog(onlineAppointment.getImageUrl());
                    });
                }

                holder.maximizeImageView.setOnClickListener(v -> {
                    showExpandedDialog(onlineAppointment);
                });

                holder.onlineAppointment_response.setVisibility(
                        onlineAppointment.getResponse().isEmpty() ? View.GONE : View.VISIBLE);
                holder.onlineAppointment_response.setText("Response: " + onlineAppointment.getResponse());


            }
        });
    // Only allow the vet to respond
            if (isVet) {
                holder.Online_Appointment_CARD_data.setOnClickListener(v -> {
                    if (responseCallBack != null && onlineAppointment.getActive()) {
                        showResponseDialog(onlineAppointment,position);

                    }
                });
            }
    }

    private void showImageZoomDialog(String imageUrl) {
        // Inflate the custom dialog layout with the image view
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image_zoom, null);
        ImageView zoomableImageView = dialogView.findViewById(R.id.zoomableImageView);
        FloatingActionButton closeButton = dialogView.findViewById(R.id.buttonCloseImage);

        // Load the image into the zoomableImageView using Glide
        if (imageUrl.isEmpty()) {
            zoomableImageView.setImageResource(R.drawable.unavailable_photo);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .into(zoomableImageView);
        }

        // Create and show the dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        dialog.show();

        // Close button listener
        closeButton.setOnClickListener(v -> dialog.dismiss());
    }





    private void showResponseDialog(OnlineAppointment onlineAppointment,int position) {

            if (responseCallBack != null) {
                responseCallBack.onResponseToOnlineAppointmentCallBack(onlineAppointment,position);
            }


    }

    @Override
    public int getItemCount() {
        return onlineAppointments == null ? 0 : onlineAppointments.size();
    }

    public OnlineAppointment getItem(int position) {
        return onlineAppointments.get(position);
    }

    private void showExpandedDialog(OnlineAppointment onlineAppointment) {
        // Inflate the custom dialog layout with a ScrollView
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_expanded_online_appointment, null);
        MaterialTextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        MaterialTextView responseTextView = dialogView.findViewById(R.id.responseTextView);
        MaterialButton closeButton = dialogView.findViewById(R.id.buttonClose);

        // Populate the dialog with data
        messageTextView.setText("The message is: " + onlineAppointment.getMessage());
        responseTextView.setText("Response: " + onlineAppointment.getResponse());


        // Create the dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        // Show the dialog first to access the window parameters
        dialog.show();



        // Close button listener
        closeButton.setOnClickListener(v -> dialog.dismiss());
    }


    public class OnlineAppointmentViewHolder extends RecyclerView.ViewHolder {

        private final CardView Online_Appointment_CARD_data;
        private final MaterialTextView onlineAppointment_username;
        private final MaterialTextView onlineAppointment_LBL_date;
        private final MaterialTextView onlineAppointment_message;
        private final ShapeableImageView onlineAppointment_image;
        private final MaterialTextView onlineAppointment_response;
        private final ShapeableImageView maximizeImageView;

        public OnlineAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            Online_Appointment_CARD_data = itemView.findViewById(R.id.Online_Appointment_CARD_data);
            onlineAppointment_username = itemView.findViewById(R.id.usernameTextView);
            onlineAppointment_LBL_date = itemView.findViewById(R.id.appointmentTimeAndDateTextView);
            onlineAppointment_message = itemView.findViewById(R.id.messageTextView);
            onlineAppointment_response = itemView.findViewById(R.id.responseTextView);
            onlineAppointment_image = itemView.findViewById(R.id.IMG_poster);
            maximizeImageView = itemView.findViewById(R.id.maximizeImageView);





        }

    }
}
