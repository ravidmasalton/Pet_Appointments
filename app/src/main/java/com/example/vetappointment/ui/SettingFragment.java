package com.example.vetappointment.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetappointment.Adapter.BlockDateAdapter;
import com.example.vetappointment.Interfaces.CancelBlockDateCallBack;
import com.example.vetappointment.Listeners.ListenerBlockAppointmentFromDB;
import com.example.vetappointment.Listeners.ListenerGetAllAppointmentFromDB;
import com.example.vetappointment.Models.Appointment;
import com.example.vetappointment.Models.BlockAppointment;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.databinding.FragmentSettingBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private Spinner startTime;
    private Spinner endTime;
    private TextInputEditText dateText;
    private TextInputEditText reasonText;
    private RecyclerView recyclerView;
    private MaterialButton submitButton;
    private BlockDateAdapter blockDateAdapter;
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();
    private ArrayList<BlockAppointment> blockAppointments = new ArrayList<>();
    private ArrayList<Appointment> appointments = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupUI(root);
        findViews();
        getAllBlockAppointments();
        initView();
        getAllAppointments();

        return root;
    }

    private void initView() {
        submitButton.setOnClickListener(v -> {
            String selectedDate = dateText.getText().toString();
            String start = startTime.getSelectedItem().toString();
            String end = endTime.getSelectedItem().toString();
            String reason = reasonText.getText().toString();

            if (selectedDate.isEmpty()) {
                dateText.setError("Please select a date");
                return;
            }
            if (start.isEmpty()) {
                return;
            }
            if (end.isEmpty()) {
                return;
            }
            if (reason.isEmpty()) {
                reasonText.setError("Please provide a reason");
                return;
            }
            fireBaseManager.updateVetManager(start, end);
            BlockAppointment blockAppointment = new BlockAppointment(selectedDate, reason);
            blockAppointment.setBlockDayId(UUID.randomUUID().toString());
            saveBlockAppointment(blockAppointment);
            // Clear fields after saving
            dateText.setText("");
            reasonText.setText("");
            blockAppointments.add(blockAppointment);
            blockDateAdapter.setBlockDates(blockAppointments);
        });

    }


    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        blockDateAdapter = new BlockDateAdapter(getContext(), blockAppointments);
        blockDateAdapter.setCancelBlockDateCallBack(new CancelBlockDateCallBack() {
            @Override
            public void cancelBlockDate(BlockAppointment blockDate, int position) {
                fireBaseManager.removeBlockAppointment(blockDate);
                blockAppointments.remove(position);
                blockDateAdapter.notifyItemRemoved(position);
            }
        });
        recyclerView.setAdapter(blockDateAdapter);
    }

    private void saveBlockAppointment(BlockAppointment blockAppointment) {
        fireBaseManager.saveBlockAppointment(blockAppointment);
    }

    private void getAllBlockAppointments() {
        fireBaseManager.getAllBlockAppointments(new ListenerBlockAppointmentFromDB() {
            @Override
            public void onBlockAppointmentLoadSuccess(ArrayList<BlockAppointment> blockAppointmentList) {
                blockAppointments = blockAppointmentList;
                initRecyclerView();

            }

            @Override
            public void onBlockAppointmentLoadFailed(String message) {
                // Handle error
            }
        });
    }

    private void initDatePickerAndTime() {
        dateText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (getContext() == null) return;

            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        dateText.setText(selectedDate);
                    },
                    year, month, day
            );

            Calendar minDate = Calendar.getInstance();
            datePickerDialog.setMinDate(minDate);

            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.MONTH, 4);
            datePickerDialog.setMaxDate(maxDate);

            // Disable dates with appointments
            Calendar[] disabledDays = getDisabledDays();
            datePickerDialog.setDisabledDays(disabledDays);

            datePickerDialog.show(getParentFragmentManager(), "Datepickerdialog");
        });
    }

    private void getAllAppointments() {
        fireBaseManager.getAllAppointments(new ListenerGetAllAppointmentFromDB() {

            @Override
            public void onAppointmentFromDBLoadSuccess(ArrayList<Appointment> appointments) {
                getAllAppointments(appointments);
                initDatePickerAndTime();
            }

            @Override
            public void onAppointmentFromDBLoadFailed(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getAllAppointments(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }

    private Calendar[] getDisabledDays() { // This method will return an array of disabled days to disable in the date picker
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());  // Assuming BlockDay uses this format
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 4);
        ArrayList<Calendar> disabledDays = new ArrayList<>();

        while (startDate.before(endDate)) {
            int dayOfWeek = startDate.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                Calendar disabledDay = Calendar.getInstance();
                disabledDay.setTime(startDate.getTime());
                disabledDays.add(disabledDay);
            }
            startDate.add(Calendar.DAY_OF_YEAR, 1); // Increment day by day
        }

        for (Appointment appointment : appointments) {
            try {
                Date blockDate = sdf.parse(appointment.getDate());
                Calendar blockCalendar = Calendar.getInstance();
                blockCalendar.setTime(blockDate);
                disabledDays.add(blockCalendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (BlockAppointment blockDay : blockAppointments) {
            try {
                Date blockDate = sdf.parse(blockDay.getDate());
                Calendar blockCalendar = Calendar.getInstance();
                blockCalendar.setTime(blockDate);
                disabledDays.add(blockCalendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Convert ArrayList to Array
        Calendar[] disabledDaysArray = new Calendar[disabledDays.size()];
        disabledDaysArray = disabledDays.toArray(disabledDaysArray);
        return disabledDaysArray;
    }

    private void setupUI(View view) {
        // Set up touch listener for non-text box views to hide the keyboard.
        if (!(view instanceof TextInputEditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard(v);
                return false;
            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void findViews() {
        startTime = binding.optionsSpinnerStartTime;
        endTime = binding.optionsSpinnerEndTime;
        dateText = binding.appointmentDate;
        recyclerView = binding.recyclerViewBlockAppointments;
        reasonText = binding.blockReason;
        submitButton = binding.buttonSubmit;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
