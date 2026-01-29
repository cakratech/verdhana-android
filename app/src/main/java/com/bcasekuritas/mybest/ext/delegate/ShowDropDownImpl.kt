package com.bcasekuritas.mybest.ext.delegate

import android.content.Context
import android.view.View
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.domain.dto.response.StockCodeItem
import com.bcasekuritas.mybest.widget.dropdown.DropDownList

interface ShowDropDown {
    fun showDropDownAccount(context: Context,
                            items: List<String> = ArrayList(),
                            anchor: View,
                            itemClickListener: (index: Int, value: String) -> Unit
    )

    fun showDropDownEarningPerShare(context: Context,
                            items: List<String> = ArrayList(),
                            anchor: View,
                            itemClickListener: (index: Int, value: String) -> Unit
    )
    fun showDropDownSearchable(context: Context,
                            items: List<StockParamObject> = ArrayList(),
                            anchor: View,
                            itemClickListener: (index: Int, value: String) -> Unit
    )
    fun showDropDownStringSearchable(context: Context,
                            items: List<String> = ArrayList(),
                            anchor: View,
                            hint: String,
                            itemClickListener: (index: Int, value: String) -> Unit
    )
    fun showSimpleDropDownWidth80(context: Context,
                                  items: List<String> = ArrayList(),
                                  anchor: View,
                                  itemClickListener: (index: Int, value: String) -> Unit
    )
    fun showSimpleDropDown(context: Context,
                                  items: List<String> = ArrayList(),
                                  anchor: View,
                                  itemClickListener: (index: Int, value: String) -> Unit
    )

    fun showDropDownSearchableStockCode(context: Context,
                               items: List<StockCodeItem> = ArrayList(),
                               anchor: View,
                               itemClickListener: (stockCode: String, stockName: String) -> Unit
    )

    fun showDropDownPriceAlert(context: Context,
                               anchor: View,
                               itemClickListener: (index: Int) -> Unit)

    fun showSimpleDropDownOrderType(context: Context,
                           items: List<String> = ArrayList(),
                           anchor: View,
                           isMarketClosed: Boolean,
                           itemClickListener: (index: Int, value: String) -> Unit
    )
}
class ShowDropDownImpl: ShowDropDown {
    override fun showDropDownAccount(
        context: Context,
        items: List<String>,
        anchor: View,
        itemClickListener: (index: Int, value: String) -> Unit
    ) {
        val listDropdown = DropDownList.getPlainListPopUpWindow(
            context = context,
            items = items,
            anchor = anchor,
            backgroundDrawableRes = R.drawable.bg_ffffff_8_stroke_dae8f6,
            cellLayoutRes = R.layout.item_list_dropdown
        )

        listDropdown.setOnItemClickListener { _, _, index, _ ->
            val selectedValue = items[index]
            itemClickListener(index, selectedValue)
            listDropdown.dismiss()
        }

        listDropdown.show()
    }

    override fun showDropDownEarningPerShare(
        context: Context,
        items: List<String>,
        anchor: View,
        itemClickListener: (index: Int, value: String) -> Unit
    ) {
        val listDropdown = DropDownList.getPlainListPopUpWindowWithLine(
            context = context,
            items = items,
            anchor = anchor,
            textView = R.id.tv_dropdown_view,
            backgroundDrawableRes = R.drawable.rounded_dae8f6_8,
            cellLayoutRes = R.layout.item_list_dropdown_view
        )

        listDropdown.setOnItemClickListener { _, _, index, _ ->
            val selectedValue = items[index]
            itemClickListener(index, selectedValue)
            listDropdown.dismiss()
        }

        listDropdown.show()
    }

    override fun showDropDownSearchable(
        context: Context,
        items: List<StockParamObject>,
        anchor: View,
        itemClickListener: (index: Int, value: String) -> Unit
    ) {
        DropDownList.getSearchableListPopUpWindow(
            context = context,
            items = items,
            anchor = anchor,
            backgroundDrawableRes = R.drawable.bg_ffffff_8_stroke_dae8f6,
            itemClickListener = itemClickListener
        )
    }

