package tech.progarden.world.app;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by milan on 1/12/2016.
 */
public class WebAppInterface {
    Context mContext;

    JSONObject jsonObject;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c) {
        mContext = c;
    }

    public void setJsonObject(JSONObject jObj){
        jsonObject = jObj;
    }



    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public String getJson(){
        return jsonObject.toString();
    }
}
