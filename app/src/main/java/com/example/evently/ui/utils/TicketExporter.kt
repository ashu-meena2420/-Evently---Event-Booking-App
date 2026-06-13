package com.example.evently.ui.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.evently.model.Booking
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object TicketExporter {
    fun exportTicketToDownloads(context: Context, booking: Booking): Boolean {
        val fileName = "Evently_Ticket_${booking.id}.txt"
        val content = """
            ==================================================
                            EVENTLY TICKET RECEIPT
            ==================================================
            Booking ID:       ${booking.id}
            Event Name:       ${booking.event.name}
            Category:         ${booking.event.category}
            Date & Time:      ${booking.event.date} at ${booking.event.time}
            Venue:            ${booking.event.venue}
            Organizer:        ${booking.event.organizer}
            --------------------------------------------------
            Ticket Type:      ${booking.ticketType}
            Quantity:         ${booking.quantity}
            Total Paid:       ₹${booking.totalPrice}
            --------------------------------------------------
            Customer Name:    ${booking.customerName}
            Customer Phone:   ${booking.customerPhone}
            Customer Email:   ${booking.customerEmail}
            Booking Time:     ${booking.bookingTime}
            --------------------------------------------------
            QR Entry Token:   ${booking.qrCodeToken}
            ==================================================
            Thank you for booking with Evently! Please present
            this text ticket or scan the QR code at the gates.
            ==================================================
        """.trimIndent()

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentResolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri: Uri? = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
                    outputStream?.use {
                        it.write(content.toByteArray())
                    }
                    Toast.makeText(context, "Ticket saved to Downloads!", Toast.LENGTH_LONG).show()
                    true
                } else {
                    false
                }
            } else {
                // Older Android version fallback
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use {
                    it.write(content.toByteArray())
                }
                Toast.makeText(context, "Ticket saved to Downloads!", Toast.LENGTH_LONG).show()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save ticket: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            false
        }
    }
}
