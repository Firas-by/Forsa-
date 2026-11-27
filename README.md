# Forsa (فرصة) — Wijhti

**Forsa (فرصة)** is a native Android application (**Java + XML**) designed for Tunisian students finishing their **Licence (BAC+3)**.  
Built on top of the *EmployeeManager* academic architecture, it combines a complete **University Career & Guidance Office Advisor Manager** (full CRUD) with **Wijhti**, an evidence-based orientation assistant across four post-Licence pathways.

> **Current Progress:** Phases 1 & 2 implemented and verified (`assembleDebug` passing). Phases 3–6 are scheduled next.

---

## Tech Stack & Constraints

- **Language & UI:** Java 11 + XML layouts only (no Kotlin, no Jetpack Compose)
- **Views & Binding:** `findViewById()` only (no ViewBinding / DataBinding / Hilt)
- **Lists & Grids:** `ListView` + `GridView` with `BaseAdapter` and `static class ViewHolder` (no `RecyclerView`, no `Navigation` component, no `Fragment`s)
- **Storage:** Static in-memory `ArrayList` collections in [`DataStore.java`](app/src/main/java/tn/forsa/app/data/DataStore.java) (no Room / SQLite / Firebase / Retrofit)
- **SDK:** `minSdk 24`, `targetSdk 35`, `compileSdk 35`
- **Dependencies:** `androidx.appcompat:appcompat:1.7.0`, `com.google.android.material:material:1.12.0`
- **UI Language:** French with friendly Tunisian touches (*"Marhbe bik fi Forsa !"*)

---

## Design & Honesty Principles

- **Tunisian-inspired palette** ([`colors.xml`](app/src/main/res/values/colors.xml)):
  - Sidi Bou Said Blue (`#1E5AA8`) — Primary
  - Tunisian Red (`#D32F2F`) — Accent / Delete
  - Warm Cream (`#FFF8F0`) — Background
  - Jasmine Green (`#2E9E6B`) — Eligible / Open
  - Orange (`#F29D38`) — Potential
  - Cool Grey (`#9AA5B1`) — Insufficient info
- **Transparency commitments** (displayed on the home dashboard):
  1. Aucune recommandation sans source officielle vérifiable.
  2. Aucune équivalence non vérifiée présentée comme un fait.
  3. Aucune opportunité expirée affichée comme ouverte.
  4. Compatibilité avec les critères publiés ≠ probabilité d'admission.

---

## Implemented Screens (Phases 1 & 2)

| Screen | Activity | Layout | Purpose |
| :--- | :--- | :--- | :--- |
| **Accueil (Dashboard)** | [`MainActivity.java`](app/src/main/java/tn/forsa/app/ui/MainActivity.java) | [`activity_main.xml`](app/src/main/res/layout/activity_main.xml) | Welcome banner, live counters (advisors, departments, open opportunities), 2×2 `GridView` for the 4 pathways, quick shortcuts, honesty rules card |
| **Conseillers (Liste)** | [`EmployeesActivity.java`](app/src/main/java/tn/forsa/app/ui/EmployeesActivity.java) | [`activity_employees.xml`](app/src/main/res/layout/activity_employees.xml) | `ListView` of 6+ guidance advisors, live `TextWatcher` search, department filter chips, `+` FAB, edit/delete per row with `AlertDialog.Builder` |
| **Ajouter un conseiller** | [`AddEmployeeActivity.java`](app/src/main/java/tn/forsa/app/ui/AddEmployeeActivity.java) | [`activity_add_employee.xml`](app/src/main/res/layout/activity_add_employee.xml) | Validated creation form with department `Spinner` and `DatePickerDialog` |
| **Modifier un conseiller** | [`EditEmployeeActivity.java`](app/src/main/java/tn/forsa/app/ui/EditEmployeeActivity.java) | [`activity_edit_employee.xml`](app/src/main/res/layout/activity_edit_employee.xml) | Pre-filled form via `Intent` extras, returning `RESULT_OK` via `setResult()` / `ActivityResultLauncher` |
| **Fiche du conseiller** | [`EmployeeDetailsActivity.java`](app/src/main/java/tn/forsa/app/ui/EmployeeDetailsActivity.java) | [`activity_details.xml`](app/src/main/res/layout/activity_details.xml) | Full advisor card with circular initials avatar, contact details, *Modifier* and *Supprimer* (`AlertDialog`) buttons |

