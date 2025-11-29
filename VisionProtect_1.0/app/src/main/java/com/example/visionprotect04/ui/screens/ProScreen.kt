package com.example.visionprotect04.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NeonCyan = Color(0xFF00F5FF)
private val NeonPurple = Color(0xFFBF00FF)
private val NeonPink = Color(0xFFFF00A0)
private val NeonGold = Color(0xFFFFD700)
private val DeepSpace = Color(0xFF0A0A1A)
private val CardDark = Color(0xFF15152A)

@Composable
fun ProScreen(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pro")
    val shimmer by infiniteTransition.animateFloat(
        0f, 1f, infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "shimmer"
    )
    
    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CardDark)
                        .border(1.dp, NeonGold.copy(alpha = 0.5f), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text("Upgrade to PRO", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(Modifier.height(40.dp))
            
            // Crown Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonGold, Color(0xFFFF8C00)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, null, Modifier.size(50.dp), tint = Color.White)
            }
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                "VisionProtect PRO",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                style = LocalTextStyle.current.copy(
                    brush = Brush.linearGradient(listOf(NeonGold, Color(0xFFFF8C00), NeonGold))
                )
            )
            
            Text(
                "Unlock the full power of AI eye protection",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(32.dp))
            
            // Pro Features
            ProFeatureItem("Advanced Posture AI", "Real-time spine & neck analysis")
            ProFeatureItem("Extended Analytics", "Weekly & monthly reports")
            ProFeatureItem("Multi-Device Sync", "Sync across all your devices")
            ProFeatureItem("Custom Alerts", "Personalized warning sounds")
            ProFeatureItem("No Ads", "Completely ad-free experience")
            ProFeatureItem("Priority Support", "24/7 dedicated support")
            
            Spacer(Modifier.height(32.dp))
            
            // Pricing Cards
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PricingCard(
                    Modifier.weight(1f),
                    "Monthly",
                    "₹149",
                    "/month",
                    false
                )
                PricingCard(
                    Modifier.weight(1f),
                    "Yearly",
                    "₹999",
                    "/year",
                    true,
                    "Save 44%"
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Subscribe Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Brush.linearGradient(listOf(NeonGold, Color(0xFFFF8C00))))
                    .clickable { /* TODO: Payment */ },
                contentAlignment = Alignment.Center
            ) {
                Text("Subscribe Now", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                "7-day free trial • Cancel anytime",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                "Restore Purchase",
                color = NeonCyan,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { }
            )
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ProFeatureItem(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(NeonGold.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, null, Modifier.size(18.dp), tint = NeonGold)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            Text(description, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun PricingCard(
    modifier: Modifier,
    plan: String,
    price: String,
    period: String,
    isPopular: Boolean,
    badge: String? = null
) {
    Card(
        modifier = modifier
            .border(
                if (isPopular) 2.dp else 1.dp,
                if (isPopular) NeonGold else Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonGold)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Spacer(Modifier.height(8.dp))
            }
            Text(plan, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(period, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
            }
        }
    }
}
