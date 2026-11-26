package com.example.ck_mobile.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ImageUtils {

    private ImageUtils() {
    }

    /** Đọc ảnh từ Uri, thu nhỏ về cạnh dài tối đa maxSide và nén JPEG. */
    public static byte[] toJpegBytes(ContentResolver resolver, Uri uri, int maxSide, int quality) throws IOException {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream in = resolver.openInputStream(uri)) {
            BitmapFactory.decodeStream(in, null, bounds);
        }
        int sample = 1;
        while (Math.max(bounds.outWidth, bounds.outHeight) / sample > maxSide * 2) sample *= 2;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = sample;
        Bitmap bmp;
        try (InputStream in = resolver.openInputStream(uri)) {
            bmp = BitmapFactory.decodeStream(in, null, opts);
        }
        if (bmp == null) throw new IOException("Không đọc được ảnh.");

        float scale = (float) maxSide / Math.max(bmp.getWidth(), bmp.getHeight());
        if (scale < 1f) {
            bmp = Bitmap.createScaledBitmap(bmp, Math.round(bmp.getWidth() * scale),
                    Math.round(bmp.getHeight() * scale), true);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, out);
        return out.toByteArray();
    }
}
