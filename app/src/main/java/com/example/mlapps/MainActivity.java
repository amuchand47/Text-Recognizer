package com.example.mlapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button capture,detect;
    private ImageView img;
    private TextView result;



    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capture = (Button)findViewById(R.id.capture_image);
        detect = (Button)findViewById(R.id.detect_image);

        img = (ImageView)findViewById(R.id.image_view);

        result = (TextView)findViewById(R.id.text);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 dispatchTakePictureIntent();
                 result.setText(" ");
            }
        });


        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();

            }

        });

    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextFromImage() {

        final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionTextDetector firebaseVisionTextRecognizer = FirebaseVision.getInstance().getVisionTextDetector();

        firebaseVisionTextRecognizer.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
              disPlayTextFromImage(firebaseVisionText);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("Error: ", e.getMessage());
            }
        });

    }

    private void disPlayTextFromImage(FirebaseVisionText firebaseVisionText) {

        List <FirebaseVisionText.Block> textBlockList = firebaseVisionText.getBlocks();
        if(textBlockList.size()==0){
            Toast.makeText(MainActivity.this, "No text found in image", Toast.LENGTH_LONG).show();

        }
        else{

            for(FirebaseVisionText.Block textBlock : firebaseVisionText.getBlocks()){
                String text =textBlock.getText();
                result.setText(text);
            }
        }
    }


}
