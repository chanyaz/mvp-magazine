package taiwan.no1.app.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewStub
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.jakewharton.rxbinding.support.v4.view.pageSelections
import kotterknife.bindView
import taiwan.no1.app.R
import taiwan.no1.app.internal.di.annotations.PerFragment
import taiwan.no1.app.internal.di.components.FragmentComponent
import taiwan.no1.app.mvp.contracts.fragment.TvDetailContract
import taiwan.no1.app.mvp.models.FilmCastsModel
import taiwan.no1.app.mvp.models.FilmVideoModel
import taiwan.no1.app.mvp.models.IVisitable
import taiwan.no1.app.mvp.models.tv.TvBriefModel
import taiwan.no1.app.mvp.models.tv.TvSeasonsModel
import taiwan.no1.app.ui.BaseFragment
import taiwan.no1.app.ui.adapter.BackdropPagerAdapter
import taiwan.no1.app.ui.adapter.CommonRecyclerAdapter
import taiwan.no1.app.ui.adapter.itemdecorator.MovieHorizontalItemDecorator
import taiwan.no1.app.utilies.ImageLoader.IImageLoader
import java.util.Arrays.asList
import javax.inject.Inject

/**
 *
 * @author  Jieyi
 * @since   2/12/17
 */
@PerFragment
class TvDetailFragment : BaseFragment(), TvDetailContract.View {
    //region Static initialization
    companion object Factory {
        // The key name of the fragment initialization parameters.
        private const val ARG_PARAM_TV_ID: String = "param_tv_id"
        private const val ARG_PARAM_FROM_ID: String = "param_tv_from_fragment"

        /**
         * Use this factory method to create a new instance of this fragment using the provided parameters.
         *
         * @return A new instance of [TvDetailFragment].
         */
        fun newInstance(id: String, from: Int): TvDetailFragment = TvDetailFragment().apply {
            this.arguments = Bundle().apply {
                this.putString(ARG_PARAM_TV_ID, id)
                this.putInt(ARG_PARAM_FROM_ID, from)
            }
        }
    }
    //endregion

    @Inject
    lateinit var presenter: TvDetailContract.Presenter
    @Inject
    lateinit var imageLoader: IImageLoader
    // Prevent the selection icon will be strange after returning from the gallery fragment.
    private var isResume: Boolean = true

    //region View variables
    private val vpDropPoster by bindView<ViewPager>(R.id.vp_drop_poster)
    private val ibLeft by bindView<ImageButton>(R.id.ib_left_slide)
    private val ibRight by bindView<ImageButton>(R.id.ib_right_slide)
    private val tvTitle by bindView<TextView>(R.id.tv_title)
    private val tvStatus by bindView<TextView>(R.id.tv_status)
    private val tvVoteRate by bindView<TextView>(R.id.tv_vote)
    private val tvSeasonCount by bindView<TextView>(R.id.tv_season_count)
    private val tvRunTime by bindView<TextView>(R.id.tv_run_time)
    private val stubBriefIntro by bindView<ViewStub>(R.id.stub_tv_brief_intro)
    private val stubIntro by bindView<ViewStub>(R.id.stub_introduction)
    private val stubSeasons by bindView<ViewStub>(R.id.stub_seasons)
    private val stubCasts by bindView<ViewStub>(R.id.stub_casts)
    private val stubCrews by bindView<ViewStub>(R.id.stub_crews)
    private val stubRelated by bindView<ViewStub>(R.id.stub_related)
    private val stubTrailer by bindView<ViewStub>(R.id.stub_trailer)
    private val tvTitleOverview by bindView<TextView>(R.id.tv_title_overview)
    private val tvTitleLastAirDate by bindView<TextView>(R.id.tv_title_last_air_date)
    private val tvTitleLanguage by bindView<TextView>(R.id.tv_title_language)
    private val tvTitleHomepage by bindView<TextView>(R.id.tv_title_homepage)
    private val tvTitleProduction by bindView<TextView>(R.id.tv_title_productions)
    private val tvOverview by bindView<TextView>(R.id.tv_overview)
    private val tvLastAirDate by bindView<TextView>(R.id.tv_last_air_date)
    private val tvLanguage by bindView<TextView>(R.id.tv_language)
    private val tvHomepage by bindView<TextView>(R.id.tv_homepage)
    private val tvProduction by bindView<TextView>(R.id.tv_productions)
    private val tvRelatedTitle by bindView<TextView>(R.id.tv_related_title)
    private val rvSeasons by bindView<RecyclerView>(R.id.rv_seasons)
    private val rvCasts by bindView<RecyclerView>(R.id.rv_casts)
    private val rvCrews by bindView<RecyclerView>(R.id.rv_crews)
    private val rvRelated by bindView<RecyclerView>(R.id.rv_related)
    private val rvTrailer by bindView<RecyclerView>(R.id.rv_trailer)
    //endregion

    // Get the arguments from the bundle here.
    private val id: String by lazy { this.arguments.getString(ARG_PARAM_TV_ID) }
    private val argFromFragment: Int by lazy { this.arguments.getInt(ARG_PARAM_FROM_ID) }

    //region Fragment lifecycle
    override fun onResume() {
        super.onResume()
        this.presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        this.presenter.pause()
    }

    override fun onDestroy() {
        // After super.onDestroy() is executed, the presenter will be destroy. So the presenter should be
        // executed before super.onDestroy().
        this.presenter.destroy()
        super.onDestroy()
    }
    //endregion

