package com.example.readerfortheblindv2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.List;
import java.util.Locale;

public class TextRecognition extends AppCompatActivity {


    private TextToSpeech mTTS;
    private Button comeBackBtn;
    private Button playAgainBtn;
    private ImageView imageView;
    private TextView textView;
    private Settings mTTSSettings;

    private String comeBackBtnLongEng = "Come back to main activity Button";
    private String comeBackBtnLongGer = "Kommen Sie zurück zur Hauptaktivität Taste";
    private String playAgainBtnLongEng = "Play again recognized text button";
    private String playAgainBtnLongGer = "Wiedergegebenen Text wiedergeben Taste";
    private String noText = "NO TEXT HERE!";
    private String noTextGer = "KEIN TEXT HIER!";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        dispatchTakePictureIntent();

        comeBackBtn = findViewById(R.id.ComeBackBtn);
        playAgainBtn = findViewById(R.id.PlayAgainBtn);
        imageView = findViewById(R.id.pictureView);
        textView = findViewById(R.id.textView);
        mTTSSettings = new Settings();
        mTTSSettings.updateSettings(this);

        if (mTTSSettings.getEnglish()){
            comeBackBtn.setText("COME BACK");
            playAgainBtn.setText("PLAY AGAIN");
        }else {
            comeBackBtn.setText("KOMM ZURÜCK");
            playAgainBtn.setText("NOCHMAL ABSPIELEN");
        }

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = mTTS.setLanguage(mTTSSettings.getLanguage());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS", "Language not supported.");
                    }
                } else {
                    Log.e("TTS", "Initialization TTS failed.");
                }
            }
        });

        comeBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextRecognition.this, MainActivity.class);
                startActivity(intent);
            }
        });

        comeBackBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mTTSSettings.getEnglish()){
                    mTTSSettings.speak(mTTS, comeBackBtnLongEng);
                }else {
                    mTTSSettings.speak(mTTS, comeBackBtnLongGer);
                }

                return true;
            }
        });

        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTTSSettings.speak(mTTS, textView.getText().toString());
            }
        });

        playAgainBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mTTSSettings.getEnglish()){
                    mTTSSettings.speak(mTTS, playAgainBtnLongEng);
                }else {
                    mTTSSettings.speak(mTTS, playAgainBtnLongGer);
                }
                return true;
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            textRecognition(imageBitmap);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // Get the device's sensor orientation.
        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
        }
        return rotationCompensation;
    }

    private void textRecognition(Bitmap bitmap) {
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer textRecognizer = com.google.mlkit.vision.text.TextRecognition.getClient();
        Task<Text> result = textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                String resultText = text.getText();
                if (!resultText.isEmpty()){
                    textView.setText(resultText);
                    mTTSSettings.speak(mTTS, resultText);
                }else {
                    if (mTTSSettings.getEnglish()){
                        textView.setText(noText);
                        mTTSSettings.speak(mTTS, noText);
                    }else {
                        textView.setText(noTextGer);
                        mTTSSettings.speak(mTTS, noTextGer);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

}