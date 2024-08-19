package com.example.vetappointment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetappointment.Models.User;
import com.example.vetappointment.Models.VetManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private DatabaseReference userRef;

    private MaterialButton saveButton;
    private MaterialButton signOutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        setupUI(findViewById(android.R.id.content));
        initUI();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        // addDoctorOffice(); // Add the doctor's office to the database only once

        if (user == null) {
            login();
        } else {
            checkUserExistence(user);
        }

    }



    // Add the doctor's office to the database only once when the app is first installed
    private  void addDoctorOffice() {
        VetManager vetManager = new VetManager();
        vetManager.setVetId(UUID.randomUUID().toString())
                .setVetName("Vet Care")
                .setVetPhone("0544922924")
                .setVetEmail("vetClinic@gmail.com").
                setVetAddress("Yitzhak ben Zvi St 18, Petah Tikva")
                .setVetDescription("Our clinic is dedicated to providing the highest quality of care for your pets. We are committed to ensuring that every visit is a positive experience for both you and your beloved animal companions. Our experienced staff and state-of-the-art facilities are here to meet all of your pet's health and wellness needs.").
                setStartTime("8:00").
                setEndTime("20:00");
        DatabaseReference officeReference = FirebaseDatabase.getInstance().getReference("vetManager");
        officeReference.setValue(vetManager);
    }
    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );
  // Choose authentication providers and create and launch sign-in intent
    private void login() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .build();
        signInLauncher.launch(signInIntent);

    }

        // Handle the result of the sign-in process and update the UI
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK && auth.getCurrentUser()!=null) {
            updateUIWithUserDetails(auth.getCurrentUser());
            FirebaseUser user = auth.getCurrentUser();
            checkUserExistence(user);

        } else {
            Toast.makeText(this, "Sign-in cancelled by user.", Toast.LENGTH_SHORT).show();
        }
    }
    // Update the UI with the user's details
    private void updateUIWithUserDetails(FirebaseUser user) {
        nameEditText.setText(user.getEmail());
        nameEditText.setText(user.getDisplayName());
        phoneEditText.setText(user.getPhoneNumber());
    }


    private void prepopulateUserDetails(FirebaseUser user) { // if user details are already saved, prepopulate the fields
        boolean isDetailsNeeded = false;
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            nameEditText.setText(user.getDisplayName());
            nameEditText.setEnabled(false);
            isDetailsNeeded = true;
        } else {
            nameEditText.setEnabled(true);
            nameEditText.setFocusable(true);
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailEditText.setText(user.getEmail());
            emailEditText.setEnabled(false);
            isDetailsNeeded = true;
        } else {
            emailEditText.setEnabled(true);
            emailEditText.setFocusable(true);

        }

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            phoneEditText.setText(user.getPhoneNumber());
            phoneEditText.setEnabled(false);
            isDetailsNeeded = true;
        } else {
            phoneEditText.setEnabled(true);
            phoneEditText.setFocusable(true);
        }
        if (isDetailsNeeded) {
            new AlertDialog.Builder(this)
                    .setTitle("Details Needed")
                    .setMessage("Some details are missing. Please complete the fields.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Just close the dialog
                        dialog.dismiss();
                    })
                    .show();
        }
    }





    private void findViews() {
        signOutButton = findViewById(R.id.signOutButton);
        saveButton = findViewById(R.id.saveButton);
        nameEditText = findViewById(R.id.editName);
        emailEditText = findViewById(R.id.editEmail);
        phoneEditText = findViewById(R.id.editPhoneNumber);
    }

    private void initUI() {
        signOutButton.setOnClickListener(v -> signOut());
        saveButton.setOnClickListener(v -> attemptSaveUserInformation());
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
    }
    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to sign out", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // Validate the inputs and save the user's information
    private void attemptSaveUserInformation() {
        if (validateInputs()) {
            saveUserInformation();
        }
    }

    // Validate the inputs and save the user's information to the database
    private boolean validateInputs() {
        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError("Name is required");
            return false;
        }
        String email =emailEditText.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please make sure the email is valid");
            return false;
        }

        if (phoneEditText.getText().toString().isEmpty() || !checkPhoneNumber(phoneEditText.getText().toString())) {
            phoneEditText.setError("Phone is required");
            return false;
        }

        return true;
    }

    // Save the user's information to the database
    public void saveUserInformation(){
        String name = nameEditText.getText().toString();
        String email =  emailEditText.getText().toString();
        String phone =  phoneEditText.getText().toString();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return;
        }
      //check if exist doctor in the database if not the user will be the doctor and save it to the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.orderByChild("isDoctor").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isDoctor = !snapshot.exists(); // If no doctor exists, this user will be the doctor.


                User newuser = new User()  //save user details
                        .setId(user.getUid())
                        .setEmail(email)
                        .setName(name)
                        .setPhone(phone)
                        .setAppointments(null)
                        .setIsDoctor(isDoctor).
                        setMessagesToDoctor(null);


                userRef.child(user.getUid()).setValue(newuser) //save user details to the database
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                navigateToMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to save user details", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }
  // Check if the user exists in the database and navigate to the main activity
    private void checkUserExistence(FirebaseUser user) {

        DatabaseReference userFromDB = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        userFromDB.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                navigateToMainActivity();
            } else {
                prepopulateUserDetails(user);
                Toast.makeText(this, "No such user exists", Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public  boolean checkPhoneNumber(String string) {
        if (string.length()< 10) {
            return false;
        }
        int i=0;
        if (string.charAt(0) == '+') {
            i++;
        }
        for (; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    private void setupUI(View view) {
        // Set up a touch listener to close the keyboard when the user taps outside the text boxes
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard(v);
                return false;
            });
        }

        // If a layout container, iterate over children and apply the setupUI method
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}