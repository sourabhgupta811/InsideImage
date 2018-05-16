package com.project.iitcsl.mlkit;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.wonderkiln.camerakit.CameraKitEventListenerAdapter;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CameraView camera;
    RecyclerView recyclerview;
    List<ImageData> list=new ArrayList<>();
    ScrollView scrollView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerview=findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        scrollView=findViewById(R.id.scroll_view);
        progressBar=findViewById(R.id.progress_bar);
        scrollView.animate().translationY(1000);
        FirebaseApp.initializeApp(getApplicationContext());
        camera=findViewById(R.id.camera);
        camera.addCameraKitListener(new CameraKitEventListenerAdapter() {
            @Override
            public void onImage(CameraKitImage image) {
                getLabelsForImage(image.getBitmap());
            }
        });
    }
    public void capture(View v){
        progressBar.setVisibility(View.VISIBLE);
        camera.captureImage();
    }
    private void getLabelsForImage(Bitmap bitmap) {
        FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionLabelDetector detector= FirebaseVision.getInstance().getVisionLabelDetector();
        Task<List<FirebaseVisionLabel>> listTask = detector.detectInImage(image);
        listTask.addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionLabel> firebaseVisionLabels) {
                list.clear();
                for(FirebaseVisionLabel vision:firebaseVisionLabels){
                    list.add(new ImageData(vision.getLabel(),String.valueOf(vision.getConfidence())));
                    loadRecyclerview(list);
                }
            }
        });
        listTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Task failed with an exception
                Snackbar.make(camera,"Sorry, something went wrong!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRecyclerview(List<ImageData> list) {
        progressBar.setVisibility(View.GONE);
        RecyclerviewAdapter adapter=new RecyclerviewAdapter(this,list);
        recyclerview.setAdapter(adapter);
        scrollView.setVisibility(View.VISIBLE);
        scrollView.animate().translationYBy(-1000).setDuration(2000).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    public void onBackPressed() {
        if(scrollView.getVisibility()==View.VISIBLE) {
            scrollView.animate().translationYBy(1000).setDuration(2000).start();
            scrollView.setVisibility(View.GONE);
        }
        else
            super.onBackPressed();
    }
}
