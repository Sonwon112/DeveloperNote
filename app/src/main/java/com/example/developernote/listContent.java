package com.example.developernote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class listContent extends LinearLayout {

    EditText edtInput;
    Button btnsetInput;
    TextView showInput;

    @SuppressLint("ResourceAsColor")
    public listContent(Context context) {
        super(context);
        edtInput = new EditText(context);
        btnsetInput = new Button(context);
        showInput = new TextView(context);

        this.setBackgroundResource(R.drawable.listcontent);
        this.setOrientation(HORIZONTAL);

        btnsetInput.setText("추가");
        btnsetInput.setWidth(200);
        edtInput.setWidth(800);

        showInput.setHeight(100);
        showInput.setGravity(Gravity.CENTER_VERTICAL);
        showInput.setTextColor(Color.parseColor("#000000"));

        this.addView(edtInput);
        this.addView(btnsetInput);

        btnsetInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = edtInput.getText().toString();
                if(inputText.equals("")){
                    Toast.makeText(context,"목표를 입력하세요",Toast.LENGTH_SHORT).show();
                }else{
                    removeEdtBtn();
                    addTextView(inputText);
                }
            }
        });
    }
    public void removeEdtBtn(){
        this.removeView(edtInput);
        this.removeView(btnsetInput);
    }

    public void addTextView(String inputText){
        showInput.setText("ㆍ"+inputText);
        this.addView(showInput);
    }
}
