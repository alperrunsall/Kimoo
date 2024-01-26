package com.kimoo.android.extra;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResimIndir {
    private Context mContext;
    private CircleImageView imageView;
    private ImageView normalView;
    private String url, dosyaParent, fotoAdi;

    public ResimIndir(Context mContext, String url, String dosyaParent, String fotoAdi) {
        this.mContext = mContext;
        this.imageView = imageView;
        this.url = url;
        this.fotoAdi = fotoAdi;
        this.normalView = normalView;
        this.dosyaParent = dosyaParent;

        new downloadImage().execute(url);
    }

    public class downloadImage extends AsyncTask<String, Void, Bitmap> {

        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(mContext, result, fotoAdi);
        }
        public void saveImage(Context context, Bitmap b, String imageName) {
            ContextWrapper wrapper = new ContextWrapper(context);
            File file = wrapper.getDir(dosyaParent, MODE_PRIVATE);
            file = new File(file, imageName);
            if (b != null) {
                try {
                    OutputStream stream = null;
                    stream = new FileOutputStream(file);
                    b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Uri savedImageURI = Uri.parse(file.getAbsolutePath());
        }
    }

}
