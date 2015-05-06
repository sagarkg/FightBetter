package com.ganatragmail.sagar.fightbetter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *  1. Creates/Inflates View for CrimeFragment
 *  2. Set's listeners for EditText(crime title), camera button, dateSelector button, imageView and Checkbox
 *  3. defines NewInstance method for initializing a custom CrimeFragment object with UUID set.
 *  4. Set's up button in the ActionBar and then set's up listener for optionsMenu
 *
 */


public class CrimeFragment extends Fragment {

        public static final String EXTRA_CRIME_ID = "fightbetter.CRIME_ID"; //Used by CrimeList Fragment to send Crime Id of the
                                                                              //  item selected along with the Intent


        private static final int REQUEST_DATE = 0; //Used to start DatePicker Dialog Activity
        private static final int REQUEST_PHOTO = 1; //Used to start Camera Activity
        private static final int REQUEST_CONTACT = 2; //Used to start intent for Phone Contacts

        private static final String DIALOG_DATE = "date";
        private static final String DIALOG_IMAGE = "image";

        private Crime mCrime;
        private EditText mTitleField;
        private Button mDateButton;
        private ImageButton mPhotoButton;
        private ImageView mPhotoView;
        private Button reportButton;
        private Button suspectButton;



    /*  CrimeFragment accesses intent from the PagerActivity using the Bundle.

    */

    //The newInstance method gets the UUID of the new view to be created. This is used instead of creating a custom constructor.
    //Reason being when config changes frag manager destroys and creates a new default fragment reading arg values from the bundle.
    // Thus it's considered good practice to use bundle for creating custom objects and custom constructors
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);


            UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID); //Gets UUID from the bundle
            mCrime = com.ganatragmail.sagar.fightbetter.SingletonCrimeAccessor.get(getActivity()).getCrime(crimeId); // gets crime object from the UUID

        }


        @TargetApi(11)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_crime, container, false);

            // Sets the up button in ActionBar to parent activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                if (NavUtils.getParentActivityName(getActivity())!= null)
                    ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }


            mPhotoButton = (ImageButton) rootView.findViewById(R.id.crime_ImageButton);
            mPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), CameraActivity.class); //Opens CameraActivity on buttonclick
                    startActivityForResult(i, REQUEST_PHOTO);
                }
            });


            //If camera is not available, then disable camera functionality
            PackageManager pm = getActivity().getPackageManager();
            boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)  || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
            && Camera.getNumberOfCameras() >0);

            if(!hasCamera){
                mPhotoButton.setEnabled(false);
            }


            mPhotoView = (ImageView)rootView.findViewById(R.id.crime_ImageView);

            mPhotoView.setOnClickListener(new View.OnClickListener() {  //Set's Imageview listener. Opens photo in a dialog box
                @Override
                public void onClick(View v) {
                    Photo p = mCrime.getPhoto();
                    if (p == null)
                        return;

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
                    ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE); //Opens new instance of ImageFragment with path defined using FragmentManager

                }
            });


            //Setting Title field listener
            mTitleField = (EditText) rootView.findViewById(R.id.crime_titleET);
            mTitleField.setText(mCrime.getTitle());

                    mTitleField.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            mCrime.setTitle(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

            //Set Button to Current Date

            mDateButton = (Button)rootView.findViewById(R.id.crime_dateButton);
           // get date in readable format

            //String readableDate = getDateInFormat(mCrime.getDate());
            updateDate(); //Set's button text to current date

            mDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getActivity()
                            .getSupportFragmentManager();
                    DateAndTimePickerFragment dialog = DateAndTimePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    //similar to startActivity but at a fragment level. Used because both fragment have same parent activity and target fragment's parent is current fragment
                    dialog.show(fm, DIALOG_DATE);

                }
            });




            reportButton = (Button)rootView.findViewById(R.id.crime_reportButton);
            reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                    i = Intent.createChooser(i, getString(R.string.send_report));
                    startActivity(i);
                }
            });

            suspectButton = (Button)rootView.findViewById(R.id.crime_suspectButton);

            if(mCrime.getSuspect()!= null) {
                suspectButton.setText(mCrime.getSuspect());
            }

            suspectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(i, REQUEST_CONTACT);
                }
            });

            return rootView;

        }

        //Sets button text to current crime date
        private void updateDate(){
            mDateButton.setText(mCrime.getDate().toString());
        }

        //For Options Menu
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch(item.getItemId()){
                //Handles up button in ActionBar using NavUtils
                case android.R.id.home:
                    if (NavUtils.getParentActivityName(getActivity()) != null){
                        NavUtils.navigateUpFromSameTask(getActivity());
                    }

                    return true;

                default:
                    return super.onOptionsItemSelected(item);


            }


        }


        private String getCrimeReport(){


            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            String dateString = dateFormat.format(mCrime.getDate());
            String suspect = mCrime.getSuspect();

            if (suspect == null ) {
                suspect = getString(R.string.crime_report_no_suspect);
            }
            else {
                suspect = getString(R.string.crime_report_suspect, suspect);
            }

            String report = getString (R.string.crime_report, mCrime.getTitle(), dateString, suspect);

            return report;
        }

        //Display photo in Image view
        private void showPhoto(){
            //Re set the image button's image based on the photo

            Photo p = mCrime.getPhoto();

            BitmapDrawable b = null;

            if (p!=null) {
                String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
                b = PictureUtils.getScaledDrawable(getActivity(), path);

            }

            mPhotoView.setImageDrawable(b);



        }


        @Override
        public void onStart(){
            super.onStart();
            showPhoto();

        }

        @Override
        public void onPause(){
            super.onPause();
            com.ganatragmail.sagar.fightbetter.SingletonCrimeAccessor.get(getActivity()).saveCrimes();

        }

        @Override
        public void onStop() {
            super.onStop();
            PictureUtils.cleanImageView(mPhotoView);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data){

            if (resultCode != Activity.RESULT_OK)
                return;

            if (requestCode ==  REQUEST_DATE){
                Date date = (Date)data.getSerializableExtra(DateAndTimePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
            }
            else if (requestCode == REQUEST_PHOTO) {

                String filename = data.getStringExtra(CameraFragment.EXTRA_PHOTO_FILENAME);

                if (filename != null){

                    Photo p = new Photo(filename);
                    mCrime.setPhoto(p);
                    showPhoto();
                }
            }
            else if (requestCode == REQUEST_CONTACT) {
                Uri contactsUri = data.getData();

                //Specify which fields you want your query to return values for
                String[] queryFields = new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME
                };

                //Perform your query

                Cursor c = getActivity().getContentResolver().query(
                        contactsUri, queryFields, null, null, null);

                //Double check if you got results
                if (c.getCount() == 0) {
                    c.close();
                    return;
                }

                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                suspectButton.setText(suspect);
                c.close();



            }

        }


    /*  Unused Code
        public String getDateInFormat(Date date){
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String day_of_the_week = sdf.format(date);
            DateFormat df = DateFormat.getDateInstance();
            String main_date = df.format(date);
            String final_date = day_of_the_week + ", " + main_date;
            return final_date;

        }


    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK, null);
    }
    */


}

