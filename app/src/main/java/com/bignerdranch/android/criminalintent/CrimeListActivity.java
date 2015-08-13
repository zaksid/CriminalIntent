package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by zaksid on 7/1/15.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
