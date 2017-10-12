package com.cookie_apps.myseniorapp1.Adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cookie_apps.myseniorapp1.MyClass.Font;
import com.cookie_apps.myseniorapp1.MyClass.MyParseDateConverter;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import com.cookie_apps.myseniorapp1.R;

/**
 * Created by acount on 15/01/16.
 */
public class AdapterItemSearch extends BaseAdapter
{
    Context context;
    List<ParseObject> objectList;

    String[] status = {"", "ใช้งานอยู่", "ชำรุด/เสื่อมสภาพ", "ไม่จำเป็นใช้งาน", "หาไม่พบ"};
    String[] _status = {"", "#4CAF50>ใช้งานอยู่", "#FF9800>ชำรุด/เสื่อมสภาพ", "#9E9E9E>ไม่จำเป็นใช้งาน", "#F44336>หาไม่พบ"};

    String[] imgKeys = {"image1", "image2", "image3", "image4"};

    public AdapterItemSearch(Context context, List<ParseObject> objectList)
    {
        this.context = context;
        this.objectList = objectList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static LayoutInflater inflater = null;

    private ViewHolder holder;

    private class ViewHolder
    {
        int imageLatest = 0;
        ImageView img;
        TextView txtName, txtCode, txtPrice, txtStatus;
    }

    @Override
    public int getCount()
    {
        return objectList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ParseObject parseObject = objectList.get(position);
        holder = new ViewHolder();
        View rowView = inflater.inflate(R.layout.items_search, null);
        holder.img = (ImageView) rowView.findViewById(R.id.img);
        holder.txtName = (TextView) rowView.findViewById(R.id.txtName);
        holder.txtCode = (TextView) rowView.findViewById(R.id.txtCode);
        holder.txtPrice = (TextView) rowView.findViewById(R.id.txtPrice);
        holder.txtStatus = (TextView) rowView.findViewById(R.id.txtStatus);

        Font.setFontFace(holder.txtName);
        Font.setFontFace(holder.txtCode);
        Font.setFontFace(holder.txtPrice);
        Font.setFontFace(holder.txtStatus);

        int status_position = 0;
        for (int i=0; i<4; i++)
        {
            if (parseObject.getString("status") != null && parseObject.getString("status").matches(status[i])) status_position = i;
        }

        for (int i=0; i<imgKeys.length; i++) {
            if (parseObject.getParseFile(imgKeys[i]) != null) holder.imageLatest++;
        }
        if (holder.imageLatest>0) Glide.with(context).load(parseObject.getParseFile(imgKeys[holder.imageLatest-1]).getUrl()).skipMemoryCache(false).into(holder.img);

        holder.txtName.setText(Html.fromHtml(((parseObject.getString("list") != null) ? parseObject.getString("list") : "ไม่มีชื่อครุภัณฑ์/font>")));
        holder.txtCode.setText(Html.fromHtml("<font color=#303F9F>รหัสครุภัณฑ์: </font> <font color=#303F9F>" + ((parseObject.getString("code")!=null) ? parseObject.getString("code") : "ไม่มีรหัสครุภัณฑ์</font>")));
//        holder.txtPrice.setText(Html.fromHtml("<font color=#303F9F>ราคา: </font> <font color=#303F9F>" + ((parseObject.getString("price")!=null) ? parseObject.getString("price") : "ไม่มีราคา</font>")));
        holder.txtPrice.setText(Html.fromHtml("<font color=#303F9F>วันที่ตรวจ: </font>" + ((status_position!=0) ? "<font color=#303F9F>" + new MyParseDateConverter().convert(parseObject.getUpdatedAt().toString(), 1) + "</font>" : "ยังไม่ได้ตรวจ")));
        holder.txtStatus.setText(Html.fromHtml("<font color=#303F9F>สภาพของสินทรัพย์: </font> " + ((status_position!=0) ? "<font color=" + _status[status_position]  + "</font>" : "ไม่มีข้อมูล")));

        return rowView;
    }
}
