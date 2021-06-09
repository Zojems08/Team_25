package at.sw21_tug.team_25.expirydates

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.sw21_tug.team_25.expirydates.data.ExpItemDatabase
import at.sw21_tug.team_25.expirydates.ui.detailview.ui.DetailView
import at.sw21_tug.team_25.expirydates.utils.NotificationManager
import at.sw21_tug.team_25.expirydates.utils.ReminderScheduler
import at.sw21_tug.team_25.expirydates.utils.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView
    lateinit var updateLayoutList: ArrayList<Int>
    private lateinit var navMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // without this, the title is not updated on other fragments on first load
        updateLayoutList = ArrayList()

        val language = Util.getLanguage(this)
        Util.setLocale(this, language)

        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_add, R.id.navigation_list
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        NotificationManager.whenAppWasStartedByExpiryNotification(this.intent) {
            navController.navigate(R.id.navigation_list)

            GlobalScope.async {
                val dao = ExpItemDatabase.getDatabase(applicationContext).expItemDao()
                val item = dao.getItemByID(it.toLong())
                if (item != null) {
                    runOnUiThread {
                        DetailView.openDetailView(this@MainActivity, item)
                    }
                } else {
                    Log.d("MainActivity", "no item found for id $it")
                }
            }
        }

        GlobalScope.async {
            ReminderScheduler.ensureNextReminderScheduled(applicationContext)
        }
        navMenu = navView.menu
    }


    fun refreshCurrentFragment() {
        val id = navController.currentDestination?.id

        val home = navMenu.findItem(R.id.navigation_home)
        if (home != null) {
            home.title = getString(R.string.title_home)
        }
        val add = navMenu.findItem(R.id.navigation_add)
        if (add != null) {
            add.title = getString(R.string.title_add)
        }
        val list = navMenu.findItem(R.id.navigation_list)
        if (list != null) {
            list.title = getString(R.string.title_list)
        }
        updateTitle()

        navController.popBackStack(id!!, true)
        navController.navigate(id)
    }

    fun updateTitle() {
        when (navController.currentDestination?.id) {
            R.id.navigation_add -> navController.currentDestination?.label =
                getString(R.string.title_add)
            R.id.navigation_home -> navController.currentDestination?.label =
                getString(R.string.title_home)
            R.id.navigation_list -> navController.currentDestination?.label =
                getString(R.string.title_list)
        }
    }

    fun requestUpdates(fragToIgnore: Int) {
        updateLayoutList.clear()
        updateLayoutList.add(R.id.navigation_home)
        updateLayoutList.add(R.id.navigation_add)
        updateLayoutList.add(R.id.navigation_list)
        updateLayoutList.remove(fragToIgnore)
    }
}
