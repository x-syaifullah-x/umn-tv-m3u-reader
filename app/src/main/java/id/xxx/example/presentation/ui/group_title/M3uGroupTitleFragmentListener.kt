package id.xxx.example.presentation.ui.group_title

interface M3uGroupTitleFragmentListener {

    sealed interface Action {

        data class OnItemFocus(
            val groupTitle: String,
        ) : Action
    }

    fun onAction(action: Action)
}