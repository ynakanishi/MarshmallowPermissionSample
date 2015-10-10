package com.example.newpermission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.*;

public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int PERMISSION_GRANTED =  PermissionChecker.PERMISSION_GRANTED;
    private static final int PERMISSION_DENIED =  PermissionChecker.PERMISSION_DENIED;
    private static final int PERMISSION_DENIED_APP_OP = PermissionChecker.PERMISSION_DENIED_APP_OP;

    private static final int SAMPLE_REQUEST_00 = 0;
    private static final int SAMPLE_REQUEST_01 = 1;
    private static final int SAMPLE_REQUEST_02 = 2;
    private static final int SAMPLE_REQUEST_03 = 3;
    private static final int SAMPLE_REQUEST_04 = 4;
    private static final int SAMPLE_REQUEST_05 = 5;

    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listview);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                case 0:
                    checkAndRequestPermission(Manifest.permission.CAMERA, SAMPLE_REQUEST_00);
                    break;
                case 1:
                    checkAndRequestPermissions(new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.SEND_SMS,
                    }, SAMPLE_REQUEST_01);
                    break;
                case 2:
                    checkAndRequestPermission(Manifest.permission.READ_CONTACTS, SAMPLE_REQUEST_02);
                    break;
                case 3:
                    checkAndRequestPermission(Manifest.permission.INTERNET, SAMPLE_REQUEST_03);
                    break;
                case 4:
                    checkAndRequestPermission(Manifest.permission.WAKE_LOCK, SAMPLE_REQUEST_04);
                    break;
                case 5:
                    checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, SAMPLE_REQUEST_05);
                    break;
                case 6:
                    checkPermissionBySupportLibrary(Manifest.permission.CAMERA);
                    break;
                case 7:
                    checkPermissionBySupportLibrary(Manifest.permission.READ_CONTACTS);
                    break;
                case 8:
                    checkPermissionBySupportLibrary(Manifest.permission.INTERNET);
                    break;
                case 9:
                    checkPermissionBySupportLibrary(Manifest.permission.WAKE_LOCK);
                    break;
                case 10:
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                default:
                    break;
                }
            }
        });

        initListAdapter();
    }

    private void initListAdapter() {
        List<Map<String, String>> itemList = new ArrayList<>();

        String[] titleArray = getResources().getStringArray(R.array.sample_titles);
        String[] descriptionArray = getResources().getStringArray(R.array.sample_descriptions);

        int count = titleArray.length;
        for (int i = 0; i < count; i++) {
            String title = titleArray[i];
            String desciption = descriptionArray[i];
            Map<String, String> map = new HashMap<>();
            map.put(TITLE, title);
            map.put(DESCRIPTION, desciption);
            itemList.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, itemList,
                R.layout.custom_list_item_2,
                new String[] {TITLE, DESCRIPTION},
                new int[] {R.id.custom_text1, R.id.custom_text2});

        mListView.setAdapter(adapter);
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nop
            }
        });
        builder.setCancelable(true);

        builder.create().show();
    }

    private boolean hasSelfPermission(Context context, String permission) {
//        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkAndRequestPermission(String permission, int requestCode) {
      if (hasSelfPermission(this, permission)) {
            showAlertDialog(getString(R.string.info_title), getString(R.string.granted));
      } else {
          if (shouldShowRequestPermissionRationale(permission)) {
              showAlertDialog("テスト", "テスト");
          }

          ActivityCompat.requestPermissions(this, new String[]{
                  permission
          }, requestCode);
      }
    }

    private void checkAndRequestPermissions(String[] permissions, int requestCode) {
        boolean allGranted = true;
        for (String permission: permissions) {
            if (!hasSelfPermission(this, permission)) {
                allGranted = false;
            }
        }

        if (allGranted) {
            showAlertDialog(getString(R.string.info_title), getString(R.string.granted));
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    private void checkPermissionBySupportLibrary(String permission) {
        switch(PermissionChecker.checkSelfPermission(this, permission)) {
        case PERMISSION_GRANTED:
            showAlertDialog(getString(R.string.info_title), getString(R.string.granted));
            break;
        case PERMISSION_DENIED:
            showAlertDialog(getString(R.string.error_title), getString(R.string.denied));
            break;
        case PERMISSION_DENIED_APP_OP:
            showAlertDialog(getString(R.string.error_title), getString(R.string.denied_app_op));
            break;
        default:
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int size = permissions.length;

        ArrayList<String> grantedPermissionList = new ArrayList<>();
        ArrayList<String> deniedPermissionList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];

            if (grantResult == PERMISSION_GRANTED) {
                grantedPermissionList.add(permission);
            } else {
                deniedPermissionList.add(permission);
            }
        }

        StringBuilder sb = new StringBuilder();
        if (grantedPermissionList.size() > 0) {
            sb.append(getString(R.string.granted_section)).append("\n");
            for (String grantedPermission: grantedPermissionList) {
                sb.append(grantedPermission).append("\n");
            }
        }

        if (deniedPermissionList.size() > 0) {
            sb.append(getString(R.string.denied_section)).append("\n");
            for (String deniedPermission: deniedPermissionList) {
                sb.append(deniedPermission).append("\n");
            }
        }

        showAlertDialog(getString(R.string.info_title), sb.toString());
    }
}
