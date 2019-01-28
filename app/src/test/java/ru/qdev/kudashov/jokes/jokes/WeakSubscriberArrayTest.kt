package ru.qdev.kudashov.jokes.jokes

import org.junit.Test

import org.junit.Assert.*
import ru.qdev.kudashov.jokes.utils.WeakSubscriberArray
import java.lang.ref.WeakReference

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class WeakSubscriberArrayTest {
    @Test
    fun isCorrect() {
        class Subscriber

        val weakSubscriberArray = WeakSubscriberArray<Subscriber>()

        val subscriber0 = Subscriber()
        var subscriberForWeakTest : Subscriber? = null
        val subscriber1 = Subscriber()
        val subscriber2 = Subscriber()

        //Test addSubscriber
        subscriberForWeakTest = Subscriber()
        weakSubscriberArray.addSubscriber(subscriber0)
        weakSubscriberArray.addSubscriber(subscriberForWeakTest)
        weakSubscriberArray.addSubscriber(subscriber1)
        assertEquals(weakSubscriberArray.size, 3)
        subscriberForWeakTest = null
        System.gc()
        weakSubscriberArray.addSubscriber(subscriber2)
        assertEquals(weakSubscriberArray.size, 3)
        assertEquals(weakSubscriberArray[0].get(), subscriber0)
        assertEquals(weakSubscriberArray[1].get(), subscriber1)
        assertEquals(weakSubscriberArray[2].get(), subscriber2)

        //Test removeSubscriber
        subscriberForWeakTest = Subscriber()
        weakSubscriberArray.add(0, WeakReference(subscriberForWeakTest))
        assertEquals(weakSubscriberArray.size, 4)
        subscriberForWeakTest = null
        System.gc()
        weakSubscriberArray.removeSubscriber(subscriber0)
        assertEquals(weakSubscriberArray.size, 2)
        assertEquals(weakSubscriberArray[0].get(), subscriber1)
        assertEquals(weakSubscriberArray[1].get(), subscriber2)
        weakSubscriberArray.removeSubscriber(subscriber2)
        assertEquals(weakSubscriberArray.size, 1)
        assertEquals(weakSubscriberArray[0].get(), subscriber1)

        //Test clearEmptySubscribers
        weakSubscriberArray.clear()
        weakSubscriberArray.addSubscriber(subscriber0)
        subscriberForWeakTest = Subscriber()
        weakSubscriberArray.addSubscriber(subscriber1)
        weakSubscriberArray.addSubscriber(subscriberForWeakTest)
        weakSubscriberArray.addSubscriber(subscriber2)
        assertEquals(weakSubscriberArray.size, 4)
        subscriberForWeakTest = null
        System.gc()
        weakSubscriberArray.clearEmptySubscribers()
        assertEquals(weakSubscriberArray.size, 3)
        assertEquals(weakSubscriberArray[0].get(), subscriber0)
        assertEquals(weakSubscriberArray[1].get(), subscriber1)
        assertEquals(weakSubscriberArray[2].get(), subscriber2)

        //Test forEachSubscribers
        subscriberForWeakTest = Subscriber()
        weakSubscriberArray.add(1, WeakReference(subscriberForWeakTest))
        subscriberForWeakTest = null
        System.gc()
        val subscribersArray = ArrayList<Subscriber>()
        weakSubscriberArray.forEachSubscribers {
            subscribersArray.add(it)
        }
        assertEquals(subscribersArray.size, 3)
        assertEquals(subscribersArray[0], subscriber0)
        assertEquals(subscribersArray[1], subscriber1)
        assertEquals(subscribersArray[2], subscriber2)
    }
}
