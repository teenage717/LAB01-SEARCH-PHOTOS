package com.example.ratingimages;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.FileOutputStream;
import android.graphics.drawable.BitmapDrawable;
import android.content.Context;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "epC63ZdptUXxo-6ex0UgcmMaJowNwDHgHVwtS7bheCg"; // Ваш Access Key

    private ImageView mainImageView;
    private EditText searchEditText;
    private Button searchButton, aboutButton, downloadbutton;
    private ImageButton likeImageButton, dislikeImageButton;
    private String imageUrl; // Переменная для хранения URL изображения
    private ImageView imageView; // Переменная для ImageView
    private static final String error_image = "your_error_image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        mainImageView = findViewById(R.id.mainImageView);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.SearchButton);
        aboutButton = findViewById(R.id.aboutButton);
        likeImageButton = findViewById(R.id.likeImageButton);
        dislikeImageButton = findViewById(R.id.dislikeImageButton);
        downloadbutton = findViewById(R.id.downloadbutton);

        downloadbutton.setOnClickListener(view -> {
            downloadImage(imageUrl); // Передайте URL изображения для скачивания
        });

        aboutButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Автор Куликович Иван Сергеевич, Rating Images — это минималистичное Android-приложение, которое позволяет искать изображения в интернете и оценивать их.", Toast.LENGTH_SHORT).show();
        });

        likeImageButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Куликовичу Понравилось изображение!", Toast.LENGTH_SHORT).show();
        });

        dislikeImageButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Куликовичу Не понравилось изображение!", Toast.LENGTH_SHORT).show();
        });

        // Обработчик кнопки поиска
        searchButton.setOnClickListener(view -> {
            String query = searchEditText.getText().toString();
            if (!query.isEmpty()) {
                searchImage(query);
            } else {
                Toast.makeText(MainActivity.this, "Введите запрос", Toast.LENGTH_SHORT).show();
            }

        });
    }

    // Метод поиска изображений через Unsplash API
    private void searchImage(String query) {
        UnsplashApiService apiService = RetrofitClient.getRetrofitInstance().create(UnsplashApiService.class);
        Call<UnsplashResponse> call = apiService.searchPhotos(query, CLIENT_ID);
        // создаетя обьект call текущего запроса асинх парс то есть search photos
        call.enqueue(new Callback<UnsplashResponse>() {
            @Override
            public void onResponse(Call<UnsplashResponse> call, Response<UnsplashResponse> response) {
               // response ответ на сетевой запрос
                // Call тип представл асинхр запрос, unsplash тип данных, call обьект предст текущий запрос search photos
                if (response.isSuccessful() && response.body() != null) {
                    // Получение первого изображения из результата
                    if (response.body().results.size() > 0) {
                        UnsplashImage image = response.body().results.get(0);
                        displayImage(image.urls.regular);
                    } else {
                        Toast.makeText(MainActivity.this, "Нет результатов", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка при поиске", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UnsplashResponse> call, Throwable t) {
                Log.e("MainActivity", "Ошибка: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Ошибка соединения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadImage(String imageUrl) {
        Log.d("MainActivity", "Начало загрузки изображения: " + imageUrl);
        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(this)
                .asBitmap() // Указываем тип, чтобы явно загрузить как Bitmap
                .load(imageUrl)
                .apply(requestOptions)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImageToInternalStorage(resource); // Передаем Bitmap для сохранения
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Метод загрузки очищен
                    }
                });
    }


    private void saveImageToInternalStorage(Bitmap bitmapImage) {
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YourDirectoryName");

        if (!storageDir.exists()) {
            storageDir.mkdirs(); // Создание директории, если её нет
        }

        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // Обновление галереи после сохранения изображения
            MediaScannerConnection.scanFile(this, new String[]{imageFile.getAbsolutePath()}, null, null);

            Toast.makeText(MainActivity.this, "Изображение сохранено", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("MainActivity", "Ошибка при сохранении изображения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод для отображения изображения
    private void displayImage(String imageUrl) {
        this.imageUrl = imageUrl; // Обновление переменной imageUrl
        Glide.with(this)
                .load(imageUrl)
                .into(mainImageView);
    }
}