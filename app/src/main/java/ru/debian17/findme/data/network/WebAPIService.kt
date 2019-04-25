package ru.debian17.findme.data.network

import android.net.ConnectivityManager
import io.reactivex.Completable
import io.reactivex.Single
import ru.debian17.findme.data.model.auth.AuthParams
import ru.debian17.findme.data.model.auth.AuthResponse
import ru.debian17.findme.data.model.category.Category
import ru.debian17.findme.data.model.edge.EdgeInfo
import ru.debian17.findme.data.model.registration.RegistrationParams
import ru.debian17.findme.data.model.route.RouteParam
import ru.debian17.findme.data.model.route.RoutePoint

class WebAPIService(
    private val webAPI: WebAPI,
    private val connectivityManager: ConnectivityManager
) {

    private fun checkNetworkState() {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            throw NoNetworkException()
        }
    }

    private fun <T> prepareSingleRequest(request: Single<T>): Single<T> {
        return Completable.fromAction(this::checkNetworkState).andThen(request)
    }

    private fun prepareCompletableRequest(request: Completable): Completable {
        return Completable.fromAction(this::checkNetworkState).andThen(request)
    }

    fun registration(registrationParams: RegistrationParams): Completable {
        return prepareCompletableRequest(webAPI.registration(registrationParams))
    }

    fun auth(authParams: AuthParams): Single<AuthResponse> {
        return prepareSingleRequest(webAPI.auth(authParams))
    }

    fun buildRoute(routeParam: RouteParam): Single<List<RoutePoint>> {
        return prepareSingleRequest(webAPI.buildRoute(routeParam))
    }

    fun getAttributesOfEdge(edgeId: Int): Single<EdgeInfo> {
        return prepareSingleRequest(webAPI.getAttributesOfEdge(edgeId))
    }

    fun getCategories(): Single<List<Category>>{
        return prepareSingleRequest(webAPI.getCategories())
    }

}