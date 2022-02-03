package com.example.makeitso.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.makeitso.R.string as Text
import com.example.makeitso.common.ToolBar

//This method should be private and part of a fragment in the future
@Preview
@Composable
fun LoginScreen() {
    ToolBar(Text.login)
}