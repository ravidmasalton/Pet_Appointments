package com.example.vetappointment.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetappointment.Adapter.OnlineAppointmentAdapter;
import com.example.vetappointment.Interfaces.ResponseToOnlineAppointmentCallBack;
import com.example.vetappointment.Listeners.ListenerGetAllOnlineAppointmentFromDB;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.Models.OnlineAppointment;
import com.example.vetappointment.R;
import com.example.vetappointment.databinding.FragmentAllMessagesToVetBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

public class AllMessagesToVetFragment extends Fragment {
    private FragmentAllMessagesToVetBinding binding;

    private RecyclerView main_LST_onlineAppointments;
    private MaterialTextView edit_date_filter;
    private FireBaseManager firebaseManager = FireBaseManager.getInstance();
    private FirebaseAuth auth;
    private ArrayList<OnlineAppointment> onlineAppointments = new ArrayList<>();
    private MaterialButtonToggleGroup toggleButtonGroup;
    private OnlineAppointmentAdapter onlineAppointmentAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAllMessagesToVetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        findViews();
        getAllOnlineAppointmentsFromDatabase();
        filterAppointmentsButton();
        return root;
    }

    private void getAllOnlineAppointmentsFromDatabase() {
        String userId = auth.getCurrentUser().getUid();
        firebaseManager.getAllOnlineAppointmentsForUser(userId, new ListenerGetAllOnlineAppointmentFromDB() {
            @Override
            public void onOnlineAppointmentFromDBLoadSuccess(ArrayList<OnlineAppointment> appointments) {
                getAllOnlineAppointments(appointments);
                setupAdapterAndRecyclerView();
                filterAndDisplayAppointments(R.id.Pending_appointments_button); // Show only pending messages after loading data
            }

            @Override
            public void onOnlineAppointmentFromDBLoadFailed(String message) {
                Log.e("ravid", "Failed to load online appointments: " + message);
            }
        });
    }

    public void getAllOnlineAppointments(ArrayList<OnlineAppointment> appointments) {
        onlineAppointments.clear();
        onlineAppointments.addAll(appointments);
    }

    private void filterAppointmentsButton() {
        edit_date_filter.setOnClickListener(v -> filterAppointmentsByDate());
    }

    private void filterAppointmentsByDate() {
        if (onlineAppointmentAdapter.getItemCount() == 0)
            return;
        ArrayList<OnlineAppointment> currentAllAppointments = new ArrayList<>();
        currentAllAppointments.addAll(onlineAppointmentAdapter.getOnlineAppointments());
        Collections.sort(currentAllAppointments);
        onlineAppointmentAdapter.updateOnlineAppointments(currentAllAppointments);
    }

    private void filterAndDisplayAppointments(int checkedId) {
        ArrayList<OnlineAppointment> filteredOnlineAppointments = new ArrayList<>();
        if (checkedId == R.id.Pending_appointments_button) {
            for (OnlineAppointment appointment : onlineAppointments) {
                if (appointment != null && appointment.getActive()) {
                    filteredOnlineAppointments.add(appointment);
                }
            }
        } else if (checkedId == R.id.Completed_appointments_button) {
            for (OnlineAppointment appointment : onlineAppointments) {
                if (appointment != null && !appointment.getActive()) {
                    filteredOnlineAppointments.add(appointment);
                }
            }
        }

        onlineAppointmentAdapter.updateOnlineAppointments(filteredOnlineAppointments);
    }

    private void setupToggleButtons() {
        toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                filterAndDisplayAppointments(checkedId);
            }
        });

        isVetLoggedIn();

    }

    private void setupAdapterAndRecyclerView() {
        onlineAppointmentAdapter = new OnlineAppointmentAdapter(getContext(), onlineAppointments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        main_LST_onlineAppointments.setLayoutManager(linearLayoutManager);
        main_LST_onlineAppointments.setAdapter(onlineAppointmentAdapter);
        setupToggleButtons();
    }

    private void findViews() {
        main_LST_onlineAppointments = binding.recyclerViewAppointments;
        toggleButtonGroup = binding.toggleButtonMessages;
        edit_date_filter = binding.dateFilterText;
    }

    private void isVetLoggedIn() {
        firebaseManager.isVeterinarian(auth.getCurrentUser().getUid(), new FireBaseManager.CallBack<Boolean>() {
            @Override
            public void res(Boolean res) {
                if (res) {
                    getAllOnlineAppointments();

                    onlineAppointmentAdapter.setResponseCallBack(new ResponseToOnlineAppointmentCallBack() {
                        @Override
                        public void onResponseToOnlineAppointmentCallBack(OnlineAppointment onlineAppointment, int position) {
                            showResponseDialog(onlineAppointment, position);
                        }
                    });
                }
            }
        });
    }

    private void getAllOnlineAppointments() {
        firebaseManager.getAllOnlineAppointments(new ListenerGetAllOnlineAppointmentFromDB() {
            @Override
            public void onOnlineAppointmentFromDBLoadSuccess(ArrayList<OnlineAppointment> appointments) {
                onlineAppointments.clear();
                onlineAppointments.addAll(appointments);
                onlineAppointmentAdapter.updateOnlineAppointments(onlineAppointments);
                onlineAppointmentAdapter.setVet(true);
                filterAndDisplayAppointments(R.id.Pending_appointments_button); // Ensure the filter is applied
            }

            @Override
            public void onOnlineAppointmentFromDBLoadFailed(String message) {
                Log.e("ravid", "Failed to load online appointments: " + message);
            }
        });
    }

    private void showResponseDialog(OnlineAppointment onlineAppointment, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Respond to Appointment");

        // Set up the input
        final EditText input = new EditText(getContext());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String responseText = input.getText().toString();
            firebaseManager.saveResponseToOnlineAppointment(onlineAppointment.getOnlineAppointmentId(), responseText, success -> {
                if (success) {
                    Toast.makeText(getContext(), "Response saved successfully", Toast.LENGTH_SHORT).show();
                    // Refresh the list to show only pending appointments
                    onlineAppointments.get(position).setActive(false);
                    filterAndDisplayAppointments(R.id.Pending_appointments_button);
                } else {
                    Toast.makeText(getContext(), "Failed to save response", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
