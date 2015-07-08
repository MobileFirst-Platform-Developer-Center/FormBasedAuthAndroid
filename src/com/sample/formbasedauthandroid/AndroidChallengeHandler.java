/*
 *
    COPYRIGHT LICENSE: This information contains sample code provided in source code form. You may copy, modify, and distribute
    these sample programs in any form without payment to IBM® for the purposes of developing, using, marketing or distributing
    application programs conforming to the application programming interface for the operating platform for which the sample code is written.
    Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES,
    EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY,
    FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT,
    INDIRECT, INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE.
    IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.

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
