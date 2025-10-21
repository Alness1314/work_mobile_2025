package com.susess.cv360.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.susess.cv360.databinding.ItemReceptionsBinding
import com.susess.cv360.helpers.DateTimeUtils
import com.susess.cv360.model.receptions.ReceptionResponse
import java.math.RoundingMode

class ReceptionAdapter(private val onClick: (ReceptionResponse) -> Unit = {}): ListAdapter<ReceptionResponse, ReceptionAdapter.ViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemReceptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ReceptionResponse>() {
        override fun areItemsTheSame(old: ReceptionResponse, new: ReceptionResponse) = old.publicKey == new.publicKey
        override fun areContentsTheSame(old: ReceptionResponse, new: ReceptionResponse) = old == new
    }

    inner class ViewHolder(private val binding: ItemReceptionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ReceptionResponse) {
            binding.textTitleReception.text = item.tanque?.externalKey
            binding.textSubtitleReception.text = "${item.volumenRecepcion?.valorNumerico?.setScale(3,
                RoundingMode.DOWN)} ${item.volumenRecepcion?.unidadDeMedida?.descripcion}"
            binding.textDateReception.text = DateTimeUtils.formatOffsetDateTimeToDateTime2(item.fechaYHoraFinalRecepcion!!)
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}