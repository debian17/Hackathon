package ru.debian17.findme.app.ui.auth


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_auth.*
import ru.debian17.findme.R
import ru.debian17.findme.app.App
import ru.debian17.findme.app.mvp.BaseFragment
import ru.debian17.findme.app.ui.menu.MenuFragment
import ru.debian17.findme.app.ui.auth.registration.RegistrationFragment


class AuthFragment : BaseFragment(), AuthView {

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 123
        const val TAG = "AuthFragmentTag"
        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }

    @InjectPresenter
    lateinit var presenter: AuthPresenter

    @ProvidePresenter
    fun providePresenter(): AuthPresenter {
        return AuthPresenter(
                (activity!!.application as App).getDataSourceComponent()
                        .provideAuthRepository()
        )
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAuth.setOnClickListener {
            navigator.replaceScreen(MenuFragment.TAG)
        }

        btnRegistration.setOnClickListener {
            navigator.addFragment(RegistrationFragment.TAG)
        }
    }

    override fun showMenu() {
        navigator.replaceScreen(MenuFragment.TAG)
    }

    override fun checkPermission() {
        val locationPermission = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        val storagePermission = ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permissions = ArrayList<String>()
        if (locationPermission == PackageManager.PERMISSION_DENIED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (storagePermission == PackageManager.PERMISSION_DENIED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            grantResults.forEach { result ->
                if (result == PackageManager.PERMISSION_DENIED) {
                    activity!!.finish()
                    return@forEach
                }
            }
        }
    }

    override fun showLoading() {

    }

    override fun showMain() {

    }

    override fun showError(errorMessage: String?) {

    }

}
