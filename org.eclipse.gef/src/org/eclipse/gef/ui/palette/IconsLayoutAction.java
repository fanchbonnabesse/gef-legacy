package org.eclipse.gef.ui.palette;

import org.eclipse.jface.action.Action;

/**
 * @author Pratik Shah
 */
public class IconsLayoutAction 
	extends Action 
{

private PaletteViewerPreferences prefs;

/**
 * Constructor
 * 
 * @param	prefs	The PaletteViewerPreferences where the preference is stored
 */
public IconsLayoutAction(PaletteViewerPreferences prefs) {
	super(PaletteMessages.SETTINGS_ICONS_VIEW_LABEL);
	this.prefs = prefs;
	if (prefs.getLayoutSetting() == PaletteViewerPreferences.LAYOUT_ICONS) {
		setChecked(true);
	}
}

/**
 * @see org.eclipse.jface.action.Action#run()
 */
public void run() {
	prefs.setLayoutSetting(PaletteViewerPreferences.LAYOUT_ICONS);
}

}