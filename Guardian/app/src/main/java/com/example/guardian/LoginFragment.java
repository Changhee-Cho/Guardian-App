package com.example.guardian;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {
    SharedPreferences pref;
    EditText editText;
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);
        pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editText = rootView.findViewById(R.id.editTextNumberPasswordForLogin);
        Button button = rootView.findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(restoreState().equals(editText.getText().toString())){
                    Toast.makeText(getContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

                    // 리스트 프래그먼트로 이동
                    ListFragment listFragment = new ListFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, listFragment)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }


    public String restoreState(){
        if((pref!=null)&&(pref.contains("password"))){
            String password = pref.getString("password","");
            return password;
        }
        return "";
    }
}