package net.sourceforge.bochs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList      = new ArrayList<>();
    private List<String>   mFragmentTitleList = new ArrayList<>();
    private FragmentManager      mManager;

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
        mManager = manager;
    }

    public ViewPagerAdapter(FragmentManager manager, List<Fragment> fragmentList) {
        super(manager);
        mManager      = manager;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void clear() {
        removeAllFragments();
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }


    private void removeAllFragments() {
        if (mFragmentList != null) {
            FragmentTransaction ft = mManager.beginTransaction();
            for (Fragment f : mFragmentList) {
                ft.remove(f);
                ft.commitNow();
            }
        }
    }
}
