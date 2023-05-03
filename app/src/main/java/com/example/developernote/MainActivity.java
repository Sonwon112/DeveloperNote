package com.example.developernote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {
    ImageView imgTurnOverLeft, imgTurnOverRight, imgPlayer, imgBuilding; // 메인 콘텐츠인 버튼과 건물 이미지, 플레이어 이미지
    ImageView imgDay,imgAfternoon,imgNight; // 배경화면
    Button btnMission, btnTodoList, btnProfile; // 아래쪽 리스트 박스 내용 변환용 버튼
    ScrollView missionScrollView, todoListScrollView; // 미션과 todolist 스크롤 뷰
    LinearLayout layoutProfile; // 자신의 프로필을 나타내는 레이아웃


    Animation animSlideHide_right, animSlideShow_right,animSlideHide_left,animSlideShow_left; // 화살표 버튼 클릭시 빌딩이 사라졌다가 나타나는 애니메이션
    AnimationDrawable playerWalkAnim; // 빌딩 전환시 player가 걷는 애니메이션 실행
    boolean isAnimPlay = false; // 애니메이션 실행동안 버튼 동작을 제한 하기 위한 flag boolean 값

    int[] buildingImgArr = {R.drawable.searchbuilding, R.drawable.errornotebuilding}; // 화살표 버튼 클릭에 따라 변환하는 이미지 변환을 관리하기 위한 빌딩이미지 배열
    int currBuildingImgNum = 0; // (0 = 검색 건물, 1 = 에러노트) 건물 현재 화면에 표시되는 빌딩이미지 index
    int buildingImgMaxNum; // 빌딩 이미지 배열에서 가장 마지막 인덱스를 추출하는 용도의 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildingImgMaxNum = buildingImgArr.length-1; // 빌딩 이미지 배열에 max 값 추출

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 전체화면으로 표시

        //---------------- 레이아웃의 뷰와 변수를 일치 시키는 부분
        imgTurnOverLeft = (ImageView) findViewById(R.id.imgTurnOverLeft);
        imgTurnOverRight = (ImageView) findViewById(R.id.imgTurnOverRight);
        imgPlayer = (ImageView) findViewById(R.id.imgPlayer);
        imgBuilding = (ImageView) findViewById(R.id.imgContentBuilding);

        imgDay = (ImageView) findViewById(R.id.bgDay);
        imgAfternoon = (ImageView) findViewById(R.id.bgAfternoon);
        imgNight = (ImageView) findViewById(R.id.bgNight);

        btnMission = (Button) findViewById(R.id.btnDailyMission);
        btnTodoList = (Button) findViewById(R.id.btnToDoList);
        btnProfile = (Button) findViewById(R.id.btnProfile);

        missionScrollView = (ScrollView) findViewById(R.id.dailyMissionScroll);
        todoListScrollView = (ScrollView) findViewById(R.id.toDolistScroll);
        layoutProfile = (LinearLayout) findViewById(R.id.profileLayout);

        //----------------- 애니메이션 xml 지정
        animSlideHide_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_hide_toright);
        animSlideShow_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_show_toright);
        animSlideHide_left = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_hide_toleft);
        animSlideShow_left = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_show_toleft);

        //----------------- 애니메이션 listener 할당
        animSlideHide_right.setAnimationListener(animListener);
        animSlideHide_left.setAnimationListener(animListener);
        animSlideShow_left.setAnimationListener(animListener);
        animSlideShow_right.setAnimationListener(animListener);

        playerWalkAnim = (AnimationDrawable) getDrawable(R.drawable.player_walk);

        //---------------- 화살표 버튼 클릭시 동작할 touchlistener 할당
        imgTurnOverRight.setOnTouchListener(leftRightBtnTouchListener);
        imgTurnOverLeft.setOnTouchListener(leftRightBtnTouchListener);

        //---------------- 목록 박스 버튼 클릭시 목록 전환을 위한 clicklistener 할당
        btnProfile.setOnClickListener(actionListClickListener);
        btnMission.setOnClickListener(actionListClickListener);
        btnTodoList.setOnClickListener(actionListClickListener);

        //---------------- 빌딩 이미지 클릭시 동작을 구현하기 위해 click listener 할당
        imgBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable imgViewDrawable = imgBuilding.getDrawable();

                compareimg : for(int imgId : buildingImgArr){
                    Drawable resDrawable = getResources().getDrawable(imgId);
                    Bitmap imgBitmap = ((BitmapDrawable) imgViewDrawable).getBitmap();
                    Bitmap resBitmap = ((BitmapDrawable) resDrawable).getBitmap();
                    if(imgBitmap.sameAs(resBitmap)){
                        switch (imgId){
                            case R.drawable.searchbuilding:
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                                    startActivity(intent);
                                break;
                            case R.drawable.errornotebuilding:
                                break;
                        }
                        break compareimg;
                    }
                }
            }
        });

        //------------------ 시간에 따라 배경이미지 변환을 위한 thread
        Thread checkTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
                SimpleDateFormat minFormat = new SimpleDateFormat("mm");
                long currTime = System.currentTimeMillis();
                Date currHour = new Date(currTime);
                int time = Integer.parseInt(hourFormat.format(currHour));
                float min = Float.parseFloat(minFormat.format(currHour));

                if(time>=0 && time <= 5){ // 밤 -> 아침
                    imgAfternoon.setAlpha(1f);
                    setImageAlpha(imgNight,time,(t)->t != 0 ? 1f-((float)t/5f)-(min/600f):1f);
                } else if (time>=6 && time <=8) { // 아침 -> 낮
                    setImageAlpha(imgAfternoon,time,(t)->t-6 != 0 ? 1f-((float)(t-6)/2f)-(min/600f):1f);
                } else if( time >= 16 && time <= 18){ // 낮 -> 저녁
                    setImageAlpha(imgAfternoon,time,(t)->t-6!=0?((float)(t-16)/2f)+(min/600f):0.1f);
                } else if( time >= 19 && time <= 23){ // 저녁 -> 밤
                    imgAfternoon.setAlpha(1f);
                    setImageAlpha(imgNight,time,(t)->t-19 != 0? ((float)(t-19)/5f)+(min/600f):0.1f);
                }
            }

            // 시간에 따라 이미지에 alpha값을 수정하여 배경을 변환시킨다
            public void setImageAlpha(ImageView img,int currHour, Function<Integer,Float> mappingFunc){
                //Log.d("check", "setImageAlpha: "+mappingFunc.apply(currHour));
                img.setAlpha(mappingFunc.apply(currHour));
            }


        });
        checkTimeThread.start();
    }

    // 이미지 넘기기 버튼 touch listener
    View.OnTouchListener leftRightBtnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
                // 버튼 touchdown시 버튼이 눌러진 이미지로 변경 touchup시 원상복구 후 버튼에 맞는 사라지는 애니메이션 실행, 현재 이미지 값을 변경
                if (view.getId() == R.id.imgTurnOverLeft) {
                    // 왼쪽 화살표 버튼
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && !isAnimPlay) {
                        imgTurnOverLeft.setImageResource(R.drawable.pressed_leftbtn);
                        // 사라지는 애니메이션
                        imgBuilding.startAnimation(animSlideHide_right);
                        // player가 걷는 애니메이션
                        imgPlayer.setBackground(playerWalkAnim);
                        imgPlayer.setScaleX(-1);
                        playerWalkAnim.start();
                        // 현재 빌딩이미지 index 값 수정
                        currBuildingImgNum = currBuildingImgNum == 0 ? buildingImgMaxNum : currBuildingImgNum - 1;
                        isAnimPlay = true;
                        return true;
                    }if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        imgTurnOverLeft.setImageResource(R.drawable.default_leftbtn);
                        return true;
                    }
                } else if (view.getId() == R.id.imgTurnOverRight) {
                    // 오른쪽 화살표 버튼
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && !isAnimPlay) {
                        imgTurnOverRight.setImageResource(R.drawable.pressed_rightbtn);
                        // 사라지는 애니메이션
                        imgBuilding.startAnimation(animSlideHide_left);
                        // player가 걷는 애니메이션
                        imgPlayer.setBackground(playerWalkAnim);
                        imgPlayer.setScaleX(1);
                        playerWalkAnim.start();
                        //현재 빌딩 이미지 index 값 수정
                        currBuildingImgNum = currBuildingImgNum == buildingImgMaxNum ? 0 : currBuildingImgNum + 1;
                        isAnimPlay = true;
                        return true;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        imgTurnOverRight.setImageResource(R.drawable.default_rightbtn);
                        return true;
                    }
                }

                return false;

        }

    };

    // 아래쪽 목록창 전환 구현용 버튼 클릭 리스너
    View.OnClickListener actionListClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 선택된 버튼에 따라 버튼의 background 변경 스크롤뷰나 레이아웃 표시 여부 변경
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

    //사라지는 애니메이션이 끝난 후 이미지 변경후 나타내는 애니메이션 실행
    Animation.AnimationListener animListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            imgBuilding.setImageResource(buildingImgArr[currBuildingImgNum]); // 사라지는 애니메이션 종료 후 이미지를 전환
            // animSlideHide_right가 끝나면 이미지 전환후 나타내기 효과 표시
            if(animation.equals(animSlideHide_right)){
                imgBuilding.startAnimation(animSlideShow_right);
            }else if(animation.equals(animSlideHide_left)){
                imgBuilding.startAnimation(animSlideShow_left);
            }else{
                // 나타내기 애니메이션이 종료 되었을때 버튼이 다시 동작할 수 있겠금 flag값 변경
                isAnimPlay = false;
                playerWalkAnim.stop();
                imgPlayer.setImageResource(R.drawable.player_idle);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

}