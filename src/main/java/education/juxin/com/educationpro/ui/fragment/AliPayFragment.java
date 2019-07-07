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
import education.juxin.com.educationpro.bean.AlipayBean;
import education.juxin.com.educationpro.bean.ExtractionPriceBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.ui.activity.mine.BindAliPayActivity;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 提现申请 支付宝
 * Created by Administrator on 2018/3/19.
 */
public class AliPayFragment extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private TextView aliPayAccountTv;
    private PriceGridAdapter priceGridAdapter;

    private ArrayList<ExtractionPriceBean.ExtractionPriceData> mList = new ArrayList<>();
    private String aliPayIdStr;

    public static AliPayFragment newInstance(String title) {
        AliPayFragment fragment = new AliPayFragment();
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

        aliPayAccountTv = rootView.findViewById(R.id.pay_account_tv);
        aliPayAccountTv.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        reqPriceList();
        getAlipayData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_account_tv:
                startActivity(new Intent(getActivity(), BindAliPayActivity.class));
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

    // 获取支付宝账号信息
    private void getAlipayData() {
        OkHttpUtils.post()
                .url(HttpConstant.GET_AIPAY_ACCOUNT)
                .build()
                .execute(new HttpCallBack<AlipayBean>(AlipayBean.class, true, getActivity()) {
                    @Override
                    public void onResponse(AlipayBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        String aliPayAccountStr;

                        if (response.getData() == null) {
                            aliPayIdStr = "";
                            aliPayAccountStr = "";
                        } else {
                            aliPayIdStr = response.getData().getId();
                            aliPayAccountStr = response.getData().getAccount();
                        }

                        if (aliPayAccountStr != null && !aliPayAccountStr.trim().isEmpty()) {
                            aliPayAccountTv.setText(String.format(getString(R.string.alipay_account_with_param), aliPayAccountStr));
                        } else {
                            aliPayAccountTv.setText(getString(R.string.please_set_alipay_account));
                        }
                    }
                });
    }

    public PriceGridAdapter getPriceGridAdapter() {
        return priceGridAdapter;
    }

    public String getAliPayIdStr() {
        return aliPayIdStr;
    }
}
