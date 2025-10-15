package com.susess.cv360.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.susess.cv360.databinding.ItemDeliveriesBinding
import com.susess.cv360.helpers.DateTimeUtils
import com.susess.cv360.model.deliveries.DeliveryResponse
import java.math.RoundingMode

class DeliveryAdapter(private val onClick: (DeliveryResponse) -> Unit = {}): ListAdapter<DeliveryResponse, DeliveryAdapter.ViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDeliveriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<DeliveryResponse>() {
        override fun areItemsTheSame(old: DeliveryResponse, new: DeliveryResponse) = old.publicKey == new.publicKey
        override fun areContentsTheSame(old: DeliveryResponse, new: DeliveryResponse) = old == new
    }
    inner class ViewHolder(private val binding: ItemDeliveriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DeliveryResponse) {
            binding.textTitleDelivery.text = item.tanque.externalKey
            binding.textSubtitleDelivery.text = "${item.volumenEntregado.valorNumerico.setScale(3,
                RoundingMode.DOWN)} ${item.volumenEntregado.unidadDeMedida.descripcion}"
            binding.textDateDelivery.text = DateTimeUtils.formatOffsetDateTimeToDateTime2(item.fechaYHoraFinalEntrega)
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}