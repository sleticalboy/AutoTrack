package com.sleticalboy.autotrack

/**
 * Created on 19-5-8.
 *
 * @author leebin
 */
class TrackableTest {

    private var trackable: Trackable? = null

    // @Before
    fun setUp() {
        trackable = object : Trackable() {
            override fun path(): CharSequence {
                return "test-path"
            }

            override fun label(): String {
                return "TestTrackable"
            }

            override fun type(): String {
                // return "PageView"
                return "TestEvent"
            }
        }
    }

    // @After
    fun tearDown() {
        trackable = null
    }

    // @Test
    fun formatTest() {
        println(trackable)
    }
}