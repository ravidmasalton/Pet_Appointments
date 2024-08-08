package com.example.vetappointment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetappointment.Adapter.AppointmentAdapter;
import com.example.vetappointment.Interfaces.CancelAppointmentCallback;
import com.example.vetappointment.Listeners.ListenerGetAllAppointmentFromDB;
import com.example.vetappointment.Models.Appointment;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.R;
import com.example.vetappointment.databinding.FragmentAddNewAppointmentBinding;
import com.example.vetappointment.databinding.FragmentAllAppointmentsBinding;
import com.example.vetappointment.databinding.FragmentAllMessagesToVetBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class AllAppointmentsUserFragment extends Fragment {

    private FragmentAllAppointmentsBinding binding;

    private RecyclerView recycler_view_appointments;

    private MaterialTextView edit_text_date_filter;
    private FirebaseAuth auth;
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();
    private ArrayList<Appointment> allAppointments = new ArrayList<>();
    private ArrayList<Appointment> userAppointments = new ArrayList<>();
    private AppointmentAdapter appointmentAdapter;
    private MaterialButtonToggleGroup toggleButtonGroup;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAllAppointmentsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        findViews();
        getAllAppointmentsFromDatabase();
        filterAppointmentsButton();





        return root;
    }


    private void getAllAppointmentsFromDatabase() {
        fireBaseManager.getAllAppointments(new ListenerGetAllAppointmentFromDB() {
            @Override
            public void onAppointmentFromDBLoadSuccess(ArrayList<Appointment> appointments) {
                allAppointments.clear();
                allAppointments.addAll(appointments);
                getUserAppointment();





            }

            @Override
            public void onAppointmentFromDBLoadFailed(String message) {
                Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show();

            }
        });


    }
    private void getUserAppointment() {

        String currentUserId = auth.getCurrentUser().getUid();
        for (Appointment appointment : allAppointments) {
            if (appointment.getIdCustomer().equals(currentUserId)) {
                userAppointments.add(appointment);
            }
        }
        setupAdapterAndRecyclerView();




    }

    private void setupAdapterAndRecyclerView() {
        appointmentAdapter = new AppointmentAdapter(getContext(), userAppointments,false);
        appointmentAdapter.setAppointmentCallBack(new CancelAppointmentCallback() {
            @Override
            public void cancelAppointment(Appointment appointment, int position) {
                fireBaseManager.removeAppointment(appointment);
                fireBaseManager.removeAppointmentFromUser(appointment.getIdCustomer(),appointment.getAppointmentId());
                getAllAppointmentsFromDatabase();



            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_view_appointments.setLayoutManager(linearLayoutManager);
        recycler_view_appointments.setAdapter(appointmentAdapter);
        setupToggleButtons();






    }




    private void filterAppointmentsButton() {
        edit_text_date_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAppointmentsByDate();
            }
        });
    }

    private void filterAppointmentsByDate() {
        ArrayList<Appointment>currentAllAppointments =appointmentAdapter.getAllappointments();
        Collections.sort(currentAllAppointments);
        appointmentAdapter.updateAppointments(currentAllAppointments,appointmentAdapter.getIsPastAppointments());

    }



    private void filterAndDisplayAppointments(int checkedId) {
        ArrayList<Appointment> filteredAppointments = new ArrayList<>();
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        if (checkedId == R.id.future_appointments_button) {
            for (Appointment appointment : userAppointments) {
                try {
                    Date appointmentDate = sdf.parse(appointment.getDate() + " " + appointment.getTime());
                    if (appointmentDate != null && appointmentDate.after(currentDate)) {
                        filteredAppointments.add(appointment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (checkedId == R.id.history_appointments_button) {
            for (Appointment appointment : userAppointments) {
                try {
                    Date appointmentDate = sdf.parse(appointment.getDate() + " " + appointment.getTime());
                    if (appointmentDate != null && appointmentDate.before(currentDate)) {
                        filteredAppointments.add(appointment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        appointmentAdapter.updateAppointments(filteredAppointments, checkedId == R.id.history_appointments_button);
    }

    private void setupToggleButtons() {
        toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                filterAndDisplayAppointments(checkedId);
            }
        });
    }





    private void findViews() {
        recycler_view_appointments = binding.recyclerViewAppointments;
        edit_text_date_filter = binding.dateFilterText;
        toggleButtonGroup = binding.toggleButton;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}