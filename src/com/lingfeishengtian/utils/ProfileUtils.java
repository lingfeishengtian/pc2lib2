package com.lingfeishengtian.utils;

import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.profile.ProfileLoadException;
import edu.csus.ecs.pc2.profile.ProfileManager;

import java.io.File;
import java.io.IOException;

public class ProfileUtils {
    private ProfileManager manager;
    private Profile currentProfile;

    public ProfileUtils(String binPath, boolean shouldSearchForProfileProp, String profilePasscode, InternalController controller, InternalContest contest){
        manager = new ProfileManager();
        File profilesPropertiesFile = new File(binPath + File.separator + "profiles.properties");
        if(profilesPropertiesFile.exists() && shouldSearchForProfileProp){
            try {
                currentProfile = manager.getDefaultProfile(profilesPropertiesFile.getAbsolutePath());
                if(currentProfile.getProfilePath().startsWith("profiles/"))
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

            LanguageManager manager = new LanguageManager(contest, controller);
            manager.loadDefaultLanguages();
        }
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }
}
