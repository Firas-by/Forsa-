package tn.forsa.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import tn.forsa.app.R;
import tn.forsa.app.model.Employee;

/**
 * Role: Connects the filtered list of advisors (Employees) to the ListView.
 * PDF Requirement: Extends BaseAdapter with getCount(), getItem(), getItemId(), getView(),
 * LayoutInflater called ONLY when convertView == null, and a static ViewHolder class.
 */
public class EmployeeAdapter extends BaseAdapter {

    /**
     * Callback interface so EmployeesActivity handles clicks on rows, edit, and delete icons.
     */
    public interface OnEmployeeActionListener {
        void onRowClick(Employee employee);
        void onEditClick(Employee employee);
        void onDeleteClick(Employee employee);
    }

    private final Context context;
    private final ArrayList<Employee> displayedEmployees;
    private final OnEmployeeActionListener listener;

    public EmployeeAdapter(Context context, ArrayList<Employee> displayedEmployees,
                           OnEmployeeActionListener listener) {
        this.context = context;
        this.displayedEmployees = displayedEmployees;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return displayedEmployees.size();
    }

    @Override
    public Object getItem(int position) {
        return displayedEmployees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return displayedEmployees.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Inflate the row layout ONLY when no recycled view is available
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
            holder = new ViewHolder();
            holder.tvAvatar = convertView.findViewById(R.id.tvAvatar);
            holder.tvFullName = convertView.findViewById(R.id.tvFullName);
            holder.tvRole = convertView.findViewById(R.id.tvRole);
            holder.tvDepartment = convertView.findViewById(R.id.tvDepartment);
            holder.tvPhone = convertView.findViewById(R.id.tvPhone);
            holder.btnEdit = convertView.findViewById(R.id.btnRowEdit);
            holder.btnDelete = convertView.findViewById(R.id.btnRowDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Employee employee = displayedEmployees.get(position);

        holder.tvAvatar.setText(employee.getInitiales());
        holder.tvFullName.setText(employee.getNomComplet());
        holder.tvRole.setText(employee.getPoste());
        holder.tvDepartment.setText(employee.getDepartement());
        holder.tvPhone.setText(employee.getTelephone());

        // Row tap opens EmployeeDetailsActivity
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRowClick(employee);
                }
            }
        });

        // Edit icon tap opens EditEmployeeActivity
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditClick(employee);
                }
            }
        });

        // Delete icon tap asks confirmation via AlertDialog
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(employee);
                }
            }
        });

        return convertView;
    }

    /**
     * Static ViewHolder caches view references to avoid repeated findViewById() calls while scrolling.
     */
    static class ViewHolder {
        TextView tvAvatar;
        TextView tvFullName;
        TextView tvRole;
        TextView tvDepartment;
        TextView tvPhone;
        ImageButton btnEdit;
        ImageButton btnDelete;
    }
}
