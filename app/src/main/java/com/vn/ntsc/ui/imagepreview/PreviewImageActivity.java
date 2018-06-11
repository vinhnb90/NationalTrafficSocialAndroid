package com.vn.ntsc.ui.imagepreview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;

public class PreviewImageActivity extends BaseActivity implements PreviewImageAdapter.OnEventListener {

	public static String POSITION_KEY = "position";
	public static String LIST_FILE_KEY = "filePaths";

	private PreviewImageAdapter adapter;
	private ArrayList<String> 	filePaths = new ArrayList<String>();

	@BindView(R.id.activity_preview_image_pager) ViewPager viewPager;

	@Override
	public int getLayoutId() {
		return R.layout.activity_preview_image;
	}

	@Override
	public void onCreateView(View rootView) {

	}

	/**
	 * Start Preview Image with List file input
	 * @param context
	 * @param filePaths The ArrayList file path need to preview
	 * @param defaultPosition The default position to preview
	 */
	public static void startActivity(Context context, ArrayList<String> filePaths, int defaultPosition) {
		Intent intent = new Intent(context, PreviewImageActivity.class);
		intent.putStringArrayListExtra(LIST_FILE_KEY, filePaths);
		intent.putExtra(POSITION_KEY, defaultPosition);
		context.startActivity(intent);
	}

	@Override
	public void onViewReady() {
		Intent intent = getIntent();
		int position = intent.getIntExtra(POSITION_KEY, 0);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			ArrayList<String> filePaths = bundle.getStringArrayList(LIST_FILE_KEY);
			if (filePaths != null && filePaths.size() > 0) {
				this.filePaths.addAll(filePaths);
			}
		}
		adapter = new PreviewImageAdapter(this, this, filePaths);
		viewPager.setAdapter(adapter);

		//Displaying selected image first
		viewPager.setCurrentItem(position);
	}

	@Override
	public void onClose() {
		finish();
	}
}
