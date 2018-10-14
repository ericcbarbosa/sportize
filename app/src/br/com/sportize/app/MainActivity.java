/*
 * Copyright (c) 2012-present, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.sportize.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import br.com.sportize.app.adapter.GroupAdapater;
import br.com.sportize.app.model.Group;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity {

    private RestClient client;
    private ArrayAdapter<Group> listAdapter;
    private ArrayList<Group> groupList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.main);
	}

	@Override
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);

		groupList = new ArrayList<>();
		listAdapter = new GroupAdapater(MainActivity.this, groupList);

		ListView listView = findViewById(R.id.contacts_list);
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Group group = groupList.get(position);

				Intent intent = new Intent(MainActivity.this, GroupDetailActivity.class);
				intent.putExtra("id", group.getId());
				intent.putExtra("name", group.getName());
				intent.putExtra("description", group.getDescription());

				startActivity(intent);
			}
		});

		super.onResume();
	}

	@Override
	public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;

		// Show everything
		findViewById(R.id.root).setVisibility(View.VISIBLE);

		// Get Groups
		try {
			sendRequest("SELECT Id, Name, group_description__c FROM group__c\t");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Toast.makeText(MainActivity.this, "Não foi possível baixar a lista de Grupos: \n"+e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Called when "Logout" button is clicked.
	 *
	 * @param v
	 */
	public void onLogoutClick(View v) {
		SalesforceSDKManager.getInstance().logout(this);
	}

	/**
	 * Called when "Clear" button is clicked.
	 *
	 * @param v
	 */
	public void onClearClick(View v) {
		listAdapter.clear();
	}

	// TODO: Buscar os dados completos do User
	public void onFetchUsersClick(View v) throws UnsupportedEncodingException {
        sendRequest("SELECT player_id__c,name,email__c From player__c\t");
	}

	/**
	 * Called when "Fetch Contacts" button is clicked
	 *
	 * @param v
	 * @throws UnsupportedEncodingException
	 */
	public void onFetchContactsClick(View v) throws UnsupportedEncodingException {
        sendRequest("SELECT Id, Name, group_description__c FROM group__c\t");
	}

	/**
	 * Called when "Fetch Accounts" button is clicked
	 *
	 * @param v
	 * @throws UnsupportedEncodingException
	 */
	public void onFetchAccountsClick(View v) throws UnsupportedEncodingException {
//		sendRequest("SELECT Name FROM Account");
	}

	private void sendRequest(String soql) throws UnsupportedEncodingException {
		RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(this), soql);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, final RestResponse result) {
				result.consumeQuietly(); // consume before going back to main thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							listAdapter.clear();
							JSONArray records = result.asJSONObject().getJSONArray("records");

							// TODO: Extrair dados do JSON e transformar em User
							for (int i = 0; i < records.length(); i++) {

								// TODO: Passar um User para o adapter
								String groupId = records.getJSONObject(i).getString("Id");
								String groupName = records.getJSONObject(i).getString("Name");
								String groupDescription = records.getJSONObject(i).getString("group_description__c");

								Group g = new Group(groupId, groupName, groupDescription);
								groupList.add(g);
							}
						} catch (Exception e) {
							onError(e);
						}
					}
				});
			}

			@Override
			public void onError(final Exception exception) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(MainActivity.this,
								MainActivity.this.getString(
										SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(),
										exception.toString()
								),
								Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
}
