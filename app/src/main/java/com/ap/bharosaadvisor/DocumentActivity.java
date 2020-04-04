package com.ap.bharosaadvisor;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class DocumentActivity extends AppCompatActivity implements DownloadFile.Listener
{
    String url;
    RemotePDFViewPager remotePDFViewPager;
    ProgressDialog dialog;
    PDFPagerAdapter adapter;
    boolean hasErrors = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        url = getIntent().getStringExtra("url");
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading LearnDocument...");
        dialog.show();

        remotePDFViewPager = new RemotePDFViewPager(this, url, this);
    }

    @Override
    public void onSuccess(String url, String destinationPath)
    {
        dialog.dismiss();
        if (hasErrors)
            return;

        Toast.makeText(this,
                R.string.document_instruction, Toast.LENGTH_LONG)
                .show();
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter);
        setContentView(remotePDFViewPager);
    }

    @Override
    public void onFailure(Exception e)
    {
        dialog.dismiss();
        hasErrors = true;
        Toast.makeText(this, R.string.error_document, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (adapter != null)
            adapter.close();
        finish();
    }

    @Override
    public void onProgressUpdate(int progress, int total)
    {
    }
}
