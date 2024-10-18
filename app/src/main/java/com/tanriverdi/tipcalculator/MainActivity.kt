package com.tanriverdi.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tanriverdi.tipcalculator.ui.theme.TipCalculatorTheme
import java.text.NumberFormat

// MainActivity: Uygulamanın başlangıç noktası.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Edge-to-edge ekran desteğini etkinleştirir.
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            // Tema uygulaması başlatılır.
            TipCalculatorTheme {
                // Ana yüzeyi belirler, tüm ekranı kaplar.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // Tip hesaplama düzenini çağırır.
                    TipTimeLayout()
                }
            }
        }
    }
}

// TipTimeLayout: Bahşiş hesaplama ekranının düzeni.
@Composable
fun TipTimeLayout() {
    // Kullanıcının girdiği hesap tutarı ve bahşiş oranı.
    var amountInput by remember { mutableStateOf("") }
    var tipInput by remember { mutableStateOf("") }
    var roundUp by remember { mutableStateOf(false) }

    // Hesap tutarı ve bahşiş oranını sayıya dönüştür.
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
    // Bahşişi hesapla.
    val tip = calculateTip(amount, tipPercent, roundUp)

    // Bahşiş hesaplama arayüzü düzeni.
    Column(
        modifier = Modifier
            .statusBarsPadding() // Durum çubuğu alanını korur.
            .padding(horizontal = 40.dp) // Yatayda kenar boşlukları.
            .verticalScroll(rememberScrollState()) // Dikey kaydırma ekler.
            .safeDrawingPadding(), // Ekran güvenli alanı koruma.
        horizontalAlignment = Alignment.CenterHorizontally, // Yatay hizalama.
        verticalArrangement = Arrangement.Center // Dikey hizalama.
    ) {
        // Başlık metni.
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        // Hesap tutarı girişi.
        EditNumberField(
            label = R.string.bill_amount, // Giriş alanı etiketi.
            leadingIcon = R.drawable.money, // Sol tarafta gösterilecek simge.
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, // Sayısal giriş tipi.
                imeAction = ImeAction.Next // İleri aksiyonu.
            ),
            value = amountInput, // Girdi değeri.
            onValueChanged = { amountInput = it }, // Değer değişikliğinde yapılacaklar.
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
        )
        // Bahşiş oranı girişi.
        EditNumberField(
            label = R.string.how_was_the_service,
            leadingIcon = R.drawable.percent,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            value = tipInput,
            onValueChanged = { tipInput = it },
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
        )
        // Bahşiş yuvarlama ayarı.
        RoundTheTipRow(
            roundUp = roundUp, // Yuvarlama durumu.
            onRoundUpChanged = { roundUp = it }, // Değişiklik olduğunda yapılacaklar.
            modifier = Modifier.padding(bottom = 32.dp)
        )
        // Hesaplanan bahşiş tutarı.
        Text(
            text = stringResource(R.string.tip_amount, tip),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(150.dp)) // Arayüzde boşluk bırakır.
    }
}

// EditNumberField: Kullanıcıdan sayısal giriş almak için kullanılan bileşen.
@Composable
fun EditNumberField(
    @StringRes label: Int, // Giriş alanı etiketi.
    @DrawableRes leadingIcon: Int, // Sol tarafta gösterilecek simge.
    keyboardOptions: KeyboardOptions, // Klavye seçenekleri.
    value: String, // Girdi değeri.
    onValueChanged: (String) -> Unit, // Değer değiştiğinde çağrılacak fonksiyon.
    modifier: Modifier = Modifier
) {
    // Giriş alanı.
    TextField(
        value = value,
        singleLine = true, // Tek satırlık giriş.
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), null) }, // İkon.
        modifier = modifier,
        onValueChange = onValueChanged, // Değer değişikliği.
        label = { Text(stringResource(label)) }, // Etiket.
        keyboardOptions = keyboardOptions // Klavye seçenekleri.
    )
}

// RoundTheTipRow: Bahşişin yuvarlanıp yuvarlanmayacağını seçmek için kullanılan satır.
@Composable
fun RoundTheTipRow(
    roundUp: Boolean, // Yuvarlama durumu.
    onRoundUpChanged: (Boolean) -> Unit, // Değişiklik olduğunda yapılacaklar.
    modifier: Modifier = Modifier
) {
    // Yuvarlama seçeneğini gösteren satır düzeni.
    Row(
        modifier = modifier.fillMaxWidth(), // Satır tam genişliği kaplar.
        verticalAlignment = Alignment.CenterVertically // Dikey hizalama.
    ) {
        Text(text = stringResource(R.string.round_up_tip)) // Yuvarlama metni.
        Switch(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End), // Sonunda hizalama.
            checked = roundUp, // Seçili durumu.
            onCheckedChange = onRoundUpChanged // Değişiklik olduğunda yapılacaklar.
        )
    }
}

// calculateTip: Bahşişi hesaplayan fonksiyon.
private fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundUp: Boolean): String {
    // Bahşiş hesaplanır.
    var tip = tipPercent / 100 * amount
    // Yuvarlama seçiliyse bahşiş yukarı yuvarlanır.
    if (roundUp) {
        tip = kotlin.math.ceil(tip)
    }
    // Bahşiş tutarı formatlanarak döndürülür.
    return NumberFormat.getCurrencyInstance().format(tip)
}

// TipTimeLayoutPreview: TipTimeLayout bileşeninin önizlemesi.
@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipCalculatorTheme {
        TipTimeLayout() // Düzenin önizlemesi gösterilir.
    }
}
