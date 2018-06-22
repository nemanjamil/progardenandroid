package tech.progarden.world.callback_interfaces;

import org.json.JSONObject;

/**
 * Created by 1 on 1/29/2016.
 */
public interface WebRequestCallbackInterface {
    void webRequestSuccess(boolean success, JSONObject[] jsonObjects);
    void webRequestError(String error);
}
