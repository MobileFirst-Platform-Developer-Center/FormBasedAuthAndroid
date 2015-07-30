package com.sample.formbasedauthandroid;


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
import java.net.URI;
import java.net.URISyntaxException;

import com.sample.formbasedauthandroid.R;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLResourceRequest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainFormBasedAuth extends Activity {

	private static TextView mainText = null;
	private Button invokeBtn, logoutBtn;	
	private static MainFormBasedAuth otherThis;

	private AndroidChallengeHandler challengeHandler;
	private String realm = "SampleAppRealm";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_form_based_auth);
		getActionBar().setTitle("Form Based Authentication");
		
		mainText = (TextView) findViewById(R.id.result);

		otherThis = this;

		final WLClient client = WLClient.createInstance(this);

		client.connect(new MyConnectionListener());

		challengeHandler = new AndroidChallengeHandler(this, realm);
		client.registerChallengeHandler(challengeHandler);

		invokeBtn = (Button) findViewById(R.id.invoke);
		invokeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try{
					//Define the URI of the resource. For a JS HTTP adapter: /adapters/{AdapterName}/{ProcedureName}
					URI adapterPath = new URI("/adapters/AuthAdapter/getSecretData");
					//Create WLResourceRequest object. Choose the HTTP Method (GET, POST, etc).
					WLResourceRequest request = new WLResourceRequest(adapterPath,WLResourceRequest.GET);
					request.send(new MyResponseListener());
				}catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}

		});
		
		logoutBtn = (Button) findViewById(R.id.logout);
		logoutBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				client.logout(realm, new MyRequestListener());			
			}
		});

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean back = data.getBooleanExtra(LoginFormBasedAuth.Back, true);
		String username = data.getStringExtra(LoginFormBasedAuth.UserNameExtra);
		String password = data.getStringExtra(LoginFormBasedAuth.PasswordExtra);
		challengeHandler.submitLogin(resultCode, username, password, back);
	}

	public static void setMainText(final String txt){
		Runnable run = new Runnable() {			
			public void run() {
				mainText.setText(txt);				
			}
		};
		otherThis.runOnUiThread(run);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_form_based_auth, menu);
		return true;
	}

}
