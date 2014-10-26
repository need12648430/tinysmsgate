package rocks.jahn.tinysmsgate;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSGateService extends Service {
	private IBinder binder = new SMSGateServiceBinder();
	private SMSGateWebServer webServer;
	private SharedPreferences preferences;
	private SmsManager smsManager;
	private Context context;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    return super.onStartCommand(intent,flags,startId);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		webServer.stop();
	    Toast.makeText(this, "SMSGate Web Service stopped.", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}
	
	public void setSmsManager(SmsManager smsManager) {
		this.smsManager = smsManager;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void startServer() {
		if(webServer == null) {
			String port = preferences.getString("txtPort", "8080");
			webServer = new SMSGateWebServer(Integer.parseInt(port));
			webServer.setPreferences(preferences);
			webServer.setSmsManager(smsManager);
			webServer.setContext(context);
		}

		boolean receptionEnabled = preferences.getBoolean("chkReceiveSMS", false);
		
		if(receptionEnabled) {
		    try {
		    	webServer.start();
			    Toast.makeText(this, "Tiny SMS Gate Web Service started.", Toast.LENGTH_SHORT).show();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		} else {
		    Toast.makeText(this, "Tiny SMS Gate Web Service is disabled in the preferences.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void stopServer() {
		if(webServer.isAlive())
			webServer.stop();
		webServer = null;
	    Toast.makeText(this, "Tiny SMS Gate Web Service stopped.", Toast.LENGTH_SHORT).show();
	}
	
	public boolean isAlive() {
		return webServer != null && webServer.isAlive();
	}
	
	public int getPort() {
		return webServer.getListeningPort();
	}
	
	public class SMSGateServiceBinder extends Binder {
		public SMSGateService getServerInstance() {
			return SMSGateService.this;
		}
	}

}
