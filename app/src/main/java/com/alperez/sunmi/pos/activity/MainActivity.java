package com.alperez.sunmi.pos.activity;

import android.os.Bundle;
import android.view.View;

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
        nSectionPrinting --;
        if (nSectionPrinting == 0) {
            vProgress.setVisibility(View.GONE);
            //TODO Show result dialog here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    }
}
