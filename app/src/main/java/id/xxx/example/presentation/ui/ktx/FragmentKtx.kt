@file:JvmName("FragmentKtx")

package id.xxx.example.presentation.ui.ktx

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.getListener(): T? {
    val result =
        if (parentFragment is T) {
            parentFragment
        } else if (activity is T) {
            activity
        } else {
            null
        }
    return result as? T
}