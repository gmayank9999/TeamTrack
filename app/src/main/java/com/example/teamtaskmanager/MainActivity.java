//package com.example.teamtaskmanager;
//
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class MainActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // You can route based on login state later
//        startActivity(new Intent(this, RegisterActivity.class)); // or LoginActivity.class
//        finish(); // so MainActivity doesn't stay in backstack
//    }
//}
package com.example.teamtaskmanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Optionally set a splash layout first
        setContentView(R.layout.activity_main);

        // Delay (e.g., 1 sec splash), then go to LoginActivity
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // prevent user from going back to splash
        }, 1000); // 1000ms = 1 second delay
    }
}
