package education.juxin.com.educationpro.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.recyclerview.TabCourseCacheAdapter;
import education.juxin.com.educationpro.base.BaseFragment;
import education.juxin.com.educationpro.dialog.ComTwnBtnDialog;
import education.juxin.com.educationpro.download.BigFileDownloadManager;
import education.juxin.com.educationpro.download.VideoCacheInfoData;
import education.juxin.com.educationpro.download.VideoCacheManager;
import education.juxin.com.educationpro.interfaces.IRefreshUI;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseCachePlayActivity;
import education.juxin.com.educationpro.ui.activity.mine.SettingActivity;
import education.juxin.com.educationpro.util.StorageUtil;
import education.juxin.com.educationpro.view.RecycleItemDecoration;

/**
 * 课程缓存页面
 * Created by Administrator on 2018/3/9.
 */
public class TabMyCacheFragment extends BaseFragment implements View.OnClickListener, IRefreshUI {

    private View mNoDataLayout;
    private RelativeLayout deleteRootLayout;
    private RelativeLayout deleteFooterLayout;
    private ArrayList<VideoCacheInfoData> mList = new ArrayList<>();

    private boolean isSelectAllState = false;
    private View mView;
    private TabCourseCacheAdapter adapter;
    private CheckBox selectAllChBox;
    private TextView selectAllTxt;
    private TextView deleteOrCancelTxt;
    private ImageView deleteImg;
    private SmartRefreshLayout refreshLayout;

