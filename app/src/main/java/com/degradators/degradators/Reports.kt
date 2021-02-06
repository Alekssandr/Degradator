package com.degradators.degradators

import androidx.annotation.StringRes

enum class Reports(
    @StringRes val nameResId: Int
) {
    R_CIVIL(R.string.breaks_my_country_rules),
    SPAM_HARM(R.string.harassment),
    V_ME(R.string.threatening_violence),
    P_ME(R.string.sharing_personal_information),
    HATE(R.string.hate),
    SEXUAL(R.string.involuntary_pornography),
    COPY_ME(R.string.copyright_violation),
    H_ME(R.string.self_harm),
    SPAM_OTHER(R.string.spam),
    MISI(R.string.misinformation),
    T_ME(R.string.sexualization)
}