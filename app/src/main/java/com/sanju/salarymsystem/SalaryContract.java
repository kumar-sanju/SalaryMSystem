package com.sanju.salarymsystem;

import android.provider.BaseColumns;

/**
 * Created by jmprathab on 03/11/15.
 */
public final class SalaryContract {
    public SalaryContract() {
    }

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "details";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_IN_TIME = "in_time";
        public static final String COLUMN_NAME_OUT_TIME = "out_time";
        public static final String COLUMN_NAME_PAY = "pay";
        public static final String COLUMN_NAME_OTPAY = "ot_pay";
    }
}
