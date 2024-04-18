package com.example.budgetbuddy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class PaymentReminderDialogFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_payment_reminder_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmButton = view.findViewById<Button>(R.id.confirm_zlecenie_stale)
        val cancelButton = view.findViewById<Button>(R.id.no_confirm_zlecenie_stale)

        confirmButton.setOnClickListener {
            // Tutaj możesz umieścić kod obsługi kliknięcia przycisku potwierdzenia

            // Zamykamy okno dialogowe po kliknięciu przycisku
            dismiss()
        }

        cancelButton.setOnClickListener {

            dismiss()
        }
    }


    companion object {

        fun newInstance() =
            PaymentReminderDialogFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}