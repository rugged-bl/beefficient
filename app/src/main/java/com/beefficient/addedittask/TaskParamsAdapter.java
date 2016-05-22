package com.beefficient.addedittask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beefficient.R;

import java.util.List;

public class TaskParamsAdapter extends BaseAdapter {

    private List<TaskParam> items;

    public TaskParamsAdapter(List<TaskParam> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public TaskParam getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            Context context = parent.getContext();
            view = LayoutInflater.from(context).inflate(R.layout.item_task_param, parent, false);

            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        TaskParam param = getItem(position);
        holder.name.setText(param.getName());
        holder.icon.setImageResource(param.getIcon());
        holder.text.setText(param.getText());

        return view;
    }

    static class ViewHolder {
        TextView name;
        TextView text;
        ImageView icon;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.task_param_name);
            text = (TextView) view.findViewById(R.id.task_param_text);
            icon = (ImageView) view.findViewById(R.id.task_param_icon);
        }
    }
}
