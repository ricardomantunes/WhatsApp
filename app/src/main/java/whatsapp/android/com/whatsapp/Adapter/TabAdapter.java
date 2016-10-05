package whatsapp.android.com.whatsapp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import whatsapp.android.com.whatsapp.fragment.ContactsFragment;
import whatsapp.android.com.whatsapp.fragment.ConversationsFragment;

public class TabAdapter extends FragmentStatePagerAdapter{

    private String[] titleTabs = {"CONVERSATIONS", "CONTACTS"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position)
        {
            case 0:
                fragment = new ConversationsFragment();
                break;
            case 1:
                fragment = new ContactsFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return titleTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titleTabs[position];
    }
}
