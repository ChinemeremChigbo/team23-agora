package com.example.agora.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agora.model.repository.SearchFilterUtils
import com.example.agora.ui.components.FilterPill

@Composable
fun FilterScreen(viewModel: SearchViewModel = viewModel(), onCancel: () -> Unit) {
    val selectedPriceIntervals by viewModel.selectedPriceIntervals.collectAsState()

    Column(
        modifier = Modifier.padding(21.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onCancel) {
                    Text(text = "Cancel", fontSize = 16.sp)
                }
                Text(
                    text = "Filter",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { viewModel.clearFilters() }) {
                    Text(text = "Clear all", fontSize = 16.sp)
                }
            }

            Spacer(Modifier.size(40.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(21.dp, 0.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Price range", fontSize = 19.sp)
                }

                Spacer(Modifier.size(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SearchFilterUtils.priceFilterOptions.keys.take(3).forEach { option ->
                        FilterPill(
                            option,
                            { viewModel.togglePriceInterval(option) },
                            selectedPriceIntervals.contains(option)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SearchFilterUtils.priceFilterOptions.keys.drop(3).take(2).forEach { option ->
                        FilterPill(
                            option,
                            { viewModel.togglePriceInterval(option) },
                            selectedPriceIntervals.contains(option)
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.getSuspendedResults()
                onCancel()
            },
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Apply filters", fontSize = 16.sp)
        }
    }
}