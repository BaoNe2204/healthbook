package com.example.healthbook;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.healthbook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.navView, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.navigation_home ||
                    destination.getId() == R.id.navigation_appointments ||
                    destination.getId() == R.id.navigation_profile ||
                    destination.getId() == R.id.navigation_notifications ||
                    destination.getId() == R.id.navigation_account) {
                    binding.navView.setVisibility(android.view.View.VISIBLE);
                } else {
                    binding.navView.setVisibility(android.view.View.GONE);
                }
            });
        }
    }
}
