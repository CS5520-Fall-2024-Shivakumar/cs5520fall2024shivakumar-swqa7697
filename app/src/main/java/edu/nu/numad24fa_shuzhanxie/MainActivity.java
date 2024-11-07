package edu.nu.numad24fa_shuzhanxie;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.button_about_me).setOnClickListener(v -> aboutMeActivity());
        findViewById(R.id.button_quick_calc).setOnClickListener(v -> calcActivity());
        findViewById(R.id.button_contacts).setOnClickListener(v -> contactsActivity());
    }

    private void aboutMeActivity() {
        Intent intent = new Intent(MainActivity.this, AboutMeActivity.class);
        startActivity(intent);
    }

    private void calcActivity() {
        Intent intent = new Intent(MainActivity.this, CalcActivity.class);
        startActivity(intent);
    }

    private void contactsActivity() {
        Intent intent = new Intent(MainActivity.this, ContactsCollectorActivity.class);
        startActivity(intent);
    }
}