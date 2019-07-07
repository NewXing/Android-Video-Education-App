package education.juxin.com.educationpro.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.listview.PriceGridAdapter;
import education.juxin.com.educationpro.base.BaseFragment;
import education.juxin.com.educationpro.bean.ExtractionPriceBean;
import education.juxin.com.educationpro.bean.UserWxInfoBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.ui.activity.mine.BindWeChatActivity;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 申请提现 微信 碎片
 * Created by Administrator on 2018/3/19.
 */
public class WeChatFragment extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private TextView wxPayAccountTv;
    private PriceGridAdapter priceGridAdapter;

    private ArrayList<ExtractionPriceBean.ExtractionPriceData> mList = new ArrayList<>();
    private String wxPayIdStr;

    public static WeChatFragment newInstance(String title) {
        WeChatFragment fragment = new WeChatFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alipay_or_wechat, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View rootView) {
        GridView gridView = rootView.findViewById(R.id.price_grid);
        priceGridAdapter = new PriceGridAdapter(getActivity(), mList);
        gridView.setAdapter(priceGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                priceGridAdapter.itemChangeState(position);
            }
        });

        wxPayAccountTv = rootView.findViewById(R.id.pay_account_tv);
        wxPayAccountTv.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        reqPriceList();
        reqUserWxInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_account_tv:
                startActivity(new Intent(getActivity(), BindWeChatActivity.class));
                break;
            default:
                break;
        }
    }

    private void reqPriceList() {
        OkHttpUtils.get()
                .url(HttpConstant.EXTRACTION_PRICE_LIST)
                .addParams("page", "1")
                .addParams("rows", "10")
                .build()
                .execute(new HttpCallBack<ExtractionPriceBean>(ExtractionPriceBean.class, true, getActivity()) {
                    @Override
                    public void onResponse(ExtractionPriceBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        mList.clear();
                        mList.addAll(response.getData());
                        priceGridAdapter.initVecItemState();
                        priceGridAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void reqUserWxInfo() {
        OkHttpUtils.get()
                .url(HttpConstant.USER_WX_INFO_GET)
                .build()
                .execute(new HttpCallBack<UserWxInfoBean>(UserWxInfoBean.class, true, getActivity()) {
                    @Override
                    public void onResponse(UserWxInfoBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        String wxPayAccountStr;

                        if (response.getData() == null) {
                            wxPayIdStr = "";
                            wxPayAccountStr = "";
                        } else {
                            wxPayIdStr = response.getData().getId();
                            wxPayAccountStr = response.getData().getPhone();
                        }

                        if (wxPayAccountStr != null && !wxPayAccountStr.trim().isEmpty()) {
                            wxPayAccountTv.setText(String.format(getString(R.string.wxpay_account_with_param), wxPayAccountStr));
                        } else {
                            wxPayAccountTv.setText(getString(R.string.please_set_wxpay_account));
                        }
                    }
                });
    }

    public PriceGridAdapter getPriceGridAdapter() {
        return priceGridAdapter;
    }

    public String getWxPayIdStr() {
        return wxPayIdStr;
    }
}
