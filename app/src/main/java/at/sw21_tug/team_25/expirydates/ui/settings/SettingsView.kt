package at.sw21_tug.team_25.expirydates.ui.settings

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import at.sw21_tug.team_25.expirydates.MainActivity
import at.sw21_tug.team_25.expirydates.R
import at.sw21_tug.team_25.expirydates.data.ExpItem
import at.sw21_tug.team_25.expirydates.data.ExpItemDao
import at.sw21_tug.team_25.expirydates.data.ExpItemDatabase
import at.sw21_tug.team_25.expirydates.misc.Util
import at.sw21_tug.team_25.expirydates.ui.detailview.ui.DetailView
import at.sw21_tug.team_25.expirydates.utils.GlobalSettings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SettingsView(private val view: View) : TimePickerDialog.OnTimeSetListener {

    companion object {

        fun openSettingsView(activity: Activity) {

            val inflater: LayoutInflater =
                    activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            // settings view
            val popupView = inflater.inflate(R.layout.fragment_settings, null)

            val popupWindow = PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            )

            activity.findViewById<View>(android.R.id.content).post {
                val view = activity.findViewById<View>(android.R.id.content).rootView
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
            }

            val dayEdit = popupView.findViewById<EditText>(R.id.day_selection)
            val closeSettingsButton = popupView.findViewById<Button>(R.id.closeSettings)
            val saveSettingsButton = popupView.findViewById<Button>(R.id.saveSettings)
            val enLanguageButton = popupView.findViewById<Button>(R.id.bt_lang_en)
            val ruLanguageButton = popupView.findViewById<Button>(R.id.bt_lang_ru)

            enLanguageButton.setOnClickListener {
                Util.setLanguage("en", activity)
                Util.setLocale(activity, Locale("en"))
                (activity as MainActivity).refreshCurrentFragment()
            }

            ruLanguageButton.setOnClickListener {
                Util.setLanguage("ru", activity)
                Util.setLocale(activity, Locale("ru"))
                (activity as MainActivity).refreshCurrentFragment()
            }

            saveSettingsButton.setOnClickListener {
                //TODO save values
                var days = dayEdit.text.toString()
                if (days.equals(""))
                    days = "0"
                GlobalSettings.setNotificationDayOffset(activity, days.toInt())
                popupWindow.dismiss()
            }

            closeSettingsButton.setOnClickListener {
                popupWindow.dismiss()
            }

            //(activity as MainActivity).requestUpdates(R.id.navigation_list)

            dayEdit.editableText.clear()
            //nameEdit.editableText.append("test")

            popupWindow.elevation = 10.0F
            popupWindow.isFocusable = true
            // set on-click listener

            /*            nameEdit.setOnClickListener {
+
+                    nameEdit.editableText.append(activity.getString(R.string.save))
+            }*/

        }

        private fun save(productName: String, date: String, activity: Activity) {

        }

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        TODO("Not yet implemented")
    }
}
