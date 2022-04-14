package stellarnear.lost_ark_companion.VersionCheck;

public class VersionData {
    private String version_name;
    private Integer version_code;
    private String release_date;
    private String dl_link; // from direct https://filetransfer.io/
    private String patch_note;

    public VersionData() {

    }

    public Integer getVersion_code() {
        return version_code;
    }

    public String getDl_link() {
        return dl_link;
    }

    public String getPatch_note() {
        return patch_note;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getVersion_name() {
        return version_name;
    }
}
