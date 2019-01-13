package io.mrarm.irc.setting.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import io.mrarm.irc.R;
import io.mrarm.irc.SettingsActivity;
import io.mrarm.irc.setting.CheckBoxSetting;
import io.mrarm.irc.setting.ListSetting;
import io.mrarm.irc.setting.MaterialColorSetting;
import io.mrarm.irc.setting.SettingsListAdapter;
import io.mrarm.irc.util.EntryRecyclerViewAdapter;
import io.mrarm.irc.util.StyledAttributesHelper;
import io.mrarm.irc.util.theme.ThemeAttrMapping;
import io.mrarm.irc.util.theme.ThemeInfo;
import io.mrarm.irc.util.theme.ThemeManager;

public class ThemeSettingsFragment extends SettingsListFragment implements NamedSettingsFragment {

    public static final String ARG_THEME_UUID = "theme";

    private ThemeInfo themeInfo;
    private ListSetting baseSetting;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ThemeManager themeManager = ThemeManager.getInstance(getContext());
        themeInfo = themeManager.getCustomTheme(
                UUID.fromString(getArguments().getString(ARG_THEME_UUID)));
        if (themeInfo == null)
            throw new RuntimeException("Invalid theme UUID");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public String getName() {
        return themeInfo.name;
    }

    @Override
    public SettingsListAdapter createAdapter() {
        ThemeManager themeManager = ThemeManager.getInstance(getContext());
        SettingsListAdapter a = new SettingsListAdapter(this);
        a.setRequestCodeCounter(((SettingsActivity) getActivity()).getRequestCodeCounter());
        Collection<ThemeManager.BaseTheme> baseThemes = themeManager.getBaseThemes();
        String[] baseThemeNames = new String[baseThemes.size()];
        String[] baseThemeIds = new String[baseThemes.size()];
        int i = 0;
        for (ThemeManager.BaseTheme baseTheme : baseThemes) {
            baseThemeNames[i] = getString(baseTheme.getNameResId());
            baseThemeIds[i] = baseTheme.getId();
            ++i;
        }
        String baseThemeId =  themeInfo.base == null ? themeManager.getFallbackTheme().getId() : themeInfo.base;
        baseSetting = new ListSetting(getString(R.string.theme_base), baseThemeNames, baseThemeIds, baseThemeId);
        baseSetting.addListener((EntryRecyclerViewAdapter.Entry entry) -> {
            themeInfo.base = ((ListSetting) entry).getSelectedOptionValue();
            onPropertyChanged();
        });
        a.add(baseSetting);

        SettingsListAdapter.SettingChangedListener applyListener =
                (EntryRecyclerViewAdapter.Entry entry) -> onPropertyChanged();
        a.add(new ThemeColorSetting(getString(R.string.theme_color_primary))
                .linkProperty(getContext(), themeInfo, ThemeInfo.COLOR_PRIMARY)
                .addListener(applyListener));
        a.add(new ThemeColorSetting(getString(R.string.theme_color_primary_dark))
                .linkProperty(getContext(), themeInfo, ThemeInfo.COLOR_PRIMARY_DARK)
                .addListener(applyListener));
        a.add(new ThemeColorSetting(getString(R.string.theme_color_accent))
                .linkProperty(getContext(), themeInfo, ThemeInfo.COLOR_ACCENT)
                .addListener(applyListener));
        a.add(new ThemeBoolSetting(getString(R.string.theme_light_toolbar))
                .linkProperty(getContext(), themeInfo, ThemeInfo.PROP_LIGHT_TOOLBAR)
                .addListener(applyListener));
        a.add(new ThemeColorSetting(getString(R.string.theme_color_background))
                .linkProperty(getContext(), themeInfo, ThemeInfo.COLOR_BACKGROUND)
                .addListener(applyListener));
        a.add(new ThemeColorSetting(getString(R.string.theme_color_background_floating))
                .linkProperty(getContext(), themeInfo, ThemeInfo.COLOR_BACKGROUND_FLOATING)
                .addListener(applyListener));
        a.add(new ThemeColorSetting(getString(R.string.theme_color_text_primary))
                .linkProperty(getContext(), themeInfo, ThemeInfo.COLOR_TEXT_PRIMARY)
                .addListener(applyListener));
        a.add(new ThemeColorSetting(getString(R.string.theme_color_text_secondary))
                .linkProperty(getContext(), themeInfo, ThemeInfo.COLOR_TEXT_SECONDARY)
                .addListener(applyListener));
        a.add(new ThemeColorSetting(getString(R.string.theme_color_icon))
                .linkProperty(getContext(), themeInfo, ThemeInfo.COLOR_ICON)
                .addListener(applyListener));
        a.add(new ThemeColorSetting(getString(R.string.theme_color_icon_opaque))
                .linkProperty(getContext(), themeInfo, ThemeInfo.COLOR_ICON_OPAQUE)
                .addListener(applyListener));
        return a;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            ThemeManager.getInstance(getContext()).saveTheme(themeInfo);
        } catch (IOException e) {
            Log.w("ThemeSettings", "Failed to save theme");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings_theme, menu);
        menu.findItem(R.id.action_rename).setOnMenuItemClickListener(item -> {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_edit_text, null);
            EditText text = view.findViewById(R.id.edit_text);
            text.setText(themeInfo.name);
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.action_rename)
                    .setView(view)
                    .setPositiveButton(R.string.action_ok, (dialog1, which) -> {
                        themeInfo.name = text.getText().toString();
                        ((SettingsActivity) getActivity()).updateTitle();
                    })
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();
            return true;
        });
    }

    private void onPropertyChanged() {
        ThemeManager.getInstance(getContext()).invalidateCurrentCustomTheme();
        getActivity().recreate();
    }

    public static class SingleLineMaterialColorSetting extends MaterialColorSetting {

        private static final int sHolder = SettingsListAdapter.registerViewHolder(Holder.class,
                R.layout.settings_list_entry_color_single_line);

        public SingleLineMaterialColorSetting(String name) {
            super(name);
        }

        @Override
        public int getViewHolder() {
            return sHolder;
        }

    }

    public static final class ThemeColorSetting extends SingleLineMaterialColorSetting {

        private boolean mHasCustomColor = false;
        private ThemeInfo mTheme;
        private String mThemeProp;

        public ThemeColorSetting(String name) {
            super(name);
        }

        @Override
        public void setSelectedColor(int color) {
            super.setSelectedColor(color);
            mHasCustomColor = true;
            if (mTheme != null) {
                mTheme.colors.put(mThemeProp, color);
            }
        }

        private static int getDefaultValue(Context context, ThemeInfo theme, String prop) {
            List<Integer> attrs = ThemeAttrMapping.getColorAttrs(prop);
            if (attrs == null || attrs.size() == 0)
                return 0;
            StyledAttributesHelper attrVals = StyledAttributesHelper.obtainStyledAttributes(context,
                    theme.baseThemeInfo.getThemeResId(), new int[] { attrs.get(0) });
            return attrVals.getColor(attrs.get(0), 0);
        }

        public ThemeColorSetting linkProperty(Context context, ThemeInfo theme, String prop) {
            Integer color = theme.colors.get(prop);
            if (color != null) {
                setSelectedColor(color);
            } else {
                mHasCustomColor = false;
                super.setSelectedColor(getDefaultValue(context, theme, prop));
            }
            mTheme = theme;
            mThemeProp = prop;
            return this;
        }

    }

    public static final class ThemeBoolSetting extends CheckBoxSetting {

        private boolean mHasCustomValue = false;
        private ThemeInfo mTheme;
        private String mThemeProp;

        public ThemeBoolSetting(String name) {
            super(name, false);
        }

        @Override
        public void setChecked(boolean checked) {
            super.setChecked(checked);
            mHasCustomValue = true;
            if (mTheme != null) {
                mTheme.properties.put(mThemeProp, checked);
            }
        }

        private static boolean getDefaultValue(Context context) {
            return false;
        }

        public ThemeBoolSetting linkProperty(Context context, ThemeInfo theme, String prop) {
            Boolean value = theme.getBool(prop);
            if (value != null) {
                setChecked(value);
            } else {
                mHasCustomValue = false;
                super.setChecked(getDefaultValue(context));
            }
            mTheme = theme;
            mThemeProp = prop;
            return this;
        }

    }

}