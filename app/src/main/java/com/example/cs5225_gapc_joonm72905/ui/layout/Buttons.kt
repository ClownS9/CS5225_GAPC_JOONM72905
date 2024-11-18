package com.example.cs5225_gapc_joonm72905.ui.layout

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.cs5225_gapc_joonm72905.utils.ImageUtils

data class RadioButtonData(
    val role: String,
    val value: String,
)

@Composable
fun Buttons(onBtnAction: () -> Unit, label: String) {
    Button(
        onClick = onBtnAction,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TextButtons(onBtnAction: () -> Unit, label: String) {
    TextButton(onClick = onBtnAction) {
        Text(label, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun SuccessDialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun ErrorDialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun RadioButtons(
    data: List<RadioButtonData>,
    label: String,
    selectedRole: MutableState<String>,
    inputData: MutableState<String>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(label, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(25.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            data.forEach { radio ->
                CreateRadioButton(radio.role, radio.value, selectedRole)
            }
        }
    }

    if (selectedRole.value == "child") {
        Spacer(modifier = Modifier.height(24.dp))
        Inputs(inputData, "Parent's E-mail Address", false)
    } else {
        inputData.value = ""
    }
}

@Composable
private fun CreateRadioButton(role: String, value: String, selectedRole: MutableState<String>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.selectable(
            selected = (value == selectedRole.value),
            onClick = { selectedRole.value = value }
        )
    ) {
        RadioButton(
            selected = (value == selectedRole.value),
            onClick = { selectedRole.value = value },
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.tertiary)
        )
        Text(
            text = role,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun UploadPic(onProfileSelected: (Uri) -> Unit, profilePicUri: MutableState<Uri?>) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val resizedBitmap = ImageUtils.resizeImages(context, it, 640, 640)

                resizedBitmap?.let { bitmap ->
                    val resizedUri = ImageUtils.saveBitmapToTempFile(context, bitmap)

                    profilePicUri.value = resizedUri

                    onProfileSelected(resizedUri!!)
                }
            }
        }

    UploadPicContent(
        profilePicUri = profilePicUri,
        onClick = { launcher.launch("image/*") }
    )
}

@Composable
private fun UploadPicContent(profilePicUri: MutableState<Uri?>, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        profilePicUri.value?.let { uri ->
            ProfilePicture(uri = uri, onClick = onClick)
        } ?: UploadPlaceholder(onClick = onClick)
    }
}

@Composable
private fun ProfilePicture(uri: Uri, onClick: () -> Unit) {
    Image(
        painter = rememberAsyncImagePainter(uri),
        contentDescription = "Profile Picture",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable { onClick() }
    )
}

@Composable
private fun UploadPlaceholder(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = Icons.Filled.CloudUpload,
            contentDescription = "Upload Profile Picture",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(50.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap to Upload Profile Picture",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ToggleButtons(onChange: (Boolean) -> Unit, checked: Boolean) {
    Switch(
        checked = checked,
        onCheckedChange = onChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.tertiary,
            uncheckedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
            uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    )
}