package com.travels.searchtravels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.lifecycle.Lifecycle
import com.google.api.services.vision.v1.model.LatLng
import com.travels.searchtravels.VisionApiTest.TestListener.EventType
import com.travels.searchtravels.activity.MainActivity
import com.travels.searchtravels.api.OnVisionApiListener
import com.travels.searchtravels.api.VisionApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class VisionApiTest {
    companion object {
        private const val LOOP_MESSAGE_POST_TIMEOUT_MILLIS = 7500L // 7.5 seconds

        private const val VALID_GOOGLE_TOKEN = "ya29.c.KpYB3AeR8wvXgnykweoazg5xCVCXqJYsgDICQQ6vWaXfB83B2dt_rcOhWEarvT5x4COoR4NOfZVK6dvvGUdPurGxObjVVK_7KvEBDp32TvwvihJRJpgzmVwVEW_qBq6FF6BGYIyKg4ozTWj0nADebhFi3P8HE0G1K0dEL2COG3jXrPY_40UC5MrnPCJDh9yvu-_YSQeQ3o0r"

        private const val HERMITAGE_IMAGE_URL = "https://psv4.userapi.com/c856536/u45938510/docs/d10/9b25206456ad/hermitage.jpg?extra=I4LLawl1ciaPb4IoIY1ynLYXvVsrXKqNZgcOqqZzISigpj5cx5O7gJIqUBmoKN6iJ9XBayz_qsOfE2w4NEjqHrFeGSoWnc60nv6z1-30subrjaDyHVP_7-DQvaiqxhzgu8D1Q-NSoD_sDUpY9XYygpg"

        private const val BEACH_IMAGE_URL = "https://psv4.userapi.com/c856336/u157075587/docs/d9/51fdc9104d4d/beach.jpg?extra=DJirh-PaGTi-XC5NE_RpmxxWStOpBDWep0NqlU46IZhsvqmBzAkVp2i6rbkgvfT-22GEBvvePjugTpeNIvcjW30EsI3937ob3B5TRlg4WfbGflU-9huvqI0Lg8s97-IJElGmicNfj9nV19uTkQ5ZPpXG"

        private const val SEA_IMAGE_URL = "https://psv4.userapi.com/c856336/u157075587/docs/d9/925c2fcb1b01/sea.jpg?extra=NzfjbYcuZgkz5ImAhrMncJ7-TiOb8d6kri5kWSE0OeWikiCCSmrULMlu8HqrLRkyA2aaiS5pc5ue33ZIxIJxjFel-2PVJ89uw17gCI3M3WuBcaS4VqJNuvNrtXBNjFELmDDJUG_iLgKN2f-RogPY6E-K"

        private const val OCEAN_IMAGE_URL = "https://psv4.userapi.com/c856336/u157075587/docs/d13/f7d9c2d63649/ocean.jpg?extra=Aiukpasp_8x9pzg9t_xF5d5skA8Pgf9gdFTk939pojoobpvcL5uKxq49qHll0hanDUHalw7IGtjYBIAdjiEzYTthr6-26iZXAAb6Ji1pUBByzFVlexNTzyFQ02XDV4YQtyIYpuj0_GWWkuJkVWlirgMO"

        private const val MOUNTAINS_IMAGE_URL = "https://psv4.userapi.com/c856336/u157075587/docs/d14/267c47ec54a7/mountains.jpg?extra=38mI-U_zgWYwf4aUByjc0zsGH-7sTQnPjMaFXZZ3MwM9czS6Fvy35TczlJv4W1uYs4ic6S6XMgvNxae38sxUVsvEMoRTrasbaiGupSkxofM8pKn7aXz4U0ZfQU687qeRYyru0Pu7hSYXhp1h9zrN4bLl"

        private const val SNOW_IMAGE_URL = "https://psv4.userapi.com/c856336/u157075587/docs/d7/746efdfec436/snow.jpg?extra=GemmlDDOmeRg17AZMHcCqjgn4Tb-oQRTLyDv24GTM-cMAIcKb9h1Aj8I7toXixMkhqMpo8A3WOxks-1fTVXK9UaZ0d8NzO74-rPCtUzNbf6kmhFho0jS4tizmLee8X5U8J0wgnHyFF4WhehwChbVAz6j"

        private const val OTHER_IMAGE_URL = "https://psv4.userapi.com/c856336/u157075587/docs/d3/3cd0a1dcdcf5/other.jpg?extra=7QT2HZmIJ9qxs_4kqc3wbRmxvOOdNBpG9lF3Cx5_441wdyocbSxYa9CKC-uNGI05OTH_ADMxHp3j_7IClhJHWfIQaYVDiM7XaY2-RapJrYJ2AkuIXHg2VZTttdBqiyEoVDGwZb629V5zGTBzV-9kC7s4"
    }

    private lateinit var scenario: ActivityScenario<*>

    @Before
    fun startUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
    }

    @Test
    fun positiveTestSuccessHermitage() {
        val expectedLandmarkCoordinates = LatLng().apply {
            latitude = 59.939832
            longitude = 30.314560
        }
        executeSuccessTest(HERMITAGE_IMAGE_URL, expectedLandmarkCoordinates)
    }

    @Test
    fun positiveTestErrorSeaPlace() {
        executeErrorPlaceTest(SEA_IMAGE_URL, "sea")
    }

    @Test
    fun positiveTestErrorOceanPlace() {
        executeErrorPlaceTest(OCEAN_IMAGE_URL, "ocean")
    }

    @Test
    fun positiveTestErrorBeachPlace() {
        executeErrorPlaceTest(BEACH_IMAGE_URL, "beach")
    }

    @Test
    fun positiveTestErrorMountainsPlace() {
        executeErrorPlaceTest(MOUNTAINS_IMAGE_URL, "mountain")
    }

    @Test
    fun positiveTestErrorSnowPlace() {
        executeErrorPlaceTest(SNOW_IMAGE_URL, "snow")
    }

    @Test
    fun positiveTestErrorOtherPlace() {
        executeErrorTest(OTHER_IMAGE_URL, VALID_GOOGLE_TOKEN)
    }

    @Test
    fun negativeTestNullTokenResponse() {
        executeErrorTest(BEACH_IMAGE_URL, null)
    }

    @Test
    fun negativeTestIncorrectTokenResponse() {
        executeErrorTest(BEACH_IMAGE_URL, "some incorrect token")
    }

    @Test
    fun negativeTestNullImageResponse() {
        executeErrorTest(null, VALID_GOOGLE_TOKEN, expectedEventsCount = 0)
    }

    private fun executeSuccessTest(imageUrl: String, expectedLandmarkCoordinates: LatLng) {
        val image = downloadImage(imageUrl)
        val token = VALID_GOOGLE_TOKEN
        val listener = TestListener()

        performServiceCall(listener) { VisionApi.findLocation(image, token, listener) }

        assertEquals("Listener must be called once", 1, listener.listenerEvents.size)

        val (eventType, result) = listener.listenerEvents[0]
        assertEquals("Event 'SUCCESS' must be triggered", EventType.SUCCESS, eventType)
        assertEquals("Landmark coordinates must equal", expectedLandmarkCoordinates, result)
    }

    private fun executeErrorPlaceTest(imageUrl: String, expectedCategory: String?) {
        val image = downloadImage(imageUrl)
        val token = VALID_GOOGLE_TOKEN
        val listener = TestListener()

        performServiceCall(listener) { VisionApi.findLocation(image, token, listener) }

        assertEquals("Listener must be called once", 1, listener.listenerEvents.size)

        val (eventType, result) = listener.listenerEvents[0]
        assertEquals("Event 'ERROR_PLACE' must be triggered", EventType.ERROR_PLACE, eventType)
        assertEquals("Category '${expectedCategory}' must be resolved", expectedCategory, result)
    }

    private fun executeErrorTest(imageUrl: String?, token: String?, expectedEventsCount: Int = 1) {
        val image = imageUrl?.let { downloadImage(imageUrl) }
        val listener = TestListener()

        performServiceCall(listener) { VisionApi.findLocation(image, token, listener) }

        assertEquals("Listener must be called", expectedEventsCount, listener.listenerEvents.size)

        if(expectedEventsCount > 0) {
            listener.listenerEvents.forEach { event ->
                val (eventType, result) = event
                assertEquals("Error event must be triggered", EventType.ERROR, eventType)
                assertNull(result)
            }
        }
    }

    private fun performServiceCall(
        listener: TestListener,
        serviceCall: () -> Unit
    ) {
        scenario.onActivity { serviceCall() }

        listener.eventLatch.await(LOOP_MESSAGE_POST_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals("All events must be received", 0, listener.eventLatch.count)
    }

    private fun downloadImage(urlString: String): Bitmap {
        // https://sites.google.com/site/lokeshurl/download-image-from-url-in-android
        val url = URL(urlString)

        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val input = connection.inputStream
        return BitmapFactory.decodeStream(input)
    }

    private class TestListener(expectedEvents: Int = 1) : OnVisionApiListener {
        val eventLatch = CountDownLatch(expectedEvents)
        val listenerEvents = ArrayList<ListenerEvent<*>>()

        override fun onSuccess(latLng: LatLng?) {
            registerEvent(ListenerEvent(EventType.SUCCESS, latLng))
        }

        override fun onErrorPlace(category: String?) {
            registerEvent(ListenerEvent(EventType.ERROR_PLACE, category))
        }

        override fun onError() {
            registerEvent(ListenerEvent(EventType.ERROR))
        }

        private fun registerEvent(event: ListenerEvent<Any>) {
            listenerEvents.add(event)
            eventLatch.countDown()
        }

        data class ListenerEvent<T>(
            var eventType: EventType,
            var result: T? = null,
            var throwable: Throwable? = null
        )

        enum class EventType { SUCCESS, ERROR_PLACE, ERROR }
    }
}
