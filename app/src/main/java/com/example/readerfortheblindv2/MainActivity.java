package com.example.readerfortheblindv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button captureImageButton, settingsButton;
    private TextToSpeech mTTS;
    private Settings mTTSSettings;

    private String mainWelcomeEng = "You are in the main activity in Reader For The Blind. On the bottom you have got Settings Button, " +
                                    "above it you have got Get Picture Button, which move you to the camera. On the top and center there is our logo." +
                                    "Long clicks on buttons activate their descriptions. ";
    private String mainWelcomeGer = "Sie befinden sich in der Hauptaktivität in Reader For The Blind. Unten befindet sich die Schaltfläche Einstellungen." +
                                    "Darüber befindet sich die Schaltfläche Bild abrufen, mit der Sie zur Kamera gelangen. Oben und in der Mitte befindet " +
                                    "sich unser Logo. Lange Klicks auf Schaltflächen aktivieren deren Beschreibungen.";
    private String capImgBtnLongEng = "Capture Image Activity Button";
    private String capImgBtnLongGer = "Bildaktivitätsschaltfläche erfassen";
    private String settBtnLongEng = "Settings Activity Button";
    private String settBtnLongGer = "Einstellungsaktivitätsschaltfläche";
    private String capImgBtnEng = "You are going to Capture Image Activity. After this description, you must take a picture. After this action, " +
                                    "the application will find the text. On the top will be Come Back Button, under this, will be your picture and the" +
                                    "found text. On the bottom will be Play Again Button. Now, take your picture.";
    private String capImgBtnGer = "Sie werden die Bildaktivität erfassen. Nach dieser Beschreibung müssen Sie ein Bild aufnehmen. Nach dieser Aktion findet" +
                                    "die Anwendung den Text. Oben befindet sich die Schaltfläche Zurück. Darunter befindet sich Ihr Bild und der gefundene Text." +
                                    "Unten befindet sich die Schaltfläche Erneut spielen. Nehmen Sie jetzt Ihr Foto auf.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureImageButton = findViewById(R.id.capture_image);
        settingsButton = findViewById(R.id.settings_button);
        mTTSSettings = new Settings();
        mTTSSettings.updateSettings(this);

        if (mTTSSettings.getEnglish()){
            captureImageButton.setText("GET PICTURE");
            settingsButton.setText("SETTINGS");
        }else {
            captureImageButton.setText("BILD ERHALTEN");
            settingsButton.setText("DIE EINSTELLUNGEN");
        }

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTTSSettings.getEnglish()){
                    mTTSSettings.speak(mTTS, capImgBtnEng);
                }else {
                    mTTSSettings.speak(mTTS, capImgBtnGer);
                }

                Intent intent = new Intent(MainActivity.this, TextRecognition.class);
                startActivity(intent);
            }
        });

        captureImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mTTSSettings.getEnglish()){
                    mTTSSettings.speak(mTTS, capImgBtnLongEng);
                }else {
                    mTTSSettings.speak(mTTS, capImgBtnLongGer);
                }

                return true;
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mTTSSettings.getEnglish()){
                    mTTSSettings.speak(mTTS, settBtnLongEng);
                }else {
                    mTTSSettings.speak(mTTS, settBtnLongGer);
                }

                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTTSSettings.updateSettings(getApplicationContext());
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = mTTS.setLanguage(mTTSSettings.getLanguage());

                    if (mTTSSettings.getEnglish()){
                        mTTSSettings.speak(mTTS, mainWelcomeEng);
                    }else {
                        mTTSSettings.speak(mTTS, mainWelcomeGer);
                    }

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS", "Language not supported.");
                    }
                } else {
                    Log.e("TTS", "Initialization TTS failed.");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
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
