package com.example.vetappointment.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetappointment.Adapter.AppointmentAdapter;
import com.example.vetappointment.Interfaces.CancelAppointmentCallback;
import com.example.vetappointment.Listeners.ListenerGetAllAppointmentFromDB;
import com.example.vetappointment.Listeners.ListenerGetAllOnlineAppointmentFromDB;
import com.example.vetappointment.Models.Appointment;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.Models.OnlineAppointment;
import com.example.vetappointment.Models.User;
import com.example.vetappointment.R;
import com.example.vetappointment.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recycler_view_appointments;
    private MaterialTextView user_name;
    private MaterialTextView numOfMessagesPending;
    private MaterialButton btn_view_all_appointments;
    private MaterialButton btn_view_all_messages;
    private AppointmentAdapter appointmentAdapter;
    private ArrayList<Appointment> allAppointments = new ArrayList<>();
    private ArrayList<Appointment> userAppointments = new ArrayList<>();
    private FirebaseAuth auth;
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();
    private MaterialTextView titleYourNextAppointment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        findViews();
        getUserInfo();
        initView();
        setupAdapterAndRecyclerView();
        getAllAppointmentsFromDatabase();
        getAllOnlineAppointmentsOFUserFromDatabase();
        return root;
    }

    private void initView() {
        btn_view_all_appointments.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_all_appointments));

        btn_view_all_messages.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_myMessages));
    }

    private void getUserInfo() {
        fireBaseManager.getUser(auth.getCurrentUser().getUid(), new FireBaseManager.CallBack<User>() {
            @Override
            public void res(User user) {
                if (user == null) {
                    return;
                }
                user_name.setText("Hello " + user.getName());
            }
        });
    }



    private void getAllOnlineAppointmentsOFUserFromDatabase() {
        String userId = auth.getCurrentUser().getUid();
        fireBaseManager.getAllOnlineAppointmentsForUser(userId, new ListenerGetAllOnlineAppointmentFromDB() {
            @Override
            public void onOnlineAppointmentFromDBLoadSuccess(ArrayList<OnlineAppointment> appointments) {
                int count = 0;
                for (OnlineAppointment appointment : appointments) {
                    if (appointment!=null &&  appointment.getActive()) {
                        count++;
                    }
                }
                numOfMessagesPending.setText(" You have " + count + " pending messages");
            }

            @Override
            public void onOnlineAppointmentFromDBLoadFailed(String message) {
                Log.e("ravid", "Failed to load online appointments: " + message);
            }
        });
    }

    private void getAllAppointmentsFromDatabase() {
        fireBaseManager.getAllAppointments(new ListenerGetAllAppointmentFromDB() {
            @Override
            public void onAppointmentFromDBLoadSuccess(ArrayList<Appointment> appointments) {
                allAppointments.clear();
                allAppointments.addAll(appointments);
                isVeterinarian();
            }

            @Override
            public void onAppointmentFromDBLoadFailed(String message) {
                Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isVeterinarian() {
        fireBaseManager.isVeterinarian(auth.getCurrentUser().getUid(), new FireBaseManager.CallBack<Boolean>() {
            @Override
            public void res(Boolean isVet) {
                if (isVet) {
                    userAppointments=allAppointments;
                    getTheNextappointment();
                    getAllOnlineAppointments();
                } else {
                    getUserAppointment();
                }
            }
        });
    }

    private void getAllOnlineAppointments() {
        fireBaseManager.getAllOnlineAppointments(new ListenerGetAllOnlineAppointmentFromDB(){

            @Override
            public void onOnlineAppointmentFromDBLoadSuccess(ArrayList<OnlineAppointment> appointments) {
                int count = 0;
                for (OnlineAppointment appointment : appointments) {
                    if (appointment!=null &&  appointment.getActive()) {
                        count++;
                    }
                }
                numOfMessagesPending.setText(" You have " + count + " pending messages");
            }

            @Override
            public void onOnlineAppointmentFromDBLoadFailed(String message) {

            }
        });

    }

    private void getTheNextappointment() {
        ArrayList<Appointment> filteredAppointments = new ArrayList<>();
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

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

        if (!filteredAppointments.isEmpty()) {
            Collections.sort(filteredAppointments, (a1, a2) -> {
                try {
                    Date date1 = sdf.parse(a1.getDate() + " " + a1.getTime());
                    Date date2 = sdf.parse(a2.getDate() + " " + a2.getTime());
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            ArrayList<Appointment> updateUserAppointments = new ArrayList<>();
            updateUserAppointments.add(filteredAppointments.get(0));
            appointmentAdapter.updateAppointments(updateUserAppointments, appointmentAdapter.getIsPastAppointments());
        } else {
            ArrayList<Appointment> updateUserAppointments = new ArrayList<>();
            appointmentAdapter.updateAppointments(updateUserAppointments, appointmentAdapter.getIsPastAppointments());
            titleYourNextAppointment.setText("You have no upcoming appointments");
        }

    }

    private void getUserAppointment() {
        String currentUserId = auth.getCurrentUser().getUid();
        userAppointments.clear();
        for (Appointment appointment : allAppointments) {
            if (appointment.getIdCustomer().equals(currentUserId)) {
                userAppointments.add(appointment);
            }
        }
        getTheNextappointment();


    }



    private void setupAdapterAndRecyclerView() {
        appointmentAdapter = new AppointmentAdapter(getContext(), userAppointments, false);
        appointmentAdapter.setAppointmentCallBack(new CancelAppointmentCallback() {
            @Override
            public void cancelAppointment(Appointment appointment, int position) {
                fireBaseManager.removeAppointment(appointment);
                fireBaseManager.removeAppointmentFromUser(appointment.getIdCustomer(), appointment.getAppointmentId());
                userAppointments.remove(appointment);
                getTheNextappointment();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_view_appointments.setLayoutManager(linearLayoutManager);
        recycler_view_appointments.setAdapter(appointmentAdapter);
    }

    private void findViews() {
        recycler_view_appointments = binding.recyclerViewNextAppointment;
        user_name = binding.txtHello;
        numOfMessagesPending = binding.txtMessagePending;
        btn_view_all_appointments = binding.btnViewAllAppointments;
        btn_view_all_messages = binding.btnViewAllMessages;
        titleYourNextAppointment = binding.txtNextAppointment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
