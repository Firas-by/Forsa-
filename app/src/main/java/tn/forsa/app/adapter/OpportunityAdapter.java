package tn.forsa.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tn.forsa.app.R;
import tn.forsa.app.data.DataStore;
import tn.forsa.app.engine.EligibilityEngine;
import tn.forsa.app.model.EligibilityResult;
import tn.forsa.app.model.Opportunity;
import tn.forsa.app.util.EligibilityUiHelper;

/**
 * Role: Connects the filtered list of opportunities to the ListView in OpportunitiesActivity.
 * Uses BaseAdapter + ViewHolder with LayoutInflater called only when convertView == null.
 */
public class OpportunityAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Opportunity> displayedOpportunities;

    public OpportunityAdapter(Context context, ArrayList<Opportunity> displayedOpportunities) {
        this.context = context;
        this.displayedOpportunities = displayedOpportunities;
    }

    @Override
    public int getCount() {
        return displayedOpportunities.size();
    }

    @Override
    public Object getItem(int position) {
        return displayedOpportunities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return displayedOpportunities.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_opportunity, parent, false);
            holder = new ViewHolder();
            holder.tvStatusBadge = convertView.findViewById(R.id.tvOppStatusBadge);
            holder.tvDeadlineBadge = convertView.findViewById(R.id.tvOppDeadlineBadge);
            holder.tvTitle = convertView.findViewById(R.id.tvOppTitle);
            holder.tvInstitution = convertView.findViewById(R.id.tvOppInstitution);
            holder.tvCategory = convertView.findViewById(R.id.tvOppCategory);
            holder.tvCompatibility = convertView.findViewById(R.id.tvOppCompatibility);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Opportunity opportunity = displayedOpportunities.get(position);
        EligibilityResult result = EligibilityEngine.evaluate(DataStore.currentStudent, opportunity);

        holder.tvTitle.setText(opportunity.getTitle());
        holder.tvInstitution.setText(opportunity.getInstitution());
        holder.tvCategory.setText(opportunity.getCategoryLabel());
        holder.tvCompatibility.setText(
                context.getString(R.string.opp_compatibility_label, result.getCompatibilityPercent())
        );

        EligibilityUiHelper.applyStatusBadge(context, holder.tvStatusBadge, result.getStatus());
        EligibilityUiHelper.applyDeadlineBadge(context, holder.tvDeadlineBadge, opportunity);

        return convertView;
    }

    static class ViewHolder {
        TextView tvStatusBadge;
        TextView tvDeadlineBadge;
        TextView tvTitle;
        TextView tvInstitution;
        TextView tvCategory;
        TextView tvCompatibility;
    }
}
