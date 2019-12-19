package ru.go2hero.improvedigital;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initViewElements();
    }

    private void initViewElements() {
        Toolbar toolbar = findViewById(R.id.user_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final ImageView imageView = findViewById(R.id.user_image);
        final TextView emailText = findViewById(R.id.mail_text);
        final TextView phoneText = findViewById(R.id.phone_text);
        TextView passwordText = findViewById(R.id.password_text);

        final Bitmap image = getIntent().getParcelableExtra("image");
        imageView.setImageBitmap(image);
        emailText.setText(getIntent().getStringExtra("email"));
        phoneText.setText(getIntent().getStringExtra("phone"));
        passwordText.setText(getIntent().getStringExtra("password"));


        Button button = findViewById(R.id.send_by_email_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/image");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailText.getText().toString()});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name)
                        + ": данные анкеты");
                intent.putExtra(Intent.EXTRA_TEXT, "Email: " + emailText.getText()
                        + "\n\rPhone: " + phoneText.getText() + "\n\r");
                intent.putExtra(Intent.EXTRA_STREAM, getIntent().getParcelableExtra("imageUri"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}
