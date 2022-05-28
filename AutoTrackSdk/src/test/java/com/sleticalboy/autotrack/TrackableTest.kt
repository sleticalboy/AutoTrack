package com.sleticalboy.autotrack

/**
 * Created on 19-5-8.
 *
 * @author leebin
 */
class TrackableTest {

  private var mBaseTrackInfo: BaseTrackInfo? = null

  // @Before
  fun setUp() {
    mBaseTrackInfo = object : BaseTrackInfo() {
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
    mBaseTrackInfo = null
  }

  // @Test
  fun formatTest() {
    println(mBaseTrackInfo)
  }
}