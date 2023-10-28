package com.zebra.fxreader;

import static com.zebra.fxreader.RFIDController.hostName;
import static com.zebra.fxreader.RFIDController.mReader;
import static com.zebra.fxreader.RFIDController.port;
import static com.zebra.fxreader.RFIDController.timeoutMilliSeconds;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import androidx.navigation.fragment.NavHostFragment;

import com.zebra.fxreader.databinding.FragmentConnectBinding;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDReader;

public class ConnectFragment extends Fragment {

    private static final String TAG = "CONNECT_FRAGMENT";
    private FragmentConnectBinding binding;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentConnectBinding.inflate(inflater, container, false);
        setUpMenu();
        return binding.getRoot();
    }

    private void setUpMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.menu_fragment_connect, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_inventory) {
                    NavHostFragment.findNavController(ConnectFragment.this)
                .navigate(R.id.action_ConnectFragment_to_InventoryFragment);
                    return true;
                }
                return false;
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUI();

        binding.tvSdkVersion.setText(com.zebra.rfid.api3.BuildConfig.VERSION_NAME);

        binding.edittextIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Patterns.IP_ADDRESS.matcher(s).matches()) {
                    hostName = s.toString().trim();
                    binding.buttonConnect.setEnabled(true);
                }
            }
        });

        binding.buttonConnect.setOnClickListener(view1 -> {
            if (mReader != null && mReader.isConnected()) {
                try {
                    mReader.disconnect();
                    updateUI();
                } catch (InvalidUsageException | OperationFailureException e) {
                    if (e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                }
            } else {
                Log.d(TAG, "hostName: " + hostName);
                if (!hostName.isEmpty()) {
                    mReader = new RFIDReader(hostName, port, timeoutMilliSeconds);
                    Log.d(TAG, String.valueOf(mReader.getHostName()));
                    try {
                        mReader.connect();
                        ReaderConnection connection = new MainActivity();
                        connection.onConnected();
                        updateUI();
                    } catch (InvalidUsageException | OperationFailureException e) {
                        if (e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    }
                } else {
                    binding.edittextIp.setError("Please enter valid IP");
                }
            }
        });
    }

    private void updateUI() {
        if (mReader != null && mReader.isConnected()) {
            binding.buttonConnect.setText(R.string.disconnect);
            binding.tvStatus.setText(String.format("Connected to: %s", mReader.getHostName()));
            binding.tvReaderId.setText(mReader.ReaderCapabilities.ReaderID.getID());
            binding.tvModelName.setText(mReader.ReaderCapabilities.getModelName());
            binding.tvCommunicationStandard.setText(mReader.ReaderCapabilities.getCommunicationStandard().toString());
            binding.tvCountryCode.setText(String.valueOf(mReader.ReaderCapabilities.getCountryCode()));
            binding.tvFirmwareVersion.setText(mReader.ReaderCapabilities.getFirwareVersion());
            binding.tvRssiFilter.setText(String.valueOf(mReader.ReaderCapabilities.isRSSIFilterSupported()));
        } else {
            binding.buttonConnect.setText(R.string.connect);
            binding.tvStatus.setText(R.string.not_connected);
            binding.tvReaderId.setText("");
            binding.tvModelName.setText("");
            binding.tvCommunicationStandard.setText("");
            binding.tvCountryCode.setText("");
            binding.tvFirmwareVersion.setText("");
            binding.tvRssiFilter.setText("");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}