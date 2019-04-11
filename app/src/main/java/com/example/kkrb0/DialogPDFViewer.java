package com.example.kkrb0;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;

public class DialogPDFViewer extends Dialog {
    PDFView pdfView;
    byte[] decodedString;

    public DialogPDFViewer(Context context, String base64, final DialogPDFViewer.OnDialogPdfViewerListener onDialogPdfViewerListener) {
        super(context);

        setContentView(R.layout.dialog_pdf_viewer);
        findViewById(R.id.dialog_pdf_viewer_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogPdfViewerListener.onCloseClick(DialogPDFViewer.this);
            }
        });

//        findViewById(R.id.dialog_pdf_viewer_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onDialogPdfViewerListener.onAgreeClick(DialogPDFViewer.this);
//            }
//        });

        decodedString = Base64.decode(base64, Base64.DEFAULT);

        pdfView = ((PDFView) findViewById(R.id.pdfView));
        pdfView.fromBytes(decodedString).load();

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onDialogPdfViewerListener.onCloseClick(DialogPDFViewer.this);
                }
                return true;
            }
        });

    }

    public interface OnDialogPdfViewerListener {
        void onAgreeClick(DialogPDFViewer dialogFullEula);

        void onCloseClick(DialogPDFViewer dialogFullEula);
    }
}