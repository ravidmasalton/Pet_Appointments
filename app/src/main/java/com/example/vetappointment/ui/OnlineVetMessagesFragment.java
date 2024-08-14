package com.example.vetappointment.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.vetappointment.MainActivity;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.Models.OnlineAppointment;
import com.example.vetappointment.databinding.FragmentOnlineVetMessagesBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class OnlineVetMessagesFragment extends Fragment {

    private FragmentOnlineVetMessagesBinding binding;
    private TextInputEditText editTextUser;
    private ExtendedFloatingActionButton submitButton;
    private ShapeableImageView uploadImageButton;
    private Uri imageUri = null;
    private final FireBaseManager fireBaseManager = FireBaseManager.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    updateImageUrl(uri);
                }
            }); // Register the activity result launcher for picking an image from the gallery

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOnlineVetMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews();

        setupUI(root); // Call the setupUI method to handle hiding the keyboard

        uploadImageButton.setOnClickListener(v -> editImage());

        submitButton.setOnClickListener(v -> saveOnlineAppointment());

        return root;
    }

    private void findViews() { // Find the views by their IDs
        editTextUser = binding.messageInput;
        uploadImageButton = binding.petIMGFromUser;
        submitButton = binding.sendButton;
    }

    private void editImage() { // Launch the activity to pick an image from the gallery
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void updateImageUrl(Uri uri) { // Update the image URI and set the image to the image view
        imageUri = uri;
        uploadImageButton.setImageURI(uri);
    }

    private void saveOnlineAppointment() { // Save the online appointment
        String message = editTextUser.getText().toString().trim();

        if (message.isEmpty()) {
            editTextUser.setError("Message cannot be empty");
            return;
        }

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        OnlineAppointment onlineAppointment = new OnlineAppointment();
        onlineAppointment.setOnlineAppointmentId(UUID.randomUUID().toString())
                .setUserId(auth.getCurrentUser().getUid())
                .setMessage(message)
                .setDate(date)
                .setTime(time)
                .setActive(true);

        // Save the online appointment along with the image URI if available
        fireBaseManager.saveOnlineAppointment(onlineAppointment, imageUri);

        navigateToHome();
    }

    private void navigateToHome() { // Navigate to the main activity
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void setupUI(View view) { // Set up the UI to hide the keyboard when the user taps outside the text boxes
        // Set up a touch listener to close the keyboard when the user taps outside the text boxes
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard(v);
                return false;
            });
        }

        // If a layout container, iterate over children and apply the touch listener
        if (view instanceof ViewGroup) { // If the view is a ViewGroup, iterate over its children and apply the touch listener
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideKeyboard(View view) { // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
