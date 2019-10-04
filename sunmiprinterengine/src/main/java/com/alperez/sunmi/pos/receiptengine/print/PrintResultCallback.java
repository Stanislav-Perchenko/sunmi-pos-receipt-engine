package com.alperez.sunmi.pos.receiptengine.print;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface PrintResultCallback {
    void onComplete();
    void onError(@NonNull String reason, @Nullable Throwable t);
}
