package tk.jonathancowling.rxjavatoy.features.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import tk.jonathancowling.rxjavatoy.R
import tk.jonathancowling.rxjavatoy.business.events.IntEventEmitter
import tk.jonathancowling.rxjavatoy.business.events.IntEventListener
import tk.jonathancowling.rxjavatoy.features.multiplerequests.MultipleRequestsActivity
import tk.jonathancowling.rxjavatoy.features.oneaftertheother.OneRequestAfterTheOtherActivity
import tk.jonathancowling.rxjavatoy.features.reactivevscallbacks.ReactiveVsCallbacksActivity
import kotlinx.android.synthetic.main.activity_example_launcher.*
import kotlinx.android.synthetic.main.app_bar_example_launcher.*

class DemoLauncherActivity : AppCompatActivity(), DemoLauncherView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_launcher)
        setSupportActionBar(toolbar)

        DemoLauncherPresenter(this, object: IntEventEmitter.Factory {
            override fun create(listener: IntEventListener): NavigationDrawerEventEmitter {
                val emitter = NavigationDrawerEventEmitter(listener, drawer_layout)
                nav_view.setNavigationItemSelectedListener(emitter)
                return emitter
            }
        })

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.example_launcher, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun launchMultiple() {
        quickLaunch(MultipleRequestsActivity::class.java)
    }

    override fun launchOneAfterTheOther() {
        quickLaunch(OneRequestAfterTheOtherActivity::class.java)
    }

    override fun launchReactiveVsCallbacks() {
        quickLaunch(ReactiveVsCallbacksActivity::class.java)
    }

    private fun <T : Activity> quickLaunch(clazz: Class<T>) {
        startActivity(Intent(this, clazz))
    }

    class NavigationDrawerEventEmitter(private val listener: IntEventListener, private val drawerLayout: DrawerLayout) : IntEventEmitter, NavigationView.OnNavigationItemSelectedListener {
        override fun emitEvent(eventId: Int) = listener.onEvent(eventId)

        override fun onNavigationItemSelected(p0: MenuItem): Boolean {
            val eventHandled =  emitEvent(p0.itemId)
            if (eventHandled) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            return eventHandled
        }

    }

    companion object {
        const val MULTIPLE_REQUESTS = R.id.multiple_launcher
        const val ONE_AFTER_THE_OTHER = R.id.one_after_the_other_launcher
        const val REACTIVE_VS_CALLBACKS = R.id.reactive_vs_callbacks_launcher
    }

}
