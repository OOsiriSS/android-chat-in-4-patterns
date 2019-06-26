package nju.androidchat.client.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import lombok.Setter;
import nju.androidchat.client.R;

public class ItemTextSend extends LinearLayout implements View.OnLongClickListener {
    @StyleableRes
    int index0 = 0;

    private TextView textView;
    private ImageView imageView;
    private Context context;
    private UUID messageId;
    @Setter private OnRecallMessageRequested onRecallMessageRequested;

    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Bitmap bmp=(Bitmap)msg.obj;
                    imageView.setImageBitmap(bmp);
                    break;
            }
        };
    };

    public ItemTextSend(Context context, String text, UUID messageId, OnRecallMessageRequested onRecallMessageRequested) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_text_send, this);
       
    if(isMarkdownImage(text)){
            this.imageView = findViewById(R.id.chat_item_content_image);
            this.textView = findViewById(R.id.chat_item_content_text);
        
            this.messageId = messageId;
            this.onRecallMessageRequested = onRecallMessageRequested;
        
            this.setOnLongClickListener(this);
        
            this.setImage(text.substring(4, text.length() - 1));
            textView.setVisibility(INVISIBLE);
        }else{
            this.textView = findViewById(R.id.chat_item_content_text);
            this.messageId = messageId;
            this.onRecallMessageRequested = onRecallMessageRequested;
        
            this.setOnLongClickListener(this);
            //https://raw.githubusercontent.com/NJUSSJ/android-chat-in-4-patterns/master/docs/img/frp-throttle.png
            setText(text);
            //imageView.setVisibility(INVISIBLE);
        }
    }

    public String getText() {
        return textView.getText().toString();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setImage(String text){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Bitmap bmp = getHttpBitmap(text);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bmp;
                handle.sendMessage(msg);
            }
        }).start();
        //imageView.setImageURI(Uri.parse(text));
    }
    
    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定要撤回这条消息吗？")
                .setPositiveButton("是", (dialog, which) -> {
                    if (onRecallMessageRequested != null) {
                        onRecallMessageRequested.onRecallMessageRequested(this.messageId);
                    }
                })
                .setNegativeButton("否", ((dialog, which) -> {
                }))
                .create()
                .show();

        return true;


    }

private boolean isMarkdownImage(String text){
        boolean isImage = false;
        if(text.startsWith("![](") && text.endsWith(")")){
            isImage = true;
        }
        return isImage;
    }

    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            conn.setConnectTimeout(6000);
            //conn.setDoInput(true);
            conn.setUseCaches(false);

            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }



}
