package net.nolanbecker.mileagelog.data.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;

import net.nolanbecker.mileagelog.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatePickerDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public DatePicker datePicker;
    public Button btnDateCancel, btnDateOk;
    private Boolean wasCancelled;

    public DatePickerDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_dialog);

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        btnDateCancel = (Button) findViewById(R.id.btnDateCancel);
        btnDateOk = (Button) findViewById(R.id.btnDateOk);

        wasCancelled = true;

        btnDateOk.setOnClickListener(this);
        btnDateCancel.setOnClickListener(this);

        datePicker.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public String getDate() {
        int dom = datePicker.getDayOfMonth();
        int mon = datePicker.getMonth()+1;
        int year = datePicker.getYear();
        String tmp = year + "-" + mon + "-" + dom;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tmp);
            return new SimpleDateFormat("MMM dd yyyy").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean wasCancelled() {
        return wasCancelled;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDateCancel:
                wasCancelled = true;
                dismiss();
                break;
            case R.id.btnDateOk:
                wasCancelled = false;
                dismiss();
                break;
            default:
                break;
        }
    }
}
