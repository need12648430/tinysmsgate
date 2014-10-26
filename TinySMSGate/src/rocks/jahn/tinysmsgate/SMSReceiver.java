package rocks.jahn.tinysmsgate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	public SMSReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean forwardingEnabled = preferences.getBoolean("chkForwardSMS", false);
		
		if(forwardingEnabled) {
			Bundle bundle = intent.getExtras();
			SmsMessage[] messages = null;
			String from = "";
			String message = "";
			
			if(bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				
				messages = new SmsMessage[pdus.length];
				for(int i = 0; i < messages.length; i ++) {
					messages[i]= SmsMessage.createFromPdu((byte[]) pdus[i]);
					message += messages[i].getMessageBody().toString();
					from = messages[i].getOriginatingAddress();
				}
			}
			
			new SMSForwarder(
				context,
				from,
				message,
				preferences.getString("txtUrl", ""),
				preferences.getString("lstSendMethod", "POST")
			).execute();
		}
	}
	
}