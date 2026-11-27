package tn.forsa.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import tn.forsa.app.R;
import tn.forsa.app.adapter.EmployeeAdapter;
import tn.forsa.app.data.DataStore;
import tn.forsa.app.model.Employee;
import tn.forsa.app.util.BottomNavHelper;

/**
 * Role: Displays the list of guidance-office advisors (Employees) with live search,
 * department filter chips, add FAB, and row edit/delete/details actions.
 * PDF Requirement: EmployeesActivity using ListView + EmployeeAdapter, TextWatcher search,
 * department filters, AlertDialog.Builder delete confirmation, and notifyDataSetChanged().
 */
public class EmployeesActivity extends AppCompatActivity {

    public static final String EXTRA_EMPLOYEE_ID = "extra_employee_id";
    private static final String FILTER_ALL = "ALL";

    private EditText etSearchEmployee;
    private ListView lvEmployees;
    private TextView tvEmptyEmployees;
    private TextView tvEmployeesCount;
    private FloatingActionButton fabAddEmployee;

    private TextView chipAll;
    private TextView chipDeptMaster;
    private TextView chipDeptScholarships;
    private TextView chipDeptJobs;
    private TextView chipDeptEntrepreneurship;

    private final ArrayList<Employee> filteredEmployees = new ArrayList<>();
    private EmployeeAdapter adapter;

    private String currentSearchQuery = "";
    private String selectedDepartment = FILTER_ALL;

    // ActivityResultLauncher refreshes the ListView when Add/Edit/Details returns RESULT_OK
    private final ActivityResultLauncher<Intent> employeeFormLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                applyFilters();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);

        // Initialize in-memory DataStore once
        DataStore.initIfNeeded();

        bindViews();
        setupAdapter();
        setupSearch();
        setupFilterChips();
        setupFab();
        BottomNavHelper.setup(this, BottomNavHelper.TAB_ADVISORS);

        applyFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Keep list synchronized if modified from another screen
        applyFilters();
    }

    private void bindViews() {
        etSearchEmployee = findViewById(R.id.etSearchEmployee);
        lvEmployees = findViewById(R.id.lvEmployees);
        tvEmptyEmployees = findViewById(R.id.tvEmptyEmployees);
        tvEmployeesCount = findViewById(R.id.tvEmployeesCount);
        fabAddEmployee = findViewById(R.id.fabAddEmployee);

        chipAll = findViewById(R.id.chipAll);
        chipDeptMaster = findViewById(R.id.chipDeptMaster);
        chipDeptScholarships = findViewById(R.id.chipDeptScholarships);
        chipDeptJobs = findViewById(R.id.chipDeptJobs);
        chipDeptEntrepreneurship = findViewById(R.id.chipDeptEntrepreneurship);
    }

    private void setupAdapter() {
        adapter = new EmployeeAdapter(this, filteredEmployees,
                new EmployeeAdapter.OnEmployeeActionListener() {
                    @Override
                    public void onRowClick(Employee employee) {
                        Intent intent = new Intent(EmployeesActivity.this, EmployeeDetailsActivity.class);
                        intent.putExtra(EXTRA_EMPLOYEE_ID, employee.getId());
                        employeeFormLauncher.launch(intent);
                    }

                    @Override
                    public void onEditClick(Employee employee) {
                        Intent intent = new Intent(EmployeesActivity.this, EditEmployeeActivity.class);
                        intent.putExtra(EXTRA_EMPLOYEE_ID, employee.getId());
                        employeeFormLauncher.launch(intent);
                    }

                    @Override
                    public void onDeleteClick(Employee employee) {
                        confirmDeleteEmployee(employee);
                    }
                });
        lvEmployees.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s != null ? s.toString().trim().toLowerCase(Locale.ROOT) : "";
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupFilterChips() {
        bindChipClick(chipAll, FILTER_ALL);
        bindChipClick(chipDeptMaster, DataStore.DEPARTMENTS[0]);
        bindChipClick(chipDeptScholarships, DataStore.DEPARTMENTS[1]);
        bindChipClick(chipDeptJobs, DataStore.DEPARTMENTS[2]);
        bindChipClick(chipDeptEntrepreneurship, DataStore.DEPARTMENTS[3]);
        updateChipStyles();
    }

    private void bindChipClick(TextView chip, final String departmentValue) {
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDepartment = departmentValue;
                updateChipStyles();
                applyFilters();
            }
        });
    }

    private void updateChipStyles() {
        styleSingleChip(chipAll, FILTER_ALL.equals(selectedDepartment));
        styleSingleChip(chipDeptMaster, DataStore.DEPARTMENTS[0].equals(selectedDepartment));
        styleSingleChip(chipDeptScholarships, DataStore.DEPARTMENTS[1].equals(selectedDepartment));
        styleSingleChip(chipDeptJobs, DataStore.DEPARTMENTS[2].equals(selectedDepartment));
        styleSingleChip(chipDeptEntrepreneurship, DataStore.DEPARTMENTS[3].equals(selectedDepartment));
    }

    private void styleSingleChip(TextView chip, boolean isSelected) {
        if (isSelected) {
            chip.setBackgroundResource(R.drawable.bg_chip_selected);
            chip.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        } else {
            chip.setBackgroundResource(R.drawable.bg_chip_default);
            chip.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));
        }
    }

    private void setupFab() {
        fabAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeesActivity.this, AddEmployeeActivity.class);
                employeeFormLauncher.launch(intent);
            }
        });
    }

    /**
     * Filters DataStore.employees using plain loops and refreshes the ListView.
     */
    private void applyFilters() {
        filteredEmployees.clear();

        for (int i = 0; i < DataStore.employees.size(); i++) {
            Employee emp = DataStore.employees.get(i);

            // Check department filter
            boolean matchesDept = FILTER_ALL.equals(selectedDepartment)
                    || selectedDepartment.equals(emp.getDepartement());

            // Check search text filter
            boolean matchesSearch = true;
            if (!currentSearchQuery.isEmpty()) {
                String fullName = emp.getNomComplet().toLowerCase(Locale.ROOT);
                String role = emp.getPoste() != null ? emp.getPoste().toLowerCase(Locale.ROOT) : "";
                String email = emp.getEmail() != null ? emp.getEmail().toLowerCase(Locale.ROOT) : "";
                String dept = emp.getDepartement() != null ? emp.getDepartement().toLowerCase(Locale.ROOT) : "";

                matchesSearch = fullName.contains(currentSearchQuery)
                        || role.contains(currentSearchQuery)
                        || email.contains(currentSearchQuery)
                        || dept.contains(currentSearchQuery);
            }

            if (matchesDept && matchesSearch) {
                filteredEmployees.add(emp);
            }
        }

        // Notify the BaseAdapter that underlying data changed
        adapter.notifyDataSetChanged();

        tvEmployeesCount.setText(getString(R.string.employees_count_format, filteredEmployees.size()));
        if (filteredEmployees.isEmpty()) {
            lvEmployees.setVisibility(View.GONE);
            tvEmptyEmployees.setVisibility(View.VISIBLE);
        } else {
            lvEmployees.setVisibility(View.VISIBLE);
            tvEmptyEmployees.setVisibility(View.GONE);
        }
    }

    /**
     * Shows an AlertDialog confirmation before deleting an advisor.
     */
    private void confirmDeleteEmployee(final Employee employee) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, employee.getNomComplet()))
                .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataStore.deleteEmployeeById(employee.getId());
                        applyFilters();
                        Toast.makeText(EmployeesActivity.this,
                                R.string.msg_employee_deleted, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
