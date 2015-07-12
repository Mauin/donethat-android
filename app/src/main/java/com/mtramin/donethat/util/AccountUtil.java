package com.mtramin.donethat.util;

import android.accounts.AccountManager;
import android.content.Context;

import com.mtramin.donethat.R;

/**
 * Created by m.ramin on 7/12/15.
 */
public class AccountUtil {

    public static boolean hasAccount(Context context) {
        AccountManager manager = AccountManager.get(context);
        return manager.getAccountsByType(context.getString(R.string.auth_account_type)).length > 0;
    }
}
