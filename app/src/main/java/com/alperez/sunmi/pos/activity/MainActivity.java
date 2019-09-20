package com.alperez.sunmi.pos.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alperez.sunmi.pos.R;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PrintResultCallback;
import com.alperez.sunmi.pos.receiptengine.print.SunmiBtPrinter;
import com.alperez.sunmi.pos.receiptengine.template.ITemplateItem;
import com.alperez.sunmi.pos.receiptengine.template.ReceiptTemplate;
import com.alperez.sunmi.pos.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ReceiptTemplate receiptTemplate;

    private View vProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vProgress = findViewById(R.id.progress);
        vProgress.setVisibility(View.GONE);

        try {
            String templateJson = FileUtils.loadAsset(this, "receipt_template.json");
            JSONObject jTemplate = new JSONObject(templateJson);

            String dataJson = FileUtils.loadAsset(this, "receipt_data.json");
            JSONObject jData = new JSONObject(dataJson);

            ParameterValueMapper mapper = ParameterValueMapper.getInstance(jData);
            receiptTemplate = ReceiptTemplate.fromJson(jTemplate, mapper);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            finish();
            return;
        }

        findViewById(R.id.action_print).setOnClickListener(this::onPrint);
    }

    @Override
    protected void onDestroy() {
        SunmiBtPrinter.getInstance(this).shutdown();
        super.onDestroy();
    }

    private void onPrint(View v) {
        resetPrintTracker();
        SunmiBtPrinter printer = SunmiBtPrinter.getInstance(this);
        for (ITemplateItem tItem : receiptTemplate.getTemplateItems()) {
            startPrintSection();
            printer.print(tItem.getPrinterRawData(), new PrintResultCallback() {
                @Override
                public void onComplete() {
                    endPrintSection();
                }

                @Override
                public void onError(@NonNull String reason, @Nullable Throwable t) {
                    errorText = reason;
                    error = t;
                    endPrintSection();
                }
            });
        }
    }

    private int nSectionPrinting;
    private String errorText;
    private Throwable error;

    private void resetPrintTracker() {
        nSectionPrinting = 0;
        errorText = null;
        error = null;
        vProgress.setVisibility(View.GONE);
    }

    private void startPrintSection() {
        if (nSectionPrinting == 0) {
            vProgress.setVisibility(View.VISIBLE);
            errorText = null;
            error = null;
        }
        nSectionPrinting ++;
    }

    private void endPrintSection() {
        if (nSectionPrinting > 0) {
            nSectionPrinting --;
            if (nSectionPrinting == 0) {
                vProgress.setVisibility(View.GONE);
                if (errorText == null) {
                    new AlertDialog.Builder(this).setMessage(R.string.print_ok_dialog_message).setPositiveButton(android.R.string.ok, (dialog, which) -> finish()).show();
                } else {
                    showPrintError(getString(R.string.err_print_dialog_message, errorText), (error == null) ? null : String.format("(%s: %s)", error.getClass().getSimpleName(), error.getMessage()));
                }
            }
        }
    }

    private void showPrintError(@NonNull CharSequence msgPrimary, @Nullable CharSequence msgSecondary) {
        View content = getLayoutInflater().inflate(R.layout.dialog_print_error, new FrameLayout(this), false);
        ((TextView) content.findViewById(R.id.txt_primary)).setText(msgPrimary);
        if(TextUtils.isEmpty(msgSecondary)) {
            content.findViewById(R.id.txt_secondary).setVisibility(View.GONE);
        } else {
            ((TextView) content.findViewById(R.id.txt_secondary)).setText(msgSecondary);
        }
        AlertDialog dialog = new AlertDialog.Builder(this).setPositiveButton(android.R.string.ok, null).create();
        dialog.setView(content);
        dialog.show();
    }
}
