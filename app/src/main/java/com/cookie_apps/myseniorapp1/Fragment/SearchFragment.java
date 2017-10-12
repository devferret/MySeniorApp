package com.cookie_apps.myseniorapp1.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookie_apps.myseniorapp1.Adapter.AdapterItemSearch;
import com.cookie_apps.myseniorapp1.MainActivity;
import com.cookie_apps.myseniorapp1.MyApplication;
import com.cookie_apps.myseniorapp1.MyClass.Font;
import com.cookie_apps.myseniorapp1.R;
import com.cookie_apps.myseniorapp1.ShowDataActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    EditText edtSearch;
    ListView list;
    GridView grid;
    AdapterItemSearch adapter;
    TextView textNoData;
    String department;
    ImageView btnFilter;
    String filter = "ทั้งหมด";

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = this.getArguments();
        department = args.getString("department");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView(view);

        if (isOnline()) {
            grid.setVisibility(View.VISIBLE);
            textNoData.setVisibility(View.GONE);
            query();
        } else {
            grid.setVisibility(View.GONE);
            textNoData.setVisibility(View.VISIBLE);
        }

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    if (edtSearch.getText().toString() != null)
                        if (isOnline()) {
                            grid.setVisibility(View.VISIBLE);
                            textNoData.setVisibility(View.GONE);
                            query();
                        } else {
                            grid.setVisibility(View.GONE);
                            textNoData.setVisibility(View.VISIBLE);
                        }
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isOnline()) {
            query();
        }
    }

    private void bindView(View v) {
        edtSearch = (EditText) v.findViewById(R.id.edtSearch);
        list = (ListView) v.findViewById(R.id.list);
        grid = (GridView) v.findViewById(R.id.grid);
        textNoData = (TextView) v.findViewById(R.id.textNoData);
        btnFilter = (ImageView) v.findViewById(R.id.btnFilter);

        Font.setFontFace(edtSearch);
        Font.setFontFace(textNoData);

        filter();
    }

    private void query() {
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        ParseQuery<ParseObject> query_code = ParseQuery.getQuery("Articles");
        // Code
        if (!edtSearch.getText().toString().isEmpty())
            query_code.whereMatches("code", edtSearch.getText().toString(), "i");
        else
            query_code.whereMatches("code", "", "i");

        queries.add(query_code);

        // List
        ParseQuery<ParseObject> query_name = ParseQuery.getQuery("Articles");
        if (!edtSearch.getText().toString().isEmpty()) {
            query_name.whereMatches("list", edtSearch.getText().toString(), "i");
            queries.add(query_name);
        }

        // Price
        ParseQuery<ParseObject> query_price = ParseQuery.getQuery("Articles");
        if (!edtSearch.getText().toString().isEmpty()) {
            query_price.whereMatches("price", edtSearch.getText().toString(), "i");
            queries.add(query_price);
        }

        Log.e("department", department);
        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        if (!department.contains("Admin"))
            mainQuery.whereEqualTo("department", department); // Department
        if (!filter.contains("ทั้งหมด")) mainQuery.whereEqualTo("status", filter); // Filter
        if (filter.equals("ยังไม่ได้รับการตรวจ")) mainQuery.whereEqualTo("status", "ไม่มีข้อมูล"); // Filter
        mainQuery.setLimit(15);
        mainQuery.orderByAscending("updatedAt");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.e("objects", String.valueOf(objects.size()));
//
                    adapter = new AdapterItemSearch(getActivity(), objects);
                    grid.setAdapter(adapter);
                    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), ShowDataActivity.class);
                            intent.putExtra("result", objects.get(position).getString("code"));
                            startActivity(intent);
                        }
                    });

                    if (objects.size() == 0) {
                        Toast.makeText(getActivity(), "ไม่พบข้อมูล", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("ParseException Search", e.toString());
                    Toast.makeText(getActivity(), "ParseException Search: " + e.toString(), Toast.LENGTH_LONG).show();
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

    public void filter() {
        final String[] status_dialog = {"ทั้งหมด", "ใช้งานอยู่", "ชำรุด/เสื่อมสภาพ", "ไม่จำเป็นใช้งาน", "หาไม่พบ", "ยังไม่ได้รับการตรวจ"};
        final String[] _status_dialog = {"ทั้งหมด", "ใช้งานอยู่", "ชำรุด/เสื่อมสภาพ", "ไม่จำเป็นใช้งาน", "หาไม่พบ", "ยังไม่ได้รับการตรวจ"};

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("แสดงสภาพของสินทรัพย์");
                builder.setItems(_status_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        filter = status_dialog[item];
                        query();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
