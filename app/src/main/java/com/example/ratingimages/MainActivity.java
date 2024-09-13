package com.example.ratingimages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "AFN6XlSDeZmCfEaIkwB_ShtGWrC1zr0E2VDXf9pX0ME"; // Ваш Access Key

    private ImageView mainImageView;
    private EditText searchEditText;
    private Button searchButton, aboutButton;
    private ImageButton likeImageButton, dislikeImageButton;

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


        aboutButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Автор Кухарчук Илья Николаевич, Rating Images — это минималистичное Android-приложение, которое позволяет искать изображения в интернете и оценивать их.", Toast.LENGTH_SHORT).show();
        });

        likeImageButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Кухарчуку Понравилось изображение!", Toast.LENGTH_SHORT).show();
        });

        dislikeImageButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Кухарчуку Не понравилось изображение!", Toast.LENGTH_SHORT).show();
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

        call.enqueue(new Callback<UnsplashResponse>() {
            @Override
            public void onResponse(Call<UnsplashResponse> call, Response<UnsplashResponse> response) {
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

    // Метод для отображения изображения
    private void displayImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(mainImageView);
    }
}