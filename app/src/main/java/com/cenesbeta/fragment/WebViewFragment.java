package com.cenesbeta.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.cenesbeta.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WebViewFragment extends CenesFragment {

    private WebView wvWebview;
    private ImageView ivBackButton;

    public String webViewUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

         View view = inflater.inflate(R.layout.fragment_webview, container, false);

         wvWebview = (WebView)view.findViewById(R.id.wv_webview);
         ivBackButton = (ImageView) view.findViewById(R.id.iv_back_button);

         wvWebview.getSettings().setJavaScriptEnabled(true);
         wvWebview.loadUrl(webViewUrl);

        ivBackButton.setOnClickListener(onClickListener);

         return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_back_button:
                    getActivity().onBackPressed();
                    break;

            }
        }
    };
}
