package com.zebra.fxreader;

import static com.zebra.fxreader.RFIDController.isInventoryRunning;
import static com.zebra.fxreader.RFIDController.mReader;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.zebra.fxreader.databinding.FragmentInventoryBinding;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;

import java.util.ArrayList;

public class InventoryFragment extends Fragment {
    private static final String TAG = "INVENTORY_FRAGMENT";
    private FragmentInventoryBinding binding;
    InventoryAdapter adaptor;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        setUpMenu();
        return binding.getRoot();
    }

    private void setUpMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.menu_fragment_inventory, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
               return false;
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isInventoryRunning) {
            binding.fabInventory.setImageResource(android.R.drawable.ic_media_pause);
        }

        binding.fabInventory.setOnClickListener(v -> {
            if (mReader != null && mReader.isConnected()) {
                if (!isInventoryRunning) {
                    try {
                        isInventoryRunning = true;
                        mReader.Actions.Inventory.perform();
                        binding.fabInventory.setImageResource(android.R.drawable.ic_media_pause);
                    } catch (InvalidUsageException | OperationFailureException e) {
                        if (e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    }
                } else {
                    try {
                        isInventoryRunning = false;
                        mReader.Actions.Inventory.stop();
                        binding.fabInventory.setImageResource(android.R.drawable.ic_media_play);
                    } catch (InvalidUsageException | OperationFailureException e) {
                        if (e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    }
                }
            } else {
                Snackbar.make(view, "Reader Not Connected", Snackbar.LENGTH_LONG).show();
            }
        });

        ArrayList<InventoryItem> arrayOfItems = new ArrayList<>();
        adaptor = new InventoryAdapter(requireActivity(), arrayOfItems);
        binding.inventoryList.setAdapter(adaptor);

        RFIDController.viewModel.getInventoryItem().observe(requireActivity(), inventoryItemHashMap -> {
            adaptor.clear();
            adaptor.addAll(inventoryItemHashMap.values());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}