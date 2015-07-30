/**
* Copyright 2015 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.sample.formbasedauthandroid;

import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.challengehandler.ChallengeHandler;

public class AndroidChallengeHandler extends ChallengeHandler{
	private Activity parentActivity;
	private WLResponse cachedResponse; 


	public AndroidChallengeHandler(Activity activity, String realm) {
		super(realm);
		parentActivity = activity;
	}

	@Override
	public void onFailure(WLFailResponse response) {
		submitFailure(response);
	}

	@Override
	public void onSuccess(WLResponse response) {
		submitSuccess(response);
	}

	@Override
	public boolean isCustomResponse(WLResponse response) {
		if (response == null || response.getResponseText() == null || 
				response.getResponseText().indexOf("j_security_check") == -1) { 
			return false; 
		} 
		return true; 
	}

	@Override
	public void handleChallenge(WLResponse response){
		if (!isCustomResponse(response)) {
			submitSuccess(response);
		} else {
			cachedResponse = response;
			Intent login = new Intent(parentActivity, LoginFormBasedAuth.class);
			parentActivity.startActivityForResult(login, 1);
		}
		
	}
	
	
	public void submitLogin(int resultCode, String userName, String password, boolean back){
		if (resultCode != Activity.RESULT_OK || back) {
			submitFailure(cachedResponse);
		} else {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("j_username", userName);
			params.put("j_password", password);
			submitLoginForm("/j_security_check", params, null, 0, "post");
		}
	}
	



}
