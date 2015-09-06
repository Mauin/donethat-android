package com.mtramin.donethat.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.mtramin.donethat.R;
import com.mtramin.donethat.auth.TwitterAuthenticationService;
import com.mtramin.donethat.data.model.twitter.TwitterUser;
import com.mtramin.donethat.ui.MainActivity;

/**
 * Created by m.ramin on 7/12/15.
 */
public class AccountUtil {

    public static boolean hasAccount(Context context) {
        return getAccounts(context).length > 0;
    }

    public static Account[] getAccounts(Context context) {
        AccountManager manager = AccountManager.get(context);
        return manager.getAccountsByType(context.getString(R.string.auth_account_type));
    }

    public static Account getAccount(Context context) {
        return getAccounts(context)[0];
    }

    public static String getUserData(Context context, String key) {
        AccountManager manager = AccountManager.get(context);
        return manager.getUserData(getAccount(context), key);
    }

    public static TwitterUser getUser(Context context) {
        String accountName = AccountUtil.getUserData(context, TwitterAuthenticationService.ACCOUNT_USERNAME);
        String accountHandle = "@" + AccountUtil.getUserData(context, TwitterAuthenticationService.ACCOUNT_SCREEN_NAME);
        String backgroundUrl = AccountUtil.getUserData(context, TwitterAuthenticationService.ACCOUNT_BACKGOROUND_IMAGE_URL);
        String avatarUrl = AccountUtil.getUserData(context, TwitterAuthenticationService.ACCOUNT_PROFILE_IMAGE_URL);

        return new TwitterUser(accountName, accountHandle, backgroundUrl, avatarUrl);
    }
}
