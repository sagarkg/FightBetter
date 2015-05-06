package com.ganatragmail.sagar.fightbetter;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import java.util.ArrayList;
import java.util.UUID;

/**
 *  1. Accepts Intent extras from calling fragment(CrimeListFragment) and then sets the current UUID to calling list item
 *  2. Activity creates ViewPager for hosting CrimeFragment such that each list item can be also scrolled by swiping left and/or right.
 *  3. Implements FragmentStatePagerAdapter
 */

public class PagerActivity extends ActionBarActivity {

    private ViewPager mViewPager;

    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(android.os.Build.VERSION.SDK_INT < 11) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }




        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager); // ViewPager is created directly in the code and not in XML. Thus it's id is defined in the ID's file.
        setContentView(mViewPager);

        mCrimes = com.ganatragmail.sagar.fightbetter.SingletonCrimeAccessor.get(this).getCrimes();


        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {

                UUID crimeId = mCrimes.get(position).getId(); //Gets the crimeId of the new position.

                return CrimeFragment.newInstance(crimeId); // Initializes CrimeFragment by calling calling newInstance method from CrimeFragment.
                                                          // This created CrimeFragment with UUID already set.

            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Crime crime = mCrimes.get(position);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    getSupportActionBar().setSubtitle(crime.getTitle());
                } else {

                    setTitle(crime.getTitle());
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Get Intent serializable and use it to set the current view.
        UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);

                break;
            }

        }


    }

}
