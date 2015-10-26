package com.gk.touchstone.utils;

import java.util.Map;

import com.gk.touchstone.R;
import com.gk.touchstone.adapter.PlanAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Activity内接收自定义的广播
 * 
 * @author Administrator
 * 
 */
public class ActivityReceiveBroadCast extends BroadcastReceiver {
	private Context context;
	private Activity activity = null;

	public ActivityReceiveBroadCast(Context context) {
		this.context = context;
		activity = (Activity) context;
	}

	public void onReceive(Intent intent, PlanAdapter adapter,
			Map<String, Object> stateMap) {
		if (activity == null) {
			return;
		}

		Button btnRun = (Button) activity.findViewById(R.id.title_rbtn1);
		Button btnStop = (Button) activity.findViewById(R.id.title_rbtn2);

		String bcAction = context.getResources().getString(
				R.string.customPlanAction);
		String bcName = context.getResources().getString(
				R.string.customPlanBroadCastName);

		if (intent.getAction().equals(bcAction)) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				int receiveValue = bundle.getInt(bcName);
				if (receiveValue == 0) {
					adapter.setState(null);
					btnRun.setVisibility(View.VISIBLE);
					btnStop.setVisibility(View.GONE);
				} else {
					adapter.setState(stateMap);
				}
				adapter.notifyDataSetChanged();

			}
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

	}

}
