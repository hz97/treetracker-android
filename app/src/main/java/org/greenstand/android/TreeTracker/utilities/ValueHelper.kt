package org.greenstand.android.TreeTracker.utilities


object ValueHelper {

    const val NAME_SPACE = "org.greenstand.android"

    const val NEW_TREE_FRAGMENT = "NEW_TREE_FRAGMENT"

    private const val intentsOffset = 1000
    const val INTENT_CAMERA = intentsOffset + 1

    const val JPEG_FILE_PREFIX = "IMG_"
    const val JPEG_FILE_SUFFIX = ".jpg"

    var SPLASH_SCREEN_DURATION: Long = 1000

    const val TIME_TO_NEXT_UPDATE_GLOBAL_SETTING = "TIME_TO_NEXT_UPDATE_GLOBAL_SETTING"
    const val TIME_TO_NEXT_UPDATE_ADMIN_DB_SETTING = "TIME_TO_NEXT_UPDATE_ADMIN_DB_SETTING"
    const val TIME_TO_NEXT_UPDATE_ADMIN_DB_SETTING_PRESENT = "TIME_TO_NEXT_UPDATE_ADMIN_DB_SETTING_PRESENT"
    const val MIN_ACCURACY_GLOBAL_SETTING = "MIN_ACCURACY_GLOBAL_SETTING"
    const val TREE_TRACKER_SETTINGS_USED = "TREE_TRACKER_SETTINGS_USED"
    const val FIRST_RUN = "FIRST_RUN"
    const val TAKEN_IMAGE_PATH = "TAKEN_IMAGE_PATH"

    const val TIME_TO_NEXT_UPDATE_DEFAULT_SETTING = 30
    const val MIN_ACCURACY_DEFAULT_SETTING = 10

    const val TIME_OF_LAST_PLANTER_CHECK_IN_SECONDS = "TIME_OF_LAST_PLANTER_CHECK_IN_SECONDS"
    const val PLANTER_PHOTO = "PLANTER_PHOTO"
    const val PLANTER_INFO_ID = "PLANTER_INFO_ID"
    const val PLANTER_CHECK_IN_ID = "PLANTER_CHECK_IN_ID"

    const val CHECK_IN_TIMEOUT = 60 * 60 * 2

    const val TAKE_SELFIE_EXTRA = "TAKE_SELFIE_EXTRA"
    const val FOCUS_METRIC_VALUE = "FOCUS_METRIC_VALUE"
}
