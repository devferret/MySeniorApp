package com.cookie_apps.myseniorapp1.Fragment;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cookie_apps.myseniorapp1.MyClass.ConnectionAlert;
import com.cookie_apps.myseniorapp1.MyClass.Font;
import com.cookie_apps.myseniorapp1.R;
import com.cookie_apps.myseniorapp1.ScanBarcode;

public class ScanBarcodeFragment extends Fragment
{
    TextView txtDesc;
    Button btnScan;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_barcode, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        txtDesc = (TextView) view.findViewById(R.id.txtDesc);
        Font.setFontFace(txtDesc);
        btnScan = (Button) view.findViewById(R.id.btnScan);
        Font.setFontFace(btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (isOnline()) {
                    Intent intent = new Intent(getActivity(), ScanBarcode.class);
                    startActivity(intent);
                } else {
                    new ConnectionAlert(getActivity());
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
