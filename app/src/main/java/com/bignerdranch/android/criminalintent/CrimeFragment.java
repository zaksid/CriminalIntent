package com.bignerdranch.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by zaksid on 6/30/15.
 * Provides displaying single crime info in fragment
 */
public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    public static final String DATE_TIME_FORMAT = "EEE LLLL d, yyyy | h:mm a";

    private static final String LOG_TAG = "CrimeFragment";

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;

    private static final String DIALOG_TIME = "time";
    private static final int REQUEST_TIME = 1;

    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_CONTACT = 3;

    private static final String DIALOG_IMAGE = "image";

    private Crime crime;
    private Button buttonDateAndTime;
    private Button chooseSuspectButton;
    private ImageView photoView;

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

    private void showPhoto() {
        Photo photo = crime.getPhoto();
        BitmapDrawable drawable = null;
        if (photo != null) {
            String path = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
            drawable = PictureUtils.getScaledDrawable(getActivity(), path);
        }

        photoView.setImageDrawable(drawable);
    }

    private void deletePhoto() {
        Photo photo = crime.getPhoto();
        if (photo == null)
            return;
        String path = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
        File file = new File(path);
        boolean deleted = file.delete();
        if (!deleted) {
            Log.e(LOG_TAG, "Photo was not deleted");
            return;
        }
        crime.deletePhoto();
        PictureUtils.cleanImageView(photoView);
    }

    private String getCrimeReport() {
        String isSolved;
        if (crime.isSolved()) {
            isSolved = getString(R.string.crime_report_solved);
        } else {
            isSolved = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String date = DateFormat.format(dateFormat, crime.getDate()).toString();

        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report, crime.getTitle(), date, isSolved, suspect);
    }

    @SuppressWarnings("ConstantConditions")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(photoView);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, parent, false);

        buttonDateAndTime = (Button) view.findViewById(R.id.crime_date_time);
        updateDateAndTime(buttonDateAndTime, crime.getDate());
        buttonDateAndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        ImageButton photoButton = (ImageButton) view.findViewById(R.id.crime_imageButton);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });

        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            photoButton.setEnabled(false);
        }

        photoView = (ImageView) view.findViewById(R.id.crime_imageView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Photo photo = crime.getPhoto();
                if (photo == null)
                    return;

                FragmentManager manager = getActivity().getSupportFragmentManager();
                String path = getActivity()
                        .getFileStreamPath(photo.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(manager, DIALOG_IMAGE);
            }
        });

        CheckBox isSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        isSolvedCheckBox.setChecked(crime.isSolved());
        isSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setIsSolved(isChecked);
            }
        });

        EditText titleField = (EditText) view.findViewById(R.id.crime_title);
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

        Button sendReportButton = (Button) view.findViewById(R.id.crime_sendReportButton);
        sendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                // do not set default for action (always ask)
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        chooseSuspectButton = (Button) view.findViewById(R.id.crime_suspectButton);
        chooseSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACT);
            }
        });

        if (crime.getSuspect() != null) {
            chooseSuspectButton.setText(crime.getSuspect());
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_DATE) {
            crime.setDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));

            FragmentManager fm = getActivity().getSupportFragmentManager();
            TimePickerFragment dialog = TimePickerFragment.newInstance(crime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
            dialog.show(fm, DIALOG_TIME);
        }

        if (requestCode == REQUEST_TIME) {
            crime.setDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
            updateDateAndTime(buttonDateAndTime, crime.getDate());
        }

        if (requestCode == REQUEST_PHOTO) {
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                deletePhoto();
                Photo photo = new Photo(filename);
                crime.setPhoto(photo);
                showPhoto();
            }
        }

        if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] queryFilds = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor cursor = getActivity().getContentResolver()
                    .query(contactUri, queryFilds, null, null, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                return;
            }

            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            crime.setSuspect(suspect);
            chooseSuspectButton.setText(suspect);
            cursor.close();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            case R.id.menu_item_delete_crime:
                CrimeLab crimeLab = CrimeLab.get(getActivity());
                crimeLab.deleteCrime(crime);
                crimeLab.saveCrimes();
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
