package ec.richardnarvaez.chatf.chat.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.Fragments.FragmentChat;
import ec.richardnarvaez.chatf.chat.Fragments.FragmentUsers;



public class TabsPagesAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES =
            new int[] { R.string.frgChat, R.string.frgCall};
    private final Context mContext;

    public TabsPagesAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentChat.newInstance();
            case 1:
                return FragmentUsers.newInstance();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }




}
