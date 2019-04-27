package ru.debian17.findme.app.ui.menu.attribute.add.lon

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.debian17.findme.app.mvp.BaseView
import ru.debian17.findme.data.model.category.Category

interface AddLongAttributeView : BaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onCategoriesLoaded(categories: List<Category>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun onError(errorMessage: String?)

}