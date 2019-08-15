package com.juhao.iot;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;

public class MyLoginActivity extends LoginActivity {

    private CheckBox cb_agree;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        setContentView(R.layout.activity_login);
        LinearLayout main=findViewById(R.id.main);
        main.setBackgroundColor(Color.WHITE);
        oauthWidget.setVisibility(View.GONE);

        ((LinearLayout)main.getChildAt(0)).getChildAt(0).setBackground(null);
        LinearLayout child1=((LinearLayout)main.getChildAt(0));
        LinearLayout.LayoutParams temp= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        temp.setMargins(15,15,15,15);
        child1.setLayoutParams(temp);
        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) (main.getChildAt(0)).getLayoutParams();
        layoutParams.topMargin=UIUtils.dip2PX(100);
        View view=View.inflate(this,R.layout.view_line,null);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(1));
        ((LinearLayout)((LinearLayout)main.getChildAt(0)).getChildAt(0)).addView(view,lp);


        View view_bottom=View.inflate(this,R.layout.view_login_bottom,null);
        View viewTemp=new View(this);
        LinearLayout.LayoutParams pl=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
        LinearLayout.LayoutParams pl2=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,UIUtils.dip2PX(50));
        pl.weight=1;
        main.addView(viewTemp,pl);
        main.addView(view_bottom,pl2);
        cb_agree = view_bottom.findViewById(R.id.cb_agree);
        TextView tv_bottom_1=view_bottom.findViewById(R.id.tv_bottom_1);
        TextView tv_bottom_2=view_bottom.findViewById(R.id.tv_bottom_2);
        tv_bottom_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.showSystemStopDialog(MyLoginActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_agree.setChecked(true);
                    }
                });
            }
        });
        tv_bottom_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.showSystemStopDialog(MyLoginActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cb_agree.setChecked(true);
                    }
                });
            }
        });
        Button var3 = (Button)this.findViewById("next");
        var3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_agree.isChecked()){
                  login(view);
                }else {
                    UIUtils.showSystemStopDialog(MyLoginActivity.this, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cb_agree.setChecked(true);
                        }
                    });
                }
            }
        });


//        R.layout.ali_sdk_openaccount_login
    }
}
