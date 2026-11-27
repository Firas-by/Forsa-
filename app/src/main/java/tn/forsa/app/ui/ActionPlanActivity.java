package tn.forsa.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import tn.forsa.app.R;
import tn.forsa.app.adapter.ActionPlanAdapter;
import tn.forsa.app.data.DataStore;
import tn.forsa.app.engine.ActionPlanBuilder;
import tn.forsa.app.model.ActionItem;
import tn.forsa.app.util.AnimationHelper;
import tn.forsa.app.util.BottomNavHelper;

/**
 * Role: Displays the personalized 90-day prioritized action plan ("Tes 90 prochains jours").
 * Uses ActionPlanBuilder to compute 3-5 concrete steps from the Student Passport and opportunities.
 */
public class ActionPlanActivity extends AppCompatActivity {

    private TextView tvPlanProgress;
    private Button btnEditPassportFromPlan;
    private ListView lvActionPlan;
    private TextView tvEmptyPlan;

    private final ArrayList<ActionItem> planItems = new ArrayList<>();
    private ActionPlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_plan);

        DataStore.initIfNeeded();

        bindViews();
        setupAdapter();
        setupButtons();
        BottomNavHelper.setup(this, BottomNavHelper.TAB_PLAN);

        refreshPlan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPlan();
    }

    private void bindViews() {
        tvPlanProgress = findViewById(R.id.tvPlanProgress);
        btnEditPassportFromPlan = findViewById(R.id.btnEditPassportFromPlan);
        lvActionPlan = findViewById(R.id.lvActionPlan);
        tvEmptyPlan = findViewById(R.id.tvEmptyPlan);
    }

    private void setupAdapter() {
        adapter = new ActionPlanAdapter(this, planItems, new ActionPlanAdapter.OnActionInteractionListener() {
            @Override
            public void onActionCheckedChanged() {
                updateProgressLabel();
            }

            @Override
            public void onActionRowClicked(ActionItem item) {
                navigateFromAction(item);
            }
        });
        lvActionPlan.setAdapter(adapter);
    }

    private void setupButtons() {
        btnEditPassportFromPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActionPlanActivity.this, PassportActivity.class));
            }
        });
    }

    private void refreshPlan() {
        planItems.clear();
        planItems.addAll(ActionPlanBuilder.buildPlan(DataStore.currentStudent, DataStore.opportunities));
        adapter.notifyDataSetChanged();
        updateProgressLabel();

        if (planItems.isEmpty()) {
            lvActionPlan.setVisibility(View.GONE);
            tvEmptyPlan.setVisibility(View.VISIBLE);
        } else {
            lvActionPlan.setVisibility(View.VISIBLE);
            tvEmptyPlan.setVisibility(View.GONE);
        }
    }

    private void updateProgressLabel() {
        int done = 0;
        for (int i = 0; i < planItems.size(); i++) {
            if (planItems.get(i).isCompleted()) {
                done++;
            }
        }
        tvPlanProgress.setText(getString(R.string.plan_progress_format, done, planItems.size()));
    }

    private void navigateFromAction(ActionItem item) {
        String type = item.getTargetType();
        if (ActionItem.TARGET_PASSPORT.equals(type)) {
            AnimationHelper.startActivityAnimated(this, new Intent(this, PassportActivity.class));
        } else if (ActionItem.TARGET_OPPORTUNITY.equals(type) && item.getRelatedOpportunityId() != -1) {
            Intent intent = new Intent(this, OpportunityDetailsActivity.class);
            intent.putExtra(OpportunitiesActivity.EXTRA_OPPORTUNITY_ID, item.getRelatedOpportunityId());
            AnimationHelper.startActivityAnimated(this, intent);
        } else if (ActionItem.TARGET_ADVISOR.equals(type)) {
            AnimationHelper.startActivityAnimated(this, new Intent(this, EmployeesActivity.class));
        } else {
            AnimationHelper.startActivityAnimated(this, new Intent(this, OpportunitiesActivity.class));
        }
    }
}
