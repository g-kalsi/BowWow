package com.codeninjas.bowwow.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.MainActivity;
import com.codeninjas.bowwow.adapters.SettingAdapter;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.interfaces.SettingInterface;


public class SettingsFragment extends BaseFragment implements SettingInterface {

    RecyclerView settingRV;
    SettingAdapter settingAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("Settings", true, true, false);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingAdapter = new SettingAdapter(baseActivity, this);
        settingRV = view.findViewById(R.id.settingRV);
        settingRV.setAdapter(settingAdapter);

    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
               gotofragmenttransstack(new ProfileFrament());
               break;
            case 1:
                gotofragmenttransstack(new MyFeedsFragment());
                break;
            case 2:
                gotofragmenttransstack(new MyReportsFragment());
                break;
            case 3:
                gotofragmenttransstack(new RemindersFragment());
                break;
            case 4:
                gotofragmenttransstack(new NotesFragment());
                break;
            case 5:
                WebPageFragment webPageFragment = new WebPageFragment();
                webPageFragment.title = "Terms & Conditions";
                webPageFragment.url = "https://bowwow-8f417.web.app/terms.html";
                gotofragmenttransstack(webPageFragment);
                break;
            case 6:
                webPageFragment = new WebPageFragment();
                webPageFragment.title = "Privacy Policy";
                webPageFragment.url = "https://bowwow-8f417.web.app/privacypolicy.html";
                gotofragmenttransstack(webPageFragment);
                break;
            case 7:
                webPageFragment = new WebPageFragment();
                webPageFragment.title = "Need Insurance?";
                webPageFragment.url = "https://bowwow-8f417.web.app/insurance.html";
                gotofragmenttransstack(webPageFragment);
                break;
            case 8:
                baseActivity.logout();
                break;
            default:
                gotofragmenttransstack(new ProfileFrament());

        }
    }
}