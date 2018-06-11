package com.vn.ntsc.ui.search.byname;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.search.MeetPeopleBean;
import com.vn.ntsc.ui.search.SearchSettingActivity;
import com.vn.ntsc.ui.search.result.SearchResultFragment;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hnc on 23/08/2017.
 */

public class SearchByNameActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;
    @BindView(R.id.activity_search_by_name_edt_search)
    EditText mEdtSearch;

    private List<MeetPeopleBean> mDataCallback = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_by_name;
    }

    @Override
    public void onCreateView(View rootView) {
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Utils.setVectorDrawableLeft(R.drawable.ic_search, mEdtSearch);
        mEdtSearch.requestFocus();
        mEdtSearch.setOnEditorActionListener(searchAction);
        mEdtSearch.addTextChangedListener(textChangeListener);
    }

    //TODOUpdate Search by Name title - Created by Robert on 2018 May 16
    public void setSearchByNameTitle(String title) {
        mToolbar.setTitleCenter(title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putParcelableArrayListExtra(SearchSettingActivity.ACTIVITY_RESULT_LIST_MEET_PEOPLE, (ArrayList<? extends Parcelable>) mDataCallback);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //TODO 11962#note-5: Update Search by Name title - Updated by Robert on 2018 May 16
            if (Utils.isEmptyOrNull(mEdtSearch.getText().toString())) {
                setSearchByNameTitle(getString(R.string.title_search_by_name));
            }
        }
    };

    private TextView.OnEditorActionListener searchAction = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.activity_search_by_name_root, SearchResultFragment.newInstance(textView.getText().toString()));
                ft.addToBackStack("SearchResultFragment");
                ft.commit();

                InputMethodManager imm = (InputMethodManager) SearchByNameActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);

                return true;
            }
            return false;
        }
    };

    //============================= Return this datas to MainActivity ==============================
    // 2 method này được gọi khi Fragment: SearchResult được add trực tiếp trên SearchByNameActivity này
    // và khi "favorite" hoặc "unFavorite"

    public void addDataCallback(MeetPeopleBean meetPeopleBean) {
        mDataCallback.add(meetPeopleBean);
    }

    public void removeDataCallback(MeetPeopleBean meetPeopleBean) {
        mDataCallback.remove(meetPeopleBean);
    }
}
