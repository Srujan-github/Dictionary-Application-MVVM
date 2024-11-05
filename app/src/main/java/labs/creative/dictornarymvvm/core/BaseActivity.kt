package labs.creative.dictornarymvvm.core


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import labs.creative.dictornarymvvm.core.utils.NetworkUtils

open class BaseActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun isNetworkConnected(): Boolean {
        return labs.creative.dictornarymvvm.core.utils.NetworkUtils.isNetworkConnected(applicationContext)
    }

}