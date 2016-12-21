package taiwan.no1.accounting.internal.di.components

import dagger.Component
import taiwan.no1.accounting.App
import taiwan.no1.accounting.data.source.CloudDataStore
import taiwan.no1.accounting.internal.di.modules.NetModule
import javax.inject.Singleton

/**
 *
 * @author  Jieyi Wu
 * @version 0.0.1
 * @since   12/8/16
 */

@Singleton
@Component(modules = arrayOf(NetModule::class))
interface NetComponent {
    object Initializer {
        @JvmStatic fun init(): NetComponent = DaggerNetComponent.builder()
                .netModule(NetModule(App.getAppContext()))
                .build()
    }

    fun inject(cloudDataStore: CloudDataStore)
}
