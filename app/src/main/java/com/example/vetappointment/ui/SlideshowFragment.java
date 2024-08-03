package com.example.vetappointment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.databinding.FragmentSlideshowBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    private RecyclerView recycler_view_appointments;
    private MaterialButton show_BTN_historyAppointments;
    private MaterialButton show_BTN_futureAppointments;
    private FloatingActionButton refresh_BTN;
    private TextInputEditText edit_text_date_filter;
    private FirebaseAuth auth;
    FireBaseManager fireBaseManager = FireBaseManager.getInstance();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        auth = FirebaseAuth.getInstance();
        findViews();


        return root;
    }

    private void findViews() {
        recycler_view_appointments = binding.recyclerViewAppointments;
        show_BTN_historyAppointments = binding.historyAppointmentsButton;
        show_BTN_futureAppointments = binding.futureAppointmentsButton;
        refresh_BTN = binding.resetButton;
        edit_text_date_filter = binding.editTextDateInput;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}