package com.example.myapplication123;


import android.os.Bundle;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 커버 화면 해상도를 설정
        int coverWidth = 720;
        int coverHeight = 800;

        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);

        // WebView 설정
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        // HTML 데이터 로드
        String videoId = "84CavklLRYQ";
        String htmlData = "<html><body style='margin:0;padding:0;'>" +
                "<iframe width='100%' height='100%' src='https://www.youtube.com/embed/" + videoId +
                "?autoplay=1&controls=0&modestbranding=1&fs=0&rel=0&iv_load_policy=3&vq=hd2160'" + // vq=hd2160은 4K 화질을 요청
                " frameborder='0' allow='autoplay; encrypted-media' allowfullscreen>" +
                "</iframe></body></html>";

        webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null);

        webView.setWebViewClient(new WebViewClient());
        webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null);

        // 터치 이벤트 비활성화
        webView.setOnTouchListener((v, event) -> true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 모든 터치 이벤트를 무시
        return true;
    }
}
