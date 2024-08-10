package com.example.vetappointment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.Models.User;
import com.example.vetappointment.databinding.ActivityMainBinding;
import com.example.vetappointment.ui.SettingFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_add_appointment, R.id.nav_all_appointments, R.id.nav_onlineVet, R.id.nav_myMessages, R.id.nav_review, R.id.nav_about_us, R.id.nav_logout, R.id.nav_setting)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header = navigationView.getHeaderView(0);
        String userId = auth.getCurrentUser().getUid();
        changeNavHeader(header, userId);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });
    }

    private void changeNavHeader(View header, String userId) {
        TextView textTitleLabel = header.findViewById(R.id.name_LBL);
        TextView emailTitleLabel = header.findViewById(R.id.email_LBL);
        ImageView imageTitleLabel = header.findViewById(R.id.imageView);

        fireBaseManager.getUser(userId, new FireBaseManager.CallBack<User>() {
            @Override
            public void res(User res) {
                textTitleLabel.setText(res.getName());
                emailTitleLabel.setText(res.getEmail());
            }
        });

        FirebaseUser user = auth.getCurrentUser();
        Glide.with(header.getContext()) // Load the image from the URL
                .load(user.getPhotoUrl())
                .into(imageTitleLabel);

        // Using Glide to load the image with resizing
        Glide.with(header.getContext())
                .load(user.getPhotoUrl() != null ? user.getPhotoUrl() : R.drawable.user) //if the user has no image, load the default image
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .override(200, 200) // Resize the image
                .into(imageTitleLabel);
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void logout() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // Redirect the user to the LoginActivity
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}
