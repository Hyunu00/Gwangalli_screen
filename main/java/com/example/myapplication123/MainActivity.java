package com.example.myapplication123;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.ResourceBundle;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private String[] videoUrls = {
            "https://www.youtube.com/embed/84CavklLRYQ",
            "https://www.youtube.com/embed/-JhoMGoAfFc",
            "https://www.youtube.com/embed/dbrTAsxhdiE"
    };

    private String[] videoNames = {
            "광안리",
            "서울 한강",
            "서울 남산타워"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient());
        sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadInitialVideo();

        Button menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUrlSelectionDialog();
            }
        });
    }

    private void loadInitialVideo() {
        String savedUrl = sharedPreferences.getString("selectedUrl", null);

        if (savedUrl == null) {
            Random random = new Random();
            int randomIndex = random.nextInt(videoUrls.length);
            loadVideo(videoUrls[randomIndex]);
        } else {
            loadVideo(savedUrl);
        }
    }

    private void loadVideo(String videoUrl) {
        String htmlData = "<html><body style='margin:0;padding:0;'>" +
                "<iframe width='100%' height='100%' src='" + videoUrl +
                "?autoplay=1&controls=0&modestbranding=1&fs=0&rel=0&iv_load_policy=3&vq=hd2160'" +
                " frameborder='0' allow='autoplay; encrypted-media' allowfullscreen>" +
                "</iframe></body></html>";

        webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null);
        webView.setOnTouchListener((v, event) -> true);
    }

    private void saveUserSelection(String selectedUrl) {
        editor.putString("selectedUrl", selectedUrl);
        editor.apply();
    }

    private void showUrlSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Video");

        builder.setItems(videoNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedUrl = videoUrls[which];
                loadVideo(selectedUrl);
                saveUserSelection(selectedUrl);
            }
        });

        builder.show();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 모든 터치 이벤트를 무시
        return true;
    }
}

