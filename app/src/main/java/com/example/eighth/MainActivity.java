package com.example.eighth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView helloWorld;
    Button sendText;
    Button sendBin;
    Button sendMult;
    Button addPicBtn;
    Button addPicsBtn;
    Uri imageUri = null;
    ArrayList<Uri> imageUris = new ArrayList<>();
    private ShareActionProvider shareActionProvider;
    private ListView lvFiles;
    private String[] file_names = {"image1.png", "image2.png"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helloWorld = findViewById(R.id.helloWorldtext);
        sendText = findViewById(R.id.sendText);
        sendBin = findViewById(R.id.sendBin);
        sendMult = findViewById(R.id.sendMult);
        addPicBtn = findViewById(R.id.addPicBtn);
        addPicsBtn = findViewById(R.id.addPicsBtn);
        lvFiles = (ListView) findViewById(R.id.lv_files);
        lvFiles.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, file_names);
        lvFiles.setAdapter(adapter);

        Intent receiveIntent = getIntent();
        if (receiveIntent.getAction() != Intent.ACTION_MAIN) {
            Uri data = receiveIntent.getClipData().getItemAt(0).getUri();
            if (data != null) {
                helloWorld = findViewById(R.id.helloWorldtext);
                helloWorld.setText(getFileName(data));
            }
        }

        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Тест");
                sendIntent.setType("text/plain");
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(sendIntent, 0);
                Intent sendChooser = Intent.createChooser(sendIntent, "Choose...");
                if (resolveInfos.size() > 0) {
                    startActivity(sendChooser);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет способов!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        sendBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                sendIntent.setType("image/*");
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(sendIntent, 0);
                Intent sendChooser = Intent.createChooser(sendIntent, "Choose...");
                if (resolveInfos.size() > 0) {
                    startActivity(sendChooser);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет способов!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        sendMult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                sendIntent.setType("image/*");
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(sendIntent, 0);
                Intent sendChooser = Intent.createChooser(sendIntent, "Choose...");
                if (resolveInfos.size() > 0) {
                    startActivity(sendChooser);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет способов!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        addPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), 0);
            }
        });
        addPicsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), 1);
            }
        });

    }

    public String getFileName(Uri uri) {
        String result = uri.getPath();
        Pattern pattern = Pattern.compile("com.+?(?=-)");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            result = result.substring(start, end);
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    if (data.getClipData() != null) {
                        int cout = data.getClipData().getItemCount();
                        for (int i = 0; i < cout; i++) {
                            Uri imageurl = data.getClipData().getItemAt(i).getUri();
                            imageUris.add(imageurl);
                        }
                    } else {
                        Uri imageurl = data.getData();
                        imageUris.add(imageurl);
                    }
                    break;
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        MenuItem item_share = menu.findItem(R.id.action_share);
        shareActionProvider = (androidx.appcompat.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item_share);
        Intent sendIntentShare = new Intent(Intent.ACTION_SEND);
        sendIntentShare.putExtra(Intent.EXTRA_STREAM, imageUri);
        sendIntentShare.setType("image/*");
        setShareIntent(sendIntentShare);
        return true;
    }

    private void setShareIntent(Intent intent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(intent);
        }
    }
}