---

## Assignment Requirements Mapping

| Requirement | Implementation |
| :--- | :--- |
| `Employee.java` (`Serializable`, private fields, getters/setters) | [`Employee.java`](app/src/main/java/tn/forsa/app/model/Employee.java) |
| `EmployeeAdapter.java` (`BaseAdapter`, `ViewHolder`, `convertView == null`) | [`EmployeeAdapter.java`](app/src/main/java/tn/forsa/app/adapter/EmployeeAdapter.java) + [`item_employee.xml`](app/src/main/res/layout/item_employee.xml) |
| In-memory static `DataStore` with 6 advisors & 4 departments | [`DataStore.java`](app/src/main/java/tn/forsa/app/data/DataStore.java) |
| `EmployeesActivity` (search, filters, FAB, `notifyDataSetChanged()`, `AlertDialog`) | [`EmployeesActivity.java`](app/src/main/java/tn/forsa/app/ui/EmployeesActivity.java) |
| `AddEmployeeActivity` & `EditEmployeeActivity` (`Spinner`, `DatePickerDialog`, `Intent` extras) | [`AddEmployeeActivity.java`](app/src/main/java/tn/forsa/app/ui/AddEmployeeActivity.java), [`EditEmployeeActivity.java`](app/src/main/java/tn/forsa/app/ui/EditEmployeeActivity.java) |
| `EmployeeDetailsActivity` (full profile + edit/delete) | [`EmployeeDetailsActivity.java`](app/src/main/java/tn/forsa/app/ui/EmployeeDetailsActivity.java) |
| `MainActivity` (counters + 4-pathway `GridView` + shortcuts) | [`MainActivity.java`](app/src/main/java/tn/forsa/app/ui/MainActivity.java), [`PathwayAdapter.java`](app/src/main/java/tn/forsa/app/adapter/PathwayAdapter.java), [`NonScrollGridView.java`](app/src/main/java/tn/forsa/app/util/NonScrollGridView.java) |
| Simple `LinearLayout` bottom navigation (no Navigation component) | [`view_bottom_nav.xml`](app/src/main/res/layout/view_bottom_nav.xml) + [`BottomNavHelper.java`](app/src/main/java/tn/forsa/app/util/BottomNavHelper.java) |

---

## Roadmap (Upcoming Phases)

- **Phase 3:** `Student.java` (with official `getMsd()` formula `(0.5 * bac + l1 + l2) / 2.5`) and `PassportActivity.java` (grades `0–20` validation, degree & skills chips).
- **Phase 4:** `Opportunity.java`, `EligibilityResult.java`, `EligibilityEngine.java`, and seeded official opportunities.
- **Phase 5:** `OpportunitiesActivity.java`, `OpportunityAdapter.java`, `OpportunityDetailsActivity.java` (document checklist `X/Y` + *"Parler à un conseiller"* handoff).
- **Phase 6:** `ActionPlanActivity.java` & `ActionPlanBuilder.java` (*"Tes 90 prochains jours"*), empty-state polish, and final documentation.

### Official Sources to Verify (for Phase 4 Seed Data)
- **Bourse du gouvernement tunisien (Circulaire 50/26):** `best.rnu.tn`
- **Mastères professionnels ISET Sfax & ISET Sousse:** Official ISET admission portals
- **Erasmus Mundus:** European Education and Culture Executive Agency (EACEA) catalog
- **CIVP:** ANETI (`emploi.nat.tn`)

---

## Running the Project

1. Clone the repository and open the folder in **Android Studio**.
2. Let Gradle sync (`compileSdk 35`, Java 11).
   > **Windows note:** If the folder path contains non-ASCII characters (such as `فرصة`), [`gradle.properties`](gradle.properties) already includes `android.overridePathCheck=true` so builds succeed normally in Android Studio.
3. Select an emulator (e.g., `Pixel_7`, API 24+) or physical device and click **Run**.
