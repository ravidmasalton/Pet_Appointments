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
import com.example.vetappointment.databinding.FragmentAllAppointmentsBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
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

    private FirebaseAuth auth;
    private MaterialTextView edit_text_filter_appointments;
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
        filterAppointmentsButton();
        getAllAppointmentsFromDatabase();

        return root;
    }

    private void getAllAppointmentsFromDatabase() {
        fireBaseManager.getAllAppointments(new ListenerGetAllAppointmentFromDB() {
            @Override
            public void onAppointmentFromDBLoadSuccess(ArrayList<Appointment> appointments) {
                allAppointments = appointments;
                String currentUserId = auth.getCurrentUser().getUid();
                for (Appointment appointment : allAppointments) {
                    if (appointment.getIdCustomer().equals(currentUserId)) {
                        userAppointments.add(appointment);
                    }
                }

                setupAdapterAndRecyclerView();
                // Automatically show future appointments when the page opens
                setupToggleButtons();
                toggleButtonGroup.check(R.id.future_appointments_button);

            }

            @Override
            public void onAppointmentFromDBLoadFailed(String message) {
                Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupAdapterAndRecyclerView() {
        appointmentAdapter = new AppointmentAdapter(getContext(), userAppointments, false);
        appointmentAdapter.setAppointmentCallBack(new CancelAppointmentCallback() {
            @Override
            public void cancelAppointment(Appointment appointment, int position) {
                // Remove appointment from database
                fireBaseManager.removeAppointment(appointment);
                fireBaseManager.removeAppointmentFromUser(appointment.getIdCustomer(), appointment.getAppointmentId());
                // Remove appointment from the local list
                userAppointments.remove(appointment);
                // Notify the adapter about the item removed
                appointmentAdapter.notifyItemRemoved(position);
                toggleButtonGroup.check(R.id.future_appointments_button);
                filterAndDisplayAppointments(toggleButtonGroup.getCheckedButtonId());
            }
        });

        recycler_view_appointments.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view_appointments.setAdapter(appointmentAdapter);
    }

    private void filterAndDisplayAppointments(int checkedId) {
        ArrayList<Appointment> filteredAppointments = new ArrayList<>();
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        for (Appointment appointment : userAppointments) {
            try {
                Date appointmentDate = sdf.parse(appointment.getDate() + " " + appointment.getTime());
                if (checkedId == R.id.future_appointments_button && appointmentDate != null && appointmentDate.after(currentDate)) {
                    filteredAppointments.add(appointment);
                } else if (checkedId == R.id.history_appointments_button && appointmentDate != null && appointmentDate.before(currentDate)) {
                    filteredAppointments.add(appointment);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        toggleButtonGroup = binding.toggleButton;
        edit_text_filter_appointments = binding.dateFilterText;
    }
    private void filterAppointmentsButton() {
        edit_text_filter_appointments.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
