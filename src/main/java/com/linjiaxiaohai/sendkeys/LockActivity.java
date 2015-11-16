package com.linjiaxiaohai.sendkeys;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 关屏
 *
 */
public class LockActivity extends AppCompatActivity {

    private DevicePolicyManager policyManager;

    private static final int REQUEST_ADD_ADMIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        lock();
        setContentView(R.layout.activity_lock);
    }

    private void lock() {
        ComponentName componentName = new ComponentName(this, AdminReceiver.class);
        if (policyManager.isAdminActive(componentName)) {
            policyManager.lockNow();
            finish();
        } else {
            activeManage(componentName);
        }
    }

    private void activeManage(ComponentName componentName) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后才能使用锁屏功能哦^.^");
        startActivityForResult(intent, REQUEST_ADD_ADMIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (REQUEST_ADD_ADMIN == requestCode) {
                policyManager.lockNow();
                finish();
            }
        }
    }
}
