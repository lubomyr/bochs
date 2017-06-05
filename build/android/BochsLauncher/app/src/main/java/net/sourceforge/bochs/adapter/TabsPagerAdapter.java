package net.sourceforge.bochs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.sourceforge.bochs.HardwareTabFragment;
import net.sourceforge.bochs.MiscTabFragment;
import net.sourceforge.bochs.StorageTabFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Storage tab fragment activity
                return new StorageTabFragment();
            case 1:
                // Hardware tab fragment activity
                return new HardwareTabFragment();
            case 2:
                // Misc tab fragment activity
                return new MiscTabFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
