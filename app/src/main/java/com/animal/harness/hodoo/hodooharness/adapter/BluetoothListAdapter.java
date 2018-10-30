package com.animal.harness.hodoo.hodooharness.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.domain.BluetoothItem;

import java.util.List;

public class BluetoothListAdapter extends BaseExpandableListAdapter {
    private List<String> mGroupItems;
    private List<List<BluetoothItem>> mChildItems;
    private Context mContext;
    private LayoutInflater mInflater;
    private int[] mLayouts;

    public BluetoothListAdapter(Context context, int[] layouts, List<String> groupItems, List<List<BluetoothItem>> childItems) {
        mContext = context;
        mLayouts = layouts;
        mGroupItems = groupItems;
        mChildItems = childItems;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return mGroupItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildItems.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildItems.get(groupPosition).get(childPosition);
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
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if ( convertView == null ) {
            convertView = mInflater.inflate(mLayouts[0], parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.bt_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if ( holder != null )
            holder.title.setText(mGroupItems.get(groupPosition));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if ( convertView == null ) {
            convertView = mInflater.inflate(mLayouts[1], parent, false);
            holder = new ViewHolder();
            holder.btName = convertView.findViewById(R.id.bt_item);
            holder.stateText = convertView.findViewById(R.id.state_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if ( holder != null ) {
            if ( mChildItems.get(groupPosition) != null && mChildItems.get(groupPosition).size() > 0  ) {
                if ( mChildItems.get(groupPosition).get(childPosition).isState() ) {
                    holder.btName.setTextColor(Color.BLUE);

                } else {
                    holder.btName.setTextColor(Color.parseColor("#666666"));
                }
                holder.btName.setText(mChildItems.get(groupPosition).get(childPosition).getName());
            }
            if ( mChildItems.get(groupPosition).get(childPosition).getStateStr() != null && !mChildItems.get(groupPosition).get(childPosition).getStateStr().equals("") ) {
                holder.stateText.setText(mChildItems.get(groupPosition).get(childPosition).getStateStr());
                holder.stateText.setVisibility(View.VISIBLE);
            } else {
                holder.stateText.setVisibility(View.GONE);
            }

        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public class ViewHolder {
        private TextView title;
        private TextView btName;
        private TextView stateText;
    }
}
