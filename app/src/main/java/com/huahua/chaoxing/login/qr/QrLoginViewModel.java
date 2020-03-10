package com.huahua.chaoxing.login.qr;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class QrLoginViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private LiveData<HashMap<String, String>> cookies;

    public QrLoginViewModel() {
        cookies = new MutableLiveData<>(new HashMap<String, String>());
    }

    public HashMap<String, String> getCookies() {
        return cookies.getValue();
    }

    public void setCookies(HashMap<String, String> cookies) {
        this.cookies.getValue().putAll(cookies);
    }
}
