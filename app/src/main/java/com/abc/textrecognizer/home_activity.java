package com.abc.textrecognizer;


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
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class home_activity extends AppCompatActivity {
    private static int REQUEST_IMAGE_CAPTURE=101;
  Button btn,detectBtn;
  Bitmap imageBitmap=null;
  ImageView imgV;
  TextView myTextView;
  String TAG="info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);
        btn=(Button)findViewById(R.id.snapIt);
        myTextView=(TextView) findViewById(R.id.textView);
        detectBtn=(Button)findViewById(R.id.detect);
        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageBitmap!=null)
                {
                //make an api call to the cloud
                runTextRecognizer(imageBitmap);
            }
            else
                {
                    Toast.makeText(home_activity.this, "Take a snap first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgV=(ImageView)findViewById(R.id.imageView);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageTakeIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(imageTakeIntent.resolveActivity(getPackageManager())!=null)
                {
                    startActivityForResult(imageTakeIntent,REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK)
        {
            Bundle extras=data.getExtras();
             imageBitmap=(Bitmap)extras.get("data");

            imgV.setImageBitmap(imageBitmap);
            myTextView.setText("");
        }
    }

   private void runTextRecognizer(Bitmap imageBitmap)
   {
       FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
       FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
               .getOnDeviceTextRecognizer();
       Log.i(TAG, "runImageLabeling: ");
       Task<FirebaseVisionText> result =
               detector.processImage(image)
                       .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                           @Override
                           public void onSuccess(FirebaseVisionText firebaseVisionText) {

                              myTextView.setText(null);
                              if(firebaseVisionText.getTextBlocks().size()==0)
                              {
                                  myTextView.setText(R.string.no_text);
                                  return;
                              }
                              for (FirebaseVisionText.TextBlock block :firebaseVisionText.getTextBlocks())
                              {
                                  myTextView.append(block.getText());
                              }
                           }
                       })
                       .addOnFailureListener(
                               new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Log.i(TAG, "onFailure: exception handled");
                                   }
                               });
   }

}
