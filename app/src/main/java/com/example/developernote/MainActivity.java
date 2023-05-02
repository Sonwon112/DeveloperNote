package com.example.developernote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
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
    ImageView imgTurnOverLeft, imgTurnOverRight, imgPlayer, imgBuilding;
    ImageView imgDay,imgAfternoon,imgNight;
    Button btnMission, btnTodoList, btnProfile;
    ScrollView missionScrollView, todoListScrollView;
    LinearLayout layoutProfile;


    Animation animSlideHide_right, animSlideShow_right,animSlideHide_left,animSlideShow_left;
    boolean isAnimPlay = false;

    int[] buildingImgArr = {R.drawable.searchbuilding, R.drawable.errornotebuilding};
    int currBuildingImgNum = 0; // 0 = 검색 건물, 1 = 에러노트 건물
    int buildingImgMaxNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildingImgMaxNum = buildingImgArr.length-1;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


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

        animSlideHide_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_hide_toright);
        animSlideShow_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_show_toright);
        animSlideHide_left = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_hide_toleft);
        animSlideShow_left = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_show_toleft);

        animSlideHide_right.setAnimationListener(animListener);
        animSlideHide_left.setAnimationListener(animListener);
        animSlideShow_left.setAnimationListener(animListener);
        animSlideShow_right.setAnimationListener(animListener);

        imgTurnOverRight.setOnTouchListener(leftRightBtnTouchListener);
        imgTurnOverLeft.setOnTouchListener(leftRightBtnTouchListener);

        btnProfile.setOnClickListener(actionListClickListener);
        btnMission.setOnClickListener(actionListClickListener);
        btnTodoList.setOnClickListener(actionListClickListener);

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

        Thread checkTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
                SimpleDateFormat minFormat = new SimpleDateFormat("mm");
                long currTime = System.currentTimeMillis();
                Date currHour = new Date(currTime);
                int time = Integer.parseInt(hourFormat.format(currHour));
                float min = Float.parseFloat(minFormat.format(currHour));

                if(time>=0 && time <= 5){
                    imgAfternoon.setAlpha(1f);
                    setImageAlpha(imgNight,time,(t)->t != 0 ? 1f-((float)t/5f)-(min/600f):1f);
                } else if (time>=6 && time <=8) {
                    setImageAlpha(imgAfternoon,time,(t)->t-6 != 0 ? 1f-((float)(t-6)/2f)-(min/600f):1f);
                } else if( time >= 16 && time <= 18){
                    setImageAlpha(imgAfternoon,time,(t)->t-6!=0?((float)(t-16)/2f)+(min/600f):0.1f);
                } else if( time >= 19 && time <= 23){
                    imgAfternoon.setAlpha(1f);
                    setImageAlpha(imgNight,time,(t)->t-19 != 0? ((float)(t-19)/5f)+(min/600f):0.1f);
                }
            }
            public void setImageAlpha(ImageView img,int currHour, Function<Integer,Float> mappingFunc){
                //Log.d("check", "setImageAlpha: "+mappingFunc.apply(currHour));
                img.setAlpha(mappingFunc.apply(currHour));
            }


        });
        checkTimeThread.start();
    }

    // 이미지 넘기기 버튼
    View.OnTouchListener leftRightBtnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

                if (view.getId() == R.id.imgTurnOverLeft) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && !isAnimPlay) {
                        imgTurnOverLeft.setImageResource(R.drawable.pressed_leftbtn);
                        imgBuilding.startAnimation(animSlideHide_right);
                        currBuildingImgNum = currBuildingImgNum == 0 ? buildingImgMaxNum : currBuildingImgNum - 1;
                        isAnimPlay = true;
                        return true;
                    }if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        imgTurnOverLeft.setImageResource(R.drawable.default_leftbtn);
                        return true;
                    }
                } else if (view.getId() == R.id.imgTurnOverRight) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && !isAnimPlay) {
                        imgTurnOverRight.setImageResource(R.drawable.pressed_rightbtn);
                        imgBuilding.startAnimation(animSlideHide_left);
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
    Animation.AnimationListener animListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            imgBuilding.setImageResource(buildingImgArr[currBuildingImgNum]);
            if(animation.equals(animSlideHide_right)){
                imgBuilding.startAnimation(animSlideShow_right);
            }else if(animation.equals(animSlideHide_left)){
                imgBuilding.startAnimation(animSlideShow_left);
            }else{
                isAnimPlay = false;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

}