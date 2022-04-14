package stellarnear.lost_ark_companion.VersionCheck;


import stellarnear.lost_ark_companion.BuildConfig;
import stellarnear.lost_ark_companion.Divers.Tools;

public class VersionComparator {
    private static final Tools tools = Tools.getTools();

    public VersionComparator() {
    }


    public static boolean isNewer(VersionData versionToTest, VersionData olderVersion) {
        boolean isNewer = false;
        String olderVersionName = olderVersion == null ? BuildConfig.VERSION_NAME.replace("v", "") : olderVersion.getVersion_name().replace("v", "");
        String[] olderParts = olderVersionName.split("\\.");

        Integer olderMaj = null;
        Integer olderMed = null;
        Integer olderMin = null;
        switch (olderParts.length) {
            case 3:
                olderMin = tools.toInt(olderParts[2]);
                olderMed = tools.toInt(olderParts[1]);
                olderMaj = tools.toInt(olderParts[0]);
                break;
            case 2:
                olderMed = tools.toInt(olderParts[1]);
                olderMaj = tools.toInt(olderParts[0]);
                break;
            case 1:
                olderMaj = tools.toInt(olderParts[0]);
                break;
            default:
                break;
        }


        String versionToTestName = versionToTest.getVersion_name().replace("v", "");
        String[] toTestParts = versionToTestName.split("\\.");

        Integer testMaj = null;
        Integer testMed = null;
        Integer testMin = null;
        switch (toTestParts.length) {
            case 3:
                testMin = tools.toInt(toTestParts[2]);
                testMed = tools.toInt(toTestParts[1]);
                testMaj = tools.toInt(toTestParts[0]);
                break;
            case 2:
                testMed = tools.toInt(toTestParts[1]);
                testMaj = tools.toInt(toTestParts[0]);
                break;
            case 1:
                testMaj = tools.toInt(toTestParts[0]);
                break;
            default:
                break;
        }

        if (testMaj != null && olderMaj != null && testMaj > olderMaj) {
            isNewer = true;
        } else if (testMaj != null && testMaj == olderMaj) {
            if (testMed != null && olderMed != null && testMed > olderMed) {
                isNewer = true;
            } else if (testMed != null && testMed == olderMed) {
                if (testMin != null && olderMin != null && testMin > olderMin) {
                    isNewer = true;
                }
            }
        }
        return isNewer;
    }
}


