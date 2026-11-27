package tn.forsa.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import tn.forsa.app.R;
import tn.forsa.app.model.PathwayItem;

/**
 * Role: Connects the 4 student pathways to the GridView on MainActivity.
 * Follows the same BaseAdapter + static ViewHolder pattern as EmployeeAdapter.
 */
public class PathwayAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<PathwayItem> pathways;

    public PathwayAdapter(Context context, ArrayList<PathwayItem> pathways) {
        this.context = context;
        this.pathways = pathways;
    }

    @Override
    public int getCount() {
        return pathways.size();
    }

    @Override
    public Object getItem(int position) {
        return pathways.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pathway_card, parent, false);
            holder = new ViewHolder();
            holder.viewAccent = convertView.findViewById(R.id.viewPathwayAccent);
            holder.tvTitle = convertView.findViewById(R.id.tvPathwayTitle);
            holder.tvSubtitle = convertView.findViewById(R.id.tvPathwaySubtitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PathwayItem item = pathways.get(position);
        holder.viewAccent.setBackgroundColor(ContextCompat.getColor(context, item.getAccentColorRes()));
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());

        return convertView;
    }

    static class ViewHolder {
        View viewAccent;
        TextView tvTitle;
        TextView tvSubtitle;
    }
}
