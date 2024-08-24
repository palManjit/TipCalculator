package com.jp.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jp.tipcalculator.ui.theme.TipCalculatorTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorTheme {
                // A surface container using the 'background' color from the theme
                TipCalculator()

            }
        }
    }
}

@Composable
fun TipCalculator() {
    val amounta = remember {
        mutableStateOf("")
    }
    val personCounter = remember {
        mutableStateOf(1)
    }
    val tipPercentage = remember {
        mutableStateOf(0f)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        TotalHeader(amount=getTotalHeaderAmount(amounta.value,personCounter=personCounter.value,tipPercentage=tipPercentage.value))
        UserInput(
            amount = amounta.value,
            amountChange = { amounta.value = it },
            personCounter = personCounter.value,
            onAddReducePerson = {
                if (it > 0) {
                    personCounter.value++
                } else if (it<0 && personCounter.value>1){
                    personCounter.value--
                }

            },
            tipPercentage.value,{tipPercentage.value=it})
    }
}


@Composable
fun TotalHeader(amount: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shadowElevation = 8.dp,
        color = colorResource(id = R.color.teal),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Per Person",
                style = TextStyle(fontSize = 25.sp, color = Color.Black)
            )
            Spacer(modifier = Modifier.padding(6.dp))
            Text(text ="$ ${formatTwoDecimalPoint(amount)}", style = TextStyle(fontSize = 20.sp, color = Color.Black))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UserInput(
    amount: String,
    amountChange: (String) -> Unit,
    personCounter: Int,
    onAddReducePerson: (Int) -> Unit,
    tipPercentage: Float,
    tipPercentageReduce: (Float) -> Unit
) {
    val keybordControlar = LocalSoftwareKeyboardController.current
    //  val formattedTipPercentage = String.format("%.2f", tipPercentage)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amountChange.invoke(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Enter your amount") },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = true,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ), keyboardActions = KeyboardActions(onDone = {
                    keybordControlar?.hide()
                })

            )
            if (amount.isNotBlank()) {

                Spacer(modifier = Modifier.padding(10.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(text = "Split", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.fillMaxWidth(.50f))

                    CustomButton(imageVector = Icons.Default.KeyboardArrowUp) {
                        onAddReducePerson.invoke(1)
                    }
                    Text(text = "${personCounter}")
                    CustomButton(imageVector = Icons.Default.KeyboardArrowDown) {
                        onAddReducePerson.invoke(-1)
                    }

                }
                Spacer(modifier = Modifier.padding(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tip")
                    Spacer(modifier = Modifier.fillMaxWidth(.70f))
                    Text(text = "$ ${formatTwoDecimalPoint(getTipAmount(amount, tipPercentage))}")
                }
                Spacer(modifier = Modifier.padding(10.dp))

                Text(
                    text = "${formatTwoDecimalPoint(tipPercentage.toString())}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Slider(value = tipPercentage, onValueChange = {
                    tipPercentageReduce.invoke(it)
                }, valueRange = 0f..100f, steps = 6)
            }

        }

    }
}

@Composable
fun CustomButton(imageVector: ImageVector, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { onClick.invoke() }, shape = CircleShape
    ) {
        Image(imageVector = imageVector, contentDescription = null)

    }
}
fun getTipAmount(userAmount:String,tipPercentage: Float):String {
    return when {
        userAmount.isEmpty() -> {
            "0"
        }

        else -> {
            val amount = userAmount.toFloat()
            (amount * tipPercentage.div(100)).toString()
        }
    }
}


fun getTotalHeaderAmount(amount: String, personCounter: Int, tipPercentage: Float): String {
    return when {
        amount.isEmpty() -> {
            "0"
        }

        else -> {
            val userAmount = amount.toFloat()
            val tipAmount = userAmount * tipPercentage.div(100)
            val perHeadAmount = (userAmount + tipAmount).div(personCounter)
            perHeadAmount.toString()
        }

    }
}
fun formatTwoDecimalPoint(str:String):String{
    return if(str.isEmpty()){
        ""
    }
    else{
        val format=DecimalFormat("##############.##")
        format.format(str.toFloat())
    }
}

