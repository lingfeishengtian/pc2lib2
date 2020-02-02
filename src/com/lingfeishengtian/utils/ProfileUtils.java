package com.lingfeishengtian.utils;

import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.profile.ProfileLoadException;
import edu.csus.ecs.pc2.profile.ProfileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ProfileUtils {
    private ProfileManager manager;
    private Profile currentProfile;
//    private final String deliminator = ",";

    public ProfileUtils(String binPath, boolean shouldSearchForProfileProp, String profilePasscode){
        manager = new ProfileManager();
        File profilesPropertiesFile = new File(binPath + File.separator + "profiles.properties");
        if(profilesPropertiesFile.exists() && shouldSearchForProfileProp){
            try {
                currentProfile = manager.getDefaultProfile(profilesPropertiesFile.getAbsolutePath());
                currentProfile.setProfilePath(binPath + File.separator + currentProfile.getProfilePath());
            }catch (IOException e){
                System.out.println("Something's wrong with your profile path.");
                System.exit(4);
            }catch (ProfileLoadException e){
                e.printStackTrace();
                System.exit(4);
            }catch (Exception e){
                System.out.println("WHACK ERROR");
                System.exit(5);
            }
        }else{
            currentProfile = ProfileManager.createNewProfile();
            currentProfile.setActive(true);
            try {
                manager.storeDefaultProfile(binPath + File.separator + ProfileManager.PROFILE_INDEX_FILENAME, currentProfile);
            } catch (IOException e){
                System.out.println("There was an error while saving a new profile!");
            }
            currentProfile.setProfilePath(binPath + File.separator + currentProfile.getProfilePath());
            try {
                manager.createProfilesPathandFiles(currentProfile, 1, profilePasscode);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    //    public Profile getDefaultProfile(String filename) throws IOException, ProfileLoadException {
//        if ((new File(filename)).exists()) {
//            Properties properties = new Properties();
//            properties.load(new FileInputStream(filename));
//            String key = properties.getProperty("current");
//            if (key != null) {
//                String profileLine = properties.getProperty(key);
//                return toProfile(key, profileLine);
//            }
//            return null;
//        }
//        throw new FileNotFoundException(filename);
//    }
//
//    private String stripQuote(String string) {
//        if (string == null)
//            return null;
//        StringBuffer buffer = new StringBuffer(string);
//        if (buffer.charAt(0) == '"')
//            buffer.deleteCharAt(0);
//        int idx = buffer.lastIndexOf("\"");
//        if (idx > -1)
//            buffer.deleteCharAt(idx);
//        return buffer.toString();
//    }
//
//    public Profile toProfile(String key, String profileLine) throws ProfileLoadException {
//        if (profileLine == null)
//            return null;
//        String[] fields = profileLine.split(deliminator);
//        if (fields.length < 3)
//            throw new IllegalArgumentException("Too few fields for line: " + profileLine);
//        String title = stripQuote(fields[0]);
//        String description = stripQuote(fields[1]);
//        String path = stripQuote(fields[2]);
//        String activeField = null;
//        if (fields.length > 3)
//            activeField = fields[3];
//        if (title == null || title.length() == 0)
//            throw new ProfileLoadException("No title found in: " + profileLine);
//        Profile profile = new Profile(title);
//        if (description != null)
//            profile.setDescription(description);
//        if (path == null || path.length() == 0)
//            throw new ProfileLoadException("No path found in: " + profileLine);
//        if (activeField == null) {
//            profile.setActive(true);
//        } else if (activeField.startsWith("active=")) {
//            String newValue = activeField.replaceFirst("active=", "");
//            profile.setActive(Boolean.parseBoolean(newValue));
//        } else {
//            assert false : "Invalid active= field, active= not found";
//        }
//        profile.setProfilePath(path);
//        profile.setContestId(key);
//        return profile;
//    }
}
