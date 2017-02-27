package com.yang.easyhttp.callback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangy on 2017/2/22.
 */
public abstract class EasyJsonCallback implements EasyCallback<JSONObject> {
	@Override
	public void onStart() {

	}

	@Override
	public void onFinish() {

	}

	@Override
	public JSONObject convert(String body) throws JSONException {
		return new JSONObject(body);
	}
}
