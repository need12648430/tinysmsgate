package rocks.jahn.tinysmsgate;

import rocks.jahn.smsgate.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class Preferences extends PreferenceActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        getFragmentManager()
        	.beginTransaction()
        		.replace(android.R.id.content, new SMSGatePreferenceFragment())
        	.commit();
	}
	


    public static class SMSGatePreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.smsgate_preferences);
        }
    }
}
