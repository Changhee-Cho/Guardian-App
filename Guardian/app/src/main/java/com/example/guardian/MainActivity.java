package com.example.guardian;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    SharedPreferences pref;
    public boolean smsStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        smsStatus=false;
        if((pref!=null)&&(pref.contains("password"))) {
            // 로그인 프래그먼트로 이동하는 코드 작성
            LoginFragment loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, loginFragment).commit();
        }
        else{
            JoinFragment joinFragment = new JoinFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, joinFragment).commit();
        }

    }
    @Override
    public void onBackPressed() {
        // 기본적으로 뒤로 가기 버튼을 눌렀을 때 백스택에서 pop을 호출하고, 이전 Fragment로 이동
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // 백스택에 엔트리가 있으면, 하나 pop하고 이전 Fragment로 돌아감
            getSupportFragmentManager().popBackStack();
        } else {
            // 백스택이 비어있으면, Activity 종료
            finish();
            super.onBackPressed();
        }
    }
    public void onPause(){
        if(smsStatus==false) {
            if ((pref != null) && (pref.contains("password"))) {
                // 로그인 프래그먼트로 이동하는 코드 작성
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, loginFragment).commit();
            } else {
                JoinFragment joinFragment = new JoinFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, joinFragment).commit();
            }
        }
        else{
            smsStatus=false;
        }
        super.onPause();
    }

}