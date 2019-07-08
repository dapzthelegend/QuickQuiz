package com.dapzthelegend.quickquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtA, txtB, txtC, txtD, txtQuestion, btnNext, btnEnd, txtNo;
    private List<Quiz> quizList = new ArrayList<>();
    private List<Words> wordList;
    private int count = 1;
    private String[] ans = {"A", "B", "C", "D"};
    private String answer;
    private DatabaseReference ref;
    private Toolbar mToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      getList();
        InitializeFields();


    }

    private void getList() {
        wordList = MainActivity.keyWords;
        Log.e("Size", ""+wordList.size());

        for(Words word: wordList){
           ref = FirebaseDatabase.getInstance().getReference().child("KeyWords").child(word.getKey()).child("Question");
           ref.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if(dataSnapshot.exists()){
                       for(DataSnapshot shot : dataSnapshot.getChildren()){
                           Quiz quiz = shot.getValue(Quiz.class);
                           quizList.add(quiz);
                           Log.e("Size", "Added");
                       }


                   }else{
                       Log.e("Size", "Don't exist");
                   }
                   count = 0;
                   DisplayQuestion();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
        }

        //DisplayQuestion();


    }

    private void InitializeFields() {
        txtA = (TextView) findViewById(R.id.txtA);
        txtB = (TextView) findViewById(R.id.txtB);
        txtC = (TextView) findViewById(R.id.txtC);
        txtD = (TextView) findViewById(R.id.txtD);

        txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        btnEnd = (TextView) findViewById(R.id.btnEnd);
        btnNext = (TextView) findViewById(R.id.btnNext);
        txtNo = (TextView) findViewById(R.id.txtNumber);

        txtA.setOnClickListener(this);
        txtB.setOnClickListener(this);
        txtC.setOnClickListener(this);
        txtD.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        btnNext.setOnClickListener(this);

//
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DisplayQuestion();
//
//            }
//        });
//
//        btnEnd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

//
//        wordList = MainActivity.keyWords;
//
//        for(Words word: wordList){
//           // quizList.addAll(word.getQuestion());
//        }





    }

    private void DisplayQuestion() {
        if(count<quizList.size()){
            Quiz quiz = quizList.get(count);
            txtQuestion.setText(quiz.getQuestion());
            txtB.setText("B.  "+ quiz.getB());
            txtC.setText("C.  "+ quiz.getC());
            txtD.setText("D.  "+ quiz.getD());


            txtA.setText("A.  "+ quiz.getA());
            txtNo.setText(count+"/50");
            btnNext.setVisibility(View.GONE);
            btnEnd.setVisibility(View.GONE);

            Random rand = new Random();

            answer = ans[rand.nextInt(4)];

            setEnabledTrue();
            count++;
            restoreState();

            }


    }

    private void restoreState() {
        txtA.setBackgroundResource(R.drawable.background_shape);
        txtA.setTextColor(Color.BLACK);
        txtB.setBackgroundResource(R.drawable.background_shape);
        txtB.setTextColor(Color.BLACK);
        txtC.setBackgroundResource(R.drawable.background_shape);
        txtC.setTextColor(Color.BLACK);
        txtD.setBackgroundResource(R.drawable.background_shape);
        txtD.setTextColor(Color.BLACK);

    }

    @Override
    public void onClick(View v) {
        Log.e("Text", "Clicked");
        switch (v.getId()){
            case R.id.txtA:
                Log.e("Text", answer);
                setEnabledFalse();
                markA();
                break;

            case R.id.txtB:
                Log.e("Text", answer);
                setEnabledFalse();
                markB();
                break;

            case R.id.txtC:
                Log.e("Text", answer);
                setEnabledFalse();
                markC();

                break;
            case R.id.txtD:
                Log.e("Text", answer);
                setEnabledFalse();
                markD();

                break;
            case R.id.btnEnd:
                Log.e("Text", "Clicked");
                onBackPressed();
                break;
            case R.id.btnNext:
                DisplayQuestion();
                break;
        }

    }

    public void setEnabledFalse(){
        txtA.setClickable(false);
        txtB.setClickable(false);
        txtC.setClickable(false);
        txtD.setClickable(false);

        btnNext.setVisibility(View.VISIBLE);
        btnEnd.setVisibility(View.VISIBLE);
    }


    public void setEnabledTrue(){
        txtA.setClickable(true);
        txtB.setClickable(true);
        txtC.setClickable(true);
        txtD.setClickable(true);

    }


    public void markA(){
        if(answer.equals("A")){
            txtA.setBackgroundResource(R.drawable.background_positive_button);
            txtA.setTextColor(Color.WHITE);
        }else if(answer.equals("B")){
            txtA.setBackgroundResource(R.drawable.background_negative_button);
            txtA.setTextColor(Color.WHITE);
            txtB.setBackgroundResource(R.drawable.background_positive_button);
            txtB.setTextColor(Color.WHITE);
            }else if(answer.equals("C")){
            txtA.setBackgroundResource(R.drawable.background_negative_button);
            txtA.setTextColor(Color.WHITE);
            txtC.setBackgroundResource(R.drawable.background_positive_button);
            txtC.setTextColor(Color.WHITE);
        }else if(answer.equals("D")){
            txtA.setBackgroundResource(R.drawable.background_negative_button);
            txtA.setTextColor(Color.WHITE);
            txtD.setBackgroundResource(R.drawable.background_positive_button);
            txtD.setTextColor(Color.WHITE);
        }

        }


    public void markB(){
        if(answer.equals("B")){
            txtA.setBackgroundResource(R.drawable.background_positive_button);
            txtA.setTextColor(Color.WHITE);
        }else if(answer.equals("A")){
            txtB.setBackgroundResource(R.drawable.background_negative_button);
            txtB.setTextColor(Color.WHITE);
            txtA.setBackgroundResource(R.drawable.background_positive_button);
            txtA.setTextColor(Color.WHITE);
        }else if(answer.equals("C")){
            txtB.setBackgroundResource(R.drawable.background_negative_button);
            txtB.setTextColor(Color.WHITE);
            txtC.setBackgroundResource(R.drawable.background_positive_button);
            txtC.setTextColor(Color.WHITE);
        }else if(answer.equals("D")){
            txtB.setBackgroundResource(R.drawable.background_negative_button);
            txtB.setTextColor(Color.WHITE);
            txtD.setBackgroundResource(R.drawable.background_positive_button);
            txtD.setTextColor(Color.WHITE);
        }

    }


    public void markC(){

        if(answer.equals("C")){
            txtC.setBackgroundResource(R.drawable.background_positive_button);
            txtC.setTextColor(Color.WHITE);
        }else if(answer.equals("A")){
            txtC.setBackgroundResource(R.drawable.background_negative_button);
            txtC.setTextColor(Color.WHITE);
            txtA.setBackgroundResource(R.drawable.background_positive_button);
            txtA.setTextColor(Color.WHITE);
        }else if(answer.equals("B")){
            txtC.setBackgroundResource(R.drawable.background_negative_button);
            txtC.setTextColor(Color.WHITE);
            txtB.setBackgroundResource(R.drawable.background_positive_button);
            txtB.setTextColor(Color.WHITE);
        }else if(answer.equals("D")){
            txtC.setBackgroundResource(R.drawable.background_negative_button);
            txtC.setTextColor(Color.WHITE);
            txtD.setBackgroundResource(R.drawable.background_positive_button);
            txtD.setTextColor(Color.WHITE);
        }




    }


    public void markD(){
        if(answer.equals("D")){
            txtC.setBackgroundResource(R.drawable.background_positive_button);
            txtC.setTextColor(Color.WHITE);
        }else if(answer.equals("A")){
            txtD.setBackgroundResource(R.drawable.background_negative_button);
            txtD.setTextColor(Color.WHITE);
            txtA.setBackgroundResource(R.drawable.background_positive_button);
            txtA.setTextColor(Color.WHITE);
        }else if(answer.equals("B")){
            txtD.setBackgroundResource(R.drawable.background_negative_button);
            txtD.setTextColor(Color.WHITE);
            txtB.setBackgroundResource(R.drawable.background_positive_button);
            txtB.setTextColor(Color.WHITE);
        }else if(answer.equals("C")){
            txtD.setBackgroundResource(R.drawable.background_negative_button);
            txtD.setTextColor(Color.WHITE);
            txtC.setBackgroundResource(R.drawable.background_positive_button);
            txtC.setTextColor(Color.WHITE);
        }



}



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();

        }

        else{

        }
        return true;
    }




}
