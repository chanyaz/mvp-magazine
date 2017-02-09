package taiwan.no1.app.data.entities;

import java.util.List;

/**
 * @author Jieyi
 * @since 2/4/17
 */

public class TvBriefEntity extends BriefEntity {
    private String first_air_date;
    private String name;
    private String original_name;
    private List<String> origin_country;

    public String getFirst_air_date() { return first_air_date;}

    public void setFirst_air_date(String first_air_date) { this.first_air_date = first_air_date;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getOriginal_name() { return original_name;}

    public void setOriginal_name(String original_name) { this.original_name = original_name;}

    public List<String> getOrigin_country() { return origin_country;}

    public void setOrigin_country(List<String> origin_country) { this.origin_country = origin_country;}

    @Override
    public String toString() {
        return "TvBriefEntity{" +
                "first_air_date='" + first_air_date + '\'' +
                ", name='" + name + '\'' +
                ", original_name='" + original_name + '\'' +
                ", origin_country=" + origin_country +
                '}';
    }
}