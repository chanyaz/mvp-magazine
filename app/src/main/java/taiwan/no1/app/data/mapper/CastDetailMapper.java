package taiwan.no1.app.data.mapper;

import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import taiwan.no1.app.data.entities.CastDetailEntity;
import taiwan.no1.app.domain.mapper.IBeanMapper;
import taiwan.no1.app.mvp.models.CastDetailModel;
import taiwan.no1.app.mvp.models.CastImagesModel;
import taiwan.no1.app.mvp.models.CreditsInFilmModel;
import taiwan.no1.app.mvp.models.ImageProfileModel;

/**
 * Mapper class used to transform between {@link CastDetailModel} (in the kotlin layer) and {@link CastDetailEntity}
 * (in the data layer).
 *
 * @author Jieyi
 * @since 12/28/16
 */

@Singleton
public class CastDetailMapper implements IBeanMapper<CastDetailModel, CastDetailEntity> {
    @Inject ImageInfoMapper imageInfoMapper;
    @Inject CreditsMapper creditsMapper;

    @Inject
    public CastDetailMapper() {
    }

    /**
     * Implement {@inheritDoc}
     */
    @NonNull
    @Override
    @Deprecated
    public CastDetailEntity transformFrom(@NonNull CastDetailModel model) {
        throw new Error("No-op");
    }

    /**
     * Implement {@inheritDoc}
     */
    @NonNull
    @Override
    public CastDetailModel transformTo(@NonNull CastDetailEntity entity) {
        List<ImageProfileModel> imageInfoModels = Queryable.from(entity.getImages().getProfiles())
                                                           .map(this.imageInfoMapper::transformTo)
                                                           .toList();
        CreditsInFilmModel creditsModels = this.creditsMapper.transformTo(entity.getCombined_credits());

        return new CastDetailModel(entity.isAdult(),
                                   entity.getBiography(),
                                   entity.getBirthday(),
                                   entity.getDeathday(),
                                   entity.getGender(),
                                   entity.getHomepage(),
                                   entity.getId(),
                                   entity.getImdb_id(),
                                   entity.getName(),
                                   entity.getPlace_of_birth(),
                                   entity.getPopularity(),
                                   entity.getProfile_path(),
                                   entity.getAlso_known_as(),
                                   new CastImagesModel(imageInfoModels),
                                   creditsModels);
    }
}
