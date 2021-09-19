package com.example.readerfortheblindv2;


import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Settings {
    private float pitch;
    private float speed;
    private Locale language;
    private Boolean english;

    Settings(){
        this.pitch = 1;
        this.speed = 1;
        this.language = Locale.UK;
        this.english = true;
    }

    protected void setPitch (float pitch){
        this.pitch = pitch;
    }

    protected void setSpeed (float speed){
        this.speed = speed;
    }

    protected void setLanguage (Locale language){
        this.language = language;
        if (language == Locale.UK){
            english = true;
        }else {
            english = false;
        }
    }

    protected float getPitch (){
        return pitch;
    }

    protected float getSpeed (){
        return speed;
    }

    protected Locale getLanguage (){
        return language;
    }

    protected Boolean getEnglish (){
        return english;
    }

    protected void speak (TextToSpeech mTTS, String text){
        mTTS.setPitch(this.getPitch());
        mTTS.setSpeechRate(this.getSpeed());
        mTTS.speak(text, TextToSpeech.QUEUE_ADD, null, "");
    }

    protected void updateSettings (Context context){
        FileInputStream inputStream;
        int DEFAULT_BUFFER_SIZE = 10000;
        Gson gson = new Gson();
        String readJson;

        try {

            inputStream = context.openFileInput("settings.json");
            FileReader reader = new FileReader(inputStream.getFD());
            char[] buf = new char[DEFAULT_BUFFER_SIZE];
            int n;
            StringBuilder builder = new StringBuilder();
            while ((n = reader.read(buf))>=0 ){
                String tmp = String.valueOf(buf);
                String substring = (n<DEFAULT_BUFFER_SIZE) ? tmp.substring(0, n) : tmp;
                builder.append(substring);
            }
            reader.close();
            readJson = builder.toString();
            if (!readJson.isEmpty()){
                Type collectionType = new TypeToken<Settings>(){}.getType();
                Settings newSettings = gson.fromJson(readJson, collectionType);

                this.pitch = newSettings.pitch;
                this.speed = newSettings.speed;
                this.language = newSettings.language;
                this.english = newSettings.english;
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void saveSettings (Context context){
        Gson gson = new Gson();

        Settings settings = this;
        String json = gson.toJson(settings);
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("settings.json", MODE_PRIVATE);
            FileWriter writer = new FileWriter(outputStream.getFD());
            writer.write(json);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
