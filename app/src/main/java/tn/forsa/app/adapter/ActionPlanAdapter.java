package tn.forsa.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import tn.forsa.app.R;
import tn.forsa.app.model.ActionItem;
import tn.forsa.app.util.EligibilityUiHelper;

/**
 * Role: Connects the 3-5 prioritized 90-day actions to the ListView in ActionPlanActivity.
 */
public class ActionPlanAdapter extends BaseAdapter {

    public interface OnActionInteractionListener {
        void onActionCheckedChanged();
        void onActionRowClicked(ActionItem item);
    }

    private final Context context;
    private final ArrayList<ActionItem> actions;
    private final OnActionInteractionListener listener;

    public ActionPlanAdapter(Context context, ArrayList<ActionItem> actions,
                             OnActionInteractionListener listener) {
        this.context = context;
        this.actions = actions;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return actions.size();
    }

    @Override
    public Object getItem(int position) {
        return actions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_action_plan, parent, false);
            holder = new ViewHolder();
            holder.cbDone = convertView.findViewById(R.id.cbActionDone);
            holder.tvPriority = convertView.findViewById(R.id.tvActionPriority);
            holder.tvTimeframe = convertView.findViewById(R.id.tvActionTimeframe);
            holder.tvTitle = convertView.findViewById(R.id.tvActionTitle);
            holder.tvDescription = convertView.findViewById(R.id.tvActionDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ActionItem item = actions.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvTimeframe.setText(item.getTimeframe());
        stylePriorityBadge(holder.tvPriority, item.getPriority());

        holder.cbDone.setOnCheckedChangeListener(null);
        holder.cbDone.setChecked(item.isCompleted());
        holder.cbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setCompleted(!item.isCompleted());
                if (listener != null) {
                    listener.onActionCheckedChanged();
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onActionRowClicked(item);
                }
            }
        });

        return convertView;
    }

    private void stylePriorityBadge(TextView badge, int priority) {
        if (priority == 1) {
            badge.setText(R.string.plan_priority_1);
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_accent_red));
            EligibilityUiHelper.setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_accent_red_light));
        } else if (priority == 2) {
            badge.setText(R.string.plan_priority_2);
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_potential));
            EligibilityUiHelper.setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_potential_bg));
        } else {
            badge.setText(R.string.plan_priority_3);
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_eligible));
            EligibilityUiHelper.setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_eligible_bg));
        }
    }

    static class ViewHolder {
        CheckBox cbDone;
        TextView tvPriority;
        TextView tvTimeframe;
        TextView tvTitle;
        TextView tvDescription;
    }
}
