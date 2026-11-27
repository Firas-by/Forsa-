package tn.forsa.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import tn.forsa.app.R;
import tn.forsa.app.adapter.OpportunityAdapter;
import tn.forsa.app.data.DataStore;
import tn.forsa.app.engine.EligibilityEngine;
import tn.forsa.app.model.EligibilityResult;
import tn.forsa.app.model.Opportunity;
import tn.forsa.app.util.AnimationHelper;
import tn.forsa.app.util.BottomNavHelper;

/**
 * Role: Displays all evaluated post-Licence opportunities in a ListView with category chips
 * and a "Show only eligible" CheckBox.
 */
public class OpportunitiesActivity extends AppCompatActivity {

    public static final String EXTRA_OPPORTUNITY_ID = "extra_opportunity_id";
    private static final String FILTER_ALL = "ALL";

    private TextView chipOppAll;
    private TextView chipOppMaster;
    private TextView chipOppAbroad;
    private TextView chipOppJob;
    private TextView chipOppFreelance;
    private CheckBox cbOnlyEligible;
    private TextView tvOpportunitiesCount;
    private ListView lvOpportunities;
    private TextView tvEmptyOpportunities;

    private final ArrayList<Opportunity> filteredOpportunities = new ArrayList<>();
    private OpportunityAdapter adapter;
    private String selectedCategory = FILTER_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunities);

        DataStore.initIfNeeded();

        // Optional category filter sent from MainActivity's GridView
        String incomingCategory = getIntent().getStringExtra(MainActivity.EXTRA_CATEGORY_FILTER);
        if (incomingCategory != null && !incomingCategory.trim().isEmpty()) {
            selectedCategory = incomingCategory;
        }

        bindViews();
        setupAdapter();
        setupFilterChips();
        setupCheckBox();
        BottomNavHelper.setup(this, BottomNavHelper.TAB_OPPORTUNITIES);

        applyFilters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-evaluate in case the user edited their Passport
        applyFilters();
    }

    private void bindViews() {
        chipOppAll = findViewById(R.id.chipOppAll);
        chipOppMaster = findViewById(R.id.chipOppMaster);
        chipOppAbroad = findViewById(R.id.chipOppAbroad);
        chipOppJob = findViewById(R.id.chipOppJob);
        chipOppFreelance = findViewById(R.id.chipOppFreelance);
        cbOnlyEligible = findViewById(R.id.cbOnlyEligible);
        tvOpportunitiesCount = findViewById(R.id.tvOpportunitiesCount);
        lvOpportunities = findViewById(R.id.lvOpportunities);
        tvEmptyOpportunities = findViewById(R.id.tvEmptyOpportunities);
    }

    private void setupAdapter() {
        adapter = new OpportunityAdapter(this, filteredOpportunities);
        lvOpportunities.setAdapter(adapter);

        lvOpportunities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AnimationHelper.pulseTap(view);
                Opportunity selected = filteredOpportunities.get(position);
                Intent intent = new Intent(OpportunitiesActivity.this, OpportunityDetailsActivity.class);
                intent.putExtra(EXTRA_OPPORTUNITY_ID, selected.getId());
                AnimationHelper.startActivityAnimated(OpportunitiesActivity.this, intent);
            }
        });
    }

    private void setupFilterChips() {
        bindChipClick(chipOppAll, FILTER_ALL);
        bindChipClick(chipOppMaster, Opportunity.CAT_MASTER_TN);
        bindChipClick(chipOppAbroad, Opportunity.CAT_SCHOLARSHIP_ABROAD);
        bindChipClick(chipOppJob, Opportunity.CAT_JOB);
        bindChipClick(chipOppFreelance, Opportunity.CAT_FREELANCE);
        updateChipStyles();
    }

    private void bindChipClick(TextView chip, final String categoryValue) {
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory = categoryValue;
                updateChipStyles();
                applyFilters();
            }
        });
    }

    private void updateChipStyles() {
        styleSingleChip(chipOppAll, FILTER_ALL.equals(selectedCategory));
        styleSingleChip(chipOppMaster, Opportunity.CAT_MASTER_TN.equals(selectedCategory));
        styleSingleChip(chipOppAbroad, Opportunity.CAT_SCHOLARSHIP_ABROAD.equals(selectedCategory));
        styleSingleChip(chipOppJob, Opportunity.CAT_JOB.equals(selectedCategory));
        styleSingleChip(chipOppFreelance, Opportunity.CAT_FREELANCE.equals(selectedCategory));
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

    private void setupCheckBox() {
        cbOnlyEligible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        filteredOpportunities.clear();
        boolean onlyEligible = cbOnlyEligible.isChecked();

        for (int i = 0; i < DataStore.opportunities.size(); i++) {
            Opportunity opp = DataStore.opportunities.get(i);

            boolean matchesCategory = FILTER_ALL.equals(selectedCategory)
                    || selectedCategory.equals(opp.getCategory());
            if (!matchesCategory) {
                continue;
            }

            if (onlyEligible) {
                EligibilityResult result = EligibilityEngine.evaluate(DataStore.currentStudent, opp);
                if (result.getStatus() != EligibilityResult.Status.ELIGIBLE || EligibilityEngine.isExpired(opp)) {
                    continue;
                }
            }

            filteredOpportunities.add(opp);
        }

        adapter.notifyDataSetChanged();
        tvOpportunitiesCount.setText(getString(R.string.opp_count_format, filteredOpportunities.size()));

        if (filteredOpportunities.isEmpty()) {
            lvOpportunities.setVisibility(View.GONE);
            tvEmptyOpportunities.setVisibility(View.VISIBLE);
        } else {
            lvOpportunities.setVisibility(View.VISIBLE);
            tvEmptyOpportunities.setVisibility(View.GONE);
        }
    }
}
