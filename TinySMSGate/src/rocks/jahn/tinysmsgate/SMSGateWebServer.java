// TODO: Based on settings, receive POST and send corresponding SMS.

package rocks.jahn.tinysmsgate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import fi.iki.elonen.NanoHTTPD;

public class SMSGateWebServer extends NanoHTTPD {
	private static final String SENT = "SMS_SENT";
	private static final String DELIVERED = "SMS_DELIVERED";
	
	private SharedPreferences preferences;
	private SmsManager smsManager;
	private Context context;
	private BroadcastReceiver sentReceiver, deliveredReceiver;

	public SMSGateWebServer(int port) {
		super(port);
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
	
	@Override
	public void start() throws IOException {
		sentReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				    case Activity.RESULT_OK:
				    	// sent
			        break;
				}
			}
			
		};
		
		deliveredReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				switch(getResultCode()) {
					case Activity.RESULT_OK:
						// delivered
					break;
				}
			}
			
		};
		
		context.registerReceiver(sentReceiver, new IntentFilter(SENT));
		context.registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));
		
		super.start();
	}
	
	@Override
	public void stop() {
		context.unregisterReceiver(sentReceiver);
		context.unregisterReceiver(deliveredReceiver);
		
		super.stop();
	}
	
	public void sendSms(String to, String message) {
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);
		
		smsManager.sendTextMessage(to, null, message, sentPI, deliveredPI);
	}
	
	private String pageTemplate(String head, String content) {
		return "<html><body><h1>" + head + "</h1><p>" + content + "</p></body></html>";
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		String uri = session.getUri();
        Map<String, String> files = new HashMap<String, String>();
        
        if(Method.POST.equals(method)) {
        	try {
				session.parseBody(files);
			} catch (IOException e) {
				e.printStackTrace();
				
		        return new NanoHTTPD.Response(
	        		Response.Status.INTERNAL_ERROR,
	        		"text/html",
	        		pageTemplate("Internal Server Error", e.getMessage())
		        );
			} catch (ResponseException e) {
				e.printStackTrace();
				
		        return new NanoHTTPD.Response(
	        		e.getStatus(),
	        		"text/html",
	        		pageTemplate("Internal Server Error", e.getMessage())
		        );
			}
        }
        
    	Map<String, String> data = session.getParms();
        data.put("NanoHttpd.QUERY_STRING", session.getQueryParameterString());
        boolean usePassword = preferences.getBoolean("chkUsePassword", false);
    	String password = preferences.getString("txtPassword", "");
        
        if(uri.equals(preferences.getString("txtPage", "/send"))) {
        	String desiredMethod = preferences.getString("lstReceiveMethod", "POST");
        	
        	if((desiredMethod.equals("POST") && Method.POST.equals(method)) ||
        	   (desiredMethod.equals("GET") && Method.GET.equals(method))) {
        		if(usePassword) {
        			if(data.containsKey("password")) {
        				String sentPassword = data.get("password");
        				
        				if(sentPassword.equals(password)) {
        	        		String phone = data.get("phone");
        	        		String message = data.get("message");
        	        		sendSms(phone, message);
        	        		
        	    	        return new NanoHTTPD.Response(
    	    		        	pageTemplate("SMSgate", "Sent!")
    	    		        );
        				} else {
        			        return new NanoHTTPD.Response(
        			        	Response.Status.FORBIDDEN,
        			        	"text/html",
        			        	pageTemplate("Forbidden", "Bad password.")
        			        );
        				}
        			} else {
    			        return new NanoHTTPD.Response(
    			        	Response.Status.FORBIDDEN,
    			        	"text/html",
    			        	pageTemplate("Forbidden", "Bad password.")
    			        );
        			}
        		} else {
	    	        return new NanoHTTPD.Response(
    		        	pageTemplate("SMSgate", "Sent!")
    		        );
        		}
        	} else {
    	        return new NanoHTTPD.Response(
		        	Response.Status.NOT_FOUND,
		        	"text/html",
		        	pageTemplate("404", "Aw, man. :(")
		        );
        	}
        } else if(uri.equals("/")) {
	        return new NanoHTTPD.Response(
	        	pageTemplate("SMSGate", "Welcome to SMSGate!")
	        );
        } else {
	        return new NanoHTTPD.Response(
	        	Response.Status.NOT_FOUND,
	        	"text/html",
	        	pageTemplate("404", "Aw, man. :(")
	        );
        }
	}
}
