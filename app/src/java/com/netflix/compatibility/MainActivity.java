
package com.netflix.compatibility;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.rgb(15, 15, 20));

        TextView title = new TextView(this);
        title.setText("NETFLIX COMPATIBILITY");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);

        TextView description = new TextView(this);
        description.setText(
            "Diagnostico Android TV\n\n" +
            "Aplicativo de verificacao local\n" +
            "de compatibilidade do dispositivo."
        );
        description.setTextColor(Color.LTGRAY);
        description.setTextSize(18);
        description.setGravity(Gravity.CENTER);
        description.setPadding(0, 30, 0, 30);

        layout.addView(title);
        layout.addView(description);

        setContentView(layout);
    }
}

