package at.sw21_tug.team_25.expirydates


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import at.sw21_tug.team_25.expirydates.data.ExpItem
import at.sw21_tug.team_25.expirydates.data.ExpItemDao
import at.sw21_tug.team_25.expirydates.data.ExpItemDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.Is
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
@FixMethodOrder
class DetailViewTest {

    companion object {
        private lateinit var expItemDao: ExpItemDao
        private lateinit var db: ExpItemDatabase
        private val testDispatcher = TestCoroutineDispatcher()

        @BeforeClass
        @JvmStatic
        fun setup() {
            Dispatchers.setMain(testDispatcher)
            val context = InstrumentationRegistry.getInstrumentation().targetContext

            db = ExpItemDatabase.getDatabase(context)

            expItemDao = db.expItemDao()
        }
//        - - - - - INFO: Causes errors in following test, that also use ExpItemDatabase - - - -
//        @AfterClass
//        @JvmStatic
//        @Throws(IOException::class)
//        fun closeDb() {
//            db.close()
//        }
    }

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun initializeDb() {
        val expItemDao = ExpItemDatabase.getDatabase(
            InstrumentationRegistry.getInstrumentation().targetContext
        ).expItemDao()
        expItemDao.deleteAllItems()
        GlobalScope.async {
            expItemDao.insertItem(ExpItem("Salami", "2021-01-01 01:01:01"))
            expItemDao.insertItem(ExpItem("Tomato", "2021-01-02 02:02:02"))
            expItemDao.insertItem(ExpItem("Bread", "2021-01-03 03:03:03"))
        }
    }

    @Test
    fun detailViewTest() {
        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.navigation_list), withContentDescription("List"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_view),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val materialTextViewSalami = onView(
            allOf(
                withId(R.id.item_tv), withText("Salami  2021-01-01 01:01:01"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.items_rv),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialTextViewSalami.perform(click())

        onView(withId(R.id.detail_view_popup)).inRoot(RootMatchers.isPlatformPopup())
            .check((matches(isDisplayed())))
        onView(withId(R.id.product_name)).inRoot(RootMatchers.isPlatformPopup())
            .check(matches(withText("Salami")))
        onView(withId(R.id.exp_date)).inRoot(RootMatchers.isPlatformPopup())
            .check(matches(withText("2021-01-01 01:01:01")))
        onView(withId(R.id.closePopUp)).inRoot(RootMatchers.isPlatformPopup()).perform(click())
        onView(withId(R.id.detail_view_popup)).check(doesNotExist())
    }


    @Test
    fun deleteItemTest() {
        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.navigation_list), withContentDescription("List"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_view),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val materialTextViewSalami = onView(
            allOf(
                withId(R.id.item_tv), withText("Tomato  2021-01-02 02:02:02"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.items_rv),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialTextViewSalami.perform(click())

        onView(withId(R.id.detail_view_popup)).inRoot(RootMatchers.isPlatformPopup())
            .check((matches(isDisplayed())))
        onView(withId(R.id.product_name)).inRoot(RootMatchers.isPlatformPopup())
            .check(matches(withText("Tomato")))
        onView(withId(R.id.exp_date)).inRoot(RootMatchers.isPlatformPopup())
            .check(matches(withText("2021-01-02 02:02:02")))
        onView(withId(R.id.deleteItem)).inRoot(RootMatchers.isPlatformPopup()).perform(click())
        onView(withId(R.id.detail_view_popup)).check(doesNotExist())

        onView(withId(R.id.items_rv)).check(ListFragmentTest.RecyclerViewItemCountAssertion(2))


    }

    @Test
    fun editItemTest() {
        val bottomNavigationItemView2 = onView(
            allOf(
                withId(R.id.navigation_list), withContentDescription("List"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_view),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView2.perform(click())

        val materialTextView = onView(
            allOf(
                withId(R.id.item_tv), withText("Salami  2021-01-01 01:01:01"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.items_rv),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.edit)).inRoot(RootMatchers.isPlatformPopup()).perform(click())

        onView(withId(R.id.exp_date)).inRoot(RootMatchers.isPlatformPopup()).perform(click())

        val materialButton4 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(Is.`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton4.perform(ViewActions.scrollTo(), click())

        onView(withId(R.id.product_name_edit)).perform(ViewActions.replaceText("Hauswurst"))

        onView(withId(R.id.edit)).inRoot(RootMatchers.isPlatformPopup()).perform(click())

        onView(withId(R.id.closePopUp)).inRoot(RootMatchers.isPlatformPopup()).perform(click())

        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val currentDateFormatted = currentDate.format(formatter)

        val textView = onView(
            allOf(
                withId(R.id.item_tv), withText("Hauswurst  $currentDateFormatted"),
                withParent(withParent(withId(R.id.items_rv))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Hauswurst  $currentDateFormatted")))
    }

    @Test
    fun shareViewTest() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.navigation_list), withContentDescription("List"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_view),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val shareTextView = onView(
            allOf(
                withId(R.id.item_tv), withText("Tomato  2021-01-02 02:02:02"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.items_rv),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        shareTextView.perform(click())

        onView(withId(R.id.share)).inRoot(RootMatchers.isPlatformPopup()).perform(click())

        Thread.sleep(500)
        val shareTest = uiDevice.findObject(
            By.textContains(
                "Messages"
            )
        )
        Assert.assertTrue(shareTest != null)

        uiDevice.pressBack()
    }


    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
