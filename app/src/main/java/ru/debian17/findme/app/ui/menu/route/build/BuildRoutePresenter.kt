package ru.debian17.findme.app.ui.menu.route.build

import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.osmdroid.util.GeoPoint
import ru.debian17.findme.app.dal.AttributesDataSource
import ru.debian17.findme.app.dal.CategoriesDataSource
import ru.debian17.findme.app.dal.LocationDataSource
import ru.debian17.findme.app.ext.observeOnUI
import ru.debian17.findme.app.ext.subscribeOnIO
import ru.debian17.findme.app.mvp.BasePresenter
import ru.debian17.findme.data.model.category.Category
import ru.debian17.findme.data.model.edge.EdgeInfo
import ru.debian17.findme.data.model.route.RoutePoint
import ru.debian17.findme.data.network.error.ErrorCode

@InjectViewState
class BuildRoutePresenter(
    private val locationDataSource: LocationDataSource,
    private val attributesDataSource: AttributesDataSource,
    private val categoriesDataSource: CategoriesDataSource
) : BasePresenter<BuildRouteView>() {

    private val updateCurrentLocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            if (p0 != null) {
                viewState.updateLocation(p0.lastLocation)
            }
        }
    }

    override fun onFirstViewAttach() {
        locationDataSource.subscribe(updateCurrentLocationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationDataSource.unsubscribe(updateCurrentLocationCallback)
    }

    fun buildRoute(startPoint: GeoPoint, endPoint: GeoPoint) {
        viewState.showLoading()
        unsubscribeOnDestroy(locationDataSource.buildRoute(startPoint, endPoint)
            .subscribeOnIO()
            .doOnError {
                errorBody = getError(it)
            }
            .observeOnUI()
            .subscribe(this::onRouteSuccess, this::onRouteError))
    }

    private fun onRouteSuccess(routePoints: List<RoutePoint>) {
        viewState.showMain()
        viewState.onBuildRoute(routePoints)
    }

    private fun onRouteError(throwable: Throwable) {
        viewState.showMain()
        viewState.onBuildRouteError(errorBody?.code ?: ErrorCode.UNKNOWN_ERROR)
    }

    fun getAttributesOfEdge(edgeId: Int) {
        viewState.showLoading()

        val attributesOfEdgeSource = attributesDataSource.getAttributesOfEdge(edgeId)

        val categoriesSource = categoriesDataSource.getCategories()

        unsubscribeOnDestroy(
            Single.zip(attributesOfEdgeSource, categoriesSource,
                BiFunction<EdgeInfo, List<Category>, Pair<EdgeInfo, List<Category>>> { t1, t2 ->
                    return@BiFunction Pair(t1, t2)
                })
                .subscribeOnIO()
                .doOnError {
                    errorBody = getError(it)
                }
                .observeOnUI()
                .subscribe(this::onEdgeInfoLoaded, this::onEdgeInfoError)
        )

    }

    private fun onEdgeInfoLoaded(data: Pair<EdgeInfo, List<Category>>) {
        viewState.showMain()
        viewState.onEdgeInfoLoaded(data.first, data.second)
    }

    private fun onEdgeInfoError(throwable: Throwable) {
        viewState.showMain()
        viewState.onAttributesOfEdgeError(errorBody?.code ?: ErrorCode.UNKNOWN_ERROR)
    }

}