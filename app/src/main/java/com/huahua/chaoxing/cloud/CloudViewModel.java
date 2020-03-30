package com.huahua.chaoxing.cloud;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author by Administrator
 * Email 1986754601@qq.com
 * Date on 2020/3/26.
 * 作用：云签到的viewModel
 * PS: Not easy to write code, please indicate.
 */


public class CloudViewModel extends ViewModel implements LifecycleObserver {
    private ObservableField<String> tel = new ObservableField<>("");
    private ObservableField<String> pass = new ObservableField<>("");
    private ObservableField<String> email = new ObservableField<>("");
    private ObservableField<String> signPlace = new ObservableField<>("");
    private MutableLiveData<String> state = new MutableLiveData<>("");
    private MutableLiveData<String> code = new MutableLiveData<>("");
    private MutableLiveData<String> netInfo = new MutableLiveData<>("");
    private MutableLiveData<String> oneWord = new MutableLiveData<>("");
    private ObservableField<String> url = new ObservableField<>("https://api.ixiaowai.cn/mcapi/mcapi.php");

    public MutableLiveData<String> getOneWord() {
        return oneWord;
    }

    public void setOneWord(String oneWord) {
        this.oneWord.postValue(oneWord);
    }

    public MutableLiveData<String> getState() {
        return state;
    }

    public void setState(String state) {
        this.state.postValue(state);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public MutableLiveData<String> getNetInfo() {
        return netInfo;
    }

    public void setNetInfo(String netInfo) {
        this.netInfo.postValue(netInfo);
    }

    public String getTel() {
        return tel.get();
    }

    public void setTel(String tel) {
        this.tel.set(tel);
    }

    public String getPass() {
        return pass.get();
    }

    public void setPass(String pass) {
        this.pass.set(pass);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }


    public String getSignPlace() {
        return signPlace.get();
    }

    public void setSignPlace(String signPlace) {
        this.signPlace.set(signPlace);
    }

    public String getCode() {
        return code.getValue();
    }

    public void setCode(String code) {
        this.code.postValue(code);
    }

    public MutableLiveData<String> getCodeLiveData() {
        return code;
    }
}
