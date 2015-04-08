package com.forif.cordova.plugin.kakao;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kakao.KakaoLink;
import com.kakao.KakaoLinkParseException;
import com.kakao.KakaoTalkLinkMessageBuilder;

/**
 * Created by jbpark on 2015-04-06.
 */
public class KakaoPlugin extends CordovaPlugin {
	private final static String LOG_TAG = "KakaoPlugin";
	private final static String ACTION_LINK = "link";
	
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if(data.length() < 1){
        	callbackContext.error("argument is empty!");
        	LOG.e(LOG_TAG, "argument is empty!");
        	return false;
        }
        
        LOG.d(LOG_TAG, "action:", action);
        LOG.d(LOG_TAG, "data:", data);
        try{
            JSONObject arg = data.getJSONObject(0);
            if(ACTION_LINK.equals(action)){
            	link(arg);
            	callbackContext.success("success");
            	return true;
            }else{
            	throw new IllegalArgumentException("action is empty");
            }
		} catch (Exception e){
            callbackContext.error(e.getMessage());
            LOG.e(LOG_TAG, e.getMessage(), e);
            return false;
		}
    }
    
    private void link(JSONObject arg) throws JSONException, KakaoLinkParseException{
        String text = null;
        JSONObject img = null, appLink = null, webLink = null;
        if(!arg.isNull("text"))
        	text = arg.getString("text");
        if(!arg.isNull("image"))
        	img = arg.getJSONObject("image");
        if(!arg.isNull("appLink"))
        	appLink = arg.getJSONObject("appLink");
        if(!arg.isNull("webLink"))
        	webLink = arg.getJSONObject("webLink");

        KakaoLink kakaoLink = KakaoLink.getKakaoLink(cordova.getActivity());
        KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
        if(text != null && !"".equals(text)){
        	kakaoTalkLinkMessageBuilder.addText(text);
        	LOG.d(LOG_TAG, "addText:" + text);
        }
        if(img !=null ){
        	String src = img.getString("src");
        	int width = img.getInt("width");
        	int height = img.getInt("height");
        	kakaoTalkLinkMessageBuilder.addImage(src, width, height);
        	LOG.d(LOG_TAG, "addImage:" + src);
        }
        if(appLink != null){
        	String textBtn = appLink.getString("text");
        	kakaoTalkLinkMessageBuilder.addAppButton(textBtn);
        	LOG.d(LOG_TAG, "addAppButton:" + textBtn);
        }
        if(webLink != null){
        	String textBtn = webLink.getString("text");
        	String url = webLink.getString("url");
        	kakaoTalkLinkMessageBuilder.addWebButton(textBtn, url);
        	LOG.d(LOG_TAG, "addWebButton:" + textBtn);
        }
        
        String linkMessage = kakaoTalkLinkMessageBuilder.build();
        if(linkMessage == null || "".equals(linkMessage)){
        	throw new IllegalArgumentException("linkMessage is null!");
        }   
        kakaoLink.sendMessage(linkMessage, cordova.getActivity());
    }
}
