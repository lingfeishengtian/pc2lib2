package com.lingfeishengtian.core;

import com.lingfeishengtian.utils.ProfileUtils;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

import java.io.File;
import java.io.IOException;

public class ContestInstance {
    private InternalController controller;
    private InternalContest contest;
    private String pathToBin;
    private ProfileUtils profileUtils;
    private Log mainLog;

    /**
     * Assumes new competition and new profile
     */
    public ContestInstance(String binPath, boolean useExistingProfileFromProfilesProperties, String contestPasscode){
        contest = new InternalContest();
        contest.setContestPassword(contestPasscode);

        mainLog = new Log(binPath + File.separator + "logs", "pc2lib2");
        StaticLog.setLog(new Log(binPath + File.separator + "logs", "pc2lib2staticLog"));

        controller = new InternalController(contest);
        controller.setUsingGUI(false);
        controller.setLog(mainLog);

        pathToBin = binPath;

        profileUtils = new ProfileUtils(binPath, useExistingProfileFromProfilesProperties, contestPasscode);

        contest.setProfile(profileUtils.getCurrentProfile());
        controller.setTheProfile(profileUtils.getCurrentProfile());

        controller.setContactingRemoteServer(false);

        LanguageManager manager = new LanguageManager(contest, controller);
        manager.loadDefaultLanguages();
    }

    public void startDataViewing() {
        try {
            controller.initializeServer(contest);
        }catch (FileSecurityException sec){
            System.exit(4);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(4);
        }
    }

    public void saveContest(){
        try {
            contest.storeConfiguration(mainLog);
        }catch (IOException e){
            System.out.println("File pathing issues!");
            System.exit(4);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(4);
        }
    }

    public void addProblem(DefaultProblem problem){
        contest.addProblem(problem.getProblem(), problem.generateDataFiles());
    }

    public void generateAccounts(){
        contest.generateNewAccounts(String.valueOf(ClientType.Type.TEAM), 10,true);
    }

    public void test(){
        System.out.println(contest.getJudgements()[0].getDisplayName());
    }
}
