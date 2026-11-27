package tn.forsa.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import tn.forsa.app.R;
import tn.forsa.app.data.DataStore;
import tn.forsa.app.model.Employee;

/**
 * Role: Displays the complete profile of one guidance-office advisor with Edit and Delete actions.
 * PDF Requirement: EmployeeDetailsActivity showing full employee details plus Modifier and Supprimer
 * buttons (Supprimer uses AlertDialog confirmation).
 */
public class EmployeeDetailsActivity extends AppCompatActivity {

    private TextView tvDetailsAvatar;
    private TextView tvDetailsFullName;
    private TextView tvDetailsRole;
    private TextView tvDetailsDepartment;
    private TextView tvDetailsEmail;
    private TextView tvDetailsPhone;
    private TextView tvDetailsHireDate;
    private Button btnDetailsEdit;
    private Button btnDetailsDelete;
    private ImageButton btnBackDetails;

    private int employeeId = -1;
    private Employee currentEmployee;

    // Reloads details when returning from EditEmployeeActivity
    private final ActivityResultLauncher<Intent> editLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                loadEmployeeData();
                                setResult(RESULT_OK);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        DataStore.initIfNeeded();
        employeeId = getIntent().getIntExtra(EmployeesActivity.EXTRA_EMPLOYEE_ID, -1);

        bindViews();
        loadEmployeeData();
        setupButtons();
    }

    private void bindViews() {
        tvDetailsAvatar = findViewById(R.id.tvDetailsAvatar);
        tvDetailsFullName = findViewById(R.id.tvDetailsFullName);
        tvDetailsRole = findViewById(R.id.tvDetailsRole);
        tvDetailsDepartment = findViewById(R.id.tvDetailsDepartment);
        tvDetailsEmail = findViewById(R.id.tvDetailsEmail);
        tvDetailsPhone = findViewById(R.id.tvDetailsPhone);
        tvDetailsHireDate = findViewById(R.id.tvDetailsHireDate);
        btnDetailsEdit = findViewById(R.id.btnDetailsEdit);
        btnDetailsDelete = findViewById(R.id.btnDetailsDelete);
        btnBackDetails = findViewById(R.id.btnBackDetails);
    }

    private void loadEmployeeData() {
        currentEmployee = DataStore.findEmployeeById(employeeId);
        if (currentEmployee == null) {
            Toast.makeText(this, R.string.msg_employee_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailsAvatar.setText(currentEmployee.getInitiales());
        tvDetailsFullName.setText(currentEmployee.getNomComplet());
        tvDetailsRole.setText(currentEmployee.getPoste());
        tvDetailsDepartment.setText(currentEmployee.getDepartement());
        tvDetailsEmail.setText(currentEmployee.getEmail());
        tvDetailsPhone.setText(currentEmployee.getTelephone());
        tvDetailsHireDate.setText(currentEmployee.getDateRecrutement());
    }

    private void setupButtons() {
        btnBackDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDetailsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentEmployee != null) {
                    Intent intent = new Intent(EmployeeDetailsActivity.this, EditEmployeeActivity.class);
                    intent.putExtra(EmployeesActivity.EXTRA_EMPLOYEE_ID, currentEmployee.getId());
                    editLauncher.launch(intent);
                }
            }
        });

        btnDetailsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentEmployee != null) {
                    confirmDelete();
                }
            }
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, currentEmployee.getNomComplet()))
                .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataStore.deleteEmployeeById(currentEmployee.getId());
                        Toast.makeText(EmployeeDetailsActivity.this,
                                R.string.msg_employee_deleted, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
