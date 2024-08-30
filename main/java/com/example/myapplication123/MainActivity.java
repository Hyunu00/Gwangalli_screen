package com.example.myapplication123;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ArrayList<String> videoUrls;
    private ArrayList<String> videoNames;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LinearLayout menuLayout;
    private LinearLayout linksContainer;
    private boolean isMenuVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        Button addButton = findViewById(R.id.add_button);
        Button addLinkButton = findViewById(R.id.add_link_button);
        menuLayout = findViewById(R.id.menu_layout);
        linksContainer = findViewById(R.id.links_container);

        sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        videoUrls = new ArrayList<>(sharedPreferences.getStringSet("videoUrls", new HashSet<>()));
        videoNames = new ArrayList<>(sharedPreferences.getStringSet("videoNames", new HashSet<>()));

        // 초기 링크들을 LinearLayout에 추가
        refreshLinks();

        addButton.setOnClickListener(v -> {
            if (isMenuVisible) {
                menuLayout.setVisibility(View.GONE);
                isMenuVisible = false;
            } else {
                menuLayout.setVisibility(View.VISIBLE);
                isMenuVisible = true;
            }
        });

        addLinkButton.setOnClickListener(v -> showLinkDialog(-1));

        // WebView 설정
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient());

        // 초기 로드 (첫 번째 URL로 초기화)
        if (!videoUrls.isEmpty()) {
            loadVideo(videoUrls.get(0));
        }

        // 터치 이벤트 비활성화
        webView.setOnTouchListener((v, event) -> true);
    }

    private void loadVideo(String videoUrl) {
        String htmlData = "<html><body style='margin:0;padding:0;'>" +
                "<iframe width='100%' height='100%' src='" + videoUrl +
                "?autoplay=1&controls=0&modestbranding=1&fs=0&rel=0&iv_load_policy=3&vq=hd2160'" +
                " frameborder='0' allow='autoplay; encrypted-media' allowfullscreen>" +
                "</iframe></body></html>";

        webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null);
    }

    private void showLinkDialog(int index) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_video, null);

        EditText urlInput = dialogView.findViewById(R.id.url_input);
        EditText nameInput = dialogView.findViewById(R.id.name_input);

        if (index >= 0) {
            urlInput.setText(videoUrls.get(index));
            nameInput.setText(videoNames.get(index));
        }

        new AlertDialog.Builder(this)
                .setTitle(index >= 0 ? "Edit Video" : "Add Video")
                .setView(dialogView)
                .setPositiveButton(index >= 0 ? "Save" : "Add", (dialog, which) -> {
                    String url = urlInput.getText().toString();
                    String name = nameInput.getText().toString();

                    // YouTube Live URL 처리
                    if (url.contains("youtube.com/live/")) {
                        url = url.replace("youtube.com/live/", "youtube.com/embed/");
                        int queryIndex = url.indexOf("?");
                        if (queryIndex != -1) {
                            url = url.substring(0, queryIndex); // 쿼리 파라미터 제거
                        }
                    } else if (url.contains("watch?v=")) {
                        url = url.replace("watch?v=", "embed/");
                    }

                    if (!url.isEmpty() && !name.isEmpty()) {
                        if (index >= 0) {
                            videoUrls.set(index, url);
                            videoNames.set(index, name);
                        } else {
                            videoUrls.add(url);
                            videoNames.add(name);
                        }
                        saveData();
                        refreshLinks();
                    } else {
                        Toast.makeText(this, "URL and Name must not be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addLinkToMenu(String name, String url, int index) {
        View linkItemView = LayoutInflater.from(this).inflate(R.layout.item_link, null);

        TextView linkTextView = linkItemView.findViewById(R.id.link_name);
        Button editButton = linkItemView.findViewById(R.id.edit_button);
        Button deleteButton = linkItemView.findViewById(R.id.delete_button);

        linkTextView.setText(name);
        linkItemView.setOnClickListener(v -> loadVideo(url));

        editButton.setOnClickListener(v -> showLinkDialog(index));

        deleteButton.setOnClickListener(v -> {
            videoUrls.remove(index);
            videoNames.remove(index);
            saveData();
            refreshLinks();
        });

        linksContainer.addView(linkItemView);
    }

    private void refreshLinks() {
        linksContainer.removeAllViews();
        for (int i = 0; i < videoUrls.size(); i++) {
            addLinkToMenu(videoNames.get(i), videoUrls.get(i), i);
        }
    }

    private void saveData() {
        Set<String> urlSet = new HashSet<>(videoUrls);
        Set<String> nameSet = new HashSet<>(videoNames);
        editor.putStringSet("videoUrls", urlSet);
        editor.putStringSet("videoNames", nameSet);
        editor.apply();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
