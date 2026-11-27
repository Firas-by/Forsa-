package tn.forsa.app.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import tn.forsa.app.R;
import tn.forsa.app.ui.EmployeesActivity;

/**
 * Role: Wires the shared LinearLayout bottom navigation bar without using the Navigation component.
 * Highlights the active tab and opens the corresponding Activity via explicit Intents.
 */
public class BottomNavHelper {

    public static final int TAB_HOME = 0;
    public static final int TAB_OPPORTUNITIES = 1;
    public static final int TAB_ADVISORS = 2;
    public static final int TAB_PLAN = 3;

    public static void setup(final Activity activity, final int activeTab) {
        LinearLayout navHome = activity.findViewById(R.id.navHome);
        LinearLayout navOpportunities = activity.findViewById(R.id.navOpportunities);
        LinearLayout navAdvisors = activity.findViewById(R.id.navAdvisors);
        LinearLayout navPlan = activity.findViewById(R.id.navPlan);

        if (navHome == null || navOpportunities == null || navAdvisors == null || navPlan == null) {
            return;
        }

        ImageView ivHome = activity.findViewById(R.id.ivNavHome);
        ImageView ivOpp = activity.findViewById(R.id.ivNavOpportunities);
        ImageView ivAdv = activity.findViewById(R.id.ivNavAdvisors);
        ImageView ivPlan = activity.findViewById(R.id.ivNavPlan);

        TextView tvHome = activity.findViewById(R.id.tvNavHome);
        TextView tvOpp = activity.findViewById(R.id.tvNavOpportunities);
        TextView tvAdv = activity.findViewById(R.id.tvNavAdvisors);
        TextView tvPlan = activity.findViewById(R.id.tvNavPlan);

        int activeColor = ContextCompat.getColor(activity, R.color.color_primary);
        int inactiveColor = ContextCompat.getColor(activity, R.color.color_text_secondary);

        styleTab(ivHome, tvHome, activeTab == TAB_HOME, activeColor, inactiveColor);
        styleTab(ivOpp, tvOpp, activeTab == TAB_OPPORTUNITIES, activeColor, inactiveColor);
        styleTab(ivAdv, tvAdv, activeTab == TAB_ADVISORS, activeColor, inactiveColor);
        styleTab(ivPlan, tvPlan, activeTab == TAB_PLAN, activeColor, inactiveColor);

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeTab != TAB_HOME) {
                    AnimationHelper.pulseTap(v);
                    openActivityIfPresent(activity, "tn.forsa.app.ui.MainActivity");
                }
            }
        });

        navOpportunities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeTab != TAB_OPPORTUNITIES) {
                    AnimationHelper.pulseTap(v);
                    openActivityIfPresent(activity, "tn.forsa.app.ui.OpportunitiesActivity");
                }
            }
        });

        navAdvisors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeTab != TAB_ADVISORS) {
                    AnimationHelper.pulseTap(v);
                    Intent intent = new Intent(activity, EmployeesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    AnimationHelper.startActivityAnimated(activity, intent);
                }
            }
        });

        navPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeTab != TAB_PLAN) {
                    AnimationHelper.pulseTap(v);
                    openActivityIfPresent(activity, "tn.forsa.app.ui.ActionPlanActivity");
                }
            }
        });
    }

    private static void styleTab(ImageView icon, TextView label, boolean isActive,
                                 int activeColor, int inactiveColor) {
        int color = isActive ? activeColor : inactiveColor;
        icon.setColorFilter(color);
        label.setTextColor(color);
        label.setTypeface(null, isActive ? Typeface.BOLD : Typeface.NORMAL);
    }

    private static void openActivityIfPresent(Activity activity, String className) {
        try {
            Class<?> targetClass = Class.forName(className);
            Intent intent = new Intent(activity, targetClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            AnimationHelper.startActivityAnimated(activity, intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(activity, R.string.nav_coming_soon, Toast.LENGTH_SHORT).show();
        }
    }
}
