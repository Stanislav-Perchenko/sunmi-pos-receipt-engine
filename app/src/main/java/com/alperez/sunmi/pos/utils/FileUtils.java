package com.alperez.sunmi.pos.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtils {

    public static String loadAsset(Context ctx, String path) throws IOException {
        try (InputStream src = new BufferedInputStream(ctx.getAssets().open(path, AssetManager.ACCESS_STREAMING))) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
            byte[] buffer = new byte[512];
            int count;
            while ((count = src.read(buffer)) != -1) {
                bos.write(buffer, 0, count);
            }
            return bos.toString("UTF-8");
        }
    }

    private FileUtils() {}
}
