package com.example.vetappointment.ui.gallery;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vetappointment.Appointment;
import com.example.vetappointment.User;
import com.google.firebase.auth.FirebaseUser;
import com.wdullaer.materialdatetimepicker.time.Timepoint;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.vetappointment.FireBaseManager;
import com.example.vetappointment.databinding.FragmentGalleryBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    private TextInputEditText petTypeEditText;
    private TextInputEditText appointmentDateEditText;
    private TextInputEditText appointmentTimeEditText;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Spinner optionsSpinner;
    private MaterialButton confirmButton;
    private FirebaseAuth auth;
    FireBaseManager fireBaseManager = FireBaseManager.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews();
        auth = FirebaseAuth.getInstance();
        initDatePicker();
        initTimePicker();
        confirmButton.setOnClickListener(v -> confirmAppointment());





        return root;
    }















    private void initDatePicker() {
        appointmentDateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
                // Format the selected date
                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                appointmentDateEditText.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });
    }
    private void initTimePicker() {
        timePickerDialog = TimePickerDialog.newInstance(
                (view, hourOfDay, minute, second) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    appointmentTimeEditText.setText(selectedTime);
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                0, // Set minutes to 0 initially
                true
        );

        timePickerDialog.setMinTime(8, 0, 0);
        timePickerDialog.setMaxTime(20, 0, 0);

        ArrayList<Timepoint> selectableTimes = new ArrayList<>();
        for (int hour = 8; hour <= 20; hour++) {
            selectableTimes.add(new Timepoint(hour, 0));
            if (hour != 20) { // Exclude 20:30
                selectableTimes.add(new Timepoint(hour, 30));
            }
        }
        timePickerDialog.setSelectableTimes(selectableTimes.toArray(new Timepoint[0]));

        appointmentTimeEditText.setOnClickListener(v -> timePickerDialog.show(getParentFragmentManager(), "Timepickerdialog"));
    }



    private void confirmAppointment() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this.getContext(), "No user found", Toast.LENGTH_SHORT).show();
            return;
        }
        fireBaseManager.getUser(user.getUid(),new FireBaseManager.CallBack<User>() {
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

    private void addAppointmentToDB(User user){

        String petType = petTypeEditText.getText().toString();
        String appointmentDate = appointmentDateEditText.getText().toString();
        String appointmentTime = appointmentTimeEditText.getText().toString();
        String service = optionsSpinner.getSelectedItem().toString();
        if (petType.isEmpty() || appointmentDate.isEmpty() || appointmentTime.isEmpty() || service.isEmpty()) {
            Toast.makeText(this.getContext(), "No full details", Toast.LENGTH_SHORT).show();
            return;
        }
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(UUID.randomUUID().toString()).
                setDate(appointmentDate).
                setTime(appointmentTime).
                setService(service).
                setCostumerPhone(user.getPhone()).
                setIdCustomer(user.getId()).
                setCustomerName(user.getName());

        fireBaseManager.saveAppointment(appointment);
        fireBaseManager.saveAppointmentForUser(user, appointment.getAppointmentId());
    }







    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    private void findViews() {
        petTypeEditText = binding.PetType;
        appointmentDateEditText = binding.appointmentDate;
        appointmentTimeEditText =binding.appointmentTime;
        optionsSpinner =binding.optionsSpinner;
        confirmButton = binding.searchButton;

    }



}