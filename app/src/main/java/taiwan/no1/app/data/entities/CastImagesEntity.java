package taiwan.no1.app.data.entities;

import java.util.List;

/**
 * @author Jieyi
 * @since 1/1/17
 */

public class CastImagesEntity {
    private List<ImageInfoEntity> profiles;

    public List<ImageInfoEntity> getProfiles() { return profiles;}

    public void setProfiles(List<ImageInfoEntity> profiles) { this.profiles = profiles;}

    @Override
    public String toString() {
        return "CastImagesEntity{" +
                "profiles=" + profiles +
                '}';
    }
}
