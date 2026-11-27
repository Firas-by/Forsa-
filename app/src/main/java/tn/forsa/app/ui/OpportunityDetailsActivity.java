package tn.forsa.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import tn.forsa.app.R;
import tn.forsa.app.adapter.DocumentCheckAdapter;
import tn.forsa.app.data.DataStore;
import tn.forsa.app.engine.EligibilityEngine;
import tn.forsa.app.model.DocumentItem;
import tn.forsa.app.model.EligibilityResult;
import tn.forsa.app.model.Employee;
import tn.forsa.app.model.Opportunity;
import tn.forsa.app.util.EligibilityUiHelper;

/**
 * Role: Displays full opportunity evaluation details: formal eligibility reasons,
 * separate personal fit notes, deadline countdown, funding, official source,
 * interactive document checklist (with X/Y counter), and advisor handoff button.
 */
public class OpportunityDetailsActivity extends AppCompatActivity {

    private ImageButton btnBackOppDetails;
    private TextView tvDetailStatusBadge;
    private TextView tvDetailDeadlineBadge;
    private TextView tvDetailTitle;
    private TextView tvDetailInstitution;
    private TextView tvDetailMeta;
    private TextView tvDetailCompatibility;
    private TextView tvDetailFormalReasons;
    private TextView tvDetailPersonalFit;
    private TextView tvDocsCounter;
    private ListView lvDocuments;
    private TextView tvDetailDeadlineText;
    private TextView tvDetailFunding;
    private TextView tvDetailSourceUrl;
    private TextView tvDetailVerified;
    private TextView tvDetailNotes;
    private Button btnTalkToAdvisor;

    private Opportunity opportunity;
    private DocumentCheckAdapter docAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_details);

        DataStore.initIfNeeded();

        int oppId = getIntent().getIntExtra(OpportunitiesActivity.EXTRA_OPPORTUNITY_ID, -1);
        opportunity = DataStore.findOpportunityById(oppId);
        if (opportunity == null) {
            Toast.makeText(this, R.string.opp_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        populateDetails();
        setupDocumentChecklist();
        setupButtons();
    }

    private void bindViews() {
        btnBackOppDetails = findViewById(R.id.btnBackOppDetails);
        tvDetailStatusBadge = findViewById(R.id.tvDetailStatusBadge);
        tvDetailDeadlineBadge = findViewById(R.id.tvDetailDeadlineBadge);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailInstitution = findViewById(R.id.tvDetailInstitution);
        tvDetailMeta = findViewById(R.id.tvDetailMeta);
        tvDetailCompatibility = findViewById(R.id.tvDetailCompatibility);
        tvDetailFormalReasons = findViewById(R.id.tvDetailFormalReasons);
        tvDetailPersonalFit = findViewById(R.id.tvDetailPersonalFit);
        tvDocsCounter = findViewById(R.id.tvDocsCounter);
        lvDocuments = findViewById(R.id.lvDocuments);
        tvDetailDeadlineText = findViewById(R.id.tvDetailDeadlineText);
        tvDetailFunding = findViewById(R.id.tvDetailFunding);
        tvDetailSourceUrl = findViewById(R.id.tvDetailSourceUrl);
        tvDetailVerified = findViewById(R.id.tvDetailVerified);
        tvDetailNotes = findViewById(R.id.tvDetailNotes);
        btnTalkToAdvisor = findViewById(R.id.btnTalkToAdvisor);
    }

    private void populateDetails() {
        EligibilityResult result = EligibilityEngine.evaluate(DataStore.currentStudent, opportunity);

        EligibilityUiHelper.applyStatusBadge(this, tvDetailStatusBadge, result.getStatus());
        EligibilityUiHelper.applyDeadlineBadge(this, tvDetailDeadlineBadge, opportunity);

        tvDetailTitle.setText(opportunity.getTitle());
        tvDetailInstitution.setText(opportunity.getInstitution());
        tvDetailMeta.setText(opportunity.getCategoryLabel() + " • Année " + opportunity.getAcademicYear());
        tvDetailCompatibility.setText(
                getString(R.string.opp_compatibility_label, result.getCompatibilityPercent())
        );

        tvDetailFormalReasons.setText(formatBulletLines(result.getReasons()));
        tvDetailPersonalFit.setText(formatBulletLines(result.getPersonalFitNotes()));

        long daysLeft = EligibilityEngine.getDaysLeft(opportunity.getDeadline());
        if (daysLeft == Long.MAX_VALUE) {
            tvDetailDeadlineText.setText(getString(R.string.deadline_continuous));
        } else if (daysLeft < 0) {
            tvDetailDeadlineText.setText(getString(R.string.deadline_closed) + " (" + opportunity.getDeadline() + ")");
        } else if (daysLeft <= 14) {
            tvDetailDeadlineText.setText(getString(R.string.deadline_urgent_format, daysLeft, opportunity.getDeadline()));
        } else {
            tvDetailDeadlineText.setText(getString(R.string.deadline_open_format, daysLeft, opportunity.getDeadline()));
        }

        tvDetailFunding.setText(opportunity.getFundingInfo());
        tvDetailSourceUrl.setText(opportunity.getOfficialSourceUrl());
        tvDetailVerified.setText(opportunity.getLastVerified());
        tvDetailNotes.setText(opportunity.getNotes());
    }

    private void setupDocumentChecklist() {
        docAdapter = new DocumentCheckAdapter(
                this,
                opportunity.getDocumentItems(),
                new DocumentCheckAdapter.OnDocumentChangedListener() {
                    @Override
                    public void onDocumentStatusChanged() {
                        updateDocsCounter();
                    }
                }
        );
        lvDocuments.setAdapter(docAdapter);
        updateDocsCounter();
    }

    private void updateDocsCounter() {
        ArrayList<DocumentItem> docs = opportunity.getDocumentItems();
        int ready = 0;
        for (int i = 0; i < docs.size(); i++) {
            if (docs.get(i).isAvailable()) {
                ready++;
            }
        }
        tvDocsCounter.setText(getString(R.string.opp_docs_counter_format, ready, docs.size()));
    }

    private void setupButtons() {
        btnBackOppDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvDetailSourceUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = opportunity.getOfficialSourceUrl();
                if (url == null || url.trim().isEmpty()) {
                    return;
                }
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(OpportunityDetailsActivity.this,
                            R.string.opp_source_open_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnTalkToAdvisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetDept = opportunity.getMatchingAdvisorDepartment();
                Employee advisor = DataStore.findAdvisorByDepartment(targetDept);
                if (advisor == null) {
                    Toast.makeText(OpportunityDetailsActivity.this,
                            R.string.opp_no_advisor, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(OpportunityDetailsActivity.this, EmployeeDetailsActivity.class);
                intent.putExtra(EmployeesActivity.EXTRA_EMPLOYEE_ID, advisor.getId());
                startActivity(intent);
            }
        });
    }

    private String formatBulletLines(ArrayList<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return "• Aucun détail complémentaire.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append("• ").append(lines.get(i));
        }
        return sb.toString();
    }
}
