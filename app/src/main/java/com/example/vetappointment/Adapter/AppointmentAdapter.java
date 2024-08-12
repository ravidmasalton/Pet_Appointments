package com.example.vetappointment.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetappointment.Interfaces.CancelAppointmentCallback;
import com.example.vetappointment.Models.Appointment;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.Models.User;
import com.example.vetappointment.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;


public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
   private Context context;
    private ArrayList<Appointment> Appointments;
    private boolean isPastAppointments;
    private CancelAppointmentCallback cancelAppointmentCallback;
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();

    public AppointmentAdapter( Context context,ArrayList<Appointment> appointments, boolean isPastAppointments) {
        this.context=context;
        this.Appointments = new ArrayList<>();
    }




    public AppointmentAdapter setAppointmentCallBack(CancelAppointmentCallback cancelAppointmentCallback) {
        this.cancelAppointmentCallback = cancelAppointmentCallback;
        return this;
    }
    public ArrayList<Appointment> getAllappointments() {
        return Appointments;
    }

    public Boolean getIsPastAppointments() {
        return isPastAppointments;
    }





    @NonNull
    @Override
    public AppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = getItem(position);
        fireBaseManager.getUser(appointment.getIdCustomer(), new FireBaseManager.CallBack<User>() {
            @Override
            public void res(User user) {
                if (user == null) {
                    return;
                }
                holder.usernameTextView.setText(user.getName());
                holder.appointmentDateTextView.setText(appointment.getDate());
                holder.appointmentTimeTextView.setText(appointment.getTime());
                holder.serviceTypeTextView.setText(appointment.getService());
                holder.petTypeTextView.setText(appointment.getPetType());
                holder.cancelButton.setVisibility(isPastAppointments ? View.GONE : View.VISIBLE);
            }
        });




    }

    @Override
    public int getItemCount() {
         return  this.Appointments.size();
    }

    public  Appointment getItem(int position) {
            return Appointments.get(position);


    }

    public void updateAppointments(ArrayList<Appointment> allAppointments, boolean isPastAppointments) {
        this.Appointments.clear();
        this.Appointments.addAll(allAppointments);
        this.isPastAppointments = isPastAppointments;
        notifyDataSetChanged();
    }




    public class AppointmentViewHolder extends RecyclerView.ViewHolder{
        private MaterialTextView usernameTextView;
        private MaterialTextView appointmentDateTextView;
        private MaterialTextView appointmentTimeTextView;
        private MaterialTextView serviceTypeTextView;
        private MaterialTextView petTypeTextView;
        private ExtendedFloatingActionButton cancelButton;




        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView= itemView.findViewById(R.id.usernameTextView);
            appointmentDateTextView= itemView.findViewById(R.id.appointmentDateTextView);
            appointmentTimeTextView= itemView.findViewById(R.id.appointmentTimeTextView);
            serviceTypeTextView= itemView.findViewById(R.id.serviceTypeTextView);
            petTypeTextView= itemView.findViewById(R.id.petTypeTextView);
            cancelButton= itemView.findViewById(R.id.cancelButton);

            cancelButton.setOnClickListener(v -> {
                if (cancelAppointmentCallback != null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Cancel Appointment")
                            .setMessage("Are you sure you want to cancel this appointment?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                if (cancelAppointmentCallback != null) {
                                    cancelAppointmentCallback.cancelAppointment(getItem(getAdapterPosition()), getAdapterPosition());

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });


        }
    }
}