    public static TabMyCacheFragment newInstance(String title) {
        TabMyCacheFragment fragment = new TabMyCacheFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_cache, container, false);
        initView();
        searchCacheVideos();
        BigFileDownloadManager.tabMyCacheFragmentRefreshUI = this;
        SettingActivity.refreshUI = this;
        return mView;
    }

    private void initView() {
        mNoDataLayout = mView.findViewById(R.id.no_data_view);
        deleteRootLayout = mView.findViewById(R.id.delete_root_layout);
        deleteRootLayout.setVisibility(View.GONE);

        deleteFooterLayout = mView.findViewById(R.id.delete_selected_data_layout);
        deleteFooterLayout.setVisibility(View.GONE);
        deleteFooterLayout.setEnabled(true);
        deleteFooterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<VideoCacheInfoData> tempDeleteList = new ArrayList<>();
                ArrayList<String> tempPathList = new ArrayList<>();
                for (int i = 0; i < mList.size(); ++i) {
                    if (mList.get(i).isCheck()) {
                        tempDeleteList.add(mList.get(i));
                        tempPathList.add(mList.get(i).getCacheFileName());
                    }
                }
                if (tempPathList.size() == 0) {
                    return;
                }

                ComTwnBtnDialog comTwnBtnDialog = new ComTwnBtnDialog(getActivity(), ComTwnBtnDialog.DIALOG_CLEAR_MY_CACHE);
                comTwnBtnDialog.setDialogBtnClickListener(new ComTwnBtnDialog.IDialogBtnClickListener() {
                    @Override
                    public void onDialogLeftBtnClick() {
                    }

                    @Override
                    public void onDialogRightBtnClick() {
                        mList.removeAll(tempDeleteList);

                        for (String data : tempPathList) {
                            VideoCacheManager.deleteVideoInfoById(getActivity(), data);
                            StorageUtil.deleteFileOrDirectory(new File(data));
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
                comTwnBtnDialog.show();

            }
        });

        selectAllChBox = mView.findViewById(R.id.select_all_checkbox);
        selectAllTxt = mView.findViewById(R.id.select_all_tv);
        deleteOrCancelTxt = mView.findViewById(R.id.delete_or_cancel_tv);
        deleteImg = mView.findViewById(R.id.delete_img);

        selectAllChBox.setOnClickListener(this);
        deleteOrCancelTxt.setOnClickListener(this);

        selectAllChBox.setVisibility(View.GONE);
        selectAllChBox.setChecked(false);
        selectAllTxt.setText("缓存课程");
        selectAllTxt.setEnabled(false);
        deleteImg.setVisibility(View.VISIBLE);
        deleteOrCancelTxt.setText("删除");
        deleteOrCancelTxt.setTextColor(getResources().getColor(R.color.dark_gray_txt));

        refreshLayout = mView.findViewById(R.id.smart_refresh);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                searchCacheVideos();
                refreshLayout.finishRefresh(500);
            }
        });
        RecyclerView courseRecycler = mView.findViewById(R.id.course_recycler);
        courseRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        courseRecycler.addItemDecoration(new RecycleItemDecoration(getActivity()));
        adapter = new TabCourseCacheAdapter(getActivity(), mList);
        adapter.setOnItemClickListener(new TabCourseCacheAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (selectAllChBox.getVisibility() == View.GONE) {
                    Intent intent = new Intent(getActivity(), CourseCachePlayActivity.class);
                    intent.putExtra("location_video_path", mList.get(position).getCacheFileName());
                    intent.putExtra("course_cover_url", mList.get(position).getCourseCoverImg());
                    startActivity(intent);
                }

            }

            @Override
            public void onLongClick(int position) {
            }
        });
        adapter.setOnSelectItemListener(new TabCourseCacheAdapter.OnSelectItemListener() {
            @Override
            public void onAllSelected(boolean isAll) {
                selectAllChBox.setChecked(isAll);
            }
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mList.size() == 0) {
                    mNoDataLayout.setVisibility(View.VISIBLE);
                    deleteFooterLayout.setVisibility(View.GONE);
                    deleteRootLayout.setVisibility(View.GONE);

                    isSelectAllState = false;

                    selectAllChBox.setVisibility(View.GONE);
                    selectAllChBox.setChecked(false);
                    selectAllTxt.setText("缓存课程");
                    selectAllTxt.setEnabled(false);
                    deleteImg.setVisibility(View.VISIBLE);
                    deleteOrCancelTxt.setText("删除");
                    deleteOrCancelTxt.setTextColor(getResources().getColor(R.color.dark_gray_txt));
                    adapter.setShowSelectCheckBox(isSelectAllState);
                } else {
                    mNoDataLayout.setVisibility(View.GONE);
                    deleteRootLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        courseRecycler.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_or_cancel_tv:
                if (isSelectAllState) {
                    selectAllChBox.setVisibility(View.GONE);
                    selectAllChBox.setChecked(false);
                    selectAllTxt.setText("缓存课程");
                    selectAllTxt.setEnabled(false);
                    deleteImg.setVisibility(View.VISIBLE);
                    deleteOrCancelTxt.setText("删除");
                    deleteOrCancelTxt.setTextColor(getResources().getColor(R.color.dark_gray_txt));
                    deleteFooterLayout.setVisibility(View.GONE);
                    deleteRootLayout.setVisibility(View.GONE);
                } else {
                    selectAllChBox.setVisibility(View.VISIBLE);
                    selectAllTxt.setText("");
                    selectAllTxt.setEnabled(true);
                    deleteImg.setVisibility(View.GONE);
                    deleteOrCancelTxt.setText("取消");
                    deleteOrCancelTxt.setTextColor(getResources().getColor(R.color.concern_bg));
                    if (mList != null && !mList.isEmpty()) {
                        deleteFooterLayout.setVisibility(View.VISIBLE);
                        deleteRootLayout.setVisibility(View.VISIBLE);
                    } else {
                        deleteFooterLayout.setVisibility(View.GONE);
                        deleteRootLayout.setVisibility(View.GONE);
                    }
                }

                isSelectAllState = !isSelectAllState;

                adapter.setShowSelectCheckBox(isSelectAllState);
                adapter.notifyDataSetChanged();
                break;

            case R.id.select_all_checkbox:
                adapter.setAllOrAllNoSelected(selectAllChBox.isChecked());
                adapter.notifyDataSetChanged();

            default:
                break;
        }
    }

    @Override
    public void onRefreshUI() {
        searchCacheVideos();
    }

    private void searchCacheVideos() {
        mList.clear();
        mList.addAll(VideoCacheManager.readAllVideoInfo(getActivity()));
        adapter.notifyDataSetChanged();
    }
}
