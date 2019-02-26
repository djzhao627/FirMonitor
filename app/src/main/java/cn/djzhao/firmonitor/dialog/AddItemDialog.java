package cn.djzhao.firmonitor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.djzhao.firmonitor.R;

public class AddItemDialog extends Dialog {

    private Button cancelBtn;
    private Button addBtn;
    private EditText inputTxt;

    public EditText getInputTxt() {
        return inputTxt;
    }

    private onCancelClickedListener cancelListener;
    private onAddClickedListener addListener;

    public AddItemDialog(Context context) {
        super(context, R.style.dialog_custom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_dialog_layout);
        setCanceledOnTouchOutside(false);

        initView();
        initEvent();
    }

    private void initView() {
        cancelBtn = findViewById(R.id.add_item_cancel_btn);
        addBtn = findViewById(R.id.add_item_add_btn);
        inputTxt = findViewById(R.id.add_item_input_txt);
    }

    private void initEvent() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onClick();
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (addListener != null) {
                    addListener.onClick(inputTxt.getText().toString().trim());
                }
            }
        });

        inputTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    addBtn.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    public void setOnCancelClickedListener(onCancelClickedListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setOnAddClickedListener(onAddClickedListener startListener) {
        this.addListener = startListener;
    }

    public interface onCancelClickedListener {
        public void onClick();
    }

    public interface onAddClickedListener {
        public void onClick(String title);
    }
}
