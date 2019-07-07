package education.juxin.com.educationpro.ui.activity.mine;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 意见反馈页面
 * The type Yi feedback activity.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvInput;
    private TextView tvContent;
    private EditText etTitle;
    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yi_jian);
        initToolbar(true, R.string.feedback);
        initUI();
    }

    private void initUI() {
        etTitle = findViewById(R.id.et_title);
        tvInput = findViewById(R.id.tv_input);

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                tvInput.setText(String.format(getString(R.string.input_text_count_30), length));
            }
        });

        etContent = findViewById(R.id.et_text);
        tvContent = findViewById(R.id.tv_contents);

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                tvContent.setText(String.format(getString(R.string.input_text_count_300), length));
            }
        });

        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                reqData();
                break;

            default:
                break;
        }
    }

    //意见反馈接口
    private void reqData() {
        String etTitleStr = etTitle.getText().toString();
        String etContentStr = etContent.getText().toString();

        if (StringUtils.isEmpty(etTitleStr)) {
            ToastManager.showShortToast("反馈标题不能为空！");
            return;
        }
        if (StringUtils.isEmpty(etContentStr)) {
            ToastManager.showShortToast("反馈内容不能为空！");
            return;
        }

        OkHttpUtils.post()
                .url(HttpConstant.USER_FEED_BACK)
                .addParams("title", etTitleStr)
                .addParams("content", etContentStr)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        ToastManager.showShortToast("意见反馈成功！");
                        finish();
                    }
                });
    }
}
