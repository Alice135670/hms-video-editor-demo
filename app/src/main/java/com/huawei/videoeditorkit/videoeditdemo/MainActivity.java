/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.videoeditorkit.videoeditdemo;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.videoeditor.ui.api.MediaApplication;
import com.huawei.hms.videoeditor.ui.api.MediaExportCallBack;
import com.huawei.hms.videoeditor.ui.api.MediaInfo;
import com.huawei.videoeditorkit.videoeditdemo.util.PermissionUtils;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUESTS = 1;
    private LinearLayout startEdit;
    private ImageView mSetting;
    private Context mContext;
    private final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        initSetting();
        initView();
        initData();
        initEvent();
    }

    private void requestPermission() {
        PermissionUtils.checkManyPermissions(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                startUIActivity();
            }

            @Override
            public void onUserHasReject(String... permission) {
                PermissionUtils.requestManyPermissions(mContext, PERMISSIONS, PERMISSION_REQUESTS);
            }

            @Override
            public void onUserRejectAndDontAsk(String... permission) {
                PermissionUtils.requestManyPermissions(mContext, PERMISSIONS, PERMISSION_REQUESTS);
            }
        });
    }

    private void initSetting() {
        MediaApplication.getInstance().setLicenseId("License ID"); // Unique ID generated when the VideoEdit Kit is integrated.
        //Setting the APIKey of an Application
        MediaApplication.getInstance().setApiKey("API_KEY");
        //Setting the Application Token
        // MediaApplication.getInstance().setAccessToken("set your Token");
        //Setting Exported Callbacks
        MediaApplication.getInstance().setOnMediaExportCallBack(CALL_BACK);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initEvent() {
        startEdit.setOnClickListener(v -> requestPermission());

        mSetting.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, SettingActivity.class)));
    }

    private void initData() {
    }

    private void initView() {
        startEdit = findViewById(R.id.start_edit);
        mSetting = findViewById(R.id.setting);
    }

    //The default UI is displayed.
    /**
     * Startup mode (START_MODE_IMPORT_FROM_MEDIA): Startup by importing videos or images.
     */
    private void startUIActivity() {
        //VideoEditorLaunchOption build = new VideoEditorLaunchOption
        // .Builder()
        // .setStartMode(START_MODE_IMPORT_FROM_MEDIA)
        // .build();
        //The default startup mode is (START_MODE_IMPORT_FROM_MEDIA) when the option parameter is set to null.
        MediaApplication.getInstance().launchEditorActivity(this, null);
    }

    //Export interface callback
    private static final MediaExportCallBack CALL_BACK = new MediaExportCallBack() {
        @Override
        public void onMediaExportSuccess(MediaInfo mediaInfo) {
            String mediaPath = mediaInfo.getMediaPath();
            Log.i(TAG, "The current video export path is" + mediaPath);
        }

        @Override
        public void onMediaExportFailed(int errorCode) {
            Log.d(TAG, "errorCode" + errorCode);
        }
    };

    /**
     * Display Go to App Settings Dialog
     */
    private void showToAppSettingDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.permission_tips))
                .setPositiveButton(getString(R.string.setting), (dialog, which) -> PermissionUtils.toAppSetting(mContext))
                .setNegativeButton(getString(R.string.cancels), null).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUESTS) {
            PermissionUtils.onRequestMorePermissionsResult(mContext, PERMISSIONS,
                    new PermissionUtils.PermissionCheckCallBack() {
                        @Override
                        public void onHasPermission() {
                            startUIActivity();
                        }

                        @Override
                        public void onUserHasReject(String... permission) {

                        }

                        @Override
                        public void onUserRejectAndDontAsk(String... permission) {
                            showToAppSettingDialog();
                        }
                    });
        }
    }
}