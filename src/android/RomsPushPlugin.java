package org.apache.cordova.rmcrc;

import kr.rmcrc.roms.library.Message;
import kr.rmcrc.roms.library.ROMS;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

public class RomsPushPlugin extends CordovaPlugin {

	// define some constants for the suported actions
	public static final String ACTION_INITROMS = "InitROMS";
	public static final String ACTION_PUSHMESSAGE = "PushMessage";
	public static ROMS roms;
	private static String appid;
	public Context context;
	private String reg_id;
	private static String callback;
	private static CordovaWebView gWebView;

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		// the plugin doesn't have direct access to the application context,
		// so you have to get it first
		context = this.cordova.getActivity().getApplicationContext();
		roms = new ROMS(context);

	}

	public Context getContext() {
		return this.context;
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.hasExtra("type")) {
				String type = intent.getStringExtra("type");
				if (type.equals("APPREGISTER")) {
					reg_id = intent.getStringExtra("regid");
					Log.i("reg_id", reg_id);
				} else {
					String message = intent.getStringExtra("msg");
					Log.i("message", message);
					try {
						JSONObject json = new JSONObject();
						json.put("event", "message");
						json.put("msg", message);
						sendJavascript(json);
						JSONObject obj = new JSONObject(message);
						Toast.makeText(getContext(), obj.getString("msg"),Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						// TODO: handle exception
						Log.i("Exception receive", e.getMessage());
					}
					

					 
				}
			}
		}
	};

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		// TODO Auto-generated method stub
		try {

			if (ACTION_INITROMS.equals(action)) {
				JSONObject jo = args.getJSONObject(0);
				appid = jo.getString("appid");
				callback = jo.getString("callback");
				gWebView = this.webView;
				IntentFilter intent = new IntentFilter(appid);
				context.registerReceiver(receiver, intent);
				roms.InitROMS(appid);
				Log.i("appid", appid);

				callbackContext.success();
				return true;
			} else {

				if (ACTION_PUSHMESSAGE.equals(action)) {
					JSONObject jo = args.getJSONObject(0);
					Message msg = new Message(reg_id, jo.getString("dest"), jo.getString("msg"), jo.getString("appid"));
					roms.PushMessage(msg);
					Log.i("send", jo.getString("msg"));
					callbackContext.success();
					return true;
				}
			}
			// We don't have a match, so it must be an invalid action
			callbackContext.error("Invalid Action");
			return false;
		} catch (Exception e) {
			// If we get here, then something horrible has happened
			System.err.println("Exception: " + e.getMessage());
			callbackContext.error(e.getMessage());
			return false;
		}
	}

	public static void sendJavascript(JSONObject _json) {
		String _d = callback + "(" + _json.toString() + ");";
		Log.v("sendback", "sendJavascript: " + _d);

		if (callback != null && gWebView != null) {
			gWebView.sendJavascript(_d);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		context.unregisterReceiver(receiver);
	}

}


