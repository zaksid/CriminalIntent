package com.bignerdranch.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zaksid on 6/30/15.
 */
public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    public static final String DATE_TIME_FORMAT = "EEE LLLL d, yyyy | h:mm a";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_TIME = "time";
    private static final int REQUEST_TIME = 1;

    private Crime crime;
    private EditText titleField;
    private Button buttonDate;
    private Button buttonTime;
    private Button buttonDateAndTime;
    private CheckBox isSolvedCheckBox;

    public static CrimeFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, id);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void updateDateAndTime(Button button, Date date) {
        button.setText(DateFormat.format(DATE_TIME_FORMAT, date).toString());
    }

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crime = new Crime();
        UUID id = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, parent, false);

        buttonDateAndTime = (Button) view.findViewById(R.id.crime_date_time);
        updateDateAndTime(buttonDateAndTime, (Date)crime.getDate());
        buttonDateAndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });


        isSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        isSolvedCheckBox.setChecked(crime.isSolved());
        isSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setIsSolved(isChecked);
            }
        });

        titleField = (EditText) view.findViewById(R.id.crime_title);
        titleField.setText(crime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_DATE && resultCode == Activity.RESULT_OK) {
            crime.setDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));

            FragmentManager fm = getActivity().getSupportFragmentManager();
            TimePickerFragment dialog = TimePickerFragment.newInstance(crime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
            dialog.show(fm, DIALOG_TIME);

        }

        if (requestCode == REQUEST_TIME) {

            crime.setDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
            updateDateAndTime(buttonDateAndTime, (Date) crime.getDate());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
