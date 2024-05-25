package com.dcac.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.models.ParametersItem;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ParametersActivityAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> listTabsTitle;
    private final HashMap<String, List<ParametersItem>> listTabOngletsData;


    public ParametersActivityAdapter(Context context, List<String> listGroupTitles,
                                     HashMap<String, List<ParametersItem>> listChildData) {
        this.context = context;
        this.listTabsTitle = listGroupTitles;
        this.listTabOngletsData = listChildData;
    }

    @Override
    public int getGroupCount() {
        return listTabsTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(this.listTabOngletsData.get(this.listTabsTitle.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listTabsTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(this.listTabOngletsData.get(this.listTabsTitle.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.tab_item_parameters_activity, null); // Remplacer par votre layout de groupe
        }

        String groupTitle = listTabsTitle.get(groupPosition);
        TextView groupTitleTextView = convertView.findViewById(R.id.tabs_title);
        groupTitleTextView.setText(groupTitle);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ParametersItem child = (ParametersItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_parameters_activity, null);
        }

        TextView titleTextView = convertView.findViewById(R.id.element_child_title);
        TextView descriptionTextView = convertView.findViewById(R.id.element_child_description);
        CheckBox checkBox = convertView.findViewById(R.id.elements_child_checkbox);

        titleTextView.setText(child.getTitle());
        descriptionTextView.setText(child.getDescription());
        checkBox.setChecked(child.isChecked());

        checkBox.setOnClickListener(v -> {
            boolean newState = !child.isChecked();
            child.setChecked(newState);

            // Save state in SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("NotificationsEnabled", newState);
            editor.apply();

            Toast.makeText(context, newState ? context.getString(R.string.Notifications_activated) : context.getString(R.string.Notifications_disabled), Toast.LENGTH_SHORT).show();

            notifyDataSetChanged();
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
