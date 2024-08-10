package com.example.vetappointment.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vetappointment.Listeners.ListenerBlockAppointmentFromDB;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.example.vetappointment.Listeners.ListenerGetAllAppointmentFromDB;
import com.example.vetappointment.Models.Appointment;
import com.example.vetappointment.Models.BlockAppointment;
import com.example.vetappointment.MainActivity;
import com.example.vetappointment.Models.User;
import com.example.vetappointment.Models.VetManager;
import com.example.vetappointment.databinding.FragmentAddNewAppointmentBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.wdullaer.materialdatetimepicker.time.Timepoint;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.vetappointment.Models.FireBaseManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class AddNewAppointmentFragment extends Fragment {

    private FragmentAddNewAppointmentBinding binding;

    private TextInputEditText petTypeEditText;
    private TextInputEditText appointmentDateEditText;
    private TextInputEditText appointmentTimeEditText;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Spinner optionsSpinner;
    private ArrayList<Appointment> allAppointments = new ArrayList<>();
    private String startTime;
    private String endTime;
    private ArrayList<BlockAppointment> allBlockAppointments = new ArrayList<>();
    private ExtendedFloatingActionButton confirmButton;
    private FirebaseAuth auth;
    FireBaseManager fireBaseManager = FireBaseManager.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddNewAppointmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews();
        auth = FirebaseAuth.getInstance();
        getVetManager();
        getAllAppointments(); // Get all appointments from the database
        confirmButton.setOnClickListener(v -> confirmAppointment());

        return root;
    }

    private void initDatePickerAndTime() {
        appointmentDateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (getContext() == null) return;

            datePickerDialog = DatePickerDialog.newInstance(
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the selected date
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        appointmentDateEditText.setText(selectedDate);
                        // Clear the previously selected time and show the time picker again
                        appointmentTimeEditText.setText("");
                        initTimePicker(selectedDate); // Initialize time picker based on selected date
                    },
                    year, month, day
            );

            // Set the minimum date to today
            Calendar minDate = Calendar.getInstance();
            datePickerDialog.setMinDate(minDate);

            // Set the maximum date, e.g., 4 months from today
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.MONTH, 4);
            datePickerDialog.setMaxDate(maxDate);

            // Disable Fridays and Saturdays
            Calendar[] disabledDays = getBlockedDates();
            datePickerDialog.setDisabledDays(disabledDays);

            // Show the DatePickerDialog
            datePickerDialog.show(getParentFragmentManager(), "Datepickerdialog");
        });
    }
    private Calendar[] getBlockedDates() {
        ArrayList<Calendar> blockedDates = new ArrayList<>();

        // Add Fridays and Saturdays to blocked dates
        Calendar startDate = Calendar.getInstance(); // Current date
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 4); // 4 months from today
        Calendar day = (Calendar) startDate.clone();

        while (day.before(endDate)) {
            int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                Calendar blockedDay = (Calendar) day.clone();
                blockedDates.add(blockedDay);
            }
            day.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Add blocked dates from allBlockAppointments
        for (BlockAppointment blockAppointment : allBlockAppointments) {
            Calendar blockedDay = Calendar.getInstance();
            String[] dateParts = blockAppointment.getDate().split("/"); // Assuming the date is in the format "dd/MM/yyyy"
            int dayOfMonth = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Calendar months are 0-based
            int year = Integer.parseInt(dateParts[2]);

            blockedDay.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            blockedDay.set(Calendar.MONTH, month);
            blockedDay.set(Calendar.YEAR, year);

            blockedDates.add(blockedDay);
        }

        return blockedDates.toArray(new Calendar[0]);
    }



    private void getAllBlockAppointments(){
        fireBaseManager.getAllBlockAppointments(new ListenerBlockAppointmentFromDB() {

            @Override
            public void onBlockAppointmentLoadSuccess(ArrayList<BlockAppointment> blockAppointment) {
                allBlockAppointments = blockAppointment;
                initDatePickerAndTime();
            }

            @Override
            public void onBlockAppointmentLoadFailed(String message) {

            }
        });
    }


    private Calendar[] getDisabledDays(Calendar startDate, Calendar endDate) {
        ArrayList<Calendar> disabledDays = new ArrayList<>();
        Calendar day = (Calendar) startDate.clone();

        while (day.before(endDate)) {
            int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                Calendar disabledDay = (Calendar) day.clone();
                disabledDays.add(disabledDay);
            }
            day.add(Calendar.DAY_OF_MONTH, 1);
        }

        return disabledDays.toArray(new Calendar[0]);
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
             int startTimeHour = Integer.parseInt(startTime.split(":")[0]);
             int endTimeHour = Integer.parseInt(endTime.split(":")[0]);
            timePickerDialog.setMinTime(startTimeHour, 0, 0);
            timePickerDialog.setMaxTime(endTimeHour, 0, 0);
            ArrayList<Timepoint> selectableTimes = new ArrayList<>();
            boolean isToday = selectedDate.equals(String.format("%d/%d/%d", now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH) + 1, now.get(Calendar.YEAR)));

            for (int hour = startTimeHour; hour <= endTimeHour; hour++) {
                if (isToday && hour < currentHour) continue;
                if (isToday && hour == currentHour && currentMinute > 0) {
                    if (currentMinute <= 30) {
                        selectableTimes.add(new Timepoint(hour, 30));
                    }
                    continue;
                }
                selectableTimes.add(new Timepoint(hour, 0));
                if (hour != 20) { // Exclude 20:30
                    if (!isToday || (hour > currentHour || (hour == currentHour && currentMinute < 30))) {
                        selectableTimes.add(new Timepoint(hour, 30));
                    }
                }
            }

            // Remove blocked times for the selected date
            if (allAppointments != null) {
                for (Appointment block : allAppointments) {
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

        // Save the appointment to the database

        fireBaseManager.saveAppointment(appointment);
        fireBaseManager.saveAppointmentForUser(user, appointment.getAppointmentId());
        showAlert("Appointment Confirmed", "Your appointment has been confirmed", this::navigateToHome);

    }

    private void getAllAppointments() {
        fireBaseManager.getAllAppointments(new ListenerGetAllAppointmentFromDB() {
            @Override
            public void onAppointmentFromDBLoadSuccess(ArrayList<Appointment> appointments) {
                allAppointments = appointments;
                getAllBlockAppointments();

            }

            @Override
            public void onAppointmentFromDBLoadFailed(String error) {
                Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
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


    private void getVetManager(){
        fireBaseManager.getVetManager(new FireBaseManager.CallBack<VetManager>() {
            @Override
            public void res(VetManager vetManager) {
                if (vetManager == null) {
                    Toast.makeText(getContext(), "No VetManager found", Toast.LENGTH_SHORT).show();
                }
                else{
                    startTime = vetManager.getStartTime();
                    endTime = vetManager.getEndTime();
                }
            }
        });
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
