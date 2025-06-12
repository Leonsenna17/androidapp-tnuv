package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SensorsActivity extends BaseActivity {

    private EditText etInput;
    private Button btnSave;
    private String localVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        setupToolbar(false); // false = not home activity
        setupDrawer();

        etInput = findViewById(R.id.etInput);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localVariable = etInput.getText().toString().trim();

                if(localVariable.isEmpty()) {
                    Toast.makeText(SensorsActivity.this, "Prosim vnesi nekaj!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SensorsActivity.this, "Vnos shranjen: " + localVariable, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SensorsActivity.this, MainActivity.class);

                    intent.putExtra("dodana_oznaka", localVariable);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
