package com.bcasekuritas.mybest.widget.dropdown

//object DropDownListOption {
//    object DropDownList {
//        fun getCustomListPopUpWindow(
//            context: Context,
//            items: List<String> = ArrayList(),
//            anchor: View,
//            onItemSelected: (String) -> Unit,
//            onConfirmButtonClick: () -> Unit,
//            @DrawableRes backgroundDrawableRes: Int = 0,
//            horizontalOffsetValue: Int = 0,
//            verticalOffsetValue: Int = 0
//        ): ListPopupWindow {
//            val adapter = CustomArrayAdapter(context, items)
//            val listPopupWindow = ListPopupWindow(context)
//
//            // Inflate custom layout for the popup window
//            val popupView = LayoutInflater.from(context).inflate(R.layout.custom_popup_layout, null)
//            val listView = popupView.findViewById<ListView>(R.id.listView)
//            val confirmButton = popupView.findViewById<Button>(R.id.confirmButton)
//
//            listView.adapter = adapter
//
//            listPopupWindow.apply {
//                width = anchor.width
//                height = ListPopupWindow.WRAP_CONTENT
//                isModal = true
//                anchorView = anchor
//                horizontalOffset = horizontalOffsetValue
//                verticalOffset = verticalOffsetValue
//
//                if (backgroundDrawableRes != 0) {
//                    val drawable = ContextCompat.getDrawable(context, backgroundDrawableRes)
//                    setBackgroundDrawable(drawable)
//                }
//            }
//
//            listView.setOnItemClickListener { _, _, position, _ ->
//                val selectedItem = items[position]
//                onItemSelected(selectedItem)
//                adapter.setSelectedPosition(position)
//            }
//
//            confirmButton.setOnClickListener {
//                onConfirmButtonClick()
//                listPopupWindow.dismiss()
//            }
//
//            listPopupWindow.contentView = popupView
//
//            return listPopupWindow
//        }
//
//        private class CustomArrayAdapter(
//            context: Context,
//            private val items: List<String>
//        ) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, items) {
//
//            private var selectedPosition: Int = -1
//
//            fun setSelectedPosition(position: Int) {
//                selectedPosition = position
//                notifyDataSetChanged()
//            }
//
//            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//                val view = super.getView(position, convertView, parent)
//
//                // Add check image for the selected item
//                val checkImageView = ImageView(context)
//                checkImageView.setImageResource(if (position == selectedPosition) R.drawable.ic_check else 0)
//
//                // Adjust layout parameters for the check image
//                val params = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                checkImageView.layoutParams = params
//
//                // Add check image to the view
//                (view as? LinearLayout)?.addView(checkImageView)
//
//                return view
//            }
//        }
//    }
