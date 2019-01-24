package com.listerily.mcdesign.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.listerily.mcdesign.utils.BitmapRepeater;
import com.listerily.moddedpe.R;

public abstract class DesignActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mcd_bg);
		bitmap = BitmapRepeater.repeat(getWindow().getDecorView().getWidth(), getWindow().getDecorView().getHeight(), bitmap);
		getWindow().getDecorView().setBackground(new BitmapDrawable(getResources(),bitmap));
	}
}
