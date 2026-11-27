package tn.forsa.app.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

import tn.forsa.app.R;
import tn.forsa.app.data.DataStore;
import tn.forsa.app.model.Student;

/**
 * Role: Allows the Tunisian Licence (BAC+3) student to fill and edit their Student Passport.
 * Stores data in DataStore.currentStudent, validates grade ranges (0-20), manages skill chips,
 * and computes the live official Ministry MSD formula: (0.5 * Bac + L1 + L2) / 2.5.
 */
public class PassportActivity extends AppCompatActivity {

    private static final String[] DESTINATIONS = {"Tunisie", "Europe"};
    private static final String[] LANGUAGES = {"Français", "Anglais", "Français / Anglais", "Arabe"};
    private static final String[] DEFAULT_SKILLS = {
            "Angular", "FastAPI", "Flutter", "Unity/AR",
            "AI prototypes", "React", "Spring Boot", "Python / Data", "Docker"
    };

    private ImageButton btnBackPassport;
    private TextView tvPassportMsdPreview;
    private EditText etStudentName;
    private EditText etInstitution;
    private EditText etSpecialization;
    private EditText etExactDegree;
    private EditText etBacAverage;
    private EditText etL1Average;
    private EditText etL2Average;
    private EditText etL3Average;
    private EditText etAgeSept2027;
    private EditText etYearsRepeated;
    private CheckBox cbHadInterruption;
    private LinearLayout llSkillChipsRow1;
    private LinearLayout llSkillChipsRow2;
    private EditText etCustomSkill;
    private Button btnAddCustomSkill;
    private CheckBox cbHasDeployedProject;
    private CheckBox cbHasPfe;
    private Spinner spDestination;
    private Spinner spStudyLanguage;
    private CheckBox cbNeedsFunding;
    private Button btnSavePassport;

