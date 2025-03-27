package com.example.guardian;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guardian.MainActivity;
import com.example.guardian.SettingFragment;
import com.example.guardian.WriteFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    SharedPreferences pref;
    ViewPager pager;
    EditText pageNumber;
    MyPagerAdapter pagerAdapter;

    int pages;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);
        pageNumber = rootView.findViewById(R.id.pageNumber);
        pager=rootView.findViewById(R.id.pager);

        pagerAdapter = new MyPagerAdapter();
        pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);


        pages = Integer.parseInt(restorePagesState());
        pager = rootView.findViewById(R.id.pager);
        for (int i = 1; i <= pages; i++) {
            setPage("page" + i);
        }


        ImageView zoomin = rootView.findViewById(R.id.zoomin);
        zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!restoreFontSize().isEmpty()) {
                    int size = Integer.parseInt(restoreFontSize());
                    if (size + 5 <= 100) {
                        size += 5;
                        // 모든 페이지의 TextView에 대해 크기 변경
                        for (int i = 0; i < pages; i++) {
                            pagerAdapter.getTextViewAtPosition(i).setTextSize(size);
                        }
                        saveFontState(size);
                    } else {
                        Toast.makeText(getContext(), "최대 사이즈입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ImageView zoomout = rootView.findViewById(R.id.zoomout);
        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!restoreFontSize().isEmpty()) {
                    int size = Integer.parseInt(restoreFontSize());
                    if (size - 5 > 0) {
                        size -= 5;
                        // 모든 페이지의 TextView에 대해 크기 변경
                        for (int i = 0; i < pages; i++) {
                            pagerAdapter.getTextViewAtPosition(i).setTextSize(size);
                        }
                        saveFontState(size);
                    } else {
                        Toast.makeText(getContext(), "최소 사이즈입니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 초기 폰트 크기 설정
                    int initialSize = 10;
                    for (int i = 0; i < pages; i++) {
                        pagerAdapter.getTextViewAtPosition(i).setTextSize(initialSize);
                    }
                    saveFontState(initialSize);
                }
            }
        });

        ImageView smsButton = rootView.findViewById(R.id.smsIcon);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smsText = pref.getString("page"+(pager.getCurrentItem()+1),"");
                // SMS 공유를 위한 Intent 생성
                Intent smsIntent = new Intent(Intent.ACTION_SEND);
                smsIntent.setType("text/plain");
                smsIntent.putExtra(Intent.EXTRA_TEXT, smsText);

                // 공유를 위해 나가는 것은 일시적으로 락을 해제하나, 그 외에는 앱 밖으로 나가면 락을 설정
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.smsStatus = true;

                // SMS 공유 Intent 실행
                startActivity(Intent.createChooser(smsIntent, "Share SMS via"));
            }
        });


        ImageView editButton = rootView.findViewById(R.id.editIcon);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteFragment writeFragment = new WriteFragment();
                pager.setCurrentItem(0);
                pageNumber.setText("1");
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, writeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ImageView settingButton = rootView.findViewById(R.id.settingIcon);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingFragment settingFragment = new SettingFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, settingFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        pageNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int number = Integer.parseInt(pageNumber.getText().toString());
                if (number < 1 || number > pages) {
                    Toast.makeText(getContext(), "1 페이지 ~ " + pages+" 페이지까지만 이동이 가능합니다.", Toast.LENGTH_SHORT).show();
                    pageNumber.setText(String.valueOf(pager.getCurrentItem() + 1));
                } else {
                    pager.setCurrentItem(number - 1);  // number - 1로 설정해야 페이지 번호가 맞음
                }
                return true;
            }
        });

        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                pageNumber.setText(String.valueOf(position + 1));  // 페이지 번호 업데이트
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        pager.setCurrentItem(0);
        pageNumber.setText("1");
        return rootView;
    }
    public String restorePagesState() {
        if ((pref != null) && (pref.contains("pages"))) {
            return pref.getString("pages", "");
        }
        return "1";
    }

    public String restoreFontSize() {
        if ((pref != null) && (pref.contains("font-size"))) {
            return pref.getString("font-size", "10");
        }
        return "10";
    }

    public void saveFontState(int f) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("font-size", String.valueOf(f));
        editor.commit();
    }

    public void setPage(String name) {
        if ((pref != null) && (pref.contains(name))) {
            String content = pref.getString(name, "");
            ScrollView scrollView = new ScrollView(getContext());
            scrollView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
            ));
            scrollView.setPadding(20, 0, 20, 0); //dp단위
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            if (content.isEmpty()) {
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.GRAY);
                textView.setText("저장된 내용이 없습니다. ^^;");
            } else {
                textView.setText(content);
            }

            // 폰트 크기 적용
            textView.setTextSize(Float.parseFloat(restoreFontSize()));

            scrollView.addView(textView);
            pagerAdapter.addItem(scrollView, textView);
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        private ArrayList<View> items = new ArrayList<>();
        private ArrayList<TextView> textViews = new ArrayList<>();

        // 페이지 추가 메서드
        public void addItem(View item, TextView textView) {
            items.add(item);
            textViews.add(textView);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = items.get(position);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        public TextView getTextViewAtPosition(int position) {
            return textViews.get(position);
        }
    }
}
