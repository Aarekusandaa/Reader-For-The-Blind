package com.example.readerfortheblindv2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private TextToSpeech mTTS;
    private Button comeBackBtn;
    private SeekBar seekBarSoundLvl;
    private SeekBar seekBarSoundSpeed;
    private RadioGroup radioGroup;
    private RadioButton radioButtonEnglish;
    private RadioButton radioButtonGerman;
    private Settings mTTSSettings;
    private TextView seekBarPitchText;
    private TextView seekBarSoundText;
    private TextView languageText;

    private String settActivEng = "You are in Settings Activity. On the top, you have Come Back Button. Under it, you have Sound Pitch Seek Bar." +
                                    "Next is Sound Speed Seek Bar. On the bottom you can choose language: English or German.";
    private String settActivGer = "Sie befinden sich in der Einstellungsaktivität. Oben haben Sie den Come Back Button. Darunter befindet sich die Sound Pitch" +
                                    "Seek Bar. Als nächstes kommt die Sound Speed Seek Bar. Unten können Sie die Sprache wählen: Englisch oder Deutsch.";
    private String comeBackBtnLongEng = "Come back to main activity button";
    private String comeBackBtnLongGer = "Kommen Sie zurück zur Hauptaktivität Taste";
    private String seekBarSPitchEng = "New pitch";
    private String seekBarSPitchGer = "Die neue Tonhöhe";
    private String seekBarSpeedEng = "New speed.";
    private String seekBarSpeedGer = "Neue Geschwindigkeit.";
    private String setLanguageEng = "You chose English language.";
    private String setLanguageGer = "Sie haben die deutsche Sprache gewählt.";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        comeBackBtn = findViewById(R.id.ComeBackBtn);
        seekBarSoundLvl = findViewById(R.id.sound_lvl_sb);
        seekBarSoundSpeed = findViewById(R.id.sound_speed_sb);
        radioGroup = findViewById(R.id.radio_group_language);
        radioButtonEnglish = findViewById(R.id.radio_btn_english);
        radioButtonGerman = findViewById(R.id.radio_btn_german);
        seekBarPitchText = findViewById(R.id.sound_lvl_text);
        seekBarSoundText = findViewById(R.id.sound_speed_text);
        languageText = findViewById(R.id.language_txt);

        mTTSSettings = new Settings();
        mTTSSettings.updateSettings(this);

        if (mTTSSettings.getEnglish()){
            comeBackBtn.setText("COME BACK");
            seekBarPitchText.setText("SOUND PITCH");
            seekBarSoundText.setText("SOUND SPEED");
            languageText.setText("LANGUAGE");
            radioButtonEnglish.setText("ENGLISH");
            radioButtonGerman.setText("GERMAN");
        }else {
            comeBackBtn.setText("KOMM ZURÜCK");
            seekBarPitchText.setText("TONHÖHE");
            seekBarSoundText.setText("TONGESCHWINDIGKEIT");
            languageText.setText("SPRACHE");
            radioButtonEnglish.setText("ENGLISCH");
            radioButtonGerman.setText("DEUTSCHE");
        }

        comeBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        comeBackBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mTTSSettings.getEnglish()){
                    mTTSSettings.speak(mTTS, comeBackBtnLongEng);
                }else {
                    mTTSSettings.speak(mTTS, comeBackBtnLongGer);
                }

                return true;
            }
        });

        seekBarSoundLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float pitch = seekBarSoundLvl.getProgress() / 50;
                if (pitch < 0.1){
                    pitch = 0.1f;
                }
                mTTSSettings.setPitch(pitch);
                mTTSSettings.saveSettings(getApplicationContext());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mTTSSettings.getEnglish()){
                    mTTSSettings.speak(mTTS, seekBarSPitchEng);
                }else {
                    mTTSSettings.speak(mTTS, seekBarSPitchGer);
                }
            }
        });

        seekBarSoundSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = seekBarSoundSpeed.getProgress() / 50;
                if (speed < 0.1){
                    speed = 0.1f;
                }
                mTTSSettings.setSpeed(speed);
                mTTSSettings.saveSettings(getApplicationContext());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mTTSSettings.getEnglish()){
                    mTTSSettings.speak(mTTS, seekBarSpeedEng);
                }else {
                    mTTSSettings.speak(mTTS, seekBarSpeedGer);
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_btn_german:
                        mTTSSettings.setLanguage(Locale.GERMANY);
                        mTTSSettings.saveSettings(getApplicationContext());
                        mTTSSettings.speak(mTTS, setLanguageGer);
                        comeBackBtn.setText("KOMM ZURÜCK");
                        seekBarPitchText.setText("TONHÖHE");
                        seekBarSoundText.setText("TONGESCHWINDIGKEIT");
                        languageText.setText("SPRACHE");
                        radioButtonEnglish.setText("ENGLISCH");
                        radioButtonGerman.setText("DEUTSCHE");
                        break;
                    case R.id.radio_btn_english:
                    default:
                        mTTSSettings.setLanguage(Locale.UK);
                        mTTSSettings.saveSettings(getApplicationContext());
                        mTTSSettings.speak(mTTS, setLanguageEng);
                        comeBackBtn.setText("COME BACK");
                        seekBarPitchText.setText("SOUND PITCH");
                        seekBarSoundText.setText("SOUND SPEED");
                        languageText.setText("LANGUAGE");
                        radioButtonEnglish.setText("ENGLISH");
                        radioButtonGerman.setText("GERMAN");
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = mTTS.setLanguage(mTTSSettings.getLanguage());

                    if (mTTSSettings.getEnglish()){
                        mTTSSettings.speak(mTTS, settActivEng);
                    }else {
                        mTTSSettings.speak(mTTS, settActivGer);
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
    protected void onDestroy() {
        if (mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

}
