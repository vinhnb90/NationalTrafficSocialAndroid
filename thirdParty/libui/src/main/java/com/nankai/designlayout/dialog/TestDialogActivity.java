package com.nankai.designlayout.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nankai.designlayout.R;

public class TestDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(0);
        // Build some dialogs for the sample app
        final DialogMaterial.Builder dialogHeader_1 = new DialogMaterial.Builder(this)
                .setIcon(R.drawable.icon)
                .withDialogAnimation(true)
                .setTitle("Awesome!")
                .setContent("DialogMaterial test")
                .setHeaderColor(R.color.colorAccent)
                .onPositive("DialogMaterial", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO
                    }
                });
        dialogHeader_1.show();
//        final DialogMaterial.Builder dialogHeader_2 = new DialogMaterial.Builder(context)
//                .setIcon(R.mipmap.ic_launcher)
//                .withIconAnimation(false)
//                .setContent("DialogMaterial test")
//                .setHeaderColor(R.color.dialog_2)
//                .setPositiveText("DialogMaterial")
//                .onPositive("DialogMaterial", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).build();
//
//        final DialogMaterial.Builder dialogHeader_3 = new DialogMaterial.Builder(context)
//                .setHeaderDrawable(R.drawable.header)
//                .setIcon(R.mipmap.ic_launcher)
//                .withDialogAnimation(true)
//                .setTitle("DialogMaterial")
//                .setContent("DialogMaterial test")
//                .setPositiveText("DialogMaterial")
//                .onPositive("DialogMaterial", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).build();
//
//        final DialogMaterial.Builder dialogHeader_4 = new DialogMaterial.Builder(context)
//                .setHeaderDrawable(R.drawable.header_2)
//                .setTitle("Sweet!")
//                .setContent("DialogMaterial test")
//                .setPositiveText("DialogMaterial")
//                .onPositive("Google Play", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).build();
        //inflate view
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View customView = inflater.inflate(R.layout.custom_view, null);
//        Button dismissButton = (Button) customView.findViewById(R.id.custom_button);


//        final DialogMaterial dialogHeader_5 = new DialogMaterial.Builder(this)
//                .setIcon(R.mipmap.ic_launcher)
//                .withDialogAnimation(true)
//                .setContent("DialogMaterial test")
//                .setHeaderColor(R.color.colorAccent)
//                .setCustomView(customView, 20, 20, 20, 0)
//                .onPositive("DialogMaterial", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).build();
//        dialogHeader_5.show();
//
//        final DialogMaterial dialogHeader_6 = new DialogMaterial.Builder(this)
//                .setStyle(Style.HEADER_WITH_TITLE)
//                .withDialogAnimation(true)
//                .setTitle("DialogMaterial")
//                .setContent("DialogMaterial test")
//                .setHeaderColor(R.color.colorAccent)
//                .setCustomView(customView, 20, 20, 20, 0)
//                .onPositive("DialogMaterial", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).build();
//        dialogHeader_6.show();

//
//        final DialogMaterial.Builder dialogHeader_7 = new DialogMaterial.Builder(context)
//                .setStyle(Style.HEADER_WITH_TITLE)
//                .setHeaderDrawable(R.drawable.header)
//                .withDialogAnimation(true)
//                .withDarkerOverlay(true)
//                .setTitle("DialogMaterial")
//                .setContent("DialogMaterial test")
//                .setPositiveText("DialogMaterial")
//                 .onPositive("DialogMaterial", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).build();
//        dialogHeader_7.show();
    }
}
