package com.example.vetappointment.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vetappointment.Models.Appointment;
import com.example.vetappointment.Models.BlockAppointment;
import com.example.vetappointment.Listeners.ListenerBlockAppointmentFromDB;
import com.example.vetappointment.MainActivity;
import com.example.vetappointment.Models.User;
import com.google.firebase.auth.FirebaseUser;
import com.wdullaer.materialdatetimepicker.time.Timepoint;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.databinding.FragmentGalleryBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class PickAppointmentFragment extends Fragment {

    private FragmentGalleryBinding binding;

    private TextInputEditText petTypeEditText;
    private TextInputEditText appointmentDateEditText;
    private TextInputEditText appointmentTimeEditText;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Spinner optionsSpinner;
    private ArrayList<BlockAppointment> blockAppointments = new ArrayList<>();
    private MaterialButton confirmButton;
    private FirebaseAuth auth;
    FireBaseManager fireBaseManager = FireBaseManager.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews();
        auth = FirebaseAuth.getInstance();
        getAllBlockAppointments(); // Ensure blockAppointments is populated
        initDatePicker();
        confirmButton.setOnClickListener(v -> confirmAppointment());

        return root;
    }

    private void initDatePicker() {
        appointmentDateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if(getContext() == null) return;

            datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
                // Format the selected date
                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                appointmentDateEditText.setText(selectedDate);
                // Clear the previously selected time and show the time picker again
                appointmentTimeEditText.setText("");
                initTimePicker(selectedDate); // Initialize time picker based on selected date
            }, year, month, day);

            // Set the minimum date to today
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            datePickerDialog.show();
        });
    }

    private void initTimePicker(String selectedDate) {
        appointmentTimeEditText.setOnClickListener(v -> {
            final Calendar now = Calendar.getInstance();
            int currentHour = now.get(Calendar.HOUR_OF_DAY);
            int currentMinute = now.get(Calendar.MINUTE);

            timePickerDialog = TimePickerDialog.newInstance(
                    (view, hourOfDay, minute, second) -> {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        appointmentTimeEditText.setText(selectedTime);
                    },
                    currentHour,
                    currentMinute,
                    true
            );

            timePickerDialog.setMinTime(8, 0, 0);
            timePickerDialog.setMaxTime(20, 0, 0);

            ArrayList<Timepoint> selectableTimes = new ArrayList<>();
            boolean isToday = selectedDate.equals(String.format("%d/%d/%d", now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH) + 1, now.get(Calendar.YEAR)));

            for (int hour = 8; hour <= 20; hour++) {
                if (isToday && hour < currentHour) continue;
                selectableTimes.add(new Timepoint(hour, 0));
                if (hour != 20) { // Exclude 20:30
                    if (!isToday || (hour > currentHour || (hour == currentHour && currentMinute <= 30))) {
                        selectableTimes.add(new Timepoint(hour, 30));
                    }
                }
            }

            // Remove blocked times for the selected date
            if (blockAppointments != null) {
                for (BlockAppointment block : blockAppointments) {
                    if (block.getDate().equals(selectedDate)) {
                        String[] timeParts = block.getTime().split(":");
                        int hour = Integer.parseInt(timeParts[0]);
                        int minute = Integer.parseInt(timeParts[1]);
                        selectableTimes.remove(new Timepoint(hour, minute));
                    }
                }
            }

            timePickerDialog.setSelectableTimes(selectableTimes.toArray(new Timepoint[0]));
            timePickerDialog.show(getParentFragmentManager(), "Timepickerdialog");
        });
    }

    private void confirmAppointment() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this.getContext(), "No user found", Toast.LENGTH_SHORT).show();
            return;
        }
        fireBaseManager.getUser(user.getUid(), new FireBaseManager.CallBack<User>() {
            @Override
            public void res(User user) {
                if (user == null) {
                    Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
                    return;
                }
                addAppointmentToDB(user);
            }
        });
        showAlert("Appointment Confirmed", "Your appointment has been confirmed", this::navigateToHome);
       // navigateToHome();
    }

    private void addAppointmentToDB(User user) {
        // Get the input values
        String petType = petTypeEditText.getText().toString();
        String appointmentDate = appointmentDateEditText.getText().toString();
        String appointmentTime = appointmentTimeEditText.getText().toString();
        String service = optionsSpinner.getSelectedItem().toString();
        if (petType.isEmpty() || appointmentDate.isEmpty() || appointmentTime.isEmpty() || service.isEmpty()) {
            Toast.makeText(this.getContext(), "No full details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Appointment object
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(UUID.randomUUID().toString())
                .setDate(appointmentDate)
                .setTime(appointmentTime)
                .setService(service)
                .setCostumerPhone(user.getPhone())
                .setIdCustomer(user.getId())
                .setCustomerName(user.getName()).setPetType(petType);

        // Create a new BlockAppointment object
        BlockAppointment blockAppointment = new BlockAppointment();
        blockAppointment.setBlockDayId(UUID.randomUUID().toString())
                .setDate(appointmentDate)
                .setTime(appointmentTime).setAppointmentId(appointment.getAppointmentId());

        // Save the appointment and block appointment to the database
        saveBlockAppointment(blockAppointment);
        fireBaseManager.saveAppointment(appointment);
        fireBaseManager.saveAppointmentForUser(user, appointment.getAppointmentId());
    }

    public void saveBlockAppointment(BlockAppointment blockAppointment) {
        fireBaseManager.saveBlockAppointment(blockAppointment);
    }

    public void getAllBlockAppointments() {
        fireBaseManager.getAllBlockAppointments(new ListenerBlockAppointmentFromDB() {
            @Override
            public void onBlockAppointmentLoadSuccess(ArrayList<BlockAppointment> blockAppointment) {
                blockAppointments = new ArrayList<>(blockAppointment);
            }

            @Override
            public void onBlockAppointmentLoadFailed(String message) {
                // Handle the error
            }
        });
    }

    public void navigateToHome() {
         Intent intent = new Intent(this.getContext(), MainActivity.class);
        startActivity(intent);

    }

    private void showAlert(String title, String message, Runnable onDismiss) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> onDismiss.run())
                .setCancelable(false) // Prevents the dialog from being dismissed by the back button or tapping outside
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void findViews() {
        petTypeEditText = binding.PetType;
        appointmentDateEditText = binding.appointmentDate;
        appointmentTimeEditText = binding.appointmentTime;
        optionsSpinner = binding.optionsSpinner;
        confirmButton = binding.searchButton;
    }
}
