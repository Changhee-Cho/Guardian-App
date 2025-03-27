package com.example.guardian;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JoinFragment extends Fragment {
    SharedPreferences pref;
    EditText tv1;
    EditText tv2;

    public JoinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_join, container, false);

        pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);

        tv1 = rootView.findViewById(R.id.editTextNumberPassword);
        tv2 = rootView.findViewById(R.id.editTextReNumberPassword);

        Button button = rootView.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비밀번호 확인 로직
                if(tv1.getText().toString().equals(tv2.getText().toString())){
                    if(tv1.getText().toString().equals("")){
                        Toast.makeText(getActivity(), "설정할 비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        saveState();  // 상태 저장
                        saveFontState(20);
                        Toast.makeText(getContext(), "성공적으로 비밀번호를 등록했습니다. 로그인 해 주세요.", Toast.LENGTH_LONG).show();

                        // 로그인 프래그먼트로 이동
                        LoginFragment loginFragment = new LoginFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, loginFragment)
                                .commit();
                    }
                } else {
                    Toast.makeText(getContext(), "비밀번호가 서로 맞지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    // SharedPreferences에 비밀번호 저장
    public void saveState(){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("password", tv1.getText().toString());
        editor.putString("pages", "1");
        editor.putString("page1","");
        editor.commit();
    }
    public void saveFontState(int f){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("font-size", f+"");
        editor.commit();
    }
}
