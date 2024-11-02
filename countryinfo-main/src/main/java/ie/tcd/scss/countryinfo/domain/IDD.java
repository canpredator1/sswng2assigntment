package ie.tcd.scss.countryinfo.domain;

import java.util.List;

/**
 * International direct dialing
 * https://en.wikipedia.org/wiki/International_direct_dialing
 * https://en.wikipedia.org/wiki/List_of_international_call_prefixes
 */

public class IDD {
    private String root;
    private List<String> suffixes;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public List<String> getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(List<String> suffixes) {
        this.suffixes = suffixes;
    }
}
