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

        // Seed data to Firestore if needed
        new com.example.healthbook.data.ApiRepository().seedDataIfNeeded();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.navView, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.navigation_home || id == R.id.navigation_appointments || 
                    id == R.id.navigation_profile || id == R.id.navigation_notifications || 
                    id == R.id.navigation_account || id == R.id.navigation_doctor_home ||
                    id == R.id.navigation_doctor_schedule || id == R.id.navigation_doctor_appointments ||
                    id == R.id.navigation_admin_dashboard || id == R.id.navigation_admin_users ||
                    id == R.id.navigation_admin_hospitals) {
                    binding.navView.setVisibility(android.view.View.VISIBLE);
                } else {
                    binding.navView.setVisibility(android.view.View.GONE);
                }
            });
        }
    }

    public void setupNavigationForRole(String role) {
        if (role == null) role = "patient";
        
        binding.navView.getMenu().clear();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment == null) return;
        NavController navController = navHostFragment.getNavController();

        if (role.equalsIgnoreCase("admin")) {
            binding.navView.inflateMenu(R.menu.bottom_nav_menu_admin);
            navController.navigate(R.id.navigation_admin_dashboard);
        } else if (role.equalsIgnoreCase("doctor")) {
            binding.navView.inflateMenu(R.menu.bottom_nav_menu_doctor);
            navController.navigate(R.id.navigation_doctor_home);
        } else {
            binding.navView.inflateMenu(R.menu.bottom_nav_menu);
            navController.navigate(R.id.navigation_home);
        }
    }
}
