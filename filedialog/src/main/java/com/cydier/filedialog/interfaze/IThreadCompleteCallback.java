package com.cydier.filedialog.interfaze;

public interface IThreadCompleteCallback {
    void call(int reasonCode, Object result);

    int getCallCode();
}
