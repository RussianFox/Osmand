package net.osmand.plus.settings.bottomsheets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.OsmandSettings.BooleanPreference;
import net.osmand.plus.OsmandSettings.OsmandPreference;
import net.osmand.plus.R;
import net.osmand.plus.base.bottomsheetmenu.BaseBottomSheetItem;
import net.osmand.plus.base.bottomsheetmenu.BottomSheetItemWithCompoundButton;
import net.osmand.plus.base.bottomsheetmenu.BottomSheetItemWithDescription;
import net.osmand.plus.base.bottomsheetmenu.simpleitems.TitleItem;
import net.osmand.plus.settings.preferences.SwitchPreferenceEx;

import org.apache.commons.logging.Log;

public class BooleanPreferenceBottomSheet extends BasePreferenceBottomSheet {

	public static final String TAG = BooleanPreferenceBottomSheet.class.getSimpleName();

	private static final Log LOG = PlatformUtil.getLog(BooleanPreferenceBottomSheet.class);

	@Override
	public void createMenuItems(Bundle savedInstanceState) {
		OsmandApplication app = getMyApplication();
		if (app == null) {
			return;
		}
		final SwitchPreferenceEx switchPreference = getSwitchPreferenceEx();
		if (switchPreference == null) {
			return;
		}
		OsmandPreference preference = app.getSettings().getPreference(switchPreference.getKey());
		if (!(preference instanceof BooleanPreference)) {
			return;
		}

		String title = switchPreference.getTitle().toString();
		items.add(new TitleItem(title));

		final OsmandSettings.BooleanPreference pref = (BooleanPreference) preference;
		final String on = getString(R.string.shared_string_on);
		final String off = getString(R.string.shared_string_off);
		boolean checked = pref.get();

		final BottomSheetItemWithCompoundButton[] preferenceBtn = new BottomSheetItemWithCompoundButton[1];
		preferenceBtn[0] = (BottomSheetItemWithCompoundButton) new BottomSheetItemWithCompoundButton.Builder()
				.setChecked(checked)
				.setTitle(checked ? on : off)
				.setLayoutId(R.layout.bottom_sheet_item_with_switch)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						boolean newValue = !pref.get();
						if (switchPreference.callChangeListener(newValue)) {
							switchPreference.setChecked(newValue);
							preferenceBtn[0].setTitle(newValue ? on : off);
							preferenceBtn[0].setChecked(newValue);
						}
					}
				})
				.create();
		items.add(preferenceBtn[0]);

		String description = switchPreference.getDescription();
		if (description != null) {
			BaseBottomSheetItem preferenceDescription = new BottomSheetItemWithDescription.Builder()
					.setDescription(description)
					.setLayoutId(R.layout.bottom_sheet_item_description_long)
					.create();
			items.add(preferenceDescription);
		}
	}

	@Override
	protected int getDismissButtonTextId() {
		return R.string.shared_string_close;
	}

	private SwitchPreferenceEx getSwitchPreferenceEx() {
		return (SwitchPreferenceEx) getPreference();
	}

	public static void showInstance(@NonNull FragmentManager fm, String prefId, Fragment target) {
		try {
			if (fm.findFragmentByTag(BooleanPreferenceBottomSheet.TAG) == null) {
				Bundle args = new Bundle();
				args.putString(PREFERENCE_ID, prefId);
				BooleanPreferenceBottomSheet fragment = new BooleanPreferenceBottomSheet();
				fragment.setArguments(args);
				fragment.setTargetFragment(target, 0);
				fragment.show(fm, BooleanPreferenceBottomSheet.TAG);
			}
		} catch (RuntimeException e) {
			LOG.error("showInstance", e);
		}
	}
}