    private final ArrayList<String> availableSkills = new ArrayList<>();
    private final ArrayList<String> selectedSkills = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);

        DataStore.initIfNeeded();

        bindViews();
        setupSpinners();
        loadStudentData();
        renderSkillChips();
        setupMsdLivePreview();
        setupButtons();
    }

    private void bindViews() {
        btnBackPassport = findViewById(R.id.btnBackPassport);
        tvPassportMsdPreview = findViewById(R.id.tvPassportMsdPreview);
        etStudentName = findViewById(R.id.etStudentName);
        etInstitution = findViewById(R.id.etInstitution);
        etSpecialization = findViewById(R.id.etSpecialization);
        etExactDegree = findViewById(R.id.etExactDegree);
        etBacAverage = findViewById(R.id.etBacAverage);
        etL1Average = findViewById(R.id.etL1Average);
        etL2Average = findViewById(R.id.etL2Average);
        etL3Average = findViewById(R.id.etL3Average);
        etAgeSept2027 = findViewById(R.id.etAgeSept2027);
        etYearsRepeated = findViewById(R.id.etYearsRepeated);
        cbHadInterruption = findViewById(R.id.cbHadInterruption);
        llSkillChipsRow1 = findViewById(R.id.llSkillChipsRow1);
        llSkillChipsRow2 = findViewById(R.id.llSkillChipsRow2);
        etCustomSkill = findViewById(R.id.etCustomSkill);
        btnAddCustomSkill = findViewById(R.id.btnAddCustomSkill);
        cbHasDeployedProject = findViewById(R.id.cbHasDeployedProject);
        cbHasPfe = findViewById(R.id.cbHasPfe);
        spDestination = findViewById(R.id.spDestination);
        spStudyLanguage = findViewById(R.id.spStudyLanguage);
        cbNeedsFunding = findViewById(R.id.cbNeedsFunding);
        btnSavePassport = findViewById(R.id.btnSavePassport);
    }

    private void setupSpinners() {
        ArrayAdapter<String> destAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, DESTINATIONS);
        destAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDestination.setAdapter(destAdapter);

        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, LANGUAGES);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStudyLanguage.setAdapter(langAdapter);
    }

    private void loadStudentData() {
        Student student = DataStore.currentStudent;
        if (student == null) {
            student = new Student();
            DataStore.currentStudent = student;
        }

        etStudentName.setText(student.getName());
        etInstitution.setText(student.getInstitution());
        etSpecialization.setText(student.getSpecialization());
        etExactDegree.setText(student.getExactDegreeTitle());

        if (student.getBacAverage() >= 0) {
            etBacAverage.setText(String.format(Locale.US, "%.2f", student.getBacAverage()));
        }
        if (student.getL1Average() >= 0) {
            etL1Average.setText(String.format(Locale.US, "%.2f", student.getL1Average()));
        }
        if (student.getL2Average() >= 0) {
            etL2Average.setText(String.format(Locale.US, "%.2f", student.getL2Average()));
        }
        if (student.getL3Average() >= 0) {
            etL3Average.setText(String.format(Locale.US, "%.2f", student.getL3Average()));
        }
        if (student.getAgeOnSept2027() > 0) {
            etAgeSept2027.setText(String.valueOf(student.getAgeOnSept2027()));
        }
        etYearsRepeated.setText(String.valueOf(student.getYearsRepeated()));

        cbHadInterruption.setChecked(student.isHadInterruption());
        cbHasDeployedProject.setChecked(student.isHasDeployedProject());
        cbHasPfe.setChecked(student.isHasPfe());
        cbNeedsFunding.setChecked(student.isNeedsFunding());

        selectSpinnerValue(spDestination, DESTINATIONS, student.getPreferredDestination());
        selectSpinnerValue(spStudyLanguage, LANGUAGES, student.getStudyLanguage());

        // Populate available + selected skill chips
        availableSkills.clear();
        for (int i = 0; i < DEFAULT_SKILLS.length; i++) {
            availableSkills.add(DEFAULT_SKILLS[i]);
        }
        selectedSkills.clear();
        ArrayList<String> studentSkills = student.getSkills();
        for (int i = 0; i < studentSkills.size(); i++) {
            String skill = studentSkills.get(i);
            selectedSkills.add(skill);
            if (!containsIgnoreCase(availableSkills, skill)) {
                availableSkills.add(skill);
            }
        }

        updateMsdPreview();
    }

    private void selectSpinnerValue(Spinner spinner, String[] options, String value) {
        if (value == null) {
            return;
        }
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void renderSkillChips() {
        llSkillChipsRow1.removeAllViews();
        llSkillChipsRow2.removeAllViews();

        int half = (availableSkills.size() + 1) / 2;
        for (int i = 0; i < availableSkills.size(); i++) {
            final String skill = availableSkills.get(i);
            TextView chip = createSkillChipView(skill);
            if (i < half) {
                llSkillChipsRow1.addView(chip);
            } else {
                llSkillChipsRow2.addView(chip);
            }
        }
    }

    private TextView createSkillChipView(final String skill) {
        final TextView chip = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dpToPx(34)
        );
        params.setMarginEnd(dpToPx(8));
        chip.setLayoutParams(params);
        chip.setPadding(dpToPx(14), 0, dpToPx(14), 0);
        chip.setGravity(android.view.Gravity.CENTER);
        chip.setTextSize(12);
        chip.setText(skill);

        applyChipStyle(chip, containsIgnoreCase(selectedSkills, skill));

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSkill(skill);
                applyChipStyle(chip, containsIgnoreCase(selectedSkills, skill));
            }
        });

        return chip;
    }

    private void applyChipStyle(TextView chip, boolean selected) {
        if (selected) {
            chip.setBackgroundResource(R.drawable.bg_chip_selected);
            chip.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        } else {
            chip.setBackgroundResource(R.drawable.bg_chip_default);
            chip.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));
        }
    }

    private void toggleSkill(String skill) {
        for (int i = 0; i < selectedSkills.size(); i++) {
            if (selectedSkills.get(i).equalsIgnoreCase(skill)) {
                selectedSkills.remove(i);
                return;
            }
        }
        selectedSkills.add(skill);
    }

    private boolean containsIgnoreCase(ArrayList<String> list, String target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    private void setupMsdLivePreview() {
        TextWatcher msdWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMsdPreview();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etBacAverage.addTextChangedListener(msdWatcher);
        etL1Average.addTextChangedListener(msdWatcher);
        etL2Average.addTextChangedListener(msdWatcher);
    }

    private void updateMsdPreview() {
        Double bac = tryParseGrade(etBacAverage.getText().toString().trim());
        Double l1 = tryParseGrade(etL1Average.getText().toString().trim());
        Double l2 = tryParseGrade(etL2Average.getText().toString().trim());

        if (bac != null && l1 != null && l2 != null) {
            double msd = (0.5 * bac + l1 + l2) / 2.5;
            tvPassportMsdPreview.setText(getString(R.string.passport_msd_value_format, msd));
        } else {
            tvPassportMsdPreview.setText(R.string.passport_msd_missing);
        }
    }

    private Double tryParseGrade(String raw) {
        if (raw.isEmpty()) {
            return null;
        }
        try {
            double val = Double.parseDouble(raw.replace(',', '.'));
            if (val >= 0.0 && val <= 20.0) {
                return val;
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    private void setupButtons() {
        btnBackPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddCustomSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String custom = etCustomSkill.getText().toString().trim();
                if (custom.isEmpty()) {
                    return;
                }
                if (!containsIgnoreCase(availableSkills, custom)) {
                    availableSkills.add(custom);
                }
                if (!containsIgnoreCase(selectedSkills, custom)) {
                    selectedSkills.add(custom);
                }
                etCustomSkill.setText("");
                renderSkillChips();
            }
        });

        btnSavePassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePassport();
            }
        });
    }

    private void savePassport() {
        String name = etStudentName.getText().toString().trim();
        String institution = etInstitution.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();
        String exactDegree = etExactDegree.getText().toString().trim();

        if (name.isEmpty()) {
            etStudentName.setError(getString(R.string.passport_err_name));
            etStudentName.requestFocus();
            return;
        }
        if (institution.isEmpty()) {
            etInstitution.setError(getString(R.string.passport_err_institution));
            etInstitution.requestFocus();
            return;
        }
        if (specialization.isEmpty()) {
            etSpecialization.setError(getString(R.string.passport_err_spec));
            etSpecialization.requestFocus();
            return;
        }

        // Validate 0-20 grades whenever a field is non-empty
        double bac = parseAndValidateOptionalGrade(etBacAverage);
        if (bac == -2.0) return;

        double l1 = parseAndValidateOptionalGrade(etL1Average);
        if (l1 == -2.0) return;

        double l2 = parseAndValidateOptionalGrade(etL2Average);
        if (l2 == -2.0) return;

        double l3 = parseAndValidateOptionalGrade(etL3Average);
        if (l3 == -2.0) return;

        int age = Student.UNSET_AGE;
        String ageText = etAgeSept2027.getText().toString().trim();
        if (!ageText.isEmpty()) {
            try {
                age = Integer.parseInt(ageText);
                if (age < 16 || age > 45) {
                    etAgeSept2027.setError(getString(R.string.passport_err_age_range));
                    etAgeSept2027.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etAgeSept2027.setError(getString(R.string.passport_err_age_range));
                etAgeSept2027.requestFocus();
                return;
            }
        }

        int repeats = 0;
        String repText = etYearsRepeated.getText().toString().trim();
        if (!repText.isEmpty()) {
            try {
                repeats = Integer.parseInt(repText);
                if (repeats < 0 || repeats > 5) {
                    etYearsRepeated.setError(getString(R.string.passport_err_repeats_range));
                    etYearsRepeated.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etYearsRepeated.setError(getString(R.string.passport_err_repeats_range));
                etYearsRepeated.requestFocus();
                return;
            }
        }

        Student student = DataStore.currentStudent;
        student.setName(name);
        student.setInstitution(institution);
        student.setSpecialization(specialization);
        student.setExactDegreeTitle(exactDegree);
        student.setBacAverage(bac);
        student.setL1Average(l1);
        student.setL2Average(l2);
        student.setL3Average(l3);
        student.setAgeOnSept2027(age);
        student.setYearsRepeated(repeats);
        student.setHadInterruption(cbHadInterruption.isChecked());
        student.setSkills(new ArrayList<>(selectedSkills));
        student.setHasDeployedProject(cbHasDeployedProject.isChecked());
        student.setHasPfe(cbHasPfe.isChecked());
        student.setPreferredDestination(spDestination.getSelectedItem().toString());
        student.setStudyLanguage(spStudyLanguage.getSelectedItem().toString());
        student.setNeedsFunding(cbNeedsFunding.isChecked());

        Toast.makeText(this, R.string.passport_msg_saved, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    /**
     * Parses a grade field:
     * - Returns Student.UNSET_GRADE (-1.0) if blank (to support INSUFFICIENT_INFO checks).
     * - Returns -2.0 if invalid or outside [0, 20], setting an error on the EditText.
     */
    private double parseAndValidateOptionalGrade(EditText field) {
        String raw = field.getText().toString().trim();
        if (raw.isEmpty()) {
            return Student.UNSET_GRADE;
        }
        try {
            double val = Double.parseDouble(raw.replace(',', '.'));
            if (val < 0.0 || val > 20.0) {
                field.setError(getString(R.string.passport_err_grade_range));
                field.requestFocus();
                return -2.0;
            }
            return val;
        } catch (NumberFormatException e) {
            field.setError(getString(R.string.passport_err_grade_range));
            field.requestFocus();
            return -2.0;
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