    //region Initialization's order
    /**
     * Inject this fragment and [FragmentComponent].
     */
    override fun inject() {
        this.getComponent(FragmentComponent::class.java).inject(TvDetailFragment@ this)
    }

    /**
     * Set this fragment xml layout.
     *
     * @return [LayoutRes] xml layout.
     */
    @LayoutRes
    override fun inflateView(): Int = R.layout.fragment_tv_detail

    /**
     * Set the presenter initialization in [onCreateView].
     */
    override fun initPresenter() {
        this.presenter.init(TvDetailFragment@ this)
    }

    /**
     * Initialization of this fragment. Set the listeners or view components' attributions.
     *
     * @param savedInstanceState the previous fragment data status after the system calls [onPause].
     */
    override fun init(savedInstanceState: Bundle?) {
        this.showLoading()

        this.presenter.requestListTvs(this.id.toInt())
        this.isResume = true

        View.OnClickListener { view ->
            this.vpDropPoster.currentItem.let {
                when (view) {
                    this.ibLeft -> it - 1
                    this.ibRight -> it + 1
                    else -> it
                }
                // Set the view pager to the assigned page.
            }.let { nextPosition -> this.vpDropPoster.setCurrentItem(nextPosition, true) }
        }.let {
            this.ibLeft.setOnClickListener(it)
            this.ibRight.setOnClickListener(it)
        }
        // FIXED: 4/2/17 The selection is strange is fixed by using a flag(isResume).
        this.vpDropPoster.pageSelections().compose(this.bindToLifecycle<Int>()).subscribe {
            this.presenter.scrollBackdropTo(if (isResume) 0 else it)
        }
        this.isResume = false
    }
    //endregion

    //region View implementations
    override fun showTvBackdrops(viewList: List<View>) {
        this.vpDropPoster.adapter = BackdropPagerAdapter(viewList)
    }

    override fun showTvSingleBackdrop(uri: String, imageview: ImageView) {
        this.imageLoader.display(uri, listener = object : BitmapImageViewTarget(imageview) {
            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                this@TvDetailFragment.presenter.onResourceFinished(imageview, this@TvDetailFragment.argFromFragment)
                super.onResourceReady(resource, glideAnimation)
            }
        }, isFitCenter = false)
    }

    override fun setLeftSlideButton(visibility: Int) {
        this.ibLeft.visibility = visibility
    }

    override fun setRightSlideButton(visibility: Int) {
        this.ibRight.visibility = visibility
    }

    override fun showTvBriefInfo(title: String, status: String, rate: String, seasonCount: String, runTime: String) {
        this.showViewStub(this.stubBriefIntro, {
            this.tvTitle.text = title
            this.tvStatus.text = status
            this.tvVoteRate.text = rate
            this.tvSeasonCount.text = seasonCount
            this.tvRunTime.text = runTime
        })
    }

    override fun showTvDetail(overview: String, lastAirDate: String, language: String, homepage: String,
                              productions: String) {
        this.showViewStub(this.stubIntro, {
            this.showText(overview, this.tvOverview, asList(this.tvOverview, this.tvTitleOverview))
            this.showText(lastAirDate, this.tvLastAirDate, asList(this.tvLastAirDate, this.tvTitleLastAirDate))
            this.showText(language, this.tvLanguage, asList(this.tvLanguage, this.tvTitleLanguage))
            this.showText(homepage, this.tvHomepage, asList(this.tvHomepage, this.tvTitleHomepage))
            this.showText(productions, this.tvProduction, asList(this.tvProduction, this.tvTitleProduction))
        })
    }

    override fun showTvSeasons(seasons: List<TvSeasonsModel>) {
        // Inflate the season section.
        if (seasons.isNotEmpty())
            this.showViewStub(this.stubSeasons, { this.showCardItems(this.rvSeasons, seasons) })
    }

    override fun showTvCasts(casts: List<FilmCastsModel.CastBean>) {
        // Inflate the cast section.
        if (casts.isNotEmpty())
            this.showViewStub(this.stubCasts, { this.showCardItems(this.rvCasts, casts) })
    }

    override fun showTvCrews(crews: List<FilmCastsModel.CrewBean>) {
        // Inflate the crew section.
        if (crews.isNotEmpty())
            this.showViewStub(this.stubCrews, { this.showCardItems(this.rvCrews, crews) })
    }

    override fun showRelatedTvs(relatedTvs: List<TvBriefModel>) {
        // Inflate the related movieList section.
        if (relatedTvs.isNotEmpty())
            this.showViewStub(this.stubRelated, {
                this.tvRelatedTitle.text = "Related Tvs"
                this.showCardItems(this.rvRelated, relatedTvs)
            })
    }

    override fun showTvTrailers(trailers: List<FilmVideoModel>) {
        // Inflate the trailer movieList section.
        if (trailers.isNotEmpty())
            this.showViewStub(this.stubTrailer, { this.showCardItems(this.rvTrailer, trailers) })
    }
    //endregion

    private fun <T : IVisitable> showCardItems(recyclerView: RecyclerView, list: List<T>) {
        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(this.context, HORIZONTAL, false)
            this.adapter = CommonRecyclerAdapter(list, argFromFragment)
            this.addItemDecoration(MovieHorizontalItemDecorator(30))
        }
    }
}
