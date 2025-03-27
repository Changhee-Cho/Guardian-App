package com.example.guardian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WriteFragment extends Fragment {
    SharedPreferences pref;
    EditText pageNumber;
    ViewPager pager;
    MyPagerAdapter pagerAdapter;
    int pages;

    public WriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_write, container, false);
        pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        pager = rootView.findViewById(R.id.pagerWrite);
        pagerAdapter = new MyPagerAdapter();
        pageNumber = rootView.findViewById(R.id.currentPage);
        pages = Integer.parseInt(restorePagesState());

        for (int i = 1; i <= pages; i++) {
            setPage("page" + i);
        }


        // Zoom In 버튼 클릭 리스너
        ImageView zoomin = rootView.findViewById(R.id.zoomin);
        zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!restoreFontSize().isEmpty()) {
                    int size = Integer.parseInt(restoreFontSize());
                    if (size + 5 <= 100) {
                        size += 5;
                        // 모든 페이지의 EditText에 대해 크기 변경
                        for (int i = 0; i < pages; i++) {
                            pagerAdapter.getEditTextAtPosition(i).setTextSize(size);
                        }
                        saveFontState(size);
                    } else {
                        Toast.makeText(getContext(), "최대 사이즈입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Zoom Out 버튼 클릭 리스너
        ImageView zoomout = rootView.findViewById(R.id.zoomout);
        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!restoreFontSize().isEmpty()) {
                    int size = Integer.parseInt(restoreFontSize());
                    if (size - 5 > 0) {
                        size -= 5;
                        // 모든 페이지의 EditText에 대해 크기 변경
                        for (int i = 0; i < pages; i++) {
                            pagerAdapter.getEditTextAtPosition(i).setTextSize(size);
                        }
                        saveFontState(size);
                    } else {
                        Toast.makeText(getContext(), "최소 사이즈입니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 초기 폰트 크기 설정
                    int initialSize = 10;
                    for (int i = 0; i < pages; i++) {
                        pagerAdapter.getEditTextAtPosition(i).setTextSize(initialSize);
                    }
                    saveFontState(initialSize);
                }
            }
        });

        ImageView saveIcon = rootView.findViewById(R.id.save);
        saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_message();
            }
        });

        ImageView clearIcon = rootView.findViewById(R.id.clear);
        clearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_message();
            }
        });

        ImageView plusIcon = rootView.findViewById(R.id.plus);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("안내");
                builder.setMessage("페이지를 추가하시겠습니까?\n마지막 장에 페이지가 추가됩니다.");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        pages += 1;  // 페이지 수 증가

                        setPage("page" + pages);  // 새로운 페이지 추가
                        savePages();  // 페이지 수 저장

                        // 어댑터 갱신
                        pagerAdapter.notifyDataSetChanged();// 추가된 페이지를 ViewPager에 반영

                        // 새로 추가된 페이지로 이동
                        pager.setCurrentItem(pages - 1, true);  // 페이지 번호는 0부터 시작하므로, pages - 1로 설정

                        Toast.makeText(getContext(), "페이지가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("아니오", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ImageView minusIcon = rootView.findViewById(R.id.minus);
        minusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pagerAdapter.getCount() > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("안내");
                    builder.setMessage("마지막 페이지를 삭제하시겠습니까?\n마지막 페이지의 내용도 삭제됩니다.");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deletePage(pages - 1);  // 마지막 페이지 삭제
                            pager.setCurrentItem(pages - 1, true);  // 새로 추가된 페이지로 이동
                            Toast.makeText(getContext(), "페이지가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("아니오", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    Toast.makeText(getContext(),"노트는 최소 1장 이상이어야 합니다.",Toast.LENGTH_SHORT).show();
                }
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

        // ViewPager 페이지 변경 리스너 추가
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 페이지가 선택될 때의 처리
                pageNumber.setText(String.valueOf(position + 1));  // 페이지 번호 업데이트
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(0);
        pageNumber.setText("1");
        return rootView;
    }

    // 페이지 설정
    public void setPage(String name) {
        if ((pref != null) && !pref.contains(name)){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(name,"");
            editor.commit();
        }
        if ((pref != null) && (pref.contains(name))) {
            String content = pref.getString(name, "");

            // ScrollView를 사용하여 EditText의 내용이 많을 경우 스크롤 가능하게 처리
            ScrollView scrollView = new ScrollView(getContext());
            scrollView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
            ));
            scrollView.setPadding(20, 0, 20, 0);
            EditText editText = new EditText(getContext());
            editText.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            editText.setHint("메모를 입력하세요.");
            if (content.isEmpty()) {
                editText.setText("");
            } else {
                editText.setText(content);
            }

            // 폰트 크기 적용
            if (!restoreFontSize().isEmpty()) {
                editText.setTextSize(Float.parseFloat(restoreFontSize()));
            } else {
                editText.setTextSize(10);  // 초기 폰트 크기
            }

            // Enter 키를 눌러도 줄 바꿈이 가능하도록 설정
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editText.setBackgroundColor(Color.TRANSPARENT);
            editText.setBackground(null);

            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setLineSpacing(0f, 1.0f);


            scrollView.addView(editText);
            pagerAdapter.addItem(scrollView, editText);
        }
    }

    public String restorePagesState() {
        if ((pref != null) && (pref.contains("pages"))) {
            return pref.getString("pages", "");
        }
        return "1";
    }

    public void savePages(){
        SharedPreferences.Editor editor = pref.edit();
        if ((pref != null) && (pref.contains("pages"))) {
            editor.putString("pages", pages+"");
            editor.commit();
        }
    }
    public void deletePage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < pages) {
            pagerAdapter.getEditTextAtPosition(pagerAdapter.getCount()-1).setText("");
            pagerAdapter.getEditTextAtPosition(pagerAdapter.getCount()-1).setHint("");
            pagerAdapter.removeItemAtPosition(pageIndex);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("page" + (pageIndex + 1));
            editor.commit();

            pages--;
            savePages();

            // 페이지를 새로 갱신
            pagerAdapter.notifyDataSetChanged(); // 데이터 변경 사항 반영
        }
    }

    // 폰트 크기 복원
    public String restoreFontSize() {
        if ((pref != null) && (pref.contains("font-size"))) {
            String size = pref.getString("font-size", "");
            return size;
        }
        return "";
    }

    // 폰트 크기 저장
    public void saveFontState(int f) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("font-size", f + "");
        editor.commit();
    }

    // 저장 메시지
    private void save_message() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("안내");
        builder.setMessage("저장하시겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SharedPreferences.Editor editor = pref.edit();
                for (int i = 0; i < pages; i++) {
                    String content = pagerAdapter.getEditTextAtPosition(i).getText().toString();
                    editor.putString("page" + (i + 1), content);  // 페이지 번호를 1부터 시작하게 처리
                }
                editor.commit();
                Toast.makeText(getContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                MainActivity mainActivity = (MainActivity) getActivity();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        builder.setNegativeButton("아니오", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 지우기 메시지
    private void clear_message() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("안내");
        builder.setMessage("현재 페이지의 노트를 지우시겠습니까?\n(저장하기 전까지 기존 자료는 유지됩니다.)");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                pagerAdapter.getEditTextAtPosition(pager.getCurrentItem()).setText("");
                Toast.makeText(getContext(), "현재 페이지의 노트를 지웠습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("아니오", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // PagerAdapter 구현
    public class MyPagerAdapter extends PagerAdapter {
        private ArrayList<View> items = new ArrayList<>();
        private ArrayList<EditText> editTexts = new ArrayList<>();

        // 페이지 추가 메서드
        public void addItem(View item, EditText editText) {
            items.add(item);
            editTexts.add(editText);
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

        public EditText getEditTextAtPosition(int position) {
            return editTexts.get(position);
        }
        public void removeItemAtPosition(int position) {
            if (position >= 0 && position < items.size()) {
                items.remove(position);
                editTexts.remove(position);
                notifyDataSetChanged();
            }
        }
    }
}
