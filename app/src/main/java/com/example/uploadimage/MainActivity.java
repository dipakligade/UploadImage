package com.example.uploadimage;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.Bottomnavigation);
        NavController navController = Navigation.findNavController(this, R.id.fragment);

        // Use NavigationUI to set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}
