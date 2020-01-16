package dr.mtr.passenger.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.components.FontAwesomeIcon;
import dr.mtr.passenger.utils.GlobalUtil;
import dr.mtr.passenger.utils.ObjectUtil;
import dr.mtr.passenger.utils.UIUtil;

public class GlobalAlertDialog extends Dialog implements View.OnClickListener {

    protected LinearLayout cancelBtnLayout, optionalBtnLayout;
    private Button okButton, cancelButton, optionalBtn;
    private TextView alertTextView;
    private String message = "";
    private boolean isConfirmation, isWarning, isOptionalBtnVisible;
    private int layoutId = R.layout.dialog_alert_custom;
    private String positiveBtnTxt, negativeBtnTxt;
    private TableRow headerContainer;

    public GlobalAlertDialog(Context context) {
        super(context);
    }

    public GlobalAlertDialog(Context context, boolean isConfirmation, boolean isWarning) {
        super(context);
        this.isConfirmation = isConfirmation;
        this.isWarning = isWarning;
    }

    public GlobalAlertDialog(Context context, boolean isConfirmation, boolean isWarning, boolean isOptionalBtnVisible) {
        super(context);
        this.isConfirmation = isConfirmation;
        this.isWarning = isWarning;
        this.isOptionalBtnVisible = isOptionalBtnVisible;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getAttributes().windowAnimations = R.style.ScaleFromCenter;
        this.setContentView(this.layoutID());
        this.setCanceledOnTouchOutside(false);
        this.loadHeader();
        this.loadBody();
        this.loadFooter();
        Resources resources = getContext().getResources();
        int scrWidth = resources.getConfiguration().screenWidthDp;
        if (GlobalUtil.getDipFromPixel(this.getContext(), scrWidth) <= 700) {
            this.getWindow().setLayout((GlobalUtil.getDipFromPixel(this.getContext(), scrWidth) - 50), LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            this.getWindow().setLayout((3 * GlobalUtil.getDipFromPixel(this.getContext(), scrWidth)) / 4, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void show(@StringRes int message) {
        this.show(getContext().getString(message));
    }

    public void show(String message) {
        this.message = message;
        super.show();
    }

    //TODO load the header part of dialog a/c to user choice
    protected void loadHeader() {
        float dialogRadius = UIUtil.getDimension(R.dimen._3sdp);
        UIUtil.setBackgroundRound(this.findViewById(R.id.DialogNLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        this.headerContainer = this.findViewById(R.id.tableRow1);
        FontAwesomeIcon titleIcon = findViewById(R.id.titleIcon);
        titleIcon.setText(this.getHeaderIcon());
        this.headerContainer.setBackgroundColor(UIUtil.getColor(this.getHeaderIconColor()));
    }

    //TODO load the body part of dialog a/c to user choice
    protected void loadBody() {
        this.alertTextView = this.findViewById(R.id.txt_alert_message);
        this.alertTextView.setText(UIUtil.fromHtml(message));
    }

    //TODO load the footer part of dialog a/c to user choice
    protected void loadFooter() {
        float dialogRadius = UIUtil.getDimension(R.dimen._3sdp);
        UIUtil.setBackgroundRound(this.findViewById(R.id.pop_up_footer), android.R.color.white, new float[]{0, 0, 0, 0, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        this.cancelBtnLayout = this.findViewById(R.id.btn_cancel_layout);
        this.optionalBtnLayout = this.findViewById(R.id.optionalBtn_layout);
        this.okButton = this.findViewById(R.id.submitButton);
        this.cancelButton = this.findViewById(R.id.cancelButton);
        this.optionalBtn = this.findViewById(R.id.disapprove);
        if (ObjectUtil.isNonEmptyStr(positiveBtnTxt)) {
            this.okButton.setText(positiveBtnTxt);
        }
        if (ObjectUtil.isNonEmptyStr(negativeBtnTxt)) {
            this.cancelButton.setText(negativeBtnTxt);
        }
        this.okButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
        final float buttonRadius = UIUtil.getDimension(R.dimen._50sdp);
        UIUtil.setBackgroundRect(okButton, R.color.dark_gray, buttonRadius);
        if (this.isConfirmation /*|| this.isOptionalBtnVisible*/) {
            UIUtil.setBackgroundRect(okButton, R.color.colorDarkSkyBlue, buttonRadius);
            UIUtil.setBackgroundRect(cancelBtnLayout, android.R.color.transparent, buttonRadius);
            cancelBtnLayout.setVisibility(View.VISIBLE);
            cancelButton.setTextColor(UIUtil.getColor(R.color.blackMedium));
        }
        if (this.isOptionalBtnVisible) {
            this.findViewById(R.id.tableRow3).setVisibility(View.GONE);
            optionalBtnLayout.setVisibility(View.VISIBLE);
            optionalBtn.setTextColor(UIUtil.getColor(R.color.blackMedium));
            optionalBtn.setOnClickListener(this);
            //            cancelButton.setTextColor(UIUtil.getColor(android.R.color.white));
            //            UIUtil.setBackgroundRect(cancelButton, R.color.gray_default, buttonRadius);
        }
    }

    public void setPositiveBtnTxt(String positiveBtnTxt) {
        this.positiveBtnTxt = positiveBtnTxt;
    }

    public void setNegativeBtnTxt(String negativeBtnTxt) {
        this.negativeBtnTxt = negativeBtnTxt;
    }

    protected int layoutID() {
        return layoutId;
    }

    private String getHeaderIcon() {
        if (isConfirmation)
            return ApplicationHelper.application().getString(R.string.icon_check_cricle);
        else if (isWarning) {
            return ApplicationHelper.application().getString(R.string.icon_exclamation_triangle);
        }
        return ApplicationHelper.application().getString(R.string.icon_info_sign);
    }

    private @ColorRes
    int getHeaderIconColor() {
        if (isConfirmation)
            return R.color.colorDarkSkyBlue;
        else if (isWarning) {
            return R.color.darkRed;
        }
        return R.color.orangeLight;
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        if (isConfirmation || isOptionalBtnVisible) {
            if (v.getId() == okButton.getId()) {
                this.onConfirmation();
            } else if (v.getId() == cancelButton.getId()) {
                this.onNegativeConfirmation();
                //                if (isConfirmation) {
                //                    onCancelConfirmation();
                //                } else {
                //                    onNegativeConfirmation();
                //                }
            } else if (v.getId() == optionalBtn.getId()) {
                this.onCancelConfirmation();
            }
        } else if (isWarning) {
            this.onWarningDismiss();
        } else {
            this.onDefault();
        }
    }

    public void onConfirmation() {
    }

    public void onCancelConfirmation() {
    }

    public void onNegativeConfirmation() {
    }

    public void onWarningDismiss() {
    }

    public void onDefault() {
    }

}
