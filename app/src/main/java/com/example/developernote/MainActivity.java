package com.example.developernote;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {


    ImageView imgTurnOverLeft, imgTurnOverRight, imgPlayer, imgBuilding;
    Button btnMission, btnTodoList, btnProfile;
    ScrollView missionScrollView, todoListScrollView;
    LinearLayout layoutProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imgTurnOverLeft = (ImageView) findViewById(R.id.imgTurnOverLeft);
        imgTurnOverRight = (ImageView) findViewById(R.id.imgTurnOverRight);
        imgPlayer = (ImageView) findViewById(R.id.imgPlayer);
        imgBuilding = (ImageView) findViewById(R.id.imgContentBuilding);

        btnMission = (Button) findViewById(R.id.btnDailyMission);
        btnTodoList = (Button) findViewById(R.id.btnToDoList);
        btnProfile = (Button) findViewById(R.id.btnProfile);


        missionScrollView = (ScrollView) findViewById(R.id.dailyMissionScroll);
        todoListScrollView = (ScrollView) findViewById(R.id.toDolistScroll);
        layoutProfile = (LinearLayout) findViewById(R.id.profileLayout);


        imgTurnOverRight.setOnTouchListener(leftRightBtnTouchListener);
        imgTurnOverLeft.setOnTouchListener(leftRightBtnTouchListener);

        btnProfile.setOnClickListener(actionListClickListener);
        btnMission.setOnClickListener(actionListClickListener);
        btnTodoList.setOnClickListener(actionListClickListener);
    }

    // 이미지 넘기기 버튼
    View.OnTouchListener leftRightBtnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(view.getId() == R.id.imgTurnOverLeft){
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    imgTurnOverLeft.setImageResource(R.drawable.pressed_leftbtn);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    imgTurnOverLeft.setImageResource(R.drawable.default_leftbtn);
                    return true;
                }
            }else if(view.getId() == R.id.imgTurnOverRight) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    imgTurnOverRight.setImageResource(R.drawable.pressed_rightbtn);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    imgTurnOverRight.setImageResource(R.drawable.default_rightbtn);
                    return true;
                }
            }
            return false;
        }
    };

    // 아래쪽 목록창 전환 구현
    View.OnClickListener actionListClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btnDailyMission){
                btnMission.setBackgroundResource(R.drawable.btn_selected);
                btnTodoList.setBackgroundResource(R.drawable.btn_unselected);
                btnProfile.setBackgroundResource(R.drawable.btn_unselected);
                missionScrollView.setVisibility(View.VISIBLE);
                todoListScrollView.setVisibility(View.INVISIBLE);
                layoutProfile.setVisibility(View.INVISIBLE);
            }else if(view.getId() == R.id.btnToDoList){
                btnMission.setBackgroundResource(R.drawable.btn_unselected);
                btnTodoList.setBackgroundResource(R.drawable.btn_selected);
                btnProfile.setBackgroundResource(R.drawable.btn_unselected);
                missionScrollView.setVisibility(View.INVISIBLE);
                todoListScrollView.setVisibility(View.VISIBLE);
                layoutProfile.setVisibility(View.INVISIBLE);
            }else if(view.getId() == R.id.btnProfile){
                btnMission.setBackgroundResource(R.drawable.btn_unselected);
                btnTodoList.setBackgroundResource(R.drawable.btn_unselected);
                btnProfile.setBackgroundResource(R.drawable.btn_selected);
                missionScrollView.setVisibility(View.INVISIBLE);
                todoListScrollView.setVisibility(View.INVISIBLE);
                layoutProfile.setVisibility(View.VISIBLE);
            }
        }
    };
}