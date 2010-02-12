package edu.ucla.cens.budburst.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.util.Log;
import edu.ucla.cens.budburst.PreferencesManager;
import edu.ucla.cens.budburst.helper.netUtils;

public class SyncableDatabase extends WritableDatabase {
	private static final String TAG = "WritableDatabase";
	private String downUrl;
	private String upUrl;
	private Context context;

	public SyncableDatabase(Context context, String downUrl, String upUrl, SyncableRow row) {
		super(new DatabaseHelper(context, row), row.getName(), row);
		this.downUrl = downUrl;
		this.upUrl = upUrl;
		this.context = context;
	}

	public Boolean sync(String json_data) {
		try {
			JSONObject ret = new JSONObject(new JSONTokener(json_data));
			// check success status
			if (ret.getBoolean("success")) {

				JSONArray json = new JSONArray(ret.getString("results"));

				insertRows(json);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	// adds samples to the database from a correctly formated json array
	public long insertRows(JSONArray json) {
		long rowid = -1;

		for (int i = 0; i < json.length(); i++) {
			JSONObject object;
			try {
				object = json.getJSONObject(i);
				Iterator keys = object.keys();
				ContentValues vals = new ContentValues();

				while (keys.hasNext()) {
					String key = keys.next().toString();
					String value = object.get(key).toString();

					vals.put(key, value);
				}

				// should be set that they are synced
				vals.put("synced", true);

				rowid = insertRow(vals);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
				Log.d(TAG, "did not insert element");
			}

		}

		return rowid;
	}

	public void uploadData() {
		// TODO more robust checking of if the data can be uploaded
		if (upUrl.equals(""))
			return;

		ArrayList<Row> rows = find("synced=" + false);
		for (Iterator<Row> i = rows.iterator(); i.hasNext();) {
			Log.d(TAG, "for each row");
			try {

				SyncableRow row = (SyncableRow) i.next();
				// Create new client.
				HttpClient httpClient = new DefaultHttpClient();

				// Compile post data.
				ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
				data.addAll(PreferencesManager.currentAuthParams(context));

				data.addAll(row.parameters());

				// Form request with post data.
				HttpPost httpRequest = new HttpPost(upUrl);
				httpRequest.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));

				// Send request.
				HttpResponse response = httpClient.execute(httpRequest);

				// Get message.
				HttpEntity entity = response.getEntity();
				String responseVal = netUtils.generateString(entity.getContent());

				// Get status.
				int status = response.getStatusLine().getStatusCode();

				// Act on result.
				if (status == 200) {
				}

				if (entity != null) {
					// Delete entity.
					entity.consumeContent();
				}

				Log.d("httppost", responseVal);

				if (responseVal.contains("success")) {
					row.synced = true;
					row.put();
				}

			} catch (ClientProtocolException e) {
				Log.e("httpPost", e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("httpPost", e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				if (e.getMessage() != null)
					Log.e("httpPost", e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public String getUpURL() {
		return upUrl;
	}

	public String getDownURL() {
		return downUrl;
	}
}
