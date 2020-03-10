package com.huahua.chaoxing.userinfo.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Map<String, String>> cookies = new MutableLiveData<>();
    private MutableLiveData<Map<Object, Object>> temp = new MutableLiveData<>();
    private MutableLiveData<Boolean> refresh = new MutableLiveData<>();

    public PageViewModel() {
        cookies.setValue(new HashMap<>());
        temp.setValue(new HashMap<>());
        refresh.setValue(false);
    }

    public void setRefresh() {
        this.refresh.setValue(!refresh.getValue());
    }

    public MutableLiveData<Boolean> getRefresh() {
        return refresh;
    }

    public LiveData<Map<Object, Object>> getLiveTemp() {

        return temp;
    }

    public Map<Object, Object> getTemp() {
        return temp.getValue();
    }

    public void setTemp(Object key, Object value) {
        this.temp.getValue().put(key, value);
    }

    public Map<String, String> getCookies() {
        return cookies.getValue();
    }

    public void setCookies(Map<String, String> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            Objects.requireNonNull(this.cookies.getValue()).putAll(cookies);
            System.out.println("保存的cookies" + cookies);
        }
    }


}