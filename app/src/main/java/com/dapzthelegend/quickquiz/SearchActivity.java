package com.dapzthelegend.quickquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String query;
    private ProgressDialog mLoadingBar;
    private WebView webView;
    private Toolbar mToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        recyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);
        webView = (WebView) findViewById(R.id.searchWebView);
        layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        query = getIntent().getStringExtra("query").toLowerCase();
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL, 0));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getSupportActionBar().setTitle(query);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }


    @Override
    protected void onStart() {
        super.onStart();
        mLoadingBar = ProgressDialog.show(this, "Title", "Message");
        mLoadingBar.getWindow().setGravity(Gravity.CENTER);
        mLoadingBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mLoadingBar.setContentView(R.layout.loader);
        mLoadingBar.setCanceledOnTouchOutside(true);
        mLoadingBar.setMessage(null);
        mLoadingBar.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("KeyWords");


        FirebaseRecyclerOptions<Words> options =
                new FirebaseRecyclerOptions.Builder<Words>()
                .setQuery(ref.orderByChild("key").startAt(query), Words.class)
                .build();

        FirebaseRecyclerAdapter<Words, KeyWordsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Words, KeyWordsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final KeyWordsViewHolder holder, int i, @NonNull final Words model) {
                        mLoadingBar.dismiss();




                        holder.txtName.setText( String.valueOf(model.getKey().charAt(0)).toUpperCase() + model.getKey().substring(1, model.getKey().length()));
                        holder.txtDescription.setText(model.getDefinition());
                        try{
                            holder.imageView.setImageResource(Values.getImages());

                        }catch (Exception e){
                            e.printStackTrace();
                        }



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                holder.itemView.setSelected(true);
                                webView.setVisibility(View.VISIBLE);
                                webView.loadUrl(model.getLink());
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public KeyWordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyword_layout, parent, false);
                       KeyWordsViewHolder holder = new KeyWordsViewHolder(view);
                       return holder;

                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


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
