package com.bcasekuritas.mybest.app.feature.dialog.share

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.data.layout.UIDialogShareModel
import com.bcasekuritas.mybest.databinding.ItemListShareBinding

class DialogShareAdapter (
    private val onClickCallback: (itemEntity: UIDialogShareModel) -> Unit
) : RecyclerView.Adapter<DialogShareAdapter.ViewHolder>() {
    private val dataSet = mutableListOf<UIDialogShareModel>()

    fun addItems(items: List<UIDialogShareModel>) {
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemListShareBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UIDialogShareModel, onClickCallback: (itemEntity: UIDialogShareModel) -> Unit) {
            binding.tvTitleItemSheet.text = data.name
            binding.imgItemSheet.background = data.drawable
            binding.root.setOnClickListener {
                onClickCallback.invoke(data)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListShareBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.bind(item) {
            onClickCallback.invoke(item)
        }
    }


}