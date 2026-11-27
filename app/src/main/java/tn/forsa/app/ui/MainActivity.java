package tn.forsa.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import tn.forsa.app.R;
import tn.forsa.app.adapter.PathwayAdapter;
import tn.forsa.app.data.DataStore;
import tn.forsa.app.model.PathwayItem;
import tn.forsa.app.util.BottomNavHelper;

/**
 * Role: Home dashboard of Forsa (فرصة).
 * PDF Requirement: MainActivity displaying the "Marhbe bik" welcome card, live counters
 * (total advisors, number of departments, open opportunities), a GridView with the 4 pathways,
 * quick shortcuts, and the honesty commitments card.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_FILTER = "extra_category_filter";

    private TextView tvCountEmployees;
    private TextView tvCountDepartments;
    private TextView tvCountOpenOpportunities;
    private GridView gvPathways;
    private Button btnOpenPassportHero;
    private Button btnShortcutPassport;
    private Button btnShortcutAdvisors;
    private Button btnShortcutPlan;

    private final ArrayList<PathwayItem> pathways = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataStore.initIfNeeded();

        bindViews();
        setupPathwaysGrid();
        setupShortcuts();
        BottomNavHelper.setup(this, BottomNavHelper.TAB_HOME);
        refreshCounters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCounters();
    }

    private void bindViews() {
        tvCountEmployees = findViewById(R.id.tvCountEmployees);
        tvCountDepartments = findViewById(R.id.tvCountDepartments);
        tvCountOpenOpportunities = findViewById(R.id.tvCountOpenOpportunities);
        gvPathways = findViewById(R.id.gvPathways);
        btnOpenPassportHero = findViewById(R.id.btnOpenPassportHero);
        btnShortcutPassport = findViewById(R.id.btnShortcutPassport);
        btnShortcutAdvisors = findViewById(R.id.btnShortcutAdvisors);
        btnShortcutPlan = findViewById(R.id.btnShortcutPlan);
    }

    private void refreshCounters() {
        tvCountEmployees.setText(String.valueOf(DataStore.employees.size()));
        tvCountDepartments.setText(String.valueOf(DataStore.DEPARTMENTS.length));
        tvCountOpenOpportunities.setText(String.valueOf(DataStore.getOpenOpportunitiesCount()));
    }

    private void setupPathwaysGrid() {
        pathways.clear();
        pathways.add(new PathwayItem(
                getString(R.string.pathway_master_title),
                getString(R.string.pathway_master_sub),
                "MASTER_TN",
                R.color.color_primary
        ));
        pathways.add(new PathwayItem(
                getString(R.string.pathway_abroad_title),
                getString(R.string.pathway_abroad_sub),
                "SCHOLARSHIP_ABROAD",
                R.color.color_potential
        ));
        pathways.add(new PathwayItem(
                getString(R.string.pathway_job_title),
                getString(R.string.pathway_job_sub),
                "JOB",
                R.color.color_eligible
        ));
        pathways.add(new PathwayItem(
                getString(R.string.pathway_freelance_title),
                getString(R.string.pathway_freelance_sub),
                "FREELANCE",
                R.color.color_accent_red
        ));

        PathwayAdapter adapter = new PathwayAdapter(this, pathways);
        gvPathways.setAdapter(adapter);

        gvPathways.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PathwayItem selected = pathways.get(position);
                openActivityWithOptionalCategory("tn.forsa.app.ui.OpportunitiesActivity", selected.getCategoryKey());
            }
        });
    }

    private void setupShortcuts() {
        View.OnClickListener openPassport = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityWithOptionalCategory("tn.forsa.app.ui.PassportActivity", null);
            }
        };

        btnOpenPassportHero.setOnClickListener(openPassport);
        btnShortcutPassport.setOnClickListener(openPassport);

        btnShortcutAdvisors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmployeesActivity.class);
                startActivity(intent);
            }
        });

        btnShortcutPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityWithOptionalCategory("tn.forsa.app.ui.ActionPlanActivity", null);
            }
        });
    }

    /**
     * Safely launches future-phase screens when present, or shows a friendly Toast in early phases.
     */
    private void openActivityWithOptionalCategory(String className, String categoryKey) {
        try {
            Class<?> targetClass = Class.forName(className);
            Intent intent = new Intent(MainActivity.this, targetClass);
            if (categoryKey != null) {
                intent.putExtra(EXTRA_CATEGORY_FILTER, categoryKey);
            }
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(MainActivity.this, R.string.nav_coming_soon, Toast.LENGTH_SHORT).show();
        }
    }
}
