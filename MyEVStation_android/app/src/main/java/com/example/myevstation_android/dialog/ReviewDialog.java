package com.example.myevstation_android.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.myevstation_android.R;
import com.example.myevstation_android.UtilFunction;

import java.util.Objects;

public class ReviewDialog {
    private Context context;
    private AlertDialog alertDialog;
    private ReviewDialog.ButtonListener buttonListener;
    private TextView button;
    private EditText reviewEditText;

    public interface ButtonListener{
        void clickButton(AlertDialog alertDialog, String reviewText);
    }

    public ReviewDialog(Context context, ReviewDialog.ButtonListener buttonListener){
        this.context = context;
        this.buttonListener = buttonListener;

        LayoutInflater li = LayoutInflater.from(context);
        View modalView = li.inflate(R.layout.review_dialog, null);

        GradientDrawable shape =  new GradientDrawable();
        shape.setColor(Color.WHITE);
        int radius = UtilFunction.dpToPx(9);
        float[] rad = {radius, radius,radius, radius, radius, radius,radius, radius};
        shape.setCornerRadii(rad);
        modalView.setBackground(shape);

        reviewEditText = modalView.findViewById(R.id.review_editText);

        button = modalView.findViewById(R.id.button);
        button.setOnClickListener(v-> buttonListener.clickButton(alertDialog,reviewEditText.getText().toString()));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(modalView);

        alertDialog = alertDialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void execute(){
        alertDialog.show();
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setLayout((int) (UtilFunction.getScreenWidth((Activity) context) * 0.87), alertDialog.getWindow().getAttributes().height);
        }
    }
}
