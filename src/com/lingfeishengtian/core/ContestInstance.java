package com.lingfeishengtian.core;

import com.lingfeishengtian.utils.ProfileUtils;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.*;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

public class ContestInstance {
    private InternalController controller;
    private InternalContest contest;
    private String pathToBin;
    private ProfileUtils profileUtils;
    private Log mainLog;

    public int DEFAULT_TEAMS = 40;
    public int DEFAULT_ADMINISTRATORS = 1;
    public int DEFAULT_JUDGES = 5;
    public int DEFAULT_SCOREBOARDS = 1;

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

        controller.setContactingRemoteServer(false);
        contest.setClientId(new ClientId(1, ClientType.Type.SERVER, 0));

        profileUtils = new ProfileUtils(binPath, useExistingProfileFromProfilesProperties, contestPasscode, controller, contest);

        contest.setProfile(profileUtils.getCurrentProfile());
        controller.setTheProfile(profileUtils.getCurrentProfile());
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
            String profPath = contest.getProfile().getProfilePath().replace(pathToBin, "");
            if(profPath.startsWith("/")) profPath = profPath.substring(1);
            contest.getProfile().setProfilePath(profPath);
            contest.storeConfiguration(mainLog);
        }catch (IOException e){
            System.out.println("File pathing issues!");
            System.exit(4);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(4);
        }
    }

    public void setScoringProperties(int pointsForYes, int pointsForNo, int pointsPerMinute){
        ContestInformation contestInformation = contest.getContestInformation();
        Properties prop = contestInformation.getScoringProperties();
        prop.setProperty("Base Points per Yes", String.valueOf(pointsForYes));
        prop.setProperty("Points per No", String.valueOf(pointsForNo));
        prop.setProperty("Points per Minute (for 1st yes)", String.valueOf(pointsPerMinute));
    }

    public void setDefaultScoring(){
        setScoringProperties(60, -5, 0);
    }

    public void addProblem(DefaultProblem problem){
        contest.addProblem(problem.getProblem(), problem.generateDataFiles());
        setContestAutoJudges();
    }

    public void setContestTime(long seconds){
        ContestTime time = contest.getContestTime();
        time.resetClock();
        time.setContestLengthSecs(seconds);
    }

    public void setDefaultContestTime(){
        long hours = 2;
        setContestTime(hours * 60 * 60);
    }

    public void setContestAutoJudges(){
        final ClientSettings[] clientSettingsExist = contest.getClientSettingsList();
        ClientSettings[] shouldAdd = new ClientSettings[0];
        Problem[] problems = contest.getProblems();
        for (Account acc : getAccountsOfType(ClientType.Type.JUDGE)){
            boolean shouldAddBool = true;
            for (int i = 0; i < clientSettingsExist.length; i++) {
                if(clientSettingsExist[i].getClientId().equals(acc.getClientId())){
                    shouldAddBool = false;
                    break;
                }
            }
            if(shouldAddBool) {
                ClientSettings c = new ClientSettings(acc.getClientId());
                c.setAutoJudging(true);
                Filter filter = c.getAutoJudgeFilter();
                filter.setFilterOn();
                filter.setUsingProblemFilter(true);
                if (filter.isFilteringProblems()) {
                    for (Problem p : problems) {
                        filter.addProblem(p);
                    }
                }

                ClientSettings[] tmp = shouldAdd;
                shouldAdd = new ClientSettings[shouldAdd.length + 1];
                for (int i = 0; i < tmp.length; i++) {
                    shouldAdd[i] = tmp[i];
                }
                shouldAdd[tmp.length] = c;
            }
        }
        for (ClientSettings settings :
                shouldAdd) {
            contest.addClientSettings(settings);
        }
    }

    public void setDefaultNumberOfAccounts(){
        checkAndGenerate(ClientType.Type.ADMINISTRATOR);
        checkAndGenerate(ClientType.Type.JUDGE);
        checkAndGenerate(ClientType.Type.TEAM);
        checkAndGenerate(ClientType.Type.SCOREBOARD);
    }

    public void generateSingleAccount(ClientType.Type type){
        contest.generateNewAccounts(String.valueOf(type), 1, true);
    }

    private void checkAndGenerate(ClientType.Type type){
        int existingAmtOfAcc = getAccountsOfType(type).size();
        if(getDefaultFrom(type) > existingAmtOfAcc)
            contest.generateNewAccounts(String.valueOf(type), getDefaultFrom(type) - existingAmtOfAcc, true);
    }

    private int getDefaultFrom(ClientType.Type type){
        switch (type){
            case TEAM:
                return DEFAULT_TEAMS;
            case ADMINISTRATOR:
                return DEFAULT_ADMINISTRATORS;
            case JUDGE:
                return DEFAULT_JUDGES;
            case SCOREBOARD:
                return DEFAULT_SCOREBOARDS;
            default:
                return 0;
        }
    }

    private Vector<Account> getAccountsOfType(ClientType.Type type){
        return contest.getAccounts(type);
    }

    public void test(){
        System.out.println(contest.getJudgements()[0].getDisplayName());
    }
}
