package com.example.guardian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingFragment extends Fragment {
    SharedPreferences pref;
    EditText currentPw;
    EditText newPw1;
    EditText newPw2;
    int status = 0;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
        pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        currentPw = rootView.findViewById(R.id.editTextNumberPasswordCurrent);
        newPw1 = rootView.findViewById(R.id.editTextNumberPasswordChange1);
        newPw2 = rootView.findViewById(R.id.editTextNumberPasswordChange2);
        newPw1.setFocusable(false);
        newPw2.setFocusable(false);

        Button check = rootView.findViewById(R.id.buttonNow);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(restoreState().equals(currentPw.getText().toString())){
                    currentPw.setFocusable(false);
                    currentPw.setFocusableInTouchMode(false);
                    Toast.makeText(getContext(), "현재 비밀번호 검증에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    TextView notice = rootView.findViewById(R.id.textViewNotice);
                    notice.setTextColor(Color.parseColor("#0000FF"));
                    notice.setText("비밀번호 검증에 성공하였습니다.");
                    newPw1.setFocusable(true);
                    newPw1.setFocusableInTouchMode(true);
                    newPw1.setHint("");
                    newPw2.setFocusable(true);
                    newPw2.setFocusableInTouchMode(true);
                    newPw2.setHint("");
                    status = 1;
                }
                else{
                    Toast.makeText(getContext(), "현재 비밀번호가 맞지 않습니다. 다시 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button saveButton = rootView.findViewById(R.id.buttonOK);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status == 1) {
                    if (newPw1.getText().toString().equals(newPw2.getText().toString())) {
                        if(newPw1.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "변경할 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            saveState();
                            Toast.makeText(getContext(), "성공적으로 비밀번호를 변경하였습니다.", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    } else {
                        Toast.makeText(getContext(), "변경할 비밀번호와 확인이 맞지 않습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "현재 비밀번호 검증 절차부터 진행해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }
    public void saveState(){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("password", newPw1.getText().toString());
        editor.commit();
    }
    public String restoreState(){
        if((pref!=null)&&(pref.contains("password"))){
            String password = pref.getString("password","");
            return password;
        }
        return "";
    }
}
