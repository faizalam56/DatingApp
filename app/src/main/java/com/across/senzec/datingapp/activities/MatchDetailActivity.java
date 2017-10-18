package com.across.senzec.datingapp.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.font.FontChangeCrawler;
import com.across.senzec.datingapp.fragments.FragmentFindMatchDetail;
import com.across.senzec.datingapp.models.User;
import com.squareup.picasso.Picasso;

public class MatchDetailActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView iv_back,iv_logo_icon;
    TextView tv_title;
    Toolbar toolbar;
    TextView tv_profile_name,tv_profile_age,tv_user_distance;
    ImageView iv;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);
        init();
        user = (User) getIntent().getSerializableExtra("user");
        if(savedInstanceState==null){
            FragmentFindMatchDetail fragmentFindMatchDetail = new FragmentFindMatchDetail();
            Bundle bundle = new Bundle();
            bundle.putSerializable("user",user);
            fragmentFindMatchDetail.setArguments(bundle);
            setFragment(fragmentFindMatchDetail,"home");
            mFragmentTransaction.addToBackStack("TAG");
            tv_title.setText(user.getDisplayName());
            iv_logo_icon.setVisibility(View.GONE);
            iv_back.setVisibility(View.VISIBLE);
            iv_back.setImageResource(R.mipmap.back_small);
        }

        iv_back.setOnClickListener(this);
    }
    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_logo_icon=(ImageView)findViewById(R.id.iv_logo_icon);
        tv_title=(TextView) findViewById(R.id.tv_title);
        iv_back=(ImageView)findViewById(R.id.iv_back);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                Fragment home = mFragmentManager.findFragmentByTag("home");
                if(home!=null){
                    if(home.isVisible()){
                        MatchDetailActivity.this.finish();
                    }else{
                        super.onBackPressed();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Fragment home = mFragmentManager.findFragmentByTag("home");
        if(home!=null) {
            if (home.isVisible()) {
                MatchDetailActivity.this.finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void setFragment(Fragment fragment, String tagName) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_replace_, fragment,tagName);
//        mFragmentTransaction.addToBackStack("TAG");
        mFragmentTransaction.commit();

    }

}
