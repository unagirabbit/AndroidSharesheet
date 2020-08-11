package com.example.mylibrary;

import com.unity3d.player.UnityPlayer;
import android.os.Build;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.ComponentName;
import androidx.core.content.FileProvider;
import android.net.Uri;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

public class SocialShare
{
    /**
     * シェアウィンドウで特定のパッケージのみを開く
     * 除外の必要がなければstartChooserを使えば良い
     * @param title シェア画面の説明メッセージ
     * @param message 投稿メッセージ
     * @param type コンテンツタイプ
     * @param filePath 投稿するファイルのパス
     * @param ignorePackage 除外するActivity名
     */
    public static void send(String title, String message, String type, String filePath, String ignorePackage)
    {
        Activity currentActivity = UnityPlayer.currentActivity;
        Context context = currentActivity.getApplicationContext();

        Intent targetIntent = new Intent(Intent.ACTION_SEND);
        // コンテンツのType
        targetIntent.setType(type);
        // 投稿するメッセージ
        targetIntent.putExtra(Intent.EXTRA_TEXT, message);

        if(!filePath.isEmpty())
        {
            // API24以降はFileProviderを利用する
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            {
                File file = new File(filePath);
                Uri uri = Uri.fromFile(file);
                targetIntent.putExtra(Intent.EXTRA_STREAM, uri);
            }
            else
            {
                File file = new File(filePath);
                Uri uri = FileProvider.getUriForFile(context, "com.DefaultCompany.AndroidSharesheet.fileprovider", file);
                targetIntent.putExtra(Intent.EXTRA_STREAM, uri);
            }
        }

        // targetIntentで指定したTypeと一致するActivityを取得する
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(targetIntent, PackageManager.MATCH_ALL);
        ArrayList<Intent> shareIntentList = new ArrayList<>();
        ArrayList<ComponentName> comIntentList = new ArrayList<>();
        for(ResolveInfo info : resolveInfoList)
        {
            ComponentName componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
            // info.activityInfo.nameかinfo.activityInfo.nameでフィルターすると良い
            if(ignorePackage.contains(info.activityInfo.name))
            {
                // 24以上は除外するIntentを指定する
                comIntentList.add(componentName);
            }
            else
            {
                // 23以下は追加するIntentを指定する
                Intent shareIntent = new Intent(targetIntent);
                shareIntent.setComponent(componentName);
                shareIntentList.add(shareIntent);
            }
        }

        // 23以下はtypeに対応した動作するActivityを渡して生成しないといけない
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            Intent chooser = Intent.createChooser(shareIntentList.remove(0), title);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentList.toArray(new Parcelable[0]));
            currentActivity.startActivity(chooser);
        }
        else
        {
            Intent chooser = Intent.createChooser(targetIntent, title);
            chooser.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, comIntentList.toArray(new Parcelable[0]));
            currentActivity.startActivity(chooser);
        }
    }
}
