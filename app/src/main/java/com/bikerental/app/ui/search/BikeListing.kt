package com.bikerental.app.ui.search

import com.bikerental.app.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.bikerental.app.ui.theme.AppTheme

/**
 * Preview composable for displaying the BikeRentalCard.
 * This will allow the card to be previewed in the UI toolkit.
 */
@Preview(showBackground = true)
@Composable
fun BikeRentalCardPreview() {
    AppTheme {
        BikeRentalCard()
    }
}

/**
 * A composable that displays a rental bike card with details.
 * It includes the bike image, rating, price, rental duration, description, and a rent button.
 */
@Composable
fun BikeRentalCard() {
    Surface(modifier = Modifier.fillMaxSize()) {
        // Card displaying bike rental details
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            /**
             * Display the image of the bike in the card.
             * The image is clipped to a rounded corner shape for a more polished appearance.
             */
            Image(
                painter = painterResource(id = R.drawable.bike),
                contentDescription = "Bike Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            /**
             * Display the bike's rating (stars) and price in a row.
             * Rating is shown with a star icon and the price is displayed beside it.
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "5.0", style = MaterialTheme.typography.bodyMedium)
                }
                Text(text = "50 €", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            /**
             * Display the rental duration (e.g., 5-10 days) below the price.
             */
            Text(text = "5-10 days", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            /**
             * Display a short description about the bike rental.
             * Describes the purpose of the bike rental, which is to enjoy the city with a reliable bike.
             */
            Text(
                text = "Enjoy the city with a reliable bike. Perfect for short or long rides.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * A button that allows the user to rent the bike.
             */
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2E59C))
            ) {
                Text("Rent")
            }
        }
    }
}
