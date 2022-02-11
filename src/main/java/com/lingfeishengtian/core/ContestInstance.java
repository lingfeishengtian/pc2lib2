package com.lingfeishengtian.core;

import com.lingfeishengtian.utils.ProfileUtils;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.*;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.security.SecureRandom;

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

    private String pPath;

    /**
     * Initializes contest instance and stores the competition data.
     * <p>
     * If useExistingProfileFromProfilesProperties is true, then it'll attempt to
     * search for the existing profiles. If one doesn't exist, it'll create a new
     * one. If useExistingProfileFromProfilesProperties is false, then it'll always
     * create a new profile.
     *
     * @param binPath
     * @param useExistingProfileFromProfilesProperties
     * @param contestPasscode
     */

    public ContestInstance(String binPath, boolean useExistingProfileFromProfilesProperties, String contestPasscode) {
        contest = new InternalContest();
        contest.setContestPassword(contestPasscode);

        mainLog = new Log(binPath + File.separator + "logs", "pc2lib2");
        StaticLog.setLog(new Log(binPath + File.separator + "logs", "pc2lib2staticLog"));

        controller = new InternalController(contest);
        controller.setHaltOnFatalError(false);
        controller.setUsingGUI(false);
        controller.setLog(mainLog);

        pathToBin = binPath;

        controller.setContactingRemoteServer(false);
        contest.setClientId(new ClientId(1, ClientType.Type.SERVER, 0));

        profileUtils = new ProfileUtils(binPath, useExistingProfileFromProfilesProperties, contestPasscode, controller,
                contest);

        Profile f = profileUtils.getCurrentProfile();
        pPath = f.getProfilePath();
        f.setProfilePath(binPath + File.separator + f.getProfilePath());
        contest.setProfile(f);
        controller.setTheProfile(f);
    }

    /**
     * Initializes the server and exposes internal API to allow modification.
     */
    public void startDataViewing() throws FileSecurityException, IOException, ClassNotFoundException {
        controller.initializeServer(contest);
    }

    public void saveContest() {
        try {
            // String profPath = contest.getProfile().getProfilePath().replace(pathToBin,
            // "");
            // if (profPath.startsWith(File.separator)) profPath = profPath.substring(1);
            // contest.getProfile().setProfilePath(profPath);
            Profile f = contest.getProfile();
            f.setProfilePath(pPath);

            contest.setProfile(f);
            controller.setTheProfile(f);

            contest.storeConfiguration(mainLog);

            f.setProfilePath(pathToBin + File.separator + f.getProfilePath());
        } catch (IOException e) {
            System.out.println("File pathing issues!");
            System.exit(4);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(4);
        }
    }

    /**
     * Retrieves contest information and then sets values.
     *
     * @param pointsForYes    How many points are given for a correct answer.
     * @param pointsForNo     How many points are counted off for a wrong answer.
     * @param pointsPerMinute How many points are given for minutes after timer and
     *                        problem solved. (Predicted I actually have no idea
     *                        what this means)
     */
    public void setScoringProperties(int pointsForYes, int pointsForNo, int pointsPerMinute) {
        ContestInformation contestInformation = contest.getContestInformation();
        Properties prop = contestInformation.getScoringProperties();
        prop.setProperty("Base Points per Yes", String.valueOf(pointsForYes));
        prop.setProperty("Points per No", String.valueOf(pointsForNo));
        prop.setProperty("Points per Minute (for 1st yes)", String.valueOf(pointsPerMinute));
    }

    public void setDefaultScoring() {
        setScoringProperties(60, -5, 0);
    }

    public void addProblem(DefaultProblem problem) {
        contest.addProblem(problem.getProblem(), problem.generateDataFiles());
        setContestAutoJudges();
    }

    public void setContestTime(long seconds) {
        ContestTime time = contest.getContestTime();
        time.resetClock();
        time.setContestLengthSecs(seconds);
    }

    public void setDefaultContestTime() {
        long hours = 2;
        setContestTime(hours * 60 * 60);
    }

    public void setContestAutoJudges() {
        ClientSettings[] shouldAdd = new ClientSettings[0];
        Problem[] problems = contest.getProblems();
        for (Account acc : getAccountsOfType(ClientType.Type.JUDGE)) {
            // int clientSettingsIndex = -1;
            // for (int i = 0; i < clientSettingsExist.length; i++) {
            // if (clientSettingsExist[i].getClientId().equals(acc.getClientId())) {
            // clientSettingsIndex = i;
            // break;
            // }
            // }
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

            // if(clientSettingsIndex == -1) {
            ClientSettings[] tmp = shouldAdd;
            shouldAdd = new ClientSettings[shouldAdd.length + 1];
            for (int i = 0; i < tmp.length; i++) {
                shouldAdd[i] = tmp[i];
            }
            shouldAdd[tmp.length] = c;
            // } else {
            // clientSettingsExist[clientSettingsIndex] = c;
            // }
        }
        for (ClientSettings settings : shouldAdd) {
            contest.addClientSettings(settings);
        }
    }

    public void setDefaultNumberOfAccounts() {
        checkAndGenerate(ClientType.Type.ADMINISTRATOR);
        checkAndGenerate(ClientType.Type.JUDGE);
        checkAndGenerate(ClientType.Type.TEAM);
        checkAndGenerate(ClientType.Type.SCOREBOARD);
        setAllRandomPasswords();
    }

    public void generateSingleAccount(ClientType.Type type) {
        contest.generateNewAccounts(String.valueOf(type), 1, true);
    }

    private void checkAndGenerate(ClientType.Type type) {
        int existingAmtOfAcc = getAccountsOfType(type).size();
        if (getDefaultFrom(type) > existingAmtOfAcc)
            contest.generateNewAccounts(String.valueOf(type), getDefaultFrom(type) - existingAmtOfAcc, true);
    }

    private int getDefaultFrom(ClientType.Type type) {
        switch (type) {
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

    private Vector<Account> getAccountsOfType(ClientType.Type type) {
        return contest.getAccounts(type);
    }

    public void bulkAddProblems(File inoutdir) {
        bulkAddProblems(inoutdir, null);
    }

    public String parseName(String name) {
        boolean isIn = name.endsWith(".in");
        name = name.substring(0, name.length() - (isIn ? ".in" : ".out").length());
        String[] splitName = name.split("_");
        String newString = "";
        for (int i = 0; i < splitName.length; i++) {
            newString += (i == 0 ? "" : " ") + Character.toUpperCase(splitName[i].charAt(0))
                    + splitName[i].substring(1);
        }
        return newString;
    }

    private File[] getFilesWithName(File folder, String name) {
        File[] files = folder.listFiles();
        Vector<File> ret = new Vector<File>();
        for (File f : files) {
            if ((f.isDirectory() && f.getName().equals(name))
                    || (f.isFile() && f.getName().substring(0, f.getName().lastIndexOf(".")).equals(name))) {
                ret.add(f);
            }
        }
        return ret.toArray(new File[0]);
    }

    private String getFileNameWithoutExtension(File f) {
        return f.isDirectory() ? f.getName() : f.getName().substring(0, f.getName().lastIndexOf("."));
    }

    public void bulkAddProblems(File testCases, File problemList) {
        if(problemList == null) {
            System.out.println("No problem list.");
            return;
        }
        try {
            Scanner scan = new Scanner(problemList);

            if (testCases != null) {
                DefaultProblem problem;
                while (scan.hasNext()) {
                    String testCaseName = scan.nextLine().trim();
                    String name = scan.nextLine().trim();

                    File[] testCaseNameFiles = getFilesWithName(testCases, testCaseName);
                    if (testCaseNameFiles == null) {
                        System.out.println("No test case folder for " + testCaseName);
                    }
                    //TODO: DUPLICATED CODE
                    HashMap<String, InOutPair> testCaseFiles = new HashMap<String, InOutPair>();
                    for (File f : testCaseNameFiles) {
                        if (f.isDirectory()) {
                            File[] inouts = f.listFiles();
                            Arrays.sort(inouts);

                            for(File inout : inouts){
                                if(!inout.getName().startsWith(".")){
                                    String inoutName = getFileNameWithoutExtension(inout);
                                    if(testCaseFiles.get(inoutName) == null) {
                                        testCaseFiles.put(inoutName, new InOutPair());
                                        testCaseFiles.get(inoutName).put(inout);
                                    }else{
                                        testCaseFiles.get(inoutName).put(inout);
                                    }
                                }
                            }
                        }else{
                            if(!f.getName().startsWith(".")){
                                String inoutName = getFileNameWithoutExtension(f);
                                if(testCaseFiles.get(inoutName) == null) {
                                    testCaseFiles.put(inoutName, new InOutPair());
                                    testCaseFiles.get(inoutName).put(f);
                                } else
                                    testCaseFiles.get(inoutName).put(f);
                            }
                        }
                    }

                    SerializedFile[] ins = new SerializedFile[testCaseFiles.size()];
                    SerializedFile[] outs = new SerializedFile[testCaseFiles.size()];

                    for (int i = 0; i < testCaseFiles.values().size(); i++) {
                        InOutPair pair = testCaseFiles.values().toArray(new InOutPair[0])[i];
                        ins[i] = pair.infile != null ? new SerializedFile(pair.infile.getAbsolutePath()) : null;
                        outs[i] = pair.outfile != null ? new SerializedFile(pair.outfile.getAbsolutePath()) : null;
                    }

                    try {
                        problem = new DefaultProblem(name, testCaseName, ins, outs);
                        addProblem(problem);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.out.println("Mismatched input output files.\nYou are missing an output file or you don't have input values where there should be.");
                    }
                }
                scan.close();
            } else {
                System.out.println("Your inout directory does not exist.");
            }
        } catch (FileNotFoundException | ProblemException ex) {
            ex.printStackTrace();
            System.out.println("Invalid file input.");
        }
    }

    public String getPathToBin() {
        return pathToBin;
    }

    private class InOutPair {
        public File infile, outfile;

        public void put(File x) {
            if (x.getName().endsWith(".dat") || x.getName().endsWith(".in"))
                infile = x;
            else if (x.getName().endsWith(".out") || x.getName().endsWith(".ans"))
                outfile = x;
        }
    }

    public void setGeneratedPasswords(File file) throws Exception {
        String[] lines;
        try {
            lines = loadFile(file.getAbsolutePath());
        } catch (IOException iOException) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        if (lines.length < 1)
            throw new FileNotFoundException(file.getAbsolutePath());
        int numberOfPasswords = lines.length;
        Vector<Account> accounts = contest.getAccounts(ClientType.Type.TEAM, contest.getSiteNumber());
        int numberOfTeams = accounts.size();
        if (numberOfPasswords > numberOfTeams)
            throw new Exception(
                    "Too few accounts, expecting " + numberOfPasswords + " accounts, found " + numberOfTeams);
        Account[] teams = accounts.toArray(new Account[accounts.size()]);
        Arrays.sort(teams, new AccountComparator());
        ArrayList<Account> accountList = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            teams[i].setPassword(lines[i]);
            accountList.add(teams[i]);
        }
        Account[] changedAccounts = accountList.toArray(new Account[accountList.size()]);

        for (int i = 0; i < changedAccounts.length; i++) {
            System.out.println(changedAccounts[i].getDisplayName() + ": " + changedAccounts[i].getPassword());
        }

        controller.updateAccounts(changedAccounts);
    }

    private String genPassword(int length) {
        // ASCII range - alphanumeric (0-9, a-z, A-Z)
        final String chars = "GV9Jm2u7rmsCe65wKzPTw5jtS38n2tVEGiijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // each iteration of loop choose a character randomly from the given ASCII range
        // and append it to StringBuilder instance

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    private void setAllRandomPasswords() {
        Vector<Account> accounts = contest.getAccounts(ClientType.Type.TEAM, contest.getSiteNumber());
        int numberOfTeams = accounts.size();
        Account[] teams = accounts.toArray(new Account[accounts.size()]);
        Arrays.sort(teams, new AccountComparator());
        ArrayList<Account> accountList = new ArrayList<>();
        System.out.println("Password List\n==========================");
        for (int i = 0; i < numberOfTeams; i++) {
            String pass = genPassword(8);
            System.out.println(pass);
            teams[i].setPassword(pass);
            accountList.add(teams[i]);
        }
        Account[] changedAccounts = accountList.toArray(new Account[accountList.size()]);
        controller.updateAccounts(changedAccounts);
    }

    private String[] loadFile(String filename) throws IOException {
        Vector<String> lines = new Vector<>();
        if (filename == null)
            throw new IllegalArgumentException("filename is null");
        if (!(new File(filename)).exists())
            return new String[0];
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
        String line = in.readLine();
        while (line != null) {
            lines.addElement(line);
            line = in.readLine();
        }
        in.close();
        in = null;
        if (lines.size() == 0)
            return new String[0];
        String[] out = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++)
            out[i] = lines.elementAt(i);
        return out;
    }

    public void test() {
        System.out.println(contest.getJudgements()[0].getDisplayName());
    }
}
