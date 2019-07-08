package com.dapzthelegend.quickquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;


import java.util.List;

public class KeyWordsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private WebView webView;
    private ActionBar ab;
    private Button btnQuiz;
    private Toolbar mToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_words);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);


        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerView);

        btnQuiz = findViewById(R.id.btnQuiz);
        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KeyWordsActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });
        webView = (WebView) findViewById(R.id.webView);
        ab = (ActionBar) getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 0));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(MainActivity.keyWords);

        recyclerView.setAdapter(recyclerAdapter);




    }



    private class RecyclerAdapter extends RecyclerView.Adapter<KeyWordsViewHolder>{


       private List<Words> wordList;

        public RecyclerAdapter(List<Words> wordList){
            this.wordList= wordList;
            Log.e("Word", ""+wordList.size());
        }

        @NonNull
        @Override
        public KeyWordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyword_layout, parent, false);
            KeyWordsViewHolder holder = new KeyWordsViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final KeyWordsViewHolder holder, int position) {
            final Words model = wordList.get(position);


            try{
                holder.imageView.setImageResource(Values.getImages());

            }catch (Exception e){
                e.printStackTrace();
            }



            holder.txtName.setText( String.valueOf(model.getKey().charAt(0)).toUpperCase() + model.getKey().substring(1, model.getKey().length()));
            holder.txtDescription.setText(model.getDefinition());


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.itemView.setSelected(true);
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(model.getLink());
                }
            });

        }

        @Override
        public int getItemCount() {
            return wordList.size();
        }
    }

    @Override
    public void onBackPressed() {

        if(webView.getVisibility() == View.VISIBLE){
            webView.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
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
