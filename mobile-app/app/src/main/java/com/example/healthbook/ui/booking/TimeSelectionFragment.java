package com.example.healthbook.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.adapters.DateAdapter;
import com.example.healthbook.adapters.TimeAdapter;
import java.util.Arrays;
import java.util.List;

public class TimeSelectionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_selection, container, false);

        View btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        View btnContinue = view.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.appointmentConfirmFragment));

        RecyclerView rvDates = view.findViewById(R.id.rvDates);
        RecyclerView rvTimes = view.findViewById(R.id.rvTimes);

        List<String> days = Arrays.asList("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6");
        List<String> dates = Arrays.asList("27/05", "28/05", "29/05", "30/05", "31/05");
        DateAdapter dateAdapter = new DateAdapter(days, dates);
        rvDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDates.setAdapter(dateAdapter);

        List<String> times = Arrays.asList("07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "11:00", "13:30");
        TimeAdapter timeAdapter = new TimeAdapter(times);
        rvTimes.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvTimes.setAdapter(timeAdapter);

        return view;
    }
}
