package com.sample.android.essentialcompose.livecodingsamples

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Requirements:
 *   - Hardcode at least 5 FAQ items as a List<Pair<String, String>> (question, answer)
 *   - Use animateContentSize() for smooth expand/collapse
 *   - The question text should be bold; the answer should be normal weight
 *   - Add a trailing chevron icon that rotates 180 degrees when expanded
 */

/**
 * Hardcoded FAQ list a List<Pair<String, String>> (question, answer)
 */
val faqList = listOf(
    Pair("How do I use this app?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
    Pair("What are the benefits of using this app?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
    Pair("How can I contact support?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
    Pair("What is the time complexity of this algorithm?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
    Pair("How do I optimize this code?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
)

@Composable
fun FaqExpandableListScreen(faqs: List<Pair<String, String>> = faqList) {

    // State hoisting: pass expandedIndices to FaqExpandableListItem
    // Keep track of the indices of the expanded items. Set allows multiple expanded items.
    var expandedIndices by remember { mutableStateOf(setOf<Int>()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(faqs) { index, faq ->
            FaqExpandableListItem(
                faq = faq,
                expanded = index in expandedIndices,
                onToggle = {
                    expandedIndices = if (index in expandedIndices) {
                        expandedIndices - index
                    } else {
                        expandedIndices + index
                    }
                }
            )
        }
    }
}

@Composable
fun FaqExpandableListItem(
        faq: Pair<String , String>,
        expanded: Boolean,
        onToggle: () -> Unit) {

    // 1. Animate the rotation angle for chevron icon (0 to 180)
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Chevron Rotation"
    )
    // The list item as a card
    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onToggle() }
            // 2. Smoothly animate the height change
            .animateContentSize()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 3. Question Text (bold)
                Text(
                    text = faq.first,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )

                // 4. Trailing chevron with rotation animation
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(chevronRotation)
                )

            }
            // 5. Answer text (normal weight, shown conditionally)
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = faq.second, fontWeight = FontWeight.Normal)
            }
        }
    }
}

/**
 * This shows a single expanded item at a time.
 */
@Composable
fun FaqExpandableListScreenExpandOne(faqs: List<Pair<String, String>> = faqList) {

    // State hoisting: pass expandedIndex to FaqExpandableListItem
    // Keep track of the index of the expanded item.
    var expandedIndex: Int by remember { mutableIntStateOf(-1) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(faqs) { index, faq ->
            FaqExpandableListItem(
                faq = faq,
                expanded = expandedIndex == index,
                onToggle = {
                    expandedIndex = if (expandedIndex == index)  -1 else index
                }
            )
        }
    }
}
