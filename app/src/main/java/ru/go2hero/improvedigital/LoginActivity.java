package ru.go2hero.improvedigital;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 101;

    private Bitmap userImageBitmap;
    private Uri imageUri;
    private String userEmail = "";
    private String userPhone;
    private String userPassword = "";

    private String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[a-z]{2,4}$";
    private String phoneNumberMask = "+7[000][000][00][00]";
    private String passwordExpression = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getImageInit();
        infoFieldsInit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {

            Bundle extras = data.getExtras();
            userImageBitmap = (Bitmap) extras.get("data");
            imageUri = getImageUri(getBaseContext(), userImageBitmap);

            CircleImageView profileImage = findViewById(R.id.profile_image);
            profileImage.setImageBitmap(userImageBitmap);

            ImageView photoIcon = findViewById(R.id.photo_icon);
            photoIcon.setVisibility(View.INVISIBLE);

        }
    }

    private void getImageInit() {
        CircleImageView profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                } else {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
    }

    private void infoFieldsInit() {
        final TextInputEditText email = findViewById(R.id.login_email_text);
        TextInputEditText phone = findViewById(R.id.login_phone_text);
        final TextInputEditText password = findViewById(R.id.login_password_text);
        Button button = findViewById(R.id.send_to_preview_button);

        MaskedTextChangedListener phoneListener = MaskedTextChangedListener.Companion.installOn(
                phone, phoneNumberMask,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(
                            boolean maskFilled, @NotNull String extractedValue, @NotNull String formattedValue) {
                        if (maskFilled) {
                            userPhone = "+7" + extractedValue;
                        }
                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = email.getText().toString().trim();
                userPassword = password.getText().toString().trim();

                if (userImageBitmap == null) {
                    Toast.makeText(getBaseContext(), "You need to take a picture",
                            Toast.LENGTH_SHORT).show();
                } else if (!isValid(userEmail, emailExpression, false)) {
                    Toast.makeText(getBaseContext(), "Wrong email format",
                            Toast.LENGTH_SHORT).show();
                } else if (userPhone == null) {
                    Toast.makeText(getBaseContext(), "Wrong phone format",
                            Toast.LENGTH_SHORT).show();
                } else if (!isValid(userPassword, passwordExpression, true)) {
                    Toast.makeText(getBaseContext(),
                            "Password must be at least 6 characters long, contain letters and numbers",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getBaseContext(), UserActivity.class);
                    intent.putExtra("image", userImageBitmap);
                    intent.putExtra("imageUri", imageUri);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("phone", userPhone);
                    intent.putExtra("password", userPassword);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getBaseContext().startActivity(intent);
                }
            }
        });
    }

    private boolean isValid(String string, String expression, boolean isCapsAllow) {
        Pattern pattern;
        if (isCapsAllow) {
            pattern = Pattern.compile(expression);
        } else {
            pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        }
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }

}
