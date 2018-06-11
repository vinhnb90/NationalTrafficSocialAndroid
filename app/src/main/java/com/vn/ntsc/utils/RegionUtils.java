package com.vn.ntsc.utils;

import android.content.Context;

import com.vn.ntsc.repository.model.region.Region;
import com.vn.ntsc.repository.model.region.RegionComponent;
import com.vn.ntsc.repository.model.region.RegionGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nankai on 8/8/2017.
 */

public class RegionUtils {
    private static final String TAG = RegionUtils.class.getSimpleName();
    private static List<RegionComponent> regionGroups;
    private static RegionUtils instance;

    public static RegionUtils getInstance(Context context) {
        if (instance == null) {
            instance = new RegionUtils(context);
        }
        return instance;
    }

    public RegionUtils(Context context) {
        // initialize regiongroup here
        try {
            regionGroups = getRegionGroupList(context);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private XmlPullParser getXmlPullParser(Context context)
            throws XmlPullParserException, IOException {
        InputStream is = context.getResources().getAssets().open("regions.xml");
        XmlPullParserFactory pullParserFactory;
        pullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = pullParserFactory.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(is, null);
        return parser;
    }

    public List<RegionComponent> getRegionComponentList() {
        return regionGroups;
    }

    private List<RegionComponent> getRegionGroupList(Context context)
            throws XmlPullParserException, IOException {
        List<RegionComponent> listGroups = null;
        Region region;
        RegionGroup regionGroup = null;
        XmlPullParser parser = getXmlPullParser(context);
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String nameTag;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    listGroups = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    nameTag = parser.getName();
                    if (nameTag.equalsIgnoreCase("regionlist")) {
                        listGroups = new ArrayList<>();
                    } else if (nameTag.equalsIgnoreCase("region_group")) {
                        regionGroup = new RegionGroup();
                        regionGroup.setName(parser.getAttributeValue(null, "name"));
                        LogUtils.i(TAG, "getRegionGroupList: " + regionGroup.getName());
                    } else if (nameTag.equalsIgnoreCase("region")) {
                        int regionCode = Integer.valueOf(parser.getAttributeValue(
                                null, "code"));
                        String regionAlias = parser
                                .getAttributeValue(null, "alias");
                        String regionName = parser.nextText();
                        region = new Region(regionCode, regionAlias, regionName);
                        regionGroup.getRegion().add(region);
                        LogUtils.i(TAG, "getRegionGroupListItem: " + region.getName());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    nameTag = parser.getName();
                    if (nameTag.equalsIgnoreCase("region_group")) {
                        listGroups.add(regionGroup);
                        listGroups.addAll(regionGroup.getRegion());
                    }
            }
            eventType = parser.next();
        }
        return listGroups;
    }

    public int getRegionCodeFromRegionName(String regionName) {
        for (RegionComponent regionComponent : regionGroups) {
            if (regionComponent.getName().equalsIgnoreCase(regionName)) {
                return (regionComponent).getCode();
            }
        }
        throw new IllegalArgumentException("Not found region name");
    }


    public String getRegionName(int regionCode) {
        for (RegionComponent regionComponent : regionGroups) {
            if ((regionComponent).getCode() == regionCode) {
                return (regionComponent).getName();
            }
        }
        return null;
    }

    public int getRegionCodeFromAlias(String regionAlias) {
        for (RegionComponent regionComponent : regionGroups) {
            if ((regionComponent).getAlias().contains(regionAlias)) {
                return (regionComponent).getCode();
            }
        }
        throw new IllegalArgumentException("Not found region alias");
    }

    public String[] getRegionAlias() {
        List<String> aliasList = new ArrayList<String>();
        for (RegionComponent regionComponent : regionGroups) {
            aliasList.add((regionComponent).getAlias());
        }
        String[] alias = new String[aliasList.size()];
        for (int i = 0; i < aliasList.size(); i++) {
            alias[i] = aliasList.get(i);
        }
        return alias;
    }

    public String[] getRegionNames() {
        List<String> nameList = new ArrayList<String>();
        for (RegionComponent regionComponent : regionGroups) {
            nameList.add((regionComponent).getName());
        }
        String[] regionNames = new String[nameList.size()];
        for (int i = 0; i < nameList.size(); i++) {
            regionNames[i] = nameList.get(i);
        }
        return regionNames;
    }
}
