package com.example.fitlife

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegateScreen(
    navController: NavController,
    prefilledMessage: String
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Message Preview:", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = prefilledMessage,
                onValueChange = {},   // read-only preview
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                minLines = 6
            )

            Spacer(Modifier.height(8.dp))

            // ✅ WhatsApp (direct)
            Button(
                onClick = {
                    shareToWhatsApp(context, prefilledMessage)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = prefilledMessage.isNotBlank()
            ) {
                Text("Send via WhatsApp")
            }

            // ✅ Email (direct - opens email client)
            Button(
                onClick = {
                    shareViaEmail(context, prefilledMessage)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = prefilledMessage.isNotBlank()
            ) {
                Text("Send via Email")
            }

            // ✅ Share sheet (WhatsApp / Gmail / SMS / etc.)
            OutlinedButton(
                onClick = {
                    shareViaChooser(context, prefilledMessage)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = prefilledMessage.isNotBlank()
            ) {
                Text("Share (Choose App)")
            }
        }
    }
}

/** Opens WhatsApp directly (if installed) with text */
private fun shareToWhatsApp(context: Context, message: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // WhatsApp not installed -> fallback to share sheet
        shareViaChooser(context, message)
    }
}

/** Opens email app with subject + body */
private fun shareViaEmail(context: Context, message: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // ensures only email apps handle it
        putExtra(Intent.EXTRA_SUBJECT, "My FitLife Workout Plan")
        putExtra(Intent.EXTRA_TEXT, message)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // No email app installed -> fallback
        shareViaChooser(context, message)
    }
}

/** Android share sheet (best universal fallback) */
private fun shareViaChooser(context: Context, message: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    val chooser = Intent.createChooser(intent, "Share workout via")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
}
