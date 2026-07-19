package com.berrys.crochetinventory.ui.orders;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.berrys.crochetinventory.R;
import java.io.File;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Image");
        }

        ImageView imageView = findViewById(R.id.iv_fullscreen);
        String imagePath = getIntent().getStringExtra("image_path");

        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Glide.with(this).load(imgFile).into(imageView);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
