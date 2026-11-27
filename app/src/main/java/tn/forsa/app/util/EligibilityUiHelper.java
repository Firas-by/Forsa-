package tn.forsa.app.util;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import tn.forsa.app.R;
import tn.forsa.app.engine.EligibilityEngine;
import tn.forsa.app.model.EligibilityResult;
import tn.forsa.app.model.Opportunity;

/**
 * Role: Shared UI helper for formatting eligibility status badges and deadline countdown badges
 * consistently between OpportunityAdapter and OpportunityDetailsActivity.
 */
public class EligibilityUiHelper {

    public static void applyStatusBadge(Context context, TextView badge, EligibilityResult.Status status) {
        int textColor;
        int bgColor;
        int labelRes;

        if (status == EligibilityResult.Status.ELIGIBLE) {
            textColor = ContextCompat.getColor(context, R.color.color_eligible);
            bgColor = ContextCompat.getColor(context, R.color.color_eligible_bg);
            labelRes = R.string.status_eligible;
        } else if (status == EligibilityResult.Status.POTENTIALLY_ELIGIBLE) {
            textColor = ContextCompat.getColor(context, R.color.color_potential);
            bgColor = ContextCompat.getColor(context, R.color.color_potential_bg);
            labelRes = R.string.status_potential;
        } else if (status == EligibilityResult.Status.NOT_ELIGIBLE) {
            textColor = ContextCompat.getColor(context, R.color.color_accent_red);
            bgColor = ContextCompat.getColor(context, R.color.color_accent_red_light);
            labelRes = R.string.status_not_eligible;
        } else {
            textColor = ContextCompat.getColor(context, R.color.color_insufficient);
            bgColor = ContextCompat.getColor(context, R.color.color_insufficient_bg);
            labelRes = R.string.status_insufficient;
        }

        badge.setText(labelRes);
        badge.setTextColor(textColor);
        setRoundedPillColor(badge, bgColor);
    }

    public static void applyDeadlineBadge(Context context, TextView badge, Opportunity opportunity) {
        long daysLeft = EligibilityEngine.getDaysLeft(opportunity.getDeadline());

        if (daysLeft == Long.MAX_VALUE) {
            badge.setText(R.string.deadline_continuous);
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_eligible));
            setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_eligible_bg));
        } else if (daysLeft < 0) {
            badge.setText(R.string.deadline_closed);
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_accent_red));
            setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_accent_red_light));
        } else if (daysLeft <= 14) {
            badge.setText(context.getString(R.string.deadline_urgent_format, daysLeft, opportunity.getDeadline()));
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_accent_red));
            setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_potential_bg));
        } else {
            badge.setText(context.getString(R.string.deadline_open_format, daysLeft, opportunity.getDeadline()));
            badge.setTextColor(ContextCompat.getColor(context, R.color.color_primary));
            setRoundedPillColor(badge, ContextCompat.getColor(context, R.color.color_primary_light));
        }
    }

    public static void setRoundedPillColor(TextView view, int bgColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(24f);
        drawable.setColor(bgColor);
        view.setBackground(drawable);
    }
}
