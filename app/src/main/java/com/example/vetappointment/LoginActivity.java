package com.example.vetappointment;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetappointment.Models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

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
        initUI();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        auth= FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if(user==null)
            login();
        else{
            prepopulateUserDetails(user);
            checkUserExistence(user.getUid());
        }
    }

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


    private void updateUIWithUserDetails(FirebaseUser user) {
        nameEditText.setText(user.getEmail());
        nameEditText.setText(user.getDisplayName());
        phoneEditText.setText(user.getPhoneNumber());
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



    private void prepopulateUserDetails(FirebaseUser user) { // if user details are already saved, prepopulate the fields
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            nameEditText.setText(user.getDisplayName());
            nameEditText.setEnabled(false);
        } else {
            nameEditText.setEnabled(true);
            nameEditText.setFocusable(true);
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailEditText.setText(user.getEmail());
            emailEditText.setEnabled(false);
        } else {
            emailEditText.setEnabled(true);
            emailEditText.setFocusable(true);

        }

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            phoneEditText.setText(user.getPhoneNumber());
            phoneEditText.setEnabled(false);
        } else {
            phoneEditText.setEnabled(true);
            phoneEditText.setFocusable(true);
        }
        Log.e("ffff", "prepopulateUserDetails: ");
    }


    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK && auth.getCurrentUser()!=null) {
            updateUIWithUserDetails(auth.getCurrentUser());
            FirebaseUser user = auth.getCurrentUser();
            checkUserExistence(user.getUid());

        } else {
            Toast.makeText(this, "Sign-in cancelled by user.", Toast.LENGTH_SHORT).show();
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

    private void attemptSaveUserInformation() {
        if (validateInputs()) {
            saveUserInformation();
        }
    }
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

    public void saveUserInformation(){
        String name = nameEditText.getText().toString();
        String email =  emailEditText.getText().toString();
        String phone =  phoneEditText.getText().toString();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return;
        }

        User newuser = new User()  //save user details
                .setId(user.getUid())
                .setEmail(email)
                .setName(name)
                .setPhone(phone)
                .setAppointments(null)
                .setIsDoctor(false).
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

    private void checkUserExistence(String uid) {

        DatabaseReference userFromDB = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userFromDB.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                navigateToMainActivity();
            } else {
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


}
