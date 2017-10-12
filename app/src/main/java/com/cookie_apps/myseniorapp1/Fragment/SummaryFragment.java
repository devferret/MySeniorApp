package com.cookie_apps.myseniorapp1.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cookie_apps.myseniorapp1.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by acount on 05/31/16.
 */
public class SummaryFragment extends Fragment {

    private Spinner spinnerYear;
    private PieChart sumChart;
    private String[] statusArray = {"ใช้งานอยู่", "ชำรุด/เสื่อมสภาพ", "ไม่จำเป็นใช้งาน", "หาไม่พบ", "ไม่ได้รับการตรวจ"};
    private ArrayList<String> tempHistoryCode = new ArrayList<>();
    private int worked = 0, waste = 0, notNecessary = 0, notFound = 0, unChecked = 0;
    private List<String> yearsDisplay, years;
    private TextView txtWorked, txtWaste, txtNotNecessary, txtNotFound, txtUnChecked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = this.getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        bindView(view);
    }

    private void bindView(final View v) {
        txtWorked = (TextView) v.findViewById(R.id.txtWorked);
        txtWaste = (TextView) v.findViewById(R.id.txtWaste);
        txtNotNecessary = (TextView) v.findViewById(R.id.txtNotNecessary);
        txtNotFound = (TextView) v.findViewById(R.id.txtNotFound);
        txtUnChecked = (TextView) v.findViewById(R.id.txtUnChecked);

        spinnerYear = (Spinner) v.findViewById(R.id.spinnerYear);
        sumChart = (PieChart) v.findViewById(R.id.sumChart);
        sumChart.setUsePercentValues(true);
        sumChart.setDescription("");
        sumChart.setRotationEnabled(false);
        sumChart.setDrawSlicesUnderHole(false);
        sumChart.setNoDataText("ไม่มีข้อมูล");
        sumChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = sumChart.getLegend();
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        yearsDisplay = new ArrayList<String>();
        yearsDisplay.add("เลือกปีงบประมาณ");
        yearsDisplay.add("ตุลาคม 2557 - กันยายน 2558");
        yearsDisplay.add("ตุลาคม 2558 - กันยายน 2559");
        yearsDisplay.add("ตุลาคม 2559 - กันยายน 2560");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, yearsDisplay);

        years = new ArrayList<String>();
        years.add("2557");
        years.add("2558");
        years.add("2559");

        spinnerYear.setAdapter(dataAdapter);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("click", position + "");

                if (position>0) getData(years.get(position-1));
                else {
                    worked = 0;
                    waste = 0;
                    notNecessary = 0;
                    notFound = 0;
                    unChecked = 0;

                    setData(statusArray.length, 100);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("no click", "");
            }
        });
    }

    private void setData(int count, int range) {
        float mult = range;

        ArrayList<Entry> data = new ArrayList<Entry>();
        data.add(new Entry((float) worked, 0));
        data.add(new Entry((float) waste, 1));
        data.add(new Entry((float) notNecessary, 2));
        data.add(new Entry((float) notFound, 3));
        data.add(new Entry((float) unChecked, 4));

        ArrayList<String> dataName = new ArrayList<String>();

        for (int i = 0; i < count; i++)
            dataName.add(statusArray[i]);

        PieDataSet dataSet = new PieDataSet(data, ""); // Graph name
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData piedata = new PieData(dataName, dataSet);
        piedata.setValueFormatter(new PercentFormatter());
        sumChart.setData(piedata);
        sumChart.invalidate();
    }

    private void getData(final String year) {
        String budgetYear = year;
        worked = 0;
        waste = 0;
        notNecessary = 0;
        notFound = 0;
        unChecked = 0;
        tempHistoryCode.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("History");
        query.whereEqualTo("budgetYear", budgetYear);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null) {
                    Log.e("budgetYear " + year, objects.size() + "");

                    for (int i=0; i<objects.size(); i++) {
                        if (tempHistoryCode.contains(objects.get(i).getString("code"))) {
                            // skip same data
                        } else {
                            Log.e("add", objects.get(i).getString("list") + " status: " + objects.get(i).getString("status"));
                            tempHistoryCode.add(objects.get(i).getString("code")); // first data

                            if (objects.get(i).getString("status").contains("ใช้งานอยู่")) {
                                worked++;

                            } else if (objects.get(i).getString("status").contains("ชำรุด/เสื่อมสภาพ")) {
                                waste++;

                            } else if (objects.get(i).getString("status").contains("ไม่จำเป็นใช้งาน")) {
                                notNecessary++;

                            } else if (objects.get(i).getString("status").contains("หาไม่พบ")) {
                                notFound++;

                            } else {
                                unChecked++;

                            }
                        }
                        txtWorked.setText("ใช้งานอยู่: " + worked);
                        txtWaste.setText("ชำรุด/เสื่อมสภาพ: " + waste);
                        txtNotNecessary.setText("ไม่จำเป็นใช้งาน: " + notNecessary);
                        txtNotFound.setText("หาไม่พบ: " + notFound);
                        txtUnChecked.setText("ไม่ได้รับการตรวจ: " + unChecked);
                    }

                    Log.e("conclude", "worked: " + worked + " waste: " + waste + " notNecessary: " + notNecessary + " notFound: " + notFound + " unChecked: " + unChecked);
                    setData(statusArray.length, 100);
                } else {
                    Log.e("ParseException", e.toString());
                }
            }
        });
    }
}
