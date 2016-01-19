package com.pzl.bordercircleimageview;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity
{
    private RequestQueue mQueue;
    
    private ImageRequest imageRequest;
    
    private BorderCircleImageView borderCircleImageView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this);
        borderCircleImageView = (BorderCircleImageView)findViewById(R.id.bciv);
        imageRequest = new ImageRequest("http://img1.touxiang.cn/uploads/20120509/09-014358_953.jpg",
            new Response.Listener<Bitmap>()
            {
                @Override
                public void onResponse(Bitmap response)
                {
                    borderCircleImageView.setBitmap(response);
                }
            }, 0, 0, Config.RGB_565, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                }
            });
    }
    
    public void click(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.tv_loadNetImage:
                mQueue.add(imageRequest);
                break;
        }
    }
    
}
