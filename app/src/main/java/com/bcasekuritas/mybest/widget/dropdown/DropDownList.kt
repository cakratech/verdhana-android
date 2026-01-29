package com.bcasekuritas.mybest.widget.dropdown

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.domain.dto.response.StockCodeItem
import com.bcasekuritas.mybest.databinding.CustomDropdownSearchableBinding

object DropDownList {
    fun getPlainListPopUpWindow(
        context: Context,
        items: List<String> = ArrayList(),
        anchor: View,
        @DrawableRes backgroundDrawableRes: Int = 0,
        @LayoutRes cellLayoutRes: Int = android.R.layout.simple_list_item_1,
        horizontalOffsetValue: Int = 0,
        verticalOffsetValue: Int = 0
    ): ListPopupWindow {
        val adapter: ArrayAdapter<String> = ArrayAdapter(context, cellLayoutRes, items)
        val listPopupWindow = ListPopupWindow(context)

        listPopupWindow.apply {
            setAdapter(adapter)

            width = anchor.width
            height = ListPopupWindow.WRAP_CONTENT
            isModal = true
            anchorView = anchor
            horizontalOffset = horizontalOffsetValue
            verticalOffset = verticalOffsetValue

            if (backgroundDrawableRes != 0) {
                val drawable = ContextCompat.getDrawable(context, backgroundDrawableRes)
                setBackgroundDrawable(drawable)
            }
        }

        return listPopupWindow
    }

    fun getPlainListPopUpWindowWithLine(
        context: Context,
        items: List<String> = ArrayList(),
        anchor: View,
        textView: Int,
        @DrawableRes backgroundDrawableRes: Int = 0,
        @LayoutRes cellLayoutRes: Int = android.R.layout.simple_list_item_1,
        horizontalOffsetValue: Int = 0,
        verticalOffsetValue: Int = 0
    ): ListPopupWindow {
        val adapter: ArrayAdapter<String> = ArrayAdapter(context, cellLayoutRes, textView, items)
        val listPopupWindow = ListPopupWindow(context)

        listPopupWindow.apply {
            setAdapter(adapter)

            width = anchor.width
            height = ListPopupWindow.WRAP_CONTENT
            isModal = true
            anchorView = anchor
            horizontalOffset = horizontalOffsetValue
            verticalOffset = verticalOffsetValue

            if (backgroundDrawableRes != 0) {
                val drawable = ContextCompat.getDrawable(context, backgroundDrawableRes)
                setBackgroundDrawable(drawable)
            }
        }

        return listPopupWindow
    }


//    fun getDropDownSearchbar(
//        context: Context,
//        items: List<String> = ArrayList(),
//        anchor: View,
//        @DrawableRes backgroundDrawableRes: Int = 0,
//        @LayoutRes cellLayoutRes: Int = android.R.layout.simple_list_item_1,
//        horizontalOffsetValue: Int = 0,
//        verticalOffsetValue: Int = 0
//    ): PopupWindow {
//        val binding: CustomDropdownSearchableBinding = DataBindingUtil.inflate(
//            LayoutInflater.from(context),
//            R.layout.custom_dropdown_searchable,
//            null,
//            false
//        )
//
//        binding.listView.adapter = ArrayAdapter(context, cellLayoutRes, items)
//
////        binding.searchbar.addTextChangedListener(object : TextWatcher {
////            override fun afterTextChanged(s: Editable?) {
////                // Implement search functionality here
////            }
////
////            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
////
////            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
////        })
//
//
//        val listPopupWindow = ListPopupWindow(context)
//        listPopupWindow.apply {
//            width = anchor.width
//            height = ListPopupWindow.WRAP_CONTENT
//            isModal = true
//            anchorView = anchor
//            horizontalOffset = horizontalOffsetValue
//            verticalOffset = verticalOffsetValue
//
//            if (backgroundDrawableRes != 0) {
//                val drawable = ContextCompat.getDrawable(context, backgroundDrawableRes)
//                setBackgroundDrawable(drawable)
//            }
//
//            anchorView = anchor
//
//        }
//
//        return listPopupWindow
//    }

