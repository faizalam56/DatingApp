package com.across.senzec.datingapp.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.font.FontChangeCrawler;

/**
 * Created by ravi on 15/7/17.
 */

public class FragmentNotificationPopupDialog extends DialogFragment implements View.OnClickListener {

    View view;
    Button btn_damn,btn_like,btn_dislike;
    ImageView iv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification_popup_dialog,container,false);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "gothic.ttf","gothic.ttf");
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        iv = (ImageView) view.findViewById(R.id.iv);
        btn_damn = (Button) view.findViewById(R.id.btn_damn);
        btn_like = (Button) view.findViewById(R.id.btn_like);
        btn_dislike = (Button) view.findViewById(R.id.btn_dislike);


        btn_damn.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_dislike.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_damn:
                break;
            case R.id.btn_like:
                break;
            case R.id.btn_dislike:
                break;
        }
    }
}
