package com.example.vetappointment.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.Models.VetManager;
import com.example.vetappointment.databinding.FragmentAboutUsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

public class AboutUsFragment extends Fragment {

    private FragmentAboutUsBinding binding;
    private ImageView logoImageView;
    private MaterialTextView introductionTextView;
    private MaterialTextView openingTimeTextView;
    private MaterialTextView closingTimeTextView;
    private MaterialTextView addressTextView;
    private FloatingActionButton mapButton;
    private FloatingActionButton phoneButton;
    private String phoneNumber;

    FireBaseManager fireBaseManager = FireBaseManager.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAboutUsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews();
        getVetManager();
        initView();

        return root;
    }

    private void initView(){
        mapButton.setOnClickListener(v -> navigateToMaps());
        phoneButton.setOnClickListener(v -> callToPhone());
    }

    private void findViews() {
        logoImageView = binding.logoImageView;
        introductionTextView = binding.introductionTextView;
        openingTimeTextView = binding.openingTimeTextView;
        closingTimeTextView = binding.closingTimeTextView;
        addressTextView = binding.addressTextView;
        mapButton = binding.buttonMap;
        phoneButton = binding.buttonPhone;
    }

    private void getVetManager() {
        fireBaseManager.getVetManager(new FireBaseManager.CallBack<VetManager>() {
            @Override
            public void res(VetManager res) {
                introductionTextView.setText(res.getVetDescription());
                openingTimeTextView.setText("Opening Time: " +res.getStartTime()+ " AM");
                closingTimeTextView.setText("Closing Time: " +res.getEndTime()+ " PM");
                addressTextView.setText(res.getVetAddress());
                phoneNumber = res.getVetPhone();
            }
        });
    }

    private void navigateToMaps() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(addressTextView.getText().toString()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void callToPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
