package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                InfixCalculatorScreen()
            }
        }
    }
}

@Composable
fun InfixCalculatorScreen() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BasicTextField(
            value = expression,
            onValueChange = { expression = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    if (expression.isEmpty()) Text("Enter infix expression (e.g., (6+7)-2*3)")
                    innerTextField()
                }
            }
        )
        Button(
            onClick = {
                try {
                    result = evaluateInfixExpression(expression).toString()
                } catch (e: Exception) {
                    result = "Error: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate")
        }
        Text(
            text = "Result: $result",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

// Infix Functions for Arithmetic Operations
infix fun Double.add(other: Double): Double = this + other
infix fun Double.subtract(other: Double): Double = this - other
infix fun Double.multiply(other: Double): Double = this * other
infix fun Double.divide(other: Double): Double {
    if (other == 0.0) throw ArithmeticException("Division by zero!")
    return this / other
}

// Evaluation Logic
fun evaluateInfixExpression(expression: String): Double {
    val operators = Stack<Char>()
    val values = Stack<Double>()

    val tokens = expression.replace(" ", "").toCharArray()
    var i = 0
    while (i < tokens.size) {
        when {
            tokens[i].isDigit() || tokens[i] == '.' -> {
                val stringBuilder = StringBuilder()
                while (i < tokens.size && (tokens[i].isDigit() || tokens[i] == '.')) {
                    stringBuilder.append(tokens[i])
                    i++
                }
                values.push(stringBuilder.toString().toDouble())
                continue
            }
            tokens[i] == '(' -> operators.push(tokens[i])
            tokens[i] == ')' -> {
                while (operators.isNotEmpty() && operators.peek() != '(') {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()))
                }
                operators.pop()
            }
            tokens[i] in "+-*/" -> {
                while (operators.isNotEmpty() && hasPrecedence(tokens[i], operators.peek())) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()))
                }
                operators.push(tokens[i])
            }
        }
        i++
    }

    while (operators.isNotEmpty()) {
        values.push(applyOperation(operators.pop(), values.pop(), values.pop()))
    }

    return values.pop()
}

// Apply Operation Using Infix Functions
fun applyOperation(op: Char, b: Double, a: Double): Double {
    return when (op) {
        '+' -> a add b
        '-' -> a subtract b
        '*' -> a multiply b
        '/' -> a divide b
        else -> throw UnsupportedOperationException("Invalid operator: $op")
    }
}

fun hasPrecedence(op1: Char, op2: Char): Boolean {
    if (op2 == '(' || op2 == ')') return false
    if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false
    return true
}

@Preview(showBackground = true)
@Composable
fun InfixCalculatorScreenPreview() {
    MyApplicationTheme {
        InfixCalculatorScreen()
    }
}
