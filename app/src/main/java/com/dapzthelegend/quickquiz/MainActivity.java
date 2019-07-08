package com.dapzthelegend.quickquiz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener , View.OnClickListener {
    private DatabaseReference keyWordsRef;
    private FirebaseAuth mAuth;
    private static final int CAMERA_REQUEST = 123;
    private String mCurrentPhotoPath;
    private String pathFile;
    private Uri mImageUri;
    private TextRecognizer textRecognizer;
    private String text;
    private ProgressDialog mLoadingBar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Button btnCancel;
    private Button btnDone;
    private CircleImageView imgCamera;
    private CircleImageView imgScan;
    private CircleImageView imgHistory;
    private TextView txtScannedText, txtScan, txtCamera;
    private SurfaceView cameraView;
    private CameraSource cameraSource;
    public static List<Words> keyWords = new ArrayList<>();
    private List<Quiz> questionList = new ArrayList<>();
    private SearchView searchView ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitializeFields();


        RoundedCornersTransformation transformation = new RoundedCornersTransformation(10,10);

        try{
            Picasso.get().load(R.drawable.camera).transform(transformation).into(imgCamera);
            Picasso.get().load(R.drawable.scan).transform(transformation).into(imgScan);
        }catch (Exception e){

        }



        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }


        cameraView = findViewById(R.id.surface_view);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent
                        (MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;

                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("error", "error creating file");
                }

                if (photoFile != null) {
                    pathFile = photoFile.getAbsolutePath();
                    mImageUri = FileProvider.getUriForFile(getApplicationContext(), "com.dapzthelegend.quickquiz.fileprovider", photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textRecognizer = null;
                displayItems();


            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textRecognizer = null;

                text = txtScannedText.getText().toString();

               displayItems();


               checkKeyWords(txtScannedText.getText().toString());

                //checkKeyWords(text);

            }
        });

        imgScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
              removeItems();

            }
        });

        }

    public  void InitializeFields(){
        imgScan = (CircleImageView) findViewById(R.id.imgScan);
        imgCamera = (CircleImageView) findViewById(R.id.imgCamera);
        imgHistory = (CircleImageView) findViewById(R.id.imgHistory);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        txtScannedText = (TextView) findViewById(R.id.txtScannedText);
        txtScan = (TextView) findViewById(R.id.txtScan);
        txtCamera = (TextView) findViewById(R.id.txtPicture);
        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        recyclerView = (RecyclerView) findViewById(R.id.subRecyclerView);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(MainActivity.this);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerAdapter adapter = new RecyclerAdapter(Values.getList());
        recyclerView.setAdapter(adapter);
        }




    private void runTextRecognition(Bitmap map) {
       // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(map);

        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        processTextRecognitionResult(firebaseVisionText);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        String s ="";

        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < blocks.size(); i++) {
            Log.e("TAG", "Block");
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                s = s + lines.get(j).getText() + " ";
            }
        }
           Log.e("Tag", "no"+ s);
        checkKeyWords(s);
    }





    private void CreateNewAccount() {
        final String email = "dapzthelegend@gmail.com";
        final String password = "123456";
        final String name = "Dara";
        mAuth = FirebaseAuth.getInstance();




            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                final String currentUserID = mAuth.getCurrentUser().getUid();

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name", name);
                                userMap.put("password", password);
                                userMap.put("uid", currentUserID);
                                userMap.put("email", email);

                                DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference();

                                mUsersRef.child("Users").child(currentUserID).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.e("Exception", "" + "success");

                                        } else {

                                            Log.e("Exception", "" + "failed");

                                        }
                                    }
                                });

                            } else {

                                String error = task.getException().toString();
                                int id = error.indexOf(":");
                                String err = error.substring(id + 1);

                              Log.e("TAG", err);
                            }

                        }
                    });

        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            try {
                DisplayProgress();
                Bitmap bitmap = BitmapFactory.decodeFile(pathFile);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                runTextRecognition(bitmap);
                txtCamera.setText("Retake Picture");
                displayItems();


            } catch (Exception e) {
                Log.e("Error","Uri not set" );
            }
        }

    }



    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }
        File image = File.createTempFile(imageFileName,
                ".jpg", storageDir);

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }




    private void scan(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        cameraView.setVisibility(View.VISIBLE);
      textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e){

                    }

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();

                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {



                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    Log.e("Detected" , "Detectionns");
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0){
                        txtScannedText.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i = 0; i<items.size(); i++){
                                    TextBlock item = items.valueAt(i);
                                    Log.e("Detected" , " s"+ item.getValue());
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                txtScannedText.setText(stringBuilder);
                            }

                        });
                    }
                }
            });


    }


    private void checkKeyWords(final String note){
        DisplayProgress();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("KeyWords");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;

                String result = "none yet";
                for(DataSnapshot shot: dataSnapshot.getChildren()){
                    Log.e("TAG", "searching");
                    for (String str : shot.getKey().split("\\s")){
                        if(str.length() >= 4){
                            if(note.contains(shot.getKey().toString())){
                                count++;

                                Words word = shot.getValue(Words.class);
                                keyWords.add(word);
                            }

                        }

                    }
                    if(count>=10){

                        mLoadingBar.dismiss();
                        break;
                    }

                }

                Log.e("TAG", ""+keyWords.size());
                SendUserToWordsActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @SuppressLint("RestrictedApi")
    public void removeItems(){
        imgCamera.setVisibility(View.GONE);
        imgScan.setVisibility(View.GONE);
        imgHistory.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.VISIBLE);
        txtScannedText.setVisibility(View.VISIBLE);


    }


    public void displayItems(){

        txtScannedText.setVisibility(View.GONE);
        imgCamera.setVisibility(View.VISIBLE);
        imgScan.setVisibility(View.VISIBLE);
        imgHistory.setVisibility(View.VISIBLE);
        cameraView.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

    }

    private void DisplayProgress(){
        mLoadingBar = ProgressDialog.show(this, "", "Message");
        mLoadingBar.getWindow().setGravity(Gravity.CENTER);
        mLoadingBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mLoadingBar.setCanceledOnTouchOutside(true);
        mLoadingBar.setContentView(R.layout.loader);
        mLoadingBar.show();
    }

    private void SendUserToWordsActivity(){
        mLoadingBar.dismiss();
        Intent keyWordIntent = new Intent(MainActivity.this, KeyWordsActivity.class);
        startActivity(keyWordIntent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(MainActivity.this,SearchActivity.class );
        intent.putExtra("query", searchView.getQuery().toString());
        startActivity(intent);


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }

    }


    private class RecyclerAdapter extends RecyclerView.Adapter<SubjectsViewHolder>{
        private List<Subject> subList;

        private RecyclerAdapter(List<Subject> subList){
            this.subList = subList;
        }

        @NonNull
        @Override
        public SubjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_layout, parent, false);
            SubjectsViewHolder holder = new SubjectsViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final SubjectsViewHolder holder, int position) {
            Subject subject = subList.get(position);

            try{
                holder.imageView.setImageResource(subject.getImage());
            }catch (Exception e){

            }

            holder.txtName.setText(subject.getSubject());


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.itemView.setSelected(true);
                }
            });


        }

        @Override
        public int getItemCount() {
            return subList.size();
        }
    }









}