    fun getPlainListPopUpWindowOrderType(
        context: Context,
        items: List<String> = ArrayList(),
        anchor: View,
        @DrawableRes backgroundDrawableRes: Int = 0,
        @LayoutRes cellLayoutRes: Int = android.R.layout.simple_list_item_1,
        horizontalOffsetValue: Int = 0,
        verticalOffsetValue: Int = 0,
        isMarketClosed: Boolean = false
    ): ListPopupWindow {
        val adapter = object : ArrayAdapter<String>(
            context, cellLayoutRes, items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getView(position, convertView, parent) as TextView
                val item = getItem(position)
                textView.text = item
                if (position == 1) {
                    if (isMarketClosed) {
                        val color = ContextCompat.getColor(context, R.color.bgGrey)
                        textView.setTextColor(color)
                    }
                } else {
                    val color = ContextCompat.getColor(context, R.color.black)
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                    textView.setTextColor(color)
                }
                return textView
            }
        }
        val listPopupWindow = ListPopupWindow(context)

        listPopupWindow.apply {
            setAdapter(adapter)
            width = anchor.width
            height = ListPopupWindow.WRAP_CONTENT
            isModal = true
            anchorView = anchor
            horizontalOffset = horizontalOffsetValue
            verticalOffset = verticalOffsetValue

            if (backgroundDrawableRes != 0) {
                val drawable = ContextCompat.getDrawable(context, backgroundDrawableRes)
                setBackgroundDrawable(drawable)
            }
        }

