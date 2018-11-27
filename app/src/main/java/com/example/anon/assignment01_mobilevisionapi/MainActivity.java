package com.example.anon.assignment01_mobilevisionapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button buttonDetect;
    Button buttonUndetect;
    Button buttonChoosePic;

    // bitmap default and temp
    Bitmap bitmap_default;
    Bitmap bitmap_temp;

    // code for intent to send
    int SELECT_IMAGE_CODE = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // gan id
        imageView = findViewById(R.id.imageView);
        buttonDetect = findViewById(R.id.buttonDetect);
        buttonUndetect = findViewById(R.id.buttonUndetect);
        buttonChoosePic = findViewById(R.id.buttonChoosePic);

        // anh default
        setDefaultImage();

        // dung Paint ve khuon mat
        final Paint paint = getDrawFacePaint();

        // cai dat button detect
        buttonDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap_default == null)
                    return;

                // tao canvas dung bitmap_temp
                // ve bitmap_default tren canvas
                bitmap_temp = Bitmap.createBitmap(bitmap_default.getWidth(),
                        bitmap_default.getHeight(),
                        Bitmap.Config.RGB_565);
                final Canvas canvas = new Canvas(bitmap_temp);
                canvas.drawBitmap(bitmap_default, 0, 0, null);

                // create FaceDetector
                FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setMode(FaceDetector.FAST_MODE)
                        .build();

                // gms not installed
                if (!faceDetector.isOperational()) {
                    Toast.makeText(MainActivity.this,
                            "FaceDetector can not be set up on your device :(",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // detect face
                Frame frame = new Frame.Builder().setBitmap(bitmap_default).build();
                SparseArray<Face> faces = faceDetector.detect(frame);
                for (int i = 0; i < faces.size(); ++i) {
                    Face face = faces.valueAt(i);
                    float mostLeft_x = face.getPosition().x;
                    float mostLeft_y = face.getPosition().y;
                    float width = face.getWidth();
                    float height = face.getHeight();
                    RectF rect = new RectF(mostLeft_x, mostLeft_y,
                            mostLeft_x + width, mostLeft_y + height);
                    // ve face tren canvas
                    canvas.drawRect(rect, paint);
                }

                // thay bitmap_default bang bitmap_temp trong imageView
                imageView.setImageBitmap(bitmap_temp);
                faceDetector.release();
            }
        });

        // cai dat button undetect
        buttonUndetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap_default == null)
                    return;

                imageView.setImageBitmap(bitmap_default);
            }
        });

        // cai dat button choose picture
        buttonChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // su dung intent de goi gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Choose picture"),
                        SELECT_IMAGE_CODE);
            }
        });
    }

    // tao paint de ve khung guong mat
    private Paint getDrawFacePaint() {
        final Paint paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        // lay anh tu gallery
                        bitmap_default = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(),
                                data.getData());
                        imageView.setImageBitmap(bitmap_default);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // anh mac dinh la duong tang
    private void setDefaultImage() {
        bitmap_default = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.tank);
        imageView.setImageBitmap(bitmap_default);
    }


}
