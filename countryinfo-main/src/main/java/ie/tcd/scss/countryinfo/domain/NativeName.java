package ie.tcd.scss.countryinfo.domain;

import java.util.Map;

public class NativeName {

    private Map<String, Language> eng;
    private Map<String, Language> gle;

    public Map<String, Language> getEng() {
        return eng;
    }

    public void setEng(Map<String, Language> eng) {
        this.eng = eng;
    }

    public Map<String, Language> getGle() {
        return gle;
    }

    public void setGle(Map<String, Language> gle) {
        this.gle = gle;
    }

    // Add more languages as needed
}