    override fun showDropDownStringSearchable(
        context: Context,
        items: List<String>,
        anchor: View,
        hint: String,
        itemClickListener: (index: Int, value: String) -> Unit
    ) {
        DropDownList.getSearchableListStringPopUpWindow(
            context = context,
            items = items,
            anchor = anchor,
            backgroundDrawableRes = R.drawable.bg_ffffff_8_stroke_dae8f6,
            hint = hint,
            itemClickListener = itemClickListener
        )
    }

    override fun showSimpleDropDownOrderType(
        context: Context,
        items: List<String>,
        anchor: View,
        isMarketClosed: Boolean,
        itemClickListener: (index: Int, value: String) -> Unit,
    ) {
        val listDropdown = DropDownList.getPlainListPopUpWindowOrderType(
            context = context,
            items = items,
            anchor = anchor,
            backgroundDrawableRes = R.drawable.bg_ffffff_8_stroke_dae8f6,
            cellLayoutRes = R.layout.item_list_dropdown,
            isMarketClosed = isMarketClosed
        )

        listDropdown.width = (anchor.width.plus(24))

        listDropdown.setOnItemClickListener { _, _, index, _ ->
            val selectedValue = items[index]
            val disableItems = isMarketClosed && index == 1
            if (!disableItems) {
                itemClickListener(index, selectedValue)
                listDropdown.dismiss()
            }
        }

        listDropdown.show()
    }

    override fun showSimpleDropDownWidth80(
        context: Context,
        items: List<String>,
        anchor: View,
        itemClickListener: (index: Int, value: String) -> Unit
    ) {
        val listDropdown = DropDownList.getPlainListPopUpWindow(
            context = context,
            items = items,
            anchor = anchor,
            backgroundDrawableRes = R.drawable.bg_ffffff_8_stroke_dae8f6,
            cellLayoutRes = R.layout.item_list_dropdown
        )

        listDropdown.width = (anchor.width.plus(80))

        listDropdown.setOnItemClickListener { _, _, index, _ ->
            val selectedValue = items[index]
            itemClickListener(index, selectedValue)
            listDropdown.dismiss()
        }

        listDropdown.show()
    }

    override fun showSimpleDropDown(
        context: Context,
        items: List<String>,
        anchor: View,
        itemClickListener: (index: Int, value: String) -> Unit,
    ) {
        val listDropdown = DropDownList.getPlainListPopUpWindow(
            context = context,
            items = items,
            anchor = anchor,
            backgroundDrawableRes = R.drawable.bg_ffffff_8_stroke_dae8f6,
            cellLayoutRes = R.layout.item_list_dropdown
        )

        listDropdown.width = (anchor.width)

        listDropdown.setOnItemClickListener { _, _, index, _ ->
            val selectedValue = items[index]
            itemClickListener(index, selectedValue)
            listDropdown.dismiss()
        }

        listDropdown.show()
    }

    override fun showDropDownPriceAlert(
        context: Context,
        anchor: View,
        itemClickListener: (index: Int) -> Unit,
    ) {
        val listDropdown = DropDownList.getPlainListPopUpWindowPriceAlert(
            context = context,
            anchor = anchor,
            backgroundDrawableRes = R.drawable.bg_ffffff_8_stroke_dae8f6,
            cellLayoutRes = R.layout.item_list_dropdown
        )

        listDropdown.width = (anchor.width.plus(24))

        listDropdown.setOnItemClickListener { _, _, index, _ ->
            itemClickListener(index)
            listDropdown.dismiss()
        }

        listDropdown.show()
    }

    override fun showDropDownSearchableStockCode(
        context: Context,
        items: List<StockCodeItem>,
        anchor: View,
        itemClickListener: (stockCode: String, stockName: String) -> Unit
    ) {
        DropDownList.getSearchableListStockPopUpWindow(
            context = context,
            items = items,
            anchor = anchor,
            backgroundDrawableRes = R.drawable.bg_ffffff_8_stroke_dae8f6,
            itemClickListener = itemClickListener
        )
    }
}