        return listPopupWindow
    }

    fun getPlainListPopUpWindowPriceAlert(
        context: Context,
        anchor: View,
        @DrawableRes backgroundDrawableRes: Int = 0,
        @LayoutRes cellLayoutRes: Int = android.R.layout.simple_list_item_1,
        horizontalOffsetValue: Int = 0,
        verticalOffsetValue: Int = 0,
    ): ListPopupWindow {
        val items = listOf("Edit", "Delete")
        val arrayAdapter = object : ArrayAdapter<String>(
            context, cellLayoutRes, items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getView(position, convertView, parent) as TextView
                val item = getItem(position)
                textView.text = item
                val color = if (position > 0 ) ContextCompat.getColor(context, R.color.textDown) else ContextCompat.getColor(context, R.color.black)
                textView.setTextColor(color)
                return textView
            }
        }
        val listPopupWindow = ListPopupWindow(context)


        listPopupWindow.apply {
            setAdapter(arrayAdapter)

            width = anchor.width
            height = ListPopupWindow.WRAP_CONTENT
            isModal = true
            anchorView = anchor
            horizontalOffset = horizontalOffsetValue
            verticalOffset = verticalOffsetValue

            if (backgroundDrawableRes != 0) {
                val drawable = ContextCompat.getDrawable(context, backgroundDrawableRes)
                setBackgroundDrawable(drawable)
            }
        }

        return listPopupWindow
    }

    fun getSearchableListPopUpWindow(
        context: Context,
        items: List<StockParamObject> = ArrayList(),
        anchor: View,
        @DrawableRes backgroundDrawableRes: Int = 0,
        @LayoutRes cellLayoutRes: Int = R.layout.item_dropdown,
        horizontalOffsetValue: Int = 0,
        verticalOffsetValue: Int = 0,
        itemClickListener: (index: Int, value: String) -> Unit
    ): PopupWindow {
        val binding: CustomDropdownSearchableBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.custom_dropdown_searchable,
            null,
            false
        )

        val arrayAdapter = object : ArrayAdapter<StockParamObject>(
            context, cellLayoutRes, items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(cellLayoutRes, parent, false)
                val stock = getItem(position)
                val text = view.findViewById<TextView>(R.id.tv_dropdown)
                text.text = stock?.stockCode
                return view
            }
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim() // Trim to remove leading and trailing spaces

                // Split the query into words
                val searchWords = query.split("\\s+".toRegex())

                // Filter the items based on the search words
                val filteredList = items.filter { stock ->
                    searchWords.all { searchWord ->
                        stock.stockName.contains(searchWord, ignoreCase = true) ||
                                stock.stockCode.contains(searchWord, ignoreCase = true)
                    }
                }

                // Create a new ArrayList from the filteredList
                val filteredArrayList = ArrayList(filteredList)

                // Create a new ArrayAdapter with the filtered list
                val newAdapter = object : ArrayAdapter<StockParamObject>(
                    context, cellLayoutRes, filteredArrayList
                ) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = convertView ?: LayoutInflater.from(context)
                            .inflate(cellLayoutRes, parent, false)
                        val stock = getItem(position)
                        val text = view.findViewById<TextView>(R.id.tv_dropdown)

                        // Add null checks
                        if (text != null && stock != null) {
                            text.text = stock.stockCode
                        }

                        return view
                    }
                }

                // Set the new adapter to the listView
                binding.listView.adapter = newAdapter
            }

            }


        binding.searchbar.setTextWatcher(searchTextWatcher)

        binding.listView.adapter = arrayAdapter

        binding.listView.layoutParams.height = 330

        val popupWindow = PopupWindow(
            binding.root,
            anchor.width,
            ListPopupWindow.WRAP_CONTENT,
            true
        )

        popupWindow.apply {
            isOutsideTouchable = true
            elevation = 10.0f

            if (backgroundDrawableRes != 0) {
                val drawable = ContextCompat.getDrawable(context, backgroundDrawableRes)
                setBackgroundDrawable(drawable)
            }
        }

        binding.listView.setOnItemClickListener { adapterView, view, index, siza ->
            val selectedValue = arrayAdapter.getItem(index)
            itemClickListener(index, selectedValue?.stockCode!!)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchor, horizontalOffsetValue, verticalOffsetValue)

        return popupWindow
    }

    fun getSearchableListStringPopUpWindow(
        context: Context,
        items: List<String> = ArrayList(),
        anchor: View,
        @DrawableRes backgroundDrawableRes: Int = 0,
        @LayoutRes cellLayoutRes: Int = R.layout.item_dropdown,
        horizontalOffsetValue: Int = 0,
        verticalOffsetValue: Int = 0,
        hint: String,
        itemClickListener: (index: Int, value: String) -> Unit
    ): PopupWindow {
        val binding: CustomDropdownSearchableBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.custom_dropdown_searchable,
            null,
            false
        )

        val arrayAdapter = ArrayAdapter(context, cellLayoutRes, items)

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                arrayAdapter.filter.filter(s)

            }
        }

        binding.searchbar.setTextWatcher(searchTextWatcher)
        binding.searchbar.setHint(hint)
        binding.listView.adapter = arrayAdapter

        binding.listView.layoutParams.height = 330

        val popupWindow = PopupWindow(
            binding.root,
            anchor.width,
            ListPopupWindow.WRAP_CONTENT,
            true
        )

        popupWindow.apply {
            isOutsideTouchable = true
            elevation = 10.0f

            if (backgroundDrawableRes != 0) {
                val drawable = ContextCompat.getDrawable(context, backgroundDrawableRes)
                setBackgroundDrawable(drawable)
            }
        }

        binding.listView.setOnItemClickListener { adapterView, view, index, siza ->
            val selectedValue = arrayAdapter.getItem(index)
            itemClickListener(index, selectedValue!!)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchor, horizontalOffsetValue, verticalOffsetValue)

        return popupWindow
    }

    fun getSearchableListStockPopUpWindow(
        context: Context,
        items: List<StockCodeItem> = ArrayList(),
        anchor: View,
        @DrawableRes backgroundDrawableRes: Int = 0,
        @LayoutRes cellLayoutRes: Int = R.layout.item_dropdown,
        horizontalOffsetValue: Int = 0,
        verticalOffsetValue: Int = 0,
        itemClickListener: (stockCode: String, stockName: String) -> Unit
    ): PopupWindow {
        val binding: CustomDropdownSearchableBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.custom_dropdown_searchable,
            null,
            false
        )

        val map = mutableMapOf<String,StockCodeItem>()
        items.map {
            map[it.stockCode] = StockCodeItem(it.stockCode, it.stockName)
        }
        val arrayAdapter = ArrayAdapter(context, cellLayoutRes, map.keys.toList().sorted())

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                arrayAdapter.filter.filter(s)

            }
        }

        binding.searchbar.setTextWatcher(searchTextWatcher)

        binding.listView.adapter = arrayAdapter

        binding.listView.layoutParams.height = 330

        val popupWindow = PopupWindow(
            binding.root,
            anchor.width,
            ListPopupWindow.WRAP_CONTENT,
            true
        )

        popupWindow.apply {
            isOutsideTouchable = true
            elevation = 10.0f

            if (backgroundDrawableRes != 0) {
                val drawable = ContextCompat.getDrawable(context, backgroundDrawableRes)
                setBackgroundDrawable(drawable)
            }
        }

        binding.listView.setOnItemClickListener { adapterView, view, index, siza ->
            val selectedValue = arrayAdapter.getItem(index)
            val stockName = map[selectedValue]?.stockName
            itemClickListener(selectedValue.toString(), stockName.toString())
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchor, horizontalOffsetValue, verticalOffsetValue)

        return popupWindow
    }

}