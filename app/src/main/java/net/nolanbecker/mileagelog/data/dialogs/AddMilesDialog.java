package net.nolanbecker.mileagelog.data.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.nolanbecker.mileagelog.MainActivity;
import net.nolanbecker.mileagelog.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMilesDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public Button btnAdd;
    public EditText editMiles;
    public static TextView txtDatePicker;
    private boolean cancelled;
    public Date date;

    public AddMilesDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cancelled = true;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_dialog);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        editMiles = (EditText) findViewById(R.id.editMiles);
        txtDatePicker = (TextView) findViewById(R.id.txtDatePicker);

        btnAdd.setOnClickListener(this);
        txtDatePicker.setOnClickListener(this);

        setDate(MainActivity.DATE);

        editMiles.setHint(String.valueOf(MainActivity.TOTAL));
        editMiles.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void setDate(String date) {
        txtDatePicker.setText(date);
        try {
            this.date = new SimpleDateFormat("MMM dd yyyy").parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDate(Date date) {
        this.date = date;
        txtDatePicker.setText(new SimpleDateFormat("MMM dd yyy").format(date));
    }

    public int getMiles() {
        String text = editMiles.getText().toString();
        int miles;
        if (text.isEmpty())
            miles = 0;
        else
            miles = Integer.parseInt(text);
        return miles;
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public boolean wasCancelled() {
        return cancelled;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                cancelled = false;
                dismiss();
                break;
            case R.id.txtDatePicker:
                txtDatePicker.requestFocus();
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity);
                datePickerDialog.show();
                datePickerDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!datePickerDialog.wasCancelled())
                            setDate(datePickerDialog.getDate());
                    }
                });
                break;
            default:
                break;
        }
    }
}
