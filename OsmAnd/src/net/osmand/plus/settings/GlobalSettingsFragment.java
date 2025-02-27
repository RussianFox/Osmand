package net.osmand.plus.settings;

import android.app.Activity;
import android.content.Context;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.Preference;
import android.util.Pair;

import net.osmand.plus.ApplicationMode;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.activities.SettingsGeneralActivity;
import net.osmand.plus.dialogs.SendAnalyticsBottomSheetDialogFragment;
import net.osmand.plus.settings.preferences.ListPreferenceEx;
import net.osmand.plus.settings.preferences.SwitchPreferenceEx;


public class GlobalSettingsFragment extends BaseSettingsFragment implements SendAnalyticsBottomSheetDialogFragment.OnSendAnalyticsPrefsUpdate, OnPreferenceChanged {

	public static final String TAG = "GlobalSettingsFragment";

	private static final String SEND_ANONYMOUS_DATA_PREF_ID = "send_anonymous_data";

	@Override
	protected int getPreferencesResId() {
		return R.xml.global_settings;
	}

	@Override
	protected int getToolbarResId() {
		return R.layout.global_preference_toolbar;
	}

	@Override
	protected String getToolbarTitle() {
		return getString(R.string.osmand_settings);
	}

	@Override
	protected void setupPreferences() {
		setupDefaultAppModePref();
		setupPreferredLocalePref();
//		setupExternalStorageDirPref();

		setupSendAnonymousDataPref();
		setupEnableProxyPref();
	}

	@Override
	public void onDisplayPreferenceDialog(Preference preference) {
		String prefId = preference.getKey();

		if (prefId.equals(SEND_ANONYMOUS_DATA_PREF_ID)) {
			FragmentManager fragmentManager = getFragmentManager();
			if (fragmentManager != null) {
				SendAnalyticsBottomSheetDialogFragment.showInstance(app, fragmentManager, this);
			}
		} else {
			super.onDisplayPreferenceDialog(preference);
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String prefId = preference.getKey();

		if (prefId.equals(SEND_ANONYMOUS_DATA_PREF_ID)) {
			if (newValue instanceof Boolean) {
				boolean enabled = (Boolean) newValue;
				if (enabled) {
					FragmentManager fragmentManager = getFragmentManager();
					if (fragmentManager != null) {
						SendAnalyticsBottomSheetDialogFragment.showInstance(app, fragmentManager, this);
					}
				} else {
					settings.SEND_ANONYMOUS_MAP_DOWNLOADS_DATA.set(false);
					settings.SEND_ANONYMOUS_APP_USAGE_DATA.set(false);
					return true;
				}
			}
			return false;
		}

		return super.onPreferenceChange(preference, newValue);
	}

	@Override
	public void onPreferenceChanged(String prefId) {
		if (prefId.equals(settings.DEFAULT_APPLICATION_MODE.getId())) {
			setupDefaultAppModePref();
		} else if (prefId.equals(settings.PREFERRED_LOCALE.getId())) {
			// recreate activity to update locale
			Activity activity = getActivity();
			OsmandApplication app = getMyApplication();
			if (app != null && activity != null) {
				app.checkPreferredLocale();
				activity.recreate();
			}
		}
	}

	@Override
	public void onAnalyticsPrefsUpdate() {
		setupSendAnonymousDataPref();
	}

	private void setupDefaultAppModePref() {
		OsmandApplication app = getMyApplication();
		if (app == null) {
			return;
		}
		ApplicationMode selectedMode = settings.DEFAULT_APPLICATION_MODE.get();

		ApplicationMode[] appModes = ApplicationMode.values(app).toArray(new ApplicationMode[0]);
		String[] entries = new String[appModes.length];
		String[] entryValues = new String[appModes.length];
		for (int i = 0; i < entries.length; i++) {
			entries[i] = appModes[i].toHumanString(app);
			entryValues[i] = appModes[i].getStringKey();
		}

		ListPreferenceEx defaultApplicationMode = (ListPreferenceEx) findPreference(settings.DEFAULT_APPLICATION_MODE.getId());
		defaultApplicationMode.setIcon(getContentIcon(selectedMode.getIconRes()));
		defaultApplicationMode.setEntries(entries);
		defaultApplicationMode.setEntryValues(entryValues);
	}

	private void setupPreferredLocalePref() {
		Context ctx = getContext();
		if (ctx == null) {
			return;
		}
		ListPreferenceEx preferredLocale = (ListPreferenceEx) findPreference(settings.PREFERRED_LOCALE.getId());
		preferredLocale.setIcon(getContentIcon(R.drawable.ic_action_map_language));
		preferredLocale.setSummary(settings.PREFERRED_LOCALE.get());

		Pair<String[], String[]> preferredLocaleInfo = SettingsGeneralActivity.getPreferredLocaleIdsAndValues(ctx);
		if (preferredLocaleInfo != null) {
			preferredLocale.setEntries(preferredLocaleInfo.first);
			preferredLocale.setEntryValues(preferredLocaleInfo.second);
		}

		// Add " (Display language)" to menu title in Latin letters for all non-en languages
		if (!getResources().getString(R.string.preferred_locale).equals(getResources().getString(R.string.preferred_locale_no_translate))) {
			preferredLocale.setTitle(getString(R.string.preferred_locale) + " (" + getString(R.string.preferred_locale_no_translate) + ")");
		}
	}

	private void setupSendAnonymousDataPref() {
		boolean enabled = settings.SEND_ANONYMOUS_MAP_DOWNLOADS_DATA.get() || settings.SEND_ANONYMOUS_APP_USAGE_DATA.get();

		SwitchPreference sendAnonymousData = (SwitchPreference) findPreference(SEND_ANONYMOUS_DATA_PREF_ID);
		sendAnonymousData.setChecked(enabled);
	}

	private void setupEnableProxyPref() {
		SwitchPreferenceEx enableProxy = (SwitchPreferenceEx) findPreference(settings.ENABLE_PROXY.getId());
		enableProxy.setIcon(getContentIcon(R.drawable.ic_action_proxy));
	}
}