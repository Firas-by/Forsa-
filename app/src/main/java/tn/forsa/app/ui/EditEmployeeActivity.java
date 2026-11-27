package tn.forsa.app.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import tn.forsa.app.R;
import tn.forsa.app.data.DataStore;
import tn.forsa.app.model.Employee;

/**
 * Role: Form Activity pre-filled with an existing advisor's data for editing.
 * PDF Requirement: EditEmployeeActivity receives the employee ID via Intent extras,
 * pre-fills the form, validates updates, and returns RESULT_OK via setResult()
 * (using the modern ActivityResultLauncher contract on the caller side instead of the
 * deprecated startActivityForResult(), which gives the same result-returning flow cleanly).
 */
public class EditEmployeeActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etRole;
    private Spinner spDepartment;
    private EditText etHireDate;
    private Button btnCancelEdit;
    private Button btnSaveEdit;

    private Employee currentEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        DataStore.initIfNeeded();

        int employeeId = getIntent().getIntExtra(EmployeesActivity.EXTRA_EMPLOYEE_ID, -1);
        currentEmployee = DataStore.findEmployeeById(employeeId);
        if (currentEmployee == null) {
            Toast.makeText(this, R.string.msg_employee_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        setupSpinner();
        prefillForm();
        setupDatePicker();
        setupButtons();
    }

    private void bindViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etRole = findViewById(R.id.etRole);
        spDepartment = findViewById(R.id.spDepartment);
        etHireDate = findViewById(R.id.etHireDate);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnSaveEdit = findViewById(R.id.btnSaveEdit);
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                DataStore.DEPARTMENTS
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartment.setAdapter(spinnerAdapter);
    }

    private void prefillForm() {
        etFullName.setText(currentEmployee.getNomComplet());
        etEmail.setText(currentEmployee.getEmail());
        etPhone.setText(currentEmployee.getTelephone());
        etRole.setText(currentEmployee.getPoste());
        etHireDate.setText(currentEmployee.getDateRecrutement());

        // Select the matching department in the Spinner using a plain loop
        for (int i = 0; i < DataStore.DEPARTMENTS.length; i++) {
            if (DataStore.DEPARTMENTS[i].equals(currentEmployee.getDepartement())) {
                spDepartment.setSelection(i);
                break;
            }
        }
    }

    private void setupDatePicker() {
        etHireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        EditEmployeeActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int y, int m, int d) {
                                String formatted = String.format(Locale.US, "%02d/%02d/%04d", d, m + 1, y);
                                etHireDate.setText(formatted);
                                etHireDate.setError(null);
                            }
                        },
                        year, month, day
                );
                dialog.show();
            }
        });
    }

    private void setupButtons() {
        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String role = etRole.getText().toString().trim();
        String department = spDepartment.getSelectedItem().toString();
        String hireDate = etHireDate.getText().toString().trim();

        String[] parts = fullName.split("\\s+", 2);
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            etFullName.setError(getString(R.string.err_full_name_required));
            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.err_email_invalid));
            etEmail.requestFocus();
            return;
        }

        String digitsOnly = phone.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 8) {
            etPhone.setError(getString(R.string.err_phone_invalid));
            etPhone.requestFocus();
            return;
        }

        if (role.isEmpty()) {
            etRole.setError(getString(R.string.err_role_required));
            etRole.requestFocus();
            return;
        }

        if (hireDate.isEmpty()) {
            etHireDate.setError(getString(R.string.err_date_required));
            Toast.makeText(this, R.string.err_date_required, Toast.LENGTH_SHORT).show();
            return;
        }

        currentEmployee.setPrenom(parts[0]);
        currentEmployee.setNom(parts[1]);
        currentEmployee.setEmail(email);
        currentEmployee.setTelephone(phone);
        currentEmployee.setPoste(role);
        currentEmployee.setDepartement(department);
        currentEmployee.setDateRecrutement(hireDate);

        DataStore.updateEmployee(currentEmployee);

        Toast.makeText(this, R.string.msg_employee_updated, Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EmployeesActivity.EXTRA_EMPLOYEE_ID, currentEmployee.getId());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
