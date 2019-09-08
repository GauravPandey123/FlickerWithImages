package com.example.medrait.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.example.medrait.R;
import com.pnikosis.materialishprogress.ProgressWheel;


public class ScreenStateManager {

    private final ViewGroup root;
    private final View stateRoot;
    protected ProgressWheel stateProgress;
    protected ImageView stateImage;
    protected TextView stateMessage;

    public ScreenStateManager(ViewGroup root) {
        this.root = root;
        stateRoot = LayoutInflater.from(root.getContext()).inflate(R.layout.template_state, root, false);
        stateProgress = stateRoot.findViewById(R.id.stateProgress);
        stateImage = stateRoot.findViewById(R.id.stateImage);
        stateMessage = stateRoot.findViewById(R.id.stateMessage);
        root.addView(stateRoot);
    }

    public void hideAll() {
        setChildrenVisibility(View.VISIBLE);
        stateRoot.setVisibility(View.GONE);
    }

    public void showLoading() {
        setChildrenVisibility(View.GONE);
        stateRoot.setVisibility(View.VISIBLE);
        stateProgress.setVisibility(View.VISIBLE);
        stateImage.setVisibility(View.GONE);
        stateMessage.setVisibility(View.GONE);
    }

    public void showConnectionError() {
        showMessage(R.drawable.ic_signal_wifi_off_24dp, R.string.connectionErrorMessage);
    }

    public void showError(@StringRes int message) {
        showMessage(R.drawable.ic_error_24dp, message);
    }

    public void showEmpty(@StringRes int message) {
        showMessage(R.drawable.ic_inbox_24dp, message);
    }

    public void showMessage(@DrawableRes int image, @StringRes int message) {
        setChildrenVisibility(View.GONE);
        stateRoot.setVisibility(View.VISIBLE);
        stateProgress.setVisibility(View.GONE);
        stateImage.setVisibility(View.VISIBLE);
        stateImage.setImageResource(image);
        stateMessage.setVisibility(View.VISIBLE);
        stateMessage.setText(message);
    }

    private void setChildrenVisibility(int visibility) {
        for (int i = 0; i < root.getChildCount(); i++) {
            root.getChildAt(i).setVisibility(visibility);
        }
    }

}
