package com.example.medrait.ui;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


import timber.log.Timber;


public abstract class AbstractBaseFragment
        extends Fragment {

    protected abstract int getLayout();

    @Override
    public void onStart() {
        super.onStart();
        Timber.i("onStart");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(getLayout(), container, false);

        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("onStop");
    }

}