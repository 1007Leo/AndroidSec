/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.makeitso.screens.login

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitso.R.string as AppText
import com.example.makeitso.common.composable.*
import com.example.makeitso.common.ext.basicButton
import com.example.makeitso.common.ext.fieldModifier
import com.example.makeitso.common.ext.textButton
import com.example.makeitso.theme.MakeItSoTheme

@Composable
fun LoginScreen(
  openAndPopUp: (String, String) -> Unit,
  viewModel: LoginViewModel = hiltViewModel()
) {
  val isDarkTheme = isSystemInDarkTheme()
  val uiState by viewModel.uiState
  val context = LocalContext.current as Activity

  LoginScreenContent(
    onGoogleSignInClick = { viewModel.onGoogleSignInClick(context, openAndPopUp) },
    isDarkTheme = isDarkTheme,
    uiState = uiState,
    onEmailChange = viewModel::onEmailChange,
    onPasswordChange = viewModel::onPasswordChange,
    onSignInClick = { viewModel.onSignInClick(openAndPopUp) },
    onCreateAccountClick = {viewModel.onCreateAccountClick(openAndPopUp)},
    onForgotPasswordClick = viewModel::onForgotPasswordClick
  )
}

@Composable
fun LoginScreenContent(
  onGoogleSignInClick: () -> Unit,
  modifier: Modifier = Modifier,
  isDarkTheme: Boolean,
  uiState: LoginUiState,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onSignInClick: () -> Unit,
  onCreateAccountClick: () -> Unit,
  onForgotPasswordClick: () -> Unit
) {
  BasicToolbar(AppText.login_details)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    EmailField(uiState.email, onEmailChange, Modifier.fieldModifier())
    PasswordField(uiState.password, onPasswordChange, Modifier.fieldModifier())

    BasicButton(AppText.sign_in, Modifier.basicButton()) { onSignInClick() }
    BasicButton(AppText.sign_up, Modifier.basicButton()) { onCreateAccountClick() }

    Box(
      contentAlignment = Alignment.Center,
    ) {
      Surface(
        shape = CircleShape,
        color = when (isDarkTheme) {
          true -> Color(0xFF131314)
          false -> Color(0xFFFFFFFF)
        },
        modifier = Modifier
          .height(50.dp)
          .width(260.dp)
          .clip(CircleShape)
          .border(
            BorderStroke(
              width = 1.dp,
              color = when (isDarkTheme) {
                true -> Color(0xFF8E918F)
                false -> Color.Transparent
              }
            ),
            shape = CircleShape
          )
          .clickable { onGoogleSignInClick() }
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(vertical = 5.dp)
        ) {
          Spacer(modifier = Modifier.width(14.dp))
          Image(
            painterResource(id = com.example.makeitso.R.drawable.ic_sign_in),
            contentDescription = null,
            modifier = Modifier.padding(vertical = 5.dp)
          )
          Spacer(modifier = Modifier.weight(1f))
          Text(text = "Continue with Google")
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }


    BasicTextButton(AppText.forgot_password, Modifier.textButton()) {
      onForgotPasswordClick()
    }
  }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
  val uiState = LoginUiState(
    email = "email@test.com"
  )

  MakeItSoTheme {
    LoginScreenContent(
      onGoogleSignInClick = { },
      isDarkTheme = isSystemInDarkTheme(),
      uiState = uiState,
      onEmailChange = { },
      onPasswordChange = { },
      onSignInClick = { },
      onCreateAccountClick = { },
      onForgotPasswordClick = { }
    )
  }
}
