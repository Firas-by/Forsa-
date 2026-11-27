package tn.forsa.app.ui;

import android.app.DatePickerDialog;
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
 * Role: Form Activity used to create and validate a new guidance-office advisor.
 * PDF Requirement: AddEmployeeActivity with full name, email, phone, role, department Spinner,
 * DatePickerDialog for hire date, input validation, and saving to DataStore.
 */
public class AddEmployeeActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etRole;
    private Spinner spDepartment;
    private EditText etHireDate;
    private Button btnCancelAdd;
    private Button btnSaveAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        DataStore.initIfNeeded();
        bindViews();
        setupSpinner();
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
        btnCancelAdd = findViewById(R.id.btnCancelAdd);
        btnSaveAdd = findViewById(R.id.btnSaveAdd);
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

    private void setupDatePicker() {
        etHireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddEmployeeActivity.this,
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
        btnCancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnSaveAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewEmployee();
            }
        });
    }

    private void saveNewEmployee() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String role = etRole.getText().toString().trim();
        String department = spDepartment.getSelectedItem().toString();
        String hireDate = etHireDate.getText().toString().trim();

        // Split full name into prenom (first word) and nom (remaining words)
        String[] parts = fullName.split("\\s+", 2);
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            etFullName.setError(getString(R.string.err_full_name_required));
            etFullName.requestFocus();
            return;
        }
        String prenom = parts[0];
        String nom = parts[1];

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

        Employee newEmployee = new Employee(
                DataStore.getNextEmployeeId(),
                nom,
                prenom,
                email,
                phone,
                role,
                department,
                hireDate
        );

        DataStore.employees.add(newEmployee);
        Toast.makeText(this, R.string.msg_employee_added, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
