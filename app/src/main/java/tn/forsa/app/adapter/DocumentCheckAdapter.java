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
import tn.forsa.app.model.DocumentItem;
import tn.forsa.app.util.EligibilityUiHelper;

/**
 * Role: Displays the document checklist inside OpportunityDetailsActivity.
 * Each row shows a CheckBox, document name, and status badge
 * (Disponible / Manquant / Seulement après présélection).
 */
public class DocumentCheckAdapter extends BaseAdapter {

    public interface OnDocumentChangedListener {
        void onDocumentStatusChanged();
    }

    private final Context context;
    private final ArrayList<DocumentItem> documents;
    private final OnDocumentChangedListener listener;

    public DocumentCheckAdapter(Context context, ArrayList<DocumentItem> documents,
                                OnDocumentChangedListener listener) {
        this.context = context;
        this.documents = documents;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return documents.size();
    }

    @Override
    public Object getItem(int position) {
        return documents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_document_check, parent, false);
            holder = new ViewHolder();
            holder.cbDocReady = convertView.findViewById(R.id.cbDocReady);
            holder.tvDocName = convertView.findViewById(R.id.tvDocName);
            holder.tvDocStatusBadge = convertView.findViewById(R.id.tvDocStatusBadge);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DocumentItem doc = documents.get(position);

        holder.tvDocName.setText(doc.getName());
        holder.cbDocReady.setOnCheckedChangeListener(null);
        holder.cbDocReady.setChecked(doc.isAvailable());

        styleDocStatusBadge(holder.tvDocStatusBadge, doc.getStatus());

        // Tapping the CheckBox toggles Disponible <-> Manquant
        holder.cbDocReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doc.isAvailable()) {
                    doc.setStatus(DocumentItem.STATUS_MISSING);
                } else {
                    doc.setStatus(DocumentItem.STATUS_AVAILABLE);
                }
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onDocumentStatusChanged();
                }
            }
        });

        // Tapping the row or status badge cycles through all 3 statuses
        View.OnClickListener cycleListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doc.cycleStatus();
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onDocumentStatusChanged();
                }
            }
        };
        convertView.setOnClickListener(cycleListener);
        holder.tvDocStatusBadge.setOnClickListener(cycleListener);

        return convertView;
    }

    private void styleDocStatusBadge(TextView badge, String status) {
        badge.setText(status);
        if (DocumentItem.STATUS_AVAILABLE.equals(status)) {
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_eligible));
            EligibilityUiHelper.setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_eligible_bg));
        } else if (DocumentItem.STATUS_AFTER_PRESELECTION.equals(status)) {
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_potential));
            EligibilityUiHelper.setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_potential_bg));
        } else {
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_accent_red));
            EligibilityUiHelper.setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_accent_red_light));
        }
    }

    static class ViewHolder {
        CheckBox cbDocReady;
        TextView tvDocName;
        TextView tvDocStatusBadge;
    }
}
